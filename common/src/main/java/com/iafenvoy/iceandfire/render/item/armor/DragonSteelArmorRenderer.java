package com.iafenvoy.iceandfire.render.item.armor;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.registry.IafArmorMaterials;
import com.iafenvoy.iceandfire.render.model.armor.DragonSteelFireArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.DragonSteelIceArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.DragonSteelLightningArmorModel;
import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class DragonSteelArmorRenderer implements IArmorRendererBase<LivingEntity> {
    @Override
    public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot armorSlot, BipedEntityModel<LivingEntity> bipedEntityModel) {
        boolean inner = armorSlot == EquipmentSlot.LEGS || armorSlot == EquipmentSlot.HEAD;
        if (itemStack.getItem() instanceof ArmorItem armorItem) {
            ArmorMaterial armorMaterial = armorItem.getMaterial().value();
            if (IafArmorMaterials.DRAGONSTEEL_FIRE_ARMOR_MATERIAL.value().equals(armorMaterial))
                return new DragonSteelFireArmorModel(inner);
            if (IafArmorMaterials.DRAGONSTEEL_ICE_ARMOR_MATERIAL.value().equals(armorMaterial))
                return new DragonSteelIceArmorModel(inner);
            if (IafArmorMaterials.DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL.value().equals(armorMaterial))
                return new DragonSteelLightningArmorModel(inner);
        }
        return null;
    }

    @Override
    public Identifier getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot) {
        ArmorMaterial material = ((ArmorItem) stack.getItem()).getMaterial().value();
        if (material == IafArmorMaterials.DRAGONSTEEL_FIRE_ARMOR_MATERIAL.value())
            return Identifier.of(IceAndFire.MOD_ID, "textures/entity/armor/armor_dragonsteel_fire" + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png"));
        else if (material == IafArmorMaterials.DRAGONSTEEL_ICE_ARMOR_MATERIAL.value())
            return Identifier.of(IceAndFire.MOD_ID, "textures/entity/armor/armor_dragonsteel_ice" + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png"));
        else
            return Identifier.of(IceAndFire.MOD_ID, "textures/entity/armor/armor_dragonsteel_lightning" + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png"));
    }
}
