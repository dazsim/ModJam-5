package org.atomicworkshop.tesr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import org.atomicworkshop.libraries.ItemLibrary;
import org.atomicworkshop.sequencing.Pattern;
import org.atomicworkshop.sequencing.Sequencer;
import org.atomicworkshop.tiles.TileEntitySequencer;

@SuppressWarnings("OverlyComplexMethod")
public class TESRBlockSequencer extends TileEntitySpecialRenderer<TileEntitySequencer>
{
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	@Override
	public void render(
			TileEntitySequencer te,
			double x, double y, double z,
			float partialTicks,
			int destroyStage,
			float alpha) {

		if (te == null) return;
		final RenderItem itemRenderer = mc.getRenderItem();
		final int facing = te.getBlockMetadata();
		//render buttons
		//render cards
		//render BPM
		GlStateManager.pushMatrix();


        GlStateManager.translate(x, y, z);
        //Adjust Origin for rotations.
        GlStateManager.translate(0.5, 0.5, 0.5);
		//Rotate according to facing
        GlStateManager.rotate(-90*(facing) ,0.0f,1.0f,0.0f);
        //Tilt plane
		//TODO: Calculate angle accurately rather than eyeballing it.
        GlStateManager.rotate(23, 1.0f, 0, 0);
        //Return origin
		GlStateManager.translate(-0.5, -0.2, -0.5);

		final Sequencer sequencer = te.sequencer;
		if (sequencer == null) return;



		//TODO: Replace EntityItem with ItemStack?
		//not current interval rows.
		final EntityItem disabledItemInactiveInterval = new EntityItem(getWorld(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.SILVER.getMetadata()));
		final EntityItem enabledItemInactiveInterval = new EntityItem(getWorld(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.BROWN.getMetadata()));
		final EntityItem disabledItemActiveInterval = new EntityItem(getWorld(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.WHITE.getMetadata()));
		final EntityItem enabledItemActiveInterval = new EntityItem(getWorld(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.YELLOW.getMetadata()));

		final EntityItem disabledItemInactiveIntervalSharp = new EntityItem(getWorld(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.BLACK.getMetadata()));
		final EntityItem enabledItemInactiveIntervalSharp = new EntityItem(getWorld(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.BROWN.getMetadata()));
		final EntityItem disabledItemActiveIntervalSharp = new EntityItem(getWorld(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.GRAY.getMetadata()));
		final EntityItem enabledItemActiveIntervalSharp = new EntityItem(getWorld(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.CONCRETE, 1, EnumDyeColor.ORANGE.getMetadata()));
		EntityItem punchCard = new EntityItem(getWorld(),0.0D,0.0D,0.0D,new ItemStack(ItemLibrary.punchCardBlank,1,0));


		final Pattern p = sequencer.getCurrentPattern();
		final int currentInterval = sequencer.getCurrentInterval();



		//GlStateManager.pushMatrix();
		float scale = 1/28.0f;
		//Scale the UI so that it is made up of roughly 32 squares.
		GlStateManager.scale(scale, scale, scale);
		//Three squares of space along the top/left hand side, giving roughly 7 blocks of space on the right hand side.
		GlStateManager.translate(3, 0, 3.5);

		drawBPM(sequencer.getBeatsPerMinute());

		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();
		GlStateManager.depthMask(true);

		GlStateManager.pushMatrix(); // Matrix for pattern data
		for (int interval=0;interval<16;interval++)
		{
			final boolean[] rawPatternData = p.getRawPatternData(interval);


	        //outer loop. reset after every outer loop.
	        for (int pitch=0;pitch<24;pitch++)
			{
				final boolean isEnabled = rawPatternData[pitch];

				final EntityItem disabledItem;
				final EntityItem enabledItem;

			    if (isSharpPitch(pitch)) {
			        //Use sharp colours.
					if (currentInterval == interval)
					{
						disabledItem = disabledItemActiveIntervalSharp;
						enabledItem = enabledItemActiveIntervalSharp;
					} else {
						disabledItem = disabledItemInactiveIntervalSharp;
						enabledItem = enabledItemInactiveIntervalSharp;
					}
				} else {
			        //Use non-sharp colours.
					if (currentInterval == interval)
					{
						disabledItem = disabledItemActiveInterval;
						enabledItem = enabledItemActiveInterval;

					} else {
						disabledItem = disabledItemInactiveInterval;
						enabledItem = enabledItemInactiveInterval;
					}
				}

				GlStateManager.pushMatrix(); // Matrix for an individual button on the pattern sequence data

				GlStateManager.translate(
						interval,
						isEnabled ? -0.0f : 0.015f,
						(25 - pitch)
				);

				if (isEnabled) {
				    itemRenderer.renderItem(enabledItem.getItem(), TransformType.FIXED);
				} else {
					itemRenderer.renderItem(disabledItem.getItem(), TransformType.FIXED);
				}

	            GlStateManager.popMatrix(); // Matrix for an individual button on the pattern sequence data
	        }
	    }
		GlStateManager.popMatrix(); // Matrix for pattern data

		//Render Pattern buttons
		final int currentPatternIndex = sequencer.getCurrentPatternIndex();
		final int pendingPatternIndex = sequencer.getPendingPatternIndex();

		GlStateManager.pushMatrix();
		GlStateManager.translate(18.5, 0, 12);
		for (int patternIndex = 0; patternIndex < 8; patternIndex++)
		{
			final int patternButtonX = patternIndex & 3;
			final int patternButtonY = (patternIndex & 4) >> 2;

			final boolean isEnabled = patternIndex == pendingPatternIndex;
			final boolean isCurrent = patternIndex == currentPatternIndex;

			GlStateManager.pushMatrix();

			GlStateManager.translate(patternButtonX, isEnabled ? -0.0f : 0.015f, patternButtonY);

			if (isCurrent) {
				itemRenderer.renderItem(enabledItemActiveInterval.getItem(), TransformType.FIXED);
			} else if (isEnabled) {
				itemRenderer.renderItem(enabledItemInactiveInterval.getItem(), TransformType.FIXED);
		    } else {
				itemRenderer.renderItem(disabledItemInactiveInterval.getItem(), TransformType.FIXED);
			}

			GlStateManager.popMatrix();

		}
		GlStateManager.popMatrix();

		if (te.getHasCard())
		{
			renderCard(itemRenderer, punchCard);
		}

		GlStateManager.enableLighting();
		GlStateManager.popAttrib();

        GlStateManager.popMatrix();

		//render
		
	}

	private void renderCard(RenderItem itemRenderer, EntityItem punchCard) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(20.25, 0, 25);
		float cardScale = 5f;
		GlStateManager.scale(cardScale, cardScale, cardScale);
		itemRenderer.renderItem(punchCard.getItem(), TransformType.FIXED);

		GlStateManager.popMatrix();
	}

	private void drawBPM(int bpm) {
		final FontRenderer fontrenderer = getFontRenderer();
		final float textScale = 0.30f;//0.015625F * f1;
		final String bpmText = String.valueOf(bpm);

		GlStateManager.pushMatrix();
		GlStateManager.translate(20, 0.09, 1.5);
		GlStateManager.rotate(-90, 1, 0, 0);
		GlStateManager.scale(textScale, -textScale, textScale);

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
