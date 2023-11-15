package com.ultramega.centrifugetiersreproduced.blockentity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.container.TieredCentrifugeContainer;
import com.ultramega.centrifugetiersreproduced.recipe.TieredCentrifugeRecipe;
import com.ultramega.centrifugetiersreproduced.registry.IMultiRecipeProcessingBlockEntity;
import com.ultramega.centrifugetiersreproduced.registry.ModBlockEntityTypes;
import com.ultramega.centrifugetiersreproduced.registry.ModBlocks;
import com.ultramega.centrifugetiersreproduced.registry.ModMenuTypes;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.Centrifuge;
import cy.jdkdigital.productivebees.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.UpgradeableBlockEntity;
import cy.jdkdigital.productivebees.common.item.*;
import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.common.recipe.TimedRecipeInterface;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TieredCentrifugeBlockEntity extends CapabilityBlockEntity implements UpgradeableBlockEntity, IMultiRecipeProcessingBlockEntity {
    private final CentrifugeRecipe[] currentRecipe = new CentrifugeRecipe[4];
    public int[] recipeProgress = new int[4];
    public int fluidId = 0;
    public int transferCooldown = -1;
    public CentrifugeTiers tier;
    private int tankTick = 0;
    private int currentSlot = 0;

    private final LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(21, this) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == InventoryHandlerHelper.BOTTLE_SLOT) return false;

            return super.isItemValid(slot, stack);
        }

        @Override
        public boolean isContainerItem(Item item) {
            return false;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate, boolean fromAutomation) {
            if (fromAutomation) {
                // Skip lookup if the item is different
                ItemStack existing = this.stacks.get(slot);
                if (!existing.isEmpty() && !ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                    return stack;
                }
            }
            return super.insertItem(slot, stack, simulate, fromAutomation);
        }

        @Override
        public boolean isInputSlotItem(int slot, ItemStack item) {
            var currentStack = getStackInSlot(slot);

            if (currentStack.getCount() == currentStack.getMaxStackSize()) {
                return false;
            }

            boolean isProcessableItem =
                    ItemStack.isSameItemSameTags(currentStack, item) ||
                            item.getItem().equals(ModItems.GENE_BOTTLE.get()) ||
                            item.getItem().equals(ModItems.HONEY_TREAT.get()) ||
                            TieredCentrifugeBlockEntity.this.canProcessItemStack(item);

            return (isProcessableItem && (slot == InventoryHandlerHelper.INPUT_SLOT[0] || slot == InventoryHandlerHelper.INPUT_SLOT[1] || slot == InventoryHandlerHelper.INPUT_SLOT[2] || slot == InventoryHandlerHelper.INPUT_SLOT[3]))  || (!isProcessableItem && !super.isInputSlot(slot));
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            for(int i = 0; i < 4; i++) {
                if (slot == InventoryHandlerHelper.INPUT_SLOT[i] && this.getStackInSlot(slot).isEmpty()) {
                    TieredCentrifugeBlockEntity.this.recipeProgress[i] = 0;
                }
            }
        }
    });
    protected LazyOptional<IFluidHandler> fluidInventory = LazyOptional.of(() -> new InventoryHandlerHelper.FluidHandler(tier.getFluidCapacity()) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            TieredCentrifugeBlockEntity.this.fluidId = BuiltInRegistries.FLUID.getId(getFluid().getFluid());
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

    @Override
    public TimedRecipeInterface[] getCurrentRecipes() {
        return currentRecipe;
    }

    @Override
    public int[] getRecipeProgress() {
        return recipeProgress;
    }

    @Override
    public int getProcessingTime(TimedRecipeInterface recipe) {
        if(tier != CentrifugeTiers.CREATIVE) {
            return (int) ((recipe != null ? recipe.getProcessingTime() : ProductiveBeesConfig.GENERAL.centrifugeProcessingTime.get()) * getProcessingTimeModifier() / tier.getSpeed());
        } else {
            return 0;
        }
    }

    protected double getProcessingTimeModifier() {
        double timeUpgradeModifier = 1 - (getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());

        return Math.max(0, timeUpgradeModifier);
    }

    protected double getEnergyConsumptionModifier() {
        double timeUpgradeModifier = 1D + (getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());

        return Math.max(1, timeUpgradeModifier) * tier.getSpeed() * 10;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TieredCentrifugeBlockEntity blockEntity) {
        blockEntity.inventoryHandler.ifPresent(invHandler -> {
            if(blockEntity.currentSlot >= blockEntity.tier.getInputSlotAmount() - 1) {
                blockEntity.currentSlot = 0;
            } else {
                blockEntity.currentSlot++;
            }

            if (!invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[blockEntity.currentSlot]).isEmpty() && blockEntity.canOperate()) {
                // Process gene bottles
                ItemStack invItem = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[blockEntity.currentSlot]);
                if (invItem.getItem().equals(ModItems.GENE_BOTTLE.get())) {
                    level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, true));
                    int totalTime = blockEntity.getProcessingTime(null);

                    if (++blockEntity.recipeProgress[blockEntity.currentSlot] >= totalTime) {
                        blockEntity.completeGeneProcessing(invHandler, level.random);
                        blockEntity.recipeProgress[blockEntity.currentSlot] = 0;
                        blockEntity.setChanged();
                    }
                } else if (invItem.getItem().equals(ModItems.HONEY_TREAT.get())) {
                    level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, true));
                    int totalTime = blockEntity.getProcessingTime(null);

                    if (++blockEntity.recipeProgress[blockEntity.currentSlot] >= totalTime) {
                        blockEntity.completeTreatProcessing(invHandler);
                        blockEntity.recipeProgress[blockEntity.currentSlot] = 0;
                        blockEntity.setChanged();
                    }
                } else {
                    CentrifugeRecipe recipe = blockEntity.getRecipe(invHandler);
                    if (blockEntity.canProcessRecipe(recipe, invHandler)) {
                        level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, true));
                        int totalTime = blockEntity.getProcessingTime(recipe);

                        if (++blockEntity.recipeProgress[blockEntity.currentSlot] >= totalTime) {
                            blockEntity.completeRecipeProcessing(recipe, invHandler, level.random);
                            blockEntity.recipeProgress[blockEntity.currentSlot] = 0;
                            blockEntity.setChanged();
                        }
                    }
                }
            } else {
                level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, false));
            }

            // Pull items dropped on top
            if (ProductiveBeesConfig.GENERAL.centrifugeHopperMode.get() && --blockEntity.transferCooldown <= 0) {
                blockEntity.transferCooldown = 22;
                blockEntity.suckInItems(invHandler);
            }
        });
        if (++blockEntity.tankTick > 21) {
            blockEntity.tankTick = 0;
            blockEntity.tickFluidTank(level, pos, state, blockEntity);
        }

        if (state.getValue(Centrifuge.RUNNING) && level instanceof ServerLevel) {
            blockEntity.energyHandler.ifPresent(handler -> handler.extractEnergy((int) (ProductiveBeesConfig.GENERAL.centrifugePowerUse.get() * blockEntity.getEnergyConsumptionModifier()), false));
        }
    }

    public void tickFluidTank(Level level, BlockPos pos, BlockState state, TieredCentrifugeBlockEntity blockEntity) {
        this.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(fluidHandler -> {
            FluidStack fluidStack = fluidHandler.getFluidInTank(0);
            if (fluidStack.getAmount() > 0) {
                Direction[] directions = Direction.values();
                for (Direction direction : directions) {
                    BlockEntity te = level.getBlockEntity(worldPosition.relative(direction));
                    if (te != null && fluidStack.getAmount() > 0) {
                        te.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(h -> {
                            int amount = h.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);
                            if (amount > 0) {
                                amount = h.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                                fluidHandler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                            }
                        });
                    }
                }
            }
        });
    }

    private void suckInItems(IItemHandlerModifiable invHandler) {
        for (ItemEntity itemEntity : getCaptureItems()) {
            ItemStack itemStack = itemEntity.getItem();
            if (canProcessItemStack(itemStack) ||
                    itemStack.getItem().equals(ModItems.GENE_BOTTLE.get()) ||
                    itemStack.getItem().equals(ModItems.HONEY_TREAT.get()) && HoneyTreat.hasGene(itemStack)
            ) {
                captureItem(invHandler, itemEntity, currentSlot);
            }
        }
    }

    private List<ItemEntity> getCaptureItems() {
        assert level != null;
        return Centrifuge.COLLECTION_AREA_SHAPE.toAabbs().stream().flatMap((blockPos) -> level.getEntitiesOfClass(ItemEntity.class, blockPos.move(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()), EntitySelector.ENTITY_STILL_ALIVE).stream()).collect(Collectors.toList());
    }

    private static void captureItem(IItemHandlerModifiable invHandler, ItemEntity itemEntity, int currentSlot) {
        ItemStack leftoverStack = invHandler.insertItem(InventoryHandlerHelper.INPUT_SLOT[currentSlot], itemEntity.getItem(), false);
        if (leftoverStack.isEmpty()) {
            itemEntity.discard();
        } else {
            itemEntity.setItem(leftoverStack);
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
    public LazyOptional<IItemHandlerModifiable> getUpgradeHandler() {
        return upgradeHandler;
    }

    public boolean canProcessItemStack(ItemStack stack) {
        var directProcess = canProcessItemStack2(stack);

        if (stack.is(ModTags.Forge.COMBS) && !directProcess) {
            ItemStack singleComb;
            // config honeycomb
            if (stack.getItem() instanceof CombBlockItem) {
                singleComb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                singleComb.setTag(stack.getTag());
            } else {
                singleComb = BeeHelper.getRecipeOutputFromInput(level, stack.getItem());
            }
            return !singleComb.isEmpty() && canProcessItemStack2(singleComb);
        }

        return directProcess;
    }

    public boolean canProcessItemStack2(ItemStack stack) {
        IItemHandlerModifiable inv = new InventoryHandlerHelper.ItemHandler(5, null);
        inv.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT[currentSlot], stack);

        boolean isAllowedByFilter = true;
        List<ItemStack> filterUpgrades = this.getInstalledUpgrades(ModItems.UPGRADE_FILTER.get());
        if (!filterUpgrades.isEmpty()) {
            isAllowedByFilter = false;
            for (ItemStack filter : filterUpgrades) {
                List<Supplier<BeeIngredient>> allowedBees = FilterUpgradeItem.getAllowedBees(filter);
                for (Supplier<BeeIngredient> allowedBee : allowedBees) {
                    List<ItemStack> produceList = BeeHelper.getBeeProduce(level, (Bee) allowedBee.get().getCachedEntity(level), false);
                    for (ItemStack pStack: produceList) {
                        if (pStack.getItem().equals(stack.getItem())) {
                            isAllowedByFilter = true;
                            break;
                        }
                    }
                }
            }
        }

        CentrifugeRecipe recipe = getRecipe(inv);

        return isAllowedByFilter && recipe != null;
    }

    static Map<ItemStack, CentrifugeRecipe> blockRecipeMap = new HashMap<>();
    protected CentrifugeRecipe getRecipe(IItemHandlerModifiable inputHandler) {
        ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[currentSlot]);
        var directRecipe = getRecipe2(inputHandler);
        if (input.is(ModTags.Forge.COMBS) && directRecipe == null) {
            if (!blockRecipeMap.containsKey(input)) {
                ItemStack singleComb;
                // config honeycomb
                if (input.getItem() instanceof CombBlockItem) {
                    singleComb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                    singleComb.setTag(input.getTag());
                } else {
                    singleComb = BeeHelper.getRecipeOutputFromInput(level, input.getItem());
                }
                IItemHandlerModifiable inv = new InventoryHandlerHelper.ItemHandler(5);
                inv.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT[currentSlot], singleComb);
                blockRecipeMap.put(input, getRecipe2(inv));
            }
            return blockRecipeMap.get(input);
        }
        return directRecipe;
    }

    protected CentrifugeRecipe getRecipe2(IItemHandlerModifiable inputHandler) {
        ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[currentSlot]);
        if (input.isEmpty() || input == ItemStack.EMPTY || level == null) {
            return null;
        }

        if (currentRecipe[currentSlot] != null && new TieredCentrifugeRecipe(currentRecipe[currentSlot]).matches(new RecipeWrapper(inputHandler), level, currentSlot)) {
            return currentRecipe[currentSlot];
        }

        currentRecipe[currentSlot] = BeeHelper.getCentrifugeRecipe(level, inputHandler);

        Map<ResourceLocation, CentrifugeRecipe> allRecipes = level.getRecipeManager().byType(ModRecipeTypes.CENTRIFUGE_TYPE.get());
        Container inv = new RecipeWrapper(inputHandler);
        for (Map.Entry<ResourceLocation, CentrifugeRecipe> entry : allRecipes.entrySet()) {
            CentrifugeRecipe recipe = entry.getValue();
            if (new TieredCentrifugeRecipe(recipe).matches(inv, level, currentSlot)) {
                currentRecipe[currentSlot] = recipe;
                break;
            }
        }

        return currentRecipe[currentSlot];
    }


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

    protected void completeRecipeProcessing(CentrifugeRecipe recipe, IItemHandlerModifiable invHandler, RandomSource random) {
        ItemStack input = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[currentSlot]).copy();
        if (input.is(ModTags.Forge.COMBS) && !recipe.ingredient.test(input)) {
            ItemStack singleComb;
            if (input.getItem() instanceof CombBlockItem) {
                singleComb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get(), 4);
                singleComb.setTag(input.getTag());
            } else {
                singleComb = BeeHelper.getRecipeOutputFromInput(level, input.getItem());
            }
            invHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT[currentSlot], singleComb);
            for (int i = 0; i < 4; i++) {
                completeRecipeProcessing2(recipe, invHandler, random);
            }
            input.shrink(1);
            invHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT[currentSlot], input);
        } else {
            completeRecipeProcessing2(recipe, invHandler, random);
        }
    }

    protected void completeRecipeProcessing2(CentrifugeRecipe recipe, IItemHandlerModifiable invHandler, RandomSource random) {
        recipe.getRecipeOutputs().forEach((itemStack, recipeValues) -> {
            if (random.nextInt(100) <= recipeValues.get(2).getAsInt()) {
                int count = Mth.nextInt(random, Mth.floor(recipeValues.get(0).getAsInt()), Mth.floor(recipeValues.get(1).getAsInt()));
                count *= tier.getOutputMultiplier();
                ItemStack output = itemStack.copy();
                output.setCount(count);
                ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(output);
            }
        });

        invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[currentSlot]).shrink(1);

        Pair<Fluid, Integer> fluidOutput = recipe.getFluidOutputs();
        if (fluidOutput != null) {
            fluidInventory.ifPresent(fluidHandler -> {
                fluidHandler.fill(new FluidStack(fluidOutput.getFirst(), fluidOutput.getSecond()), IFluidHandler.FluidAction.EXECUTE);
            });
        }
    }

    private void completeGeneProcessing(IItemHandlerModifiable invHandler, RandomSource random) {
        ItemStack geneBottle = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[currentSlot]);

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

        invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[currentSlot]).shrink(1);
    }

    private void completeTreatProcessing(IItemHandlerModifiable invHandler) {
        ItemStack honeyTreat = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[currentSlot]);

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

        invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT[currentSlot]).shrink(1);
    }

    @Override
    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);

        recipeProgress = tag.getIntArray("RecipeProgress");

        // set fluid ID for screens
        Fluid fluid = fluidInventory.map(fluidHandler -> fluidHandler.getFluidInTank(0).getFluid()).orElse(Fluids.EMPTY);
        fluidId = BuiltInRegistries.FLUID.getId(fluid);

        inventoryHandler.ifPresent(invHandler -> {
            for(int i = 0; i < InventoryHandlerHelper.OUTPUT_SLOTS.length; i++) {
                invHandler.setStackInSlot(InventoryHandlerHelper.OUTPUT_SLOTS[i], new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(tag.getString("SlotItem" + i))).asItem(), tag.getInt("SlotItemAmount" + i)));
            }
        });
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
                slotItem[i] = ForgeRegistries.ITEMS.getKey(invHandler.getStackInSlot(InventoryHandlerHelper.OUTPUT_SLOTS[i]).getItem()).toString();
            }
        });

        for(int i = 0; i < slotItemAmount.length; i++) {
            tag.putInt("SlotItemAmount" + i, slotItemAmount[i]);
            tag.putString("SlotItem" + i, slotItem[i]);
        }
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
        if (tier != CentrifugeTiers.CREATIVE && cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    @NotNull
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