package com.thewatcher.event;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
public class SignSpawner {
    private static final Random RNG=new Random();
    private static final String[][] EARLY={
        {"don't go","to sleep","",""},{"he was here","before you","",""},
        {"you built","over something","",""},{"it followed","you home","",""},
        {"why did you","come back","",""},{"he doesn't","have a shadow","",""},
        {"i counted.","too many.","",""},{"the door was open","when i arrived","",""}
    };
    private static final String[][] MID={
        {"i saw what","you did","at 3am",""},{"he is standing","behind your","character",""},
        {"you were","warned","",""},{"don't look at","the player list","",""},
        {"the names","aren't real.","the last one is",""},
        {"player27392","isn't a player","",""},{"he is in the","tab menu","right now",""},
        {"scroll down","","",""}
    };
    private static final String[][] LATE={
        {"DON'T","LOOK","",""},{"you already","looked","",""},
        {"close","the game","",""},{"i'm serious.","close it.","",""},
        {"he's already","in the list","",""},{"Marcus","looked","",""}
    };
    public static void spawnSigns(ServerWorld world, ServerPlayerEntity player, int day){
        String[][] pool = day>=7?LATE:day>=5?MID:EARLY;
        int count=2+RNG.nextInt(3);
        for(int i=0;i<count;i++){
            int dist=20+RNG.nextInt(60);
            double ang=RNG.nextDouble()*Math.PI*2;
            BlockPos base=player.getBlockPos().add((int)(Math.cos(ang)*dist),0,(int)(Math.sin(ang)*dist));
            BlockPos ground=findGround(world,base);
            if(ground==null) continue;
            world.setBlockState(ground.up(),Blocks.OAK_SIGN.getDefaultState());
            if(world.getBlockEntity(ground.up()) instanceof SignBlockEntity sign){
                String[] lines=pool[RNG.nextInt(pool.length)];
                SignText text=new SignText();
                for(int l=0;l<4;l++) text=text.withMessage(l,Text.literal(lines[l]));
                sign.setText(text,true);
            }
        }
    }
    private static BlockPos findGround(ServerWorld world,BlockPos base){
        BlockPos p=new BlockPos(base.getX(),world.getTopY()-1,base.getZ());
        while(p.getY()>60&&world.getBlockState(p).isAir()) p=p.down();
        return p.getY()>60?p:null;
    }
}
