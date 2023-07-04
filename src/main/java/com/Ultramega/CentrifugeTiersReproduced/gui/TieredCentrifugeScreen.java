package com.Ultramega.CentrifugeTiersReproduced.gui;

import com.Ultramega.CentrifugeTiersReproduced.CentrifugeTiers;
import com.Ultramega.CentrifugeTiersReproduced.CentrifugeTiersReproduced;
import com.Ultramega.CentrifugeTiersReproduced.config.CentrifugeTiersReproducedConfig;
import com.Ultramega.CentrifugeTiersReproduced.container.TieredCentrifugeContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TieredCentrifugeScreen extends AbstractContainerScreen<TieredCentrifugeContainer> {
    private final CentrifugeTiers tier;

    public TieredCentrifugeScreen(TieredCentrifugeContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);

        this.tier = getMenu().tileEntity.tier;
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, -5f, 6.0F, 4210752);

        int inventoryY = tier == CentrifugeTiers.COSMIC || tier == CentrifugeTiers.CREATIVE ? 78 : 96;
        this.font.draw(matrixStack, this.playerInventoryTitle, -5f, (float) (this.getYSize() - inventoryY + 2), 4210752);

        this.menu.tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            // Fluid level tooltip
            int fluidX = tier == CentrifugeTiers.CREATIVE ? 145 : 127;
            int fluidSizeY = tier == CentrifugeTiers.COSMIC || tier == CentrifugeTiers.CREATIVE ? 72 : 54;
            if (isHovering(fluidX, 16, 6, fluidSizeY, mouseX, mouseY)) {
                List<FormattedCharSequence> tooltipList = new ArrayList<>();

                if (fluidStack.getAmount() > 0) {
                    tooltipList.add(new TranslatableComponent("productivebees.screen.fluid_level", new TranslatableComponent(fluidStack.getTranslationKey()).getString(), fluidStack.getAmount() + "mB").getVisualOrderText());
                }
                else {
                    tooltipList.add(new TranslatableComponent("productivebees.hive.tooltip.empty").getVisualOrderText());
                }

                renderTooltip(matrixStack, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
            }
        });

        if(tier != CentrifugeTiers.CREATIVE) {
            this.menu.tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
                int energyAmount = handler.getEnergyStored();

                // Energy level tooltip
                if (isHovering(-5, 16, 6, tier == CentrifugeTiers.COSMIC ? 72 : 54, mouseX, mouseY)) {
                    List<FormattedCharSequence> tooltipList = new ArrayList<>();
                    tooltipList.add(new TranslatableComponent("productivebees.screen.energy_level", energyAmount + "FE").getVisualOrderText());

                    renderTooltip(matrixStack, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
                }
            });
        }
    }

    @Override
    protected void renderBg(@Nonnull PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.setShaderTexture(0, new ResourceLocation(CentrifugeTiersReproduced.MOD_ID, "textures/gui/" + getMenu().tileEntity.tier.getName() + "_centrifuge.png"));

        // Draw main screen
        int xOffset = tier == CentrifugeTiers.CREATIVE ? 0 : 26;
        int yOffset = tier == CentrifugeTiers.COSMIC || tier == CentrifugeTiers.CREATIVE ? 18 : 0;
        blit(matrixStack, this.getGuiLeft() - 13, this.getGuiTop(), 0, 0, this.getXSize() + xOffset, this.getYSize() + yOffset);

        // Draw progress
        int arrowX = tier == CentrifugeTiers.CREATIVE ? 176 : 202;
        int arrowY = tier == CentrifugeTiers.COSMIC ? 71 : 52;

        int progress = (int) (this.menu.tileEntity.recipeProgress[0] * (24 / (float) this.menu.tileEntity.getProcessingTime()));
        blit(matrixStack, this.getGuiLeft() + 35, this.getGuiTop() + 17, arrowX, arrowY, progress + 1, 16);

        int progress2 = (int) (this.menu.tileEntity.recipeProgress[1] * (24 / (float) this.menu.tileEntity.getProcessingTime()));
        blit(matrixStack, this.getGuiLeft() + 35, this.getGuiTop() + 53, arrowX, arrowY, progress2 + 1, 16);

        if(tier.getInputSlotAmount() > 2) {
            int progress3 = (int) (this.menu.tileEntity.recipeProgress[2] * (24 / (float) this.menu.tileEntity.getProcessingTime()));
            blit(matrixStack, this.getGuiLeft() + 35, this.getGuiTop() + 35, arrowX, arrowY, progress3 + 1, 16);

            if(tier.getInputSlotAmount() > 3) {
                int progress4 = (int) (this.menu.tileEntity.recipeProgress[3] * (24 / (float) this.menu.tileEntity.getProcessingTime()));
                blit(matrixStack, this.getGuiLeft() + 35, this.getGuiTop() + 71, arrowX, arrowY, progress4 + 1, 16);
            }
        }

        // Draw energy level
        int energySizeY = tier == CentrifugeTiers.COSMIC ? 70 : 52;
        blit(matrixStack, getGuiLeft() - 5, getGuiTop() + 17, 206, 0, 4, energySizeY);
        this.menu.tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
            int energyAmount = handler.getEnergyStored();
            double energyLevel = energyAmount * (energySizeY / (double) handler.getMaxEnergyStored());
            blit(matrixStack, getGuiLeft() - 5, getGuiTop() + 17, 8, 17, 4, (int)(energySizeY - energyLevel));
        });

        // Draw fluid tank
        this.menu.tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);

            if (fluidStack.getAmount() > 0) {
                int fluidLevel = (int) (fluidStack.getAmount() * (52 / (float)(CentrifugeTiersReproducedConfig.HIGH_END_CENTRIFUGE_FLUID_CAPACITY.get())));

                FluidContainerUtil.setColors(fluidStack);

                int fluidX = tier == CentrifugeTiers.CREATIVE ? 145 : 127;
                int fluidY = tier == CentrifugeTiers.COSMIC || tier == CentrifugeTiers.CREATIVE ? 87 : 69;
                FluidContainerUtil.drawTiledSprite(this.getGuiLeft() + fluidX, this.getGuiTop() + fluidY, 0, 4, fluidLevel, FluidContainerUtil.getSprite(fluidStack.getFluid().getAttributes().getStillTexture()), 16, 16, getBlitOffset());

                FluidContainerUtil.resetColor();
            }
        });
    }
}