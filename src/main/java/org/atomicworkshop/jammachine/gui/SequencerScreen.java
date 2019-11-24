package org.atomicworkshop.jammachine.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import org.atomicworkshop.jammachine.JamMachineMod;
import org.atomicworkshop.jammachine.Reference;
import org.atomicworkshop.jammachine.network.SequencerSetPitchAtInterval;
import org.atomicworkshop.jammachine.sequencing.AdjacentNoteBlock;
import org.atomicworkshop.jammachine.sequencing.MusicPlayer;
import org.atomicworkshop.jammachine.sequencing.Sequencer;
import org.atomicworkshop.jammachine.tiles.SequencerTileEntity;

public class SequencerScreen extends ContainerScreen<SequencerContainer> {
    private final SequencerTileEntity tileEntity;
    private final Sequencer sequencer;
    private TextureManager textureManager;
    private int lastInterval = -1;

    private static final ResourceLocation guiTexture = new ResourceLocation(Reference.MOD_ID, "textures/gui/sequencer.png");

    public SequencerScreen(SequencerContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        tileEntity = container.getTileEntity();
        sequencer = tileEntity.getSequencer();
    }

    private PatternButton[][] patternButtons = new PatternButton[16][25];

    @Override
    protected void init() {
        this.xSize = 196;
        this.ySize = 246;

        super.init();

        assert this.minecraft != null;
        textureManager = this.minecraft.getTextureManager();

        int width = 10;
        int height = 8;

        for (int interval=0;interval<16;interval++) {
            for (int pitch=0;pitch<25;pitch++) {
                int x = interval * (width + 1) + (interval / 4) + guiLeft + 8;
                int y = (24-pitch) * (height + 1) + guiTop + 8;

                PatternButton b = new PatternButton(x, y, width, height, interval, pitch, sequencer, this::onPatternPressed);
                addButton(b);
                patternButtons[interval][pitch] = b;
            }
        }

        int bpmXPos = 340;
        int bpmYPos = 30;
        int bpmWidth = 14;
        int bpmHeight = 10;

        addButton(new Button(bpmXPos, bpmYPos, bpmWidth, bpmHeight, "<<", (button) -> {
            final int beatsPerMinute = sequencer.getBeatsPerMinute() - 10;
            sequencer.setDesiredBPM(beatsPerMinute);
        }));

        bpmXPos += bpmWidth + 1;
        addButton(new Button(bpmXPos, bpmYPos, bpmWidth, bpmHeight, "<", (button) -> {
            final int beatsPerMinute = sequencer.getBeatsPerMinute() - 1;
            sequencer.setDesiredBPM(beatsPerMinute);
        }));

        bpmXPos += bpmWidth + 1;
        addButton(new Button(bpmXPos, bpmYPos, bpmWidth, bpmHeight, ">", (button) -> {
            final int beatsPerMinute = sequencer.getBeatsPerMinute() + 1;
            sequencer.setDesiredBPM(beatsPerMinute);
        }));

        bpmXPos += bpmWidth + 1;
        addButton(new Button(bpmXPos, bpmYPos, bpmWidth, bpmHeight, ">>", (button) -> {
            final int beatsPerMinute = sequencer.getBeatsPerMinute() + 10;
            sequencer.setDesiredBPM(beatsPerMinute);
        }));

        addButton(new Button(340, 45, (bpmWidth * 4) + 3, 18, "Prog 1", (button) -> {

        }));

    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        int currentInterval = sequencer.getCurrentInterval();
        if (!sequencer.isPlaying()) {
            currentInterval = -1;
        }

        if (currentInterval != lastInterval) {
            if (lastInterval != -1) {
                for (PatternButton patternButton : patternButtons[lastInterval]) {
                    patternButton.updateInterval();
                }
            }

            if (currentInterval != -1) {
                for (PatternButton patternButton : patternButtons[currentInterval]) {
                    patternButton.updateInterval();
                }
            }

            lastInterval = currentInterval;
        }

        super.render(mouseX, mouseY, partialTicks);

        font.drawString("SEQ-9001", guiLeft + 200, 8, 0xFFFFFF);

        double textScale = 1.5;
        GlStateManager.pushMatrix();
        GlStateManager.scaled(textScale, textScale, textScale);
        final String bpmText = String.valueOf(sequencer.getBeatsPerMinute());
        final float offset = font.getStringWidth(bpmText) / 2.0f;
        font.drawString(bpmText, guiLeft + 105 - offset, 12, 0xFFFFFF);
        GlStateManager.popMatrix();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        textureManager.bindTexture(guiTexture);
        int lvt_4_1_ = this.guiLeft;
        int lvt_5_1_ = this.guiTop;
        this.blit(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
    }

    private void onPatternPressed(int finalPitch, int finalInterval, boolean isChecked) {
        container.set(finalPitch, finalInterval, isChecked);
        final World world = tileEntity.getWorld();
        assert world != null;
        JamMachineMod.CHANNEL.send(PacketDistributor.SERVER.noArg(),
                new SequencerSetPitchAtInterval(
                        finalPitch,
                        finalInterval,
                        isChecked,
                        tileEntity.getPos(),
                        world.dimension.getType()
                ));
        sequencer.getCurrentPattern().setPitchAtInterval(finalInterval, finalPitch, isChecked);
        if (isChecked) {
            ImmutableList<AdjacentNoteBlock> availableNoteBlocks = sequencer.getAvailableNoteBlocks();
            if (!availableNoteBlocks.isEmpty()) {
                AdjacentNoteBlock noteBlock = availableNoteBlocks.get(0);
                MusicPlayer.playNote(sequencer, noteBlock, (byte) finalPitch);
            }
        }
    }
}
