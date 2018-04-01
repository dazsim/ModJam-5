package org.atomicworkshop.tesr;

import org.atomicworkshop.sequencing.Sequencer;
import org.atomicworkshop.tiles.TileEntitySequencer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class TESRBlockSequencer extends TileEntitySpecialRenderer<TileEntitySequencer>
{
	public static Minecraft mc = Minecraft.getMinecraft();
	
	
	 
	    
	
	@Override
	public void render(TileEntitySequencer te, double x, double y, double z, float partialTicks,
			int destroyStage,float alpha) {
			
			int f = te.getBlockMetadata();
			int bpm = 120;
			//f == orientation 
			//render buttons
			//render cards
			//render BPM
			float f1 = 0.6666667F;
        	float f3 = 0.015625F * f1;
        	GlStateManager.pushMatrix();
        	FontRenderer fontrenderer = this.getFontRenderer();
        	GlStateManager.translate(x, y, z);
        	GlStateManager.translate(0.8, 0.5, 0.8);
        	GlStateManager.rotate(0.0f, 0.5f, 0.0f, 1.0f);
        	if (te instanceof TileEntitySequencer && te !=null)
        	{
        		
        		if (te.sequencer instanceof Sequencer && te.sequencer!=null)
        		{
        			bpm = te.sequencer.getBeatsPerMinute();
        		}
        	}
        	String s = ""+bpm;
        	
        	
        	
        
        	
            GlStateManager.scale(f3, -f3, f3);
            GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F * f3);
            GlStateManager.depthMask(false);	
        	
        	fontrenderer.drawString(s, 0-fontrenderer.getStringWidth(s) / 2, 0, 0);
        	
            GlStateManager.popMatrix();

			//render 
		
	}
	
	
	
	
}
