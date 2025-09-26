package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.block.PixieHouseBlock;
import com.iafenvoy.iceandfire.item.block.entity.PixieHouseBlockEntity;
import com.iafenvoy.iceandfire.render.model.PixieHouseModel;
import com.iafenvoy.iceandfire.render.model.PixieModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class PixieHouseBlockEntityRenderer<T extends PixieHouseBlockEntity> implements BlockEntityRenderer<T> {
    private static final PixieHouseModel MODEL = new PixieHouseModel();
    private static final RenderLayer TEXTURE_0 = RenderLayer.getEntityCutoutNoCull(Identifier.of(IceAndFire.MOD_ID, "textures/entity/pixie/house/pixie_house_0.png"), false);
    private static final RenderLayer TEXTURE_1 = RenderLayer.getEntityCutoutNoCull(Identifier.of(IceAndFire.MOD_ID, "textures/entity/pixie/house/pixie_house_1.png"), false);
    private static final RenderLayer TEXTURE_2 = RenderLayer.getEntityCutoutNoCull(Identifier.of(IceAndFire.MOD_ID, "textures/entity/pixie/house/pixie_house_2.png"), false);
    private static final RenderLayer TEXTURE_3 = RenderLayer.getEntityCutoutNoCull(Identifier.of(IceAndFire.MOD_ID, "textures/entity/pixie/house/pixie_house_3.png"), false);
    private static final RenderLayer TEXTURE_4 = RenderLayer.getEntityCutoutNoCull(Identifier.of(IceAndFire.MOD_ID, "textures/entity/pixie/house/pixie_house_4.png"), false);
    private static final RenderLayer TEXTURE_5 = RenderLayer.getEntityCutoutNoCull(Identifier.of(IceAndFire.MOD_ID, "textures/entity/pixie/house/pixie_house_5.png"), false);
    private final PixieModel pixieModel;
    public BlockItem metaOverride;

    public PixieHouseBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.pixieModel = new PixieModel();
    }

    @Override
    public void render(T entity, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        int rotation = 0;
        int meta = 0;
        if (entity != null && entity.getWorld() != null && entity.getCachedState().getBlock() instanceof PixieHouseBlock) {
            meta = PixieHouseBlockEntity.getHouseTypeFromBlock(entity.getCachedState().getBlock());
            rotation = entity.getCachedState().get(PixieHouseBlock.FACING).getHorizontal() * 90;
        }
        if (entity == null) meta = PixieHouseBlockEntity.getHouseTypeFromBlock(this.metaOverride.getBlock());
        matrixStackIn.push();
        matrixStackIn.translate(0.5F, 1.501F, 0.5F);
        matrixStackIn.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
        matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        if (entity != null && entity.getWorld() != null && entity.hasPixie) {
            matrixStackIn.push();
            matrixStackIn.translate(0F, 0.95F, 0F);
            matrixStackIn.scale(0.55F, 0.55F, 0.55F);
            RenderLayer type = switch (entity.pixieType) {
                case 1 -> JarBlockEntityRenderer.TEXTURE_1;
                case 2 -> JarBlockEntityRenderer.TEXTURE_2;
                case 3 -> JarBlockEntityRenderer.TEXTURE_3;
                case 4 -> JarBlockEntityRenderer.TEXTURE_4;
                case 5 -> JarBlockEntityRenderer.TEXTURE_5;
                default -> JarBlockEntityRenderer.TEXTURE_0;
            };
            RenderLayer type2 = switch (entity.pixieType) {
                case 1 -> JarBlockEntityRenderer.TEXTURE_1_GLO;
                case 2 -> JarBlockEntityRenderer.TEXTURE_2_GLO;
                case 3 -> JarBlockEntityRenderer.TEXTURE_3_GLO;
                case 4 -> JarBlockEntityRenderer.TEXTURE_4_GLO;
                case 5 -> JarBlockEntityRenderer.TEXTURE_5_GLO;
                default -> JarBlockEntityRenderer.TEXTURE_0_GLO;
            };
            matrixStackIn.push();
            this.pixieModel.animateInHouse(entity);
            this.pixieModel.render(matrixStackIn, bufferIn.getBuffer(type), combinedLightIn, combinedOverlayIn, -1);
            this.pixieModel.render(matrixStackIn, bufferIn.getBuffer(type2), combinedLightIn, combinedOverlayIn, -1);
            matrixStackIn.pop();
            matrixStackIn.pop();
        }
        RenderLayer pixieType = switch (meta) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            case 3 -> TEXTURE_3;
            case 4 -> TEXTURE_4;
            case 5 -> TEXTURE_5;
            default -> TEXTURE_0;
        };
        matrixStackIn.push();
        MODEL.render(matrixStackIn, bufferIn.getBuffer(pixieType), combinedLightIn, combinedOverlayIn, -1);
        matrixStackIn.pop();
        matrixStackIn.pop();
    }
}
