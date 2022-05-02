package net.minecraft.data;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;
import net.minecraft.data.HashCache;

public interface DataProvider {
   HashFunction SHA1 = Hashing.sha1();

   void run(HashCache var1) throws IOException;

   String getName();

   static default void save(Gson gson, HashCache hashCache, JsonElement jsonElement, Path path) throws IOException {
      String var4 = gson.toJson(jsonElement);
      String var5 = SHA1.hashUnencodedChars(var4).toString();
      if(!Objects.equals(hashCache.getHash(path), var5) || !Files.exists(path, new LinkOption[0])) {
         Files.createDirectories(path.getParent(), new FileAttribute[0]);
         BufferedWriter var6 = Files.newBufferedWriter(path, new OpenOption[0]);
         Throwable var7 = null;

         try {
            var6.write(var4);
         } catch (Throwable var16) {
            var7 = var16;
            throw var16;
         } finally {
            if(var6 != null) {
               if(var7 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var15) {
                     var7.addSuppressed(var15);
                  }
               } else {
                  var6.close();
               }
            }

         }
      }

      hashCache.putNew(path, var5);
   }
}
