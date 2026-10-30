package com.streamermod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AuctionTrackerScreen extends Screen {
    private TextFieldWidget minimumField;
    private TextFieldWidget worthField;
    private TextFieldWidget durationField;

    public AuctionTrackerScreen() {
        super(Text.literal("Auction Tracker"));
    }

    @Override
    protected void init() {
        int center = width / 2;
        int top = height / 2 - 66;

        minimumField = new TextFieldWidget(textRenderer, center - 85, top + 15, 170, 20, Text.literal("Minimum"));
        minimumField.setText("100000");
        minimumField.setPlaceholder(Text.literal("Minimum"));
        addDrawableChild(minimumField);

        worthField = new TextFieldWidget(textRenderer, center - 85, top + 55, 170, 20, Text.literal("Worth"));
        worthField.setText("0");
        worthField.setPlaceholder(Text.literal("Worth"));
        addDrawableChild(worthField);

        durationField = new TextFieldWidget(textRenderer, center - 85, top + 95, 170, 20, Text.literal("Minutes"));
        durationField.setText("5");
        durationField.setPlaceholder(Text.literal("Minutes"));
        addDrawableChild(durationField);

        addDrawableChild(ButtonWidget.builder(Text.literal("Start Auction"), button -> {
            try {
                double min = Double.parseDouble(minimumField.getText().replace(",", ""));
                double itemWorth = Double.parseDouble(worthField.getText().replace(",", ""));
                int minutes = Integer.parseInt(durationField.getText());
                if (min >= 0 && itemWorth >= 0 && minutes > 0) {
                    AuctionTracker.start(min, itemWorth, minutes);
                    close();
                }
            } catch (NumberFormatException ignored) {}
        }).dimensions(center - 85, top + 130, 170, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("End Auction"), button -> {
            AuctionTracker.stop();
            close();
        }).dimensions(center - 85, top + 155, 170, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button ->
                client.setScreen(new StreamerMenuScreen()))
                .dimensions(center - 85, top + 180, 170, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int center = width / 2;
        int top = height / 2 - 85;
        context.fill(center - 105, top, center + 105, top + 275, 0xE619202B);
        context.drawCenteredTextWithShadow(textRenderer, "AUCTION SETUP", center, top + 8, 0xFF66D9EF);
        context.drawTextWithShadow(textRenderer, "Min ($)", center - 85, top + 31, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, "Worth ($)", center - 85, top + 71, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, "Time (minutes)", center - 85, top + 111, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,
                AuctionTracker.isActive() ? "An auction is currently running" : "Payments are read from server chat",
                center, top + 253, 0xFFB8C5D1);
        super.render(context, mouseX, mouseY, delta);
    }
}
