package net.minecraft.server.rcon.thread;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.Util;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.rcon.NetworkDataOutputStream;
import net.minecraft.server.rcon.PktUtils;
import net.minecraft.server.rcon.thread.GenericThread;

public class QueryThreadGs4 extends GenericThread {
   private long lastChallengeCheck;
   private final int port;
   private final int serverPort;
   private final int maxPlayers;
   private final String serverName;
   private final String worldName;
   private DatagramSocket socket;
   private final byte[] buffer = new byte[1460];
   private DatagramPacket request;
   private final Map idents;
   private String hostIp;
   private String serverIp;
   private final Map validChallenges;
   private final long lastChallengeClean;
   private final NetworkDataOutputStream rulesResponse;
   private long lastRulesResponse;

   public QueryThreadGs4(ServerInterface serverInterface) {
      super(serverInterface, "Query Listener");
      this.port = serverInterface.getProperties().queryPort;
      this.serverIp = serverInterface.getServerIp();
      this.serverPort = serverInterface.getServerPort();
      this.serverName = serverInterface.getServerName();
      this.maxPlayers = serverInterface.getMaxPlayers();
      this.worldName = serverInterface.getLevelIdName();
      this.lastRulesResponse = 0L;
      this.hostIp = "0.0.0.0";
      if(!this.serverIp.isEmpty() && !this.hostIp.equals(this.serverIp)) {
         this.hostIp = this.serverIp;
      } else {
         this.serverIp = "0.0.0.0";

         try {
            InetAddress var2 = InetAddress.getLocalHost();
            this.hostIp = var2.getHostAddress();
         } catch (UnknownHostException var3) {
            this.warn("Unable to determine local host IP, please set server-ip in server.properties: " + var3.getMessage());
         }
      }

      this.idents = Maps.newHashMap();
      this.rulesResponse = new NetworkDataOutputStream(1460);
      this.validChallenges = Maps.newHashMap();
      this.lastChallengeClean = (new Date()).getTime();
   }

   private void sendTo(byte[] bytes, DatagramPacket datagramPacket) throws IOException {
      this.socket.send(new DatagramPacket(bytes, bytes.length, datagramPacket.getSocketAddress()));
   }

   private boolean processPacket(DatagramPacket datagramPacket) throws IOException {
      byte[] vars2 = datagramPacket.getData();
      int var3 = datagramPacket.getLength();
      SocketAddress var4 = datagramPacket.getSocketAddress();
      this.debug("Packet len " + var3 + " [" + var4 + "]");
      if(3 <= var3 && -2 == vars2[0] && -3 == vars2[1]) {
         this.debug("Packet \'" + PktUtils.toHexString(vars2[2]) + "\' [" + var4 + "]");
         switch(vars2[2]) {
         case 0:
            if(!this.validChallenge(datagramPacket).booleanValue()) {
               this.debug("Invalid challenge [" + var4 + "]");
               return false;
            } else if(15 == var3) {
               this.sendTo(this.buildRuleResponse(datagramPacket), datagramPacket);
               this.debug("Rules [" + var4 + "]");
            } else {
               NetworkDataOutputStream var5 = new NetworkDataOutputStream(1460);
               var5.write(0);
               var5.writeBytes(this.getIdentBytes(datagramPacket.getSocketAddress()));
               var5.writeString(this.serverName);
               var5.writeString("SMP");
               var5.writeString(this.worldName);
               var5.writeString(Integer.toString(this.currentPlayerCount()));
               var5.writeString(Integer.toString(this.maxPlayers));
               var5.writeShort((short)this.serverPort);
               var5.writeString(this.hostIp);
               this.sendTo(var5.toByteArray(), datagramPacket);
               this.debug("Status [" + var4 + "]");
            }
         default:
            return true;
         case 9:
            this.sendChallenge(datagramPacket);
            this.debug("Challenge [" + var4 + "]");
            return true;
         }
      } else {
         this.debug("Invalid packet [" + var4 + "]");
         return false;
      }
   }

   private byte[] buildRuleResponse(DatagramPacket datagramPacket) throws IOException {
      long var2 = Util.getMillis();
      if(var2 < this.lastRulesResponse + 5000L) {
         byte[] vars4 = this.rulesResponse.toByteArray();
         byte[] vars5 = this.getIdentBytes(datagramPacket.getSocketAddress());
         vars4[1] = vars5[0];
         vars4[2] = vars5[1];
         vars4[3] = vars5[2];
         vars4[4] = vars5[3];
         return vars4;
      } else {
         this.lastRulesResponse = var2;
         this.rulesResponse.reset();
         this.rulesResponse.write(0);
         this.rulesResponse.writeBytes(this.getIdentBytes(datagramPacket.getSocketAddress()));
         this.rulesResponse.writeString("splitnum");
         this.rulesResponse.write(128);
         this.rulesResponse.write(0);
         this.rulesResponse.writeString("hostname");
         this.rulesResponse.writeString(this.serverName);
         this.rulesResponse.writeString("gametype");
         this.rulesResponse.writeString("SMP");
         this.rulesResponse.writeString("game_id");
         this.rulesResponse.writeString("MINECRAFT");
         this.rulesResponse.writeString("version");
         this.rulesResponse.writeString(this.serverInterface.getServerVersion());
         this.rulesResponse.writeString("plugins");
         this.rulesResponse.writeString(this.serverInterface.getPluginNames());
         this.rulesResponse.writeString("map");
         this.rulesResponse.writeString(this.worldName);
         this.rulesResponse.writeString("numplayers");
         this.rulesResponse.writeString("" + this.currentPlayerCount());
         this.rulesResponse.writeString("maxplayers");
         this.rulesResponse.writeString("" + this.maxPlayers);
         this.rulesResponse.writeString("hostport");
         this.rulesResponse.writeString("" + this.serverPort);
         this.rulesResponse.writeString("hostip");
         this.rulesResponse.writeString(this.hostIp);
         this.rulesResponse.write(0);
         this.rulesResponse.write(1);
         this.rulesResponse.writeString("player_");
         this.rulesResponse.write(0);
         String[] vars4 = this.serverInterface.getPlayerNames();

         for(String var8 : vars4) {
            this.rulesResponse.writeString(var8);
         }

         this.rulesResponse.write(0);
         return this.rulesResponse.toByteArray();
      }
   }

