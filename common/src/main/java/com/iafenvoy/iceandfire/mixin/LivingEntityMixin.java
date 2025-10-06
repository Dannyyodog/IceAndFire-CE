package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.effect.FrozenStatusEffect;
import com.iafenvoy.iceandfire.event.CommonEvents;
import com.iafenvoy.iceandfire.item.ability.BuiltinAbilities;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract ItemStack getStackInHand(Hand hand);

    @Inject(method = "tick", at = @At("RETURN"))
    private void onEntityTick(CallbackInfo ci) {
        CommonEvents.LIVING_TICK.invoker().accept((LivingEntity) (Object) this);
    }

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;Z)V", at = @At("HEAD"))
    private void onSwingHand(Hand hand, boolean fromServerPlayer, CallbackInfo ci) {
        if (this.getStackInHand(hand).isIn(IafItemTags.SUMMON_GHOST_SWORD) && BuiltinAbilities.SUMMON_GHOST_SWORD.isEnable())
            BuiltinAbilities.SUMMON_GHOST_SWORD.active((LivingEntity) (Object) this);
    }

    @Inject(method = "onStatusEffectRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateAttributes()V"))
    private void handleFrozenEffectRemove(StatusEffectInstance effect, CallbackInfo ci) {
        if (effect.getEffectType().value() instanceof FrozenStatusEffect e) e.onRemoved((LivingEntity) (Object) this);
    }
}
