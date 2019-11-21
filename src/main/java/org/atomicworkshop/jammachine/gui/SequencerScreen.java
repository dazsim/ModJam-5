package org.atomicworkshop.jammachine.gui;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.text.ITextComponent;

public class SequencerScreen extends ContainerScreen<SequencerContainer> {
    public SequencerScreen(SequencerContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    protected void init() {
        super.init();

        for (int interval=0;interval<16;interval++) {
            for (int pitch=0;pitch<25;pitch++) {
                int finalPitch = pitch;
                int finalInterval = interval;

                int x = interval * 10 + 10;
                int y = pitch * 10 + 10;

                Button b = new Button(x, y, width, height, "", button -> {
                    container.invert(finalPitch, finalInterval);
                    button.setFGColor(0x054334);
                });
                addButton(b);
            }
        }

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    }
}
