package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.registry.IafToolMaterials;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class StymphalianDaggerItem extends SwordItem {
    public StymphalianDaggerItem() {
        super(IafToolMaterials.STYMHALIAN_SWORD_TOOL_MATERIAL, new Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.STYMHALIAN_SWORD_TOOL_MATERIAL, 3, -1.0F)));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity targetEntity, LivingEntity attacker) {
        return super.postHit(stack, targetEntity, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.iceandfire.legendary_weapon.desc").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.stymphalian_bird_dagger.desc_0").formatted(Formatting.GRAY));
    }
}