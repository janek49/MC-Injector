package com.mojang.realmsclient.client;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.realmsclient.dto.RegionPingResult;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ClientJarOnly
public class Ping {
   public static List ping(Ping.Region... ping$Regions) {
      for(Ping.Region var4 : ping$Regions) {
         ping(var4.endpoint);
      }

      List<RegionPingResult> list = new ArrayList();

      for(Ping.Region var5 : ping$Regions) {
         list.add(new RegionPingResult(var5.name, ping(var5.endpoint)));
      }

      Collections.sort(list, new Comparator() {
         public int compare(RegionPingResult var1, RegionPingResult var2) {
            return var1.ping() - var2.ping();
         }

         // $FF: synthetic method
         public int compare(Object var1, Object var2) {
            return this.compare((RegionPingResult)var1, (RegionPingResult)var2);
         }
      });
      return list;
   }

   private static int ping(String string) {
      int var1 = 700;
      long var2 = 0L;
      Socket var4 = null;

      for(int var5 = 0; var5 < 5; ++var5) {
         try {
            SocketAddress var6 = new InetSocketAddress(string, 80);
            var4 = new Socket();
            long var7 = now();
            var4.connect(var6, 700);
            var2 += now() - var7;
         } catch (Exception var12) {
            var2 += 700L;
         } finally {
            close(var4);
         }
      }

      return (int)((double)var2 / 5.0D);
   }

   private static void close(Socket socket) {
      try {
         if(socket != null) {
            socket.close();
         }
      } catch (Throwable var2) {
         ;
      }

   }

   private static long now() {
      return System.currentTimeMillis();
   }

   public static List pingAllRegions() {
      return ping(Ping.Region.values());
   }

   @ClientJarOnly
   static enum Region {
      US_EAST_1("us-east-1", "ec2.us-east-1.amazonaws.com"),
      US_WEST_2("us-west-2", "ec2.us-west-2.amazonaws.com"),
      US_WEST_1("us-west-1", "ec2.us-west-1.amazonaws.com"),
      EU_WEST_1("eu-west-1", "ec2.eu-west-1.amazonaws.com"),
      AP_SOUTHEAST_1("ap-southeast-1", "ec2.ap-southeast-1.amazonaws.com"),
      AP_SOUTHEAST_2("ap-southeast-2", "ec2.ap-southeast-2.amazonaws.com"),
      AP_NORTHEAST_1("ap-northeast-1", "ec2.ap-northeast-1.amazonaws.com"),
      SA_EAST_1("sa-east-1", "ec2.sa-east-1.amazonaws.com");

      private final String name;
      private final String endpoint;

      private Region(String name, String endpoint) {
         this.name = name;
         this.endpoint = endpoint;
      }
   }
}
