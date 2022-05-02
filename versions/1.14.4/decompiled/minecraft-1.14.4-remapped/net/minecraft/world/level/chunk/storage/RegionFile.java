package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.world.level.ChunkPos;

public class RegionFile implements AutoCloseable {
   private static final byte[] EMPTY_SECTOR = new byte[4096];
   private final RandomAccessFile file;
   private final int[] offsets = new int[1024];
   private final int[] chunkTimestamps = new int[1024];
   private final List sectorFree;

   public RegionFile(File file) throws IOException {
      this.file = new RandomAccessFile(file, "rw");
      if(this.file.length() < 4096L) {
         this.file.write(EMPTY_SECTOR);
         this.file.write(EMPTY_SECTOR);
      }

      if((this.file.length() & 4095L) != 0L) {
         for(int var2 = 0; (long)var2 < (this.file.length() & 4095L); ++var2) {
            this.file.write(0);
         }
      }

      int var2 = (int)this.file.length() / 4096;
      this.sectorFree = Lists.newArrayListWithCapacity(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         this.sectorFree.add(Boolean.valueOf(true));
      }

      this.sectorFree.set(0, Boolean.valueOf(false));
      this.sectorFree.set(1, Boolean.valueOf(false));
      this.file.seek(0L);

      for(int var3 = 0; var3 < 1024; ++var3) {
         int var4 = this.file.readInt();
         this.offsets[var3] = var4;
         if(var4 != 0 && (var4 >> 8) + (var4 & 255) <= this.sectorFree.size()) {
            for(int var5 = 0; var5 < (var4 & 255); ++var5) {
               this.sectorFree.set((var4 >> 8) + var5, Boolean.valueOf(false));
            }
         }
      }

      for(int var3 = 0; var3 < 1024; ++var3) {
         int var4 = this.file.readInt();
         this.chunkTimestamps[var3] = var4;
      }

   }

   @Nullable
   public synchronized DataInputStream getChunkDataInputStream(ChunkPos chunkPos) throws IOException {
      int var2 = this.getOffset(chunkPos);
      if(var2 == 0) {
         return null;
      } else {
         int var3 = var2 >> 8;
         int var4 = var2 & 255;
         if(var3 + var4 > this.sectorFree.size()) {
            return null;
         } else {
            this.file.seek((long)(var3 * 4096));
            int var5 = this.file.readInt();
            if(var5 > 4096 * var4) {
               return null;
            } else if(var5 <= 0) {
               return null;
            } else {
               byte var6 = this.file.readByte();
               if(var6 == 1) {
                  byte[] vars7 = new byte[var5 - 1];
                  this.file.read(vars7);
                  return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(vars7))));
               } else if(var6 == 2) {
                  byte[] vars7 = new byte[var5 - 1];
                  this.file.read(vars7);
                  return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(vars7))));
               } else {
                  return null;
               }
            }
         }
      }
   }

   public boolean doesChunkExist(ChunkPos chunkPos) {
      int var2 = this.getOffset(chunkPos);
      if(var2 == 0) {
         return false;
      } else {
         int var3 = var2 >> 8;
         int var4 = var2 & 255;
         if(var3 + var4 > this.sectorFree.size()) {
            return false;
         } else {
            try {
               this.file.seek((long)(var3 * 4096));
               int var5 = this.file.readInt();
               return var5 > 4096 * var4?false:var5 > 0;
            } catch (IOException var6) {
               return false;
            }
         }
      }
   }

   public DataOutputStream getChunkDataOutputStream(ChunkPos chunkPos) {
      return new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(chunkPos))));
   }

   protected synchronized void write(ChunkPos chunkPos, byte[] bytes, int var3) throws IOException {
      int var4 = this.getOffset(chunkPos);
      int var5 = var4 >> 8;
      int var6 = var4 & 255;
      int var7 = (var3 + 5) / 4096 + 1;
      if(var7 >= 256) {
         throw new RuntimeException(String.format("Too big to save, %d > 1048576", new Object[]{Integer.valueOf(var3)}));
      } else {
         if(var5 != 0 && var6 == var7) {
            this.write(var5, bytes, var3);
         } else {
            for(int var8 = 0; var8 < var6; ++var8) {
               this.sectorFree.set(var5 + var8, Boolean.valueOf(true));
            }

            int var8 = this.sectorFree.indexOf(Boolean.valueOf(true));
            int var9 = 0;
            if(var8 != -1) {
               for(int var10 = var8; var10 < this.sectorFree.size(); ++var10) {
                  if(var9 != 0) {
                     if(((Boolean)this.sectorFree.get(var10)).booleanValue()) {
                        ++var9;
                     } else {
                        var9 = 0;
                     }
                  } else if(((Boolean)this.sectorFree.get(var10)).booleanValue()) {
                     var8 = var10;
                     var9 = 1;
                  }

                  if(var9 >= var7) {
                     break;
                  }
               }
            }

            if(var9 >= var7) {
               var5 = var8;
               this.setOffset(chunkPos, var8 << 8 | var7);

               for(int var10 = 0; var10 < var7; ++var10) {
                  this.sectorFree.set(var5 + var10, Boolean.valueOf(false));
               }

               this.write(var5, bytes, var3);
            } else {
               this.file.seek(this.file.length());
               var5 = this.sectorFree.size();

               for(int var10 = 0; var10 < var7; ++var10) {
                  this.file.write(EMPTY_SECTOR);
                  this.sectorFree.add(Boolean.valueOf(false));
               }

               this.write(var5, bytes, var3);
               this.setOffset(chunkPos, var5 << 8 | var7);
            }
         }

         this.setTimestamp(chunkPos, (int)(Util.getEpochMillis() / 1000L));
      }
   }

   private void write(int var1, byte[] bytes, int var3) throws IOException {
      this.file.seek((long)(var1 * 4096));
      this.file.writeInt(var3 + 1);
      this.file.writeByte(2);
      this.file.write(bytes, 0, var3);
   }

   private int getOffset(ChunkPos chunkPos) {
      return this.offsets[this.getOffsetIndex(chunkPos)];
   }

   public boolean hasChunk(ChunkPos chunkPos) {
      return this.getOffset(chunkPos) != 0;
   }

   private void setOffset(ChunkPos chunkPos, int var2) throws IOException {
      int var3 = this.getOffsetIndex(chunkPos);
      this.offsets[var3] = var2;
      this.file.seek((long)(var3 * 4));
      this.file.writeInt(var2);
   }

   private int getOffsetIndex(ChunkPos chunkPos) {
      return chunkPos.getRegionLocalX() + chunkPos.getRegionLocalZ() * 32;
   }

   private void setTimestamp(ChunkPos chunkPos, int var2) throws IOException {
      int var3 = this.getOffsetIndex(chunkPos);
      this.chunkTimestamps[var3] = var2;
      this.file.seek((long)(4096 + var3 * 4));
      this.file.writeInt(var2);
   }

   public void close() throws IOException {
      this.file.close();
   }

   class ChunkBuffer extends ByteArrayOutputStream {
      private final ChunkPos pos;

      public ChunkBuffer(ChunkPos pos) {
         super(8096);
         this.pos = pos;
      }

      public void close() throws IOException {
         RegionFile.this.write(this.pos, this.buf, this.count);
      }
   }
}
