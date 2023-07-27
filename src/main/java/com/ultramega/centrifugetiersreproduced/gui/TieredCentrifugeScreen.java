package com.ultramega.centrifugetiersreproduced.gui;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import com.ultramega.centrifugetiersreproduced.config.CentrifugeTiersReproducedConfig;
import com.ultramega.centrifugetiersreproduced.container.TieredCentrifugeContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TieredCentrifugeScreen extends AbstractContainerScreen<TieredCentrifugeContainer> {
    private CentrifugeTiers tier;

    public TieredCentrifugeScreen(TieredCentrifugeContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);

        this.tier = getMenu().tileEntity.tier;
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, -5, 6, 4210752, false);

        int inventoryY = tier == CentrifugeTiers.COSMIC || tier == CentrifugeTiers.CREATIVE ? 78 : 96;
        guiGraphics.drawString(this.font, this.playerInventoryTitle, -5, this.getYSize() - inventoryY + 2, 4210752, false);

        this.menu.tileEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            // Fluid level tooltip
            int fluidX = tier == CentrifugeTiers.CREATIVE ? 145 : 127;
            int fluidSizeY = tier == CentrifugeTiers.COSMIC || tier == CentrifugeTiers.CREATIVE ? 72 : 54;
            if (isHovering(fluidX, 16, 6, fluidSizeY, mouseX, mouseY)) {
                List<FormattedCharSequence> tooltipList = new ArrayList<>();

                if (fluidStack.getAmount() > 0) {
                    tooltipList.add(Component.translatable("productivebees.screen.fluid_level", Component.translatable(fluidStack.getTranslationKey()).getString(), fluidStack.getAmount() + "mB").getVisualOrderText());
                }
                else {
                    tooltipList.add(Component.translatable("productivebees.hive.tooltip.empty").getVisualOrderText());
                }

                guiGraphics.renderTooltip(this.font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
            }
        });

        if(tier != CentrifugeTiers.CREATIVE) {
            this.menu.tileEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(handler -> {
                int energyAmount = handler.getEnergyStored();

                // Energy level tooltip
                if (isHovering(-5, 16, 6, tier == CentrifugeTiers.COSMIC ? 72 : 54, mouseX, mouseY)) {
                    List<FormattedCharSequence> tooltipList = new ArrayList<>();
                    tooltipList.add(Component.translatable("productivebees.screen.energy_level", energyAmount + "FE").getVisualOrderText());

                    guiGraphics.renderTooltip(this.font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
                }
            });
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation GUI = new ResourceLocation(CentrifugeTiersReproduced.MOD_ID, "textures/gui/" + getMenu().tileEntity.tier.getName() + "_centrifuge.png");

        // Draw main screen
        int xOffset = tier == CentrifugeTiers.CREATIVE ? 0 : 26;
        int yOffset = tier == CentrifugeTiers.COSMIC || tier == CentrifugeTiers.CREATIVE ? 18 : 0;
        guiGraphics.blit(GUI, this.getGuiLeft() - 13, this.getGuiTop(), 0, 0, this.getXSize() + xOffset, this.getYSize() + yOffset);

        // Draw progress
        int arrowX = tier == CentrifugeTiers.CREATIVE ? 176 : 202;
        int arrowY = tier == CentrifugeTiers.COSMIC ? 71 : 52;

        int progress = (int) (this.menu.tileEntity.recipeProgress[0] * (24 / (float) this.menu.tileEntity.getProcessingTime(this.menu.tileEntity.getCurrentRecipe())));
        guiGraphics.blit(GUI, this.getGuiLeft() + 35, this.getGuiTop() + 17, arrowX, arrowY, progress + 1, 16);

        int progress2 = (int) (this.menu.tileEntity.recipeProgress[1] * (24 / (float) this.menu.tileEntity.getProcessingTime(this.menu.tileEntity.getCurrentRecipe())));
        guiGraphics.blit(GUI, this.getGuiLeft() + 35, this.getGuiTop() + 53, arrowX, arrowY, progress2 + 1, 16);

        if(tier.getInputSlotAmount() > 2) {
            int progress3 = (int) (this.menu.tileEntity.recipeProgress[2] * (24 / (float) this.menu.tileEntity.getProcessingTime(this.menu.tileEntity.getCurrentRecipe())));
            guiGraphics.blit(GUI, this.getGuiLeft() + 35, this.getGuiTop() + 35, arrowX, arrowY, progress3 + 1, 16);

            if(tier.getInputSlotAmount() > 3) {
                int progress4 = (int) (this.menu.tileEntity.recipeProgress[3] * (24 / (float) this.menu.tileEntity.getProcessingTime(this.menu.tileEntity.getCurrentRecipe())));
                guiGraphics.blit(GUI, this.getGuiLeft() + 35, this.getGuiTop() + 71, arrowX, arrowY, progress4 + 1, 16);
            }
        }

        // Draw energy level
        int energySizeY = tier == CentrifugeTiers.COSMIC ? 70 : 52;
        guiGraphics.blit(GUI, getGuiLeft() - 5, getGuiTop() + 17, 206, 0, 4, energySizeY);
        this.menu.tileEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(handler -> {
            int energyAmount = handler.getEnergyStored();
            double energyLevel = energyAmount * (energySizeY / (double) handler.getMaxEnergyStored());
            guiGraphics.blit(GUI, getGuiLeft() - 5, getGuiTop() + 17, 8, 17, 4, (int)(energySizeY - energyLevel));
        });

        // Draw fluid tank
        this.menu.tileEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            if (fluidStack.getAmount() > 0) {
                int fluidX = tier == CentrifugeTiers.CREATIVE ? 145 : 127;
                int fluidY = tier == CentrifugeTiers.COSMIC || tier == CentrifugeTiers.CREATIVE ? 87 : 69;

                FluidContainerUtil.renderFluidTank(guiGraphics, this, fluidStack, handler.getTankCapacity(0), fluidX, fluidY, 4, 52, 0);
            }
        });
    }
}