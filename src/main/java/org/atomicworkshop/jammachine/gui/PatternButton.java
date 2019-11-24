package org.atomicworkshop.jammachine.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.item.DyeColor;
import net.minecraft.util.SoundEvents;
import org.atomicworkshop.jammachine.sequencing.Sequencer;

public class PatternButton extends AbstractButton {
    private final int interval;
    private final int pitch;
    private final Sequencer sequencer;
    protected final IPressable onPress;
    private int colour;
    private boolean isChecked;

    public PatternButton(int x, int y, int width, int height, int interval, int pitch, Sequencer sequencer, IPressable onPressed) {
        super(x, y, width, height, "");
        this.interval = interval;
        this.pitch = pitch;
        this.sequencer = sequencer;
        this.onPress = onPressed;

        this.isChecked = sequencer.isPlaying() && sequencer.getCurrentPattern().isPitchActiveAtInterval(interval, pitch);
        updateColour();
    }

    @Override
    public void onPress() {
        this.isChecked = !this.isChecked;
        this.onPress.onPress(pitch, interval, isChecked);
        updateColour();
    }

    @Override
    public void playDownSound(SoundHandler p_playDownSound_1_) {
        //No click for you
    }

    @Override
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);

        float r = ((colour & 0xff0000) >> 16) / 255.0f;
        float g = ((colour & 0x00ff00) >> 8) / 255.0f;
        float b = ((colour & 0x0000ff) >> 0) / 255.0f;
        GlStateManager.color4f(r * 2, g * 2, b * 2, this.alpha);
        //GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.blit(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
    }

    private static int getSequenceButtonColour(int currentInterval, int interval, int pitch, boolean isEnabled)
    {
        int disabledItemInactiveInterval = DyeColor.LIGHT_GRAY.getColorValue();
        int enabledItemInactiveInterval = DyeColor.BROWN.getColorValue();
        int disabledItemActiveInterval = DyeColor.WHITE.getColorValue();
        int enabledItemActiveInterval = DyeColor.YELLOW.getColorValue();

        int disabledItemInactiveIntervalSharp = DyeColor.BLACK.getColorValue();
        int enabledItemInactiveIntervalSharp = DyeColor.BROWN.getColorValue();
        int disabledItemActiveIntervalSharp = DyeColor.GRAY.getColorValue();
        int enabledItemActiveIntervalSharp = DyeColor.ORANGE.getColorValue();

        if (isSharpPitch(pitch)) {
            //Use sharp colours.
            if (currentInterval == interval)
            {
                return isEnabled ? enabledItemActiveIntervalSharp : disabledItemActiveIntervalSharp;
            } else {
                return isEnabled ? enabledItemInactiveIntervalSharp : disabledItemInactiveIntervalSharp;
            }
        } else {
            //Use non-sharp colours.
            if (currentInterval == interval) {
                return isEnabled ? enabledItemActiveInterval : disabledItemActiveInterval;
            } else {
                return isEnabled ? enabledItemInactiveInterval : disabledItemInactiveInterval;
            }
        }
    }

    @Override
    public void renderToolTip(int p_renderToolTip_1_, int p_renderToolTip_2_) {

    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    private static boolean isSharpPitch(int pitch)
    {
        return pitch == 0 || pitch == 2 || pitch == 4 || pitch == 7 || pitch == 9 || pitch == 12 ||
                pitch == 14 || pitch == 16 || pitch == 19 || pitch == 21 || pitch == 24;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        updateColour();
    }

    private void updateColour() {
        this.colour = getSequenceButtonColour(sequencer.getCurrentInterval(), interval, pitch, isChecked);
    }

    public void updateInterval() {
        updateColour();
    }

    public interface IPressable {
        void onPress(int pitch, int interval, boolean isChecked);
    }
}