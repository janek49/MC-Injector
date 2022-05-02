package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Proxy;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RepeatedNarrator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;

@ClientJarOnly
public class Realms {
   private static final RepeatedNarrator REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));

   public static boolean isTouchScreen() {
      return Minecraft.getInstance().options.touchscreen;
   }

   public static Proxy getProxy() {
      return Minecraft.getInstance().getProxy();
   }

   public static String sessionId() {
      User var0 = Minecraft.getInstance().getUser();
      return var0 == null?null:var0.getSessionId();
   }

   public static String userName() {
      User var0 = Minecraft.getInstance().getUser();
      return var0 == null?null:var0.getName();
   }

   public static long currentTimeMillis() {
      return Util.getMillis();
   }

   public static String getSessionId() {
      return Minecraft.getInstance().getUser().getSessionId();
   }

   public static String getUUID() {
      return Minecraft.getInstance().getUser().getUuid();
   }

   public static String getName() {
      return Minecraft.getInstance().getUser().getName();
   }

   public static String uuidToName(String string) {
      return Minecraft.getInstance().getMinecraftSessionService().fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(string), (String)null), false).getName();
   }

   public static CompletableFuture execute(Supplier supplier) {
      return Minecraft.getInstance().submit(supplier);
   }

   public static void execute(Runnable runnable) {
      Minecraft.getInstance().execute(runnable);
   }

   public static void setScreen(RealmsScreen screen) {
      execute(() -> {
         setScreenDirect(screen);
         return null;
      });
   }

   public static void setScreenDirect(RealmsScreen screenDirect) {
      Minecraft.getInstance().setScreen(screenDirect.getProxy());
   }

   public static String getGameDirectoryPath() {
      return Minecraft.getInstance().gameDirectory.getAbsolutePath();
   }

   public static int survivalId() {
      return GameType.SURVIVAL.getId();
   }

   public static int creativeId() {
      return GameType.CREATIVE.getId();
   }

   public static int adventureId() {
      return GameType.ADVENTURE.getId();
   }

   public static int spectatorId() {
      return GameType.SPECTATOR.getId();
   }

   public static void setConnectedToRealms(boolean connectedToRealms) {
      Minecraft.getInstance().setConnectedToRealms(connectedToRealms);
   }

   public static CompletableFuture downloadResourcePack(String var0, String var1) {
      return Minecraft.getInstance().getClientPackSource().downloadAndSelectResourcePack(var0, var1);
   }

   public static void clearResourcePack() {
      Minecraft.getInstance().getClientPackSource().clearServerPack();
   }

   public static boolean getRealmsNotificationsEnabled() {
      return Minecraft.getInstance().options.realmsNotifications;
   }

   public static boolean inTitleScreen() {
      return Minecraft.getInstance().screen != null && Minecraft.getInstance().screen instanceof TitleScreen;
   }

   public static void deletePlayerTag(File file) {
      if(file.exists()) {
         try {
            CompoundTag var1 = NbtIo.readCompressed(new FileInputStream(file));
            CompoundTag var2 = var1.getCompound("Data");
            var2.remove("Player");
            NbtIo.writeCompressed(var1, new FileOutputStream(file));
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

   }

   public static void openUri(String string) {
      Util.getPlatform().openUri(string);
   }

   public static void setClipboard(String clipboard) {
      Minecraft.getInstance().keyboardHandler.setClipboard(clipboard);
   }

   public static String getMinecraftVersionString() {
      return SharedConstants.getCurrentVersion().getName();
   }

   public static ResourceLocation resourceLocation(String string) {
      return new ResourceLocation(string);
   }

   public static String getLocalizedString(String var0, Object... objects) {
      return I18n.get(var0, objects);
   }

   public static void bind(String string) {
      ResourceLocation var1 = new ResourceLocation(string);
      Minecraft.getInstance().getTextureManager().bind(var1);
   }

   public static void narrateNow(String string) {
      NarratorChatListener var1 = NarratorChatListener.INSTANCE;
      var1.clear();
      var1.handle(ChatType.SYSTEM, new TextComponent(fixNarrationNewlines(string)));
   }

   private static String fixNarrationNewlines(String string) {
      return string.replace("\\n", System.lineSeparator());
   }

   public static void narrateNow(String... strings) {
      narrateNow((Iterable)Arrays.asList(strings));
   }

   public static void narrateNow(Iterable iterable) {
      narrateNow(joinNarrations(iterable));
   }

   public static String joinNarrations(Iterable iterable) {
      return String.join(System.lineSeparator(), iterable);
   }

   public static void narrateRepeatedly(String string) {
      REPEATED_NARRATOR.narrate(fixNarrationNewlines(string));
   }
}
