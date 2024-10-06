package com.ultramega.centrifugetiersreproduced.compat.jei;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.gui.TieredCentrifugeScreen;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static cy.jdkdigital.productivebees.compat.jei.ProductiveBeesJeiPlugin.CENTRIFUGE_TYPE;

public class TieredCentrifugeContainerHandler implements IGuiContainerHandler<TieredCentrifugeScreen> {
    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(TieredCentrifugeScreen screen, double mouseX, double mouseY) {
        List<IGuiClickableArea> clickableAreas = new ArrayList<>();
        int minusHeight = screen.getTierHeight() / 2;

        for (int i = 0; i < 1 + screen.getTier().getInputSlotAmountIncrease(); i++) {
            int yPos = 17 - minusHeight + (i * (screen.getTier() == CentrifugeTiers.TIER_1 ? 36 : 18));
            clickableAreas.add(IGuiClickableArea.createBasic(35, yPos, 24, 16, CENTRIFUGE_TYPE));
        }

        return clickableAreas;
    }
}
