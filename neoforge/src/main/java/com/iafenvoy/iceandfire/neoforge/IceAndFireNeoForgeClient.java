package com.iafenvoy.iceandfire.neoforge;

import com.iafenvoy.iceandfire.IceAndFireClient;
import com.iafenvoy.iceandfire.config.IafClientConfig;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.registry.IafRenderers;
import com.iafenvoy.jupiter.render.screen.ConfigSelectScreen;
import net.minecraft.text.Text;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(Dist.CLIENT)
public class IceAndFireNeoForgeClient {
    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(IceAndFireClient::process);
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (container, parent) -> new ConfigSelectScreen<>(Text.translatable("config.iceandfire.title"), parent, IafCommonConfig.INSTANCE, IafClientConfig.INSTANCE));
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        IafRenderers.registerParticleRenderers(holder -> holder.applyRegister(event::registerSpecial, event::registerSpriteSet));
    }
}
