package net.minecraft.world.level.saveddata.maps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapBanner;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapFrame;

public class MapItemSavedData extends SavedData {
   public int x;
   public int z;
   public DimensionType dimension;
   public boolean trackingPosition;
   public boolean unlimitedTracking;
   public byte scale;
   public byte[] colors = new byte[16384];
   public boolean locked;
   public final List carriedBy = Lists.newArrayList();
   private final Map carriedByPlayers = Maps.newHashMap();
   private final Map bannerMarkers = Maps.newHashMap();
   public final Map decorations = Maps.newLinkedHashMap();
   private final Map frameMarkers = Maps.newHashMap();

   public MapItemSavedData(String string) {
      super(string);
   }

   public void setProperties(int var1, int var2, int var3, boolean trackingPosition, boolean unlimitedTracking, DimensionType dimension) {
      this.scale = (byte)var3;
      this.setOrigin((double)var1, (double)var2, this.scale);
      this.dimension = dimension;
      this.trackingPosition = trackingPosition;
      this.unlimitedTracking = unlimitedTracking;
      this.setDirty();
   }

   public void setOrigin(double var1, double var3, int var5) {
      int var6 = 128 * (1 << var5);
      int var7 = Mth.floor((var1 + 64.0D) / (double)var6);
      int var8 = Mth.floor((var3 + 64.0D) / (double)var6);
      this.x = var7 * var6 + var6 / 2 - 64;
      this.z = var8 * var6 + var6 / 2 - 64;
   }

