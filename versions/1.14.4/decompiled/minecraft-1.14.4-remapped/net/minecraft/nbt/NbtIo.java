package net.minecraft.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;

public class NbtIo {
   public static CompoundTag readCompressed(InputStream inputStream) throws IOException {
      DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputStream)));
      Throwable var2 = null;

      CompoundTag var3;
      try {
         var3 = read(var1, NbtAccounter.UNLIMITED);
      } catch (Throwable var12) {
         var2 = var12;
         throw var12;
      } finally {
         if(var1 != null) {
            if(var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var11) {
                  var2.addSuppressed(var11);
               }
            } else {
               var1.close();
            }
         }

      }

      return var3;
   }

   public static void writeCompressed(CompoundTag compoundTag, OutputStream outputStream) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)));
      Throwable var3 = null;

      try {
         write(compoundTag, (DataOutput)var2);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if(var2 != null) {
            if(var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   public static void safeWrite(CompoundTag compoundTag, File file) throws IOException {
      File file = new File(file.getAbsolutePath() + "_tmp");
      if(file.exists()) {
         file.delete();
      }

      write(compoundTag, file);
      if(file.exists()) {
         file.delete();
      }

      if(file.exists()) {
         throw new IOException("Failed to delete " + file);
      } else {
         file.renameTo(file);
      }
   }

   public static void write(CompoundTag compoundTag, File file) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FileOutputStream(file));

      try {
         write(compoundTag, (DataOutput)var2);
      } finally {
         var2.close();
      }

   }

   @Nullable
   public static CompoundTag read(File file) throws IOException {
      if(!file.exists()) {
         return null;
      } else {
         DataInputStream var1 = new DataInputStream(new FileInputStream(file));

         CompoundTag var2;
         try {
            var2 = read(var1, NbtAccounter.UNLIMITED);
         } finally {
            var1.close();
         }

         return var2;
      }
   }

   public static CompoundTag read(DataInputStream dataInputStream) throws IOException {
      return read(dataInputStream, NbtAccounter.UNLIMITED);
   }

   public static CompoundTag read(DataInput dataInput, NbtAccounter nbtAccounter) throws IOException {
      Tag var2 = readUnnamedTag(dataInput, 0, nbtAccounter);
      if(var2 instanceof CompoundTag) {
         return (CompoundTag)var2;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void write(CompoundTag compoundTag, DataOutput dataOutput) throws IOException {
      writeUnnamedTag(compoundTag, dataOutput);
   }

   private static void writeUnnamedTag(Tag tag, DataOutput dataOutput) throws IOException {
      dataOutput.writeByte(tag.getId());
      if(tag.getId() != 0) {
         dataOutput.writeUTF("");
         tag.write(dataOutput);
      }
   }

   private static Tag readUnnamedTag(DataInput dataInput, int var1, NbtAccounter nbtAccounter) throws IOException {
      byte var3 = dataInput.readByte();
      if(var3 == 0) {
         return new EndTag();
      } else {
         dataInput.readUTF();
         Tag var4 = Tag.newTag(var3);

         try {
            var4.load(dataInput, var1, nbtAccounter);
            return var4;
         } catch (IOException var8) {
            CrashReport var6 = CrashReport.forThrowable(var8, "Loading NBT data");
            CrashReportCategory var7 = var6.addCategory("NBT Tag");
            var7.setDetail("Tag type", (Object)Byte.valueOf(var3));
            throw new ReportedException(var6);
         }
      }
   }
}
