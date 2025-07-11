package com.iafenvoy.iceandfire.entity.block;

import com.iafenvoy.iceandfire.entity.util.DreadSpawnerBaseLogic;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Spawner;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class BlockEntityDreadSpawner extends BlockEntity implements Spawner {
    private final DreadSpawnerBaseLogic spawner = new DreadSpawnerBaseLogic() {
        @Override
        public void sendStatus(World world, BlockPos pos, int status) {
            world.addSyncedBlockEvent(pos, Blocks.SPAWNER, status, 0);
        }

        @Override
        public void setSpawnEntry(World world, BlockPos pos, MobSpawnerEntry spawnEntry) {
            super.setSpawnEntry(world, pos, spawnEntry);
            if (world != null) {
                BlockState blockstate = world.getBlockState(pos);
                world.updateListeners(pos, blockstate, blockstate, 4);
            }
        }
    };

    public BlockEntityDreadSpawner(BlockPos pos, BlockState state) {
        super(IafBlockEntities.DREAD_SPAWNER.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.spawner.readNbt(this.world, this.pos, nbt);
    }

    public NbtCompound save(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        this.spawner.writeNbt(nbt);
        return nbt;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound compoundtag = this.save(new NbtCompound(), registryLookup);
        compoundtag.remove("SpawnPotentials");
        return compoundtag;
    }

    @Override
    public boolean onSyncedBlockEvent(int p_59797_, int p_59798_) {
        return this.spawner.handleStatus(this.world, p_59797_) || super.onSyncedBlockEvent(p_59797_, p_59798_);
    }

    @Override
    public boolean copyItemDataRequiresOperator() {
        return true;
    }

    public MobSpawnerLogic getLogic() {
        return this.spawner;
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, BlockEntityDreadSpawner blockEntity) {
        blockEntity.spawner.clientTick(world, pos);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, BlockEntityDreadSpawner blockEntity) {
        blockEntity.spawner.serverTick((ServerWorld) world, pos);
    }

    @Override
    public void setEntityType(EntityType<?> type, Random random) {
        this.spawner.setEntityId(type, this.world, random, this.pos);
        this.markDirty();
    }
}