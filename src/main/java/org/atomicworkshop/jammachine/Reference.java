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
		public static final ResourceLocation heldcable = resource("heldcable");
		private Items() { }
	}

	public static final class TileEntities {
		public static final String sequencer = tileEntityName("sequencer");
		public static final String controller = tileEntityName("controller");
		public static final String cable = tileEntityName("cable");

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
		
		/* cables */
		
		public static String floor = "floor";
		public static String ceiling = "ceiling";
		public static String north = "north";
		public static String south = "south";
		public static String east = "east";
		public static String west = "west";
		
		public static String floornorth = "floornorth";
		public static String floorsouth = "floorsouth";
		public static String flooreast = "flooreast";
		public static String floorwest = "floorwest";
		
		private NBT() { }
	}
	
	/*
	 * FOR REFERENCE
	 * 
	 * 0 = North
	 * 1 = South
	 * 2 = East
	 * 3 = West
	 * 4 = Floor
	 * 5 = Ceiling
	 */
	public static final class Connections {
		public static int[][] connections;
		public int[][] getConnections()
		{
			int[][] connections = new int[6][4];
			for (int a = 0;a<6;a++)
			{
				if (a%2==0)
				{
					connections[a][0] = (a+2) % 6;
					connections[a][1] = (a+3) % 6;
					connections[a][2] = (a+4) % 6;
					connections[a][3] = (a+5) % 6;
							
				} else
				{
					connections[a][0] = (a+1) % 6;
					connections[a][1] = (a+2) % 6;
					connections[a][2] = (a+3) % 6;
					connections[a][3] = (a+4) % 6;
				}
				   
			}
			return connections;
		}
		public Connections() { 
			this.connections = getConnections();
		}
	}

	private Reference() { }

	private static ResourceLocation resource(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
