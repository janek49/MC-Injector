package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.players.BanListEntry;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldUsersConverter {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final File OLD_IPBANLIST = new File("banned-ips.txt");
   public static final File OLD_USERBANLIST = new File("banned-players.txt");
   public static final File OLD_OPLIST = new File("ops.txt");
   public static final File OLD_WHITELIST = new File("white-list.txt");

   static List readOldListFormat(File file, Map map) throws IOException {
      List<String> list = Files.readLines(file, StandardCharsets.UTF_8);

      for(String var4 : list) {
         var4 = var4.trim();
         if(!var4.startsWith("#") && var4.length() >= 1) {
            String[] vars5 = var4.split("\\|");
            map.put(vars5[0].toLowerCase(Locale.ROOT), vars5);
         }
      }

      return list;
   }

   private static void lookupPlayers(MinecraftServer minecraftServer, Collection collection, ProfileLookupCallback profileLookupCallback) {
      String[] vars3 = (String[])collection.stream().filter((string) -> {
         return !StringUtil.isNullOrEmpty(string);
      }).toArray((i) -> {
         return new String[i];
      });
      if(minecraftServer.usesAuthentication()) {
         minecraftServer.getProfileRepository().findProfilesByNames(vars3, Agent.MINECRAFT, profileLookupCallback);
      } else {
         for(String var7 : vars3) {
            UUID var8 = Player.createPlayerUUID(new GameProfile((UUID)null, var7));
            GameProfile var9 = new GameProfile(var8, var7);
            profileLookupCallback.onProfileLookupSucceeded(var9);
         }
      }

   }

   public static boolean convertUserBanlist(final MinecraftServer minecraftServer) {
      final UserBanList var1 = new UserBanList(PlayerList.USERBANLIST_FILE);
      if(OLD_USERBANLIST.exists() && OLD_USERBANLIST.isFile()) {
         if(var1.getFile().exists()) {
            try {
               var1.load();
            } catch (FileNotFoundException var6) {
               LOGGER.warn("Could not load existing file {}", var1.getFile().getName(), var6);
            }
         }

         try {
            final Map<String, String[]> var2 = Maps.newHashMap();
            readOldListFormat(OLD_USERBANLIST, var2);
            ProfileLookupCallback var3 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile gameProfile) {
                  minecraftServer.getProfileCache().add(gameProfile);
                  String[] vars2 = (String[])var2.get(gameProfile.getName().toLowerCase(Locale.ROOT));
                  if(vars2 == null) {
                     OldUsersConverter.LOGGER.warn("Could not convert user banlist entry for {}", gameProfile.getName());
                     throw new OldUsersConverter.ConversionError("Profile not in the conversionlist");
                  } else {
                     Date var3 = vars2.length > 1?OldUsersConverter.parseDate(vars2[1], (Date)null):null;
                     String var4 = vars2.length > 2?vars2[2]:null;
                     Date var5 = vars2.length > 3?OldUsersConverter.parseDate(vars2[3], (Date)null):null;
                     String var6 = vars2.length > 4?vars2[4]:null;
                     var1.add(new UserBanListEntry(gameProfile, var3, var4, var5, var6));
                  }
               }

               public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
                  OldUsersConverter.LOGGER.warn("Could not lookup user banlist entry for {}", gameProfile.getName(), exception);
                  if(!(exception instanceof ProfileNotFoundException)) {
                     throw new OldUsersConverter.ConversionError("Could not request user " + gameProfile.getName() + " from backend systems", exception);
                  }
               }
            };
            lookupPlayers(minecraftServer, var2.keySet(), var3);
            var1.save();
            renameOldFile(OLD_USERBANLIST);
            return true;
         } catch (IOException var4) {
            LOGGER.warn("Could not read old user banlist to convert it!", var4);
            return false;
         } catch (OldUsersConverter.ConversionError var5) {
            LOGGER.error("Conversion failed, please try again later", var5);
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean convertIpBanlist(MinecraftServer minecraftServer) {
      IpBanList var1 = new IpBanList(PlayerList.IPBANLIST_FILE);
      if(OLD_IPBANLIST.exists() && OLD_IPBANLIST.isFile()) {
         if(var1.getFile().exists()) {
            try {
               var1.load();
            } catch (FileNotFoundException var11) {
               LOGGER.warn("Could not load existing file {}", var1.getFile().getName(), var11);
            }
         }

         try {
            Map<String, String[]> var2 = Maps.newHashMap();
            readOldListFormat(OLD_IPBANLIST, var2);

            for(String var4 : var2.keySet()) {
               String[] vars5 = (String[])var2.get(var4);
               Date var6 = vars5.length > 1?parseDate(vars5[1], (Date)null):null;
               String var7 = vars5.length > 2?vars5[2]:null;
               Date var8 = vars5.length > 3?parseDate(vars5[3], (Date)null):null;
               String var9 = vars5.length > 4?vars5[4]:null;
               var1.add(new IpBanListEntry(var4, var6, var7, var8, var9));
            }

            var1.save();
            renameOldFile(OLD_IPBANLIST);
            return true;
         } catch (IOException var10) {
            LOGGER.warn("Could not parse old ip banlist to convert it!", var10);
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean convertOpsList(final MinecraftServer minecraftServer) {
      final ServerOpList var1 = new ServerOpList(PlayerList.OPLIST_FILE);
      if(OLD_OPLIST.exists() && OLD_OPLIST.isFile()) {
         if(var1.getFile().exists()) {
            try {
               var1.load();
            } catch (FileNotFoundException var6) {
               LOGGER.warn("Could not load existing file {}", var1.getFile().getName(), var6);
            }
         }

         try {
            List<String> var2 = Files.readLines(OLD_OPLIST, StandardCharsets.UTF_8);
            ProfileLookupCallback var3 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile gameProfile) {
                  minecraftServer.getProfileCache().add(gameProfile);
                  var1.add(new ServerOpListEntry(gameProfile, minecraftServer.getOperatorUserPermissionLevel(), false));
               }

               public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
                  OldUsersConverter.LOGGER.warn("Could not lookup oplist entry for {}", gameProfile.getName(), exception);
                  if(!(exception instanceof ProfileNotFoundException)) {
                     throw new OldUsersConverter.ConversionError("Could not request user " + gameProfile.getName() + " from backend systems", exception);
                  }
               }
            };
            lookupPlayers(minecraftServer, var2, var3);
            var1.save();
            renameOldFile(OLD_OPLIST);
            return true;
         } catch (IOException var4) {
            LOGGER.warn("Could not read old oplist to convert it!", var4);
            return false;
         } catch (OldUsersConverter.ConversionError var5) {
            LOGGER.error("Conversion failed, please try again later", var5);
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean convertWhiteList(final MinecraftServer minecraftServer) {
      final UserWhiteList var1 = new UserWhiteList(PlayerList.WHITELIST_FILE);
      if(OLD_WHITELIST.exists() && OLD_WHITELIST.isFile()) {
         if(var1.getFile().exists()) {
            try {
               var1.load();
            } catch (FileNotFoundException var6) {
               LOGGER.warn("Could not load existing file {}", var1.getFile().getName(), var6);
            }
         }

         try {
            List<String> var2 = Files.readLines(OLD_WHITELIST, StandardCharsets.UTF_8);
            ProfileLookupCallback var3 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile gameProfile) {
                  minecraftServer.getProfileCache().add(gameProfile);
                  var1.add(new UserWhiteListEntry(gameProfile));
               }

               public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
                  OldUsersConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", gameProfile.getName(), exception);
                  if(!(exception instanceof ProfileNotFoundException)) {
                     throw new OldUsersConverter.ConversionError("Could not request user " + gameProfile.getName() + " from backend systems", exception);
                  }
               }
            };
            lookupPlayers(minecraftServer, var2, var3);
            var1.save();
            renameOldFile(OLD_WHITELIST);
            return true;
         } catch (IOException var4) {
            LOGGER.warn("Could not read old whitelist to convert it!", var4);
            return false;
         } catch (OldUsersConverter.ConversionError var5) {
            LOGGER.error("Conversion failed, please try again later", var5);
            return false;
         }
      } else {
         return true;
      }
   }

   public static String convertMobOwnerIfNecessary(final MinecraftServer minecraftServer, String var1) {
      if(!StringUtil.isNullOrEmpty(var1) && var1.length() <= 16) {
         GameProfile var2 = minecraftServer.getProfileCache().get(var1);
         if(var2 != null && var2.getId() != null) {
            return var2.getId().toString();
         } else if(!minecraftServer.isSingleplayer() && minecraftServer.usesAuthentication()) {
            final List<GameProfile> var3 = Lists.newArrayList();
            ProfileLookupCallback var4 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile gameProfile) {
                  minecraftServer.getProfileCache().add(gameProfile);
                  var3.add(gameProfile);
               }

               public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
                  OldUsersConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", gameProfile.getName(), exception);
               }
            };
            lookupPlayers(minecraftServer, Lists.newArrayList(new String[]{var1}), var4);
            return !var3.isEmpty() && ((GameProfile)var3.get(0)).getId() != null?((GameProfile)var3.get(0)).getId().toString():"";
         } else {
            return Player.createPlayerUUID(new GameProfile((UUID)null, var1)).toString();
         }
      } else {
         return var1;
      }
   }

   public static boolean convertPlayers(final DedicatedServer dedicatedServer) {
      final File var1 = getWorldPlayersDirectory(dedicatedServer);
      final File var2 = new File(var1.getParentFile(), "playerdata");
      final File var3 = new File(var1.getParentFile(), "unknownplayers");
      if(var1.exists() && var1.isDirectory()) {
         File[] vars4 = var1.listFiles();
         List<String> var5 = Lists.newArrayList();

         for(File var9 : vars4) {
            String var10 = var9.getName();
            if(var10.toLowerCase(Locale.ROOT).endsWith(".dat")) {
               String var11 = var10.substring(0, var10.length() - ".dat".length());
               if(!var11.isEmpty()) {
                  var5.add(var11);
               }
            }
         }

         try {
            final String[] vars6 = (String[])var5.toArray(new String[var5.size()]);
            ProfileLookupCallback var7 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile gameProfile) {
                  dedicatedServer.getProfileCache().add(gameProfile);
                  UUID var2 = gameProfile.getId();
                  if(var2 == null) {
                     throw new OldUsersConverter.ConversionError("Missing UUID for user profile " + gameProfile.getName());
                  } else {
                     this.movePlayerFile(var2, this.getFileNameForProfile(gameProfile), var2.toString());
                  }
               }

               public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
                  OldUsersConverter.LOGGER.warn("Could not lookup user uuid for {}", gameProfile.getName(), exception);
                  if(exception instanceof ProfileNotFoundException) {
                     String var3 = this.getFileNameForProfile(gameProfile);
                     this.movePlayerFile(var3, var3, var3);
                  } else {
                     throw new OldUsersConverter.ConversionError("Could not request user " + gameProfile.getName() + " from backend systems", exception);
                  }
               }

               private void movePlayerFile(File file, String var2x, String var3x) {
                  File file = new File(var1, var2x + ".dat");
                  File var5 = new File(file, var3x + ".dat");
                  OldUsersConverter.ensureDirectoryExists(file);
                  if(!file.renameTo(var5)) {
                     throw new OldUsersConverter.ConversionError("Could not convert file for " + var2x);
                  }
               }

               private String getFileNameForProfile(GameProfile gameProfile) {
                  String string = null;

                  for(String var6 : vars6) {
                     if(var6 != null && var6.equalsIgnoreCase(gameProfile.getName())) {
                        string = var6;
                        break;
                     }
                  }

                  if(string == null) {
                     throw new OldUsersConverter.ConversionError("Could not find the filename for " + gameProfile.getName() + " anymore");
                  } else {
                     return string;
                  }
               }
            };
            lookupPlayers(dedicatedServer, Lists.newArrayList(vars6), var7);
            return true;
         } catch (OldUsersConverter.ConversionError var12) {
            LOGGER.error("Conversion failed, please try again later", var12);
            return false;
         }
      } else {
         return true;
      }
   }

   private static void ensureDirectoryExists(File file) {
      if(file.exists()) {
         if(!file.isDirectory()) {
            throw new OldUsersConverter.ConversionError("Can\'t create directory " + file.getName() + " in world save directory.");
         }
      } else if(!file.mkdirs()) {
         throw new OldUsersConverter.ConversionError("Can\'t create directory " + file.getName() + " in world save directory.");
      }
   }

   public static boolean serverReadyAfterUserconversion(MinecraftServer minecraftServer) {
      boolean var1 = areOldUserlistsRemoved();
      var1 = var1 && areOldPlayersConverted(minecraftServer);
      return var1;
   }

   private static boolean areOldUserlistsRemoved() {
      boolean var0 = false;
      if(OLD_USERBANLIST.exists() && OLD_USERBANLIST.isFile()) {
         var0 = true;
      }

      boolean var1 = false;
      if(OLD_IPBANLIST.exists() && OLD_IPBANLIST.isFile()) {
         var1 = true;
      }

      boolean var2 = false;
      if(OLD_OPLIST.exists() && OLD_OPLIST.isFile()) {
         var2 = true;
      }

      boolean var3 = false;
      if(OLD_WHITELIST.exists() && OLD_WHITELIST.isFile()) {
         var3 = true;
      }

      if(!var0 && !var1 && !var2 && !var3) {
         return true;
      } else {
         LOGGER.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
         LOGGER.warn("** please remove the following files and restart the server:");
         if(var0) {
            LOGGER.warn("* {}", OLD_USERBANLIST.getName());
         }

         if(var1) {
            LOGGER.warn("* {}", OLD_IPBANLIST.getName());
         }

         if(var2) {
            LOGGER.warn("* {}", OLD_OPLIST.getName());
         }

         if(var3) {
            LOGGER.warn("* {}", OLD_WHITELIST.getName());
         }

         return false;
      }
   }

   private static boolean areOldPlayersConverted(MinecraftServer minecraftServer) {
      File var1 = getWorldPlayersDirectory(minecraftServer);
      if(!var1.exists() || !var1.isDirectory() || var1.list().length <= 0 && var1.delete()) {
         return true;
      } else {
         LOGGER.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
         LOGGER.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
         LOGGER.warn("** please restart the server and if the problem persists, remove the directory \'{}\'", var1.getPath());
         return false;
      }
   }

   private static File getWorldPlayersDirectory(MinecraftServer minecraftServer) {
      String var1 = minecraftServer.getLevelIdName();
      File var2 = new File(var1);
      return new File(var2, "players");
   }

   private static void renameOldFile(File file) {
      File file = new File(file.getName() + ".converted");
      file.renameTo(file);
   }

   private static Date parseDate(String string, Date var1) {
      Date var2;
      try {
         var2 = BanListEntry.DATE_FORMAT.parse(string);
      } catch (ParseException var4) {
         var2 = var1;
      }

      return var2;
   }

   static class ConversionError extends RuntimeException {
      private ConversionError(String string, Throwable throwable) {
         super(string, throwable);
      }

      private ConversionError(String string) {
         super(string);
      }
   }
}
