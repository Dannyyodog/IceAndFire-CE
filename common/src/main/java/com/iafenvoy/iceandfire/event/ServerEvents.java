package com.iafenvoy.iceandfire.event;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.component.ChainData;
import com.iafenvoy.iceandfire.entity.*;
import com.iafenvoy.iceandfire.entity.ai.EntitySheepAIFollowCyclopsGoal;
import com.iafenvoy.iceandfire.entity.ai.VillagerAIFearUntamedGoal;
import com.iafenvoy.iceandfire.entity.util.IAnimalFear;
import com.iafenvoy.iceandfire.entity.util.IVillagerFear;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.item.ChainItem;
import com.iafenvoy.iceandfire.item.DragonHornItem;
import com.iafenvoy.iceandfire.item.armor.DragonSteelArmorItem;
import com.iafenvoy.iceandfire.item.armor.DragonScaleArmorItem;
import com.iafenvoy.iceandfire.item.armor.TrollArmorItem;
import com.iafenvoy.iceandfire.item.component.StoneStatusComponent;
import com.iafenvoy.iceandfire.network.payload.PlayerHitMultipartC2SPayload;
import com.iafenvoy.iceandfire.registry.*;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import com.iafenvoy.uranus.object.RegistryHelper;
import com.iafenvoy.uranus.util.RandomHelper;
import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.value.IntValue;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public final class ServerEvents {
    public static final UUID ALEX_UUID = UUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c");
    public static final String BOLT_DONT_DESTROY_LOOT = "iceandfire.bolt_skip_loot";
    private static final Predicate<LivingEntity> VILLAGER_FEAR = entity -> entity instanceof IVillagerFear fear && fear.shouldFear();

    private static void signalChickenAlarm(LivingEntity chicken, LivingEntity attacker) {
        final float d0 = IafCommonConfig.INSTANCE.cockatrice.chickenSearchLength.getValue();
        final List<CockatriceEntity> list = chicken.getWorld().getNonSpectatingEntities(CockatriceEntity.class, (new Box(chicken.getX(), chicken.getY(), chicken.getZ(), chicken.getX() + 1.0D, chicken.getY() + 1.0D, chicken.getZ() + 1.0D)).expand(d0, 10.0D, d0));
        if (list.isEmpty()) return;

        for (final CockatriceEntity cockatrice : list) {
            if (!(attacker instanceof CockatriceEntity)) {
                if (!DragonUtils.hasSameOwner(cockatrice, attacker)) {
                    if (attacker instanceof PlayerEntity player) {
                        if (!player.isCreative() && !cockatrice.isOwner(player))
                            cockatrice.setTarget(player);
                    } else cockatrice.setTarget(attacker);
                }
            }
        }
    }

    private static void signalAmphithereAlarm(LivingEntity villager, LivingEntity attacker) {
        final float d0 = IafCommonConfig.INSTANCE.amphithere.villagerSearchLength.getValue().floatValue();
        final List<AmphithereEntity> list = villager.getWorld().getNonSpectatingEntities(AmphithereEntity.class, (new Box(villager.getX() - 1.0D, villager.getY() - 1.0D, villager.getZ() - 1.0D, villager.getX() + 1.0D, villager.getY() + 1.0D, villager.getZ() + 1.0D)).expand(d0, d0, d0));
        if (list.isEmpty()) return;

        for (final Entity entity : list) {
            if (entity instanceof AmphithereEntity amphithere && !(attacker instanceof AmphithereEntity)) {
                if (!DragonUtils.hasSameOwner(amphithere, attacker)) {
                    if (attacker instanceof PlayerEntity player) {
                        if (!player.isCreative() && !amphithere.isOwner(player))
                            amphithere.setTarget(player);
                    } else amphithere.setTarget(attacker);
                }
            }
        }
    }

    public static boolean isRidingOrBeingRiddenBy(final Entity first, final Entity entityIn) {
        if (first == null || entityIn == null) return false;
        for (final Entity entity : first.getPassengerList())
            if (entity.equals(entityIn) || isRidingOrBeingRiddenBy(entity, entityIn))
                return true;
        return false;
    }

    public static float onEntityDamage(LivingEntity entity, DamageSource source, float amount) {
        if (source.isIn(DamageTypeTags.IS_PROJECTILE)) {
            float multi = 1;
            if (entity.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof TrollArmorItem)
                multi -= 0.1f;
            if (entity.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof TrollArmorItem)
                multi -= 0.3f;
            if (entity.getEquippedStack(EquipmentSlot.LEGS).getItem() instanceof TrollArmorItem)
                multi -= 0.2f;
            if (entity.getEquippedStack(EquipmentSlot.FEET).getItem() instanceof TrollArmorItem)
                multi -= 0.1f;
            amount *= multi;
        }
        if (source.isOf(IafDamageTypes.DRAGON_FIRE_TYPE) || source.isOf(IafDamageTypes.DRAGON_ICE_TYPE) || source.isOf(IafDamageTypes.DRAGON_LIGHTNING_TYPE)) {
            float multi = 1;
            if (entity.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof DragonScaleArmorItem ||
                    entity.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof DragonSteelArmorItem)
                multi -= 0.1f;
            if (entity.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof DragonScaleArmorItem ||
                    entity.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof DragonSteelArmorItem)
                multi -= 0.3f;
            if (entity.getEquippedStack(EquipmentSlot.LEGS).getItem() instanceof DragonScaleArmorItem ||
                    entity.getEquippedStack(EquipmentSlot.LEGS).getItem() instanceof DragonSteelArmorItem)
                multi -= 0.2f;
            if (entity.getEquippedStack(EquipmentSlot.FEET).getItem() instanceof DragonScaleArmorItem ||
                    entity.getEquippedStack(EquipmentSlot.FEET).getItem() instanceof DragonSteelArmorItem)
                multi -= 0.1f;
            amount *= multi;
        }
        return amount;
    }

    public static void onLivingSetTarget(Entity tracking, ServerPlayerEntity player) {
        if (tracking instanceof LivingEntity target) {
            if (target.getType().isIn(IafEntityTags.CHICKENS)) signalChickenAlarm(target, player);
            else if (DragonUtils.isVillager(target)) signalAmphithereAlarm(target, player);
        }
    }

    public static EventResult onPlayerAttack(PlayerEntity player, World world, Entity entity, Hand hand, @Nullable EntityHitResult hitResult) {
        if (entity != null && entity.getType().isIn(IafEntityTags.SHEEP)) {
            float dist = IafCommonConfig.INSTANCE.cyclops.sheepSearchLength.getValue();
            final List<Entity> list = entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(dist, dist, dist));
            if (!list.isEmpty())
                for (final Entity e : list)
                    if (e instanceof CyclopsEntity cyclops)
                        if (!cyclops.isBlinded() && !player.isCreative())
                            cyclops.setTarget(player);
        }
        if (entity instanceof StoneStatueEntity statue) {
            statue.setHealth(statue.getMaxHealth());
            if (player != null) {
                ItemStack stack = player.getMainHandStack();
                entity.playSound(SoundEvents.BLOCK_STONE_BREAK, 2, 0.5F + (float) (RandomHelper.nextDouble(-1, 1) * 0.2 + 0.5));

                if (stack.isIn(ItemTags.PICKAXES)) {
                    statue.setCrackAmount(statue.getCrackAmount() + 1);

                    if (statue.getCrackAmount() > 9) {
                        NbtCompound writtenTag = new NbtCompound();
                        entity.writeNbt(writtenTag);
                        entity.playSound(SoundEvents.BLOCK_STONE_BREAK, 2F, (float) (RandomHelper.nextDouble(-1, 1) * 0.2 + 0.5));
                        entity.remove(Entity.RemovalReason.KILLED);

                        if (EnchantmentHelper.getLevel(RegistryHelper.getEnchantment(world.getRegistryManager(), Enchantments.SILK_TOUCH), stack) > 0) {
                            ItemStack statuette = new ItemStack(IafItems.STONE_STATUE.get());
                            statuette.set(IafDataComponents.STONE_STATUS.get(), new StoneStatusComponent(statue.getTrappedEntityTypeString().equalsIgnoreCase("minecraft:player"), statue.getTrappedEntityTypeString(), writtenTag));
                            if (!statue.getWorld().isClient)
                                statue.dropStack(statuette, 1);
                        } else if (!statue.getWorld().isClient)
                            statue.dropItem(Blocks.COBBLESTONE, 2 + player.getRandom().nextInt(4));

                        statue.remove(Entity.RemovalReason.KILLED);
                    }
                    return EventResult.interruptTrue();
                }
            }
            return EventResult.interruptDefault();
        }
        if (entity instanceof MultipartPartEntity mutlipartPart) {
            Entity parent = mutlipartPart.getParent();
            try {
                //If the attacked entity is the parent itself parent will be null and also doesn't have to be attacked
                if (parent != null)
                    player.attack(parent);
            } catch (Exception e) {
                IceAndFire.LOGGER.warn("Exception thrown while interacting with entity.", e);
            }
            int extraData = 0;
            if (mutlipartPart instanceof HydraHeadEntity hydraHead && parent instanceof HydraEntity hydra) {
                extraData = hydraHead.headIndex;
                hydra.triggerHeadFlags(extraData);
            }
            if (mutlipartPart.getWorld().isClient && parent != null)
                NetworkManager.sendToServer(new PlayerHitMultipartC2SPayload(parent.getId(), extraData));
        }
        if (entity instanceof LivingEntity livingEntity) {
            if (entity.getType().isIn(IafEntityTags.CHICKENS)) signalChickenAlarm(livingEntity, player);
            else if (DragonUtils.isVillager(entity)) signalAmphithereAlarm(livingEntity, player);
        }
        return EventResult.pass();
    }

    public static EventResult onEntityDie(LivingEntity entity, DamageSource damageSource) {
        if (entity.getWorld().isClient) return EventResult.pass();

        ChainData chainData = ChainData.get(entity);
        if (!chainData.getChainedTo().isEmpty()) {
            ItemEntity entityitem = new ItemEntity(entity.getWorld(),
                    entity.getX(),
                    entity.getY() + 1,
                    entity.getZ(),
                    new ItemStack(IafItems.CHAIN.get(), chainData.getChainedTo().size()));
            entityitem.setToDefaultPickupDelay();
            entity.getWorld().spawnEntity(entityitem);

            chainData.clearChains();
        }

        if (entity.getUuid().equals(ServerEvents.ALEX_UUID))
            entity.dropStack(new ItemStack(IafItems.WEEZER_BLUE_ALBUM.get()), 1);

        if (entity instanceof PlayerEntity) {
            if (IafCommonConfig.INSTANCE.ghost.fromPlayerDeaths.getValue()) {
                Entity attacker = entity.getAttacker();
                if (attacker instanceof PlayerEntity && entity.getRandom().nextInt(3) == 0) {
                    DamageTracker combat = entity.getDamageTracker();
                    DamageRecord entry = combat.getBiggestFall();
                    boolean flag = entry != null && (entry.damageSource().isOf(DamageTypes.FALL) || entry.damageSource().isOf(DamageTypes.DROWN) || entry.damageSource().isOf(DamageTypes.LAVA));
                    if (entity.hasStatusEffect(StatusEffects.POISON))
                        flag = true;
                    if (flag) {
                        World world = entity.getWorld();
                        GhostEntity ghost = IafEntities.GHOST.get().create(world);
                        assert ghost != null;
                        ghost.copyPositionAndRotation(entity);
                        if (world instanceof ServerWorldAccess serverWorldAccess) {
                            ghost.initialize(serverWorldAccess, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.SPAWNER, null);
                            world.spawnEntity(ghost);
                        }
                        ghost.setDaytimeMode(true);
                    }
                }
            }
        }
        return EventResult.pass();
    }

    public static EventResult onEntityInteract(PlayerEntity player, Entity entity, Hand hand) {
        // Handle chain removal
        if (entity instanceof LivingEntity target && !player.isSpectator()) {
            ChainData chainData = ChainData.get(target);
            if (chainData.isChainedTo(entity.getUuid())) {
                chainData.removeChain(entity.getUuid());
                if (!player.getWorld().isClient)
                    entity.dropItem(IafItems.CHAIN.get(), 1);
                return EventResult.interruptTrue();
            }
        }
        // Handle multipart
        if (entity instanceof MultipartPartEntity multipart) {
            //FIXME::Don't run in item class?
            // Handle some dragon items
            if (player.getStackInHand(hand).getItem() instanceof DragonHornItem horn && multipart.getParent() instanceof LivingEntity living)
                horn.useOnEntity(player.getStackInHand(hand), player, living, hand);
        }
        return EventResult.pass();
    }

    public static EventResult onPlayerRightClick(PlayerEntity player, Hand hand, BlockPos pos, Direction face) {
        World world = player.getWorld();
        if (world.getBlockState(pos).getBlock() instanceof AbstractChestBlock && !player.isCreative()) {
            float dist = IafCommonConfig.INSTANCE.dragon.goldSearchLength.getValue();
            final List<Entity> list = world.getOtherEntities(player, player.getBoundingBox().expand(dist, dist, dist));
            if (!list.isEmpty())
                for (final Entity entity : list)
                    if (entity instanceof DragonBaseEntity dragon)
                        if (!dragon.isTamed() && !dragon.isModelDead() && !dragon.isOwner(player)) {
                            dragon.setInSittingPose(false);
                            dragon.setSitting(false);
                            dragon.setTarget(player);
                        }
        }
        if (world.getBlockState(pos).getBlock() instanceof WallBlock)
            ChainItem.attachToFence(player, world, pos);
        return EventResult.pass();
    }

    public static EventResult onBreakBlock(World world, BlockPos pos, BlockState state, PlayerEntity player, @Nullable IntValue xp) {
        if (player != null && (state.getBlock() instanceof AbstractChestBlock || state.isOf(IafBlocks.GOLD_PILE.get()) || state.isOf(IafBlocks.SILVER_PILE.get()) || state.isOf(IafBlocks.COPPER_PILE.get()))) {
            final float dist = IafCommonConfig.INSTANCE.dragon.goldSearchLength.getValue();
            List<Entity> list = world.getOtherEntities(player, player.getBoundingBox().expand(dist, dist, dist));
            if (list.isEmpty()) return EventResult.pass();

            for (Entity entity : list)
                if (entity instanceof DragonBaseEntity dragon)
                    if (!dragon.isTamed() && !dragon.isModelDead() && !dragon.isOwner(player) && !player.isCreative()) {
                        dragon.setInSittingPose(false);
                        dragon.setSitting(false);
                        dragon.setTarget(player);
                    }
        }
        return EventResult.pass();
    }

    public static void onPlayerLeaveEvent(PlayerEntity player) {
        if (player != null && !player.getPassengerList().isEmpty())
            for (Entity entity : player.getPassengerList())
                entity.stopRiding();
    }

    public static boolean onEntityJoinWorld(Entity entity, World world) {
        if (entity instanceof MobEntity mob)
            try {
                if (mob.getType().isIn(IafEntityTags.SHEEP) && mob instanceof AnimalEntity animal)
                    animal.goalSelector.add(8, new EntitySheepAIFollowCyclopsGoal(animal, 1.2D));
                if (mob.getType().isIn(IafEntityTags.VILLAGERS))
                    if (IafCommonConfig.INSTANCE.dragon.villagersFear.getValue())
                        mob.goalSelector.add(1, new VillagerAIFearUntamedGoal((PathAwareEntity) mob, LivingEntity.class, 8.0F, 0.8D, 0.8D, VILLAGER_FEAR));
                if (mob.getType().isIn(IafEntityTags.FEAR_DRAGONS))
                    if (IafCommonConfig.INSTANCE.dragon.animalsFear.getValue())
                        mob.goalSelector.add(1, new VillagerAIFearUntamedGoal((PathAwareEntity) mob, LivingEntity.class, 30, 1.0D, 0.5D, e -> e instanceof IAnimalFear fear && fear.shouldAnimalsFear(mob)));
            } catch (Exception e) {
                IceAndFire.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
            }
        return true;
    }

    public static EventResult onLivingHurt(LivingEntity entity, DamageSource source, float amount) {
        if (source.isOf(DamageTypes.LIGHTNING_BOLT) &&
                entity.getEquippedStack(EquipmentSlot.HEAD).isOf(IafItems.DRAGONSTEEL_LIGHTNING_HELMET.get()) &&
                entity.getEquippedStack(EquipmentSlot.CHEST).isOf(IafItems.DRAGONSTEEL_LIGHTNING_CHESTPLATE.get()) &&
                entity.getEquippedStack(EquipmentSlot.LEGS).isOf(IafItems.DRAGONSTEEL_LIGHTNING_LEGGINGS.get()) &&
                entity.getEquippedStack(EquipmentSlot.FEET).isOf(IafItems.DRAGONSTEEL_LIGHTNING_BOOTS.get()))
            return EventResult.interruptFalse();
        return EventResult.pass();
    }
}
