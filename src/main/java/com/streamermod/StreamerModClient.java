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
            // Minecraft 1.21.1–1.21.8 uses a String category.
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
            // Newer versions use a category object. Discover its type from
            // the constructor instead of hard-coding a class name that can
            // differ between Minecraft mappings/versions.
            try {
                for (Constructor<?> candidate : KeyBinding.class.getConstructors()) {
                    Class<?>[] parameters = candidate.getParameterTypes();
                    if (parameters.length != 4
                            || parameters[0] != String.class
                            || parameters[1] != InputUtil.Type.class
                            || parameters[2] != int.class
                            || parameters[3] == String.class) {
                        continue;
                    }

                    Class<?> categoryClass = parameters[3];
                    for (Method factory : categoryClass.getMethods()) {
                        if (factory.getName().equals("create")
                                && Modifier.isStatic(factory.getModifiers())
                                && factory.getParameterCount() == 1
                                && factory.getParameterTypes()[0] == Identifier.class) {
                            Object category = factory.invoke(
                                    null, Identifier.of("streamermod", "general")
                            );
                            return (KeyBinding) candidate.newInstance(
                                    "key.streamermod.open_menu",
                                    InputUtil.Type.KEYSYM,
                                    GLFW.GLFW_KEY_RIGHT_SHIFT,
                                    category
                            );
                        }
                    }
                }
                throw new NoSuchMethodException("No compatible KeyBinding constructor/category factory found");
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Could not register Streamer Mod keybind", e);
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not register Streamer Mod keybind", e);
        }
    }
}
