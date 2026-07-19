package com.thewatcher.entity;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import java.util.List;
public class WatcherEntity extends MobEntity {
    private int disappearTimer=0;
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
    @Override public void tick(){
        super.tick();
        if(getWorld().isClient()) return;
        List<PlayerEntity> players=getWorld().getEntitiesByClass(PlayerEntity.class,getBoundingBox().expand(200),e->true);
        if(players.isEmpty()) return;
        PlayerEntity nearest=players.stream().min((a,c)->Double.compare(distanceTo(a),distanceTo(c))).orElse(null);
        if(nearest==null) return;
        lookAt(EntityAnchor.EYES, nearest.getEyePos());
        if(distanceTo(nearest)<12){scheduleDisappear();return;}
        if(markedForRemoval){disappearTimer++;if(disappearTimer>15)discard();}
    }
    public void scheduleDisappear(){markedForRemoval=true;}
    @Override public boolean isInvulnerableTo(DamageSource s){return true;}
    @Override public boolean isPushable(){return false;}
    @Override protected void initDataTracker(){}
}
