package com.thewatcher.event;
import com.thewatcher.WatcherWorldData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
public class DayTracker {
    public static void tick(MinecraftServer server){
        ServerWorld world=server.getWorld(World.OVERWORLD);
        if(world==null) return;
        WatcherWorldData data=WatcherWorldData.getOrCreate(world);
        long now=world.getTimeOfDay()%24000L;
        long last=data.getLastDayTime();
        if(last>=0 && now<last){
            data.setDayCount(data.getDayCount()+1);
            EventManager.onNewDay(server,data.getDayCount());
        }
        data.setLastDayTime(now);
    }
}
