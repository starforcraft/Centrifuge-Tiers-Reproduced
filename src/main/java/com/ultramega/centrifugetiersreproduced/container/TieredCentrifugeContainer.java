package com.ultramega.centrifugetiersreproduced.container;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.blockentity.InventoryHandlerHelper;
import com.ultramega.centrifugetiersreproduced.blockentity.TieredCentrifugeBlockEntity;
import com.ultramega.centrifugetiersreproduced.blocks.TieredCentrifugeBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TieredCentrifugeContainer extends AbstractContainer {
    public final TieredCentrifugeBlockEntity blockEntity;

    public final ContainerLevelAccess canInteractWithCallable;

    public TieredCentrifugeContainer(MenuType menuType, final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(menuType, windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public TieredCentrifugeContainer(MenuType menuType, final int windowId, final Inventory playerInventory, final TieredCentrifugeBlockEntity blockEntity) {
        super(menuType, windowId);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        addDataSlots(new ContainerData() {
            @Override
            public int get(int i) {
                return i == 0 ? blockEntity.fluidId : blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).map(fluidHandler -> fluidHandler.getFluidInTank(0).getAmount()).orElse(0);
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0:
                        blockEntity.fluidId = value;
                    case 1:
                        blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(fluidHandler -> {
                            FluidStack fluid = fluidHandler.getFluidInTank(0);
                            if (fluid.isEmpty()) {
                                fluidHandler.fill(new FluidStack(BuiltInRegistries.FLUID.byId(blockEntity.fluidId), value), IFluidHandler.FluidAction.EXECUTE);
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

        for(int i = 0; i < blockEntity.recipeProgress.length; i++) {
            int finalI = i;
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return blockEntity.recipeProgress[finalI];
                }

                @Override
                public void set(int value) {
                    blockEntity.recipeProgress[finalI] = value;
                }
            });
        }

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
            // Comb slot
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.INPUT_SLOT[0], 13, 17));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.INPUT_SLOT[1], 13, 53));
            if(blockEntity.tier == CentrifugeTiers.NUCLEAR || blockEntity.tier == CentrifugeTiers.COSMIC || blockEntity.tier == CentrifugeTiers.CREATIVE) {
                addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.INPUT_SLOT[2], 13, 35));
            }
            if(blockEntity.tier == CentrifugeTiers.COSMIC || blockEntity.tier == CentrifugeTiers.CREATIVE) {
                addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.INPUT_SLOT[3], 13, 71));
            }

            // Inventory slots
            int vertAmount = blockEntity.tier == CentrifugeTiers.COSMIC || blockEntity.tier == CentrifugeTiers.CREATIVE ? 4 : 3;
            int horAmount = blockEntity.tier == CentrifugeTiers.CREATIVE ? 4 : 3;
            addSlotBox(inv, InventoryHandlerHelper.OUTPUT_SLOTS[0], 67, 17, horAmount, 18, vertAmount, 18);
        });

        if(blockEntity.tier != CentrifugeTiers.CREATIVE) {
            this.blockEntity.getUpgradeHandler().ifPresent(upgradeHandler -> {
                addSlotBox(upgradeHandler, 0, 165, 8, 1, 18, 4, 18);
            });
        }

        int topRow = blockEntity.tier == CentrifugeTiers.COSMIC || blockEntity.tier == CentrifugeTiers.CREATIVE ? 102 : 84;
        layoutPlayerInventorySlots(playerInventory, 0, -5, topRow);

        // Energy
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
            }

            @Override
            public void set(int value) {
                blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(handler -> {
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
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
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
    protected BlockEntity getBlockEntity() {
        return blockEntity;
    }
}