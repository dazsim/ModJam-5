package org.atomicworkshop.jammachine.ter;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import org.atomicworkshop.jammachine.tiles.SequencerTileEntity;
import org.atomicworkshop.jammachine.sequencing.Pattern;
import org.atomicworkshop.jammachine.sequencing.Sequencer;
import org.atomicworkshop.jammachine.libraries.ItemLibrary;

import static net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.*;

@SuppressWarnings("OverlyComplexMethod")
public class SequencerTER extends TileEntityRenderer<SequencerTileEntity>
{
    private static final Minecraft mc = Minecraft.getInstance();

    private final ItemStack disabledItemInactiveInterval = new ItemStack(Blocks.LIGHT_GRAY_CONCRETE, 1);
    private final ItemStack enabledItemInactiveInterval = new ItemStack(Blocks.BROWN_CONCRETE, 1);
    private final ItemStack disabledItemActiveInterval = new ItemStack(Blocks.WHITE_CONCRETE, 1);
    private final ItemStack enabledItemActiveInterval = new ItemStack(Blocks.YELLOW_CONCRETE, 1);

    private final ItemStack disabledItemInactiveIntervalSharp = new ItemStack(Blocks.BLACK_CONCRETE, 1);
    private final ItemStack enabledItemInactiveIntervalSharp = new ItemStack(Blocks.BROWN_CONCRETE, 1);
    private final ItemStack disabledItemActiveIntervalSharp = new ItemStack(Blocks.GRAY_CONCRETE, 1);
    private final ItemStack enabledItemActiveIntervalSharp = new ItemStack(Blocks.ORANGE_CONCRETE, 1);

    private final ItemStack punchCard = new ItemStack(ItemLibrary.punchCardBlank,1);

    private final float disabledButtonHeight = 0.015f;

    @Override
    public void render(
            SequencerTileEntity te,
            double x, double y, double z,
            float partialTicks,
            int destroyStage) {

        if (te == null) return;
        final Sequencer sequencer = te.getSequencer();
        if (sequencer == null) return;

        //FIXME: This is a bit unneccessary.
        final float facing = te.getBlockState().get(HorizontalBlock.HORIZONTAL_FACING).getHorizontalAngle();

        GlStateManager.pushMatrix();
        {
            GlStateManager.translated(x, y, z);
            //Adjust Origin for rotations.
            GlStateManager.translated(0.5, 0.5, 0.5);
            //Rotate according to facing
            GlStateManager.rotatef(-facing, 0.0f, 1.0f, 0.0f);
            //Tilt plane
            //TODO: Calculate angle accurately rather than eyeballing it.
            GlStateManager.rotatef(23, 1.0f, 0, 0);
            //Return origin
            GlStateManager.translated(-0.5, -0.2, -0.5);
            //Scale the UI so that it is made up of roughly 28 squares.
            final float scale = 1 / 28.0f;
            GlStateManager.scalef(scale, scale, scale);
            //Three squares of space along the top/left hand side, giving roughly 7 blocks of space on the right hand side.
            GlStateManager.translated(3, 0, 3.5);

            drawBPM(sequencer.getBeatsPerMinute());

            GlStateManager.pushLightingAttributes();
            GlStateManager.disableLighting();
            GlStateManager.depthMask(true);

            renderSequence(sequencer);
            renderPatternButtons(sequencer);
            renderBPMButtons(sequencer);
            if (te.getHasCard())
            {
                renderCard();
            }

            GlStateManager.enableLighting();
            GlStateManager.popAttributes();
        }
        GlStateManager.popMatrix();
    }

