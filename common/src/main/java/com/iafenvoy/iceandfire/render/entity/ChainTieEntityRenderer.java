package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.ChainTieEntity;
import com.iafenvoy.iceandfire.render.model.ChainTieModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ChainTieEntityRenderer extends EntityRenderer<ChainTieEntity> {
    private static final Identifier TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/misc/chain_tie.png");
    private final ChainTieModel leashKnotModel = new ChainTieModel();

    public ChainTieEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(ChainTieEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.translate(0, 0.5F, 0);
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        this.leashKnotModel.setAngles(entityIn, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));
        this.leashKnotModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
        matrixStackIn.pop();
    }

    @Override
    public Identifier getTexture(ChainTieEntity entity) {
        return TEXTURE;
    }
}