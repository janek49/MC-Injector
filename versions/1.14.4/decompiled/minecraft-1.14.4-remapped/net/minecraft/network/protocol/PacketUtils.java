package net.minecraft.network.protocol;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketUtils {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void ensureRunningOnSameThread(Packet packet, PacketListener packetListener, ServerLevel serverLevel) throws RunningOnDifferentThreadException {
      ensureRunningOnSameThread(packet, packetListener, (BlockableEventLoop)serverLevel.getServer());
   }

   public static void ensureRunningOnSameThread(Packet packet, PacketListener packetListener, BlockableEventLoop blockableEventLoop) throws RunningOnDifferentThreadException {
      if(!blockableEventLoop.isSameThread()) {
         blockableEventLoop.execute(() -> {
            if(packetListener.getConnection().isConnected()) {
               packet.handle(packetListener);
            } else {
               LOGGER.debug("Ignoring packet due to disconnection: " + packet);
            }

         });
         throw RunningOnDifferentThreadException.RUNNING_ON_DIFFERENT_THREAD;
      }
   }
}