    private void renderPatternButtons(Sequencer sequencer)
    {
        final ItemRenderer itemRenderer = mc.getItemRenderer();
        final int currentPatternIndex = sequencer.getCurrentPatternIndex();
        final int pendingPatternIndex = sequencer.getPendingPatternIndex();

        GlStateManager.pushMatrix();
        {
            GlStateManager.translated(17.5, 0, 12);
            GlStateManager.scaled(2, 2, 2);
            for (int patternIndex = 0; patternIndex < 8; patternIndex++)
            {
                final int patternButtonX = patternIndex & 3;
                final int patternButtonY = (patternIndex & 4) >> 2;

                final boolean isEnabled = patternIndex == pendingPatternIndex;
                final boolean isCurrent = patternIndex == currentPatternIndex;

                GlStateManager.pushMatrix();
                {
                    GlStateManager.translatef(patternButtonX, isEnabled ? 0.0f : disabledButtonHeight, patternButtonY);

                    if (isCurrent)
                    {
                        itemRenderer.renderItem(enabledItemActiveInterval, TransformType.FIXED);
                    } else if (isEnabled)
                    {
                        itemRenderer.renderItem(enabledItemInactiveInterval, TransformType.FIXED);
                    } else
                    {
                        itemRenderer.renderItem(disabledItemInactiveInterval, TransformType.FIXED);
                    }
                }
                GlStateManager.popMatrix();
            }
        }

        //Render Run Button
        GlStateManager.pushMatrix();
        {

            ItemStack itemToRender = sequencer.isProgramming() ? enabledItemActiveInterval : disabledItemInactiveInterval;

            GlStateManager.scaled(0.5, 0.5, 0.5);
            GlStateManager.translated(3, 0, -2.5);
            GlStateManager.scaled(14, 2, 4);
            itemRenderer.renderItem(itemToRender, TransformType.FIXED);
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        {
            final FontRenderer fontrenderer = getFontRenderer();
            final float textScale = 0.05f;
            final String run = "Prog " + sequencer.getProgramLength();

            GlStateManager.translated(1.5, 0.5, -1.3);
            GlStateManager.rotated(-90, 1, 0, 0);
            GlStateManager.scaled(textScale, -textScale, textScale);
            fontrenderer.drawString(run, -fontrenderer.getStringWidth(run) / 2.0f, 0, 0xFFFFFF);
        }
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }

    private void renderBPMButtons(Sequencer sequencer)
    {
        //0.7045204265288701,0.21970650094097977
        //0.9327918869342732,0.2705630830397361
        final ItemRenderer itemRenderer = mc.getItemRenderer();
        final int bpm = sequencer.getBeatsPerMinute();

        final FontRenderer fontrenderer = getFontRenderer();
        final float textScale = 0.05f;

        GlStateManager.pushMatrix();
        GlStateManager.translated(17.8, 0, 5.3);
        GlStateManager.scaled(2.7, 2.0, 2.7);
        itemRenderer.renderItem(enabledItemInactiveInterval, TransformType.FIXED);

        GlStateManager.pushMatrix();
        GlStateManager.translated(0, 0.3, -0.2);
        GlStateManager.rotated(-90, 1, 0, 0);
        GlStateManager.scaled(textScale, -textScale, textScale);
        final String minus10 = "<<";
        fontrenderer.drawString(minus10, -fontrenderer.getStringWidth(minus10) / 2, 0, 0xFFFFFF);
        GlStateManager.popMatrix();

        GlStateManager.translated(0.6, 0, 0);
        itemRenderer.renderItem(enabledItemInactiveInterval, TransformType.FIXED);

        GlStateManager.pushMatrix();
        GlStateManager.translated(0, 0.3, -0.2);
        GlStateManager.rotated(-90, 1, 0, 0);
        GlStateManager.scaled(textScale, -textScale, textScale);
        final String minus1 = "<";
        fontrenderer.drawString(minus1, -fontrenderer.getStringWidth(minus1) / 2, 0, 0xFFFFFF);
        GlStateManager.popMatrix();

        GlStateManager.translated(0.6, 0, 0);
        itemRenderer.renderItem(enabledItemInactiveInterval, TransformType.FIXED);

        GlStateManager.pushMatrix();
        GlStateManager.translated(0, 0.3, -0.2);
        GlStateManager.rotated(-90, 1, 0, 0);
        GlStateManager.scaled(textScale, -textScale, textScale);
        final String plus1 = ">";
        fontrenderer.drawString(plus1, -fontrenderer.getStringWidth(plus1) / 2, 0, 0xFFFFFF);
        GlStateManager.popMatrix();

        GlStateManager.translated(0.6, 0, 0);
        itemRenderer.renderItem(enabledItemInactiveInterval, FIXED);

        GlStateManager.pushMatrix();
        GlStateManager.translated(0, 0.3, -0.2);
        GlStateManager.rotated(-90, 1, 0, 0);
        GlStateManager.scaled(textScale, -textScale, textScale);
        final String plus10 = ">>";
        fontrenderer.drawString(plus10, -fontrenderer.getStringWidth(plus10) / 2, 0, 0xFFFFFF);
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }

    private void renderSequence(Sequencer sequencer)
    {
        final ItemRenderer itemRenderer = mc.getItemRenderer();
        final Pattern p = sequencer.getCurrentPattern();


        GlStateManager.pushMatrix(); // Matrix for pattern data

        for (int interval=0;interval<16;interval++)
        {
            final boolean[] rawPatternData = p.getRawPatternData(interval);

            //outer loop. reset after every outer loop.
            for (int pitch=0;pitch<25;pitch++)
            {
                final boolean isEnabled = rawPatternData[pitch];

                final ItemStack renderItem = getSequenceButtonColour(sequencer, interval, pitch, isEnabled);

                GlStateManager.pushMatrix(); // Matrix for an individual button on the pattern sequence data

                GlStateManager.translated(
                        interval,
                        isEnabled ? 0.0f : disabledButtonHeight,
                        (25 - pitch)
                );

                itemRenderer.renderItem(renderItem, TransformType.FIXED);

                GlStateManager.popMatrix(); // Matrix for an individual button on the pattern sequence data
            }
        }

        GlStateManager.popMatrix(); // Matrix for pattern data
    }

    private ItemStack getSequenceButtonColour(Sequencer sequencer, int interval, int pitch, boolean isEnabled)
    {
        final int currentInterval = sequencer.getCurrentInterval();

        if (isSharpPitch(pitch)) {
            //Use sharp colours.
            if (currentInterval == interval)
            {
                return isEnabled ? enabledItemActiveIntervalSharp : disabledItemActiveIntervalSharp;
            } else {
                return isEnabled ? enabledItemInactiveIntervalSharp : disabledItemInactiveIntervalSharp;
            }
        } else {
            //Use non-sharp colours.
            if (currentInterval == interval) {
                return isEnabled ? enabledItemActiveInterval : disabledItemActiveInterval;
            } else {
                return isEnabled ? enabledItemInactiveInterval : disabledItemInactiveInterval;
            }
        }
    }

    private void renderCard() {
        final ItemRenderer itemRenderer = mc.getItemRenderer();
        final float cardScale = 5.0f;

        GlStateManager.pushMatrix();
        GlStateManager.translated(20.25, 0, 25);
        GlStateManager.scaled(cardScale, cardScale, cardScale);

        itemRenderer.renderItem(punchCard, FIXED);

        GlStateManager.popMatrix();
    }

    private void drawBPM(int bpm) {
        final FontRenderer fontrenderer = getFontRenderer();
        final float textScale = 0.30f;

        GlStateManager.pushMatrix();
        GlStateManager.translated(20, 0.09, 1.5);
        GlStateManager.rotated(-90, 1, 0, 0);
        GlStateManager.scaled(textScale, -textScale, textScale);

        final String bpmText = String.valueOf(bpm);
        fontrenderer.drawString(bpmText, -fontrenderer.getStringWidth(bpmText) / 2, 0, 0xFFFFFF);

        GlStateManager.popMatrix();
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    private static boolean isSharpPitch(int pitch)
    {
        return pitch == 0 || pitch == 2 || pitch == 4 || pitch == 7 || pitch == 9 || pitch == 12 ||
                pitch == 14 || pitch == 16 || pitch == 19 || pitch == 21 || pitch == 24;
    }
}