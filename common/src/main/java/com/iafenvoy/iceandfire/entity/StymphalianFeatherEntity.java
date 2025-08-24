package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class StymphalianFeatherEntity extends PersistentProjectileEntity {
    public StymphalianFeatherEntity(EntityType<? extends PersistentProjectileEntity> t, World worldIn) {
        super(t, worldIn);
    }

    public StymphalianFeatherEntity(EntityType<? extends PersistentProjectileEntity> t, World worldIn, LivingEntity shooter) {
        super(t, worldIn);
        this.setOwner(shooter);
        this.setDamage(IafCommonConfig.INSTANCE.stymphalianBird.featherAttackDamage.getValue());
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (IafCommonConfig.INSTANCE.stymphalianBird.featherDropChance.getValue() > 0) {
            if (this.getWorld().isClient) {
                if (this.random.nextDouble() < IafCommonConfig.INSTANCE.stymphalianBird.featherDropChance.getValue()) {
                    this.dropStack(this.asItemStack(), 0.1F);
                }
            }
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.age > 100) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHit) {
        Entity shootingEntity = this.getOwner();
        if (!(shootingEntity instanceof StymphalianBirdEntity) || entityHit.getEntity() == null || !(entityHit.getEntity() instanceof StymphalianBirdEntity)) {
            super.onEntityHit(entityHit);
            if (entityHit.getEntity() != null && entityHit.getEntity() instanceof StymphalianBirdEntity bird)
                bird.setStuckArrowCount(bird.getStuckArrowCount() - 1);
        }
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(IafItems.STYMPHALIAN_BIRD_FEATHER.get());
    }
}
