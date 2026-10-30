package com.streamermod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Constructor;

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
            try {
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
                // Newer Minecraft versions use KeyBinding.Category. Depending
                // on the game version, the constructor has either four or
                // five parameters. Build the category through its public
                // Identifier constructor so this also works after remapping.
                for (Constructor<?> candidate : KeyBinding.class.getConstructors()) {
                    Class<?>[] p = candidate.getParameterTypes();
                    if ((p.length != 4 && p.length != 5)
                            || p[0] != String.class
                            || p[1] != InputUtil.Type.class
                            || p[2] != int.class
                            || p[3] == String.class
                            || (p.length == 5 && p[4] != int.class)) {
                        continue;
                    }

                    Constructor<?> categoryConstructor =
                            p[3].getConstructor(Identifier.class);
                    Object category = categoryConstructor.newInstance(
                            Identifier.of("streamermod", "general")
                    );

                    Object[] args = p.length == 4
                            ? new Object[]{"key.streamermod.open_menu", InputUtil.Type.KEYSYM,
                                    GLFW.GLFW_KEY_RIGHT_SHIFT, category}
                            : new Object[]{"key.streamermod.open_menu", InputUtil.Type.KEYSYM,
                                    GLFW.GLFW_KEY_RIGHT_SHIFT, category, 0};

                    return (KeyBinding) candidate.newInstance(args);
                }
                throw new NoSuchMethodException("No compatible KeyBinding constructor found");
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not register Streamer Mod keybind", e);
        }
    }
}
