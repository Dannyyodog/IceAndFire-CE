package com.iafenvoy.iceandfire.item.ability.impl;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.item.ability.DamageBonusAbility;
import com.iafenvoy.iceandfire.item.ability.PostHitAbility;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class LightningDragonBloodTool implements PostHitAbility {
    private final DamageBonusAbility damageBonusFire = new DamageBonusAbilityImpl(4.0F, IafEntityTags.FIRE_DRAGON, null);
    private final DamageBonusAbility damageBonusIce = new DamageBonusAbilityImpl(4.0F, IafEntityTags.ICE_DRAGON, null);
    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        this.damageBonusFire.active(stack, target, attacker);
        this.damageBonusIce.active(stack, target, attacker);
        if (this.isEnable()) {
            AbilityImpls.SUMMON_LIGHTNING.active(stack, target, attacker);
        }
    }

    @Override
    public boolean isEnable() {
        return IafCommonConfig.INSTANCE.tools.dragonLightningAbility.getValue();
    }

    @Override
    public void addDescription(List<Text> tooltip) {
        tooltip.add(Text.translatable("dragon_sword_lightning.hurt1").formatted(Formatting.GREEN));
        if (this.isEnable()) {
            tooltip.add(Text.translatable("dragon_sword_lightning.hurt2").formatted(Formatting.DARK_PURPLE));
        }
    }
}
