package com.thewatcher;
import com.thewatcher.entity.WatcherEntity;
import com.thewatcher.event.DayTracker;
import com.thewatcher.event.EventManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class TheWatcherMod implements ModInitializer {
    public static final String MOD_ID="thewatcher";
    public static final Logger LOGGER=LoggerFactory.getLogger(MOD_ID);
    public static final EntityType<WatcherEntity> WATCHER=Registry.register(
        Registries.ENTITY_TYPE, new Identifier(MOD_ID,"watcher"),
        FabricEntityTypeBuilder.<WatcherEntity>create(SpawnGroup.MISC,WatcherEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f,1.95f)).trackRangeBlocks(128).build());
    public static SoundEvent SOUND_SCREAM,SOUND_BREATHING,SOUND_FOOTSTEPS_ECHO,SOUND_JUMPSCARE,SOUND_RED_WORLD,SOUND_DISC;
    @Override public void onInitialize(){
        LOGGER.info("[TheWatcher] It is watching.");
        SOUND_SCREAM=reg("ambient_scream"); SOUND_BREATHING=reg("breathing");
        SOUND_FOOTSTEPS_ECHO=reg("footsteps_echo"); SOUND_JUMPSCARE=reg("jumpscare_sting");
        SOUND_RED_WORLD=reg("world_red_ambience"); SOUND_DISC=reg("disc_distorted");
        FabricDefaultAttributeRegistry.register(WATCHER,WatcherEntity.createAttributes());
        WatcherNetworking.register();
        ServerTickEvents.END_SERVER_TICK.register(s->{DayTracker.tick(s);EventManager.tick(s);});
        ServerWorldEvents.LOAD.register((s,w)->WatcherWorldData.getOrCreate(w));
        ServerPlayConnectionEvents.JOIN.register((h,se,s)->EventManager.onPlayerJoin(h.player,s));
    }
    private static SoundEvent reg(String n){Identifier id=new Identifier(MOD_ID,n);return Registry.register(Registries.SOUND_EVENT,id,SoundEvent.of(id));}
}
