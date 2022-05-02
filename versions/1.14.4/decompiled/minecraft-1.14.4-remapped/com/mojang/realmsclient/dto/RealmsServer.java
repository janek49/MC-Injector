package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.ComparisonChain;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServerPing;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.realms.Realms;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsServer extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public long id;
   public String remoteSubscriptionId;
   public String name;
   public String motd;
   public RealmsServer.State state;
   public String owner;
   public String ownerUUID;
   public List players;
   public Map slots;
   public boolean expired;
   public boolean expiredTrial;
   public int daysLeft;
   public RealmsServer.WorldType worldType;
   public int activeSlot;
   public String minigameName;
   public int minigameId;
   public String minigameImage;
   public RealmsServerPing serverPing = new RealmsServerPing();

   public String getDescription() {
      return this.motd;
   }

   public String getName() {
      return this.name;
   }

   public String getMinigameName() {
      return this.minigameName;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setDescription(String description) {
      this.motd = description;
   }

   public void updateServerPing(RealmsServerPlayerList realmsServerPlayerList) {
      StringBuilder var2 = new StringBuilder();
      int var3 = 0;

      for(String var5 : realmsServerPlayerList.players) {
         if(!var5.equals(Realms.getUUID())) {
            String var6 = "";

            try {
               var6 = RealmsUtil.uuidToName(var5);
            } catch (Exception var8) {
               LOGGER.error("Could not get name for " + var5, var8);
               continue;
            }

            if(var2.length() > 0) {
               var2.append("\n");
            }

            var2.append(var6);
            ++var3;
         }
      }

      this.serverPing.nrOfPlayers = String.valueOf(var3);
      this.serverPing.playerList = var2.toString();
   }

   public static RealmsServer parse(JsonObject jsonObject) {
      RealmsServer realmsServer = new RealmsServer();

      try {
         realmsServer.id = JsonUtils.getLongOr("id", jsonObject, -1L);
         realmsServer.remoteSubscriptionId = JsonUtils.getStringOr("remoteSubscriptionId", jsonObject, (String)null);
         realmsServer.name = JsonUtils.getStringOr("name", jsonObject, (String)null);
         realmsServer.motd = JsonUtils.getStringOr("motd", jsonObject, (String)null);
         realmsServer.state = getState(JsonUtils.getStringOr("state", jsonObject, RealmsServer.State.CLOSED.name()));
         realmsServer.owner = JsonUtils.getStringOr("owner", jsonObject, (String)null);
         if(jsonObject.get("players") != null && jsonObject.get("players").isJsonArray()) {
            realmsServer.players = parseInvited(jsonObject.get("players").getAsJsonArray());
            sortInvited(realmsServer);
         } else {
            realmsServer.players = new ArrayList();
         }

         realmsServer.daysLeft = JsonUtils.getIntOr("daysLeft", jsonObject, 0);
         realmsServer.expired = JsonUtils.getBooleanOr("expired", jsonObject, false);
         realmsServer.expiredTrial = JsonUtils.getBooleanOr("expiredTrial", jsonObject, false);
         realmsServer.worldType = getWorldType(JsonUtils.getStringOr("worldType", jsonObject, RealmsServer.WorldType.NORMAL.name()));
         realmsServer.ownerUUID = JsonUtils.getStringOr("ownerUUID", jsonObject, "");
         if(jsonObject.get("slots") != null && jsonObject.get("slots").isJsonArray()) {
            realmsServer.slots = parseSlots(jsonObject.get("slots").getAsJsonArray());
         } else {
            realmsServer.slots = getEmptySlots();
         }

         realmsServer.minigameName = JsonUtils.getStringOr("minigameName", jsonObject, (String)null);
         realmsServer.activeSlot = JsonUtils.getIntOr("activeSlot", jsonObject, -1);
         realmsServer.minigameId = JsonUtils.getIntOr("minigameId", jsonObject, -1);
         realmsServer.minigameImage = JsonUtils.getStringOr("minigameImage", jsonObject, (String)null);
      } catch (Exception var3) {
         LOGGER.error("Could not parse McoServer: " + var3.getMessage());
      }

      return realmsServer;
   }

   private static void sortInvited(RealmsServer realmsServer) {
      Collections.sort(realmsServer.players, new Comparator() {
         public int compare(PlayerInfo var1, PlayerInfo var2) {
            return ComparisonChain.start().compare(Boolean.valueOf(var2.getAccepted()), Boolean.valueOf(var1.getAccepted())).compare(var1.getName().toLowerCase(Locale.ROOT), var2.getName().toLowerCase(Locale.ROOT)).result();
         }

         // $FF: synthetic method
         public int compare(Object var1, Object var2) {
            return this.compare((PlayerInfo)var1, (PlayerInfo)var2);
         }
      });
   }

   private static List parseInvited(JsonArray jsonArray) {
      ArrayList<PlayerInfo> var1 = new ArrayList();

      for(JsonElement var3 : jsonArray) {
         try {
            JsonObject var4 = var3.getAsJsonObject();
            PlayerInfo var5 = new PlayerInfo();
            var5.setName(JsonUtils.getStringOr("name", var4, (String)null));
            var5.setUuid(JsonUtils.getStringOr("uuid", var4, (String)null));
            var5.setOperator(JsonUtils.getBooleanOr("operator", var4, false));
            var5.setAccepted(JsonUtils.getBooleanOr("accepted", var4, false));
            var5.setOnline(JsonUtils.getBooleanOr("online", var4, false));
            var1.add(var5);
         } catch (Exception var6) {
            ;
         }
      }

      return var1;
   }

   private static Map parseSlots(JsonArray jsonArray) {
      Map<Integer, RealmsWorldOptions> map = new HashMap();

      for(JsonElement var3 : jsonArray) {
         try {
            JsonObject var5 = var3.getAsJsonObject();
            JsonParser var6 = new JsonParser();
            JsonElement var7 = var6.parse(var5.get("options").getAsString());
            RealmsWorldOptions var4;
            if(var7 == null) {
               var4 = RealmsWorldOptions.getDefaults();
            } else {
               var4 = RealmsWorldOptions.parse(var7.getAsJsonObject());
            }

            int var8 = JsonUtils.getIntOr("slotId", var5, -1);
            map.put(Integer.valueOf(var8), var4);
         } catch (Exception var9) {
            ;
         }
      }

      for(int var2 = 1; var2 <= 3; ++var2) {
         if(!map.containsKey(Integer.valueOf(var2))) {
            map.put(Integer.valueOf(var2), RealmsWorldOptions.getEmptyDefaults());
         }
      }

      return map;
   }

   private static Map getEmptySlots() {
      HashMap<Integer, RealmsWorldOptions> var0 = new HashMap();
      var0.put(Integer.valueOf(1), RealmsWorldOptions.getEmptyDefaults());
      var0.put(Integer.valueOf(2), RealmsWorldOptions.getEmptyDefaults());
      var0.put(Integer.valueOf(3), RealmsWorldOptions.getEmptyDefaults());
      return var0;
   }

   public static RealmsServer parse(String string) {
      RealmsServer realmsServer = new RealmsServer();

      try {
         JsonParser var2 = new JsonParser();
         JsonObject var3 = var2.parse(string).getAsJsonObject();
         realmsServer = parse(var3);
      } catch (Exception var4) {
         LOGGER.error("Could not parse McoServer: " + var4.getMessage());
      }

      return realmsServer;
   }

   private static RealmsServer.State getState(String string) {
      try {
         return RealmsServer.State.valueOf(string);
      } catch (Exception var2) {
         return RealmsServer.State.CLOSED;
      }
   }

   private static RealmsServer.WorldType getWorldType(String string) {
      try {
         return RealmsServer.WorldType.valueOf(string);
      } catch (Exception var2) {
         return RealmsServer.WorldType.NORMAL;
      }
   }

   public int hashCode() {
      return (new HashCodeBuilder(17, 37)).append(this.id).append(this.name).append(this.motd).append(this.state).append(this.owner).append(this.expired).toHashCode();
   }

   public boolean equals(Object object) {
      if(object == null) {
         return false;
      } else if(object == this) {
         return true;
      } else if(object.getClass() != this.getClass()) {
         return false;
      } else {
         RealmsServer var2 = (RealmsServer)object;
         return (new EqualsBuilder()).append(this.id, var2.id).append(this.name, var2.name).append(this.motd, var2.motd).append(this.state, var2.state).append(this.owner, var2.owner).append(this.expired, var2.expired).append(this.worldType, this.worldType).isEquals();
      }
   }

   public RealmsServer clone() {
      RealmsServer realmsServer = new RealmsServer();
      realmsServer.id = this.id;
      realmsServer.remoteSubscriptionId = this.remoteSubscriptionId;
      realmsServer.name = this.name;
      realmsServer.motd = this.motd;
      realmsServer.state = this.state;
      realmsServer.owner = this.owner;
      realmsServer.players = this.players;
      realmsServer.slots = this.cloneSlots(this.slots);
      realmsServer.expired = this.expired;
      realmsServer.expiredTrial = this.expiredTrial;
      realmsServer.daysLeft = this.daysLeft;
      realmsServer.serverPing = new RealmsServerPing();
      realmsServer.serverPing.nrOfPlayers = this.serverPing.nrOfPlayers;
      realmsServer.serverPing.playerList = this.serverPing.playerList;
      realmsServer.worldType = this.worldType;
      realmsServer.ownerUUID = this.ownerUUID;
      realmsServer.minigameName = this.minigameName;
      realmsServer.activeSlot = this.activeSlot;
      realmsServer.minigameId = this.minigameId;
      realmsServer.minigameImage = this.minigameImage;
      return realmsServer;
   }

   public Map cloneSlots(Map map) {
      Map<Integer, RealmsWorldOptions> var2 = new HashMap();

      for(Entry<Integer, RealmsWorldOptions> var4 : map.entrySet()) {
         var2.put(var4.getKey(), ((RealmsWorldOptions)var4.getValue()).clone());
      }

      return var2;
   }

   @ClientJarOnly
   public static class McoServerComparator implements Comparator {
      private final String refOwner;

      public McoServerComparator(String refOwner) {
         this.refOwner = refOwner;
      }

      public int compare(RealmsServer var1, RealmsServer var2) {
         return ComparisonChain.start().compareTrueFirst(var1.state.equals(RealmsServer.State.UNINITIALIZED), var2.state.equals(RealmsServer.State.UNINITIALIZED)).compareTrueFirst(var1.expiredTrial, var2.expiredTrial).compareTrueFirst(var1.owner.equals(this.refOwner), var2.owner.equals(this.refOwner)).compareFalseFirst(var1.expired, var2.expired).compareTrueFirst(var1.state.equals(RealmsServer.State.OPEN), var2.state.equals(RealmsServer.State.OPEN)).compare(var1.id, var2.id).result();
      }

      // $FF: synthetic method
      public int compare(Object var1, Object var2) {
         return this.compare((RealmsServer)var1, (RealmsServer)var2);
      }
   }

   @ClientJarOnly
   public static enum State {
      CLOSED,
      OPEN,
      UNINITIALIZED;
   }

   @ClientJarOnly
   public static enum WorldType {
      NORMAL,
      MINIGAME,
      ADVENTUREMAP,
      EXPERIENCE,
      INSPIRATION;
   }
}
