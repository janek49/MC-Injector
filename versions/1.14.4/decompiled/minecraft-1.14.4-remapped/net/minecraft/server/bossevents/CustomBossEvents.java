package net.minecraft.server.bossevents;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerPlayer;

public class CustomBossEvents {
   private final MinecraftServer server;
   private final Map events = Maps.newHashMap();

   public CustomBossEvents(MinecraftServer server) {
      this.server = server;
   }

   @Nullable
   public CustomBossEvent get(ResourceLocation resourceLocation) {
      return (CustomBossEvent)this.events.get(resourceLocation);
   }

   public CustomBossEvent create(ResourceLocation resourceLocation, Component component) {
      CustomBossEvent customBossEvent = new CustomBossEvent(resourceLocation, component);
      this.events.put(resourceLocation, customBossEvent);
      return customBossEvent;
   }

   public void remove(CustomBossEvent customBossEvent) {
      this.events.remove(customBossEvent.getTextId());
   }

   public Collection getIds() {
      return this.events.keySet();
   }

   public Collection getEvents() {
      return this.events.values();
   }

   public CompoundTag save() {
      CompoundTag compoundTag = new CompoundTag();

      for(CustomBossEvent var3 : this.events.values()) {
         compoundTag.put(var3.getTextId().toString(), var3.save());
      }

      return compoundTag;
   }

   public void load(CompoundTag compoundTag) {
      for(String var3 : compoundTag.getAllKeys()) {
         ResourceLocation var4 = new ResourceLocation(var3);
         this.events.put(var4, CustomBossEvent.load(compoundTag.getCompound(var3), var4));
      }

   }

   public void onPlayerConnect(ServerPlayer serverPlayer) {
      for(CustomBossEvent var3 : this.events.values()) {
         var3.onPlayerConnect(serverPlayer);
      }

   }

   public void onPlayerDisconnect(ServerPlayer serverPlayer) {
      for(CustomBossEvent var3 : this.events.values()) {
         var3.onPlayerDisconnect(serverPlayer);
      }

   }
}
