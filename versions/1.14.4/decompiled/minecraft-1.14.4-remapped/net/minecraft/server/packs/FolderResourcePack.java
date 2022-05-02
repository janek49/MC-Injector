package net.minecraft.server.packs;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractResourcePack;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FolderResourcePack extends AbstractResourcePack {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final boolean ON_WINDOWS = Util.getPlatform() == Util.OS.WINDOWS;
   private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.is('\\');

   public FolderResourcePack(File file) {
      super(file);
   }

   public static boolean validatePath(File file, String string) throws IOException {
      String string = file.getCanonicalPath();
      if(ON_WINDOWS) {
         string = BACKSLASH_MATCHER.replaceFrom(string, '/');
      }

      return string.endsWith(string);
   }

   protected InputStream getResource(String string) throws IOException {
      File var2 = this.getFile(string);
      if(var2 == null) {
         throw new ResourcePackFileNotFoundException(this.file, string);
      } else {
         return new FileInputStream(var2);
      }
   }

   protected boolean hasResource(String string) {
      return this.getFile(string) != null;
   }

   @Nullable
   private File getFile(String string) {
      try {
         File file = new File(this.file, string);
         if(file.isFile() && validatePath(file, string)) {
            return file;
         }
      } catch (IOException var3) {
         ;
      }

      return null;
   }

   public Set getNamespaces(PackType packType) {
      Set<String> set = Sets.newHashSet();
      File var3 = new File(this.file, packType.getDirectory());
      File[] vars4 = var3.listFiles(DirectoryFileFilter.DIRECTORY);
      if(vars4 != null) {
         for(File var8 : vars4) {
            String var9 = getRelativePath(var3, var8);
            if(var9.equals(var9.toLowerCase(Locale.ROOT))) {
               set.add(var9.substring(0, var9.length() - 1));
            } else {
               this.logWarning(var9);
            }
         }
      }

      return set;
   }

   public void close() throws IOException {
   }

   public Collection getResources(PackType packType, String string, int var3, Predicate predicate) {
      File var5 = new File(this.file, packType.getDirectory());
      List<ResourceLocation> var6 = Lists.newArrayList();

      for(String var8 : this.getNamespaces(packType)) {
         this.listResources(new File(new File(var5, var8), string), var3, var8, var6, string + "/", predicate);
      }

      return var6;
   }

   private void listResources(File file, int var2, String var3, List list, String var5, Predicate predicate) {
      File[] vars7 = file.listFiles();
      if(vars7 != null) {
         for(File var11 : vars7) {
            if(var11.isDirectory()) {
               if(var2 > 0) {
                  this.listResources(var11, var2 - 1, var3, list, var5 + var11.getName() + "/", predicate);
               }
            } else if(!var11.getName().endsWith(".mcmeta") && predicate.test(var11.getName())) {
               try {
                  list.add(new ResourceLocation(var3, var5 + var11.getName()));
               } catch (ResourceLocationException var13) {
                  LOGGER.error(var13.getMessage());
               }
            }
         }
      }

   }
}
