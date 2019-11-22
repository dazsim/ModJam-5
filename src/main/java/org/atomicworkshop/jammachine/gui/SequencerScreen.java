package org.atomicworkshop.jammachine.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.atomicworkshop.jammachine.Reference;
import org.atomicworkshop.jammachine.sequencing.AdjacentNoteBlock;
import org.atomicworkshop.jammachine.sequencing.MusicPlayer;
import org.atomicworkshop.jammachine.sequencing.Sequencer;
import org.atomicworkshop.jammachine.tiles.SequencerTileEntity;

public class SequencerScreen extends ContainerScreen<SequencerContainer> {
    private final SequencerTileEntity tileEntity;
    private final Sequencer sequencer;
    private int lastInterval = -1;

    private static final ResourceLocation guiTexture = new ResourceLocation(Reference.MOD_ID, "textures/gui/sequencer.png");

    public SequencerScreen(SequencerContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        tileEntity = container.getTileEntity();
        sequencer = tileEntity.getSequencer();
    }

    private PatternButton[] patternButtons = new PatternButton[25 * 16];

    @Override
    protected void init() {
        this.xSize = 176;
        this.ySize = 245;

        super.init();

        int width = 10;
        int height = 8;

        for (int interval=0;interval<16;interval++) {
            for (int pitch=0;pitch<25;pitch++) {
                int finalPitch = pitch;
                int finalInterval = interval;

                int x = interval * (width + 1) + (interval / 4) * 2 + guiLeft + 8;
                int y = (24-pitch) * (height + 1) + guiTop + 8;

                PatternButton b = new PatternButton(x, y, width, height, finalInterval, finalPitch, sequencer, button -> {
                    container.set(finalPitch, finalInterval, button.isChecked());
                    //TODO: Sync to server.
                    sequencer.getCurrentPattern().setPitchAtInterval(finalInterval, finalPitch, button.isChecked());
                    if (button.isChecked()) {
                        ImmutableList<AdjacentNoteBlock> availableNoteBlocks = sequencer.getAvailableNoteBlocks();
                        if (!availableNoteBlocks.isEmpty())
                        {
                            AdjacentNoteBlock noteBlock = availableNoteBlocks.get(0);
                            MusicPlayer.playNote(sequencer, noteBlock, (byte)finalPitch);
                        }
                    }
                });
                addButton(b);
                b.setChecked(sequencer.getCurrentPattern().isPitchActiveAtInterval(finalInterval, finalPitch));
                patternButtons[interval * 25 + pitch] = b;
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        int currentInterval = sequencer.getCurrentInterval();
        if (currentInterval != lastInterval) {
            for (PatternButton patternButton : patternButtons) {
                patternButton.updateInterval();
            }
            lastInterval = currentInterval;
        }

        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(this.guiTexture);
        int lvt_4_1_ = this.guiLeft;
        int lvt_5_1_ = this.guiTop;
        this.blit(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
    }
}
