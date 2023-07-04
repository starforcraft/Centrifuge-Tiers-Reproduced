package com.Ultramega.CentrifugeTiersReproduced.blockentity;

import com.Ultramega.CentrifugeTiersReproduced.CentrifugeTiers;
import com.Ultramega.CentrifugeTiersReproduced.blocks.TieredCentrifugeBlock;
import com.Ultramega.CentrifugeTiersReproduced.container.TieredCentrifugeContainer;
import com.Ultramega.CentrifugeTiersReproduced.registry.ModBlocks;
import com.Ultramega.CentrifugeTiersReproduced.registry.ModContainerTypes;
import com.Ultramega.CentrifugeTiersReproduced.registry.ModTileEntityTypes;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.Centrifuge;
import cy.jdkdigital.productivebees.common.block.entity.FluidTankBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.PoweredCentrifugeBlockEntity;
import cy.jdkdigital.productivebees.common.item.CombBlockItem;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.common.item.GeneBottle;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TieredCentrifugeBlockEntity extends PoweredCentrifugeBlockEntity {
    private CentrifugeRecipe[] currentRecipe = new CentrifugeRecipe[4];
    public int[] recipeProgress = new int[4];
    public CentrifugeTiers tier;

    private final LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(21, this) {
        @Override
        public boolean isContainerItem(Item item) {
            return false;
        }

        @Override
        public boolean isInputSlotItem(int slot, ItemStack item) {
            boolean isProcessableItem = item.getItem().equals(ModItems.GENE_BOTTLE.get()) || item.getItem().equals(ModItems.HONEY_TREAT.get()) || TieredCentrifugeBlockEntity.this.canProcessItemStack(item);

            return (isProcessableItem && slot == InventoryHandlerHelper.INPUT_SLOT[0]) || (isProcessableItem && slot == InventoryHandlerHelper.INPUT_SLOT[1]) || (isProcessableItem && slot == InventoryHandlerHelper.INPUT_SLOT[2]) || (isProcessableItem && slot == InventoryHandlerHelper.INPUT_SLOT[3]) || (!isProcessableItem && super.isInputSlotItem(slot, item));
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (slot == InventoryHandlerHelper.INPUT_SLOT[0] && this.getStackInSlot(slot).isEmpty()) {
                TieredCentrifugeBlockEntity.this.recipeProgress[0] = 0;
            }
            if (slot == InventoryHandlerHelper.INPUT_SLOT[1] && this.getStackInSlot(slot).isEmpty()) {
                TieredCentrifugeBlockEntity.this.recipeProgress[1] = 0;
            }
            if (slot == InventoryHandlerHelper.INPUT_SLOT[2] && this.getStackInSlot(slot).isEmpty()) {
                TieredCentrifugeBlockEntity.this.recipeProgress[2] = 0;
            }
            if (slot == InventoryHandlerHelper.INPUT_SLOT[3] && this.getStackInSlot(slot).isEmpty()) {
                TieredCentrifugeBlockEntity.this.recipeProgress[3] = 0;
            }
        }
    });
    protected LazyOptional<IFluidHandler> fluidInventory = LazyOptional.of(() -> new InventoryHandlerHelper.FluidHandler(tier.getFluidCapacity()) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            TieredCentrifugeBlockEntity.this.fluidId = Registry.FLUID.getId(getFluid().getFluid());
            TieredCentrifugeBlockEntity.this.setChanged();
        }
    });
    public LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> new EnergyStorage(tier.getEnergyCapacity()));
    protected LazyOptional<IItemHandlerModifiable> upgradeHandler = LazyOptional.of(() -> new InventoryHandlerHelper.UpgradeHandler(tier == CentrifugeTiers.CREATIVE ? 0 : 4, this));

    public TieredCentrifugeBlockEntity(BlockEntityType entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
        if(entityType == ModTileEntityTypes.HIGH_END_CENTRIFUGE.get()) {
            this.tier = CentrifugeTiers.HIGH_END;
        } else if(entityType == ModTileEntityTypes.NUCLEAR_CENTRIFUGE.get()) {
            this.tier = CentrifugeTiers.NUCLEAR;
        } else if(entityType == ModTileEntityTypes.COSMIC_CENTRIFUGE.get()) {
            this.tier = CentrifugeTiers.COSMIC;
        } else if(entityType == ModTileEntityTypes.CREATIVE_CENTRIFUGE.get()) {
            this.tier = CentrifugeTiers.CREATIVE;
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TieredCentrifugeBlockEntity blockEntity) {
        blockEntity.inventoryHandler.ifPresent(invHandler -> {
            for(int i = 0; i < blockEntity.tier.getInputSlotAmount(); i++) {
                if (!invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[i]).isEmpty() && blockEntity.canOperate()) {
                    // Process gene bottles
                    ItemStack invItem = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[i]);
                    if (invItem.getItem().equals(ModItems.GENE_BOTTLE.get())) {
                        level.setBlockAndUpdate(pos, state.setValue(TieredCentrifugeBlock.RUNNING, true));
                        int totalTime = blockEntity.getProcessingTime();

                        if (++blockEntity.recipeProgress[i] >= totalTime) {
                            blockEntity.completeGeneProcessing(invHandler, i);
                            blockEntity.recipeProgress[i] = 0;
                            blockEntity.setChanged();
                        }
                    } else if (invItem.getItem().equals(ModItems.HONEY_TREAT.get())) {
                        level.setBlockAndUpdate(pos, state.setValue(TieredCentrifugeBlock.RUNNING, true));
                        int totalTime = blockEntity.getProcessingTime();

                        if (++blockEntity.recipeProgress[i] >= totalTime) {
                            blockEntity.completeTreatProcessing(invHandler, i);
                            blockEntity.recipeProgress[i] = 0;
                            blockEntity.setChanged();
                        }
                    } else {
                        CentrifugeRecipe recipe = blockEntity.getRecipe(invHandler, i);
                        if (blockEntity.canProcessRecipe(recipe, invHandler)) {
                            level.setBlockAndUpdate(pos, state.setValue(TieredCentrifugeBlock.RUNNING, true));
                            int totalTime = blockEntity.getProcessingTime();

                            if (++blockEntity.recipeProgress[i] >= totalTime) {
                                blockEntity.completeRecipeProcessing(recipe, invHandler, i);
                                blockEntity.recipeProgress[i] = 0;
                                blockEntity.setChanged();
                            }
                        }
                    }
                } else {
                    level.setBlockAndUpdate(pos, state.setValue(TieredCentrifugeBlock.RUNNING, false));
                }

                // Pull items dropped on top
                if (ProductiveBeesConfig.GENERAL.centrifugeHopperMode.get() && --blockEntity.transferCooldown <= 0) {
                    blockEntity.transferCooldown = 22;
                    blockEntity.suckInItems(invHandler, i);
                }
            }
        });
        FluidTankBlockEntity.tick(level, pos, state, blockEntity);

        if (state.getValue(Centrifuge.RUNNING) && level instanceof ServerLevel) {
            blockEntity.energyHandler.ifPresent(handler -> {
                handler.extractEnergy((int) (ProductiveBeesConfig.GENERAL.centrifugePowerUse.get() * blockEntity.getEnergyConsumptionModifier() * blockEntity.tier.getSpeed()), false);
            });
        }
    }

    @Override
    protected boolean canProcessRecipe(@Nullable CentrifugeRecipe recipe, IItemHandlerModifiable invHandler) {
        if (recipe != null) {
            // Check if output slots has space for recipe output
            List<ItemStack> outputList = Lists.newArrayList();

            recipe.getRecipeOutputs().forEach((stack, value) -> {
                // Check for item with max possible output
                ItemStack item = new ItemStack(stack.getItem(), value.get(1).getAsInt());
                outputList.add(item);
            });

            // Allow overfilling of fluid but don't process if the tank has a different fluid
            Pair<Fluid, Integer> fluidOutput = recipe.getFluidOutputs();
            boolean fluidFlag = true;
            if (fluidOutput != null) {
                fluidFlag = fluidInventory.map(h -> h.getFluidInTank(0).isEmpty() || h.getFluidInTank(0).getFluid().equals(fluidOutput.getFirst())).orElse(false);
            }

            return fluidFlag && ((InventoryHandlerHelper.ItemHandler) invHandler).canFitStacks(outputList);
        }
        return false;
    }

    private void completeGeneProcessing(IItemHandlerModifiable invHandler, int index) {
        ItemStack geneBottle = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index]);

        CompoundTag entityData = GeneBottle.getGenes(geneBottle);
        if (entityData == null) {
            return;
        }

        double chance = ProductiveBeesConfig.BEE_ATTRIBUTES.geneExtractChance.get();
        for (String attributeName : BeeAttributes.attributeList()) {
            if (ProductiveBees.rand.nextDouble() <= chance) {
                int value = entityData.getInt("bee_" + attributeName);
                ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(Gene.getStack(BeeAttributes.getAttributeByName(attributeName), value));
            }
        }

        // Chance to get a type gene
        if (ProductiveBees.rand.nextDouble() <= chance) {
            int typePurity = ProductiveBeesConfig.BEE_ATTRIBUTES.typeGenePurity.get();
            ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(Gene.getStack(entityData.getString("type"), ProductiveBees.rand.nextInt(Math.max(0, typePurity - 5)) + 10));
        }

        invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index]).shrink(1);
    }

    private void completeTreatProcessing(IItemHandlerModifiable invHandler, int index) {
        ItemStack honeyTreat = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index]);

        ListTag genes = HoneyTreat.getGenes(honeyTreat);
        if (!genes.isEmpty()) {
            for (Tag inbt : genes) {
                ItemStack insertedGene = ItemStack.of((CompoundTag) inbt);
                if (((CompoundTag) inbt).contains("purity")) {
                    int purity = ((CompoundTag) inbt).getInt("purity");
                    Gene.setPurity(insertedGene, purity);
                }
                ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(insertedGene);
            }
        }

        invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index]).shrink(1);
    }

    private void suckInItems(IItemHandlerModifiable invHandler, int index) {
        for (ItemEntity itemEntity : getCaptureItems()) {
            ItemStack itemStack = itemEntity.getItem();
            if (canProcessItemStack(itemStack) ||
                            itemStack.getItem().equals(ModItems.GENE_BOTTLE.get()) ||
                            itemStack.getItem().equals(ModItems.HONEY_TREAT.get()) && HoneyTreat.hasGene(itemStack)) {
                captureItem(invHandler, itemEntity, index);
            }
        }
    }

    private List<ItemEntity> getCaptureItems() {
        assert level != null;
        return TieredCentrifugeBlock.COLLECTION_AREA_SHAPE.toAabbs().stream().flatMap((blockPos) -> level.getEntitiesOfClass(ItemEntity.class, blockPos.move(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()), EntitySelector.ENTITY_STILL_ALIVE).stream()).collect(Collectors.toList());
    }

    private static void captureItem(IItemHandlerModifiable invHandler, ItemEntity itemEntity, int index) {
        ItemStack insertStack = itemEntity.getItem().copy();
        ItemStack leftoverStack = invHandler.insertItem(InventoryHandlerHelper.INPUT_SLOT[index], insertStack, false);

        if (leftoverStack.isEmpty()) {
            itemEntity.discard();
        } else {
            itemEntity.setItem(leftoverStack);
        }
    }

    protected double getEnergyConsumptionModifier() {
        double timeUpgradeModifier = getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get();

        return Math.max(1, timeUpgradeModifier) * 5;
    }

    public int getProcessingTime() {
        if(tier != CentrifugeTiers.CREATIVE) {
            return (int) (ProductiveBeesConfig.GENERAL.centrifugePoweredProcessingTime.get() * getProcessingTimeModifier() / tier.getSpeed());
        } else {
            return 0;
        }
    }

    protected boolean canOperate() {
        if(tier == CentrifugeTiers.CREATIVE) {
            return true;
        } else {
            int energy = energyHandler.map(IEnergyStorage::getEnergyStored).orElse(0);
            return energy >= ProductiveBeesConfig.GENERAL.centrifugePowerUse.get();
        }
    }

    @Override
    public boolean canProcessItemStack(ItemStack stack) {
        if (stack.is(ModTags.Forge.COMBS)) {
            ItemStack singleComb;
            // config honeycomb
            if (stack.getItem() instanceof CombBlockItem) {
                singleComb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                singleComb.setTag(stack.getTag());
            } else {
                singleComb = BeeHelper.getRecipeOutputFromInput(level, stack.getItem());
            }
            return !singleComb.isEmpty() && super.canProcessItemStack(singleComb);
        }

        return super.canProcessItemStack(stack);
    }

    protected CentrifugeRecipe getRecipe(IItemHandlerModifiable inputHandler, int index) {
        ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index]);
        if (input.is(ModTags.Forge.COMBS)) {
            ItemStack singleComb;
            // config honeycomb
            if (input.getItem() instanceof CombBlockItem) {
                singleComb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                singleComb.setTag(input.getTag());
            } else {
                singleComb = BeeHelper.getRecipeOutputFromInput(level, input.getItem());
            }
            IItemHandlerModifiable inv = new InventoryHandlerHelper.ItemHandler(3);
            inv.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index], singleComb);
            return getRecipe2(inv, index);
        }

        return getRecipe2(inputHandler, index);
    }

    protected CentrifugeRecipe getRecipe2(IItemHandlerModifiable inputHandler, int index) {
        ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index]);
        if (input.isEmpty() || input == ItemStack.EMPTY || level == null) {
            return null;
        }

        if (currentRecipe[index] != null && currentRecipe[index].matches(new RecipeWrapper(inputHandler), level)) {
            return currentRecipe[index];
        }

        currentRecipe[index] = BeeHelper.getCentrifugeRecipe(level.getRecipeManager(), inputHandler);

        Map<ResourceLocation, Recipe<Container>> allRecipes = level.getRecipeManager().byType(ModRecipeTypes.CENTRIFUGE_TYPE);
        Container inv = new RecipeWrapper(inputHandler);
        for (Map.Entry<ResourceLocation, Recipe<Container>> entry : allRecipes.entrySet()) {
            CentrifugeRecipe recipe = (CentrifugeRecipe) entry.getValue();
            if (recipe.matches(inv, level)) {
                currentRecipe[index] = recipe;
                break;
            }
        }

        return currentRecipe[index];
    }

    protected void completeRecipeProcessing(CentrifugeRecipe recipe, IItemHandlerModifiable invHandler, int index) {
        ItemStack input = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index]).copy();
        if (input.is(ModTags.Forge.COMBS)) {
            ItemStack singleComb;
            if (input.getItem() instanceof CombBlockItem) {
                singleComb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get(), 4);
                singleComb.setTag(input.getTag());
            } else {
                singleComb = BeeHelper.getRecipeOutputFromInput(level, input.getItem());
            }
            invHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index], singleComb);
            for (int i = 0; i < 4; i++) {
                completeRecipeProcessing2(recipe, invHandler, index);
            }
            input.shrink(1);
            invHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index], input);
        } else {
            completeRecipeProcessing2(recipe, invHandler, index);
        }
    }

    protected void completeRecipeProcessing2(CentrifugeRecipe recipe, IItemHandlerModifiable invHandler, int index) {
        recipe.getRecipeOutputs().forEach((itemStack, recipeValues) -> {
            if (ProductiveBees.rand.nextInt(100) <= recipeValues.get(2).getAsInt()) {
                int count = Mth.nextInt(ProductiveBees.rand, Mth.floor(recipeValues.get(0).getAsInt()), Mth.floor(recipeValues.get(1).getAsInt()));
                count *= tier.getOutputMultiplier();
                ItemStack output = itemStack.copy();
                output.setCount(count);
                ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(output);
            }
        });

        invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index]).shrink(1);

        Pair<Fluid, Integer> fluidOutput = recipe.getFluidOutputs();
        if (fluidOutput != null) {
            fluidInventory.ifPresent(fluidHandler -> {
                fluidHandler.fill(new FluidStack(fluidOutput.getFirst(), fluidOutput.getSecond()), IFluidHandler.FluidAction.EXECUTE);
            });
        }
    }

    @Override
    public LazyOptional<IItemHandlerModifiable> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryHandler.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidInventory.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void savePacketNBT(CompoundTag tag) {
        super.savePacketNBT(tag);
        tag.putIntArray("RecipeProgress", recipeProgress);

        int[] slotItemAmount = new int[InventoryHandlerHelper.OUTPUT_SLOTS.length];
        String[] slotItem = new String[InventoryHandlerHelper.OUTPUT_SLOTS.length];
        inventoryHandler.ifPresent(invHandler -> {
            for(int i = 0; i < slotItemAmount.length; i++) {
                slotItemAmount[i] = invHandler.getStackInSlot(InventoryHandlerHelper.OUTPUT_SLOTS[i]).getCount();
                slotItem[i] = invHandler.getStackInSlot(InventoryHandlerHelper.OUTPUT_SLOTS[i]).getItem().getRegistryName().toString();
            }
        });

        for(int i = 0; i < slotItemAmount.length; i++) {
            tag.putInt("SlotItemAmount" + i, slotItemAmount[i]);
            tag.putString("SlotItem" + i, slotItem[i]);
        }
    }

    @Override
    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);

        recipeProgress = tag.getIntArray("RecipeProgress");

        // set fluid ID for screens
        Fluid fluid = fluidInventory.map(fluidHandler -> fluidHandler.getFluidInTank(0).getFluid()).orElse(Fluids.EMPTY);
        fluidId = Registry.FLUID.getId(fluid);

        inventoryHandler.ifPresent(invHandler -> {
            for(int i = 0; i < InventoryHandlerHelper.OUTPUT_SLOTS.length; i++) {
                invHandler.setStackInSlot(InventoryHandlerHelper.OUTPUT_SLOTS[i], new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(tag.getString("SlotItem" + i))).asItem(), tag.getInt("SlotItemAmount" + i)));
            }
        });
    }

    @Override
    @NotNull
    public Component getName() {
        switch(tier) {
            case HIGH_END -> { return new TranslatableComponent(ModBlocks.HIGH_END_CENTRIFUGE.get().getDescriptionId()); }
            case NUCLEAR -> { return new TranslatableComponent(ModBlocks.NUCLEAR_CENTRIFUGE.get().getDescriptionId()); }
            case COSMIC -> { return new TranslatableComponent(ModBlocks.COSMIC_CENTRIFUGE.get().getDescriptionId()); }
            case CREATIVE -> { return new TranslatableComponent(ModBlocks.CREATIVE_CENTRIFUGE.get().getDescriptionId()); }
            default -> { return null; }
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        MenuType menuType = null;
        switch(tier) {
            case HIGH_END -> menuType = ModContainerTypes.HIGH_END_CENTRIFUGE.get();
            case NUCLEAR -> menuType = ModContainerTypes.NUCLEAR_CENTRIFUGE.get();
            case COSMIC -> menuType = ModContainerTypes.COSMIC_CENTRIFUGE.get();
            case CREATIVE -> menuType = ModContainerTypes.CREATIVE_CENTRIFUGE.get();
        }

        return new TieredCentrifugeContainer(menuType, windowId, playerInventory, this);
    }
}