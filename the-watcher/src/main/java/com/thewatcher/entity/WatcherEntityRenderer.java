package com.thewatcher.entity;
import com.thewatcher.TheWatcherMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
@Environment(EnvType.CLIENT)
public class WatcherEntityRenderer extends MobEntityRenderer<WatcherEntity,WatcherEntityModel> {
    public static final EntityModelLayer MODEL_LAYER=
        new EntityModelLayer(new Identifier(TheWatcherMod.MOD_ID,"watcher"),"main");
    private static final Identifier TEXTURE=
        new Identifier(TheWatcherMod.MOD_ID,"textures/entity/watcher/watcher.png");
    public WatcherEntityRenderer(EntityRendererFactory.Context ctx){
        super(ctx,new WatcherEntityModel(ctx.getPart(MODEL_LAYER)),0f);
    }
    @Override public Identifier getTexture(WatcherEntity e){return TEXTURE;}
    @Override public void render(WatcherEntity e,float yaw,float d,MatrixStack m,VertexConsumerProvider v,int light){
        m.push(); m.scale(1f,1.15f,1f); super.render(e,yaw,d,m,v,light); m.pop();
    }
}
