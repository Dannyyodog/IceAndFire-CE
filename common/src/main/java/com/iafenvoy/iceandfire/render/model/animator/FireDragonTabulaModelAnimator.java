package com.iafenvoy.iceandfire.render.model.animator;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.render.model.util.DragonAnimationsLibrary;
import com.iafenvoy.iceandfire.render.model.util.DragonModelTypes;
import com.iafenvoy.iceandfire.render.model.util.DragonPoses;
import com.iafenvoy.uranus.client.model.TabulaModel;

public class FireDragonTabulaModelAnimator extends DragonTabulaModelAnimator {
    public FireDragonTabulaModelAnimator() {
        super(DragonAnimationsLibrary.getModel(DragonPoses.GROUND_POSE, DragonModelTypes.FIRE_DRAGON_MODEL));
        this.walkPoses = new TabulaModel[]{this.getModel(DragonPoses.WALK1), this.getModel(DragonPoses.WALK2), this.getModel(DragonPoses.WALK3), this.getModel(DragonPoses.WALK4)};
        this.flyPoses = new TabulaModel[]{this.getModel(DragonPoses.FLIGHT1), this.getModel(DragonPoses.FLIGHT2), this.getModel(DragonPoses.FLIGHT3), this.getModel(DragonPoses.FLIGHT4), this.getModel(DragonPoses.FLIGHT5), this.getModel(DragonPoses.FLIGHT6)};
        this.swimPoses = new TabulaModel[]{this.getModel(DragonPoses.WALK1), this.getModel(DragonPoses.WALK2), this.getModel(DragonPoses.WALK3), this.getModel(DragonPoses.WALK4)}; //TODO Proper swim animations
    }

    @Override
    protected TabulaModel<DragonBaseEntity> getModelInternal(DragonPoses pose) {
        return DragonAnimationsLibrary.getModel(pose, DragonModelTypes.FIRE_DRAGON_MODEL);
    }
}
