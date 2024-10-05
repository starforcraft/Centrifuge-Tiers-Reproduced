package com.ultramega.centrifugetiersreproduced.blockentity;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.utils.SerializationHelper;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper.areItemStackTagsEqual;

public class TierInventoryHandlerHelper {
    public static final int BOTTLE_SLOT = 0;
    public static final int[] INPUT_SLOTS_TIER_1 = generateSlots(1, 1 + CentrifugeTiers.TIER_1.getInputSlotAmountIncrease());
    public static final int[] INPUT_SLOTS_TIER_2_3 = generateSlots(1, 1 + CentrifugeTiers.TIER_2.getInputSlotAmountIncrease());
    public static final int[] INPUT_SLOTS_TIER_4 = generateSlots(1, 1 + CentrifugeTiers.TIER_4.getInputSlotAmountIncrease());
    public static final int FLUID_ITEM_OUTPUT_SLOT = 26;

    public static final int[] OUTPUT_SLOTS_TIER_1 = generateSlots(3, 3 + (3 * 3) + CentrifugeTiers.TIER_1.getOutputSlotAmountIncrease() - 1);
    public static final int[] OUTPUT_SLOTS_TIER_2_3 = generateSlots(5, 5 + (3 * 3) + CentrifugeTiers.TIER_2.getOutputSlotAmountIncrease() - 1);
    public static final int[] OUTPUT_SLOTS_TIER_4 = generateSlots(7, 7 + (3 * 3) + CentrifugeTiers.TIER_4.getOutputSlotAmountIncrease() - 1);

