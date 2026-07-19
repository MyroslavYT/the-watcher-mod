package com.thewatcher.event;
import com.thewatcher.WatcherNetworking;
import com.thewatcher.WatcherWorldData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class FinaleController {
    private static boolean running=false;
    public static void triggerFinale(ServerPlayerEntity player, ServerWorld world){
        if(running) return;
        running=true;
        WatcherWorldData.getOrCreate(world).setFinaleTriggered(true);
        ScheduledExecutorService sched=Executors.newSingleThreadScheduledExecutor();
        // Stage 1: Chat flood
        sched.schedule(()->{ floodChat(player,world); },0,TimeUnit.MILLISECONDS);
        // Stage 2: Red world
        sched.schedule(()->{ WatcherNetworking.sendRedWorld(player,200); },3000,TimeUnit.MILLISECONDS);
        // Stage 3: Jumpscare
        sched.schedule(()->{ WatcherNetworking.sendJumpscare(player); },7000,TimeUnit.MILLISECONDS);
        // Stage 4: Kick with "don\'t look"
        sched.schedule(()->{ player.networkHandler.disconnect(
            Text.literal("don\'t look")); running=false; },8500,TimeUnit.MILLISECONDS);
        sched.shutdown();
    }
    private static void floodChat(ServerPlayerEntity player, ServerWorld world){
        WatcherNetworking.sendChatFlood(player);
        String[] msgs={"don\'t look","don\'t look","DON\'T LOOK","don\'t look","DON\'T LOOK","DON\'T LOOK"};
        for(String m:msgs){
            world.getServer().getPlayerManager().broadcast(
                Text.literal(m).formatted(Formatting.DARK_RED),false);
        }
    }
    public static boolean isRunning(){return running;}
}
