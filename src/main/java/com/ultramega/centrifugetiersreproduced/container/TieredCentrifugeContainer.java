package com.ultramega.centrifugetiersreproduced.container;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import com.ultramega.centrifugetiersreproduced.blockentity.TierInventoryHandlerHelper;
import com.ultramega.centrifugetiersreproduced.blockentity.TieredCentrifugeControllerBlockEntity;
import com.ultramega.centrifugetiersreproduced.blocks.TieredCentrifugeControllerBlock;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TieredCentrifugeContainer extends AbstractContainer<TieredCentrifugeControllerBlockEntity> {
    private final CentrifugeTiers tier;
    public final TieredCentrifugeControllerBlockEntity blockEntity;

    public final ContainerLevelAccess canInteractWithCallable;

    public TieredCentrifugeContainer(CentrifugeTiers tier, final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(tier, windowId, playerInventory, getBlockEntity(playerInventory, data));
    }

    public TieredCentrifugeContainer(CentrifugeTiers tier, final int windowId, final Inventory playerInventory, final TieredCentrifugeControllerBlockEntity blockEntity) {
        super(CentrifugeTiersReproduced.getMenuType(tier), blockEntity, windowId);
        this.tier = tier;
        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        int minusHeight = getTierHeight() / 2;

        addDataSlots(new ContainerData() {
            @Override
            public int get(int i) {
                return i == 0 ?
                        blockEntity.fluidId :
                        blockEntity.fluidHandler.getFluidInTank(0).getAmount();
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0:
                        blockEntity.fluidId = value;
                    case 1:
                        FluidStack fluid = blockEntity.fluidHandler.getFluidInTank(0);
                        if (fluid.isEmpty()) {
                            blockEntity.fluidHandler.fill(new FluidStack(BuiltInRegistries.FLUID.byId(blockEntity.fluidId), value), IFluidHandler.FluidAction.EXECUTE);
                        }
                        else {
                            fluid.setAmount(value);
                        }
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        // Energy
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.energyHandler.getEnergyStored();
            }

            @Override
            public void set(int value) {
                if (blockEntity.energyHandler.getEnergyStored() > 0) {
                    blockEntity.energyHandler.extractEnergy(blockEntity.energyHandler.getEnergyStored(), false);
                }
                if (value > 0) {
                    blockEntity.energyHandler.receiveEnergy(value, false);
                }
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

        // Comb slot
        addSlotBox(this.blockEntity.inventoryHandler, TierInventoryHandlerHelper.getInputSlotsForTier(tier)[0], 13, 17 - minusHeight, 1, 18, 1 + tier.getInputSlotAmountIncrease(), tier == CentrifugeTiers.TIER_1 ? 36 : 18);

        // Inventory slots
        addSlotBox(this.blockEntity.inventoryHandler, TierInventoryHandlerHelper.getOutputSlotsForTier(tier)[0], 67, 17 - minusHeight, 3, 18, 3 + (tier.getOutputSlotAmountIncrease() / 3), 18);

        addSlotBox(this.blockEntity.getUpgradeHandler(), 0, 165, 8 - minusHeight, 1, 18, 4, 18);

        layoutPlayerInventorySlots(playerInventory, 0, -5, 84 + minusHeight);
    }

    private static TieredCentrifugeControllerBlockEntity getBlockEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final var tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof TieredCentrifugeControllerBlockEntity) {
            return (TieredCentrifugeControllerBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof TieredCentrifugeControllerBlock && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    private int getTierHeight() {
        return (tier == CentrifugeTiers.TIER_2 || tier == CentrifugeTiers.TIER_3 ? 18 : tier == CentrifugeTiers.TIER_4 ? 54 : 0);
    }
}
