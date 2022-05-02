package net.minecraft.server.rcon;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NetworkDataOutputStream {
   private final ByteArrayOutputStream outputStream;
   private final DataOutputStream dataOutputStream;

   public NetworkDataOutputStream(int i) {
      this.outputStream = new ByteArrayOutputStream(i);
      this.dataOutputStream = new DataOutputStream(this.outputStream);
   }

   public void writeBytes(byte[] bytes) throws IOException {
      this.dataOutputStream.write(bytes, 0, bytes.length);
   }

   public void writeString(String string) throws IOException {
      this.dataOutputStream.writeBytes(string);
      this.dataOutputStream.write(0);
   }

   public void write(int i) throws IOException {
      this.dataOutputStream.write(i);
   }

   public void writeShort(short s) throws IOException {
      this.dataOutputStream.writeShort(Short.reverseBytes(s));
   }

   public byte[] toByteArray() {
      return this.outputStream.toByteArray();
   }

   public void reset() {
      this.outputStream.reset();
   }
}
