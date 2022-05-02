package net.minecraft.server.rcon.thread;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.rcon.PktUtils;
import net.minecraft.server.rcon.thread.GenericThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RconClient extends GenericThread {
   private static final Logger LOGGER = LogManager.getLogger();
   private boolean authed;
   private Socket client;
   private final byte[] buf = new byte[1460];
   private final String rconPassword;

   RconClient(ServerInterface serverInterface, String rconPassword, Socket client) {
      super(serverInterface, "RCON Client");
      this.client = client;

      try {
         this.client.setSoTimeout(0);
      } catch (Exception var5) {
         this.running = false;
      }

      this.rconPassword = rconPassword;
      this.info("Rcon connection from: " + client.getInetAddress());
   }

   public void run() {
      while(true) {
         try {
            if(!this.running) {
               return;
            }

            BufferedInputStream var1 = new BufferedInputStream(this.client.getInputStream());
            int var2 = var1.read(this.buf, 0, 1460);
            if(10 <= var2) {
               int var3 = 0;
               int var4 = PktUtils.intFromByteArray(this.buf, 0, var2);
               if(var4 != var2 - 4) {
                  return;
               }

               var3 = var3 + 4;
               int var5 = PktUtils.intFromByteArray(this.buf, var3, var2);
               var3 = var3 + 4;
               int var6 = PktUtils.intFromByteArray(this.buf, var3);
               var3 = var3 + 4;
               switch(var6) {
               case 2:
                  if(this.authed) {
                     String var8 = PktUtils.stringFromByteArray(this.buf, var3, var2);

                     try {
                        this.sendCmdResponse(var5, this.serverInterface.runCommand(var8));
                     } catch (Exception var16) {
                        this.sendCmdResponse(var5, "Error executing: " + var8 + " (" + var16.getMessage() + ")");
                     }
                     continue;
                  }

                  this.sendAuthFailure();
                  continue;
               case 3:
                  String var7 = PktUtils.stringFromByteArray(this.buf, var3, var2);
                  int var10000 = var3 + var7.length();
                  if(!var7.isEmpty() && var7.equals(this.rconPassword)) {
                     this.authed = true;
                     this.send(var5, 2, "");
                     continue;
                  }

                  this.authed = false;
                  this.sendAuthFailure();
                  continue;
               default:
                  this.sendCmdResponse(var5, String.format("Unknown request %s", new Object[]{Integer.toHexString(var6)}));
                  continue;
               }
            }
         } catch (SocketTimeoutException var17) {
            return;
         } catch (IOException var18) {
            return;
         } catch (Exception var19) {
            LOGGER.error("Exception whilst parsing RCON input", var19);
            return;
         } finally {
            this.closeSocket();
         }

         return;
      }
   }

   private void send(int var1, int var2, String string) throws IOException {
      ByteArrayOutputStream var4 = new ByteArrayOutputStream(1248);
      DataOutputStream var5 = new DataOutputStream(var4);
      byte[] vars6 = string.getBytes("UTF-8");
      var5.writeInt(Integer.reverseBytes(vars6.length + 10));
      var5.writeInt(Integer.reverseBytes(var1));
      var5.writeInt(Integer.reverseBytes(var2));
      var5.write(vars6);
      var5.write(0);
      var5.write(0);
      this.client.getOutputStream().write(var4.toByteArray());
   }

   private void sendAuthFailure() throws IOException {
      this.send(-1, 2, "");
   }

   private void sendCmdResponse(int var1, String string) throws IOException {
      int var3 = string.length();

      while(true) {
         int var4 = 4096 <= var3?4096:var3;
         this.send(var1, 0, string.substring(0, var4));
         string = string.substring(var4);
         var3 = string.length();
         if(0 == var3) {
            break;
         }
      }

   }

   public void stop() {
      super.stop();
      this.closeSocket();
   }

   private void closeSocket() {
      if(null != this.client) {
         try {
            this.client.close();
         } catch (IOException var2) {
            this.warn("IO: " + var2.getMessage());
         }

         this.client = null;
      }
   }
}
