package net.minecraft;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.SharedConstants;

public class FileUtil {
   private static final Pattern COPY_COUNTER_PATTERN = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
   private static final Pattern RESERVED_WINDOWS_FILENAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);

   public static String findAvailableName(Path path, String var1, String var2) throws IOException {
      for(char var6 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
         var1 = var1.replace(var6, '_');
      }

      var1 = var1.replaceAll("[./\"]", "_");
      if(RESERVED_WINDOWS_FILENAMES.matcher(var1).matches()) {
         var1 = "_" + var1 + "_";
      }

      Matcher var3 = COPY_COUNTER_PATTERN.matcher(var1);
      int var4 = 0;
      if(var3.matches()) {
         var1 = var3.group("name");
         var4 = Integer.parseInt(var3.group("count"));
      }

      if(var1.length() > 255 - var2.length()) {
         var1 = var1.substring(0, 255 - var2.length());
      }

      while(true) {
         String var5 = var1;
         if(var4 != 0) {
            String var6 = " (" + var4 + ")";
            int var7 = 255 - var6.length();
            if(var1.length() > var7) {
               var5 = var1.substring(0, var7);
            }

            var5 = var5 + var6;
         }

         var5 = var5 + var2;
         Path var6 = path.resolve(var5);

         try {
            Path var7 = Files.createDirectory(var6, new FileAttribute[0]);
            Files.deleteIfExists(var7);
            return path.relativize(var7).toString();
         } catch (FileAlreadyExistsException var8) {
            ++var4;
         }
      }
   }

   public static boolean isPathNormalized(Path path) {
      Path path = path.normalize();
      return path.equals(path);
   }

   public static boolean isPathPortable(Path path) {
      for(Path var2 : path) {
         if(RESERVED_WINDOWS_FILENAMES.matcher(var2.toString()).matches()) {
            return false;
         }
      }

      return true;
   }

   public static Path createPathToResource(Path var0, String var1, String var2) {
      String var3 = var1 + var2;
      Path var4 = Paths.get(var3, new String[0]);
      if(var4.endsWith(var2)) {
         throw new InvalidPathException(var3, "empty resource name");
      } else {
         return var0.resolve(var4);
      }
   }
}
