package com.streamermod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class StreamerMenuScreen extends Screen {
    private static final int PANEL_WIDTH = 260;

    public StreamerMenuScreen() {
        super(Text.literal("Streamer Mod"));
    }

    @Override
    protected void init() {
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = this.height / 2 - 54;

        addDrawableChild(ButtonWidget.builder(Text.literal("Auction Notes"), button ->
                this.client.setScreen(new UtilityScreen(UtilityScreen.Mode.AUCTION)))
                .dimensions(left + 20, top, PANEL_WIDTH - 40, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("PayGuard Checklist"), button ->
                this.client.setScreen(new UtilityScreen(UtilityScreen.Mode.PAYGUARD)))
                .dimensions(left + 20, top + 28, PANEL_WIDTH - 40, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Countdown"), button ->
                this.client.setScreen(new UtilityScreen(UtilityScreen.Mode.COUNTDOWN)))
                .dimensions(left + 20, top + 56, PANEL_WIDTH - 40, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> this.close())
                .dimensions(left + 20, top + 92, PANEL_WIDTH - 40, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = this.height / 2 - 92;
        context.fill(left, top, left + PANEL_WIDTH, top + 174, 0xE619202B);
        context.drawCenteredTextWithShadow(textRenderer, "STREAMER MOD", this.width / 2, top + 14, 0xFF66D9EF);
        context.drawCenteredTextWithShadow(textRenderer, "Creator utility menu", this.width / 2, top + 29, 0xFFB8C5D1);
        super.render(context, mouseX, mouseY, delta);
    }
}
