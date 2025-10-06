package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.uranus.client.model.TabulaModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class DragonEyesFeatureRenderer<T extends DragonBaseEntity> extends FeatureRenderer<T, TabulaModel<T>> {
    public DragonEyesFeatureRenderer(MobEntityRenderer<T, TabulaModel<T>> renderIn) {
        super(renderIn);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, DragonBaseEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headpitch) {
        if (!entity.shouldRenderEyes()) return;
        Identifier eyeTexture = DragonColor.getById(entity.getVariant()).getTextureProvider().getEyesTexture(entity.getDragonStage());
        if (eyeTexture == null) return;
        this.getContextModel().render(matrices, vertexConsumers.getBuffer(RenderLayer.getEyes(eyeTexture)), light, OverlayTexture.DEFAULT_UV, -1);
    }

    @Override
    protected Identifier getTexture(DragonBaseEntity entityIn) {
        return null;
    }
}