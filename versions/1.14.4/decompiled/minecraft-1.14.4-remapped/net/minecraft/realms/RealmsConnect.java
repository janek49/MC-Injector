package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsConnect {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen onlineScreen;
   private volatile boolean aborted;
   private Connection connection;

   public RealmsConnect(RealmsScreen onlineScreen) {
      this.onlineScreen = onlineScreen;
   }

   public void connect(final String string, final int var2) {
      Realms.setConnectedToRealms(true);
      Realms.narrateNow(Realms.getLocalizedString("mco.connect.success", new Object[0]));
      (new Thread("Realms-connect-task") {
         public void run() {
            InetAddress var1 = null;

            try {
               var1 = InetAddress.getByName(string);
               if(RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection = Connection.connectToServer(var1, var2, Minecraft.getInstance().options.useNativeTransport());
               if(RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.setListener(new ClientHandshakePacketListenerImpl(RealmsConnect.this.connection, Minecraft.getInstance(), RealmsConnect.this.onlineScreen.getProxy(), (component) -> {
               }));
               if(RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.send(new ClientIntentionPacket(string, var2, ConnectionProtocol.LOGIN));
               if(RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.send(new ServerboundHelloPacket(Minecraft.getInstance().getUser().getGameProfile()));
            } catch (UnknownHostException var5) {
               Realms.clearResourcePack();
               if(RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.LOGGER.error("Couldn\'t connect to world", var5);
               Realms.setScreen(new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new TranslatableComponent("disconnect.genericReason", new Object[]{"Unknown host \'" + string + "\'"})));
            } catch (Exception var6) {
               Realms.clearResourcePack();
               if(RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.LOGGER.error("Couldn\'t connect to world", var6);
               String var3 = var6.toString();
               if(var1 != null) {
                  String var4 = var1 + ":" + var2;
                  var3 = var3.replaceAll(var4, "");
               }

               Realms.setScreen(new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new TranslatableComponent("disconnect.genericReason", new Object[]{var3})));
            }

         }
      }).start();
   }

   public void abort() {
      this.aborted = true;
      if(this.connection != null && this.connection.isConnected()) {
         this.connection.disconnect(new TranslatableComponent("disconnect.genericReason", new Object[0]));
         this.connection.handleDisconnection();
      }

   }

   public void tick() {
      if(this.connection != null) {
         if(this.connection.isConnected()) {
            this.connection.tick();
         } else {
            this.connection.handleDisconnection();
         }
      }

   }
}
