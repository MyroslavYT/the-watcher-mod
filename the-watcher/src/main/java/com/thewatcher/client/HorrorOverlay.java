package com.thewatcher.client;
import com.thewatcher.TheWatcherMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
@Environment(EnvType.CLIENT)
public class HorrorOverlay {
    private static final Identifier JUMPSCARE_TEX=new Identifier("thewatcher","textures/gui/jumpscare.png");
    private static final Identifier SKULL_PAINT=new Identifier("thewatcher","textures/gui/skull_painting.png");
    private static int redWorldTicks=0;
    private static int jumpscareTimer=0;
    private static boolean jumpscareActive=false;
    public static boolean paintingsCorrupted=false;

    public static void startRedWorld(int duration){
        redWorldTicks=duration;
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world!=null&&mc.player!=null){
            mc.player.playSound(TheWatcherMod.SOUND_RED_WORLD, SoundCategory.AMBIENT,0.8f,1.0f);
        }
    }

    public static void triggerJumpscare(){
        jumpscareActive=true;
        jumpscareTimer=25;
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world!=null&&mc.player!=null){
            mc.player.playSound(TheWatcherMod.SOUND_JUMPSCARE,SoundCategory.MASTER,1.0f,1.0f);
        }
    }

    public static void render(DrawContext ctx, float tickDelta){
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null||mc.player==null) return;
        int sw=mc.getWindow().getScaledWidth();
        int sh=mc.getWindow().getScaledHeight();

        // Red world tint overlay
        if(redWorldTicks>0){
            redWorldTicks--;
            int alpha=Math.min(180,(int)(80+Math.sin(redWorldTicks*0.1)*40));
            ctx.fill(0,0,sw,sh,((alpha&0xFF)<<24)|(150<<16)|(0<<8)|0);
        }

        // Jumpscare full-screen image
        if(jumpscareActive){
            jumpscareTimer--;
            if(jumpscareTimer>0){
                ctx.drawTexture(JUMPSCARE_TEX,0,0,sw,sh,0,0,1,1,1,1);
            } else {
                jumpscareActive=false;
            }
        }
    }

    public static int getRedWorldTicks(){return redWorldTicks;}
    public static boolean isJumpscareActive(){return jumpscareActive;}
    public static Identifier getSkullPaintingTex(){return SKULL_PAINT;}
}
