package com.iafenvoy.iceandfire.neoforge;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.IceAndFireClient;
import com.iafenvoy.iceandfire.neoforge.compat.IceAndFireArsNouveauCompat;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.integration.IntegrationExecutor;
import dev.architectury.platform.Platform;
import net.minecraft.potion.Potions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@Mod(IceAndFire.MOD_ID)
@EventBusSubscriber
public final class IceAndFireNeoForge {
    public IceAndFireNeoForge(IEventBus modBus) {
        IafAttachments.REGISTRY.register(modBus);
        IceAndFire.init();
        if (Platform.getEnv() == Dist.CLIENT)
            IceAndFireClient.init();
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(IceAndFire::process);
        IntegrationExecutor.runWhenLoad("ars_nouveau", () -> IceAndFireArsNouveauCompat::init);
    }

    @SubscribeEvent
    public static void registerBrewing(RegisterBrewingRecipesEvent event) {
        event.getBuilder().registerPotionRecipe(Potions.WATER, IafItems.SHINY_SCALES.get(), Potions.WATER_BREATHING);
    }
}
