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
		 	RenderItem itemRenderer = mc.getRenderItem();
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
        	GlStateManager.rotate(270.0f+90.0f*(4-f),0.0f,1.0f,0.0f);
        	
        	GlStateManager.translate(0.8, 0.5, 0.8);
        	GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
        	GlStateManager.rotate(-60.0f, 1.0f,0.0f,0.0f);
        	GlStateManager.translate(0.7, -0.2, -0.45);
        	if (f==0)
        	{
        		GlStateManager.translate(0.9, -0.1, 0.23);
        	}
        	if (f==1)
        	{
        		GlStateManager.translate(0.9,0.8,-0.25);
        	}
        	if (f==2)
        	{
        		GlStateManager.translate(-0.07, 0.8, -0.32);
        	}
        	if (f==3)
        	{
        		GlStateManager.translate(-0.07, -0.05, 0.18);
        	}
        	if (te instanceof TileEntitySequencer && te !=null)
        	{
        		
        		if (te.sequencer instanceof Sequencer && te.sequencer!=null)
        		{
        			
        			bpm = te.sequencer.getBeatsPerMinute();
        			Pattern p = te.sequencer.getCurrentPattern();
        			GlStateManager.pushMatrix();
        			GlStateManager.translate(-0.03,0.8,-0.13);
        			for (int loop1=0;loop1<24;loop1++)
        			{
        				GlStateManager.pushMatrix();
        				GlStateManager.translate(((loop1*0.8)/24)*-1,0, -((loop1)/24)*0.1);
        				//outer loop. reset after every outer loop.
        				for (int loop2=0;loop2<16;loop2++)
        				{
        					GlStateManager.translate(0.0,-0.050,0.008);
        					   EntityItem entityitem = new EntityItem(getWorld(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.QUARTZ_BLOCK));
        			            entityitem.getItem().setCount(1);
        			            
        			            GlStateManager.pushMatrix();
        			            GlStateManager.disableLighting();
        			            
        			            GlStateManager.rotate(180f,1.0f,0.0f,0.0f);
        			            GlStateManager.rotate(180f,0.0f,0.0f,1.0f);
        			            GlStateManager.scale(0.05F, 0.05F, 0.05F);
        			            GlStateManager.depthMask(true);
        			            GlStateManager.pushAttrib();
        			            //RenderHelper.enableStandardItemLighting();
        			            final boolean[][] pData = new boolean[16][25];
        			            //fill our local copy of pData with pattern so we can then iterate thru it and check which buttons are lit
        			            {
        			            	itemRenderer.renderItem(entityitem.getItem(), ItemCameraTransforms.TransformType.FIXED);
        			            }
        			            System.out.println("QUARTZ");
        			            //RenderHelper.disableStandardItemLighting();
        			            GlStateManager.popAttrib();
        			            
        			            GlStateManager.enableLighting();
        				}
        				GlStateManager.popMatrix();
        				
        			}
        			GlStateManager.popMatrix();
        		}
        		else
        		{
        			//System.out.println("SHWARTZ");
        		}
        		
        	}
        	String s = ""+bpm;
        	//System.out.println(f);
        	
        	
        	
        
        	
            GlStateManager.scale(f3, -f3, f3);
            GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F * f3);
            GlStateManager.depthMask(false);	
        	
        	fontrenderer.drawString(s, 0-fontrenderer.getStringWidth(s) / 2, 0, 0xFFFFFF);
        	
            GlStateManager.popMatrix();
			//render 
		
	}
	
	
	
	
}
