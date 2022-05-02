package net.minecraft.client.resources.language;

import com.fox2code.repacker.ClientJarOnly;

@ClientJarOnly
public class Language implements com.mojang.bridge.game.Language, Comparable {
   private final String code;
   private final String region;
   private final String name;
   private final boolean bidirectional;

   public Language(String code, String region, String name, boolean bidirectional) {
      this.code = code;
      this.region = region;
      this.name = name;
      this.bidirectional = bidirectional;
   }

   public String getCode() {
      return this.code;
   }

   public String getName() {
      return this.name;
   }

   public String getRegion() {
      return this.region;
   }

   public boolean isBidirectional() {
      return this.bidirectional;
   }

   public String toString() {
      return String.format("%s (%s)", new Object[]{this.name, this.region});
   }

   public boolean equals(Object object) {
      return this == object?true:(!(object instanceof Language)?false:this.code.equals(((Language)object).code));
   }

   public int hashCode() {
      return this.code.hashCode();
   }

   public int compareTo(Language language) {
      return this.code.compareTo(language.code);
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((Language)var1);
   }
}
