package net.minecraft.client.main;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.platform.DisplayData;
import java.io.File;
import java.net.Proxy;
import javax.annotation.Nullable;
import net.minecraft.client.User;
import net.minecraft.client.resources.AssetIndex;
import net.minecraft.client.resources.DirectAssetIndex;

@ClientJarOnly
public class GameConfig {
   public final GameConfig.UserData user;
   public final DisplayData display;
   public final GameConfig.FolderData location;
   public final GameConfig.GameData game;
   public final GameConfig.ServerData server;

   public GameConfig(GameConfig.UserData user, DisplayData display, GameConfig.FolderData location, GameConfig.GameData game, GameConfig.ServerData server) {
      this.user = user;
      this.display = display;
      this.location = location;
      this.game = game;
      this.server = server;
   }

   @ClientJarOnly
   public static class FolderData {
      public final File gameDirectory;
      public final File resourcePackDirectory;
      public final File assetDirectory;
      public final String assetIndex;

      public FolderData(File gameDirectory, File resourcePackDirectory, File assetDirectory, @Nullable String assetIndex) {
         this.gameDirectory = gameDirectory;
         this.resourcePackDirectory = resourcePackDirectory;
         this.assetDirectory = assetDirectory;
         this.assetIndex = assetIndex;
      }

      public AssetIndex getAssetIndex() {
         return (AssetIndex)(this.assetIndex == null?new DirectAssetIndex(this.assetDirectory):new AssetIndex(this.assetDirectory, this.assetIndex));
      }
   }

   @ClientJarOnly
   public static class GameData {
      public final boolean demo;
      public final String launchVersion;
      public final String versionType;

      public GameData(boolean demo, String launchVersion, String versionType) {
         this.demo = demo;
         this.launchVersion = launchVersion;
         this.versionType = versionType;
      }
   }

   @ClientJarOnly
   public static class ServerData {
      public final String hostname;
      public final int port;

      public ServerData(String hostname, int port) {
         this.hostname = hostname;
         this.port = port;
      }
   }

   @ClientJarOnly
   public static class UserData {
      public final User user;
      public final PropertyMap userProperties;
      public final PropertyMap profileProperties;
      public final Proxy proxy;

      public UserData(User user, PropertyMap userProperties, PropertyMap profileProperties, Proxy proxy) {
         this.user = user;
         this.userProperties = userProperties;
         this.profileProperties = profileProperties;
         this.proxy = proxy;
      }
   }
}
