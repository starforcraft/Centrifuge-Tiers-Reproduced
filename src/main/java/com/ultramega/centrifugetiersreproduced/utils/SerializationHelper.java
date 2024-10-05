package com.ultramega.centrifugetiersreproduced.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

public class SerializationHelper {
    public static final Codec<ItemStack> LARGE_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ItemStack.ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
                            ExtraCodecs.intRange(1, Integer.MAX_VALUE).fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
                            DataComponentPatch.CODEC
                                    .optionalFieldOf("components", DataComponentPatch.EMPTY)
                                    .forGetter(ItemStack::getComponentsPatch)
                    )
                    .apply(instance, ItemStack::new));

    public static Tag encodeLargeStack(ItemStack stack, HolderLookup.Provider provider, CompoundTag tag) {
        return LARGE_CODEC.encode(stack, provider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
    }

    public static ItemStack decodeLargeItemStack(HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.isEmpty()) return ItemStack.EMPTY;
        return LARGE_CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag)
                .resultOrPartial(string -> System.err.println("Tried to load invalid item: " +  string)).orElse(ItemStack.EMPTY);
    }
}
