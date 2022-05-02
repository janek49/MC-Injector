package net.minecraft.server.commands;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.GameProfiler;
import net.minecraft.util.profiling.ProfileResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugCommand {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(new TranslatableComponent("commands.debug.notRunning", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(new TranslatableComponent("commands.debug.alreadyRunning", new Object[0]));
   @Nullable
   private static final FileSystemProvider ZIP_FS_PROVIDER = (FileSystemProvider)FileSystemProvider.installedProviders().stream().filter((fileSystemProvider) -> {
      return fileSystemProvider.getScheme().equalsIgnoreCase("jar");
   }).findFirst().orElse((Object)null);

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("debug").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(3);
      })).then(Commands.literal("start").executes((commandContext) -> {
         return start((CommandSourceStack)commandContext.getSource());
      }))).then(Commands.literal("stop").executes((commandContext) -> {
         return stop((CommandSourceStack)commandContext.getSource());
      }))).then(Commands.literal("report").executes((commandContext) -> {
         return report((CommandSourceStack)commandContext.getSource());
      })));
   }

   private static int start(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
      MinecraftServer var1 = commandSourceStack.getServer();
      GameProfiler var2 = var1.getProfiler();
      if(var2.continuous().isEnabled()) {
         throw ERROR_ALREADY_RUNNING.create();
      } else {
         var1.delayStartProfiler();
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.debug.started", new Object[]{"Started the debug profiler. Type \'/debug stop\' to stop it."}), true);
         return 0;
      }
   }

   private static int stop(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
      MinecraftServer var1 = commandSourceStack.getServer();
      GameProfiler var2 = var1.getProfiler();
      if(!var2.continuous().isEnabled()) {
         throw ERROR_NOT_RUNNING.create();
      } else {
         ProfileResults var3 = var2.continuous().disable();
         File var4 = new File(var1.getFile("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
         var3.saveResults(var4);
         float var5 = (float)var3.getNanoDuration() / 1.0E9F;
         float var6 = (float)var3.getTickDuration() / var5;
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.debug.stopped", new Object[]{String.format(Locale.ROOT, "%.2f", new Object[]{Float.valueOf(var5)}), Integer.valueOf(var3.getTickDuration()), String.format("%.2f", new Object[]{Float.valueOf(var6)})}), true);
         return Mth.floor(var6);
      }
   }

   private static int report(CommandSourceStack commandSourceStack) {
      MinecraftServer var1 = commandSourceStack.getServer();
      String var2 = "debug-report-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date());

      try {
         Path var4 = var1.getFile("debug").toPath();
         Files.createDirectories(var4, new FileAttribute[0]);
         if(!SharedConstants.IS_RUNNING_IN_IDE && ZIP_FS_PROVIDER != null) {
            Path var3 = var4.resolve(var2 + ".zip");
            FileSystem var5 = ZIP_FS_PROVIDER.newFileSystem(var3, ImmutableMap.of("create", "true"));
            Throwable var6 = null;

            try {
               var1.saveDebugReport(var5.getPath("/", new String[0]));
            } catch (Throwable var16) {
               var6 = var16;
               throw var16;
            } finally {
               if(var5 != null) {
                  if(var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } else {
            Path var3 = var4.resolve(var2);
            var1.saveDebugReport(var3);
         }

         commandSourceStack.sendSuccess(new TranslatableComponent("commands.debug.reportSaved", new Object[]{var2}), false);
         return 1;
      } catch (IOException var18) {
         LOGGER.error("Failed to save debug dump", var18);
         commandSourceStack.sendFailure(new TranslatableComponent("commands.debug.reportFailed", new Object[0]));
         return 0;
      }
   }
}
