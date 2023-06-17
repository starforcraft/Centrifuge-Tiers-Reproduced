package com.Ultramega.CentrifugeTiersReproduced.blockentity;

import com.Ultramega.CentrifugeTiersReproduced.CentrifugeTiers;
import com.Ultramega.CentrifugeTiersReproduced.blocks.TieredCentrifuge;
import com.Ultramega.CentrifugeTiersReproduced.container.TieredCentrifugeContainer;
import com.Ultramega.CentrifugeTiersReproduced.recipe.TieredCentrifugeRecipe;
import com.Ultramega.CentrifugeTiersReproduced.registry.ModBlocks;
import com.Ultramega.CentrifugeTiersReproduced.registry.ModMenuTypes;
import com.Ultramega.CentrifugeTiersReproduced.registry.ModBlockEntityTypes;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TieredCentrifugeBlockEntity extends PoweredCentrifugeBlockEntity {
    private CentrifugeRecipe currentRecipe = null;
    private CentrifugeRecipe currentRecipe2 = null;
    private CentrifugeRecipe currentRecipe3 = null;
    private CentrifugeRecipe currentRecipe4 = null;
    public int recipeProgress2 = 0;
    public int recipeProgress3 = 0;
    public int recipeProgress4 = 0;
    public int fluidId = 0;
    public int transferCooldown = -1;
    public CentrifugeTiers tier;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(21, this) {
        @Override
        public boolean isContainerItem(Item item) {
            return false;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate, boolean fromAutomation) {
            if (fromAutomation) {
                // Skip recipe lookup if the item is different
                ItemStack existing = this.stacks.get(slot);
                if (!existing.isEmpty()) {
                    if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                        return stack;
                    }
                }
            }
            return super.insertItem(slot, stack, simulate, fromAutomation);
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
                TieredCentrifugeBlockEntity.this.recipeProgress = 0;
            }
            if (slot == InventoryHandlerHelper.INPUT_SLOT[1] && this.getStackInSlot(slot).isEmpty()) {
                TieredCentrifugeBlockEntity.this.recipeProgress2 = 0;
            }
            if (slot == InventoryHandlerHelper.INPUT_SLOT[2] && this.getStackInSlot(slot).isEmpty()) {
                TieredCentrifugeBlockEntity.this.recipeProgress3 = 0;
            }
            if (slot == InventoryHandlerHelper.INPUT_SLOT[3] && this.getStackInSlot(slot).isEmpty()) {
                TieredCentrifugeBlockEntity.this.recipeProgress4 = 0;
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
        if(entityType == ModBlockEntityTypes.HIGH_END_CENTRIFUGE.get()) {
            this.tier = CentrifugeTiers.HIGH_END;
        } else if(entityType == ModBlockEntityTypes.NUCLEAR_CENTRIFUGE.get()) {
            this.tier = CentrifugeTiers.NUCLEAR;
        } else if(entityType == ModBlockEntityTypes.COSMIC_CENTRIFUGE.get()) {
            this.tier = CentrifugeTiers.COSMIC;
        } else if(entityType == ModBlockEntityTypes.CREATIVE_CENTRIFUGE.get()) {
            this.tier = CentrifugeTiers.CREATIVE;
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TieredCentrifugeBlockEntity blockEntity) {
        blockEntity.inventoryHandler.ifPresent(invHandler -> {
            for(int i = 0; i < blockEntity.tier.getInputSlotAmount(); i++) {
                if(i == 0) {
                    if (!invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[i]).isEmpty() && blockEntity.canOperate()) {
                        // Process gene bottles
                        ItemStack invItem = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[i]);
                        if (invItem.getItem().equals(ModItems.GENE_BOTTLE.get())) {
                            level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, true));
                            int totalTime = blockEntity.getProcessingTime();

                            if (++blockEntity.recipeProgress >= totalTime) {
                                blockEntity.completeGeneProcessing(invHandler, i, level.random);
                                blockEntity.recipeProgress = 0;
                                blockEntity.setChanged();
                            }
                        } else if (invItem.getItem().equals(ModItems.HONEY_TREAT.get())) {
                            level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, true));
                            int totalTime = blockEntity.getProcessingTime();

                            if (++blockEntity.recipeProgress >= totalTime) {
                                blockEntity.completeTreatProcessing(invHandler, i);
                                blockEntity.recipeProgress = 0;
                                blockEntity.setChanged();
                            }
                        } else {
                            CentrifugeRecipe recipe = blockEntity.getRecipe(invHandler, i);
                            if (blockEntity.canProcessRecipe(recipe, invHandler)) {
                                level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, true));
                                int totalTime = blockEntity.getProcessingTime();

                                if (++blockEntity.recipeProgress >= totalTime) {
                                    blockEntity.completeRecipeProcessing(recipe, invHandler, i, level.random);
                                    blockEntity.recipeProgress = 0;
                                    blockEntity.setChanged();
                                }
                            }
                        }
                    } else {
                        level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, false));
                    }

                    // Pull items dropped on top
                    if (ProductiveBeesConfig.GENERAL.centrifugeHopperMode.get() && --blockEntity.transferCooldown <= 0) {
                        blockEntity.transferCooldown = 22;
                        blockEntity.suckInItems(invHandler, i);
                    }
                } else if(i == 1) {
                    if (!invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[i]).isEmpty() && blockEntity.canOperate()) {
                        // Process gene bottles
                        ItemStack invItem = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[i]);
                        if (invItem.getItem().equals(ModItems.GENE_BOTTLE.get())) {
                            level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, true));
                            int totalTime = blockEntity.getProcessingTime();

                            if (++blockEntity.recipeProgress2 >= totalTime) {
                                blockEntity.completeGeneProcessing(invHandler, i, level.random);
                                blockEntity.recipeProgress2 = 0;
                                blockEntity.setChanged();
                            }
                        } else if (invItem.getItem().equals(ModItems.HONEY_TREAT.get())) {
                            level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, true));
                            int totalTime = blockEntity.getProcessingTime();

                            if (++blockEntity.recipeProgress2 >= totalTime) {
                                blockEntity.completeTreatProcessing(invHandler, i);
                                blockEntity.recipeProgress2 = 0;
                                blockEntity.setChanged();
                            }
                        } else {
                            CentrifugeRecipe recipe = blockEntity.getRecipe(invHandler, i);
                            if (blockEntity.canProcessRecipe(recipe, invHandler)) {
                                level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, true));
                                int totalTime = blockEntity.getProcessingTime();

                                if (++blockEntity.recipeProgress2 >= totalTime) {
                                    blockEntity.completeRecipeProcessing(recipe, invHandler, i, level.random);
                                    blockEntity.recipeProgress2 = 0;
                                    blockEntity.setChanged();
                                }
                            }
                        }
                    } else {
                        level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, false));
                    }

                    // Pull items dropped on top
                    if (ProductiveBeesConfig.GENERAL.centrifugeHopperMode.get() && --blockEntity.transferCooldown <= 0) {
                        blockEntity.transferCooldown = 22;
                        blockEntity.suckInItems(invHandler, i);
                    }
                } else if(i == 2) {
                    if (!invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[i]).isEmpty() && blockEntity.canOperate()) {
                        // Process gene bottles
                        ItemStack invItem = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[i]);
                        if (invItem.getItem().equals(ModItems.GENE_BOTTLE.get())) {
                            level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, true));
                            int totalTime = blockEntity.getProcessingTime();

                            if (++blockEntity.recipeProgress3 >= totalTime) {
                                blockEntity.completeGeneProcessing(invHandler, i, level.random);
                                blockEntity.recipeProgress3 = 0;
                                blockEntity.setChanged();
                            }
                        } else if (invItem.getItem().equals(ModItems.HONEY_TREAT.get())) {
                            level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, true));
                            int totalTime = blockEntity.getProcessingTime();

                            if (++blockEntity.recipeProgress3 >= totalTime) {
                                blockEntity.completeTreatProcessing(invHandler, i);
                                blockEntity.recipeProgress3 = 0;
                                blockEntity.setChanged();
                            }
                        } else {
                            CentrifugeRecipe recipe = blockEntity.getRecipe(invHandler, i);
                            if (blockEntity.canProcessRecipe(recipe, invHandler)) {
                                level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, true));
                                int totalTime = blockEntity.getProcessingTime();

                                if (++blockEntity.recipeProgress3 >= totalTime) {
                                    blockEntity.completeRecipeProcessing(recipe, invHandler, i, level.random);
                                    blockEntity.recipeProgress3 = 0;
                                    blockEntity.setChanged();
                                }
                            }
                        }
                    } else {
                        level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, false));
                    }

                    // Pull items dropped on top
                    if (ProductiveBeesConfig.GENERAL.centrifugeHopperMode.get() && --blockEntity.transferCooldown <= 0) {
                        blockEntity.transferCooldown = 22;
                        blockEntity.suckInItems(invHandler, i);
                    }
                } else if(i == 3) {
                    if (!invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[i]).isEmpty() && blockEntity.canOperate()) {
                        // Process gene bottles
                        ItemStack invItem = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[i]);
                        if (invItem.getItem().equals(ModItems.GENE_BOTTLE.get())) {
                            level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, true));
                            int totalTime = blockEntity.getProcessingTime();

                            if (++blockEntity.recipeProgress4 >= totalTime) {
                                blockEntity.completeGeneProcessing(invHandler, i, level.random);
                                blockEntity.recipeProgress4 = 0;
                                blockEntity.setChanged();
                            }
                        } else if (invItem.getItem().equals(ModItems.HONEY_TREAT.get())) {
                            level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, true));
                            int totalTime = blockEntity.getProcessingTime();

                            if (++blockEntity.recipeProgress4 >= totalTime) {
                                blockEntity.completeTreatProcessing(invHandler, i);
                                blockEntity.recipeProgress4 = 0;
                                blockEntity.setChanged();
                            }
                        } else {
                            CentrifugeRecipe recipe = blockEntity.getRecipe(invHandler, i);
                            if (blockEntity.canProcessRecipe(recipe, invHandler)) {
                                level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, true));
                                int totalTime = blockEntity.getProcessingTime();

                                if (++blockEntity.recipeProgress4 >= totalTime) {
                                    blockEntity.completeRecipeProcessing(recipe, invHandler, i, level.random);
                                    blockEntity.recipeProgress4 = 0;
                                    blockEntity.setChanged();
                                }
                            }
                        }
                    } else {
                        level.setBlockAndUpdate(pos, state.setValue(TieredCentrifuge.RUNNING, false));
                    }

                    // Pull items dropped on top
                    if (ProductiveBeesConfig.GENERAL.centrifugeHopperMode.get() && --blockEntity.transferCooldown <= 0) {
                        blockEntity.transferCooldown = 22;
                        blockEntity.suckInItems(invHandler, i);
                    }
                }
            }
        });
        FluidTankBlockEntity.tick(level, pos, state, blockEntity);

        if (state.getValue(Centrifuge.RUNNING) && level instanceof ServerLevel) {
            blockEntity.energyHandler.ifPresent(handler -> handler.extractEnergy((int) (ProductiveBeesConfig.GENERAL.centrifugePowerUse.get() * blockEntity.getEnergyConsumptionModifier() * blockEntity.tier.getSpeed()), false));
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

    private void completeGeneProcessing(IItemHandlerModifiable invHandler, int index, RandomSource random) {
        ItemStack geneBottle = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index]);

        CompoundTag entityData = GeneBottle.getGenes(geneBottle);
        if (entityData == null) {
            return;
        }

        double chance = ProductiveBeesConfig.BEE_ATTRIBUTES.geneExtractChance.get();
        for (String attributeName : BeeAttributes.attributeList()) {
            if (random.nextDouble() <= chance) {
                int value = entityData.getInt("bee_" + attributeName);
                ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(Gene.getStack(BeeAttributes.getAttributeByName(attributeName), value));
            }
        }

        // Chance to get a type gene
        if (random.nextDouble() <= chance) {
            int typePurity = ProductiveBeesConfig.BEE_ATTRIBUTES.typeGenePurity.get();
            ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(Gene.getStack(entityData.getString("type"), random.nextInt(Math.max(0, typePurity - 5)) + 10));
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
        return TieredCentrifuge.COLLECTION_AREA_SHAPE.toAabbs().stream().flatMap((blockPos) -> level.getEntitiesOfClass(ItemEntity.class, blockPos.move(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()), EntitySelector.ENTITY_STILL_ALIVE).stream()).collect(Collectors.toList());
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
        var directProcess = super.canProcessItemStack(stack);

        if (stack.is(ModTags.Forge.COMBS) && !directProcess) {
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

        return directProcess;
    }

    protected CentrifugeRecipe getRecipe(IItemHandlerModifiable inputHandler, int index) {
        ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index]);
        var directRecipe = getRecipe2(inputHandler, index);
        if (input.is(ModTags.Forge.COMBS) && directRecipe == null) {
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

        return directRecipe;
    }

    protected CentrifugeRecipe getRecipe2(IItemHandlerModifiable inputHandler, int index) {
        if(index == 0) {
            ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index]);
            if (input.isEmpty() || input == ItemStack.EMPTY || level == null) {
                return null;
            }

            if (currentRecipe != null && currentRecipe.matches(new RecipeWrapper(inputHandler), level)) {
                return currentRecipe;
            }

            currentRecipe = BeeHelper.getCentrifugeRecipe(level.getRecipeManager(), inputHandler);

            Map<ResourceLocation, CentrifugeRecipe> allRecipes = level.getRecipeManager().byType(ModRecipeTypes.CENTRIFUGE_TYPE.get());
            Container inv = new RecipeWrapper(inputHandler);
            for (Map.Entry<ResourceLocation, CentrifugeRecipe> entry : allRecipes.entrySet()) {
                CentrifugeRecipe recipe = entry.getValue();
                if (recipe.matches(inv, level)) {
                    currentRecipe = recipe;
                    break;
                }
            }

            return currentRecipe;
        } else if(index == 1) {
            ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index]);
            if (input.isEmpty() || input == ItemStack.EMPTY || level == null) {
                return null;
            }

            if (currentRecipe2 != null && currentRecipe2.matches(new RecipeWrapper(inputHandler), level)) {
                return currentRecipe2;
            }

            currentRecipe2 = BeeHelper.getCentrifugeRecipe(level.getRecipeManager(), inputHandler);

            Map<ResourceLocation, CentrifugeRecipe> allRecipes = level.getRecipeManager().byType(ModRecipeTypes.CENTRIFUGE_TYPE.get());
            Container inv = new RecipeWrapper(inputHandler);
            for (Map.Entry<ResourceLocation, CentrifugeRecipe> entry : allRecipes.entrySet()) {
                CentrifugeRecipe recipe = entry.getValue();
                TieredCentrifugeRecipe recipe2 = new TieredCentrifugeRecipe(recipe.id, recipe.ingredient, recipe.itemOutput, recipe.fluidOutput);
                if (recipe2.matches(inv, level, index)) {
                    currentRecipe2 = recipe;
                    break;
                }
            }

            return currentRecipe2;
        } else if(index == 2) {
            ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index]);
            if (input.isEmpty() || input == ItemStack.EMPTY || level == null) {
                return null;
            }

            if (currentRecipe3 != null && currentRecipe3.matches(new RecipeWrapper(inputHandler), level)) {
                return currentRecipe3;
            }

            currentRecipe3 = BeeHelper.getCentrifugeRecipe(level.getRecipeManager(), inputHandler);

            Map<ResourceLocation, CentrifugeRecipe> allRecipes = level.getRecipeManager().byType(ModRecipeTypes.CENTRIFUGE_TYPE.get());
            Container inv = new RecipeWrapper(inputHandler);
            for (Map.Entry<ResourceLocation, CentrifugeRecipe> entry : allRecipes.entrySet()) {
                CentrifugeRecipe recipe = entry.getValue();
                TieredCentrifugeRecipe recipe2 = new TieredCentrifugeRecipe(recipe.id, recipe.ingredient, recipe.itemOutput, recipe.fluidOutput);
                if (recipe2.matches(inv, level, index)) {
                    currentRecipe3 = recipe;
                    break;
                }
            }

            return currentRecipe3;
        } else if(index == 3) {
            ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index]);
            if (input.isEmpty() || input == ItemStack.EMPTY || level == null) {
                return null;
            }

            if (currentRecipe4 != null && currentRecipe4.matches(new RecipeWrapper(inputHandler), level)) {
                return currentRecipe4;
            }

            currentRecipe4 = BeeHelper.getCentrifugeRecipe(level.getRecipeManager(), inputHandler);

            Map<ResourceLocation, CentrifugeRecipe> allRecipes = level.getRecipeManager().byType(ModRecipeTypes.CENTRIFUGE_TYPE.get());
            Container inv = new RecipeWrapper(inputHandler);
            for (Map.Entry<ResourceLocation, CentrifugeRecipe> entry : allRecipes.entrySet()) {
                CentrifugeRecipe recipe = entry.getValue();
                TieredCentrifugeRecipe recipe2 = new TieredCentrifugeRecipe(recipe.id, recipe.ingredient, recipe.itemOutput, recipe.fluidOutput);
                if (recipe2.matches(inv, level, index)) {
                    currentRecipe4 = recipe;
                    break;
                }
            }

            return currentRecipe4;
        } else {
            return null;
        }
    }

    protected void completeRecipeProcessing(CentrifugeRecipe recipe, IItemHandlerModifiable invHandler, int index, RandomSource random) {
        ItemStack input = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index]).copy();
        if (input.is(ModTags.Forge.COMBS) && !recipe.ingredient.test(input)) {
            ItemStack singleComb;
            if (input.getItem() instanceof CombBlockItem) {
                singleComb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get(), 4);
                singleComb.setTag(input.getTag());
            } else {
                singleComb = BeeHelper.getRecipeOutputFromInput(level, input.getItem());
            }
            invHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index], singleComb);
            for (int i = 0; i < 4; i++) {
                completeRecipeProcessing2(recipe, invHandler, index, random);
            }
            input.shrink(1);
            invHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT[index], input);
        } else {
            completeRecipeProcessing2(recipe, invHandler, index, random);
        }
    }

    protected void completeRecipeProcessing2(CentrifugeRecipe recipe, IItemHandlerModifiable invHandler, int index, RandomSource random) {
        recipe.getRecipeOutputs().forEach((itemStack, recipeValues) -> {
            if (random.nextInt(100) <= recipeValues.get(2).getAsInt()) {
                int count = Mth.nextInt(random, Mth.floor(recipeValues.get(0).getAsInt()), Mth.floor(recipeValues.get(1).getAsInt()));
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

    public int getRecipeProgress2() {
        return recipeProgress2;
    }

    @Override
    public LazyOptional<IItemHandlerModifiable> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryHandler.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidInventory.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void savePacketNBT(CompoundTag tag) {
        super.savePacketNBT(tag);
        tag.putInt("RecipeProgress", recipeProgress);
        tag.putInt("RecipeProgress2", recipeProgress2);
        tag.putInt("RecipeProgress3", recipeProgress3);
        tag.putInt("RecipeProgress4", recipeProgress4);

        int[] slotItemAmount = new int[InventoryHandlerHelper.OUTPUT_SLOTS.length];
        String[] slotItem = new String[InventoryHandlerHelper.OUTPUT_SLOTS.length];
        inventoryHandler.ifPresent(invHandler -> {
            for(int i = 0; i < slotItemAmount.length; i++) {
                slotItemAmount[i] = invHandler.getStackInSlot(InventoryHandlerHelper.OUTPUT_SLOTS[i]).getCount();
                slotItem[i] = ForgeRegistries.ITEMS.getKey(invHandler.getStackInSlot(InventoryHandlerHelper.OUTPUT_SLOTS[i]).getItem()).toString();
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

        recipeProgress = tag.getInt("RecipeProgress");
        recipeProgress2 = tag.getInt("RecipeProgress2");
        recipeProgress3 = tag.getInt("RecipeProgress3");
        recipeProgress4 = tag.getInt("RecipeProgress4");

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
    public Component getName() {
        switch(tier) {
            case HIGH_END -> { return Component.translatable(ModBlocks.HIGH_END_CENTRIFUGE.get().getDescriptionId()); }
            case NUCLEAR -> { return Component.translatable(ModBlocks.NUCLEAR_CENTRIFUGE.get().getDescriptionId()); }
            case COSMIC -> { return Component.translatable(ModBlocks.COSMIC_CENTRIFUGE.get().getDescriptionId()); }
            case CREATIVE -> { return Component.translatable(ModBlocks.CREATIVE_CENTRIFUGE.get().getDescriptionId()); }
            default -> { return null; }
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        MenuType menuType = null;
        switch(tier) {
            case HIGH_END -> menuType = ModMenuTypes.HIGH_END_CENTRIFUGE.get();
            case NUCLEAR -> menuType = ModMenuTypes.NUCLEAR_CENTRIFUGE.get();
            case COSMIC -> menuType = ModMenuTypes.COSMIC_CENTRIFUGE.get();
            case CREATIVE -> menuType = ModMenuTypes.CREATIVE_CENTRIFUGE.get();
        }

        return new TieredCentrifugeContainer(menuType, windowId, playerInventory, this);
    }
}