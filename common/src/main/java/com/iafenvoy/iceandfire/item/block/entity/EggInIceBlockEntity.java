package com.iafenvoy.iceandfire.item.block.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonEggEntity;
import com.iafenvoy.iceandfire.entity.IceDragonEntity;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class EggInIceBlockEntity extends BlockEntity {
    public DragonColor type;
    public int age;
    public int ticksExisted;
    public UUID ownerUUID;
    // boolean to prevent time in a bottle shenanigans
    private boolean spawned;

    public EggInIceBlockEntity(BlockPos pos, BlockState state) {
        super(IafBlockEntities.EGG_IN_ICE.get(), pos, state);
    }

    public static void tickEgg(World level, BlockPos pos, BlockState state, EggInIceBlockEntity entityEggInIce) {
        entityEggInIce.age++;
        if (entityEggInIce.age >= IafCommonConfig.INSTANCE.dragon.eggBornTime.getValue() && entityEggInIce.type != null && !entityEggInIce.spawned)
            if (!level.isClient) {
                IceDragonEntity dragon = IafEntities.ICE_DRAGON.get().create(level);
                assert dragon != null;
                dragon.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                dragon.setVariant(entityEggInIce.type.name());
                dragon.setGender(ThreadLocalRandom.current().nextBoolean());
                dragon.setTamed(true, false);
                dragon.setHunger(50);
                dragon.setOwnerUuid(entityEggInIce.ownerUUID);
                level.spawnEntity(dragon);
                entityEggInIce.spawned = true;
                level.breakBlock(pos, false);
                level.setBlockState(pos, Blocks.WATER.getDefaultState());
            }
        entityEggInIce.ticksExisted++;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (this.type != null) nbt.putString("Color", this.type.name());
        else nbt.putByte("Color", (byte) 0);
        nbt.putInt("Age", this.age);
        if (this.ownerUUID == null) nbt.putString("OwnerUUID", "");
        else nbt.putUuid("OwnerUUID", this.ownerUUID);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.type = DragonColor.getById(nbt.getString("Color"));
        this.age = nbt.getInt("Age");
        UUID s = null;
        if (nbt.containsUuid("OwnerUUID"))
            s = nbt.getUuid("OwnerUUID");
        else
            try {
                String s1 = nbt.getString("OwnerUUID");
                assert this.world != null;
                s = ServerConfigHandler.getPlayerUuidByName(this.world.getServer(), s1);
            } catch (Exception ignored) {
            }
        if (s != null) this.ownerUUID = s;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbtTagCompound = new NbtCompound();
        this.writeNbt(nbtTagCompound,registryLookup);
        return nbtTagCompound;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        NbtCompound nbtTagCompound = new NbtCompound();
        this.writeNbt(nbtTagCompound,null);
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void spawnEgg() {
        if (this.type != null) {
            DragonEggEntity egg = new DragonEggEntity(IafEntities.DRAGON_EGG.get(), this.world);
            egg.setEggType(this.type);
            egg.setPosition(this.pos.getX() + 0.5, this.pos.getY() + 1, this.pos.getZ() + 0.5);
            egg.setOwnerId(this.ownerUUID);
            assert this.world != null;
            if (!this.world.isClient)
                this.world.spawnEntity(egg);
        }
    }
}
