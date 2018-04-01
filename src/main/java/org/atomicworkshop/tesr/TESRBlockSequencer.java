package org.atomicworkshop.tesr;

import org.atomicworkshop.sequencing.Pattern;
import org.atomicworkshop.sequencing.Sequencer;
import org.atomicworkshop.tiles.TileEntitySequencer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class TESRBlockSequencer extends TileEntitySpecialRenderer<TileEntitySequencer>
{
	public static Minecraft mc = Minecraft.getMinecraft();
	
	
	 
	    
	
	@Override
	public void render(TileEntitySequencer te, double x, double y, double z, float partialTicks,
			int destroyStage,float alpha) {
		if (te == null) return;

		 	RenderItem itemRenderer = mc.getRenderItem();
			int facing = te.getBlockMetadata();
			int bpm = 120;
			//f == orientation 
			//render buttons
			//render cards
			//render BPM
			float f1 = 0.6666667F;
        	float textScale = 0.015625F * f1;
        	GlStateManager.pushMatrix();
        	
        	FontRenderer fontrenderer = this.getFontRenderer();
        	GlStateManager.translate(x, y, z);
        	GlStateManager.rotate(270.0f+90.0f*(4-facing),0.0f,1.0f,0.0f);
        	
        	GlStateManager.translate(0.8, 0.5, 0.8);
        	GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
        	GlStateManager.rotate(-68.0f, 1.0f,0.0f,0.0f);
        	GlStateManager.translate(0.7, -0.2, -0.45);
        	if (facing==0)
        	{
        		GlStateManager.translate(0.9, -0.1, 0.23);
        	}
        	if (facing==1)
        	{
        		GlStateManager.translate(0.9,0.8,-0.25);
        	}
        	if (facing==2)
        	{
        		GlStateManager.translate(-0.07, 0.8, -0.32);
        	}
        	if (facing==3)
        	{
        		GlStateManager.translate(-0.07, -0.05, 0.18);
        	}

            if (te.sequencer != null)
            {
            	//not current interval rows.
		        EntityItem disabledItemInactiveInterval = new EntityItem(getWorld(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.CONCRETE, 1, 8));
		        EntityItem enabledItemInactiveInterval = new EntityItem(getWorld(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.CONCRETE, 1, 12));
	            EntityItem disabledItemActiveInterval = new EntityItem(getWorld(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.CONCRETE, 1, 0));
	            EntityItem enabledItemActiveInterval = new EntityItem(getWorld(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.CONCRETE, 1, 5));

                bpm = te.sequencer.getBeatsPerMinute();
                Pattern p = te.sequencer.getCurrentPattern();

                GlStateManager.pushMatrix();
                GlStateManager.translate(-0.74,0.082,0.06);
		        for (int loop2=0;loop2<16;loop2++)
		        {
			        boolean[] rawPatternData = p.getRawPatternData(loop2);

			        final EntityItem disabledItem;
			        final EntityItem enabledItem;

			        if (te.sequencer.getCurrentInterval() == loop2)
			        {
				        disabledItem = disabledItemActiveInterval;
				        enabledItem = enabledItemActiveInterval;

			        } else {
						disabledItem = disabledItemInactiveInterval;
				        enabledItem = enabledItemInactiveInterval;
			        }

                    //outer loop. reset after every outer loop.
                    for (int loop1=0;loop1<24;loop1++)
			        {
				        GlStateManager.pushMatrix();
				        GlStateManager.disableLighting();

				        GlStateManager.translate((loop2 * 0.9f) /24f, (loop1 * 0.6f) / 16, 0);

                        GlStateManager.rotate(180f,1.0f,0.0f,0.0f);
                        GlStateManager.rotate(180f,0.0f,0.0f,1.0f);
                        GlStateManager.scale(0.05F, 0.05F, 0.05F);
                        GlStateManager.depthMask(true);
                        GlStateManager.pushAttrib();

				        boolean isEnabled = rawPatternData[loop1];
				        if (isEnabled) {
                                itemRenderer.renderItem(enabledItem.getItem(), ItemCameraTransforms.TransformType.FIXED);
                            } else {
					        itemRenderer.renderItem(disabledItem.getItem(), ItemCameraTransforms.TransformType.FIXED);
				        }
                        GlStateManager.popAttrib();

                        GlStateManager.enableLighting();
                        GlStateManager.popMatrix();
                    }
                }
                GlStateManager.popMatrix();
            }
            GlStateManager.translate(0.0, 0.935, 0.11);
        	String s = ""+bpm;
        	//System.out.println(f);

            GlStateManager.scale(textScale, -textScale, textScale);
            GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F * textScale);
            GlStateManager.depthMask(false);	
        	
        	fontrenderer.drawString(s, 0-fontrenderer.getStringWidth(s) / 2, 0, 0xFFFFFF);
        	
            GlStateManager.popMatrix();

			//render 
		
	}
	
	
	
	
}
