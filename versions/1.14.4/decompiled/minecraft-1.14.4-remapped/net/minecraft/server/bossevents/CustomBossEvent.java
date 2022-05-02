package net.minecraft.server.bossevents;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

public class CustomBossEvent extends ServerBossEvent {
   private final ResourceLocation id;
   private final Set players = Sets.newHashSet();
   private int value;
   private int max = 100;

   public CustomBossEvent(ResourceLocation id, Component component) {
      super(component, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS);
      this.id = id;
      this.setPercent(0.0F);
   }

   public ResourceLocation getTextId() {
      return this.id;
   }

   public void addPlayer(ServerPlayer serverPlayer) {
      super.addPlayer(serverPlayer);
      this.players.add(serverPlayer.getUUID());
   }

   public void addOfflinePlayer(UUID uUID) {
      this.players.add(uUID);
   }

   public void removePlayer(ServerPlayer serverPlayer) {
      super.removePlayer(serverPlayer);
      this.players.remove(serverPlayer.getUUID());
   }

   public void removeAllPlayers() {
      super.removeAllPlayers();
      this.players.clear();
   }

   public int getValue() {
      return this.value;
   }

   public int getMax() {
      return this.max;
   }

   public void setValue(int value) {
      this.value = value;
      this.setPercent(Mth.clamp((float)value / (float)this.max, 0.0F, 1.0F));
   }

   public void setMax(int max) {
      this.max = max;
      this.setPercent(Mth.clamp((float)this.value / (float)max, 0.0F, 1.0F));
   }

   public final Component getDisplayName() {
      return ComponentUtils.wrapInSquareBrackets(this.getName()).withStyle((style) -> {
         style.setColor(this.getColor().getFormatting()).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(this.getTextId().toString()))).setInsertion(this.getTextId().toString());
      });
   }

   public boolean setPlayers(Collection players) {
      Set<UUID> var2 = Sets.newHashSet();
      Set<ServerPlayer> var3 = Sets.newHashSet();

      for(UUID var5 : this.players) {
         boolean var6 = false;

         for(ServerPlayer var8 : players) {
            if(var8.getUUID().equals(var5)) {
               var6 = true;
               break;
            }
         }

         if(!var6) {
            var2.add(var5);
         }
      }

      for(ServerPlayer var5 : players) {
         boolean var6 = false;

         for(UUID var8 : this.players) {
            if(var5.getUUID().equals(var8)) {
               var6 = true;
               break;
            }
         }

         if(!var6) {
            var3.add(var5);
         }
      }

      for(UUID var5 : var2) {
         for(ServerPlayer var7 : this.getPlayers()) {
            if(var7.getUUID().equals(var5)) {
               this.removePlayer(var7);
               break;
            }
         }

         this.players.remove(var5);
      }

      for(ServerPlayer var5 : var3) {
         this.addPlayer(var5);
      }

      return !var2.isEmpty() || !var3.isEmpty();
   }

   public CompoundTag save() {
      CompoundTag compoundTag = new CompoundTag();
      compoundTag.putString("Name", Component.Serializer.toJson(this.name));
      compoundTag.putBoolean("Visible", this.isVisible());
      compoundTag.putInt("Value", this.value);
      compoundTag.putInt("Max", this.max);
      compoundTag.putString("Color", this.getColor().getName());
      compoundTag.putString("Overlay", this.getOverlay().getName());
      compoundTag.putBoolean("DarkenScreen", this.shouldDarkenScreen());
      compoundTag.putBoolean("PlayBossMusic", this.shouldPlayBossMusic());
      compoundTag.putBoolean("CreateWorldFog", this.shouldCreateWorldFog());
      ListTag var2 = new ListTag();

      for(UUID var4 : this.players) {
         var2.add(NbtUtils.createUUIDTag(var4));
      }

      compoundTag.put("Players", var2);
      return compoundTag;
   }

   public static CustomBossEvent load(CompoundTag compoundTag, ResourceLocation resourceLocation) {
      CustomBossEvent customBossEvent = new CustomBossEvent(resourceLocation, Component.Serializer.fromJson(compoundTag.getString("Name")));
      customBossEvent.setVisible(compoundTag.getBoolean("Visible"));
      customBossEvent.setValue(compoundTag.getInt("Value"));
      customBossEvent.setMax(compoundTag.getInt("Max"));
      customBossEvent.setColor(BossEvent.BossBarColor.byName(compoundTag.getString("Color")));
      customBossEvent.setOverlay(BossEvent.BossBarOverlay.byName(compoundTag.getString("Overlay")));
      customBossEvent.setDarkenScreen(compoundTag.getBoolean("DarkenScreen"));
      customBossEvent.setPlayBossMusic(compoundTag.getBoolean("PlayBossMusic"));
      customBossEvent.setCreateWorldFog(compoundTag.getBoolean("CreateWorldFog"));
      ListTag var3 = compoundTag.getList("Players", 10);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         customBossEvent.addOfflinePlayer(NbtUtils.loadUUIDTag(var3.getCompound(var4)));
      }

      return customBossEvent;
   }

   public void onPlayerConnect(ServerPlayer serverPlayer) {
      if(this.players.contains(serverPlayer.getUUID())) {
         this.addPlayer(serverPlayer);
      }

   }

   public void onPlayerDisconnect(ServerPlayer serverPlayer) {
      super.removePlayer(serverPlayer);
   }
}
