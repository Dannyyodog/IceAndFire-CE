package com.iafenvoy.iceandfire.render.item.armor;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.item.armor.DragonScaleArmorItem;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.render.model.armor.FireDragonScaleArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.IceDragonScaleArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.LightningDragonScaleArmorModel;
import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScaleArmorRenderer implements IArmorRendererBase<LivingEntity> {
    private static final Map<DragonType, Boolean2ObjectFunction<BipedEntityModel<LivingEntity>>> MODEL_BY_TYPE = new LinkedHashMap<>();

    @Override
    public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot armorSlot, BipedEntityModel<LivingEntity> bipedEntityModel) {
        return itemStack.getItem() instanceof DragonScaleArmorItem scaleArmor ? MODEL_BY_TYPE.getOrDefault(scaleArmor.getColor().getType(), b -> null).get(armorSlot == EquipmentSlot.LEGS || armorSlot == EquipmentSlot.HEAD) : null;
    }

    public static void register(DragonType type, Boolean2ObjectFunction<BipedEntityModel<LivingEntity>> model) {
        MODEL_BY_TYPE.put(type, model);
    }

    static {
        register(IafDragonTypes.FIRE, FireDragonScaleArmorModel::new);
        register(IafDragonTypes.ICE, IceDragonScaleArmorModel::new);
        register(IafDragonTypes.LIGHTNING, LightningDragonScaleArmorModel::new);
    }
}
