package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FriendlyByteBuf extends ByteBuf {
   private final ByteBuf source;

   public FriendlyByteBuf(ByteBuf source) {
      this.source = source;
   }

   public static int getVarIntSize(int i) {
      for(int var1 = 1; var1 < 5; ++var1) {
         if((i & -1 << var1 * 7) == 0) {
            return var1;
         }
      }

      return 5;
   }

   public FriendlyByteBuf writeByteArray(byte[] bytes) {
      this.writeVarInt(bytes.length);
      this.writeBytes(bytes);
      return this;
   }

   public byte[] readByteArray() {
      return this.readByteArray(this.readableBytes());
   }

   public byte[] readByteArray(int i) {
      int var2 = this.readVarInt();
      if(var2 > i) {
         throw new DecoderException("ByteArray with size " + var2 + " is bigger than allowed " + i);
      } else {
         byte[] vars3 = new byte[var2];
         this.readBytes(vars3);
         return vars3;
      }
   }

   public FriendlyByteBuf writeVarIntArray(int[] ints) {
      this.writeVarInt(ints.length);

      for(int var5 : ints) {
         this.writeVarInt(var5);
      }

      return this;
   }

   public int[] readVarIntArray() {
      return this.readVarIntArray(this.readableBytes());
   }

   public int[] readVarIntArray(int i) {
      int var2 = this.readVarInt();
      if(var2 > i) {
         throw new DecoderException("VarIntArray with size " + var2 + " is bigger than allowed " + i);
      } else {
         int[] vars3 = new int[var2];

         for(int var4 = 0; var4 < vars3.length; ++var4) {
            vars3[var4] = this.readVarInt();
         }

         return vars3;
      }
   }

   public FriendlyByteBuf writeLongArray(long[] longs) {
      this.writeVarInt(longs.length);

      for(long var5 : longs) {
         this.writeLong(var5);
      }

      return this;
   }

   public long[] readLongArray(@Nullable long[] longs) {
      return this.readLongArray(longs, this.readableBytes() / 8);
   }

   public long[] readLongArray(@Nullable long[] vars1, int var2) {
      int var3 = this.readVarInt();
      if(vars1 == null || vars1.length != var3) {
         if(var3 > var2) {
            throw new DecoderException("LongArray with size " + var3 + " is bigger than allowed " + var2);
         }

         vars1 = new long[var3];
      }

      for(int var4 = 0; var4 < vars1.length; ++var4) {
         vars1[var4] = this.readLong();
      }

      return vars1;
   }

   public BlockPos readBlockPos() {
      return BlockPos.of(this.readLong());
   }

   public FriendlyByteBuf writeBlockPos(BlockPos blockPos) {
      this.writeLong(blockPos.asLong());
      return this;
   }

   public SectionPos readSectionPos() {
      return SectionPos.of(this.readLong());
   }

   public Component readComponent() {
      return Component.Serializer.fromJson(this.readUtf(262144));
   }

   public FriendlyByteBuf writeComponent(Component component) {
      return this.writeUtf(Component.Serializer.toJson(component), 262144);
   }

   public Enum readEnum(Class class) {
      return ((Enum[])class.getEnumConstants())[this.readVarInt()];
   }

   public FriendlyByteBuf writeEnum(Enum enum) {
      return this.writeVarInt(enum.ordinal());
   }

   public int readVarInt() {
      int var1 = 0;
      int var2 = 0;

      while(true) {
         byte var3 = this.readByte();
         var1 |= (var3 & 127) << var2++ * 7;
         if(var2 > 5) {
            throw new RuntimeException("VarInt too big");
         }

         if((var3 & 128) != 128) {
            break;
         }
      }

      return var1;
   }

   public long readVarLong() {
      long var1 = 0L;
      int var3 = 0;

      while(true) {
         byte var4 = this.readByte();
         var1 |= (long)(var4 & 127) << var3++ * 7;
         if(var3 > 10) {
            throw new RuntimeException("VarLong too big");
         }

         if((var4 & 128) != 128) {
            break;
         }
      }

      return var1;
   }

   public FriendlyByteBuf writeUUID(UUID uUID) {
      this.writeLong(uUID.getMostSignificantBits());
      this.writeLong(uUID.getLeastSignificantBits());
      return this;
   }

   public UUID readUUID() {
      return new UUID(this.readLong(), this.readLong());
   }

   public FriendlyByteBuf writeVarInt(int i) {
      while((i & -128) != 0) {
         this.writeByte(i & 127 | 128);
         i >>>= 7;
      }

      this.writeByte(i);
      return this;
   }

   public FriendlyByteBuf writeVarLong(long l) {
      while((l & -128L) != 0L) {
         this.writeByte((int)(l & 127L) | 128);
         l >>>= 7;
      }

      this.writeByte((int)l);
      return this;
   }

   public FriendlyByteBuf writeNbt(@Nullable CompoundTag compoundTag) {
      if(compoundTag == null) {
         this.writeByte(0);
      } else {
         try {
            NbtIo.write(compoundTag, (DataOutput)(new ByteBufOutputStream(this)));
         } catch (IOException var3) {
            throw new EncoderException(var3);
         }
      }

      return this;
   }

   @Nullable
   public CompoundTag readNbt() {
      int var1 = this.readerIndex();
      byte var2 = this.readByte();
      if(var2 == 0) {
         return null;
      } else {
         this.readerIndex(var1);

         try {
            return NbtIo.read(new ByteBufInputStream(this), new NbtAccounter(2097152L));
         } catch (IOException var4) {
            throw new EncoderException(var4);
         }
      }
   }

   public FriendlyByteBuf writeItem(ItemStack itemStack) {
      if(itemStack.isEmpty()) {
         this.writeBoolean(false);
      } else {
         this.writeBoolean(true);
         Item var2 = itemStack.getItem();
         this.writeVarInt(Item.getId(var2));
         this.writeByte(itemStack.getCount());
         CompoundTag var3 = null;
         if(var2.canBeDepleted() || var2.shouldOverrideMultiplayerNbt()) {
            var3 = itemStack.getTag();
         }

         this.writeNbt(var3);
      }

      return this;
   }

   public ItemStack readItem() {
      if(!this.readBoolean()) {
         return ItemStack.EMPTY;
      } else {
         int var1 = this.readVarInt();
         int var2 = this.readByte();
         ItemStack var3 = new ItemStack(Item.byId(var1), var2);
         var3.setTag(this.readNbt());
         return var3;
      }
   }

   public String readUtf() {
      return this.readUtf(32767);
   }

   public String readUtf(int i) {
      int var2 = this.readVarInt();
      if(var2 > i * 4) {
         throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + var2 + " > " + i * 4 + ")");
      } else if(var2 < 0) {
         throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
      } else {
         String var3 = this.toString(this.readerIndex(), var2, StandardCharsets.UTF_8);
         this.readerIndex(this.readerIndex() + var2);
         if(var3.length() > i) {
            throw new DecoderException("The received string length is longer than maximum allowed (" + var2 + " > " + i + ")");
         } else {
            return var3;
         }
      }
   }

   public FriendlyByteBuf writeUtf(String string) {
      return this.writeUtf(string, 32767);
   }

   public FriendlyByteBuf writeUtf(String string, int var2) {
      byte[] vars3 = string.getBytes(StandardCharsets.UTF_8);
      if(vars3.length > var2) {
         throw new EncoderException("String too big (was " + vars3.length + " bytes encoded, max " + var2 + ")");
      } else {
         this.writeVarInt(vars3.length);
         this.writeBytes(vars3);
         return this;
      }
   }

   public ResourceLocation readResourceLocation() {
      return new ResourceLocation(this.readUtf(32767));
   }

   public FriendlyByteBuf writeResourceLocation(ResourceLocation resourceLocation) {
      this.writeUtf(resourceLocation.toString());
      return this;
   }

   public Date readDate() {
      return new Date(this.readLong());
   }

   public FriendlyByteBuf writeDate(Date date) {
      this.writeLong(date.getTime());
      return this;
   }

   public BlockHitResult readBlockHitResult() {
      BlockPos var1 = this.readBlockPos();
      Direction var2 = (Direction)this.readEnum(Direction.class);
      float var3 = this.readFloat();
      float var4 = this.readFloat();
      float var5 = this.readFloat();
      boolean var6 = this.readBoolean();
      return new BlockHitResult(new Vec3((double)((float)var1.getX() + var3), (double)((float)var1.getY() + var4), (double)((float)var1.getZ() + var5)), var2, var1, var6);
   }

   public void writeBlockHitResult(BlockHitResult blockHitResult) {
      BlockPos var2 = blockHitResult.getBlockPos();
      this.writeBlockPos(var2);
      this.writeEnum(blockHitResult.getDirection());
      Vec3 var3 = blockHitResult.getLocation();
      this.writeFloat((float)(var3.x - (double)var2.getX()));
      this.writeFloat((float)(var3.y - (double)var2.getY()));
      this.writeFloat((float)(var3.z - (double)var2.getZ()));
      this.writeBoolean(blockHitResult.isInside());
   }

   public int capacity() {
      return this.source.capacity();
   }

   public ByteBuf capacity(int i) {
      return this.source.capacity(i);
   }

   public int maxCapacity() {
      return this.source.maxCapacity();
   }

   public ByteBufAllocator alloc() {
      return this.source.alloc();
   }

   public ByteOrder order() {
      return this.source.order();
   }

   public ByteBuf order(ByteOrder byteOrder) {
      return this.source.order(byteOrder);
   }

   public ByteBuf unwrap() {
      return this.source.unwrap();
   }

   public boolean isDirect() {
      return this.source.isDirect();
   }

   public boolean isReadOnly() {
      return this.source.isReadOnly();
   }

   public ByteBuf asReadOnly() {
      return this.source.asReadOnly();
   }

   public int readerIndex() {
      return this.source.readerIndex();
   }

   public ByteBuf readerIndex(int i) {
      return this.source.readerIndex(i);
   }

   public int writerIndex() {
      return this.source.writerIndex();
   }

   public ByteBuf writerIndex(int i) {
      return this.source.writerIndex(i);
   }

   public ByteBuf setIndex(int var1, int var2) {
      return this.source.setIndex(var1, var2);
   }

   public int readableBytes() {
      return this.source.readableBytes();
   }

   public int writableBytes() {
      return this.source.writableBytes();
   }

   public int maxWritableBytes() {
      return this.source.maxWritableBytes();
   }

   public boolean isReadable() {
      return this.source.isReadable();
   }

   public boolean isReadable(int i) {
      return this.source.isReadable(i);
   }

   public boolean isWritable() {
      return this.source.isWritable();
   }

   public boolean isWritable(int i) {
      return this.source.isWritable(i);
   }

   public ByteBuf clear() {
      return this.source.clear();
   }

   public ByteBuf markReaderIndex() {
      return this.source.markReaderIndex();
   }

   public ByteBuf resetReaderIndex() {
      return this.source.resetReaderIndex();
   }

   public ByteBuf markWriterIndex() {
      return this.source.markWriterIndex();
   }

   public ByteBuf resetWriterIndex() {
      return this.source.resetWriterIndex();
   }

   public ByteBuf discardReadBytes() {
      return this.source.discardReadBytes();
   }

   public ByteBuf discardSomeReadBytes() {
      return this.source.discardSomeReadBytes();
   }

   public ByteBuf ensureWritable(int i) {
      return this.source.ensureWritable(i);
   }

   public int ensureWritable(int var1, boolean var2) {
      return this.source.ensureWritable(var1, var2);
   }

   public boolean getBoolean(int i) {
      return this.source.getBoolean(i);
   }

   public byte getByte(int te) {
      return this.source.getByte(te);
   }

   public short getUnsignedByte(int i) {
      return this.source.getUnsignedByte(i);
   }

   public short getShort(int i) {
      return this.source.getShort(i);
   }

   public short getShortLE(int i) {
      return this.source.getShortLE(i);
   }

   public int getUnsignedShort(int i) {
      return this.source.getUnsignedShort(i);
   }

   public int getUnsignedShortLE(int i) {
      return this.source.getUnsignedShortLE(i);
   }

   public int getMedium(int i) {
      return this.source.getMedium(i);
   }

   public int getMediumLE(int i) {
      return this.source.getMediumLE(i);
   }

   public int getUnsignedMedium(int i) {
      return this.source.getUnsignedMedium(i);
   }

   public int getUnsignedMediumLE(int i) {
      return this.source.getUnsignedMediumLE(i);
   }

   public int getInt(int i) {
      return this.source.getInt(i);
   }

   public int getIntLE(int i) {
      return this.source.getIntLE(i);
   }

   public long getUnsignedInt(int i) {
      return this.source.getUnsignedInt(i);
   }

   public long getUnsignedIntLE(int i) {
      return this.source.getUnsignedIntLE(i);
   }

   public long getLong(int i) {
      return this.source.getLong(i);
   }

   public long getLongLE(int i) {
      return this.source.getLongLE(i);
   }

   public char getChar(int i) {
      return this.source.getChar(i);
   }

   public float getFloat(int i) {
      return this.source.getFloat(i);
   }

   public double getDouble(int i) {
      return this.source.getDouble(i);
   }

   public ByteBuf getBytes(int var1, ByteBuf var2) {
      return this.source.getBytes(var1, var2);
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3) {
      return this.source.getBytes(var1, var2, var3);
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      return this.source.getBytes(var1, var2, var3, var4);
   }

   public ByteBuf getBytes(int var1, byte[] bytes) {
      return this.source.getBytes(var1, bytes);
   }

   public ByteBuf getBytes(int var1, byte[] bytes, int var3, int var4) {
      return this.source.getBytes(var1, bytes, var3, var4);
   }

   public ByteBuf getBytes(int var1, ByteBuffer byteBuffer) {
      return this.source.getBytes(var1, byteBuffer);
   }

   public ByteBuf getBytes(int var1, OutputStream outputStream, int var3) throws IOException {
      return this.source.getBytes(var1, outputStream, var3);
   }

   public int getBytes(int var1, GatheringByteChannel gatheringByteChannel, int var3) throws IOException {
      return this.source.getBytes(var1, gatheringByteChannel, var3);
   }

   public int getBytes(int var1, FileChannel fileChannel, long var3, int var5) throws IOException {
      return this.source.getBytes(var1, fileChannel, var3, var5);
   }

   public CharSequence getCharSequence(int var1, int var2, Charset charset) {
      return this.source.getCharSequence(var1, var2, charset);
   }

   public ByteBuf setBoolean(int var1, boolean var2) {
      return this.source.setBoolean(var1, var2);
   }

   public ByteBuf setByte(int var1, int var2) {
      return this.source.setByte(var1, var2);
   }

   public ByteBuf setShort(int var1, int var2) {
      return this.source.setShort(var1, var2);
   }

   public ByteBuf setShortLE(int var1, int var2) {
      return this.source.setShortLE(var1, var2);
   }

   public ByteBuf setMedium(int var1, int var2) {
      return this.source.setMedium(var1, var2);
   }

   public ByteBuf setMediumLE(int var1, int var2) {
      return this.source.setMediumLE(var1, var2);
   }

   public ByteBuf setInt(int var1, int var2) {
      return this.source.setInt(var1, var2);
   }

   public ByteBuf setIntLE(int var1, int var2) {
      return this.source.setIntLE(var1, var2);
   }

   public ByteBuf setLong(int var1, long var2) {
      return this.source.setLong(var1, var2);
   }

   public ByteBuf setLongLE(int var1, long var2) {
      return this.source.setLongLE(var1, var2);
   }

   public ByteBuf setChar(int var1, int var2) {
      return this.source.setChar(var1, var2);
   }

   public ByteBuf setFloat(int var1, float var2) {
      return this.source.setFloat(var1, var2);
   }

   public ByteBuf setDouble(int var1, double var2) {
      return this.source.setDouble(var1, var2);
   }

   public ByteBuf setBytes(int var1, ByteBuf var2) {
      return this.source.setBytes(var1, var2);
   }

   public ByteBuf setBytes(int var1, ByteBuf var2, int var3) {
      return this.source.setBytes(var1, var2, var3);
   }

   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      return this.source.setBytes(var1, var2, var3, var4);
   }

   public ByteBuf setBytes(int var1, byte[] bytes) {
      return this.source.setBytes(var1, bytes);
   }

   public ByteBuf setBytes(int var1, byte[] bytes, int var3, int var4) {
      return this.source.setBytes(var1, bytes, var3, var4);
   }

   public ByteBuf setBytes(int var1, ByteBuffer byteBuffer) {
      return this.source.setBytes(var1, byteBuffer);
   }

   public int setBytes(int var1, InputStream inputStream, int var3) throws IOException {
      return this.source.setBytes(var1, inputStream, var3);
   }

   public int setBytes(int var1, ScatteringByteChannel scatteringByteChannel, int var3) throws IOException {
      return this.source.setBytes(var1, scatteringByteChannel, var3);
   }

   public int setBytes(int var1, FileChannel fileChannel, long var3, int var5) throws IOException {
      return this.source.setBytes(var1, fileChannel, var3, var5);
   }

   public ByteBuf setZero(int var1, int var2) {
      return this.source.setZero(var1, var2);
   }

   public int setCharSequence(int var1, CharSequence charSequence, Charset charset) {
      return this.source.setCharSequence(var1, charSequence, charset);
   }

   public boolean readBoolean() {
      return this.source.readBoolean();
   }

   public byte readByte() {
      return this.source.readByte();
   }

   public short readUnsignedByte() {
      return this.source.readUnsignedByte();
   }

   public short readShort() {
      return this.source.readShort();
   }

   public short readShortLE() {
      return this.source.readShortLE();
   }

   public int readUnsignedShort() {
      return this.source.readUnsignedShort();
   }

   public int readUnsignedShortLE() {
      return this.source.readUnsignedShortLE();
   }

   public int readMedium() {
      return this.source.readMedium();
   }

   public int readMediumLE() {
      return this.source.readMediumLE();
   }

   public int readUnsignedMedium() {
      return this.source.readUnsignedMedium();
   }

   public int readUnsignedMediumLE() {
      return this.source.readUnsignedMediumLE();
   }

   public int readInt() {
      return this.source.readInt();
   }

   public int readIntLE() {
      return this.source.readIntLE();
   }

   public long readUnsignedInt() {
      return this.source.readUnsignedInt();
   }

   public long readUnsignedIntLE() {
      return this.source.readUnsignedIntLE();
   }

   public long readLong() {
      return this.source.readLong();
   }

   public long readLongLE() {
      return this.source.readLongLE();
   }

   public char readChar() {
      return this.source.readChar();
   }

   public float readFloat() {
      return this.source.readFloat();
   }

   public double readDouble() {
      return this.source.readDouble();
   }

   public ByteBuf readBytes(int i) {
      return this.source.readBytes(i);
   }

   public ByteBuf readSlice(int i) {
      return this.source.readSlice(i);
   }

   public ByteBuf readRetainedSlice(int i) {
      return this.source.readRetainedSlice(i);
   }

   public ByteBuf readBytes(ByteBuf byteBuf) {
      return this.source.readBytes(byteBuf);
   }

   public ByteBuf readBytes(ByteBuf var1, int var2) {
      return this.source.readBytes(var1, var2);
   }

   public ByteBuf readBytes(ByteBuf var1, int var2, int var3) {
      return this.source.readBytes(var1, var2, var3);
   }

   public ByteBuf readBytes(byte[] bytes) {
      return this.source.readBytes(bytes);
   }

   public ByteBuf readBytes(byte[] bytes, int var2, int var3) {
      return this.source.readBytes(bytes, var2, var3);
   }

   public ByteBuf readBytes(ByteBuffer byteBuffer) {
      return this.source.readBytes(byteBuffer);
   }

   public ByteBuf readBytes(OutputStream outputStream, int var2) throws IOException {
      return this.source.readBytes(outputStream, var2);
   }

   public int readBytes(GatheringByteChannel gatheringByteChannel, int var2) throws IOException {
      return this.source.readBytes(gatheringByteChannel, var2);
   }

   public CharSequence readCharSequence(int var1, Charset charset) {
      return this.source.readCharSequence(var1, charset);
   }

   public int readBytes(FileChannel fileChannel, long var2, int var4) throws IOException {
      return this.source.readBytes(fileChannel, var2, var4);
   }

   public ByteBuf skipBytes(int i) {
      return this.source.skipBytes(i);
   }

   public ByteBuf writeBoolean(boolean b) {
      return this.source.writeBoolean(b);
   }

   public ByteBuf writeByte(int i) {
      return this.source.writeByte(i);
   }

   public ByteBuf writeShort(int i) {
      return this.source.writeShort(i);
   }

   public ByteBuf writeShortLE(int i) {
      return this.source.writeShortLE(i);
   }

   public ByteBuf writeMedium(int i) {
      return this.source.writeMedium(i);
   }

   public ByteBuf writeMediumLE(int i) {
      return this.source.writeMediumLE(i);
   }

   public ByteBuf writeInt(int i) {
      return this.source.writeInt(i);
   }

   public ByteBuf writeIntLE(int i) {
      return this.source.writeIntLE(i);
   }

   public ByteBuf writeLong(long l) {
      return this.source.writeLong(l);
   }

   public ByteBuf writeLongLE(long l) {
      return this.source.writeLongLE(l);
   }

   public ByteBuf writeChar(int i) {
      return this.source.writeChar(i);
   }

   public ByteBuf writeFloat(float f) {
      return this.source.writeFloat(f);
   }

   public ByteBuf writeDouble(double d) {
      return this.source.writeDouble(d);
   }

   public ByteBuf writeBytes(ByteBuf byteBuf) {
      return this.source.writeBytes(byteBuf);
   }

   public ByteBuf writeBytes(ByteBuf var1, int var2) {
      return this.source.writeBytes(var1, var2);
   }

   public ByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
      return this.source.writeBytes(var1, var2, var3);
   }

   public ByteBuf writeBytes(byte[] bytes) {
      return this.source.writeBytes(bytes);
   }

   public ByteBuf writeBytes(byte[] bytes, int var2, int var3) {
      return this.source.writeBytes(bytes, var2, var3);
   }

   public ByteBuf writeBytes(ByteBuffer byteBuffer) {
      return this.source.writeBytes(byteBuffer);
   }

   public int writeBytes(InputStream inputStream, int var2) throws IOException {
      return this.source.writeBytes(inputStream, var2);
   }

   public int writeBytes(ScatteringByteChannel scatteringByteChannel, int var2) throws IOException {
      return this.source.writeBytes(scatteringByteChannel, var2);
   }

   public int writeBytes(FileChannel fileChannel, long var2, int var4) throws IOException {
      return this.source.writeBytes(fileChannel, var2, var4);
   }

   public ByteBuf writeZero(int i) {
      return this.source.writeZero(i);
   }

   public int writeCharSequence(CharSequence charSequence, Charset charset) {
      return this.source.writeCharSequence(charSequence, charset);
   }

   public int indexOf(int var1, int var2, byte var3) {
      return this.source.indexOf(var1, var2, var3);
   }

   public int bytesBefore(byte b) {
      return this.source.bytesBefore(b);
   }

   public int bytesBefore(int var1, byte var2) {
      return this.source.bytesBefore(var1, var2);
   }

   public int bytesBefore(int var1, int var2, byte var3) {
      return this.source.bytesBefore(var1, var2, var3);
   }

   public int forEachByte(ByteProcessor byteProcessor) {
      return this.source.forEachByte(byteProcessor);
   }

   public int forEachByte(int var1, int var2, ByteProcessor byteProcessor) {
      return this.source.forEachByte(var1, var2, byteProcessor);
   }

   public int forEachByteDesc(ByteProcessor byteProcessor) {
      return this.source.forEachByteDesc(byteProcessor);
   }

   public int forEachByteDesc(int var1, int var2, ByteProcessor byteProcessor) {
      return this.source.forEachByteDesc(var1, var2, byteProcessor);
   }

   public ByteBuf copy() {
      return this.source.copy();
   }

   public ByteBuf copy(int var1, int var2) {
      return this.source.copy(var1, var2);
   }

   public ByteBuf slice() {
      return this.source.slice();
   }

   public ByteBuf retainedSlice() {
      return this.source.retainedSlice();
   }

   public ByteBuf slice(int var1, int var2) {
      return this.source.slice(var1, var2);
   }

   public ByteBuf retainedSlice(int var1, int var2) {
      return this.source.retainedSlice(var1, var2);
   }

   public ByteBuf duplicate() {
      return this.source.duplicate();
   }

   public ByteBuf retainedDuplicate() {
      return this.source.retainedDuplicate();
   }

   public int nioBufferCount() {
      return this.source.nioBufferCount();
   }

   public ByteBuffer nioBuffer() {
      return this.source.nioBuffer();
   }

   public ByteBuffer nioBuffer(int var1, int var2) {
      return this.source.nioBuffer(var1, var2);
   }

   public ByteBuffer internalNioBuffer(int var1, int var2) {
      return this.source.internalNioBuffer(var1, var2);
   }

   public ByteBuffer[] nioBuffers() {
      return this.source.nioBuffers();
   }

   public ByteBuffer[] nioBuffers(int var1, int var2) {
      return this.source.nioBuffers(var1, var2);
   }

   public boolean hasArray() {
      return this.source.hasArray();
   }

   public byte[] array() {
      return this.source.array();
   }

   public int arrayOffset() {
      return this.source.arrayOffset();
   }

   public boolean hasMemoryAddress() {
      return this.source.hasMemoryAddress();
   }

   public long memoryAddress() {
      return this.source.memoryAddress();
   }

   public String toString(Charset charset) {
      return this.source.toString(charset);
   }

   public String toString(int var1, int var2, Charset charset) {
      return this.source.toString(var1, var2, charset);
   }

   public int hashCode() {
      return this.source.hashCode();
   }

   public boolean equals(Object object) {
      return this.source.equals(object);
   }

   public int compareTo(ByteBuf byteBuf) {
      return this.source.compareTo(byteBuf);
   }

   public String toString() {
      return this.source.toString();
   }

   public ByteBuf retain(int i) {
      return this.source.retain(i);
   }

   public ByteBuf retain() {
      return this.source.retain();
   }

   public ByteBuf touch() {
      return this.source.touch();
   }

   public ByteBuf touch(Object object) {
      return this.source.touch(object);
   }

   public int refCnt() {
      return this.source.refCnt();
   }

   public boolean release() {
      return this.source.release();
   }

   public boolean release(int i) {
      return this.source.release(i);
   }
}