    private static int[] generateSlots(int start, int end) {
        int[] slots = new int[end - start + 1];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = start + i;
        }
        return slots;
    }

    private static int getAvailableOutputSlot(CentrifugeTiers tier, BlockEntityItemStackHandler handler, ItemStack insertStack) {
        return getAvailableOutputSlot(tier, handler, insertStack, new ArrayList<>());
    }

    private static int getAvailableOutputSlot(CentrifugeTiers tier, BlockEntityItemStackHandler handler, ItemStack insertStack, List<Integer> blacklistedSlots) {
        int emptySlot = 0;
        for (int slot : handler.getOutputSlots()) {
            if (blacklistedSlots.contains(slot)) {
                continue;
            }
            ItemStack stack = handler.getStackInSlot(slot);
            System.out.println(stack.getCount());
            System.out.println(stack.getCount() + (insertStack.getCount() * tier.getOutputMultiplier()));
            System.out.println((stack.getCount() + (insertStack.getCount() * tier.getOutputMultiplier())) <= tier.getItemMaxStackSize());
            if (stack.isEmpty() && emptySlot == 0) {
                emptySlot = slot;
            } else if (stack.getItem().equals(insertStack.getItem()) && (stack.getCount() + (insertStack.getCount() * tier.getOutputMultiplier())) <= tier.getItemMaxStackSize()) {
                if (stack.isEmpty() || areItemsAndTagsEqual(stack, insertStack)) {
                    return slot;
                }
            }
        }
        return emptySlot;
    }

    public static boolean areItemsAndTagsEqual(ItemStack stack1, ItemStack stack2) {
        return (
                stack1.isEmpty() && stack2.isEmpty()
        ) ||
                (
                        stack1.getItem() == stack2.getItem() && areItemStackTagsEqual(stack1, stack2)
                );
    }

    public static int[] getInputSlotsForTier(CentrifugeTiers tier) {
        return tier == CentrifugeTiers.TIER_1 ? INPUT_SLOTS_TIER_1 : tier == CentrifugeTiers.TIER_4 ? INPUT_SLOTS_TIER_4 : INPUT_SLOTS_TIER_2_3;
    }

    public static int[] getOutputSlotsForTier(CentrifugeTiers tier) {
        return tier == CentrifugeTiers.TIER_1 ? OUTPUT_SLOTS_TIER_1 : tier == CentrifugeTiers.TIER_4 ? OUTPUT_SLOTS_TIER_4 : OUTPUT_SLOTS_TIER_2_3;
    }

    public static class BlockEntityItemStackHandler extends InventoryHandlerHelper.BlockEntityItemStackHandler {
        protected BlockEntity blockEntity;
        private final CentrifugeTiers tier;

        public BlockEntityItemStackHandler(CentrifugeTiers tier, int size) {
            this(tier, size, null);
        }

        public BlockEntityItemStackHandler(CentrifugeTiers tier, int size, @Nullable BlockEntity blockEntity) {
            super(size);
            this.blockEntity = blockEntity;
            this.tier = tier;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (blockEntity != null) {
                blockEntity.setChanged();
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            ListTag nbtTagList = new ListTag();

            for (int i = 0; i < this.stacks.size(); i++) {
                ItemStack stack = this.stacks.get(i);
                if (!stack.isEmpty()) {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putInt("Slot", i);
                    nbtTagList.add(SerializationHelper.encodeLargeStack(stack, provider, itemTag));
                }
            }

            CompoundTag nbt = new CompoundTag();
            nbt.put("Items", nbtTagList);
            return nbt;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);

            for (int i = 0; i < tagList.size(); i++) {
                CompoundTag itemTags = tagList.getCompound(i);
                int slot = itemTags.getInt("Slot");
                if (slot >= 0 && slot < this.stacks.size()) {
                    ItemStack stack = SerializationHelper.decodeLargeItemStack(provider, itemTags);
                    this.stacks.set(slot, stack);
                }
            }
        }

        @Override
        public boolean isInputSlot(int slot) {
            return slot == BOTTLE_SLOT || Arrays.stream(getInputSlotsForTier(tier)).anyMatch(s -> s == slot);
        }

        public boolean isInputSlots(int slot) {
            return Arrays.stream(getInputSlotsForTier(tier)).anyMatch(s -> s == slot);
        }

        @Override
        public boolean isInsertableSlot(int slot) {
            return slot != BOTTLE_SLOT && Arrays.stream(getInputSlotsForTier(tier)).noneMatch(s -> s == slot) && slot != FLUID_ITEM_OUTPUT_SLOT;
        }

        @Override
        public boolean isContainerItem(Item item) {
            return item == Items.GLASS_BOTTLE;
        }

        @Override
        public boolean isInputSlotItem(int slot, ItemStack item) {
            return (slot == BOTTLE_SLOT && isContainerItem(item.getItem())) || (slot == FLUID_ITEM_OUTPUT_SLOT && !isContainerItem(item.getItem()));
        }

        @Override
        public ItemStack addOutput(@Nonnull ItemStack stack) {
            //Split the stack into smaller pieces if its over 64 items big
            List<Integer> outputStacks = new LinkedList<>();
            while (stack.getCount() > 0) {
                if (stack.getCount() <= tier.getItemMaxStackSize()) {
                    outputStacks.add(stack.getCount());
                    break;
                }
                outputStacks.add(tier.getItemMaxStackSize());
                stack.setCount(stack.getCount() - tier.getItemMaxStackSize());
            }
            //Add items to the available output slots for each of the splits created
            Iterator<Integer> iterator = outputStacks.iterator();
            while (iterator.hasNext()) {
                stack.setCount(iterator.next());
                int slot = getAvailableOutputSlot(tier, this, stack);
                if (slot > 0) {
                    ItemStack existingStack = this.getStackInSlot(slot);
                    if (existingStack.isEmpty()) {
                        setStackInSlot(slot, stack.copy());
                    } else {
                        existingStack.grow(stack.getCount());
                    }
                    onContentsChanged(slot);
                    iterator.remove();
                }
            }
            stack.setCount(outputStacks.stream().mapToInt(Integer::intValue).sum());
            //Returning the stack makes it possible for other methods to see if all or some items got added to the inventory
            return stack;
        }

        @Override
        public boolean canFitStacks(List<ItemStack> stacks) {
            List<Integer> usedSlots = new ArrayList<>();
            for (ItemStack stack : stacks) {
                int slot = getAvailableOutputSlot(tier,this, stack, usedSlots);
                if (slot == 0) {
                    return false;
                }
            }
            return true;
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return tier.getItemMaxStackSize();
        }

        @Override
        public int[] getOutputSlots() {
            return getOutputSlotsForTier(tier);
        }

        @Override
        public ItemStack getItem(int slot) {
            return getStackInSlot(slot);
        }

        @Override
        public int size() {
            return getSlots();
        }
    }
}
