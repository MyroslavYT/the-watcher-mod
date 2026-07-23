package com.thewatcher;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
public class WatcherWorldData extends PersistentState {
    private static final String KEY = "thewatcher";
    private int dayCount = 0;
    private long lastDayTime = -1;
    private BlockPos holePos = null;
    private boolean holeSpawned = false;
    private int namePosX = 0, namePosZ = 0;
    private boolean finaleTriggered = false;
    private boolean[] booksSpawned = new boolean[8];
    private int advIdx = 0;
    private int randomHoles = 0;
    private boolean paintingsCorrupted = false;
    public static WatcherWorldData getOrCreate(ServerWorld w) {
    return w.getPersistentStateManager().getOrCreate(
        WatcherWorldData::new, KEY);
}
    }
    public static WatcherWorldData fromNbt(NbtCompound n) {
        WatcherWorldData d = new WatcherWorldData();
        d.dayCount = n.getInt("day"); d.lastDayTime = n.getLong("lastTime");
        d.finaleTriggered = n.getBoolean("finale"); d.namePosX = n.getInt("nx"); d.namePosZ = n.getInt("nz");
        d.advIdx = n.getInt("adv"); d.randomHoles = n.getInt("rh"); d.holeSpawned = n.getBoolean("hs");
        d.paintingsCorrupted = n.getBoolean("pc");
        for (int i=1;i<=7;i++) d.booksSpawned[i] = n.getBoolean("b"+i);
        if (n.contains("hx")) d.holePos = new BlockPos(n.getInt("hx"),n.getInt("hy"),n.getInt("hz"));
        return d;
    }
    @Override public NbtCompound writeNbt(NbtCompound n) {
        n.putInt("day",dayCount); n.putLong("lastTime",lastDayTime); n.putBoolean("finale",finaleTriggered);
        n.putInt("nx",namePosX); n.putInt("nz",namePosZ); n.putInt("adv",advIdx);
        n.putInt("rh",randomHoles); n.putBoolean("hs",holeSpawned); n.putBoolean("pc",paintingsCorrupted);
        for (int i=1;i<=7;i++) n.putBoolean("b"+i, booksSpawned[i]);
        if (holePos!=null){n.putInt("hx",holePos.getX());n.putInt("hy",holePos.getY());n.putInt("hz",holePos.getZ());}
        return n;
    }
    public int getDayCount(){return dayCount;} public void setDayCount(int v){dayCount=v;markDirty();}
    public long getLastDayTime(){return lastDayTime;} public void setLastDayTime(long v){lastDayTime=v;markDirty();}
    public BlockPos getHolePos(){return holePos;} public void setHolePos(BlockPos p){holePos=p;markDirty();}
    public boolean isHoleSpawned(){return holeSpawned;} public void setHoleSpawned(boolean v){holeSpawned=v;markDirty();}
    public int getNamePosX(){return namePosX;} public int getNamePosZ(){return namePosZ;}
    public void setNamePos(int x,int z){namePosX=x;namePosZ=z;markDirty();}
    public boolean isFinaleTriggered(){return finaleTriggered;} public void setFinaleTriggered(boolean v){finaleTriggered=v;markDirty();}
    public boolean isBookSpawned(int i){return booksSpawned[i];} public void setBookSpawned(int i,boolean v){booksSpawned[i]=v;markDirty();}
    public int getAdvIdx(){return advIdx;} public void incAdvIdx(){advIdx++;markDirty();}
    public int getRandomHoles(){return randomHoles;} public void incRandomHoles(){randomHoles++;markDirty();}
    public boolean isPaintingsCorrupted(){return paintingsCorrupted;} public void setPaintingsCorrupted(boolean v){paintingsCorrupted=v;markDirty();}
    public String getFakePlayerName(){return "NoobPlayer"+Math.abs(namePosX)+""+Math.abs(namePosZ);}
}
