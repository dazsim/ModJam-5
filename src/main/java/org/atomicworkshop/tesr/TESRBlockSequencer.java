package org.atomicworkshop.tesr;

import org.atomicworkshop.tiles.TileEntitySequencer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESRBlockSequencer extends TileEntitySpecialRenderer<TileEntitySequencer>
{
	public static Minecraft mc = Minecraft.getMinecraft();
	
	
	 
	    
	
	@Override
	public void render(TileEntitySequencer te, double x, double y, double z, float partialTicks,
			int destroyStage,float alpha) {
			
			int f = te.getBlockMetadata();
			//f == orientation 
			//render buttons
			//render cards
			//render BPM
			float f1 = 0.6666667F;
        	float f3 = 0.015625F * f1;
        	GlStateManager.pushMatrix();
        	FontRenderer fontrenderer = this.getFontRenderer();
        	
        	String s = "120";
        	if (f==2)
        	{ 
        		GlStateManager.translate(0.5, 0.4+(0.5F * f1), 0.96+(0.01f * f1));//0.5, 0.5, 0.0 NORTH
        	} else if (f==3)//south
        	{
        		GlStateManager.rotate(180, 0.0F, 1.0F, 0);
        		GlStateManager.translate(-0.5, 0.4+(0.5F * f1), -0.04+(0.01f * f1));//south
        	} else if (f==4)
        	{
        		GlStateManager.translate(0.96, 0.4+(0.5F * f1), 0.5+(0.01f * f1));
        		GlStateManager.rotate(90, 0.0F, 1.0F, 0);
        	} else if (f==5) //east
        	{
        		GlStateManager.rotate(-90, 0.0F, 1.0F, 0);
        		GlStateManager.translate(0.5, 0.4+(0.5F * f1), -0.04+(0.01f * f1));
        		
        	} 
        	
        	
        
        	
            GlStateManager.scale(f3, -f3, f3);
            GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F * f3);
            GlStateManager.depthMask(false);	
        	
        	fontrenderer.drawString(s, 0-fontrenderer.getStringWidth(s) / 2, 0, 0);
        	
            GlStateManager.popMatrix();
			//render 
		
	}
	
	
	
	
}