   private byte[] getIdentBytes(SocketAddress socketAddress) {
      return ((QueryThreadGs4.RequestChallenge)this.validChallenges.get(socketAddress)).getIdentBytes();
   }

   private Boolean validChallenge(DatagramPacket datagramPacket) {
      SocketAddress var2 = datagramPacket.getSocketAddress();
      if(!this.validChallenges.containsKey(var2)) {
         return Boolean.valueOf(false);
      } else {
         byte[] vars3 = datagramPacket.getData();
         return ((QueryThreadGs4.RequestChallenge)this.validChallenges.get(var2)).getChallenge() != PktUtils.intFromNetworkByteArray(vars3, 7, datagramPacket.getLength())?Boolean.valueOf(false):Boolean.valueOf(true);
      }
   }

   private void sendChallenge(DatagramPacket datagramPacket) throws IOException {
      QueryThreadGs4.RequestChallenge var2 = new QueryThreadGs4.RequestChallenge(datagramPacket);
      this.validChallenges.put(datagramPacket.getSocketAddress(), var2);
      this.sendTo(var2.getChallengeBytes(), datagramPacket);
   }

   private void pruneChallenges() {
      if(this.running) {
         long var1 = Util.getMillis();
         if(var1 >= this.lastChallengeCheck + 30000L) {
            this.lastChallengeCheck = var1;
            Iterator<Entry<SocketAddress, QueryThreadGs4.RequestChallenge>> var3 = this.validChallenges.entrySet().iterator();

            while(var3.hasNext()) {
               Entry<SocketAddress, QueryThreadGs4.RequestChallenge> var4 = (Entry)var3.next();
               if(((QueryThreadGs4.RequestChallenge)var4.getValue()).before(var1).booleanValue()) {
                  var3.remove();
               }
            }

         }
      }
   }

   public void run() {
      this.info("Query running on " + this.serverIp + ":" + this.port);
      this.lastChallengeCheck = Util.getMillis();
      this.request = new DatagramPacket(this.buffer, this.buffer.length);

      try {
         while(this.running) {
            try {
               this.socket.receive(this.request);
               this.pruneChallenges();
               this.processPacket(this.request);
            } catch (SocketTimeoutException var7) {
               this.pruneChallenges();
            } catch (PortUnreachableException var8) {
               ;
            } catch (IOException var9) {
               this.recoverSocketError(var9);
            }
         }
      } finally {
         this.closeSockets();
      }

   }

   public void start() {
      if(!this.running) {
         if(0 < this.port && '\uffff' >= this.port) {
            if(this.initSocket()) {
               super.start();
            }

         } else {
            this.warn("Invalid query port " + this.port + " found in server.properties (queries disabled)");
         }
      }
   }

   private void recoverSocketError(Exception exception) {
      if(this.running) {
         this.warn("Unexpected exception, buggy JRE? (" + exception + ")");
         if(!this.initSocket()) {
            this.error("Failed to recover from buggy JRE, shutting down!");
            this.running = false;
         }

      }
   }

   private boolean initSocket() {
      try {
         this.socket = new DatagramSocket(this.port, InetAddress.getByName(this.serverIp));
         this.registerSocket(this.socket);
         this.socket.setSoTimeout(500);
         return true;
      } catch (SocketException var2) {
         this.warn("Unable to initialise query system on " + this.serverIp + ":" + this.port + " (Socket): " + var2.getMessage());
      } catch (UnknownHostException var3) {
         this.warn("Unable to initialise query system on " + this.serverIp + ":" + this.port + " (Unknown Host): " + var3.getMessage());
      } catch (Exception var4) {
         this.warn("Unable to initialise query system on " + this.serverIp + ":" + this.port + " (E): " + var4.getMessage());
      }

      return false;
   }

   class RequestChallenge {
      private final long time = (new Date()).getTime();
      private final int challenge;
      private final byte[] identBytes;
      private final byte[] challengeBytes;
      private final String ident;

      public RequestChallenge(DatagramPacket datagramPacket) {
         byte[] vars3 = datagramPacket.getData();
         this.identBytes = new byte[4];
         this.identBytes[0] = vars3[3];
         this.identBytes[1] = vars3[4];
         this.identBytes[2] = vars3[5];
         this.identBytes[3] = vars3[6];
         this.ident = new String(this.identBytes, StandardCharsets.UTF_8);
         this.challenge = (new Random()).nextInt(16777216);
         this.challengeBytes = String.format("\t%s%d\u0000", new Object[]{this.ident, Integer.valueOf(this.challenge)}).getBytes(StandardCharsets.UTF_8);
      }

      public Boolean before(long l) {
         return Boolean.valueOf(this.time < l);
      }

      public int getChallenge() {
         return this.challenge;
      }

      public byte[] getChallengeBytes() {
         return this.challengeBytes;
      }

      public byte[] getIdentBytes() {
         return this.identBytes;
      }
   }
}
