package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureManager implements ResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map structureRepository = Maps.newHashMap();
   private final DataFixer fixerUpper;
   private final MinecraftServer server;
   private final Path generatedDir;

   public StructureManager(MinecraftServer server, File file, DataFixer fixerUpper) {
      this.server = server;
      this.fixerUpper = fixerUpper;
      this.generatedDir = file.toPath().resolve("generated").normalize();
      server.getResources().registerReloadListener(this);
   }

   public StructureTemplate getOrCreate(ResourceLocation resourceLocation) {
      StructureTemplate structureTemplate = this.get(resourceLocation);
      if(structureTemplate == null) {
         structureTemplate = new StructureTemplate();
         this.structureRepository.put(resourceLocation, structureTemplate);
      }

      return structureTemplate;
   }

   @Nullable
   public StructureTemplate get(ResourceLocation resourceLocation) {
      return (StructureTemplate)this.structureRepository.computeIfAbsent(resourceLocation, (resourceLocation) -> {
         StructureTemplate structureTemplate = this.loadFromGenerated(resourceLocation);
         return structureTemplate != null?structureTemplate:this.loadFromResource(resourceLocation);
      });
   }

   public void onResourceManagerReload(ResourceManager resourceManager) {
      this.structureRepository.clear();
   }

   @Nullable
   private StructureTemplate loadFromResource(ResourceLocation resourceLocation) {
      ResourceLocation resourceLocation = new ResourceLocation(resourceLocation.getNamespace(), "structures/" + resourceLocation.getPath() + ".nbt");

      try {
         Resource var3 = this.server.getResources().getResource(resourceLocation);
         Throwable var4 = null;

         StructureTemplate var5;
         try {
            var5 = this.readStructure(var3.getInputStream());
         } catch (Throwable var16) {
            var4 = var16;
            throw var16;
         } finally {
            if(var3 != null) {
               if(var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var15) {
                     var4.addSuppressed(var15);
                  }
               } else {
                  var3.close();
               }
            }

         }

         return var5;
      } catch (FileNotFoundException var18) {
         return null;
      } catch (Throwable var19) {
         LOGGER.error("Couldn\'t load structure {}: {}", resourceLocation, var19.toString());
         return null;
      }
   }

   @Nullable
   private StructureTemplate loadFromGenerated(ResourceLocation resourceLocation) {
      if(!this.generatedDir.toFile().isDirectory()) {
         return null;
      } else {
         Path var2 = this.createAndValidatePathToStructure(resourceLocation, ".nbt");

         try {
            InputStream var3 = new FileInputStream(var2.toFile());
            Throwable var4 = null;

            StructureTemplate var5;
            try {
               var5 = this.readStructure(var3);
            } catch (Throwable var16) {
               var4 = var16;
               throw var16;
            } finally {
               if(var3 != null) {
                  if(var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var15) {
                        var4.addSuppressed(var15);
                     }
                  } else {
                     var3.close();
                  }
               }

            }

            return var5;
         } catch (FileNotFoundException var18) {
            return null;
         } catch (IOException var19) {
            LOGGER.error("Couldn\'t load structure from {}", var2, var19);
            return null;
         }
      }
   }

   private StructureTemplate readStructure(InputStream inputStream) throws IOException {
      CompoundTag var2 = NbtIo.readCompressed(inputStream);
      if(!var2.contains("DataVersion", 99)) {
         var2.putInt("DataVersion", 500);
      }

      StructureTemplate var3 = new StructureTemplate();
      var3.load(NbtUtils.update(this.fixerUpper, DataFixTypes.STRUCTURE, var2, var2.getInt("DataVersion")));
      return var3;
   }

   public boolean save(ResourceLocation resourceLocation) {
      StructureTemplate var2 = (StructureTemplate)this.structureRepository.get(resourceLocation);
      if(var2 == null) {
         return false;
      } else {
         Path var3 = this.createAndValidatePathToStructure(resourceLocation, ".nbt");
         Path var4 = var3.getParent();
         if(var4 == null) {
            return false;
         } else {
            try {
               Files.createDirectories(Files.exists(var4, new LinkOption[0])?var4.toRealPath(new LinkOption[0]):var4, new FileAttribute[0]);
            } catch (IOException var19) {
               LOGGER.error("Failed to create parent directory: {}", var4);
               return false;
            }

            CompoundTag var5 = var2.save(new CompoundTag());

            try {
               OutputStream var6 = new FileOutputStream(var3.toFile());
               Throwable var7 = null;

               try {
                  NbtIo.writeCompressed(var5, var6);
               } catch (Throwable var18) {
                  var7 = var18;
                  throw var18;
               } finally {
                  if(var6 != null) {
                     if(var7 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var17) {
                           var7.addSuppressed(var17);
                        }
                     } else {
                        var6.close();
                     }
                  }

               }

               return true;
            } catch (Throwable var21) {
               return false;
            }
         }
      }
   }

   private Path createPathToStructure(ResourceLocation resourceLocation, String string) {
      try {
         Path path = this.generatedDir.resolve(resourceLocation.getNamespace());
         Path var4 = path.resolve("structures");
         return FileUtil.createPathToResource(var4, resourceLocation.getPath(), string);
      } catch (InvalidPathException var5) {
         throw new ResourceLocationException("Invalid resource path: " + resourceLocation, var5);
      }
   }

   private Path createAndValidatePathToStructure(ResourceLocation resourceLocation, String string) {
      if(resourceLocation.getPath().contains("//")) {
         throw new ResourceLocationException("Invalid resource path: " + resourceLocation);
      } else {
         Path path = this.createPathToStructure(resourceLocation, string);
         if(path.startsWith(this.generatedDir) && FileUtil.isPathNormalized(path) && FileUtil.isPathPortable(path)) {
            return path;
         } else {
            throw new ResourceLocationException("Invalid resource path: " + path);
         }
      }
   }

   public void remove(ResourceLocation resourceLocation) {
      this.structureRepository.remove(resourceLocation);
   }
}
