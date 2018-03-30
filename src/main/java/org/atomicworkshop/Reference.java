package org.atomicworkshop;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;

public final class Reference
{
	public static final String MOD_ID = "theconductor";
	public static final String NAME = "The Conductor";
	public static final String VERSION = "1.0";
	public static final String TabLabel = MOD_ID + ".tab_label";
	public static final CreativeTabs CreativeTab = new ConductorTab();

	public static class Blocks {
		public static ResourceLocation sequencer = resource("sequencer");

		private Blocks() { }
	}

	private Reference() { }

	private static ResourceLocation resource(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
