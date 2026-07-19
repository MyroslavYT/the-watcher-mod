package com.thewatcher.event;
import com.thewatcher.WatcherWorldData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import java.util.Random;
public class FakePlayerManager {
    private static final Random RNG=new Random();
    // Corrupted glitch chars spliced into the name in phase 2 (days 5-6)
    private static final String[] CORRUPT={
        "̶̲̇̈","̖̥̀͞","͉͓̐"
    };
    private static final String[][] ADVANCEMENTS={
        {"He Found You","He always does."},
        {"Watching","He has been watching since day one."},
        {"You Looked","You were told not to."},
        {"Don't Run","It doesn't matter."},
        {"Still Here","He hasn't left. He won't."},
        {"Closer Now","Check the render distance."},
        {"Found","He is done waiting."}
    };

    // ── There is only ever ONE fake player. Its name is always
    // "NoobPlayer" + hidden coordinate digits (see WatcherWorldData.getFakePlayerName()).
    // The player is never told what those digits mean. It just reads like a bad username.
    public static void triggerJoinLeave(MinecraftServer server, WatcherWorldData data, int day){
        String name=getNameForDay(data,day);
        MutableText join=Text.translatable("multiplayer.player.joined",
            Text.literal(name).formatted(Formatting.WHITE)).formatted(Formatting.YELLOW);
        server.getPlayerManager().broadcast(join,false);
        if(day<=6){
            MutableText leave=Text.translatable("multiplayer.player.left",
                Text.literal(name).formatted(Formatting.WHITE)).formatted(Formatting.YELLOW);
            server.getPlayerManager().broadcast(leave,false);
        }
        // Day 7+: It joins and never leaves again.
    }

    public static void triggerNextAdvancement(MinecraftServer server, WatcherWorldData data){
        int idx=data.getAdvIdx();
        if(idx>=ADVANCEMENTS.length) return;
        String name=getNameForDay(data,dayForAdvancement(idx));
        String advName=ADVANCEMENTS[idx][0];
        String advDesc=ADVANCEMENTS[idx][1];
        MutableText advTitle=Text.literal("["+advName+"]").formatted(Formatting.GREEN,Formatting.BOLD);
        MutableText msg=Text.literal("").append(Text.literal(name).formatted(Formatting.WHITE))
            .append(Text.literal(" has made the advancement ").formatted(Formatting.WHITE))
            .append(advTitle).formatted(Formatting.YELLOW);
        server.getPlayerManager().broadcast(msg,false);
        server.getPlayerManager().broadcast(
            Text.literal(advDesc).formatted(Formatting.DARK_GRAY,Formatting.ITALIC),false);
        data.incAdvIdx();
    }

    private static int dayForAdvancement(int idx){
        if(idx<2) return 5; if(idx<5) return 6; return 7;
    }

    /** Same identity every time — only its presentation corrupts as days pass. */
    private static String getNameForDay(WatcherWorldData data,int day){
        String base=data.getFakePlayerName();
        if(day<=4) return base;
        if(day<=6) return corruptName(base);
        return "I̘͇ẗ̖̲";
    }

    private static String corruptName(String base){
        int cut=Math.max(4,base.length()-4);
        return base.substring(0,cut)+CORRUPT[new Random().nextInt(CORRUPT.length)]+base.substring(cut);
    }
}
