package com.streamermod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class UtilityScreen extends Screen {
    public enum Mode { AUCTION, PAYGUARD, COUNTDOWN }

    private final Mode mode;
    private TextFieldWidget noteField;

    public UtilityScreen(Mode mode) {
        super(Text.literal(titleFor(mode)));
        this.mode = mode;
    }

    private static String titleFor(Mode mode) {
        return switch (mode) {
            case AUCTION -> "Auction Notes";
            case PAYGUARD -> "PayGuard Checklist";
            case COUNTDOWN -> "Countdown";
        };
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        int top = this.height / 2 - 35;

        if (mode == Mode.AUCTION) {
            noteField = new TextFieldWidget(textRenderer, center - 105, top, 210, 20,
                    Text.literal("Auction note"));
            noteField.setMaxLength(100);
            noteField.setPlaceholder(Text.literal("Item / price / player..."));
            addDrawableChild(noteField);
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Back to menu"), button ->
                this.client.setScreen(new StreamerMenuScreen()))
                .dimensions(center - 105, top + 62, 210, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int center = this.width / 2;
        context.drawCenteredTextWithShadow(textRenderer, title, center, this.height / 2 - 75, 0xFF66D9EF);

        switch (mode) {
            case AUCTION -> {
                context.drawCenteredTextWithShadow(textRenderer,
                        "Write a reminder for an auction below.", center, this.height / 2 - 52, 0xFFFFFFFF);
                context.drawCenteredTextWithShadow(textRenderer,
                        "This note is temporary and clears when you close the screen.",
                        center, this.height / 2 + 4, 0xFFB8C5D1);
            }
            case PAYGUARD -> {
                context.drawCenteredTextWithShadow(textRenderer,
                        "Before trading, check the details:", center, this.height / 2 - 48, 0xFFFFFFFF);
                context.drawCenteredTextWithShadow(textRenderer,
                        "1. Confirm the exact item and quantity", center, this.height / 2 - 26, 0xFFE6EDF3);
                context.drawCenteredTextWithShadow(textRenderer,
                        "2. Check the agreed price with the other player", center, this.height / 2 - 12, 0xFFE6EDF3);
                context.drawCenteredTextWithShadow(textRenderer,
                        "3. Verify the trade in-game before confirming", center, this.height / 2 + 2, 0xFFE6EDF3);
            }
            case COUNTDOWN -> {
                context.drawCenteredTextWithShadow(textRenderer,
                        "Countdown tools are coming in the next update.",
                        center, this.height / 2 - 20, 0xFFFFFFFF);
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }
}
