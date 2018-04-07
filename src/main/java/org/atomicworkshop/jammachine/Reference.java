package org.atomicworkshop.jammachine;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import java.util.UUID;

@SuppressWarnings("UtilityClass")
public final class Reference
{
	public static final String MOD_ID = "jammachine";
	public static final String NAME = "The Jam Machine";
	public static final String VERSION = "1.0";
	public static final String TabLabel = MOD_ID + ".tab_label";
	public static final CreativeTabs CreativeTab = new JamMachineTab();
	public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	public static final class Blocks {
		public static final ResourceLocation sequencer = resource("sequencer");
		public static final ResourceLocation controller = resource("controller");
		public static final ResourceLocation cable = resource("cable");

		private Blocks() { }
	}

	public static final class Items {
		public static final ResourceLocation punchcardblank = resource("punchcardblank");
		public static final ResourceLocation punchcardwritten = resource("punchcardwritten");
		public static final ResourceLocation braidedstring = resource("braidedstring");

		private Items() { }
	}

	public static final class TileEntities {
		public static final String sequencer = tileEntityName("sequencer");
		public static final String controller = tileEntityName("controller");

		private TileEntities() { }

		private static String tileEntityName(String name) {
			return "tile." + name;
		}
	}

	public static final class NBT {
		public static final String isPlaying = "isPlaying";
		public static String songId = "songId";
		public static String sequencerId = "sequencerId";
		public static String beatsPerMinute = "bpm";
		public static String currentPatternIndex = "currentPattern";
		public static String pendingPatternIndex = "pendingPattern";
		public static String pattern = "pattern";
		public static String sequence = "sequence";
		public static String hasCard = "hasCard";
		public static String name = "name";

        private NBT() { }
	}

	private Reference() { }

	private static ResourceLocation resource(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
