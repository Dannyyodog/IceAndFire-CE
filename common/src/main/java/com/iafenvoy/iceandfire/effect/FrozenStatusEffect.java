package com.iafenvoy.iceandfire.effect;

import com.iafenvoy.iceandfire.entity.IceDragonEntity;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class FrozenStatusEffect extends StatusEffect {
    public FrozenStatusEffect() {
        super(StatusEffectCategory.NEUTRAL, 0xFFB9CDF6);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof IceDragonEntity || entity.isDead()) return false;
        else if (entity.isOnFire()) {
            entity.extinguish();
            return false;
        } else if (!(entity instanceof PlayerEntity player && player.isCreative())) {
            entity.setVelocity(entity.getVelocity().multiply(0.25F, 1, 0.25F));
            if (!(entity instanceof EnderDragonEntity) && !entity.isOnGround())
                entity.setVelocity(entity.getVelocity().add(0, -0.2, 0));
        }
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        playSound(entity, SoundEvents.BLOCK_GLASS_PLACE, 1);
    }

    //Vanilla don't have this, so we need to do with mixin
    public void onRemoved(LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld serverWorld)
            for (int i = 0; i < 15; i++)
                serverWorld.spawnParticles(
                        new BlockStateParticleEffect(ParticleTypes.BLOCK, IafBlocks.DRAGON_ICE.get().getDefaultState()),
                        entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * entity.getWidth(),
                        entity.getY() + entity.getRandom().nextDouble() * entity.getHeight(),
                        entity.getZ() + (entity.getRandom().nextDouble() - 0.5D) * entity.getWidth(),
                        0, 0, 0, 0, 0);
        playSound(entity, SoundEvents.BLOCK_GLASS_BREAK, 3);
    }

    private static void playSound(LivingEntity entity, SoundEvent sound, int volume) {
        entity.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundCategory.NEUTRAL, volume, 1);
    }
}
