package com.iafenvoy.iceandfire.network;

import com.iafenvoy.iceandfire.config.IafClientConfig;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.util.ISyncMount;
import com.iafenvoy.iceandfire.event.ClientEvents;
import com.iafenvoy.iceandfire.item.block.entity.JarBlockEntity;
import com.iafenvoy.iceandfire.item.block.entity.PixieHouseBlockEntity;
import com.iafenvoy.iceandfire.item.block.entity.PodiumBlockEntity;
import com.iafenvoy.iceandfire.network.payload.*;
import dev.architectury.networking.NetworkManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ClientNetworkHelper {
    private static Perspective prev = Perspective.FIRST_PERSON;

    public static void registerReceivers() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, DragonSetBurnBlockPayload.ID, DragonSetBurnBlockPayload.CODEC, (payload, ctx) -> {
            PlayerEntity player = ctx.getPlayer();
            if (player != null) {
                Entity entity = player.getWorld().getEntityById(payload.entityId());
                if (entity instanceof DragonBaseEntity dragon) {
                    dragon.setBreathingFire(payload.breathing());
                    dragon.burningTarget = new BlockPos(payload.target());
                }
            }
        });
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, LightningBoltS2CPayload.ID, LightningBoltS2CPayload.CODEC, (payload, ctx) -> ctx.queue(() -> ClientEvents.LIGHTNINGS.addAll(payload.lightnings())));
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, StartRidingMobS2CPayload.ID, StartRidingMobS2CPayload.CODEC, (payload, ctx) -> {
            GameOptions options = MinecraftClient.getInstance().options;
            PlayerEntity player = ctx.getPlayer();
            if (player != null) {
                Entity entity = player.getWorld().getEntityById(payload.dragonId());
                if (entity instanceof ISyncMount && entity instanceof TameableEntity tamable) {
                    if (tamable.isOwner(player) && tamable.distanceTo(player) < 14) {
                        if (payload.ride()) {
                            if (payload.baby()) tamable.startRiding(player, true);
                            else {
                                player.startRiding(tamable, true);
                                if (IafClientConfig.INSTANCE.dragonAuto3rdPerson.getValue()) {
                                    prev = options.getPerspective();
                                    options.setPerspective(Perspective.THIRD_PERSON_BACK);
                                }
                            }
                        } else {
                            if (payload.baby()) tamable.stopRiding();
                            else {
                                player.stopRiding();
                                if (IafClientConfig.INSTANCE.dragonAuto3rdPerson.getValue())
                                    options.setPerspective(prev);
                            }
                        }
                    }
                }
            }
        });
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UpdatePixieHousePayload.ID, UpdatePixieHousePayload.CODEC, (payload, ctx) -> {
            PlayerEntity player = ctx.getPlayer();
            if (player != null) {
                BlockEntity blockEntity = player.getWorld().getBlockEntity(payload.blockPos());
                if (blockEntity instanceof PixieHouseBlockEntity house) {
                    house.hasPixie = payload.hasPixie();
                    house.pixieType = payload.pixieType();
                } else if (blockEntity instanceof JarBlockEntity jar) {
                    jar.hasPixie = payload.hasPixie();
                    jar.pixieType = payload.pixieType();
                }
            }
        });
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UpdatePixieJarPayload.ID, UpdatePixieJarPayload.CODEC, (payload, ctx) -> {
            PlayerEntity player = ctx.getPlayer();
            if (player != null)
                if (player.getWorld().getBlockEntity(payload.blockPos()) instanceof JarBlockEntity jar)
                    jar.hasProduced = payload.isProducing();
        });
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UpdatePodiumPayload.ID, UpdatePodiumPayload.CODEC, (payload, ctx) -> {
            PlayerEntity player = ctx.getPlayer();
            if (player != null)
                if (player.getWorld().getBlockEntity(payload.blockPos()) instanceof PodiumBlockEntity podium)
                    podium.setStack(0, payload.heldStack());
        });
    }
}
