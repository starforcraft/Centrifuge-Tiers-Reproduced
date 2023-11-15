package com.ultramega.centrifugetiersreproduced.container;

import com.ultramega.centrifugetiersreproduced.blockentity.InventoryHandlerHelper;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public abstract class AbstractContainer extends cy.jdkdigital.productivebees.container.AbstractContainer {
    protected AbstractContainer(@Nullable MenuType<?> type, int id) {
        super(type, id);
    }

    @Override
    protected int addSlotRange(Container handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            if (handler instanceof InventoryHandlerHelper.ItemHandler itemHandler) {
                addSlot(new ManualSlotItemHandler(itemHandler, index, x, y));
            }
            else {
                addSlot(new Slot(handler, index, x, y));
            }
            x += dx;
            index++;
        }
        return index;
    }

    @Override
    protected int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            if (handler instanceof InventoryHandlerHelper.ItemHandler itemHandler) {
                addSlot(new ManualSlotItemHandler(itemHandler, index, x, y));
            }
            x += dx;
            index++;
        }
        return index;
    }
}