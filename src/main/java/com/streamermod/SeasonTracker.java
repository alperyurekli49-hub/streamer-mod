package com.streamermod;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SeasonTracker {
    public enum Position { TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT }
    private static final Pattern AMOUNT = Pattern.compile("(?i)(?:\\$\\s*([0-9][0-9,]*(?:\\.[0-9]{1,2})?)|([0-9][0-9,]*(?:\\.[0-9]{1,2})?)\\s*\\$)");
    private static final Pattern RECEIVED = Pattern.compile("(?i)\\b(received|earned|got|deposit(?:ed)?)\\b");
    private static final Pattern SPENT = Pattern.compile("(?i)\\b(bought|buy|purchase[sd]?|paid|pay|sent|gave|transfer(?:red)?)\\b");
    private static final Path FILE = MinecraftClient.getInstance().runDirectory.toPath().resolve("config/streamermod-season.json");

    private static boolean active;
    private static boolean overlay = true;
    private static Position position = Position.TOP_RIGHT;
    private static double income;
    private static double expenses;
    private static String seasonName = "Season 1";

    private SeasonTracker() {}

    public static void initialize() {
        load();
        ClientReceiveMessageEvents.GAME.register((message, overlayMessage) -> track(message.getString()));
        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            if (active && overlay) renderHud(context);
        });
    }

    public static synchronized void start() {
        if (!active) {
            income = 0;
            expenses = 0;
            active = true;
            save();
        }
    }

    public static synchronized void end() {
        active = false;
        save();
    }

    public static synchronized void toggleOverlay() {
        overlay = !overlay;
        save();
    }

    public static synchronized void cyclePosition() {
        position = Position.values()[(position.ordinal() + 1) % Position.values().length];
        save();
    }

    public static synchronized boolean isActive() { return active; }
    public static synchronized boolean isOverlay() { return overlay; }
    public static synchronized Position getPosition() { return position; }
    public static synchronized double getIncome() { return income; }
    public static synchronized double getExpenses() { return expenses; }
    public static synchronized double getNet() { return income - expenses; }
    public static synchronized String getSeasonName() { return seasonName; }

    private static synchronized void track(String raw) {
        if (!active || raw == null) return;
        String message = raw.replaceAll("§[0-9A-FK-ORa-fk-or]", "");
        Matcher amountMatcher = AMOUNT.matcher(message);
        if (!amountMatcher.find()) return;
        String number = amountMatcher.group(1) != null ? amountMatcher.group(1) : amountMatcher.group(2);
        double value;
        try { value = Double.parseDouble(number.replace(",", "")); }
        catch (NumberFormatException ignored) { return; }

        if (RECEIVED.matcher(message).find()) {
            income += value;
            save();
        } else if (SPENT.matcher(message).find()) {
            expenses += value;
            save();
        }
    }

    private static void renderHud(DrawContext context) {
        int x = position == Position.TOP_LEFT || position == Position.BOTTOM_LEFT ? 8 : context.getScaledWindowWidth() - 158;
        int y = position == Position.BOTTOM_LEFT || position == Position.BOTTOM_RIGHT ? context.getScaledWindowHeight() - 62 : 8;
        context.fill(x, y, x + 150, y + 54, 0xD91B1728);
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.literal("§d" + seasonName), x + 7, y + 5, 0xFFFFFF);
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.literal("§a+" + money(income)), x + 7, y + 18, 0xFFFFFF);
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.literal("§c-" + money(expenses)), x + 78, y + 18, 0xFFFFFF);
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.literal("§fNet: " + money(income - expenses)), x + 7, y + 36, 0xFFFFFF);
    }

    public static String money(double amount) { return String.format(java.util.Locale.US, "$%,.2f", amount); }

    private static synchronized void save() {
        try {
            Files.createDirectories(FILE.getParent());
            JsonObject root = new JsonObject();
            root.addProperty("active", active);
            root.addProperty("overlay", overlay);
            root.addProperty("position", position.name());
            root.addProperty("income", income);
            root.addProperty("expenses", expenses);
            root.addProperty("seasonName", seasonName);
            Files.writeString(FILE, root.toString());
        } catch (IOException ignored) {}
    }

    private static synchronized void load() {
        if (!Files.exists(FILE)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
            active = root.has("active") && root.get("active").getAsBoolean();
            overlay = !root.has("overlay") || root.get("overlay").getAsBoolean();
            if (root.has("position")) position = Position.valueOf(root.get("position").getAsString());
            if (root.has("income")) income = root.get("income").getAsDouble();
            if (root.has("expenses")) expenses = root.get("expenses").getAsDouble();
            if (root.has("seasonName")) seasonName = root.get("seasonName").getAsString();
        } catch (Exception ignored) {}
    }
}
