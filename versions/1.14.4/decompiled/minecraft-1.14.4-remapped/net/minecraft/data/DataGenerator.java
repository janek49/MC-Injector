package net.minecraft.data;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.server.Bootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataGenerator {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Collection inputFolders;
   private final Path outputFolder;
   private final List providers = Lists.newArrayList();

   public DataGenerator(Path outputFolder, Collection inputFolders) {
      this.outputFolder = outputFolder;
      this.inputFolders = inputFolders;
   }

   public Collection getInputFolders() {
      return this.inputFolders;
   }

   public Path getOutputFolder() {
      return this.outputFolder;
   }

   public void run() throws IOException {
      HashCache var1 = new HashCache(this.outputFolder, "cache");
      var1.keep(this.getOutputFolder().resolve("version.json"));
      Stopwatch var2 = Stopwatch.createStarted();
      Stopwatch var3 = Stopwatch.createUnstarted();

      for(DataProvider var5 : this.providers) {
         LOGGER.info("Starting provider: {}", var5.getName());
         var3.start();
         var5.run(var1);
         var3.stop();
         LOGGER.info("{} finished after {} ms", var5.getName(), Long.valueOf(var3.elapsed(TimeUnit.MILLISECONDS)));
         var3.reset();
      }

      LOGGER.info("All providers took: {} ms", Long.valueOf(var2.elapsed(TimeUnit.MILLISECONDS)));
      var1.purgeStaleAndWrite();
   }

   public void addProvider(DataProvider dataProvider) {
      this.providers.add(dataProvider);
   }

   static {
      Bootstrap.bootStrap();
   }
}
