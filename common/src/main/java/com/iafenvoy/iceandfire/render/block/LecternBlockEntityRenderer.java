package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.block.entity.LecternBlockEntity;
import com.iafenvoy.iceandfire.item.block.LecternBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class LecternBlockEntityRenderer<T extends LecternBlockEntity> implements BlockEntityRenderer<T> {

    private static final RenderLayer ENCHANTMENT_TABLE_BOOK_TEXTURE = RenderLayer.getEntityCutoutNoCull(Identifier.of(IceAndFire.MOD_ID, "textures/entity/lectern_book.png"));
    private final BookModel bookModel;

    public LecternBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.bookModel = new BookModel(context.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public void render(T entity, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.push();
        matrixStackIn.translate(0.5F, 1.1F, 0.5F);
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
        matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(this.getRotation(entity)));
        matrixStackIn.multiply(RotationAxis.POSITIVE_X.rotationDegrees(112.0F));
        matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
        float f4 = entity.pageFlipPrev + (entity.pageFlip - entity.pageFlipPrev) * partialTicks + 0.25F;
        float f5 = entity.pageFlipPrev + (entity.pageFlip - entity.pageFlipPrev) * partialTicks + 0.75F;
        f4 = (f4 - MathHelper.floor(f4)) * 1.6F - 0.3F;
        f5 = (f5 - MathHelper.floor(f5)) * 1.6F - 0.3F;

        if (f4 < 0.0F) f4 = 0.0F;
        if (f5 < 0.0F) f5 = 0.0F;
        if (f4 > 1.0F) f4 = 1.0F;
        if (f5 > 1.0F) f5 = 1.0F;

        float f6 = 1.29F;

        this.bookModel.setPageAngles(partialTicks, MathHelper.clamp(f4, 0.0F, 1.0F), MathHelper.clamp(f5, 0.0F, 1.0F), f6);
        this.bookModel.render(matrixStackIn, bufferIn.getBuffer(ENCHANTMENT_TABLE_BOOK_TEXTURE), combinedLightIn, combinedOverlayIn, -1);
        matrixStackIn.pop();
    }

    private float getRotation(LecternBlockEntity lectern) {
        return switch (lectern.getCachedState().get(LecternBlock.FACING)) {
            case EAST -> 90;
            case WEST -> -90;
            case SOUTH -> 0;
            default -> 180;
        };
    }
}
