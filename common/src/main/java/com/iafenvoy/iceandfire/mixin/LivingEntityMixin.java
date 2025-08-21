package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.event.ClientEvents;
import com.iafenvoy.iceandfire.item.ability.impl.AbilityImpls;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
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

    @Environment(EnvType.CLIENT)
    @Inject(method = "tick", at = @At("RETURN"))
    private void onEntityTick(CallbackInfo ci) {
        ClientEvents.LIVING_TICK.invoker().accept((LivingEntity) (Object) this);
    }

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;Z)V", at = @At("HEAD"))
    private void onSwingHand(Hand hand, boolean fromServerPlayer, CallbackInfo ci) {
        if (this.getStackInHand(hand).isIn(IafItemTags.SUMMON_GHOST_SWORD) && AbilityImpls.SUMMON_GHOST_SWORD.isEnable())
            AbilityImpls.SUMMON_GHOST_SWORD.active((LivingEntity) (Object) this);
    }
}
