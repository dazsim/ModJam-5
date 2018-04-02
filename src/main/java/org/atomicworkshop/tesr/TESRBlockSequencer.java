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
		//f == orientation
		//render buttons
		//render cards
		//render BPM
		GlStateManager.pushMatrix();

        final FontRenderer fontrenderer = getFontRenderer();
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

		//GlStateManager.translate(-0.7, 0, -0.5);
		//

//        GlStateManager.translate(0.8, 0.5, 0.8);
//        GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
//        GlStateManager.rotate(-68.0f, 1.0f,0.0f,0.0f);
//        GlStateManager.translate(0.7, -0.2, -0.45);
//        if (facing==0)
//        {
//            GlStateManager.translate(0.9, -0.1, 0.23);
//        }
//        if (facing==1)
//        {
//            GlStateManager.translate(0.9,0.8,-0.25);
//        }
//        if (facing==2)
//        {
//            GlStateManager.translate(-0.07, 0.8, -0.32);
//        }
//        if (facing==3)
//        {
//            GlStateManager.translate(-0.07, -0.05, 0.18);
//        }

		int bpm = 120;
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

		bpm = sequencer.getBeatsPerMinute();
		final Pattern p = sequencer.getCurrentPattern();


		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();

		/*GlStateManager.pushMatrix();
		GlStateManager.scale(0.05F, 0.05F, 0.05F);
		itemRenderer.renderItem(enabledItemActiveInterval.getItem(), TransformType.FIXED);
		GlStateManager.popMatrix();*/

		GlStateManager.pushMatrix();
		GlStateManager.scale(1/32.0f, 1/32.0f, 1/32.0f);
		GlStateManager.translate(3, 0, 3);
		GlStateManager.pushMatrix();



		//GlStateManager.translate(-0.74,0.082,0.06);
		for (int interval=0;interval<16;interval++)
		{
			final boolean[] rawPatternData = p.getRawPatternData(interval);

			final int currentInterval = sequencer.getCurrentInterval();

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

				GlStateManager.pushMatrix();

				GlStateManager.translate(
						interval,
						isEnabled ? -0.0f : 0.015f,
						(25 - pitch)
				);

	            //GlStateManager.rotate(180.0f,1.0f,0.0f,0.0f);
	            //GlStateManager.rotate(180.0f,0.0f,0.0f,1.0f);
				//GlStateManager.scale(0.05F, 0.05F, 0.05F);
	            GlStateManager.depthMask(true);


				if (isEnabled) {
				    itemRenderer.renderItem(enabledItem.getItem(), TransformType.FIXED);
				} else {
					itemRenderer.renderItem(disabledItem.getItem(), TransformType.FIXED);
				}

	            GlStateManager.popMatrix();
	        }
	    }
		GlStateManager.popMatrix();

		//Render Pattern buttons
		final int currentPatternIndex = sequencer.getCurrentPatternIndex();
		final int pendingPatternIndex = sequencer.getPendingPatternIndex();

		GlStateManager.pushMatrix();
		//GlStateManager.translate(-0.05,0.4,0.06);
		for (int patternIndex = 0; patternIndex < 8; patternIndex++)
		{
			final int patternButtonX = patternIndex & 3;
			final int patternButtonY = 1 - (patternIndex & 4) >> 2;

			final boolean isEnabled = patternIndex == pendingPatternIndex;
			final boolean isCurrent = patternIndex == currentPatternIndex;

			GlStateManager.pushMatrix();

			GlStateManager.translate(18 + patternButtonX, 12 + patternButtonY, isEnabled ? -0.0f : 0.015f);

			//GlStateManager.rotate(180.0f,1.0f,0.0f,0.0f);
			//GlStateManager.rotate(180.0f,0.0f,0.0f,1.0f);
			GlStateManager.scale(0.05F, 0.05F, 0.05F);
			GlStateManager.depthMask(true);

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
		GlStateManager.popMatrix();

		if (te.getHasCard())
		{
			GlStateManager.pushMatrix();

			//GlStateManager.translate(0.02f,.1f,0.0);
			GlStateManager.scale(0.175, 0.175, 0.175);
			//GlStateManager.rotate(90.0f,1.0f,0.0f,0.0f);
			itemRenderer.renderItem(punchCard.getItem(), TransformType.FIXED);

			GlStateManager.popMatrix();
		}

		GlStateManager.enableLighting();
		GlStateManager.popAttrib();

		//GlStateManager.translate(0.0, 0.935, 0.11);
        final String s = String.valueOf(bpm);
        //System.out.println(f);

		final float f1 = 0.6666667F;
		final float textScale = 0.015625F * f1;
		GlStateManager.scale(textScale, -textScale, textScale);
        GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F * textScale);
        GlStateManager.depthMask(false);

        fontrenderer.drawString(s, 0-fontrenderer.getStringWidth(s) / 2, 0, 0xFFFFFF);

        GlStateManager.popMatrix();

		//render
		
	}

	@SuppressWarnings("OverlyComplexBooleanExpression")
	private static boolean isSharpPitch(int pitch)
	{
		return pitch == 0 || pitch == 2 || pitch == 4 || pitch == 7 || pitch == 9 || pitch == 12 ||
					pitch == 14 || pitch == 16 || pitch == 19 || pitch == 21 || pitch == 24;
	}
}
