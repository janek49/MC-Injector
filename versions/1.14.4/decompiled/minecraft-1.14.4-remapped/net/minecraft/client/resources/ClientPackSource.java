package net.minecraft.client.resources;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.resources.AssetIndex;
import net.minecraft.client.resources.DefaultClientResourcePack;
import net.minecraft.client.resources.UnopenedResourcePack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FileResourcePack;
import net.minecraft.server.packs.VanillaPack;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.UnopenedPack;
import net.minecraft.util.HttpUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class ClientPackSource implements RepositorySource {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
   private final VanillaPack vanillaPack;
   private final File serverPackDir;
   private final ReentrantLock downloadLock = new ReentrantLock();
   private final AssetIndex assetIndex;
   @Nullable
   private CompletableFuture currentDownload;
   @Nullable
   private UnopenedResourcePack serverPack;

   public ClientPackSource(File serverPackDir, AssetIndex assetIndex) {
      this.serverPackDir = serverPackDir;
      this.assetIndex = assetIndex;
      this.vanillaPack = new DefaultClientResourcePack(assetIndex);
   }

   public void loadPacks(Map map, UnopenedPack.UnopenedPackConstructor unopenedPack$UnopenedPackConstructor) {
      T var3 = UnopenedPack.create("vanilla", true, () -> {
         return this.vanillaPack;
      }, unopenedPack$UnopenedPackConstructor, UnopenedPack.Position.BOTTOM);
      if(var3 != null) {
         map.put("vanilla", var3);
      }

      if(this.serverPack != null) {
         map.put("server", this.serverPack);
      }

      File var4 = this.assetIndex.getFile(new ResourceLocation("resourcepacks/programmer_art.zip"));
      if(var4 != null && var4.isFile()) {
         T var5 = UnopenedPack.create("programer_art", false, () -> {
            return new FileResourcePack(var4) {
               public String getName() {
                  return "Programmer Art";
               }
            };
         }, unopenedPack$UnopenedPackConstructor, UnopenedPack.Position.TOP);
         if(var5 != null) {
            map.put("programer_art", var5);
         }
      }

   }

   public VanillaPack getVanillaPack() {
      return this.vanillaPack;
   }

   public static Map getDownloadHeaders() {
      Map<String, String> map = Maps.newHashMap();
      map.put("X-Minecraft-Username", Minecraft.getInstance().getUser().getName());
      map.put("X-Minecraft-UUID", Minecraft.getInstance().getUser().getUuid());
      map.put("X-Minecraft-Version", SharedConstants.getCurrentVersion().getName());
      map.put("X-Minecraft-Version-ID", SharedConstants.getCurrentVersion().getId());
      map.put("X-Minecraft-Pack-Format", String.valueOf(SharedConstants.getCurrentVersion().getPackVersion()));
      map.put("User-Agent", "Minecraft Java/" + SharedConstants.getCurrentVersion().getName());
      return map;
   }

   public CompletableFuture downloadAndSelectResourcePack(String var1, String var2) {
      String var3 = DigestUtils.sha1Hex(var1);
      String var4 = SHA1.matcher(var2).matches()?var2:"";
      this.downloadLock.lock();

      CompletableFuture var13;
      try {
         this.clearServerPack();
         this.clearOldDownloads();
         File var5 = new File(this.serverPackDir, var3);
         CompletableFuture<?> var6;
         if(var5.exists()) {
            var6 = CompletableFuture.completedFuture("");
         } else {
            ProgressScreen var7 = new ProgressScreen();
            Map<String, String> var8 = getDownloadHeaders();
            Minecraft var9 = Minecraft.getInstance();
            var9.executeBlocking(() -> {
               var9.setScreen(var7);
            });
            var6 = HttpUtil.downloadTo(var5, var1, var8, 52428800, var7, var9.getProxy());
         }

         this.currentDownload = var6.thenCompose((object) -> {
            return !this.checkHash(var4, var5)?Util.failedFuture(new RuntimeException("Hash check failure for file " + var5 + ", see log")):this.setServerPack(var5);
         }).whenComplete((void, throwable) -> {
            if(throwable != null) {
               LOGGER.warn("Pack application failed: {}, deleting file {}", throwable.getMessage(), var5);
               deleteQuietly(var5);
            }

         });
         var13 = this.currentDownload;
      } finally {
         this.downloadLock.unlock();
      }

      return var13;
   }

   private static void deleteQuietly(File file) {
      try {
         Files.delete(file.toPath());
      } catch (IOException var2) {
         LOGGER.warn("Failed to delete file {}: {}", file, var2.getMessage());
      }

   }

   public void clearServerPack() {
      this.downloadLock.lock();

      try {
         if(this.currentDownload != null) {
            this.currentDownload.cancel(true);
         }

         this.currentDownload = null;
         if(this.serverPack != null) {
            this.serverPack = null;
            Minecraft.getInstance().delayTextureReload();
         }
      } finally {
         this.downloadLock.unlock();
      }

   }

   private boolean checkHash(String string, File file) {
      try {
         FileInputStream var4 = new FileInputStream(file);
         Throwable var5 = null;

         String string;
         try {
            string = DigestUtils.sha1Hex(var4);
         } catch (Throwable var15) {
            var5 = var15;
            throw var15;
         } finally {
            if(var4 != null) {
               if(var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var14) {
                     var5.addSuppressed(var14);
                  }
               } else {
                  var4.close();
               }
            }

         }

         if(string.isEmpty()) {
            LOGGER.info("Found file {} without verification hash", file);
            return true;
         }

         if(string.toLowerCase(Locale.ROOT).equals(string.toLowerCase(Locale.ROOT))) {
            LOGGER.info("Found file {} matching requested hash {}", file, string);
            return true;
         }

         LOGGER.warn("File {} had wrong hash (expected {}, found {}).", file, string, string);
      } catch (IOException var17) {
         LOGGER.warn("File {} couldn\'t be hashed.", file, var17);
      }

      return false;
   }

   private void clearOldDownloads() {
      try {
         List<File> var1 = Lists.newArrayList(FileUtils.listFiles(this.serverPackDir, TrueFileFilter.TRUE, (IOFileFilter)null));
         var1.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
         int var2 = 0;

         for(File var4 : var1) {
            if(var2++ >= 10) {
               LOGGER.info("Deleting old server resource pack {}", var4.getName());
               FileUtils.deleteQuietly(var4);
            }
         }
      } catch (IllegalArgumentException var5) {
         LOGGER.error("Error while deleting old server resource pack : {}", var5.getMessage());
      }

   }

   public CompletableFuture setServerPack(File serverPack) {
      PackMetadataSection var2 = null;
      NativeImage var3 = null;
      String var4 = null;

      try {
         FileResourcePack var5 = new FileResourcePack(serverPack);
         Throwable var6 = null;

         try {
            var2 = (PackMetadataSection)var5.getMetadataSection(PackMetadataSection.SERIALIZER);

            try {
               InputStream var7 = var5.getRootResource("pack.png");
               Throwable var8 = null;

               try {
                  var3 = NativeImage.read(var7);
               } catch (Throwable var35) {
                  var8 = var35;
                  throw var35;
               } finally {
                  if(var7 != null) {
                     if(var8 != null) {
                        try {
                           var7.close();
                        } catch (Throwable var34) {
                           var8.addSuppressed(var34);
                        }
                     } else {
                        var7.close();
                     }
                  }

               }
            } catch (IllegalArgumentException | IOException var37) {
               LOGGER.info("Could not read pack.png: {}", var37.getMessage());
            }
         } catch (Throwable var38) {
            var6 = var38;
            throw var38;
         } finally {
            if(var5 != null) {
               if(var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var33) {
                     var6.addSuppressed(var33);
                  }
               } else {
                  var5.close();
               }
            }

         }
      } catch (IOException var40) {
         var4 = var40.getMessage();
      }

      if(var4 != null) {
         return Util.failedFuture(new RuntimeException(String.format("Invalid resourcepack at %s: %s", new Object[]{serverPack, var4})));
      } else {
         LOGGER.info("Applying server pack {}", serverPack);
         this.serverPack = new UnopenedResourcePack("server", true, () -> {
            return new FileResourcePack(serverPack);
         }, new TranslatableComponent("resourcePack.server.name", new Object[0]), var2.getDescription(), PackCompatibility.forFormat(var2.getPackFormat()), UnopenedPack.Position.TOP, true, var3);
         return Minecraft.getInstance().delayTextureReload();
      }
   }
}
