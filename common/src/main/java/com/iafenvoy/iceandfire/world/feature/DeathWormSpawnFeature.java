package com.iafenvoy.iceandfire.world.feature;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.DeathWormEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.world.GenerationConstants;
import com.mojang.serialization.Codec;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DeathWormSpawnFeature extends Feature<DefaultFeatureConfig> {
    public DeathWormSpawnFeature(Codec<DefaultFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess worldIn = context.getWorld();
        Random rand = context.getRandom();
        BlockPos position = context.getOrigin();
        position = worldIn.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, position.add(8, 0, 8));

        if (GenerationConstants.isFarEnoughFromSpawn(position)) {
            if (rand.nextDouble() < IafCommonConfig.INSTANCE.deathworm.spawnChance.getValue()) {
                DeathWormEntity deathWorm = IafEntities.DEATH_WORM.get().create(worldIn.toServerWorld());
                assert deathWorm != null;
                deathWorm.setPosition(position.getX() + 0.5F, position.getY() + 1, position.getZ() + 0.5F);
                deathWorm.initialize(worldIn, worldIn.getLocalDifficulty(position), SpawnReason.CHUNK_GENERATION, null);
                worldIn.spawnEntity(deathWorm);
            }
        }

        return true;
    }
}
