package com.thewatcher.event;
import com.thewatcher.*;
import com.thewatcher.entity.WatcherEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.List;
import java.util.Random;
public class EventManager {
    private static final Random RNG=new Random();
    private static int tick=0;
    private static int watcherCd=0;
    private static int redCd=0;
    private static int invShiftCd=0;
    private static int coordEchoCd=0;
    private static int holeCd=0;
    private static WatcherEntity currentWatcher=null;

    public static void tick(MinecraftServer server){
        tick++;
        ServerWorld world=server.getWorld(World.OVERWORLD);
        if(world==null) return;
        WatcherWorldData data=WatcherWorldData.getOrCreate(world);
        int day=data.getDayCount();
        List<ServerPlayerEntity> plist=server.getPlayerManager().getPlayerList();
        if(plist.isEmpty()) return;
        ServerPlayerEntity player=plist.get(0);
        if(day<3) return;

        // ── Watcher spawning ──────────────────────────────
        if(watcherCd>0) watcherCd--;
        else { trySpawnWatcher(world,player,day); watcherCd=1200+RNG.nextInt(2400); }

        // ── Check if player is near the main hole ──────────
        if(data.isHoleSpawned()&&!data.isFinaleTriggered()&&!FinaleController.isRunning()){
            BlockPos hp=data.getHolePos();
            if(hp!=null&&player.getBlockPos().isWithinDistance(hp,5)){
                FinaleController.triggerFinale(player,world);
            }
        }

        // ── Book/chest spawning ───────────────────────────
        if(day>=3&&!data.isBookSpawned(1)) ChestBookSpawner.spawnChest(world,player,1,data);
        if(day>=4&&!data.isBookSpawned(2)) ChestBookSpawner.spawnChest(world,player,2,data);
        if(day>=5&&!data.isBookSpawned(3)) ChestBookSpawner.spawnChest(world,player,3,data);
        if(day>=5&&!data.isBookSpawned(4)) ChestBookSpawner.spawnChest(world,player,4,data);
        if(day>=6&&!data.isBookSpawned(5)) ChestBookSpawner.spawnChest(world,player,5,data);
        if(day>=6&&!data.isBookSpawned(6)) ChestBookSpawner.spawnChest(world,player,6,data);
        if(day>=7&&!data.isHoleSpawned()) HoleSpawner.spawnMainHole(world,player,data);
        if(day>=7&&data.isHoleSpawned()&&!data.isBookSpawned(7)) ChestBookSpawner.spawnChestAtHole(world,data,player);

        // ── Random holes in the world ──────────────────────
        if(holeCd>0) holeCd--;
        else if(day>=3){
            if(data.getRandomHoles()<20&&RNG.nextInt(4800)==0){
                HoleSpawner.spawnRandomHole(world,player);
                data.incRandomHoles();
            }
            holeCd=4800;
        }

        // ── Fake player messages ──────────────────────────
        if(tick%900==0&&RNG.nextInt(2)==0) FakePlayerManager.triggerJoinLeave(server,data,day);
        if(tick%3000==0) FakePlayerManager.triggerNextAdvancement(server,data);

        // ── Torch extinguish (from day 5, one per night) ──
        if(day>=5&&world.getTimeOfDay()%24000==13200) TorchExtinguisher.extinguishNearest(world,player);

        // ── Inventory shift ────────────────────────────────
        if(invShiftCd>0) invShiftCd--;
        else if(day>=3&&RNG.nextInt(6000)==0){ InventoryShifter.shiftHotbar(player); invShiftCd=2400; }

        // ── Red world random event ─────────────────────────
        if(redCd>0) redCd--;
        else if(RNG.nextInt(8000)==0){
            WatcherNetworking.sendRedWorld(player,120+RNG.nextInt(60));
            redCd=12000;
        }

        // ── Coordinate echo in chat ────────────────────────
        if(coordEchoCd>0) coordEchoCd--;
        else if(RNG.nextInt(14000)==0){
            String coords=(int)player.getX()+" "+(int)player.getY()+" "+(int)player.getZ();
            server.getPlayerManager().broadcast(Text.literal(coords).formatted(Formatting.DARK_GRAY),false);
            coordEchoCd=18000;
        }

        // ── Player name alone in chat ──────────────────────
        if(tick%24000==0&&RNG.nextInt(3)==0){
            String nm=player.getName().getString();
            server.getPlayerManager().broadcast(Text.literal(nm).formatted(Formatting.GRAY),false);
        }

        // ── Check books in inventory for read tracking ─────
        checkBooksInInventory(server,player,data);

        // ── Paintings corruption (day 6) ───────────────────
        if(day>=6&&!data.isPaintingsCorrupted()){
            data.setPaintingsCorrupted(true);
        }
        // Day 7 dawn - revert
        if(day>=7&&world.getTimeOfDay()%24000<200&&data.isPaintingsCorrupted()){
            // After finale, paintings revert (handled client-side via flag)
        }
    }

