package com.streamermod;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AuctionTracker {
    private static final Pattern PAYMENT = Pattern.compile("(?i)^\\s*(.+?)\\s+paid you\\s+\\$\\s*([0-9][0-9,]*(?:\\.[0-9]+)?)\\s*([kmb]?)\\.?\\s*$");
    private static boolean active;
    private static double minimum;
    private static double worth;
    private static double highestBet;
    private static String highestPlayer = "-";
    private static long endsAt;
    private static String lastMessage = "";
    private static long lastMessageAt;

    private AuctionTracker() {}

    public static void initialize() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> handlePayment(message.getString()));
        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            if (active) renderHud(context);
        });
    }

    public static synchronized void start(double min, double itemWorth, int minutes) {
        minimum = Math.max(0, min);
        worth = Math.max(0, itemWorth);
        highestBet = 0;
        highestPlayer = "-";
        endsAt = System.currentTimeMillis() + Math.max(1, minutes) * 60_000L;
        active = true;
    }

    public static synchronized void stop() {
        active = false;
    }

    public static synchronized boolean isActive() { return active; }
    public static synchronized double getMinimum() { return minimum; }
    public static synchronized double getWorth() { return worth; }
    public static synchronized double getHighestBet() { return highestBet; }
    public static synchronized String getHighestPlayer() { return highestPlayer; }

    public static synchronized long getRemainingSeconds() {
        if (!active) return 0;
        long remaining = Math.max(0, (endsAt - System.currentTimeMillis() + 999) / 1000);
        if (remaining == 0) active = false;
        return remaining;
    }

    private static synchronized void handlePayment(String raw) {
        if (!active || raw == null) return;
        long now = System.currentTimeMillis();
        if (raw.equals(lastMessage) && now - lastMessageAt < 1500) return;
        lastMessage = raw;
        lastMessageAt = now;

        String clean = raw.replaceAll("§[0-9A-FK-ORa-fk-or]", "").trim();
        Matcher matcher = PAYMENT.matcher(clean);
        if (!matcher.matches()) return;

        String player = matcher.group(1).trim();
        String number = matcher.group(2).replace(",", "");
        String suffix = matcher.group(3).toLowerCase(java.util.Locale.ROOT);
        double amount;
        try {
            amount = Double.parseDouble(number);
            if ("k".equals(suffix)) amount *= 1_000;
            else if ("m".equals(suffix)) amount *= 1_000_000;
            else if ("b".equals(suffix)) amount *= 1_000_000_000;
        } catch (NumberFormatException ignored) {
            return;
        }

        if (amount >= minimum && amount > highestBet) {
            highestBet = amount;
            highestPlayer = player;
        }
    }

    private static void renderHud(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        int x = context.getScaledWindowWidth() - 166;
        int y = 12;
        long seconds = getRemainingSeconds();
        context.fill(x, y, x + 158, y + 76, 0xD91B2028);
        context.drawTextWithShadow(client.textRenderer, Text.literal("AUCTION"), x + 7, y + 6, 0xFF66D9EF);
        context.drawTextWithShadow(client.textRenderer, Text.literal("Highest Bet: " + money(highestBet)), x + 7, y + 20, 0xFFFFFFFF);
        context.drawTextWithShadow(client.textRenderer, Text.literal("Min: " + money(minimum)), x + 7, y + 32, 0xFFB8C5D1);
        context.drawTextWithShadow(client.textRenderer, Text.literal("Worth: " + money(worth)), x + 7, y + 44, 0xFFB8C5D1);
        context.drawTextWithShadow(client.textRenderer, Text.literal("Time: " + formatTime(seconds)), x + 7, y + 56, 0xFF66D9EF);
    }

    public static String money(double amount) {
        return "$" + String.format(java.util.Locale.US, "%,.0f", amount);
    }

    public static String formatTime(long seconds) {
        return String.format(java.util.Locale.US, "%d:%02d", seconds / 60, seconds % 60);
    }
}
