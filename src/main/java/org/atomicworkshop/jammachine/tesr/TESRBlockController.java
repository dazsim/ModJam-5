package org.atomicworkshop.jammachine.tesr;

import org.atomicworkshop.jammachine.libraries.ItemLibrary;
import org.atomicworkshop.jammachine.sequencing.ControllerPattern;
import org.atomicworkshop.jammachine.sequencing.JamController;
import org.atomicworkshop.jammachine.sequencing.Pattern;
import org.atomicworkshop.jammachine.sequencing.Sequencer;
import org.atomicworkshop.jammachine.tiles.TileEntityController;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

@SuppressWarnings("OverlyComplexMethod")
public class TESRBlockController extends TileEntitySpecialRenderer<TileEntityController>
{
	private static final Minecraft mc = Minecraft.getMinecraft();

	private final ItemStack disabledItemInactiveInterval = new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.SILVER.getMetadata());
	private final ItemStack enabledItemInactiveInterval = new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.BROWN.getMetadata());
	private final ItemStack disabledItemActiveInterval = new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.WHITE.getMetadata());
	private final ItemStack enabledItemActiveInterval = new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.YELLOW.getMetadata());

	private final ItemStack disabledItemInactiveIntervalSharp = new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.BLACK.getMetadata());
	private final ItemStack enabledItemInactiveIntervalSharp = new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.BROWN.getMetadata());
	private final ItemStack disabledItemActiveIntervalSharp = new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.GRAY.getMetadata());
	private final ItemStack enabledItemActiveIntervalSharp = new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.ORANGE.getMetadata());

	private final ItemStack punchCard = new ItemStack(ItemLibrary.punchCardBlank,1,0);

	private final float disabledButtonHeight = 0.015f;

	@Override
	public void render(
			TileEntityController te,
			double x, double y, double z,
			float partialTicks,
			int destroyStage,
			float alpha) {

		if (te == null) return;
		final JamController controller = te.getController();
		if (controller == null) return;

		final int facing = te.getBlockMetadata();

		GlStateManager.pushMatrix();
		{
			GlStateManager.translate(x, y, z);
			//Adjust Origin for rotations.
			GlStateManager.translate(0.5, 0.5, 0.5);
			//Rotate according to facing
			GlStateManager.rotate(-90 * (facing), 0.0f, 1.0f, 0.0f);
			//Tilt plane
			//TODO: Calculate angle accurately rather than eyeballing it.
			GlStateManager.rotate(23, 1.0f, 0, 0);
			//Return origin
			GlStateManager.translate(-0.5, -0.2, -0.5);
			//Scale the UI so that it is made up of roughly 28 squares.
			final float scale = 1 / 28.0f;
			GlStateManager.scale(scale, scale, scale);
			//Three squares of space along the top/left hand side, giving roughly 7 blocks of space on the right hand side.
			GlStateManager.translate(3, 0, 3.5);

			drawBPM(controller.getBeatsPerMinute());

			GlStateManager.pushAttrib();
			GlStateManager.disableLighting();
			GlStateManager.depthMask(true);

			int currentSequencerInterval = controller.getSequenceInterval();
			int displayedSection = te.getDisplayedSection();

			for (int i = 0; i < 2; ++i) {
				renderControllerPattern(te.getSelectedSequence(i), i, currentSequencerInterval, displayedSection);
			}

			//renderPatternButtons(controller);
			renderBPMButtons(controller);
			if (te.hasCard())
			{
				renderCard();
			}

			GlStateManager.enableLighting();
			GlStateManager.popAttrib();
		}
        GlStateManager.popMatrix();
	}

	private void renderBPMButtons(JamController controller)
	{
		//0.7045204265288701,0.21970650094097977
		//0.9327918869342732,0.2705630830397361
		final RenderItem itemRenderer = mc.getRenderItem();
		final int bpm = controller.getBeatsPerMinute();
		
		final FontRenderer fontrenderer = getFontRenderer();
		final float textScale = 0.05f;

		
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(17.8, 0, 5.3);
		GlStateManager.scale(2.7, 2.0, 2.7);
		itemRenderer.renderItem(enabledItemInactiveInterval, TransformType.FIXED);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0.3, -0.2);
		GlStateManager.rotate(-90, 1, 0, 0);
		GlStateManager.scale(textScale, -textScale, textScale);
		final String minus10 = "<<";
		fontrenderer.drawString(minus10, -fontrenderer.getStringWidth(minus10) / 2, 0, 0xFFFFFF);
		GlStateManager.popMatrix();
		
		GlStateManager.translate(0.6, 0, 0);
		itemRenderer.renderItem(enabledItemInactiveInterval, TransformType.FIXED);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0.3, -0.2);
		GlStateManager.rotate(-90, 1, 0, 0);
		GlStateManager.scale(textScale, -textScale, textScale);
		final String minus1 = "<";
		fontrenderer.drawString(minus1, -fontrenderer.getStringWidth(minus1) / 2, 0, 0xFFFFFF);
		GlStateManager.popMatrix();
		
		GlStateManager.translate(0.6, 0, 0);
		itemRenderer.renderItem(enabledItemInactiveInterval, TransformType.FIXED);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0.3, -0.2);
		GlStateManager.rotate(-90, 1, 0, 0);
		GlStateManager.scale(textScale, -textScale, textScale);
		final String plus1 = ">";
		fontrenderer.drawString(plus1, -fontrenderer.getStringWidth(plus1) / 2, 0, 0xFFFFFF);
		GlStateManager.popMatrix();
		
		GlStateManager.translate(0.6, 0, 0);
		itemRenderer.renderItem(enabledItemInactiveInterval, TransformType.FIXED);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0.3, -0.2);
		GlStateManager.rotate(-90, 1, 0, 0);
		GlStateManager.scale(textScale, -textScale, textScale);
		final String plus10 = ">>";
		fontrenderer.drawString(plus10, -fontrenderer.getStringWidth(plus10) / 2, 0, 0xFFFFFF);
		GlStateManager.popMatrix();
		
		GlStateManager.popMatrix();
	}

	private void renderControllerPattern(ControllerPattern cp, int index, int currentInterval, int displayedInterval)
	{
		final RenderItem itemRenderer = mc.getRenderItem();

		GlStateManager.pushMatrix(); // Matrix for pattern data

		//index represents top half or bottom half of the controller.
		GlStateManager.translate(0, 0, 2 + index * 14);

		if (cp != null) {
			Sequencer sequencer = cp.getSequencer();
			if (sequencer != null) {
				String name = sequencer.getName();
				if (name != null) {
					//TODO: Render Name
					//TODO: Render "Select Sequencer" buttons.
				}
			}
		}

		for (int sequenceInterval=0;sequenceInterval<16;sequenceInterval++)
		{
			int patternIndex = -1;
			if (cp != null) {
				patternIndex = cp.getPatternAtInterval(displayedInterval + sequenceInterval);
			}

	        for (int pattern=0;pattern<8;pattern++)
			{
				final boolean isEnabled = pattern == patternIndex;

				final ItemStack renderItem = getSequenceButtonColour(currentInterval, displayedInterval + sequenceInterval, pattern, isEnabled);

				GlStateManager.pushMatrix(); // Matrix for an individual button on the pattern sequence data

				GlStateManager.translate(
						sequenceInterval,
						isEnabled ? 0.0f : disabledButtonHeight,
						(8 - pattern)
				);

				itemRenderer.renderItem(renderItem, TransformType.FIXED);

	            GlStateManager.popMatrix(); // Matrix for an individual button on the pattern sequence data
	        }
	    }

		GlStateManager.popMatrix(); // Matrix for pattern data
	}

	private ItemStack getSequenceButtonColour(int currentInterval, int interval, int pattern, boolean isEnabled)
	{
		if (pattern == 0 || pattern == 4) {
			if (currentInterval == interval)
			{
				return isEnabled ? enabledItemActiveIntervalSharp : disabledItemActiveIntervalSharp;
			} else {
				return isEnabled ? enabledItemInactiveIntervalSharp : disabledItemInactiveIntervalSharp;
			}
		} else {
			if (currentInterval == interval) {
				return isEnabled ? enabledItemActiveInterval : disabledItemActiveInterval;
			} else {
				return isEnabled ? enabledItemInactiveInterval : disabledItemInactiveInterval;
			}
		}
	}

	private void renderCard() {
		final RenderItem itemRenderer = mc.getRenderItem();
		final float cardScale = 5.0f;

		GlStateManager.pushMatrix();
		GlStateManager.translate(20.25, 0, 25);
		GlStateManager.scale(cardScale, cardScale, cardScale);

		itemRenderer.renderItem(punchCard, TransformType.FIXED);

		GlStateManager.popMatrix();
	}

	private void drawBPM(int bpm) {
		final FontRenderer fontrenderer = getFontRenderer();
		final float textScale = 0.30f;

		GlStateManager.pushMatrix();
		GlStateManager.translate(20, 0.09, 1.5);
		GlStateManager.rotate(-90, 1, 0, 0);
		GlStateManager.scale(textScale, -textScale, textScale);

		final String bpmText = String.valueOf(bpm);
		fontrenderer.drawString(bpmText, -fontrenderer.getStringWidth(bpmText) / 2, 0, 0xFFFFFF);

		GlStateManager.popMatrix();
	}
}
