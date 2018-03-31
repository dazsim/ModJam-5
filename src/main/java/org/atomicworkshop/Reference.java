package org.atomicworkshop;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("UtilityClass")
public final class Reference
{
	public static final String MOD_ID = "theconductor";
	public static final String NAME = "The Conductor";
	public static final String VERSION = "1.0";
	public static final String TabLabel = MOD_ID + ".tab_label";
	public static final CreativeTabs CreativeTab = new ConductorTab();

	public static final class Blocks {
		public static final ResourceLocation sequencer = resource("sequencer");

		private Blocks() { }
	}

	public static final class TileEntities {
		public static final String sequencer = tileEntityName("sequencer");

		private TileEntities() { }

		private static String tileEntityName(String name) {
			return "tile." + name;
		}
	}

	public static final class NBT {
		public static final String isPlaying = "isPlaying";
		public static String songId = "songId";
	}

	private Reference() { }

	private static ResourceLocation resource(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
