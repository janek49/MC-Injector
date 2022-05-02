package net.minecraft.server.players;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.io.IOUtils;

public class GameProfileCache {
   public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   private static boolean usesAuthentication;
   private final Map profilesByName = Maps.newHashMap();
   private final Map profilesByUUID = Maps.newHashMap();
   private final Deque profileMRUList = Lists.newLinkedList();
   private final GameProfileRepository profileRepository;
   protected final Gson gson;
   private final File file;
   private static final ParameterizedType GAMEPROFILE_ENTRY_TYPE = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{GameProfileCache.GameProfileInfo.class};
      }

      public Type getRawType() {
         return List.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };

   public GameProfileCache(GameProfileRepository profileRepository, File file) {
      this.profileRepository = profileRepository;
      this.file = file;
      GsonBuilder var3 = new GsonBuilder();
      var3.registerTypeHierarchyAdapter(GameProfileCache.GameProfileInfo.class, new GameProfileCache.Serializer());
      this.gson = var3.create();
      this.load();
   }

   private static GameProfile lookupGameProfile(GameProfileRepository gameProfileRepository, String string) {
      final GameProfile[] vars2 = new GameProfile[1];
      ProfileLookupCallback var3 = new ProfileLookupCallback() {
         public void onProfileLookupSucceeded(GameProfile gameProfile) {
            vars2[0] = gameProfile;
         }

         public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
            vars2[0] = null;
         }
      };
      gameProfileRepository.findProfilesByNames(new String[]{string}, Agent.MINECRAFT, var3);
      if(!usesAuthentication() && vars2[0] == null) {
         UUID var4 = Player.createPlayerUUID(new GameProfile((UUID)null, string));
         GameProfile var5 = new GameProfile(var4, string);
         var3.onProfileLookupSucceeded(var5);
      }

      return vars2[0];
   }

   public static void setUsesAuthentication(boolean usesAuthentication) {
      usesAuthentication = usesAuthentication;
   }

   private static boolean usesAuthentication() {
      return usesAuthentication;
   }

   public void add(GameProfile gameProfile) {
      this.add(gameProfile, (Date)null);
   }

   private void add(GameProfile gameProfile, Date date) {
      UUID var3 = gameProfile.getId();
      if(date == null) {
         Calendar var4 = Calendar.getInstance();
         var4.setTime(new Date());
         var4.add(2, 1);
         date = var4.getTime();
      }

      GameProfileCache.GameProfileInfo var4 = new GameProfileCache.GameProfileInfo(gameProfile, date);
      if(this.profilesByUUID.containsKey(var3)) {
         GameProfileCache.GameProfileInfo var5 = (GameProfileCache.GameProfileInfo)this.profilesByUUID.get(var3);
         this.profilesByName.remove(var5.getProfile().getName().toLowerCase(Locale.ROOT));
         this.profileMRUList.remove(gameProfile);
      }

      this.profilesByName.put(gameProfile.getName().toLowerCase(Locale.ROOT), var4);
      this.profilesByUUID.put(var3, var4);
      this.profileMRUList.addFirst(gameProfile);
      this.save();
   }

   @Nullable
   public GameProfile get(String string) {
      String string = string.toLowerCase(Locale.ROOT);
      GameProfileCache.GameProfileInfo var3 = (GameProfileCache.GameProfileInfo)this.profilesByName.get(string);
      if(var3 != null && (new Date()).getTime() >= var3.expirationDate.getTime()) {
         this.profilesByUUID.remove(var3.getProfile().getId());
         this.profilesByName.remove(var3.getProfile().getName().toLowerCase(Locale.ROOT));
         this.profileMRUList.remove(var3.getProfile());
         var3 = null;
      }

      if(var3 != null) {
         GameProfile var4 = var3.getProfile();
         this.profileMRUList.remove(var4);
         this.profileMRUList.addFirst(var4);
      } else {
         GameProfile var4 = lookupGameProfile(this.profileRepository, string);
         if(var4 != null) {
            this.add(var4);
            var3 = (GameProfileCache.GameProfileInfo)this.profilesByName.get(string);
         }
      }

      this.save();
      return var3 == null?null:var3.getProfile();
   }

   @Nullable
   public GameProfile get(UUID uUID) {
      GameProfileCache.GameProfileInfo var2 = (GameProfileCache.GameProfileInfo)this.profilesByUUID.get(uUID);
      return var2 == null?null:var2.getProfile();
   }

   private GameProfileCache.GameProfileInfo getProfileInfo(UUID uUID) {
      GameProfileCache.GameProfileInfo gameProfileCache$GameProfileInfo = (GameProfileCache.GameProfileInfo)this.profilesByUUID.get(uUID);
      if(gameProfileCache$GameProfileInfo != null) {
         GameProfile var3 = gameProfileCache$GameProfileInfo.getProfile();
         this.profileMRUList.remove(var3);
         this.profileMRUList.addFirst(var3);
      }

      return gameProfileCache$GameProfileInfo;
   }

   public void load() {
      BufferedReader var1 = null;

      try {
         var1 = Files.newReader(this.file, StandardCharsets.UTF_8);
         List<GameProfileCache.GameProfileInfo> var2 = (List)GsonHelper.fromJson(this.gson, (Reader)var1, (Type)GAMEPROFILE_ENTRY_TYPE);
         this.profilesByName.clear();
         this.profilesByUUID.clear();
         this.profileMRUList.clear();
         if(var2 != null) {
            for(GameProfileCache.GameProfileInfo var4 : Lists.reverse(var2)) {
               if(var4 != null) {
                  this.add(var4.getProfile(), var4.getExpirationDate());
               }
            }
         }
      } catch (FileNotFoundException var9) {
         ;
      } catch (JsonParseException var10) {
         ;
      } finally {
         IOUtils.closeQuietly(var1);
      }

   }

   public void save() {
      String var1 = this.gson.toJson(this.getTopMRUProfiles(1000));
      BufferedWriter var2 = null;

      try {
         var2 = Files.newWriter(this.file, StandardCharsets.UTF_8);
         var2.write(var1);
         return;
      } catch (FileNotFoundException var8) {
         ;
      } catch (IOException var9) {
         return;
      } finally {
         IOUtils.closeQuietly(var2);
      }

   }

   private List getTopMRUProfiles(int i) {
      List<GameProfileCache.GameProfileInfo> list = Lists.newArrayList();

      for(GameProfile var5 : Lists.newArrayList(Iterators.limit(this.profileMRUList.iterator(), i))) {
         GameProfileCache.GameProfileInfo var6 = this.getProfileInfo(var5.getId());
         if(var6 != null) {
            list.add(var6);
         }
      }

      return list;
   }

   class GameProfileInfo {
      private final GameProfile profile;
      private final Date expirationDate;

      private GameProfileInfo(GameProfile profile, Date expirationDate) {
         this.profile = profile;
         this.expirationDate = expirationDate;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public Date getExpirationDate() {
         return this.expirationDate;
      }
   }

   class Serializer implements JsonDeserializer, JsonSerializer {
      private Serializer() {
      }

      public JsonElement serialize(GameProfileCache.GameProfileInfo gameProfileCache$GameProfileInfo, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject var4 = new JsonObject();
         var4.addProperty("name", gameProfileCache$GameProfileInfo.getProfile().getName());
         UUID var5 = gameProfileCache$GameProfileInfo.getProfile().getId();
         var4.addProperty("uuid", var5 == null?"":var5.toString());
         var4.addProperty("expiresOn", GameProfileCache.DATE_FORMAT.format(gameProfileCache$GameProfileInfo.getExpirationDate()));
         return var4;
      }

      public GameProfileCache.GameProfileInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         if(jsonElement.isJsonObject()) {
            JsonObject var4 = jsonElement.getAsJsonObject();
            JsonElement var5 = var4.get("name");
            JsonElement var6 = var4.get("uuid");
            JsonElement var7 = var4.get("expiresOn");
            if(var5 != null && var6 != null) {
               String var8 = var6.getAsString();
               String var9 = var5.getAsString();
               Date var10 = null;
               if(var7 != null) {
                  try {
                     var10 = GameProfileCache.DATE_FORMAT.parse(var7.getAsString());
                  } catch (ParseException var14) {
                     var10 = null;
                  }
               }

               if(var9 != null && var8 != null) {
                  UUID var11;
                  try {
                     var11 = UUID.fromString(var8);
                  } catch (Throwable var13) {
                     return null;
                  }

                  return GameProfileCache.this.new GameProfileInfo(new GameProfile(var11, var9), var10);
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((GameProfileCache.GameProfileInfo)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
