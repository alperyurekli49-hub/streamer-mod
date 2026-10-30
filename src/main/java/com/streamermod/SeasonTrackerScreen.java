package com.streamermod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class SeasonTrackerScreen extends Screen {
    public SeasonTrackerScreen() {
        super(Text.literal("Season Tracker"));
    }

    @Override
    protected void init() {
        int center = width / 2;
        int top = height / 2 - 65;

        addDrawableChild(ButtonWidget.builder(Text.literal(SeasonTracker.isActive() ? "End Season" : "Start Season"), button -> {
            if (SeasonTracker.isActive()) SeasonTracker.end();
            else SeasonTracker.start();
            this.clearAndInit();
        }).dimensions(center - 105, top, 210, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("HUD: " + (SeasonTracker.isOverlay() ? "ON" : "OFF")), button -> {
            SeasonTracker.toggleOverlay();
            this.clearAndInit();
        }).dimensions(center - 105, top + 28, 210, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("HUD position: " + SeasonTracker.getPosition().name().replace('_', ' ')), button -> {
            SeasonTracker.cyclePosition();
            this.clearAndInit();
        }).dimensions(center - 105, top + 56, 210, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Back to menu"), button ->
                this.client.setScreen(new StreamerMenuScreen()))
                .dimensions(center - 105, top + 90, 210, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int center = width / 2;
        context.drawCenteredTextWithShadow(textRenderer, "SEASON TRACKER", center, height / 2 - 105, 0xFFB88CFF);
        context.drawCenteredTextWithShadow(textRenderer, "Status: " + (SeasonTracker.isActive() ? "RUNNING" : "STOPPED"), center, height / 2 - 88, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, "Income: " + SeasonTracker.money(SeasonTracker.getIncome()), center, height / 2 - 3, 0xFF55DD88);
        context.drawCenteredTextWithShadow(textRenderer, "Expenses: " + SeasonTracker.money(SeasonTracker.getExpenses()), center, height / 2 + 12, 0xFFFF7777);
        context.drawCenteredTextWithShadow(textRenderer, "Net: " + SeasonTracker.money(SeasonTracker.getNet()), center, height / 2 + 27, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, "Start a season to track server chat transactions.", center, height / 2 + 48, 0xFFB8C5D1);
        super.render(context, mouseX, mouseY, delta);
    }
}
