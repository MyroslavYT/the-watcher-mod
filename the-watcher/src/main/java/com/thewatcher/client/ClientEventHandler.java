package com.thewatcher.client;
import com.thewatcher.TheWatcherMod;
import com.thewatcher.WatcherNetworking;
import com.thewatcher.entity.WatcherEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
@Environment(EnvType.CLIENT)
public class ClientEventHandler {
    private static int stillTicks=0;
    private static int lookAtWatcherTicks=0;
    private static boolean chatFloodActive=false;
    private static int chatFloodTick=0;
    private static int breathTick=0;
    private static int footstepEchoTick=0;
    private static boolean paintingsCorrupted=false;

    public static void onClientTick(MinecraftClient mc){
        if(mc.world==null||mc.player==null) return;

        // ── Breathing sound when standing still ─────────────
        double vel=mc.player.getVelocity().horizontalLength();
        if(vel<0.01) stillTicks++;
        else stillTicks=0;

        if(stillTicks>200&&breathTick<=0){
            float vol=Math.min(0.5f,stillTicks/800f);
            mc.player.playSound(TheWatcherMod.SOUND_BREATHING,SoundCategory.AMBIENT,vol,1.0f);
            breathTick=60;
        }
        if(breathTick>0) breathTick--;

        // ── Doubled footstep echo ────────────────────────────
        if(vel>0.1&&footstepEchoTick<=0&&mc.world.getRandom().nextInt(80)==0){
            mc.player.playSound(TheWatcherMod.SOUND_FOOTSTEPS_ECHO,SoundCategory.AMBIENT,0.3f,0.95f);
            footstepEchoTick=40;
        }
        if(footstepEchoTick>0) footstepEchoTick--;

        // ── Detect if player is looking at watcher ───────────
        if(mc.targetedEntity instanceof WatcherEntity){
            lookAtWatcherTicks++;
            if(lookAtWatcherTicks>=60){ // 3 seconds
                ClientPlayNetworking.send(WatcherNetworking.PLAYER_LOOKED, PacketByteBufs.empty());
                lookAtWatcherTicks=0;
            }
        } else {
            lookAtWatcherTicks=0;
        }

        // ── Chat flood tick ──────────────────────────────────
        if(chatFloodActive){
            chatFloodTick++;
            if(chatFloodTick%5==0){
                String msg=chatFloodTick<60?"don\'t look":"DON\'T LOOK";
                mc.inGameHud.getChatHud().addMessage(
                    net.minecraft.text.Text.literal(msg)
                        .formatted(net.minecraft.util.Formatting.DARK_RED));
            }
            if(chatFloodTick>100) chatFloodActive=false;
        }
    }

    public static void startChatFlood(){chatFloodActive=true;chatFloodTick=0;}
    public static boolean arePaintingsCorrupted(){return paintingsCorrupted;}
    public static void setPaintingsCorrupted(boolean v){paintingsCorrupted=v;}
    public static void setWatcherActive(boolean v){}
}
