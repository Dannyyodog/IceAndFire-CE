package com.iafenvoy.iceandfire.render.item.armor;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.registry.IafArmorMaterials;
import com.iafenvoy.iceandfire.render.model.armor.DeathWormArmorModel;
import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class DeathWormArmorRenderer implements IArmorRendererBase<LivingEntity> {
    @Override
    public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot armorSlot, BipedEntityModel<LivingEntity> bipedEntityModel) {
        return new DeathWormArmorModel(DeathWormArmorModel.getBakedModel(armorSlot == EquipmentSlot.LEGS || armorSlot == EquipmentSlot.HEAD));
    }

    @Override
    public Identifier getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot) {
        ArmorMaterial material = ((ArmorItem) stack.getItem()).getMaterial().value();
        if (material == IafArmorMaterials.DEATHWORM_2_ARMOR_MATERIAL.value())
            return Identifier.of(IceAndFire.MOD_ID, "textures/entity/armor/armor_deathworm_red" + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png"));
        else if (material == IafArmorMaterials.DEATHWORM_1_ARMOR_MATERIAL.value())
            return Identifier.of(IceAndFire.MOD_ID, "textures/entity/armor/armor_deathworm_white" + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png"));
        else
            return Identifier.of(IceAndFire.MOD_ID, "textures/entity/armor/armor_deathworm_yellow" + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png"));
    }
}
