package com.iafenvoy.iceandfire.render;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafClientConfig;
import com.iafenvoy.iceandfire.registry.IafStatusEffects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SirenShaderRenderHelper {
    private static final Identifier SIREN_SHADER = Identifier.of(IceAndFire.MOD_ID, "shaders/post/siren.json");

    public static void tick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        GameRenderer renderer = MinecraftClient.getInstance().gameRenderer;
        if (IafClientConfig.INSTANCE.sirenShader.getValue() && player.hasStatusEffect(Registries.STATUS_EFFECT.getEntry(IafStatusEffects.SIREN_CHARM.get())))
            enableShader(renderer);
        else disableShader(renderer);
    }

    private static boolean enabled(GameRenderer renderer) {
        PostEffectProcessor processor = renderer.getPostProcessor();
        return processor != null && SIREN_SHADER.toString().equals(processor.getName());
    }

    private static void enableShader(GameRenderer renderer) {
        if (enabled(renderer)) return;
        renderer.loadPostProcessor(SIREN_SHADER);
    }

    private static void disableShader(GameRenderer renderer) {
        if (!enabled(renderer)) return;
        renderer.disablePostProcessor();
    }
}
