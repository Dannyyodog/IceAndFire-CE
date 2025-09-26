package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.effect.SirenCharmStatusEffect;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Supplier;

public final class IafStatusEffects {
    public static final DeferredRegister<StatusEffect> REGISTRY = DeferredRegister.create(IceAndFire.MOD_ID, RegistryKeys.STATUS_EFFECT);

    public static final RegistrySupplier<StatusEffect> SIREN_CHARM = register("siren_charm", SirenCharmStatusEffect::new);

    private static <T extends StatusEffect> RegistrySupplier<T> register(String name, Supplier<T> obj) {
        return REGISTRY.register(name, obj);
    }
}
