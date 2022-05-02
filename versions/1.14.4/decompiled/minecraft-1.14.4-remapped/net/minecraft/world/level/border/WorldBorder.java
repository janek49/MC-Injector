package net.minecraft.world.level.border;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.BorderStatus;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WorldBorder {
   private final List listeners = Lists.newArrayList();
   private double damagePerBlock = 0.2D;
   private double damageSafeZone = 5.0D;
   private int warningTime = 15;
   private int warningBlocks = 5;
   private double centerX;
   private double centerZ;
   private int absoluteMaxSize = 29999984;
   private WorldBorder.BorderExtent extent = new WorldBorder.StaticBorderExtent(6.0E7D);

   public boolean isWithinBounds(BlockPos blockPos) {
      return (double)(blockPos.getX() + 1) > this.getMinX() && (double)blockPos.getX() < this.getMaxX() && (double)(blockPos.getZ() + 1) > this.getMinZ() && (double)blockPos.getZ() < this.getMaxZ();
   }

   public boolean isWithinBounds(ChunkPos chunkPos) {
      return (double)chunkPos.getMaxBlockX() > this.getMinX() && (double)chunkPos.getMinBlockX() < this.getMaxX() && (double)chunkPos.getMaxBlockZ() > this.getMinZ() && (double)chunkPos.getMinBlockZ() < this.getMaxZ();
   }

   public boolean isWithinBounds(AABB aABB) {
      return aABB.maxX > this.getMinX() && aABB.minX < this.getMaxX() && aABB.maxZ > this.getMinZ() && aABB.minZ < this.getMaxZ();
   }

   public double getDistanceToBorder(Entity entity) {
      return this.getDistanceToBorder(entity.x, entity.z);
   }

   public VoxelShape getCollisionShape() {
      return this.extent.getCollisionShape();
   }

   public double getDistanceToBorder(double var1, double var3) {
      double var5 = var3 - this.getMinZ();
      double var7 = this.getMaxZ() - var3;
      double var9 = var1 - this.getMinX();
      double var11 = this.getMaxX() - var1;
      double var13 = Math.min(var9, var11);
      var13 = Math.min(var13, var5);
      return Math.min(var13, var7);
   }

   public BorderStatus getStatus() {
      return this.extent.getStatus();
   }

   public double getMinX() {
      return this.extent.getMinX();
   }

   public double getMinZ() {
      return this.extent.getMinZ();
   }

   public double getMaxX() {
      return this.extent.getMaxX();
   }

   public double getMaxZ() {
      return this.extent.getMaxZ();
   }

   public double getCenterX() {
      return this.centerX;
   }

   public double getCenterZ() {
      return this.centerZ;
   }

   public void setCenter(double centerX, double centerZ) {
      this.centerX = centerX;
      this.centerZ = centerZ;
      this.extent.onCenterChange();

      for(BorderChangeListener var6 : this.getListeners()) {
         var6.onBorderCenterSet(this, centerX, centerZ);
      }

   }

   public double getSize() {
      return this.extent.getSize();
   }

   public long getLerpRemainingTime() {
      return this.extent.getLerpRemainingTime();
   }

   public double getLerpTarget() {
      return this.extent.getLerpTarget();
   }

   public void setSize(double size) {
      this.extent = new WorldBorder.StaticBorderExtent(size);

      for(BorderChangeListener var4 : this.getListeners()) {
         var4.onBorderSizeSet(this, size);
      }

   }

   public void lerpSizeBetween(double var1, double var3, long var5) {
      this.extent = (WorldBorder.BorderExtent)(var1 == var3?new WorldBorder.StaticBorderExtent(var3):new WorldBorder.MovingBorderExtent(var1, var3, var5));

      for(BorderChangeListener var8 : this.getListeners()) {
         var8.onBorderSizeLerping(this, var1, var3, var5);
      }

   }

   protected List getListeners() {
      return Lists.newArrayList(this.listeners);
   }

   public void addListener(BorderChangeListener borderChangeListener) {
      this.listeners.add(borderChangeListener);
   }

   public void setAbsoluteMaxSize(int absoluteMaxSize) {
      this.absoluteMaxSize = absoluteMaxSize;
      this.extent.onAbsoluteMaxSizeChange();
   }

   public int getAbsoluteMaxSize() {
      return this.absoluteMaxSize;
   }

   public double getDamageSafeZone() {
      return this.damageSafeZone;
   }

   public void setDamageSafeZone(double damageSafeZone) {
      this.damageSafeZone = damageSafeZone;

      for(BorderChangeListener var4 : this.getListeners()) {
         var4.onBorderSetDamageSafeZOne(this, damageSafeZone);
      }

   }

   public double getDamagePerBlock() {
      return this.damagePerBlock;
   }

   public void setDamagePerBlock(double damagePerBlock) {
      this.damagePerBlock = damagePerBlock;

      for(BorderChangeListener var4 : this.getListeners()) {
         var4.onBorderSetDamagePerBlock(this, damagePerBlock);
      }

   }

   public double getLerpSpeed() {
      return this.extent.getLerpSpeed();
   }

   public int getWarningTime() {
      return this.warningTime;
   }

   public void setWarningTime(int warningTime) {
      this.warningTime = warningTime;

      for(BorderChangeListener var3 : this.getListeners()) {
         var3.onBorderSetWarningTime(this, warningTime);
      }

   }

   public int getWarningBlocks() {
      return this.warningBlocks;
   }

   public void setWarningBlocks(int warningBlocks) {
      this.warningBlocks = warningBlocks;

      for(BorderChangeListener var3 : this.getListeners()) {
         var3.onBorderSetWarningBlocks(this, warningBlocks);
      }

   }

   public void tick() {
      this.extent = this.extent.update();
   }

   public void saveWorldBorderData(LevelData levelData) {
      levelData.setBorderSize(this.getSize());
      levelData.setBorderX(this.getCenterX());
      levelData.setBorderZ(this.getCenterZ());
      levelData.setBorderSafeZone(this.getDamageSafeZone());
      levelData.setBorderDamagePerBlock(this.getDamagePerBlock());
      levelData.setBorderWarningBlocks(this.getWarningBlocks());
      levelData.setBorderWarningTime(this.getWarningTime());
      levelData.setBorderSizeLerpTarget(this.getLerpTarget());
      levelData.setBorderSizeLerpTime(this.getLerpRemainingTime());
   }

   public void readBorderData(LevelData levelData) {
      this.setCenter(levelData.getBorderX(), levelData.getBorderZ());
      this.setDamagePerBlock(levelData.getBorderDamagePerBlock());
      this.setDamageSafeZone(levelData.getBorderSafeZone());
      this.setWarningBlocks(levelData.getBorderWarningBlocks());
      this.setWarningTime(levelData.getBorderWarningTime());
      if(levelData.getBorderSizeLerpTime() > 0L) {
         this.lerpSizeBetween(levelData.getBorderSize(), levelData.getBorderSizeLerpTarget(), levelData.getBorderSizeLerpTime());
      } else {
         this.setSize(levelData.getBorderSize());
      }

   }

   interface BorderExtent {
      double getMinX();

      double getMaxX();

      double getMinZ();

      double getMaxZ();

      double getSize();

      double getLerpSpeed();

      long getLerpRemainingTime();

      double getLerpTarget();

      BorderStatus getStatus();

      void onAbsoluteMaxSizeChange();

      void onCenterChange();

      WorldBorder.BorderExtent update();

      VoxelShape getCollisionShape();
   }

   class MovingBorderExtent implements WorldBorder.BorderExtent {
      private final double from;
      private final double to;
      private final long lerpEnd;
      private final long lerpBegin;
      private final double lerpDuration;

      private MovingBorderExtent(double from, double to, long var6) {
         this.from = from;
         this.to = to;
         this.lerpDuration = (double)var6;
         this.lerpBegin = Util.getMillis();
         this.lerpEnd = this.lerpBegin + var6;
      }

      public double getMinX() {
         return Math.max(WorldBorder.this.getCenterX() - this.getSize() / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize));
      }

      public double getMinZ() {
         return Math.max(WorldBorder.this.getCenterZ() - this.getSize() / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize));
      }

      public double getMaxX() {
         return Math.min(WorldBorder.this.getCenterX() + this.getSize() / 2.0D, (double)WorldBorder.this.absoluteMaxSize);
      }

      public double getMaxZ() {
         return Math.min(WorldBorder.this.getCenterZ() + this.getSize() / 2.0D, (double)WorldBorder.this.absoluteMaxSize);
      }

      public double getSize() {
         double var1 = (double)(Util.getMillis() - this.lerpBegin) / this.lerpDuration;
         return var1 < 1.0D?Mth.lerp(var1, this.from, this.to):this.to;
      }

      public double getLerpSpeed() {
         return Math.abs(this.from - this.to) / (double)(this.lerpEnd - this.lerpBegin);
      }

      public long getLerpRemainingTime() {
         return this.lerpEnd - Util.getMillis();
      }

      public double getLerpTarget() {
         return this.to;
      }

      public BorderStatus getStatus() {
         return this.to < this.from?BorderStatus.SHRINKING:BorderStatus.GROWING;
      }

      public void onCenterChange() {
      }

      public void onAbsoluteMaxSizeChange() {
      }

      public WorldBorder.BorderExtent update() {
         return (WorldBorder.BorderExtent)(this.getLerpRemainingTime() <= 0L?WorldBorder.this.new StaticBorderExtent(this.to):this);
      }

      public VoxelShape getCollisionShape() {
         return Shapes.join(Shapes.INFINITY, Shapes.box(Math.floor(this.getMinX()), Double.NEGATIVE_INFINITY, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), Double.POSITIVE_INFINITY, Math.ceil(this.getMaxZ())), BooleanOp.ONLY_FIRST);
      }
   }

   class StaticBorderExtent implements WorldBorder.BorderExtent {
      private final double size;
      private double minX;
      private double minZ;
      private double maxX;
      private double maxZ;
      private VoxelShape shape;

      public StaticBorderExtent(double size) {
         this.size = size;
         this.updateBox();
      }

      public double getMinX() {
         return this.minX;
      }

      public double getMaxX() {
         return this.maxX;
      }

      public double getMinZ() {
         return this.minZ;
      }

      public double getMaxZ() {
         return this.maxZ;
      }

      public double getSize() {
         return this.size;
      }

      public BorderStatus getStatus() {
         return BorderStatus.STATIONARY;
      }

      public double getLerpSpeed() {
         return 0.0D;
      }

      public long getLerpRemainingTime() {
         return 0L;
      }

      public double getLerpTarget() {
         return this.size;
      }

      private void updateBox() {
         this.minX = Math.max(WorldBorder.this.getCenterX() - this.size / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize));
         this.minZ = Math.max(WorldBorder.this.getCenterZ() - this.size / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize));
         this.maxX = Math.min(WorldBorder.this.getCenterX() + this.size / 2.0D, (double)WorldBorder.this.absoluteMaxSize);
         this.maxZ = Math.min(WorldBorder.this.getCenterZ() + this.size / 2.0D, (double)WorldBorder.this.absoluteMaxSize);
         this.shape = Shapes.join(Shapes.INFINITY, Shapes.box(Math.floor(this.getMinX()), Double.NEGATIVE_INFINITY, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), Double.POSITIVE_INFINITY, Math.ceil(this.getMaxZ())), BooleanOp.ONLY_FIRST);
      }

      public void onAbsoluteMaxSizeChange() {
         this.updateBox();
      }

      public void onCenterChange() {
         this.updateBox();
      }

      public WorldBorder.BorderExtent update() {
         return this;
      }

      public VoxelShape getCollisionShape() {
         return this.shape;
      }
   }
}
