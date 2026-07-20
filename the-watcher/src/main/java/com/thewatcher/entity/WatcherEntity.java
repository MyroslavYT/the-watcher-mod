package com.thewatcher.entity;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.List;
public class WatcherEntity extends MobEntity {
    private int disappearTimer=0;
    private int outOfViewTicks=0;
    public boolean markedForRemoval=false;

    public WatcherEntity(EntityType<? extends WatcherEntity> type,World world){
        super(type,world); setInvulnerable(true); setSilent(true);
    }
    public static DefaultAttributeContainer.Builder createAttributes(){
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH,1000.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED,0.0)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE,128.0);
    }
    @Override protected void initGoals(){}
    @Override protected void initDataTracker(){super.initDataTracker();}

    @Override public void tick(){
        super.tick();
        if(getWorld().isClient()) return;
        List<PlayerEntity> players=getWorld().getEntitiesByClass(PlayerEntity.class,getBoundingBox().expand(200),e->true);
        if(players.isEmpty()) return;
        PlayerEntity nearest=players.stream().min((a,c)->Double.compare(distanceTo(a),distanceTo(c))).orElse(null);
        if(nearest==null) return;
        lookAt(nearest,360f,360f);

        double dist=distanceTo(nearest);
        if(dist<12){ scheduleDisappear(); }

        if(!markedForRemoval){
            if(isInPlayerView(nearest)){
                outOfViewTicks=0;
            } else {
                outOfViewTicks++;
                // The player turned away without ever spotting it. Rather than vanish
                // unnoticed, reposition somewhere they WILL see when they look back —
                // the whole point is that they see it before it's gone.
                if(outOfViewTicks>40){
                    repositionIntoView(nearest);
                    outOfViewTicks=0;
                }
            }
        }

        if(markedForRemoval){ disappearTimer++; if(disappearTimer>15) discard(); }
    }

    /** Roughly: is this entity currently inside the player's field of view (on their screen)? */
    private boolean isInPlayerView(PlayerEntity p){
        Vec3d look=p.getRotationVec(1.0f).normalize();
        Vec3d toEntity=getEyePos().subtract(p.getEyePos()).normalize();
        double dot=look.dotProduct(toEntity);
        return dot>0.5; // roughly a generous "on screen" cone
    }

    /** Teleport to a spot that IS inside the player's current view, at render-distance-ish range. */
    private void repositionIntoView(PlayerEntity p){
        World world=getWorld();
        float yaw=p.getYaw();
        double yawRad=Math.toRadians(yaw);
        double fdx=-Math.sin(yawRad), fdz=Math.cos(yawRad);
        double spreadDeg=(world.getRandom().nextDouble()*40)-20; // within ±20° of dead ahead
        double spread=Math.toRadians(spreadDeg);
        double cosT=Math.cos(spread), sinT=Math.sin(spread);
        double dx=fdx*cosT-fdz*sinT, dz=fdx*sinT+fdz*cosT;
        int dist=45+world.getRandom().nextInt(25);
        double sx=p.getX()+dx*dist, sz=p.getZ()+dz*dist;
        int sy=world.getTopY()-1;
        BlockPos check=new BlockPos((int)sx,sy,(int)sz);
        while(sy>60&&world.getBlockState(check).isAir()){ sy--; check=new BlockPos((int)sx,sy,(int)sz); }
        refreshPositionAndAngles(sx,sy+1,sz,0f,0f);
    }

    public void scheduleDisappear(){markedForRemoval=true;}
    @Override public boolean isInvulnerableTo(DamageSource s){return true;}
    @Override public boolean isPushable(){return false;}
}
