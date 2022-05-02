package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.Locale;

@ClientJarOnly
public class RegionPingResult extends ValueObject {
   private final String regionName;
   private final int ping;

   public RegionPingResult(String regionName, int ping) {
      this.regionName = regionName;
      this.ping = ping;
   }

   public int ping() {
      return this.ping;
   }

   public String toString() {
      return String.format(Locale.ROOT, "%s --> %.2f ms", new Object[]{this.regionName, Float.valueOf((float)this.ping)});
   }
}
