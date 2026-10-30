package com.streamermod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class StreamerModClient implements ClientModInitializer {
    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        SeasonTracker.initialize();
        openMenuKey = KeyBindingHelper.registerKeyBinding(createMenuKeyBinding());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new StreamerMenuScreen());
                }
            }
        });
    }

    private static KeyBinding createMenuKeyBinding() {
        try {
            // Minecraft 1.21.1–1.21.8 uses a category translation key (String).
            Constructor<KeyBinding> legacy = KeyBinding.class.getConstructor(
                    String.class, InputUtil.Type.class, int.class, String.class
            );
            return legacy.newInstance(
                    "key.streamermod.open_menu",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT_SHIFT,
                    "category.streamermod.general"
            );
        } catch (NoSuchMethodException ignored) {
            // Minecraft 1.21.9+ uses KeyBinding.Category.
            try {
                Class<?> categoryClass = Class.forName(
                        "net.minecraft.client.option.KeyBinding$Category"
                );
                Method create = categoryClass.getMethod("create", Identifier.class);
                Object category = create.invoke(
                        null, Identifier.of("streamermod", "general")
                );
                Constructor<KeyBinding> modern = KeyBinding.class.getConstructor(
                        String.class, InputUtil.Type.class, int.class, categoryClass
                );
                return modern.newInstance(
                        "key.streamermod.open_menu",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_RIGHT_SHIFT,
                        category
                );
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Could not register Streamer Mod keybind", e);
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not register Streamer Mod keybind", e);
        }
    }
}
