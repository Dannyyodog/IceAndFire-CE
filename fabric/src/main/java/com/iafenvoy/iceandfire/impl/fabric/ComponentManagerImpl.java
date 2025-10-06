package com.iafenvoy.iceandfire.impl.fabric;

import com.iafenvoy.iceandfire.data.component.ChainData;
import com.iafenvoy.iceandfire.data.component.ChickenData;
import com.iafenvoy.iceandfire.data.component.MiscData;
import com.iafenvoy.iceandfire.data.component.PortalData;
import com.iafenvoy.iceandfire.fabric.IafAttachments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@SuppressWarnings("UnstableApiUsage")
public class ComponentManagerImpl {
    public static ChainData getChainData(LivingEntity living) {
        return living.getAttachedOrCreate(IafAttachments.CHAIN_DATA);
    }

    public static ChickenData getChickenData(LivingEntity living) {
        return living.getAttachedOrCreate(IafAttachments.CHICKEN_DATA);
    }

    public static MiscData getMiscData(LivingEntity living) {
        return living.getAttachedOrCreate(IafAttachments.MISC_DATA);
    }

    public static PortalData getPortalData(PlayerEntity player) {
        return player.getAttachedOrCreate(IafAttachments.PORTAL_DATA);
    }
}
