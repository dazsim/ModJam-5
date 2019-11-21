package org.atomicworkshop.jammachine;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Resource;
import java.util.UUID;

public final class Reference {
    public static final String MOD_ID = "jammachine";
    public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static Direction[] HORIZONTALS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    public static ResourceLocation resource(String resourceName) {
        return new ResourceLocation(MOD_ID, resourceName);
    }

    public static final class Blocks {

        public static final ResourceLocation SEQUENCER = resource("sequencer");

        private Blocks() {}
    }

    public static class TileEntities {
        public static final ResourceLocation SEQUENCER = resource("sequencer_tile");

        private TileEntities() {}
    }

    public static class Container {
        public static final ResourceLocation SEQUENCER = resource("sequencer_container");

        private Container() {}
    }

    public static final class Items {

        public static final ResourceLocation PUNCH_CARD_WRITTEN = resource("punchcardwritten");
        public static final ResourceLocation PUNCH_CARD_BLANK = resource("punchcardblank");

        private Items() {}
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
        public static String program = "program";
        public static String isProgramming = "isProgramming";

        private NBT() { }
    }

    private Reference() {}
}
