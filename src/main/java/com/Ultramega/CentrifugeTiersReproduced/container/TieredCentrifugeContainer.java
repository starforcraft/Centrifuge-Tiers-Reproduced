package com.Ultramega.CentrifugeTiersReproduced.container;

import com.Ultramega.CentrifugeTiersReproduced.CentrifugeTiers;
import com.Ultramega.CentrifugeTiersReproduced.blockentity.InventoryHandlerHelper;
import com.Ultramega.CentrifugeTiersReproduced.blockentity.TieredCentrifugeBlockEntity;
import com.Ultramega.CentrifugeTiersReproduced.blocks.TieredCentrifugeBlock;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TieredCentrifugeContainer extends AbstractContainer {
    public final TieredCentrifugeBlockEntity tileEntity;

    public final ContainerLevelAccess canInteractWithCallable;

    public TieredCentrifugeContainer(MenuType menuType, final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(menuType, windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public TieredCentrifugeContainer(MenuType menuType, final int windowId, final Inventory playerInventory, final TieredCentrifugeBlockEntity tileEntity) {
        super(menuType, windowId);

        this.tileEntity = tileEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos());

        addDataSlots(new ContainerData() {
            @Override
            public int get(int i) {
                return i == 0 ?
                        tileEntity.fluidId :
                        tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map(fluidHandler -> fluidHandler.getFluidInTank(0).getAmount()).orElse(0);
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0:
                        tileEntity.fluidId = value;
                    case 1:
                        tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluidHandler -> {
                            FluidStack fluid = fluidHandler.getFluidInTank(0);
                            if (fluid.isEmpty()) {
                                fluidHandler.fill(new FluidStack(Registry.FLUID.byId(tileEntity.fluidId), value), IFluidHandler.FluidAction.EXECUTE);
                            }
                            else {
                                fluid.setAmount(value);
                            }
                        });
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        for(int i = 0; i < tileEntity.recipeProgress.length; i++) {
            int finalI = i;
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return tileEntity.recipeProgress[finalI];
                }

                @Override
                public void set(int value) {
                    tileEntity.recipeProgress[finalI] = value;
                }
            });
        }

        this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            // Comb slot
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.INPUT_SLOT[0], 13, 17));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.INPUT_SLOT[1], 13, 53));
            if(tileEntity.tier == CentrifugeTiers.NUCLEAR || tileEntity.tier == CentrifugeTiers.COSMIC || tileEntity.tier == CentrifugeTiers.CREATIVE) {
                addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.INPUT_SLOT[2], 13, 35));
            }
            if(tileEntity.tier == CentrifugeTiers.COSMIC || tileEntity.tier == CentrifugeTiers.CREATIVE) {
                addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.INPUT_SLOT[3], 13, 71));
            }

            // Inventory slots
            int vertAmount = tileEntity.tier == CentrifugeTiers.COSMIC || tileEntity.tier == CentrifugeTiers.CREATIVE ? 4 : 3;
            int horAmount = tileEntity.tier == CentrifugeTiers.CREATIVE ? 4 : 3;
            addSlotBox(inv, InventoryHandlerHelper.OUTPUT_SLOTS[0], 67, 17, horAmount, 18, vertAmount, 18);
        });

        if(tileEntity.tier != CentrifugeTiers.CREATIVE) {
            this.tileEntity.getUpgradeHandler().ifPresent(upgradeHandler -> {
                addSlotBox(upgradeHandler, 0, 165, 8, 1, 18, 4, 18);
            });
        }

        int topRow = tileEntity.tier == CentrifugeTiers.COSMIC || tileEntity.tier == CentrifugeTiers.CREATIVE ? 102 : 84;
        layoutPlayerInventorySlots(playerInventory, 0, -5, topRow);

        // Energy
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
            }

            @Override
            public void set(int value) {
                tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
                    if (handler.getEnergyStored() > 0) {
                        handler.extractEnergy(handler.getEnergyStored(), false);
                    }
                    if (value > 0) {
                        handler.receiveEnergy(value, false);
                    }
                });
            }
        });
    }

    private static TieredCentrifugeBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof TieredCentrifugeBlockEntity tile) {
            return tile;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof TieredCentrifugeBlock && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getTileEntity() {
        return tileEntity;
    }
}