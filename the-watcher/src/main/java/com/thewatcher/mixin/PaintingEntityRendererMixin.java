package com.thewatcher.mixin;
import com.thewatcher.client.ClientEventHandler;
import com.thewatcher.client.HorrorOverlay;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.PaintingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(PaintingEntityRenderer.class)
public abstract class PaintingEntityRendererMixin {
    @Inject(method="render(Lnet/minecraft/entity/decoration/PaintingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at=@At("HEAD"), cancellable=true)
    private void onRender(PaintingEntity entity, float yaw, float tickDelta,
                          MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                          int light, CallbackInfo ci){
        if(ClientEventHandler.arePaintingsCorrupted()){
            // Cancel normal painting render - paintings go dark/invisible
            // This is the horror effect: all art in your world vanishes
            // To show skull texture instead: implement custom quad render here
            // using HorrorOverlay.getSkullPaintingTex() as the texture
            ci.cancel();
        }
    }
}
