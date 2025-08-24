package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.SirenEntity;
import com.iafenvoy.iceandfire.render.model.SirenModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SirenEntityRenderer extends MobEntityRenderer<SirenEntity, SirenModel> {
    public static final Identifier TEXTURE_0 = Identifier.of(IceAndFire.MOD_ID, "textures/entity/siren/siren_0.png");
    public static final Identifier TEXTURE_0_AGGRESSIVE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/siren/siren_0_aggressive.png");
    public static final Identifier TEXTURE_1 = Identifier.of(IceAndFire.MOD_ID, "textures/entity/siren/siren_1.png");
    public static final Identifier TEXTURE_1_AGGRESSIVE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/siren/siren_1_aggressive.png");
    public static final Identifier TEXTURE_2 = Identifier.of(IceAndFire.MOD_ID, "textures/entity/siren/siren_2.png");
    public static final Identifier TEXTURE_2_AGGRESSIVE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/siren/siren_2_aggressive.png");

    public SirenEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SirenModel(), 0.8F);
    }

    public static Identifier getSirenOverlayTexture(int siren) {
        return switch (siren) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            default -> TEXTURE_0;
        };
    }

    @Override
    public void scale(SirenEntity LivingEntityIn, MatrixStack stack, float partialTickTime) {
        stack.translate(0, 0, -0.5F);
    }

    @Override
    public Identifier getTexture(SirenEntity siren) {
        return switch (siren.getHairColor()) {
            case 1 -> siren.isAgressive() ? TEXTURE_1_AGGRESSIVE : TEXTURE_1;
            case 2 -> siren.isAgressive() ? TEXTURE_2_AGGRESSIVE : TEXTURE_2;
            default -> siren.isAgressive() ? TEXTURE_0_AGGRESSIVE : TEXTURE_0;
        };
    }
}
