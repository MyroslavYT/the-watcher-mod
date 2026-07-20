package com.thewatcher.entity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
public class WatcherEntityModel extends BipedEntityModel<WatcherEntity> {
    public WatcherEntityModel(ModelPart root){super(root);}
    public static TexturedModelData getTexturedModelData(){
        return TexturedModelData.of(BipedEntityModel.getModelData(Dilation.NONE,0.0F),64,64);
    }
    @Override public void setAngles(WatcherEntity e,float la,float ld,float ap,float hy,float hp){
        super.setAngles(e,la,ld,ap,hy,hp);
        leftLeg.pitch=0f; rightLeg.pitch=0f;
        body.pitch=0.35f; head.pitch=-0.15f;
        leftArm.pitch=-0.25f; rightArm.pitch=-0.25f;
    }
}
