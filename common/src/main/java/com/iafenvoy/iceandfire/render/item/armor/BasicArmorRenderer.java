package com.iafenvoy.iceandfire.render.item.armor;

import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class BasicArmorRenderer implements IArmorRendererBase<LivingEntity> {
    private final Boolean2ObjectFunction<BipedEntityModel<LivingEntity>> modelProvider;

    public BasicArmorRenderer(Boolean2ObjectFunction<BipedEntityModel<LivingEntity>> modelProvider) {
        this.modelProvider = modelProvider;
    }

    @Override
    public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot armorSlot, BipedEntityModel<LivingEntity> bipedEntityModel) {
        return this.modelProvider.get(armorSlot == EquipmentSlot.LEGS || armorSlot == EquipmentSlot.HEAD);
    }
}
