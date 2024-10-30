package com.ultramega.centrifugetiersreproduced.gui;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import com.ultramega.centrifugetiersreproduced.container.TieredCentrifugeContainer;
import cy.jdkdigital.productivebees.util.FluidContainerUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TieredCentrifugeScreen extends AbstractContainerScreen<TieredCentrifugeContainer> {
    private static final ResourceLocation GUI_TEXTURE_TIER_1 = ResourceLocation.fromNamespaceAndPath(CentrifugeTiersReproduced.MODID, "textures/gui/tier_1_centrifuge.png");
    private static final ResourceLocation GUI_TEXTURE_TIER_2_AND_3 = ResourceLocation.fromNamespaceAndPath(CentrifugeTiersReproduced.MODID, "textures/gui/tier_2_3_centrifuge.png");
    private static final ResourceLocation GUI_TEXTURE_TIER_4 = ResourceLocation.fromNamespaceAndPath(CentrifugeTiersReproduced.MODID, "textures/gui/tier_4_centrifuge.png");

    private final CentrifugeTiers tier;

    public TieredCentrifugeScreen(CentrifugeTiers tier, TieredCentrifugeContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.tier = tier;
    }

    @Override
    public void render(@Nonnull GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int height = 54 + getTierHeight();
        int minusHeight =  getTierHeight() / 2;

        guiGraphics.drawString(font, this.title, -5, 6 - minusHeight, 4210752, false);
        guiGraphics.drawString(font, this.playerInventoryTitle, -5, (this.getYSize() - 96 + 2) + minusHeight, 4210752, false);

        FluidStack fluidStack = this.menu.blockEntity.fluidHandler.getFluidInTank(0);

        // Fluid level tooltip
        if (isHovering(129, 16 - minusHeight, 6, height, mouseX, mouseY)) {
            List<FormattedCharSequence> tooltipList = new ArrayList<>();

            if (fluidStack.getAmount() > 0) {
                tooltipList.add(Component.translatable("productivebees.screen.fluid_level", fluidStack.getHoverName().getString(), fluidStack.getAmount() + "mB").getVisualOrderText());
            } else {
                tooltipList.add(Component.translatable("productivebees.hive.tooltip.empty").getVisualOrderText());
            }

            guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
        }

        int energyAmount = this.menu.blockEntity.energyHandler.getEnergyStored();

        // Energy level tooltip
        if (isHovering(-5, 16 - minusHeight, 6, height, mouseX, mouseY)) {
            List<FormattedCharSequence> tooltipList = new ArrayList<>();
            tooltipList.add(Component.translatable("productivebees.screen.energy_level", energyAmount + "FE").getVisualOrderText());

            guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        var GUI = tier == CentrifugeTiers.TIER_1 ? GUI_TEXTURE_TIER_1 : tier == CentrifugeTiers.TIER_4 ? GUI_TEXTURE_TIER_4 : GUI_TEXTURE_TIER_2_AND_3;
        int height = 52 + getTierHeight();
        int minusHeight = getTierHeight() / 2;

        // Draw main screen
        guiGraphics.blit(GUI, this.getGuiLeft() - 13, this.getGuiTop() - minusHeight, 0, 0, this.getXSize() + 26, this.getYSize() + getTierHeight());

        // Draw progress
        for (int i = 0; i < 1 + tier.getInputSlotAmountIncrease(); i++) {
            if (this.menu.blockEntity.recipeProgress[i] > 0) {
                int processingTime = this.menu.blockEntity.getProcessingTime(null);
                int progress = (int) ((processingTime - this.menu.blockEntity.recipeProgress[i]) * (24 / (float) processingTime));

                guiGraphics.blit(GUI, this.getGuiLeft() + 35, this.getGuiTop() + 17 - minusHeight + (i * (tier == CentrifugeTiers.TIER_1 ? 36 : 18)), 202, height, progress + 1, 16);
            }
        }

        // Draw energy level
        guiGraphics.blit(GUI, getGuiLeft() - 5, getGuiTop() + 17 - minusHeight, 206, 0, 4, height);
        int energyAmount = this.menu.blockEntity.energyHandler.getEnergyStored();
        int energyLevel = (int) (energyAmount * (height / (float)tier.getEnergyCapacity()));
        guiGraphics.blit(GUI, getGuiLeft() - 5, getGuiTop() + 17 - minusHeight, 8, 17, 4, height - energyLevel);

        // Draw fluid tank
        FluidStack fluidStack = this.menu.blockEntity.fluidHandler.getFluidInTank(0);

        if (fluidStack.getAmount() > 0) {
            FluidContainerUtil.renderFluidTank(guiGraphics, this, fluidStack, this.menu.blockEntity.fluidHandler.getTankCapacity(0), 127, 17 - minusHeight, 4, height, 0);
        }
    }

    public int getTierHeight() {
        return (tier == CentrifugeTiers.TIER_2 || tier == CentrifugeTiers.TIER_3 ? 18 : tier == CentrifugeTiers.TIER_4 ? 54 : 0);
    }

    public CentrifugeTiers getTier() {
        return tier;
    }
}