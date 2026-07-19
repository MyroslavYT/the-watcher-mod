package com.thewatcher;

import com.thewatcher.client.ClientEventHandler;
import com.thewatcher.client.HorrorOverlay;
import com.thewatcher.entity.WatcherEntityModel;
import com.thewatcher.entity.WatcherEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class TheWatcherClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(
            WatcherEntityRenderer.MODEL_LAYER, WatcherEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(TheWatcherMod.WATCHER, WatcherEntityRenderer::new);
        HudRenderCallback.EVENT.register(HorrorOverlay::render);
        ClientTickEvents.END_CLIENT_TICK.register(ClientEventHandler::onClientTick);
        WatcherNetworking.registerClient();
    }
}
