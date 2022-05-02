package net.minecraft.server.rcon.thread;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.DefaultUncaughtExceptionHandlerWithName;
import net.minecraft.server.ServerInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class GenericThread implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   protected boolean running;
   protected final ServerInterface serverInterface;
   protected final String name;
   protected Thread thread;
   protected final int maxStopWait = 5;
   protected final List datagramSockets = Lists.newArrayList();
   protected final List serverSockets = Lists.newArrayList();

   protected GenericThread(ServerInterface serverInterface, String name) {
      this.serverInterface = serverInterface;
      this.name = name;
      if(this.serverInterface.isDebugging()) {
         this.warn("Debugging is enabled, performance maybe reduced!");
      }

   }

   public synchronized void start() {
      this.thread = new Thread(this, this.name + " #" + UNIQUE_THREAD_ID.incrementAndGet());
      this.thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandlerWithName(LOGGER));
      this.thread.start();
      this.running = true;
   }

   public synchronized void stop() {
      this.running = false;
      if(null != this.thread) {
         int var1 = 0;

         while(this.thread.isAlive()) {
            try {
               this.thread.join(1000L);
               ++var1;
               if(5 <= var1) {
                  this.warn("Waited " + var1 + " seconds attempting force stop!");
                  this.closeSockets(true);
               } else if(this.thread.isAlive()) {
                  this.warn("Thread " + this + " (" + this.thread.getState() + ") failed to exit after " + var1 + " second(s)");
                  this.warn("Stack:");

                  for(StackTraceElement var5 : this.thread.getStackTrace()) {
                     this.warn(var5.toString());
                  }

                  this.thread.interrupt();
               }
            } catch (InterruptedException var6) {
               ;
            }
         }

         this.closeSockets(true);
         this.thread = null;
      }
   }

   public boolean isRunning() {
      return this.running;
   }

   protected void debug(String string) {
      this.serverInterface.debug(string);
   }

   protected void info(String string) {
      this.serverInterface.info(string);
   }

   protected void warn(String string) {
      this.serverInterface.warn(string);
   }

   protected void error(String string) {
      this.serverInterface.error(string);
   }

   protected int currentPlayerCount() {
      return this.serverInterface.getPlayerCount();
   }

   protected void registerSocket(DatagramSocket datagramSocket) {
      this.debug("registerSocket: " + datagramSocket);
      this.datagramSockets.add(datagramSocket);
   }

   protected boolean closeSocket(DatagramSocket datagramSocket, boolean var2) {
      this.debug("closeSocket: " + datagramSocket);
      if(null == datagramSocket) {
         return false;
      } else {
         boolean var3 = false;
         if(!datagramSocket.isClosed()) {
            datagramSocket.close();
            var3 = true;
         }

         if(var2) {
            this.datagramSockets.remove(datagramSocket);
         }

         return var3;
      }
   }

   protected boolean closeSocket(ServerSocket serverSocket) {
      return this.closeSocket(serverSocket, true);
   }

   protected boolean closeSocket(ServerSocket serverSocket, boolean var2) {
      this.debug("closeSocket: " + serverSocket);
      if(null == serverSocket) {
         return false;
      } else {
         boolean var3 = false;

         try {
            if(!serverSocket.isClosed()) {
               serverSocket.close();
               var3 = true;
            }
         } catch (IOException var5) {
            this.warn("IO: " + var5.getMessage());
         }

         if(var2) {
            this.serverSockets.remove(serverSocket);
         }

         return var3;
      }
   }

   protected void closeSockets() {
      this.closeSockets(false);
   }

   protected void closeSockets(boolean b) {
      int var2 = 0;

      for(DatagramSocket var4 : this.datagramSockets) {
         if(this.closeSocket(var4, false)) {
            ++var2;
         }
      }

      this.datagramSockets.clear();

      for(ServerSocket var4 : this.serverSockets) {
         if(this.closeSocket(var4, false)) {
            ++var2;
         }
      }

      this.serverSockets.clear();
      if(b && 0 < var2) {
         this.warn("Force closed " + var2 + " sockets");
      }

   }
}
