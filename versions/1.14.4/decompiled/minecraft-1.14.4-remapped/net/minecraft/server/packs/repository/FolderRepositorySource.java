package net.minecraft.server.packs.repository;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.server.packs.FileResourcePack;
import net.minecraft.server.packs.FolderResourcePack;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.UnopenedPack;

public class FolderRepositorySource implements RepositorySource {
   private static final FileFilter RESOURCEPACK_FILTER = (file) -> {
      boolean var1 = file.isFile() && file.getName().endsWith(".zip");
      boolean var2 = file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();
      return var1 || var2;
   };
   private final File folder;

   public FolderRepositorySource(File folder) {
      this.folder = folder;
   }

   public void loadPacks(Map map, UnopenedPack.UnopenedPackConstructor unopenedPack$UnopenedPackConstructor) {
      if(!this.folder.isDirectory()) {
         this.folder.mkdirs();
      }

      File[] vars3 = this.folder.listFiles(RESOURCEPACK_FILTER);
      if(vars3 != null) {
         for(File var7 : vars3) {
            String var8 = "file/" + var7.getName();
            T var9 = UnopenedPack.create(var8, false, this.createSupplier(var7), unopenedPack$UnopenedPackConstructor, UnopenedPack.Position.TOP);
            if(var9 != null) {
               map.put(var8, var9);
            }
         }

      }
   }

   private Supplier createSupplier(File file) {
      return file.isDirectory()?() -> {
         return new FolderResourcePack(file);
      }:() -> {
         return new FileResourcePack(file);
      };
   }
}
