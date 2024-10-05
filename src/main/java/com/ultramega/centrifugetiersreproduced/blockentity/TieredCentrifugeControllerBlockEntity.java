package com.ultramega.centrifugetiersreproduced.blockentity;

import com.google.common.collect.Lists;
import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import com.ultramega.centrifugetiersreproduced.blocks.TieredCentrifugeCasingBlock;
import com.ultramega.centrifugetiersreproduced.blocks.TieredCentrifugeControllerBlock;
import com.ultramega.centrifugetiersreproduced.container.TieredCentrifugeContainer;
import com.ultramega.centrifugetiersreproduced.utils.MultiBlockHelper;
import com.ultramega.centrifugetiersreproduced.utils.MultiBlockInfo;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.common.item.*;
import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.common.recipe.TimedRecipeInterface;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivebees.util.GeneGroup;
import cy.jdkdigital.productivelib.common.block.entity.FluidTankBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.block.entity.UpgradeableBlockEntity;
import cy.jdkdigital.productivelib.registry.LibItems;
import cy.jdkdigital.productivelib.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TieredCentrifugeControllerBlockEntity extends FluidTankBlockEntity implements MenuProvider, UpgradeableBlockEntity {
    private final CentrifugeTiers tier;

    protected int validateTime = 15;
    protected boolean validStructure;
    protected final List<BlockPos> structureBlocks = new ArrayList<>();

    private boolean[] running = new boolean[] { false, false, false, false, false, false};
    public int[] recipeProgress = new int[] { 0, 0, 0, 0, 0, 0 };
    public int fluidId = 0;

    public IItemHandlerModifiable inventoryHandler;
    public FluidTank fluidHandler;
    public EnergyStorage energyHandler;

    protected IItemHandlerModifiable upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
            LibItems.UPGRADE_TIME.get(),
            LibItems.UPGRADE_ENTITY_FILTER.get()
    ));

    public TieredCentrifugeControllerBlockEntity(CentrifugeTiers tier, BlockPos pos, BlockState blockState) {
        super(CentrifugeTiersReproduced.getControllerBlockEntityType(tier), pos, blockState);
        this.tier = tier;
        this.inventoryHandler = new TierInventoryHandlerHelper.BlockEntityItemStackHandler(tier, 11 + tier.getInputSlotAmountIncrease() + tier.getOutputSlotAmountIncrease(), this) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if (slot == TierInventoryHandlerHelper.BOTTLE_SLOT) return false;

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
                    if (!existing.isEmpty() && !ItemStack.isSameItemSameComponents(stack, existing)) {
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
                        ItemStack.isSameItemSameComponents(currentStack, item) ||
                                item.getItem().equals(ModItems.GENE_BOTTLE.get()) ||
                                item.getItem().equals(ModItems.HONEY_TREAT.get()) ||
                                TieredCentrifugeControllerBlockEntity.this.canProcessItemStack(item, (slot - 1) % (1 + tier.getInputSlotAmountIncrease()));

                return (isProcessableItem && isInputSlots(slot)) || (!isProcessableItem && !super.isInputSlot(slot));
            }

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                if (isInputSlots(slot) && this.getStackInSlot(slot).isEmpty()) {
                    TieredCentrifugeControllerBlockEntity.this.recipeProgress[slot - 1] = 0;
                }
            }
        };
        this.fluidHandler = new FluidTank(tier.getFluidCapacity()) {
            @Override
            protected void onContentsChanged() {
                TieredCentrifugeControllerBlockEntity.this.fluidId = BuiltInRegistries.FLUID.getId(getFluid().getFluid());
                TieredCentrifugeControllerBlockEntity.this.setChanged();
            }
        };
        this.energyHandler = new EnergyStorage(getTier().getEnergyCapacity());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TieredCentrifugeControllerBlockEntity blockEntity) {
        if (!blockEntity.isValidStructure()) {
            blockEntity.validateTime++;
            if (blockEntity.validateTime >= 20) {
                blockEntity.validateStructure(level);
            }
            return;
        }

        if (blockEntity.inventoryHandler instanceof TierInventoryHandlerHelper.BlockEntityItemStackHandler itemStackHandler) {
            for (int i = 0; i < TierInventoryHandlerHelper.getInputSlotsForTier(blockEntity.tier).length; i++) {
                if (!itemStackHandler.getStackInSlot(TierInventoryHandlerHelper.getInputSlotsForTier(blockEntity.tier)[i]).isEmpty() && blockEntity.canOperate()) {
                    // Process gene bottles
                    ItemStack invItem = itemStackHandler.getStackInSlot(TierInventoryHandlerHelper.getInputSlotsForTier(blockEntity.tier)[i]);
                    if (invItem.getItem().equals(ModItems.GENE_BOTTLE.get())) {
                        blockEntity.running[i] = true;
                        int totalTime = blockEntity.getProcessingTime(null);

                        if (++blockEntity.recipeProgress[i] >= totalTime) {
                            blockEntity.completeGeneProcessing(itemStackHandler, level.random, i);
                            blockEntity.recipeProgress[i] = 0;
                            blockEntity.setChanged();
                        }
                    } else if (invItem.getItem().equals(ModItems.HONEY_TREAT.get())) {
                        blockEntity.running[i] = true;
                        int totalTime = blockEntity.getProcessingTime(null);

                        if (++blockEntity.recipeProgress[i] >= totalTime) {
                            blockEntity.completeTreatProcessing(itemStackHandler, i);
                            blockEntity.recipeProgress[i] = 0;
                            blockEntity.setChanged();
                        }
                    } else {
                        RecipeHolder<CentrifugeRecipe> recipe = blockEntity.getRecipe(itemStackHandler, i);
                        if (blockEntity.canProcessRecipe(recipe, itemStackHandler)) {
                            blockEntity.running[i] = true;
                            int totalTime = blockEntity.getProcessingTime(recipe);

                            if (++blockEntity.recipeProgress[i] >= totalTime) {
                                blockEntity.completeRecipeProcessing(recipe, itemStackHandler, level.random, i);
                                blockEntity.recipeProgress[i] = 0;
                                blockEntity.setChanged();
                            }
                        }
                    }
                } else {
                    blockEntity.running[i] = false;
                }

                if (blockEntity.running[i]) {
                    blockEntity.energyHandler.extractEnergy((int) (ProductiveBeesConfig.GENERAL.centrifugePowerUse.get() * blockEntity.getEnergyConsumptionModifier()), false);
                }
            }
        }
        FluidTankBlockEntity.tick(level, pos, state, blockEntity);
    }

    @Override
    public void tickFluidTank(Level level, BlockPos pos, BlockState state, FluidTankBlockEntity blockEntity) {
        if (!isValidStructure()) return;

        IFluidHandler fluidHandler = blockEntity.getFluidHandler();
        FluidStack fluidStack = fluidHandler.getFluidInTank(0);
        if (fluidStack.getAmount() > 0) {
            Direction[] directions = Direction.values();
            for (Direction direction : directions) {
                if (fluidStack.getAmount() > 0) {
                    BlockPos targetPos = pos.relative(direction.getOpposite());
                    if (level.getBlockEntity(targetPos) instanceof TieredCentrifugeCasingBlockEntity casingBlockEntity && casingBlockEntity.getController().equals(this))
                        return;

                    IFluidHandler h = level.getCapability(Capabilities.FluidHandler.BLOCK, targetPos, null);
                    if (h != null) {
                        int amount = h.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);
                        if (amount > 0) {
                            amount = h.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                            fluidHandler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                }
            }
        }
    }

    public int getProcessingTime(RecipeHolder<? extends TimedRecipeInterface> recipe) {
        return (int) (
                (recipe != null ? recipe.value().getProcessingTime() : ProductiveBeesConfig.GENERAL.centrifugeProcessingTime.get()) * getProcessingTimeModifier()
        );
    }

    protected double getProcessingTimeModifier() {
        double timeUpgradeModifier = 1 - (ProductiveBeesConfig.UPGRADES.timeBonus.get() * (getUpgradeCount(ModItems.UPGRADE_TIME.get()) + getUpgradeCount(LibItems.UPGRADE_TIME.get())));

        return Math.max(0, timeUpgradeModifier) / 9 / tier.getSpeed();
    }

    protected double getEnergyConsumptionModifier() {
        double timeUpgradeModifier = 1D + (ProductiveBeesConfig.UPGRADES.timeBonus.get() * (getUpgradeCount(ModItems.UPGRADE_TIME.get()) + getUpgradeCount(LibItems.UPGRADE_TIME.get())));

        return Math.max(1, timeUpgradeModifier) * 3;
    }

    protected boolean canOperate() {
        return energyHandler.getEnergyStored() >= ProductiveBeesConfig.GENERAL.centrifugePowerUse.get();
    }

    @Override
    public IItemHandlerModifiable getUpgradeHandler() {
        return upgradeHandler;
    }

    public boolean canProcessItemStack(ItemStack stack, int inputSlot) {
        var directProcess = canProcessItemStack2(stack, inputSlot);

        if (stack.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS) && !directProcess) {
            ItemStack singleComb = getSingleComb(stack);
            return !singleComb.isEmpty() && canProcessItemStack2(singleComb, inputSlot);
        }

        return directProcess;
    }

    public boolean canProcessItemStack2(ItemStack stack, int inputSlot) {
        var inv = new TierInventoryHandlerHelper.BlockEntityItemStackHandler(tier, 2 + tier.getInputSlotAmountIncrease(), null);
        inv.setStackInSlot(TierInventoryHandlerHelper.getInputSlotsForTier(tier)[inputSlot], stack);

        boolean isAllowedByFilter = true;
        List<ItemStack> olfFilterUpgrades = this.getInstalledUpgrades(ModItems.UPGRADE_FILTER.get());
        List<ItemStack> filterUpgrades = getInstalledUpgrades(LibItems.UPGRADE_ENTITY_FILTER.get());
        if (!olfFilterUpgrades.isEmpty()) {
            isAllowedByFilter = false;
            for (ItemStack filter : olfFilterUpgrades) {
                List<Supplier<BeeIngredient>> allowedBees = FilterUpgradeItem.getAllowedBees(filter);
                for (Supplier<BeeIngredient> allowedBee : allowedBees) {
                    List<ItemStack> produceList = BeeHelper.getBeeProduce(level, (Bee) allowedBee.get().getCachedEntity(level), false, 1.0);
                    for (ItemStack pStack: produceList) {
                        if (pStack.getItem().equals(stack.getItem())) {
                            isAllowedByFilter = true;
                            break;
                        }
                    }
                }
            }
        }
        if (!filterUpgrades.isEmpty()) {
            isAllowedByFilter = false;
            for (ItemStack filter : filterUpgrades) {
                List<ResourceLocation> entities = filter.getOrDefault(ModDataComponents.ENTITY_TYPE_LIST, new ArrayList<>());
                for (ResourceLocation beeType : entities) {
                    var allowedBee = BeeIngredientFactory.getIngredient(beeType);
                    if (allowedBee.get() != null) {
                        List<ItemStack> produceList = BeeHelper.getBeeProduce(level, (Bee) allowedBee.get().getCachedEntity(level), false, 1.0);
                        for (ItemStack pStack : produceList) {
                            if (pStack.getItem().equals(stack.getItem())) {
                                isAllowedByFilter = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        RecipeHolder<CentrifugeRecipe> recipe = this.getRecipe(inv, inputSlot);

        return isAllowedByFilter && recipe != null;
    }

    static Map<String, RecipeHolder<CentrifugeRecipe>> blockRecipeMap = new HashMap<>();
    protected RecipeHolder<CentrifugeRecipe> getRecipe(TierInventoryHandlerHelper.BlockEntityItemStackHandler inputHandler, int inputSlot) {
        if (blockRecipeMap.size() > 5000) {
            blockRecipeMap.clear();
        }
        ItemStack input = inputHandler.getStackInSlot(TierInventoryHandlerHelper.getInputSlotsForTier(tier)[inputSlot]);
        String cacheKey = BuiltInRegistries.ITEM.getKey(input.getItem()).toString() + (!input.getComponents().isEmpty() ? input.getComponents().stream().map(TypedDataComponent::toString).reduce((s, s2) -> s + s2) : "");

        var directRecipe = getRecipe2(inputHandler, inputSlot);
        if (input.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS) && directRecipe == null) {
            if (!blockRecipeMap.containsKey(cacheKey)) {
                ItemStack singleComb = getSingleComb(input);
                var inv = new TierInventoryHandlerHelper.BlockEntityItemStackHandler(tier, 2 + tier.getInputSlotAmountIncrease());
                // Look up recipe for the single comb that makes up the input comb block
                inv.setStackInSlot(TierInventoryHandlerHelper.getInputSlotsForTier(tier)[inputSlot], singleComb);
                blockRecipeMap.put(cacheKey, getRecipe2(inv, inputSlot));
            }
            return blockRecipeMap.get(cacheKey);
        }
        return directRecipe;
    }

    private ItemStack getSingleComb(ItemStack input) {
        ItemStack singleComb;
        // config honeycomb
        if (input.getItem() instanceof CombBlockItem) {
            singleComb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
            singleComb.set(cy.jdkdigital.productivebees.init.ModDataComponents.BEE_TYPE, input.get(cy.jdkdigital.productivebees.init.ModDataComponents.BEE_TYPE));
        } else {
            singleComb = BeeHelper.getRecipeOutputFromInput(level, input.getItem());
        }

        return singleComb;
    }

    static Map<String, RecipeHolder<CentrifugeRecipe>> recipeMap = new HashMap<>();
    protected RecipeHolder<CentrifugeRecipe> getRecipe2(TierInventoryHandlerHelper.BlockEntityItemStackHandler inputHandler, int inputSlot) {
        if (recipeMap.size() > 5000) {
            recipeMap.clear();
        }
        ItemStack input = inputHandler.getStackInSlot(TierInventoryHandlerHelper.getInputSlotsForTier(tier)[inputSlot]);
        if (input.isEmpty() || level == null) {
            return null;
        }

        String cacheKey = BuiltInRegistries.ITEM.getKey(input.getItem()).toString() + (!input.getComponents().isEmpty() ? input.getComponents().stream().map(TypedDataComponent::toString).reduce((s, s2) -> s + s2) : "");
        if (!recipeMap.containsKey(cacheKey)) {
            recipeMap.put(cacheKey, BeeHelper.getCentrifugeRecipe(level, inputHandler));
        }

        return recipeMap.getOrDefault(cacheKey, null);
    }

    protected boolean canProcessRecipe(@Nullable RecipeHolder<CentrifugeRecipe> recipe, IItemHandlerModifiable invHandler) {
        if (recipe != null) {
            // Check if output slots has space for recipe output
            List<ItemStack> outputList = Lists.newArrayList();

            recipe.value().getRecipeOutputs().forEach((stack, value) -> {
                // Check for item with max possible output
                ItemStack item = new ItemStack(stack.getItem(), value.max());
                outputList.add(item);
            });

            // Allow overfilling of fluid but don't process if the tank has a different fluid
            FluidStack fluidOutput = recipe.value().getFluidOutputs();
            boolean fluidFlag = true;
            if (!fluidOutput.isEmpty()) {
                fluidFlag = fluidHandler.getFluidInTank(0).isEmpty() || fluidHandler.getFluidInTank(0).getFluid().equals(fluidOutput.getFluid());
            }

            return fluidFlag && ((TierInventoryHandlerHelper.BlockEntityItemStackHandler) invHandler).canFitStacks(outputList);
        }
        return false;
    }

    protected void completeRecipeProcessing(RecipeHolder<CentrifugeRecipe> recipe, IItemHandlerModifiable invHandler, RandomSource random, int inputSlot) {
        ItemStack input = invHandler.getStackInSlot(TierInventoryHandlerHelper.getInputSlotsForTier(tier)[inputSlot]).copy();
        if (input.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS) && !recipe.value().ingredient.test(input)) {
            ItemStack singleComb = getSingleComb(input);
            invHandler.setStackInSlot(TierInventoryHandlerHelper.getInputSlotsForTier(tier)[inputSlot], singleComb);
            for (int i = 0; i < 4; i++) {
                completeRecipeProcessing(recipe, invHandler, random, true, inputSlot);
            }
            input.shrink(1);
            invHandler.setStackInSlot(TierInventoryHandlerHelper.getInputSlotsForTier(tier)[inputSlot], input);
        } else {
            completeRecipeProcessing(recipe, invHandler, random, true, inputSlot);
        }
    }

    protected void completeRecipeProcessing(RecipeHolder<CentrifugeRecipe> recipe, IItemHandlerModifiable invHandler, RandomSource random, boolean stripWax, int inputSlot) {
        recipe.value().getRecipeOutputs().forEach((itemStack, recipeValues) -> {
            if ((!stripWax || !itemStack.is(ModTags.Common.WAXES)) && random.nextFloat() <= recipeValues.chance()) {
                int count = Mth.nextInt(random, Mth.floor(recipeValues.min()), Mth.floor(recipeValues.max()));
                count *= tier.getOutputMultiplier();
                ItemStack output = itemStack.copy();
                output.setCount(count);
                ((TierInventoryHandlerHelper.BlockEntityItemStackHandler) invHandler).addOutput(output);
            }
        });

        invHandler.getStackInSlot(TierInventoryHandlerHelper.getInputSlotsForTier(tier)[inputSlot]).shrink(1);

        FluidStack fluidOutput = recipe.value().getFluidOutputs();
        if (!fluidOutput.isEmpty()) {
            fluidHandler.fill(fluidOutput.copy(), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    private void completeGeneProcessing(IItemHandlerModifiable invHandler, RandomSource random, int inputSlot) {
        ItemStack geneBottle = invHandler.getStackInSlot(TierInventoryHandlerHelper.getInputSlotsForTier(tier)[inputSlot]);

        List<GeneGroup> entityData = GeneBottle.getGenes(geneBottle);
        if (entityData.isEmpty()) {
            return;
        }

        double chance = ProductiveBeesConfig.BEE_ATTRIBUTES.geneExtractChance.get();
        for (GeneGroup geneGroup : entityData) {
            if (random.nextDouble() <= chance) {
                ((TierInventoryHandlerHelper.BlockEntityItemStackHandler) invHandler).addOutput(Gene.getStack(geneGroup, 1));
            }
        }

        invHandler.getStackInSlot(TierInventoryHandlerHelper.getInputSlotsForTier(tier)[inputSlot]).shrink(1);
    }

    private void completeTreatProcessing(IItemHandlerModifiable invHandler, int inputSlot) {
        ItemStack honeyTreat = invHandler.getStackInSlot(TierInventoryHandlerHelper.getInputSlotsForTier(tier)[inputSlot]);

        List<GeneGroup> genes = HoneyTreat.getGenes(honeyTreat);
        if (!genes.isEmpty()) {
            for (GeneGroup geneGroup : genes) {
                ItemStack insertedGene = Gene.getStack(geneGroup, 1);
                ((TierInventoryHandlerHelper.BlockEntityItemStackHandler) invHandler).addOutput(insertedGene);
            }
        }

        invHandler.getStackInSlot(TierInventoryHandlerHelper.getInputSlotsForTier(tier)[inputSlot]).shrink(1);
    }

    @Override
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);

        recipeProgress = tag.getIntArray("RecipeProgress");

        // set fluid ID for screens
        Fluid fluid = fluidHandler.getFluidInTank(0).getFluid();
        fluidId = BuiltInRegistries.FLUID.getId(fluid);
    }

    @Override
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);

        tag.putIntArray("RecipeProgress", recipeProgress);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new TieredCentrifugeContainer(tier, containerId, inventory, this);
    }

    public void validateStructure(Level level) {
        validateTime = 0;
        MultiBlockHelper.buildStructureList(getBounds(), structureBlocks, blockPos -> true, this.getBlockPos());
        validStructure = MultiBlockHelper.validateStructure(structureBlocks, validBlocks(level), tier.getMultiblockInfo().blockCount());
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(TieredCentrifugeControllerBlock.PROPERTY_VALID, validStructure));

        if (validStructure) {
            linkCasings(level);
        }
    }

    protected Predicate<BlockPos> validBlocks(Level level) {
        return blockPos -> {
            Block block = level.getBlockState(blockPos).getBlock();
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (block instanceof TieredCentrifugeCasingBlock && blockEntity instanceof TieredCentrifugeCasingBlockEntity casingBlockEntity) {
                return casingBlockEntity.getTier() == this.tier && (!casingBlockEntity.isLinked() || (casingBlockEntity.getController() != null && casingBlockEntity.getController().equals(this)));
            }
            return false;
        };
    }

    protected void linkCasings(Level level) {
        if (!level.isClientSide) {
            structureBlocks.stream()
                    .map(level::getBlockEntity)
                    .filter(TieredCentrifugeCasingBlockEntity.class::isInstance)
                    .forEach(tileEntity -> ((TieredCentrifugeCasingBlockEntity) tileEntity).setControllerPos(this.worldPosition));
        }
    }

    protected void unlinkCasings(Level level) {
        if (!level.isClientSide) {
            structureBlocks.stream()
                    .map(level::getBlockEntity)
                    .filter(TieredCentrifugeCasingBlockEntity.class::isInstance)
                    .forEach(tileEntity -> ((TieredCentrifugeCasingBlockEntity) tileEntity).setControllerPos(null));
        }
    }

    public void invalidateStructure() {
        assert level != null;
        this.validStructure = false;
        if (!level.isClientSide) {
            level.getBlockState(this.getBlockPos()).setValue(TieredCentrifugeControllerBlock.PROPERTY_VALID, false);
        }
        unlinkCasings(level);
    }

    protected BoundingBox getBounds() {
        MultiBlockInfo multiblockInfo = tier.getMultiblockInfo();
        return MultiBlockHelper.buildStructureBounds(this.getBlockPos(), multiblockInfo.width(), multiblockInfo.height(), multiblockInfo.depth(), multiblockInfo.hOffset(), multiblockInfo.vOffset(), multiblockInfo.dOffset(), this.getBlockState().getValue(TieredCentrifugeControllerBlock.FACING));
    }

    @Override
    public void setRemoved() {
        assert level != null;
        unlinkCasings(level);
        super.setRemoved();
    }

    @Override
    public IItemHandler getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public FluidTank getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public EnergyStorage getEnergyHandler() {
        return energyHandler;
    }

    public CentrifugeTiers getTier() {
        return tier;
    }

    public boolean isValidStructure() {
        return validStructure;
    }
}