    private static void trySpawnWatcher(ServerWorld world, ServerPlayerEntity player, int day){
        long time=world.getTimeOfDay()%24000;
        boolean isNight=time>13000&&time<23000;
        if(!isNight&&day<5&&RNG.nextInt(5)!=0) return;
        int dist=50+RNG.nextInt(30);
        double ang=RNG.nextDouble()*Math.PI*2;
        double sx=player.getX()+Math.cos(ang)*dist;
        double sz=player.getZ()+Math.sin(ang)*dist;
        int sy=world.getTopY()-1;
        while(sy>60&&world.getBlockState(new BlockPos((int)sx,sy,(int)sz)).isAir()) sy--;
        // Try to find a tree nearby, and if found, push the spawn point just past it
        // (relative to the player) so the trunk visually breaks up the silhouette.
        BlockPos foundLog=null;
        for(int dx=-4;dx<=4&&foundLog==null;dx++) for(int dz=-4;dz<=4&&foundLog==null;dz++){
            BlockPos p=new BlockPos((int)sx+dx,sy,(int)sz+dz);
            var bs=world.getBlockState(p);
            if(bs.isOf(Blocks.OAK_LOG)||bs.isOf(Blocks.SPRUCE_LOG)||bs.isOf(Blocks.BIRCH_LOG)||bs.isOf(Blocks.DARK_OAK_LOG)) foundLog=p;
        }
        if(foundLog==null&&RNG.nextInt(3)!=0) return;
        if(foundLog!=null){
            // push 1-2 blocks further away from the player, along the player->tree line
            double dx=foundLog.getX()-player.getX(), dz=foundLog.getZ()-player.getZ();
            double len=Math.max(0.001,Math.hypot(dx,dz));
            sx=foundLog.getX()+(dx/len)*1.5;
            sz=foundLog.getZ()+(dz/len)*1.5;
            sy=foundLog.getY();
        }
        if(currentWatcher!=null&&!currentWatcher.isRemoved()) currentWatcher.discard();
        WatcherEntity w=TheWatcherMod.WATCHER.create(world);
        if(w!=null){
            w.refreshPositionAndAngles(sx,sy+1,sz,0f,0f);
            world.spawnEntity(w);
            currentWatcher=w;
        }
    }

    private static void checkBooksInInventory(MinecraftServer server, ServerPlayerEntity player, WatcherWorldData data){
        if(tick%200!=0) return;
        for(int slot=0;slot<player.getInventory().size();slot++){
            var stack=player.getInventory().getStack(slot);
            if(stack.getItem()==net.minecraft.item.Items.WRITTEN_BOOK){
                var nbt=stack.getNbt();
                if(nbt!=null&&nbt.contains("watcher_book_id")){
                    int bid=nbt.getInt("watcher_book_id");
                    int reads=nbt.getInt("watcher_read_count");
                    if(reads==0&&tick>6000){
                        // Alter book content after player has held it a while
                        var altered=BookFactory.makeBook(bid,player,true);
                        player.getInventory().setStack(slot,altered);
                    }
                }
            }
        }
    }

    public static void onPlayerLookedAtWatcher(ServerPlayerEntity player){
        if(currentWatcher!=null&&!currentWatcher.isRemoved()){
            currentWatcher.scheduleDisappear();
        }
    }

    public static void onNewDay(MinecraftServer server, int day){
        ServerWorld world=server.getWorld(World.OVERWORLD);
        if(world==null) return;
        WatcherWorldData data=WatcherWorldData.getOrCreate(world);
        List<ServerPlayerEntity> plist=server.getPlayerManager().getPlayerList();
        if(plist.isEmpty()) return;
        ServerPlayerEntity player=plist.get(0);
        if(day==3){
            // Secretly decide where the finale hole will be RIGHT NOW, on day 3.
            // Its coordinates become the digits in "NoobPlayerXXXXXX" in the tab list —
            // hidden in plain sight for the whole playthrough. Nothing spawns here yet.
            int dist=90+RNG.nextInt(60);
            double ang=RNG.nextDouble()*Math.PI*2;
            int hx=(int)(player.getX()+Math.cos(ang)*dist);
            int hz=(int)(player.getZ()+Math.sin(ang)*dist);
            data.setNamePos(hx,hz);
            SignSpawner.spawnSigns(world,player,3);
        }
        if(day==5) SignSpawner.spawnSigns(world,player,5);
        if(day==7) SignSpawner.spawnSigns(world,player,7);
    }

    public static void onPlayerJoin(ServerPlayerEntity player, MinecraftServer server){
        TheWatcherMod.LOGGER.info("[TheWatcher] "+player.getName().getString()+" joined. It noticed.");
    }
}
