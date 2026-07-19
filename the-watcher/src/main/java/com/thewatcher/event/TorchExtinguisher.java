package com.thewatcher.event;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
public class TorchExtinguisher {
    public static void extinguishNearest(ServerWorld world, ServerPlayerEntity player){
        BlockPos center=player.getBlockPos();
        for(int r=1;r<=25;r++){
            for(int x=-r;x<=r;x++) for(int z=-r;z<=r;z++) for(int y=-3;y<=5;y++){
                BlockPos pos=center.add(x,y,z);
                if(world.getBlockState(pos).isOf(Blocks.TORCH)||world.getBlockState(pos).isOf(Blocks.WALL_TORCH)){
                    world.setBlockState(pos,Blocks.AIR.getDefaultState());
                    return;
                }
            }
        }
    }
}
