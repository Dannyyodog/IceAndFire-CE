package com.iafenvoy.iceandfire.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleTypes;

public class SirenCharmStatusEffect extends StatusEffect {
    public SirenCharmStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0xFFFFC0CB, ParticleTypes.HEART);
    }
}
