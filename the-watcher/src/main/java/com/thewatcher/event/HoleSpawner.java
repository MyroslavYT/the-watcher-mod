package com.thewatcher.event;
import com.thewatcher.WatcherWorldData;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
public class HoleSpawner {
    private static final Random RNG=new Random();

    /** Dig a 1x1 shaft from surface through bedrock to the void */
    public static void digHole(ServerWorld world, int x, int z){
        int top=world.getTopY();
        int bottom=world.getBottomY(); // -64 in default worlds
        for(int y=top;y>=bottom;y--){
            BlockPos pos=new BlockPos(x,y,z);
            if(!world.getBlockState(pos).isAir()){
                // FORCE_STATE bypasses bedrock hardness check
                world.setBlockState(pos,Blocks.AIR.getDefaultState(),
                    Block.NOTIFY_ALL|Block.FORCE_STATE);
            }
        }
    }

    /** Spawn the main finale hole at the SECRET location decided back on day 3 —
     *  the same coordinates hidden inside the fake player's name (NoobPlayerXXXXXX).
     *  This is what one of the books quietly confirms. */
    public static void spawnMainHole(ServerWorld world, ServerPlayerEntity player, WatcherWorldData data){
        int x=data.getNamePosX();
        int z=data.getNamePosZ();
        int y=world.getTopY()-1;
        while(y>world.getBottomY()&&world.getBlockState(new BlockPos(x,y,z)).isAir()) y--;
        digHole(world,x,z);
        BlockPos holePos=new BlockPos(x,y,z);
        data.setHolePos(holePos);
        data.setHoleSpawned(true);
    }

    /** Spawn a random small hole near the player (for atmosphere) */
    public static void spawnRandomHole(ServerWorld world, ServerPlayerEntity player){
        int dist=15+RNG.nextInt(50);
        double ang=RNG.nextDouble()*Math.PI*2;
        int x=(int)(player.getX()+Math.cos(ang)*dist);
        int z=(int)(player.getZ()+Math.sin(ang)*dist);
        digHole(world,x,z);
    }
}
