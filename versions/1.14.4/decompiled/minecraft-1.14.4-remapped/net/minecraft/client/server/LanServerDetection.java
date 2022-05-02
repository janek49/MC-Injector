package net.minecraft.client.server;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.client.server.LanServer;
import net.minecraft.client.server.LanServerPinger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class LanServerDetection {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();

   @ClientJarOnly
   public static class LanServerDetector extends Thread {
      private final LanServerDetection.LanServerList serverList;
      private final InetAddress pingGroup;
      private final MulticastSocket socket;

      public LanServerDetector(LanServerDetection.LanServerList serverList) throws IOException {
         super("LanServerDetector #" + LanServerDetection.UNIQUE_THREAD_ID.incrementAndGet());
         this.serverList = serverList;
         this.setDaemon(true);
         this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LanServerDetection.LOGGER));
         this.socket = new MulticastSocket(4445);
         this.pingGroup = InetAddress.getByName("224.0.2.60");
         this.socket.setSoTimeout(5000);
         this.socket.joinGroup(this.pingGroup);
      }

      public void run() {
         byte[] vars2 = new byte[1024];

         while(!this.isInterrupted()) {
            DatagramPacket var1 = new DatagramPacket(vars2, vars2.length);

            try {
               this.socket.receive(var1);
            } catch (SocketTimeoutException var5) {
               continue;
            } catch (IOException var6) {
               LanServerDetection.LOGGER.error("Couldn\'t ping server", var6);
               break;
            }

            String var3 = new String(var1.getData(), var1.getOffset(), var1.getLength(), StandardCharsets.UTF_8);
            LanServerDetection.LOGGER.debug("{}: {}", var1.getAddress(), var3);
            this.serverList.addServer(var3, var1.getAddress());
         }

         try {
            this.socket.leaveGroup(this.pingGroup);
         } catch (IOException var4) {
            ;
         }

         this.socket.close();
      }
   }

   @ClientJarOnly
   public static class LanServerList {
      private final List servers = Lists.newArrayList();
      private boolean isDirty;

      public synchronized boolean isDirty() {
         return this.isDirty;
      }

      public synchronized void markClean() {
         this.isDirty = false;
      }

      public synchronized List getServers() {
         return Collections.unmodifiableList(this.servers);
      }

      public synchronized void addServer(String string, InetAddress inetAddress) {
         String string = LanServerPinger.parseMotd(string);
         String var4 = LanServerPinger.parseAddress(string);
         if(var4 != null) {
            var4 = inetAddress.getHostAddress() + ":" + var4;
            boolean var5 = false;

            for(LanServer var7 : this.servers) {
               if(var7.getAddress().equals(var4)) {
                  var7.updatePingTime();
                  var5 = true;
                  break;
               }
            }

            if(!var5) {
               this.servers.add(new LanServer(string, var4));
               this.isDirty = true;
            }

         }
      }
   }
}
