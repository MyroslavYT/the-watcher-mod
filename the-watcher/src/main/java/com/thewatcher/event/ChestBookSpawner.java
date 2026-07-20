package com.thewatcher.event;
import com.thewatcher.WatcherWorldData;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
public class ChestBookSpawner {
    private static final Random RNG=new Random();
    public static void spawnChest(ServerWorld world, ServerPlayerEntity player, int bookId, WatcherWorldData data){
        if(data.isBookSpawned(bookId)) return;
        BlockPos pos=findClearSpot(world,player,25,70);
        if(pos==null) return;
        world.setBlockState(pos,Blocks.CHEST.getDefaultState());
        if(world.getBlockEntity(pos) instanceof ChestBlockEntity chest){
            chest.setStack(0, BookFactory.makeBook(bookId,player));
            if(bookId==1) chest.setStack(1, makeLetter(player));
            // Some decoy loot so it looks accidental
            chest.setStack(3,new ItemStack(Items.BREAD,3));
            chest.setStack(5,new ItemStack(Items.COAL,8));
            if(bookId==3) chest.setStack(7, makeDisc());
        }
        data.setBookSpawned(bookId,true);
    }
    public static void spawnChestAtHole(ServerWorld world, WatcherWorldData data, ServerPlayerEntity player){
        if(data.isBookSpawned(7)||data.getHolePos()==null) return;
        BlockPos hole=data.getHolePos();
        BlockPos chestPos=hole.north(2);
        // Make sure this position is solid ground
        world.setBlockState(chestPos,Blocks.CHEST.getDefaultState());
        if(world.getBlockEntity(chestPos) instanceof ChestBlockEntity chest){
            chest.setStack(0, BookFactory.makeBook(7,player));
        }
        data.setBookSpawned(7,true);
    }
    private static ItemStack makeLetter(ServerPlayerEntity player){
        ItemStack letter=new ItemStack(Items.WRITTEN_BOOK);
        net.minecraft.nbt.NbtCompound nbt=new net.minecraft.nbt.NbtCompound();
        nbt.putBoolean("resolved",true);
        nbt.putString("title","Letter");
        nbt.putString("author","unknown");
        net.minecraft.nbt.NbtList pages=new net.minecraft.nbt.NbtList();
        String pname=player!=null?player.getName().getString():"[player]";
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"i don\'t have much time. whatever you do, don\'t keep the books. reading them is how it knows you\'re paying attention. it can\'t see you until you start paying attention.\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"i found the mod the same way you did. i read all seven books. on the eleventh day my flatmate knocked and i didn\'t answer. when she came back the door was unlocked but i wasn\'t there.\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"dont read the books dont read the books dont finish them please\"}"));
        String rep=pname+" "+pname+" "+pname+" "+pname+" "+pname+" "+pname+" "+pname+" "+pname+" "+pname+" "+pname+" "+pname+" "+pname;
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\""+rep+"\"}"));
        nbt.put("pages",pages);
        letter.setNbt(nbt);
        return letter;
    }
    private static ItemStack makeDisc(){
        ItemStack disc=new ItemStack(Items.MUSIC_DISC_WAIT);
        disc.setCustomName(net.minecraft.text.Text.literal("C418 - ?"));
        return disc;
    }
    private static BlockPos findClearSpot(ServerWorld world, ServerPlayerEntity player, int minD, int maxD){
        // Prefer placing near the player's bed/respawn point (their "home base") —
        // that's the spot they actually walk past repeatedly. Fall back to a spot
        // biased in front of where they're currently facing, so it still reads as
        // "on their way" instead of landing somewhere random they may never visit.
        BlockPos bed=player.getSpawnPointPosition();
        boolean hasBed=bed!=null&&world.getRegistryKey().equals(player.getSpawnPointDimension());
        double baseX=hasBed?bed.getX():player.getX();
        double baseZ=hasBed?bed.getZ():player.getZ();
        int nearMin=hasBed?8:15, nearMax=hasBed?28:35;
        double yawRad=Math.toRadians(player.getYaw());
        double fdx=-Math.sin(yawRad), fdz=Math.cos(yawRad);
        for(int attempt=0;attempt<20;attempt++){
            double x,z;
            if(hasBed){
                double ang=RNG.nextDouble()*Math.PI*2;
                int dist=nearMin+RNG.nextInt(nearMax-nearMin);
                x=baseX+Math.cos(ang)*dist; z=baseZ+Math.sin(ang)*dist;
            } else {
                double spread=Math.toRadians(-70+RNG.nextDouble()*140);
                double cosT=Math.cos(spread), sinT=Math.sin(spread);
                double dx=fdx*cosT-fdz*sinT, dz=fdx*sinT+fdz*cosT;
                int dist=nearMin+RNG.nextInt(nearMax-nearMin);
                x=baseX+dx*dist; z=baseZ+dz*dist;
            }
            int y=world.getTopY()-1;
            while(y>60&&world.getBlockState(new BlockPos((int)x,y,(int)z)).isAir()) y--;
            BlockPos ground=new BlockPos((int)x,y,(int)z);
            if(y>60&&!world.getBlockState(ground).isAir())
                return ground.up();
        }
        return null;
    }
}
