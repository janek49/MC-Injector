package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

@ClientJarOnly
public class User {
   private final String name;
   private final String uuid;
   private final String accessToken;
   private final User.Type type;

   public User(String name, String uuid, String accessToken, String var4) {
      this.name = name;
      this.uuid = uuid;
      this.accessToken = accessToken;
      this.type = User.Type.byName(var4);
   }

   public String getSessionId() {
      return "token:" + this.accessToken + ":" + this.uuid;
   }

   public String getUuid() {
      return this.uuid;
   }

   public String getName() {
      return this.name;
   }

   public String getAccessToken() {
      return this.accessToken;
   }

   public GameProfile getGameProfile() {
      try {
         UUID var1 = UUIDTypeAdapter.fromString(this.getUuid());
         return new GameProfile(var1, this.getName());
      } catch (IllegalArgumentException var2) {
         return new GameProfile((UUID)null, this.getName());
      }
   }

   @ClientJarOnly
   public static enum Type {
      LEGACY("legacy"),
      MOJANG("mojang");

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap((user$Type) -> {
         return user$Type.name;
      }, Function.identity()));
      private final String name;

      private Type(String name) {
         this.name = name;
      }

      @Nullable
      public static User.Type byName(String name) {
         return (User.Type)BY_NAME.get(name.toLowerCase(Locale.ROOT));
      }
   }
}
