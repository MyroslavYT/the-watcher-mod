package com.thewatcher;
import com.thewatcher.client.ClientEventHandler;
import com.thewatcher.client.HorrorOverlay;
import com.thewatcher.event.EventManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
public class WatcherNetworking {
    public static final Identifier RED_WORLD     = new Identifier(TheWatcherMod.MOD_ID,"red_world");
    public static final Identifier JUMPSCARE     = new Identifier(TheWatcherMod.MOD_ID,"jumpscare");
    public static final Identifier CHAT_FLOOD    = new Identifier(TheWatcherMod.MOD_ID,"chat_flood");
    public static final Identifier PLAYER_LOOKED = new Identifier(TheWatcherMod.MOD_ID,"player_looked");
    public static final Identifier PAINTINGS     = new Identifier(TheWatcherMod.MOD_ID,"paintings");

    public static void register(){
        ServerPlayNetworking.registerGlobalReceiver(PLAYER_LOOKED,(server,player,handler,buf,res)->
            server.execute(()->EventManager.onPlayerLookedAtWatcher(player)));
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient(){
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
            .registerGlobalReceiver(RED_WORLD,(client,handler,buf,res)->{
                int dur=buf.readInt(); client.execute(()->HorrorOverlay.startRedWorld(dur));});
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
            .registerGlobalReceiver(JUMPSCARE,(client,handler,buf,res)->
                client.execute(HorrorOverlay::triggerJumpscare));
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
            .registerGlobalReceiver(CHAT_FLOOD,(client,handler,buf,res)->
                client.execute(ClientEventHandler::startChatFlood));
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
            .registerGlobalReceiver(PAINTINGS,(client,handler,buf,res)->{
                boolean on=buf.readBoolean(); client.execute(()->ClientEventHandler.setPaintingsCorrupted(on));});
    }

    public static void sendRedWorld(ServerPlayerEntity p,int dur){
        PacketByteBuf buf=PacketByteBufs.create(); buf.writeInt(dur);
        ServerPlayNetworking.send(p,RED_WORLD,buf);}
    public static void sendJumpscare(ServerPlayerEntity p){
        ServerPlayNetworking.send(p,JUMPSCARE,PacketByteBufs.empty());}
    public static void sendChatFlood(ServerPlayerEntity p){
        ServerPlayNetworking.send(p,CHAT_FLOOD,PacketByteBufs.empty());}
    public static void sendPaintings(ServerPlayerEntity p,boolean on){
        PacketByteBuf buf=PacketByteBufs.create(); buf.writeBoolean(on);
        ServerPlayNetworking.send(p,PAINTINGS,buf);}
}
