package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.CyclopsEntity;
import com.iafenvoy.iceandfire.render.model.CyclopsModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CyclopsEntityRenderer extends MobEntityRenderer<CyclopsEntity, CyclopsModel> {
    public static final Identifier TEXTURE_0 = Identifier.of(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_0.png");
    public static final Identifier BLINK_0_TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_0_blink.png");
    public static final Identifier BLINDED_0_TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_0_injured.png");
    public static final Identifier TEXTURE_1 = Identifier.of(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_1.png");
    public static final Identifier BLINK_1_TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_1_blink.png");
    public static final Identifier BLINDED_1_TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_1_injured.png");
    public static final Identifier TEXTURE_2 = Identifier.of(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_2.png");
    public static final Identifier BLINK_2_TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_2_blink.png");
    public static final Identifier BLINDED_2_TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_2_injured.png");
    public static final Identifier TEXTURE_3 = Identifier.of(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_3.png");
    public static final Identifier BLINK_3_TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_3_blink.png");
    public static final Identifier BLINDED_3_TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_3_injured.png");

    public CyclopsEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new CyclopsModel(), 1.6F);
    }

    @Override
    protected void scale(CyclopsEntity entity, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(2.25F, 2.25F, 2.25F);
    }

    @Override
    public Identifier getTexture(CyclopsEntity cyclops) {
        switch (cyclops.getVariant()) {
            case 0 -> {
                if (cyclops.isBlinded()) return BLINDED_0_TEXTURE;
                else if (cyclops.isBlinking()) return BLINK_0_TEXTURE;
                else return TEXTURE_0;
            }
            case 1 -> {
                if (cyclops.isBlinded()) return BLINDED_1_TEXTURE;
                else if (cyclops.isBlinking()) return BLINK_1_TEXTURE;
                else return TEXTURE_1;
            }
            case 2 -> {
                if (cyclops.isBlinded()) return BLINDED_2_TEXTURE;
                else if (cyclops.isBlinking()) return BLINK_2_TEXTURE;
                else return TEXTURE_2;
            }
            case 3 -> {
                if (cyclops.isBlinded()) return BLINDED_3_TEXTURE;
                else if (cyclops.isBlinking()) return BLINK_3_TEXTURE;
                else return TEXTURE_3;
            }
        }
        return TEXTURE_0;
    }
}
