package com.mojang.blaze3d.platform;

import com.fox2code.repacker.ClientJarOnly;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import org.lwjgl.stb.STBIEOFCallback;
import org.lwjgl.stb.STBIEOFCallbackI;
import org.lwjgl.stb.STBIIOCallbacks;
import org.lwjgl.stb.STBIReadCallback;
import org.lwjgl.stb.STBIReadCallbackI;
import org.lwjgl.stb.STBISkipCallback;
import org.lwjgl.stb.STBISkipCallbackI;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@ClientJarOnly
public class PngInfo {
   public final int width;
   public final int height;

   public PngInfo(String string, InputStream inputStream) throws IOException {
      MemoryStack var3 = MemoryStack.stackPush();
      Throwable var4 = null;

      try {
         PngInfo.StbReader var5 = createCallbacks(inputStream);
         Throwable var6 = null;

         try {
            var5.getClass();
            STBIReadCallback var7 = STBIReadCallback.create(var5::read);
            Throwable var8 = null;

            try {
               var5.getClass();
               STBISkipCallback var9 = STBISkipCallback.create(var5::skip);
               Throwable var10 = null;

               try {
                  var5.getClass();
                  STBIEOFCallback var11 = STBIEOFCallback.create(var5::eof);
                  Throwable var12 = null;

                  try {
                     STBIIOCallbacks var13 = STBIIOCallbacks.mallocStack(var3);
                     var13.read(var7);
                     var13.skip(var9);
                     var13.eof(var11);
                     IntBuffer var14 = var3.mallocInt(1);
                     IntBuffer var15 = var3.mallocInt(1);
                     IntBuffer var16 = var3.mallocInt(1);
                     if(!STBImage.stbi_info_from_callbacks(var13, 0L, var14, var15, var16)) {
                        throw new IOException("Could not read info from the PNG file " + string + " " + STBImage.stbi_failure_reason());
                     }

                     this.width = var14.get(0);
                     this.height = var15.get(0);
                  } catch (Throwable var122) {
                     var12 = var122;
                     throw var122;
                  } finally {
                     if(var11 != null) {
                        if(var12 != null) {
                           try {
                              var11.close();
                           } catch (Throwable var121) {
                              var12.addSuppressed(var121);
                           }
                        } else {
                           var11.close();
                        }
                     }

                  }
               } catch (Throwable var124) {
                  var10 = var124;
                  throw var124;
               } finally {
                  if(var9 != null) {
                     if(var10 != null) {
                        try {
                           var9.close();
                        } catch (Throwable var120) {
                           var10.addSuppressed(var120);
                        }
                     } else {
                        var9.close();
                     }
                  }

               }
            } catch (Throwable var126) {
               var8 = var126;
               throw var126;
            } finally {
               if(var7 != null) {
                  if(var8 != null) {
                     try {
                        var7.close();
                     } catch (Throwable var119) {
                        var8.addSuppressed(var119);
                     }
                  } else {
                     var7.close();
                  }
               }

            }
         } catch (Throwable var128) {
            var6 = var128;
            throw var128;
         } finally {
            if(var5 != null) {
               if(var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var118) {
                     var6.addSuppressed(var118);
                  }
               } else {
                  var5.close();
               }
            }

         }
      } catch (Throwable var130) {
         var4 = var130;
         throw var130;
      } finally {
         if(var3 != null) {
            if(var4 != null) {
               try {
                  var3.close();
               } catch (Throwable var117) {
                  var4.addSuppressed(var117);
               }
            } else {
               var3.close();
            }
         }

      }

   }

   private static PngInfo.StbReader createCallbacks(InputStream inputStream) {
      return (PngInfo.StbReader)(inputStream instanceof FileInputStream?new PngInfo.StbReaderSeekableByteChannel(((FileInputStream)inputStream).getChannel()):new PngInfo.StbReaderBufferedChannel(Channels.newChannel(inputStream)));
   }

   @ClientJarOnly
   abstract static class StbReader implements AutoCloseable {
      protected boolean closed;

      private StbReader() {
      }

      int read(long var1, long var3, int var5) {
         try {
            return this.read(var3, var5);
         } catch (IOException var7) {
            this.closed = true;
            return 0;
         }
      }

      void skip(long var1, int var3) {
         try {
            this.skip(var3);
         } catch (IOException var5) {
            this.closed = true;
         }

      }

      int eof(long l) {
         return this.closed?1:0;
      }

      protected abstract int read(long var1, int var3) throws IOException;

      protected abstract void skip(int var1) throws IOException;

      public abstract void close() throws IOException;
   }

   @ClientJarOnly
   static class StbReaderBufferedChannel extends PngInfo.StbReader {
      private final ReadableByteChannel channel;
      private long readBufferAddress;
      private int bufferSize;
      private int read;
      private int consumed;

      private StbReaderBufferedChannel(ReadableByteChannel channel) {
         super(null);
         this.readBufferAddress = MemoryUtil.nmemAlloc(128L);
         this.bufferSize = 128;
         this.channel = channel;
      }

      private void fillReadBuffer(int i) throws IOException {
         ByteBuffer var2 = MemoryUtil.memByteBuffer(this.readBufferAddress, this.bufferSize);
         if(i + this.consumed > this.bufferSize) {
            this.bufferSize = i + this.consumed;
            var2 = MemoryUtil.memRealloc(var2, this.bufferSize);
            this.readBufferAddress = MemoryUtil.memAddress(var2);
         }

         var2.position(this.read);

         while(i + this.consumed > this.read) {
            try {
               int var3 = this.channel.read(var2);
               if(var3 == -1) {
                  break;
               }
            } finally {
               this.read = var2.position();
            }
         }

      }

      public int read(long var1, int var3) throws IOException {
         this.fillReadBuffer(var3);
         if(var3 + this.consumed > this.read) {
            var3 = this.read - this.consumed;
         }

         MemoryUtil.memCopy(this.readBufferAddress + (long)this.consumed, var1, (long)var3);
         this.consumed += var3;
         return var3;
      }

      public void skip(int i) throws IOException {
         if(i > 0) {
            this.fillReadBuffer(i);
            if(i + this.consumed > this.read) {
               throw new EOFException("Can\'t skip past the EOF.");
            }
         }

         if(this.consumed + i < 0) {
            throw new IOException("Can\'t seek before the beginning: " + (this.consumed + i));
         } else {
            this.consumed += i;
         }
      }

      public void close() throws IOException {
         MemoryUtil.nmemFree(this.readBufferAddress);
         this.channel.close();
      }
   }

   @ClientJarOnly
   static class StbReaderSeekableByteChannel extends PngInfo.StbReader {
      private final SeekableByteChannel channel;

      private StbReaderSeekableByteChannel(SeekableByteChannel channel) {
         super(null);
         this.channel = channel;
      }

      public int read(long var1, int var3) throws IOException {
         ByteBuffer var4 = MemoryUtil.memByteBuffer(var1, var3);
         return this.channel.read(var4);
      }

      public void skip(int i) throws IOException {
         this.channel.position(this.channel.position() + (long)i);
      }

      public int eof(long l) {
         return super.eof(l) != 0 && this.channel.isOpen()?1:0;
      }

      public void close() throws IOException {
         this.channel.close();
      }
   }
}
