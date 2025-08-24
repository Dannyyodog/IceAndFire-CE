package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.TideTridentEntity;
import com.iafenvoy.iceandfire.render.model.TideTridentModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class TideTridentEntityRenderer extends EntityRenderer<TideTridentEntity> {
    public static final Identifier TRIDENT = Identifier.of(IceAndFire.MOD_ID, "textures/entity/misc/tide_trident.png");
    private final TideTridentModel tridentModel = new TideTridentModel();

    public TideTridentEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(TideTridentEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevYaw, entityIn.getYaw()) - 90.0F));
        matrixStackIn.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevPitch, entityIn.getPitch()) + 90.0F));
        VertexConsumer ivertexbuilder = net.minecraft.client.render.item.ItemRenderer.getItemGlintConsumer(bufferIn, this.tridentModel.getLayer(this.getTexture(entityIn)), false, entityIn.isEnchanted());
        this.tridentModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public Identifier getTexture(TideTridentEntity entity) {
        return TRIDENT;
    }
}