   public void load(CompoundTag compoundTag) {
      int var2 = compoundTag.getInt("dimension");
      DimensionType var3 = DimensionType.getById(var2);
      if(var3 == null) {
         throw new IllegalArgumentException("Invalid map dimension: " + var2);
      } else {
         this.dimension = var3;
         this.x = compoundTag.getInt("xCenter");
         this.z = compoundTag.getInt("zCenter");
         this.scale = (byte)Mth.clamp(compoundTag.getByte("scale"), 0, 4);
         this.trackingPosition = !compoundTag.contains("trackingPosition", 1) || compoundTag.getBoolean("trackingPosition");
         this.unlimitedTracking = compoundTag.getBoolean("unlimitedTracking");
         this.locked = compoundTag.getBoolean("locked");
         this.colors = compoundTag.getByteArray("colors");
         if(this.colors.length != 16384) {
            this.colors = new byte[16384];
         }

         ListTag var4 = compoundTag.getList("banners", 10);

         for(int var5 = 0; var5 < var4.size(); ++var5) {
            MapBanner var6 = MapBanner.load(var4.getCompound(var5));
            this.bannerMarkers.put(var6.getId(), var6);
            this.addDecoration(var6.getDecoration(), (LevelAccessor)null, var6.getId(), (double)var6.getPos().getX(), (double)var6.getPos().getZ(), 180.0D, var6.getName());
         }

         ListTag var5 = compoundTag.getList("frames", 10);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            MapFrame var7 = MapFrame.load(var5.getCompound(var6));
            this.frameMarkers.put(var7.getId(), var7);
            this.addDecoration(MapDecoration.Type.FRAME, (LevelAccessor)null, "frame-" + var7.getEntityId(), (double)var7.getPos().getX(), (double)var7.getPos().getZ(), (double)var7.getRotation(), (Component)null);
         }

      }
   }

   public CompoundTag save(CompoundTag compoundTag) {
      compoundTag.putInt("dimension", this.dimension.getId());
      compoundTag.putInt("xCenter", this.x);
      compoundTag.putInt("zCenter", this.z);
      compoundTag.putByte("scale", this.scale);
      compoundTag.putByteArray("colors", this.colors);
      compoundTag.putBoolean("trackingPosition", this.trackingPosition);
      compoundTag.putBoolean("unlimitedTracking", this.unlimitedTracking);
      compoundTag.putBoolean("locked", this.locked);
      ListTag var2 = new ListTag();

      for(MapBanner var4 : this.bannerMarkers.values()) {
         var2.add(var4.save());
      }

      compoundTag.put("banners", var2);
      ListTag var3 = new ListTag();

      for(MapFrame var5 : this.frameMarkers.values()) {
         var3.add(var5.save());
      }

      compoundTag.put("frames", var3);
      return compoundTag;
   }

   public void lockData(MapItemSavedData mapItemSavedData) {
      this.locked = true;
      this.x = mapItemSavedData.x;
      this.z = mapItemSavedData.z;
      this.bannerMarkers.putAll(mapItemSavedData.bannerMarkers);
      this.decorations.putAll(mapItemSavedData.decorations);
      System.arraycopy(mapItemSavedData.colors, 0, this.colors, 0, mapItemSavedData.colors.length);
      this.setDirty();
   }

   public void tickCarriedBy(Player player, ItemStack itemStack) {
      if(!this.carriedByPlayers.containsKey(player)) {
         MapItemSavedData.HoldingPlayer var3 = new MapItemSavedData.HoldingPlayer(player);
         this.carriedByPlayers.put(player, var3);
         this.carriedBy.add(var3);
      }

      if(!player.inventory.contains(itemStack)) {
         this.decorations.remove(player.getName().getString());
      }

      for(int var3 = 0; var3 < this.carriedBy.size(); ++var3) {
         MapItemSavedData.HoldingPlayer var4 = (MapItemSavedData.HoldingPlayer)this.carriedBy.get(var3);
         String var5 = var4.player.getName().getString();
         if(!var4.player.removed && (var4.player.inventory.contains(itemStack) || itemStack.isFramed())) {
            if(!itemStack.isFramed() && var4.player.dimension == this.dimension && this.trackingPosition) {
               this.addDecoration(MapDecoration.Type.PLAYER, var4.player.level, var5, var4.player.x, var4.player.z, (double)var4.player.yRot, (Component)null);
            }
         } else {
            this.carriedByPlayers.remove(var4.player);
            this.carriedBy.remove(var4);
            this.decorations.remove(var5);
         }
      }

      if(itemStack.isFramed() && this.trackingPosition) {
         ItemFrame var3 = itemStack.getFrame();
         BlockPos var4 = var3.getPos();
         MapFrame var5 = (MapFrame)this.frameMarkers.get(MapFrame.frameId(var4));
         if(var5 != null && var3.getId() != var5.getEntityId() && this.frameMarkers.containsKey(var5.getId())) {
            this.decorations.remove("frame-" + var5.getEntityId());
         }

         MapFrame var6 = new MapFrame(var4, var3.getDirection().get2DDataValue() * 90, var3.getId());
         this.addDecoration(MapDecoration.Type.FRAME, player.level, "frame-" + var3.getId(), (double)var4.getX(), (double)var4.getZ(), (double)(var3.getDirection().get2DDataValue() * 90), (Component)null);
         this.frameMarkers.put(var6.getId(), var6);
      }

      CompoundTag var3 = itemStack.getTag();
      if(var3 != null && var3.contains("Decorations", 9)) {
         ListTag var4 = var3.getList("Decorations", 10);

         for(int var5 = 0; var5 < var4.size(); ++var5) {
            CompoundTag var6 = var4.getCompound(var5);
            if(!this.decorations.containsKey(var6.getString("id"))) {
               this.addDecoration(MapDecoration.Type.byIcon(var6.getByte("type")), player.level, var6.getString("id"), var6.getDouble("x"), var6.getDouble("z"), var6.getDouble("rot"), (Component)null);
            }
         }
      }

   }

   public static void addTargetDecoration(ItemStack itemStack, BlockPos blockPos, String string, MapDecoration.Type mapDecoration$Type) {
      ListTag var4;
      if(itemStack.hasTag() && itemStack.getTag().contains("Decorations", 9)) {
         var4 = itemStack.getTag().getList("Decorations", 10);
      } else {
         var4 = new ListTag();
         itemStack.addTagElement("Decorations", var4);
      }

      CompoundTag var5 = new CompoundTag();
      var5.putByte("type", mapDecoration$Type.getIcon());
      var5.putString("id", string);
      var5.putDouble("x", (double)blockPos.getX());
      var5.putDouble("z", (double)blockPos.getZ());
      var5.putDouble("rot", 180.0D);
      var4.add(var5);
      if(mapDecoration$Type.hasMapColor()) {
         CompoundTag var6 = itemStack.getOrCreateTagElement("display");
         var6.putInt("MapColor", mapDecoration$Type.getMapColor());
      }

   }

   private void addDecoration(MapDecoration.Type mapDecoration$Type, @Nullable LevelAccessor levelAccessor, String string, double var4, double var6, double var8, @Nullable Component component) {
      int var11 = 1 << this.scale;
      float var12 = (float)(var4 - (double)this.x) / (float)var11;
      float var13 = (float)(var6 - (double)this.z) / (float)var11;
      byte var14 = (byte)((int)((double)(var12 * 2.0F) + 0.5D));
      byte var15 = (byte)((int)((double)(var13 * 2.0F) + 0.5D));
      int var17 = 63;
      byte var16;
      if(var12 >= -63.0F && var13 >= -63.0F && var12 <= 63.0F && var13 <= 63.0F) {
         var8 = var8 + (var8 < 0.0D?-8.0D:8.0D);
         var16 = (byte)((int)(var8 * 16.0D / 360.0D));
         if(this.dimension == DimensionType.NETHER && levelAccessor != null) {
            int var18 = (int)(levelAccessor.getLevelData().getDayTime() / 10L);
            var16 = (byte)(var18 * var18 * 34187121 + var18 * 121 >> 15 & 15);
         }
      } else {
         if(mapDecoration$Type != MapDecoration.Type.PLAYER) {
            this.decorations.remove(string);
            return;
         }

         int var18 = 320;
         if(Math.abs(var12) < 320.0F && Math.abs(var13) < 320.0F) {
            mapDecoration$Type = MapDecoration.Type.PLAYER_OFF_MAP;
         } else {
            if(!this.unlimitedTracking) {
               this.decorations.remove(string);
               return;
            }

            mapDecoration$Type = MapDecoration.Type.PLAYER_OFF_LIMITS;
         }

         var16 = 0;
         if(var12 <= -63.0F) {
            var14 = -128;
         }

         if(var13 <= -63.0F) {
            var15 = -128;
         }

         if(var12 >= 63.0F) {
            var14 = 127;
         }

         if(var13 >= 63.0F) {
            var15 = 127;
         }
      }

      this.decorations.put(string, new MapDecoration(mapDecoration$Type, var14, var15, var16, component));
   }

   @Nullable
   public Packet getUpdatePacket(ItemStack itemStack, BlockGetter blockGetter, Player player) {
      MapItemSavedData.HoldingPlayer var4 = (MapItemSavedData.HoldingPlayer)this.carriedByPlayers.get(player);
      return var4 == null?null:var4.nextUpdatePacket(itemStack);
   }

   public void setDirty(int var1, int var2) {
      this.setDirty();

      for(MapItemSavedData.HoldingPlayer var4 : this.carriedBy) {
         var4.markDirty(var1, var2);
      }

   }

   public MapItemSavedData.HoldingPlayer getHoldingPlayer(Player player) {
      MapItemSavedData.HoldingPlayer mapItemSavedData$HoldingPlayer = (MapItemSavedData.HoldingPlayer)this.carriedByPlayers.get(player);
      if(mapItemSavedData$HoldingPlayer == null) {
         mapItemSavedData$HoldingPlayer = new MapItemSavedData.HoldingPlayer(player);
         this.carriedByPlayers.put(player, mapItemSavedData$HoldingPlayer);
         this.carriedBy.add(mapItemSavedData$HoldingPlayer);
      }

      return mapItemSavedData$HoldingPlayer;
   }

   public void toggleBanner(LevelAccessor levelAccessor, BlockPos blockPos) {
      float var3 = (float)blockPos.getX() + 0.5F;
      float var4 = (float)blockPos.getZ() + 0.5F;
      int var5 = 1 << this.scale;
      float var6 = (var3 - (float)this.x) / (float)var5;
      float var7 = (var4 - (float)this.z) / (float)var5;
      int var8 = 63;
      boolean var9 = false;
      if(var6 >= -63.0F && var7 >= -63.0F && var6 <= 63.0F && var7 <= 63.0F) {
         MapBanner var10 = MapBanner.fromWorld(levelAccessor, blockPos);
         if(var10 == null) {
            return;
         }

         boolean var11 = true;
         if(this.bannerMarkers.containsKey(var10.getId()) && ((MapBanner)this.bannerMarkers.get(var10.getId())).equals(var10)) {
            this.bannerMarkers.remove(var10.getId());
            this.decorations.remove(var10.getId());
            var11 = false;
            var9 = true;
         }

         if(var11) {
            this.bannerMarkers.put(var10.getId(), var10);
            this.addDecoration(var10.getDecoration(), levelAccessor, var10.getId(), (double)var3, (double)var4, 180.0D, var10.getName());
            var9 = true;
         }

         if(var9) {
            this.setDirty();
         }
      }

   }

   public void checkBanners(BlockGetter blockGetter, int var2, int var3) {
      Iterator<MapBanner> var4 = this.bannerMarkers.values().iterator();

      while(var4.hasNext()) {
         MapBanner var5 = (MapBanner)var4.next();
         if(var5.getPos().getX() == var2 && var5.getPos().getZ() == var3) {
            MapBanner var6 = MapBanner.fromWorld(blockGetter, var5.getPos());
            if(!var5.equals(var6)) {
               var4.remove();
               this.decorations.remove(var5.getId());
            }
         }
      }

   }

   public void removedFromFrame(BlockPos blockPos, int var2) {
      this.decorations.remove("frame-" + var2);
      this.frameMarkers.remove(MapFrame.frameId(blockPos));
   }

   public class HoldingPlayer {
      public final Player player;
      private boolean dirtyData = true;
      private int minDirtyX;
      private int minDirtyY;
      private int maxDirtyX = 127;
      private int maxDirtyY = 127;
      private int tick;
      public int step;

      public HoldingPlayer(Player player) {
         this.player = player;
      }

      @Nullable
      public Packet nextUpdatePacket(ItemStack itemStack) {
         if(this.dirtyData) {
            this.dirtyData = false;
            return new ClientboundMapItemDataPacket(MapItem.getMapId(itemStack), MapItemSavedData.this.scale, MapItemSavedData.this.trackingPosition, MapItemSavedData.this.locked, MapItemSavedData.this.decorations.values(), MapItemSavedData.this.colors, this.minDirtyX, this.minDirtyY, this.maxDirtyX + 1 - this.minDirtyX, this.maxDirtyY + 1 - this.minDirtyY);
         } else {
            return this.tick++ % 5 == 0?new ClientboundMapItemDataPacket(MapItem.getMapId(itemStack), MapItemSavedData.this.scale, MapItemSavedData.this.trackingPosition, MapItemSavedData.this.locked, MapItemSavedData.this.decorations.values(), MapItemSavedData.this.colors, 0, 0, 0, 0):null;
         }
      }

      public void markDirty(int minDirtyX, int minDirtyY) {
         if(this.dirtyData) {
            this.minDirtyX = Math.min(this.minDirtyX, minDirtyX);
            this.minDirtyY = Math.min(this.minDirtyY, minDirtyY);
            this.maxDirtyX = Math.max(this.maxDirtyX, minDirtyX);
            this.maxDirtyY = Math.max(this.maxDirtyY, minDirtyY);
         } else {
            this.dirtyData = true;
            this.minDirtyX = minDirtyX;
            this.minDirtyY = minDirtyY;
            this.maxDirtyX = minDirtyX;
            this.maxDirtyY = minDirtyY;
         }

      }
   }
}
