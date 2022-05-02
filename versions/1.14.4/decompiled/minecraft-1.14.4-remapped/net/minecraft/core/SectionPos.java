package net.minecraft.core;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

public class SectionPos extends Vec3i {
   private SectionPos(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public static SectionPos of(int var0, int var1, int var2) {
      return new SectionPos(var0, var1, var2);
   }

   public static SectionPos of(BlockPos blockPos) {
      return new SectionPos(blockToSectionCoord(blockPos.getX()), blockToSectionCoord(blockPos.getY()), blockToSectionCoord(blockPos.getZ()));
   }

   public static SectionPos of(ChunkPos chunkPos, int var1) {
      return new SectionPos(chunkPos.x, var1, chunkPos.z);
   }

   public static SectionPos of(Entity entity) {
      return new SectionPos(blockToSectionCoord(Mth.floor(entity.x)), blockToSectionCoord(Mth.floor(entity.y)), blockToSectionCoord(Mth.floor(entity.z)));
   }

   public static SectionPos of(long l) {
      return new SectionPos(x(l), y(l), z(l));
   }

   public static long offset(long var0, Direction direction) {
      return offset(var0, direction.getStepX(), direction.getStepY(), direction.getStepZ());
   }

   public static long offset(long var0, int var2, int var3, int var4) {
      return asLong(x(var0) + var2, y(var0) + var3, z(var0) + var4);
   }

   public static int blockToSectionCoord(int i) {
      return i >> 4;
   }

   public static int sectionRelative(int i) {
      return i & 15;
   }

   public static short sectionRelativePos(BlockPos blockPos) {
      int var1 = sectionRelative(blockPos.getX());
      int var2 = sectionRelative(blockPos.getY());
      int var3 = sectionRelative(blockPos.getZ());
      return (short)(var1 << 8 | var3 << 4 | var2);
   }

   public static int sectionToBlockCoord(int i) {
      return i << 4;
   }

   public static int x(long l) {
      return (int)(l << 0 >> 42);
   }

   public static int y(long l) {
      return (int)(l << 44 >> 44);
   }

   public static int z(long l) {
      return (int)(l << 22 >> 42);
   }

   public int x() {
      return this.getX();
   }

   public int y() {
      return this.getY();
   }

   public int z() {
      return this.getZ();
   }

   public int minBlockX() {
      return this.x() << 4;
   }

   public int minBlockY() {
      return this.y() << 4;
   }

   public int minBlockZ() {
      return this.z() << 4;
   }

   public int maxBlockX() {
      return (this.x() << 4) + 15;
   }

   public int maxBlockY() {
      return (this.y() << 4) + 15;
   }

   public int maxBlockZ() {
      return (this.z() << 4) + 15;
   }

   public static long blockToSection(long l) {
      return asLong(blockToSectionCoord(BlockPos.getX(l)), blockToSectionCoord(BlockPos.getY(l)), blockToSectionCoord(BlockPos.getZ(l)));
   }

   public static long getZeroNode(long l) {
      return l & -1048576L;
   }

   public BlockPos origin() {
      return new BlockPos(sectionToBlockCoord(this.x()), sectionToBlockCoord(this.y()), sectionToBlockCoord(this.z()));
   }

   public BlockPos center() {
      int var1 = 8;
      return this.origin().offset(8, 8, 8);
   }

   public ChunkPos chunk() {
      return new ChunkPos(this.x(), this.z());
   }

   public static long asLong(int var0, int var1, int var2) {
      long var3 = 0L;
      var3 = var3 | ((long)var0 & 4194303L) << 42;
      var3 = var3 | ((long)var1 & 1048575L) << 0;
      var3 = var3 | ((long)var2 & 4194303L) << 20;
      return var3;
   }

   public long asLong() {
      return asLong(this.x(), this.y(), this.z());
   }

   public Stream blocksInside() {
      return BlockPos.betweenClosedStream(this.minBlockX(), this.minBlockY(), this.minBlockZ(), this.maxBlockX(), this.maxBlockY(), this.maxBlockZ());
   }

   public static Stream cube(SectionPos sectionPos, int var1) {
      int var2 = sectionPos.x();
      int var3 = sectionPos.y();
      int var4 = sectionPos.z();
      return betweenClosedStream(var2 - var1, var3 - var1, var4 - var1, var2 + var1, var3 + var1, var4 + var1);
   }

   public static Stream betweenClosedStream(final int var0, final int var1, final int var2, final int var3, final int var4, final int var5) {
      return StreamSupport.stream(new AbstractSpliterator((long)((var3 - var0 + 1) * (var4 - var1 + 1) * (var5 - var2 + 1)), var0) {
         final Cursor3D cursor = new Cursor3D(var0, var1, var2, var3, var4, var5);

         public boolean tryAdvance(Consumer consumer) {
            if(this.cursor.advance()) {
               consumer.accept(new SectionPos(this.cursor.nextX(), this.cursor.nextY(), this.cursor.nextZ()));
               return true;
            } else {
               return false;
            }
         }
      }, false);
   }
}
