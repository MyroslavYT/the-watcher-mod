package com.thewatcher.event;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
public class InventoryShifter {
    public static void shiftHotbar(ServerPlayerEntity player){
        ItemStack first=player.getInventory().getStack(0).copy();
        for(int i=0;i<8;i++) player.getInventory().setStack(i,player.getInventory().getStack(i+1).copy());
        player.getInventory().setStack(8,first);
        player.playerScreenHandler.syncState();
    }
}
