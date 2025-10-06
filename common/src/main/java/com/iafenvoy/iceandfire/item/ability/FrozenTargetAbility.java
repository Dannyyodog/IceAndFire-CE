package com.iafenvoy.iceandfire.item.ability;

import com.iafenvoy.iceandfire.registry.IafStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public record FrozenTargetAbility(int duration) implements PostHitAbility {
    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, this.duration, 2));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, this.duration, 2));
        target.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(IafStatusEffects.FROZEN.get()), this.duration));
    }
}
