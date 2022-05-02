package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class Subscription extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public long startDate;
   public int daysLeft;
   public Subscription.SubscriptionType type = Subscription.SubscriptionType.NORMAL;

   public static Subscription parse(String string) {
      Subscription subscription = new Subscription();

      try {
         JsonParser var2 = new JsonParser();
         JsonObject var3 = var2.parse(string).getAsJsonObject();
         subscription.startDate = JsonUtils.getLongOr("startDate", var3, 0L);
         subscription.daysLeft = JsonUtils.getIntOr("daysLeft", var3, 0);
         subscription.type = typeFrom(JsonUtils.getStringOr("subscriptionType", var3, Subscription.SubscriptionType.NORMAL.name()));
      } catch (Exception var4) {
         LOGGER.error("Could not parse Subscription: " + var4.getMessage());
      }

      return subscription;
   }

   private static Subscription.SubscriptionType typeFrom(String string) {
      try {
         return Subscription.SubscriptionType.valueOf(string);
      } catch (Exception var2) {
         return Subscription.SubscriptionType.NORMAL;
      }
   }

   @ClientJarOnly
   public static enum SubscriptionType {
      NORMAL,
      RECURRING;
   }
}
