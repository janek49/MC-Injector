package net.minecraft.client.server;

import com.fox2code.repacker.ClientJarOnly;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.DefaultUncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class LanServerPinger extends Thread {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();
   private final String motd;
   private final DatagramSocket socket;
   private boolean isRunning = true;
   private final String serverAddress;

   public LanServerPinger(String motd, String serverAddress) throws IOException {
      super("LanServerPinger #" + UNIQUE_THREAD_ID.incrementAndGet());
      this.motd = motd;
      this.serverAddress = serverAddress;
      this.setDaemon(true);
      this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      this.socket = new DatagramSocket();
   }

   public void run() {
      String var1 = createPingString(this.motd, this.serverAddress);
      byte[] vars2 = var1.getBytes(StandardCharsets.UTF_8);

      while(!this.isInterrupted() && this.isRunning) {
         try {
            InetAddress var3 = InetAddress.getByName("224.0.2.60");
            DatagramPacket var4 = new DatagramPacket(vars2, vars2.length, var3, 4445);
            this.socket.send(var4);
         } catch (IOException var6) {
            LOGGER.warn("LanServerPinger: {}", var6.getMessage());
            break;
         }

         try {
            sleep(1500L);
         } catch (InterruptedException var5) {
            ;
         }
      }

   }

   public void interrupt() {
      super.interrupt();
      this.isRunning = false;
   }

   public static String createPingString(String var0, String var1) {
      return "[MOTD]" + var0 + "[/MOTD][AD]" + var1 + "[/AD]";
   }

   public static String parseMotd(String string) {
      int var1 = string.indexOf("[MOTD]");
      if(var1 < 0) {
         return "missing no";
      } else {
         int var2 = string.indexOf("[/MOTD]", var1 + "[MOTD]".length());
         return var2 < var1?"missing no":string.substring(var1 + "[MOTD]".length(), var2);
      }
   }

   public static String parseAddress(String string) {
      int var1 = string.indexOf("[/MOTD]");
      if(var1 < 0) {
         return null;
      } else {
         int var2 = string.indexOf("[/MOTD]", var1 + "[/MOTD]".length());
         if(var2 >= 0) {
            return null;
         } else {
            int var3 = string.indexOf("[AD]", var1 + "[/MOTD]".length());
            if(var3 < 0) {
               return null;
            } else {
               int var4 = string.indexOf("[/AD]", var3 + "[AD]".length());
               return var4 < var3?null:string.substring(var3 + "[AD]".length(), var4);
            }
         }
      }
   }
}
