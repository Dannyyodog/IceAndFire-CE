package com.iafenvoy.iceandfire.entity;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.util.BlacklistedFromStatues;
import com.iafenvoy.iceandfire.entity.util.IDeadMob;
import com.iafenvoy.iceandfire.item.DragonEggItem;
import com.iafenvoy.iceandfire.item.block.entity.EggInIceBlockEntity;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafDragonColors;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.uranus.object.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DragonEggEntity extends LivingEntity implements BlacklistedFromStatues, IDeadMob {
    protected static final TrackedData<Optional<UUID>> OWNER_UNIQUE_ID = DataTracker.registerData(DragonEggEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final Map<DragonType, EggTicker> TICKERS = new LinkedHashMap<>();
    private static final TrackedData<String> DRAGON_TYPE = DataTracker.registerData(DragonEggEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> DRAGON_AGE = DataTracker.registerData(DragonEggEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> LOCATION_VALID = DataTracker.registerData(DragonEggEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public DragonEggEntity(EntityType<DragonEggEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D)
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0D);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putString("Color", this.getEggType().getName());
        tag.putInt("DragonAge", this.getDragonAge());
        try {
            if (this.getOwnerId() == null) tag.putString("OwnerUUID", "");
            else tag.putString("OwnerUUID", this.getOwnerId().toString());
        } catch (Exception e) {
            IceAndFire.LOGGER.error("An error occurred while trying to read the NBT data of a dragon egg", e);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        this.setEggType(DragonColor.getById(tag.getString("Color")));
        this.setDragonAge(tag.getInt("DragonAge"));
        String s;

        if (tag.contains("OwnerUUID", 8)) s = tag.getString("OwnerUUID");
        else {
            String s1 = tag.getString("Owner");
            UUID converedUUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), s1);
            s = converedUUID == null ? s1 : converedUUID.toString();
        }
        if (!s.isEmpty()) this.setOwnerId(UUID.fromString(s));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(DRAGON_TYPE, IafDragonColors.RED.toString());
        builder.add(DRAGON_AGE, 0);
        builder.add(OWNER_UNIQUE_ID, Optional.empty());
        builder.add(LOCATION_VALID, false);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource i) {
        return i.getAttacker() != null && super.isInvulnerableTo(i);
    }

    public DragonColor getEggType() {
        return DragonColor.getById(this.getDataTracker().get(DRAGON_TYPE));
    }

    public void setEggType(DragonColor color) {
        this.getDataTracker().set(DRAGON_TYPE, color.getName());
    }

    public int getDragonAge() {
        return this.getDataTracker().get(DRAGON_AGE);
    }

    public void setDragonAge(int i) {
        this.getDataTracker().set(DRAGON_AGE, i);
    }

    public UUID getOwnerId() {
        return this.dataTracker.get(OWNER_UNIQUE_ID).orElse(null);
    }

    public void setOwnerId(UUID uuid) {
        this.dataTracker.set(OWNER_UNIQUE_ID, Optional.ofNullable(uuid));
    }

    public boolean isLocationValid() {
        return this.dataTracker.get(LOCATION_VALID);
    }

    public void setLocationValid(boolean valid) {
        this.dataTracker.set(LOCATION_VALID, valid);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient && !this.isRemoved()) {
            this.setAir(200);
            this.updateEggCondition();
        }
    }

    public void updateEggCondition() {
        DragonType dragonType = this.getEggType().getType();
        EggTicker ticker = TICKERS.get(dragonType);
        boolean hatched = this.getDragonAge() > IafCommonConfig.INSTANCE.dragon.eggBornTime.getValue();
        if (ticker != null) this.setLocationValid(ticker.tick(this, this.getWorld(), this.getBlockPos(), hatched));

        if (hatched) {
            this.getWorld().setBlockState(this.getBlockPos(), Blocks.AIR.getDefaultState());
            DragonBaseEntity dragon = dragonType.createEntity(this.getWorld());
            assert dragon != null;
            dragon.setVariant(this.getEggType().getName());
            dragon.setGender(this.getRandom().nextBoolean());
            dragon.setPosition(this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 1, this.getBlockPos().getZ() + 0.5);
            dragon.setHunger(50);
            if (!this.getWorld().isClient()) this.getWorld().spawnEntity(dragon);
            if (this.hasCustomName()) dragon.setCustomName(this.getCustomName());
            dragon.setTamed(true, true);
            dragon.setOwnerUuid(this.getOwnerId());
            this.getWorld().playSound(this.getX(), this.getY() + this.getStandingEyeHeight(), this.getZ(), IafSounds.EGG_HATCH.get(), this.getSoundCategory(), 2.5F, 1.0F, false);
            this.discard();
        }
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return null;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return ImmutableList.of();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slotIn, ItemStack stack) {

    }

    @Override
    public boolean damage(DamageSource var1, float var2) {
        if (var1.isIn(DamageTypeTags.IS_FIRE) && this.getEggType().getType() == IafDragonTypes.FIRE)
            return false;
        if (!this.getWorld().isClient && !var1.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && !this.isRemoved()) {
            this.dropItem(this.getItem().getItem(), 1);
        }
        this.remove(RemovalReason.KILLED);
        return true;
    }

    private ItemStack getItem() {
        return new ItemStack(DragonEggItem.EGGS.getOrDefault(this.getEggType(), Items.AIR));
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    @Override
    protected void pushAway(Entity entity) {
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }

    public void onPlayerPlace(PlayerEntity player) {
        this.setOwnerId(player.getUuid());
    }

    @Override
    public boolean isMobDead() {
        return true;
    }

    public static void register(DragonType type, EggTicker ticker) {
        TICKERS.put(type, ticker);
    }

    static {
        register(IafDragonTypes.FIRE, (entity, world, pos, hatched) -> {
            boolean valid = BlockUtil.isBurning(world.getBlockState(pos));
            if (valid) entity.setDragonAge(entity.getDragonAge() + 1);
            if (hatched)
                world.playSound(entity.getX(), entity.getY() + entity.getStandingEyeHeight(), entity.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, entity.getSoundCategory(), 2.5F, 1.0F, false);
            return valid;
        });
        register(IafDragonTypes.ICE, (entity, world, pos, hatched) -> {
            BlockState state = world.getBlockState(pos);
            if (state.isOf(Blocks.WATER) && entity.getRandom().nextInt(500) == 0) {
                world.setBlockState(pos, IafBlocks.EGG_IN_ICE.get().getDefaultState());
                world.playSound(entity.getX(), entity.getY() + entity.getStandingEyeHeight(), entity.getZ(), SoundEvents.BLOCK_GLASS_BREAK, entity.getSoundCategory(), 2.5F, 1.0F, false);
                if (world.getBlockEntity(pos) instanceof EggInIceBlockEntity eggInIce) {
                    eggInIce.type = entity.getEggType();
                    eggInIce.ownerUUID = entity.getOwnerId();
                }
                entity.remove(RemovalReason.DISCARDED);
            }
            return false;
        });
        register(IafDragonTypes.LIGHTNING, (entity, world, pos, hatched) -> {
            boolean isRainingAt = world.hasRain(pos) || world.hasRain(BlockPos.ofFloored(entity.getX(), entity.getY() + entity.getHeight(), entity.getZ()));
            boolean valid = world.isSkyVisible(pos.up()) && isRainingAt;
            if (valid) entity.setDragonAge(entity.getDragonAge() + 1);
            if (hatched) {
                LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
                assert bolt != null;
                bolt.setPosition(entity.getX(), entity.getY(), entity.getZ());
                bolt.setCosmetic(true);
                if (!world.isClient) world.spawnEntity(bolt);
                world.playSound(entity.getX(), entity.getY() + entity.getStandingEyeHeight(), entity.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, entity.getSoundCategory(), 2.5F, 1.0F, false);
            }
            return valid;
        });
    }

    @FunctionalInterface
    public interface EggTicker {
        boolean tick(DragonEggEntity entity, World world, BlockPos pos, boolean hatched);
    }
}
