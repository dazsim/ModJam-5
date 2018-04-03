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
		public static final ResourceLocation synchronizer = resource("synchronizer");
		public static final ResourceLocation wire = resource("wire");


		private Blocks() { }
	}

	public static final class Items {
		public static final ResourceLocation punchcardblank = resource("punchcardblank");
		public static final ResourceLocation punchcardwritten = resource("punchcardwritten");

		private Items() { }
	}

	public static final class TileEntities {
		public static final String sequencer = tileEntityName("sequencer");
		public static final String synchronizer = tileEntityName("synchronizer");

		private TileEntities() { }

		private static String tileEntityName(String name) {
			return "tile." + name;
		}
	}

	public static final class NBT {
		public static final String isPlaying = "isPlaying";
		public static String songId = "songId";
		public static String beatsPerMinute = "bpm";
		public static String currentPatternIndex = "currentPattern";
		public static String pendingPatternIndex = "pendingPattern";
		public static String pattern = "pattern";
		public static String sequence = "sequence";
		public static String hasCard = "hasCard";

		private NBT() { }
	}

	private Reference() { }

	private static ResourceLocation resource(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
