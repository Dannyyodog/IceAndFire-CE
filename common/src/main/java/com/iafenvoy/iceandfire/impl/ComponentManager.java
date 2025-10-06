package com.iafenvoy.iceandfire.impl;

import com.iafenvoy.iceandfire.data.component.ChainData;
import com.iafenvoy.iceandfire.data.component.ChickenData;
import com.iafenvoy.iceandfire.data.component.MiscData;
import com.iafenvoy.iceandfire.data.component.PortalData;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ComponentManager {
    @ExpectPlatform
    public static ChainData getChainData(LivingEntity living) {
        throw new AssertionError("This method should be replaced by Architectury.");
    }

    @ExpectPlatform
    public static ChickenData getChickenData(LivingEntity living) {
        throw new AssertionError("This method should be replaced by Architectury.");
    }

    @ExpectPlatform
    public static MiscData getMiscData(LivingEntity living) {
        throw new AssertionError("This method should be replaced by Architectury.");
    }

    @ExpectPlatform
    public static PortalData getPortalData(PlayerEntity player) {
        throw new AssertionError("This method should be replaced by Architectury.");
    }
}
