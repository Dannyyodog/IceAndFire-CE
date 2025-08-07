package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.data.component.IafEntityData;
import com.iafenvoy.iceandfire.entity.EntityGorgon;
import com.iafenvoy.iceandfire.entity.util.IBlacklistedFromStatues;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class ItemCockatriceScepter extends Item {
    private final Random rand = new Random();
    private int specialWeaponDmg;

    public ItemCockatriceScepter() {
        super(new Settings().maxDamage(700));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.iceandfire.legendary_weapon.desc").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.cockatrice_scepter.desc_0").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.cockatrice_scepter.desc_1").formatted(Formatting.GRAY));
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World worldIn, LivingEntity livingEntity, int timeLeft) {
        if (this.specialWeaponDmg > 0) {
            stack.damage(this.specialWeaponDmg, livingEntity, LivingEntity.getSlotForHand(livingEntity.getActiveHand()));
            this.specialWeaponDmg = 0;
        }
        IafEntityData data = IafEntityData.get(livingEntity);
        data.miscData.getTargetedByScepter().clear();
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player)
            player.getItemCooldownManager().set(this, 20);
        return super.finishUsing(stack, world, user);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack itemStackIn = playerIn.getStackInHand(hand);
        playerIn.setCurrentHand(hand);
        return new TypedActionResult<>(ActionResult.PASS, itemStackIn);
    }

    @Override
    public void usageTick(World level, LivingEntity player, ItemStack stack, int count) {
        if (player instanceof PlayerEntity) {
            double dist = 32;
            Vec3d playerEyePosition = player.getCameraPosVec(1.0F);
            Vec3d playerLook = player.getRotationVec(1.0F);
            Vec3d Vector3d2 = playerEyePosition.add(playerLook.x * dist, playerLook.y * dist, playerLook.z * dist);
            List<Entity> pointedEntities = new LinkedList<>();
            List<Entity> nearbyEntities = level.getOtherEntities(player, player.getBoundingBox().stretch(playerLook.x * dist, playerLook.y * dist, playerLook.z * dist).expand(1, 1, 1), entity -> {
                boolean blindness = entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffects.BLINDNESS) || (entity instanceof IBlacklistedFromStatues blacklisted && !blacklisted.canBeTurnedToStone());
                return entity != null && entity.canHit() && !blindness && (entity instanceof PlayerEntity || (entity instanceof LivingEntity living && DragonUtils.isAlive(living)));
            });
            double d2 = dist;
            for (Entity nearbyEntity : nearbyEntities) {
                Box axisalignedbb = nearbyEntity.getBoundingBox().expand(nearbyEntity.getTargetingMargin());
                Optional<Vec3d> optional = axisalignedbb.raycast(playerEyePosition, Vector3d2);

                if (axisalignedbb.contains(playerEyePosition)) {
                    if (d2 >= 0.0D) {
                        pointedEntities.add(nearbyEntity);
                        d2 = 0.0D;
                    }
                } else if (optional.isPresent()) {
                    double d3 = playerEyePosition.distanceTo(optional.get());
                    if (d3 < d2 || d2 == 0.0D)
                        if (nearbyEntity.getRootVehicle() == player.getRootVehicle()) {
                            if (d2 == 0.0D)
                                pointedEntities.add(nearbyEntity);
                        } else {
                            pointedEntities.add(nearbyEntity);
                            d2 = d3;
                        }
                }

            }
            for (Entity pointedEntity : pointedEntities)
                if (pointedEntity instanceof LivingEntity target) {
                    if (!target.isAlive()) return;
                    IafEntityData data = IafEntityData.get(player);
                    data.miscData.addScepterTarget(target);
                }

            this.attackTargets(player);
        }
    }

    private void attackTargets(final LivingEntity caster) {
        IafEntityData data = IafEntityData.get(caster);
        List<LivingEntity> targets = new ArrayList<>(data.miscData.getTargetedByScepter());
        for (LivingEntity target : targets) {
            if (!EntityGorgon.isEntityLookingAt(caster, target, 0.2F) || caster.isRemoved() || target.isRemoved()) {
                data.miscData.removeScepterTarget(target);
                continue;
            }

            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 40, 2));

            if (caster.age % 20 == 0) {
                this.specialWeaponDmg++;
                target.damage(caster.getWorld().damageSources.wither(), 2);
            }

            this.drawParticleBeam(caster, target);
        }
    }

    private void drawParticleBeam(LivingEntity origin, LivingEntity target) {
        double d5 = 80F;
        double d0 = target.getX() - origin.getX();
        double d1 = target.getY() + (double) (target.getHeight() * 0.5F)
                - (origin.getY() + (double) origin.getStandingEyeHeight() * 0.5D);
        double d2 = target.getZ() - origin.getZ();
        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        d0 = d0 / d3;
        d1 = d1 / d3;
        d2 = d2 / d3;
        double d4 = this.rand.nextDouble();
        while (d4 < d3) {
            d4 += 1.0D;
            origin.getWorld().addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0xFF000000), origin.getX() + d0 * d4, origin.getY() + d1 * d4 + (double) origin.getStandingEyeHeight() * 0.5D, origin.getZ() + d2 * d4, 0.0D, 0.0D, 0.0D);
        }
    }
}
