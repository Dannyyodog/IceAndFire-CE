package com.iafenvoy.iceandfire.render.texture;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DragonTextureProvider {
    protected final DragonType dragonType;
    protected final String name;

    public DragonTextureProvider(DragonType dragonType, String name) {
        this.dragonType = dragonType;
        this.name = name;
    }

    @NotNull
    public Identifier getTextureByEntity(DragonBaseEntity dragon) {
        int stage = dragon.getDragonStage();
        if (dragon.isModelDead()) {
            if (dragon.getDeathStage() >= dragon.getAgeInDays() / 10)
                return this.getSkeletonTexture(stage);
            else
                return this.getSleepTexture(stage);
        } else if (dragon.isSleeping() || dragon.isBlinking()) return this.getSleepTexture(stage);
        else return this.getBodyTexture(stage);
    }

    @NotNull
    public Identifier getBodyTexture(int stage) {
        return Identifier.of(IceAndFire.MOD_ID, String.format("textures/entity/%sdragon/%s_%d.png", this.dragonType.name(), this.name, stage));
    }

    @NotNull
    public Identifier getSleepTexture(int stage) {
        return Identifier.of(IceAndFire.MOD_ID, String.format("textures/entity/%sdragon/%s_%d_sleeping.png", this.dragonType.name(), this.name, stage));
    }

    @NotNull
    public Identifier getSkeletonTexture(int stage) {
        return Identifier.of(IceAndFire.MOD_ID, String.format("textures/entity/%sdragon/%s_skeleton_%d.png", this.dragonType.name(), this.dragonType.name(), stage));
    }

    @Nullable
    public Identifier getEyesTexture(int stage) {
        return Identifier.of(IceAndFire.MOD_ID, String.format("textures/entity/%sdragon/%s_%d_eyes.png", this.dragonType.name(), this.name, stage));
    }

    @Nullable
    public Identifier getMaleOverlay() {
        return Identifier.of(IceAndFire.MOD_ID, String.format("textures/entity/%sdragon/male_%s.png", this.dragonType.name(), this.name));
    }

    @NotNull
    public Identifier getEggTexture() {
        return Identifier.of(IceAndFire.MOD_ID, String.format("textures/entity/%sdragon/egg_%s.png", this.dragonType.name(), this.name));
    }
}
