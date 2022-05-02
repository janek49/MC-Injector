package net.minecraft.world.level;

import com.mojang.brigadier.ResultConsumer;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class BaseCommandBlock implements CommandSource {
   private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
   private long lastExecution = -1L;
   private boolean updateLastExecution = true;
   private int successCount;
   private boolean trackOutput = true;
   private Component lastOutput;
   private String command = "";
   private Component name = new TextComponent("@");

   public int getSuccessCount() {
      return this.successCount;
   }

   public void setSuccessCount(int successCount) {
      this.successCount = successCount;
   }

   public Component getLastOutput() {
      return (Component)(this.lastOutput == null?new TextComponent(""):this.lastOutput);
   }

   public CompoundTag save(CompoundTag compoundTag) {
      compoundTag.putString("Command", this.command);
      compoundTag.putInt("SuccessCount", this.successCount);
      compoundTag.putString("CustomName", Component.Serializer.toJson(this.name));
      compoundTag.putBoolean("TrackOutput", this.trackOutput);
      if(this.lastOutput != null && this.trackOutput) {
         compoundTag.putString("LastOutput", Component.Serializer.toJson(this.lastOutput));
      }

      compoundTag.putBoolean("UpdateLastExecution", this.updateLastExecution);
      if(this.updateLastExecution && this.lastExecution > 0L) {
         compoundTag.putLong("LastExecution", this.lastExecution);
      }

      return compoundTag;
   }

   public void load(CompoundTag compoundTag) {
      this.command = compoundTag.getString("Command");
      this.successCount = compoundTag.getInt("SuccessCount");
      if(compoundTag.contains("CustomName", 8)) {
         this.name = Component.Serializer.fromJson(compoundTag.getString("CustomName"));
      }

      if(compoundTag.contains("TrackOutput", 1)) {
         this.trackOutput = compoundTag.getBoolean("TrackOutput");
      }

      if(compoundTag.contains("LastOutput", 8) && this.trackOutput) {
         try {
            this.lastOutput = Component.Serializer.fromJson(compoundTag.getString("LastOutput"));
         } catch (Throwable var3) {
            this.lastOutput = new TextComponent(var3.getMessage());
         }
      } else {
         this.lastOutput = null;
      }

      if(compoundTag.contains("UpdateLastExecution")) {
         this.updateLastExecution = compoundTag.getBoolean("UpdateLastExecution");
      }

      if(this.updateLastExecution && compoundTag.contains("LastExecution")) {
         this.lastExecution = compoundTag.getLong("LastExecution");
      } else {
         this.lastExecution = -1L;
      }

   }

   public void setCommand(String command) {
      this.command = command;
      this.successCount = 0;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean performCommand(Level level) {
      if(!level.isClientSide && level.getGameTime() != this.lastExecution) {
         if("Searge".equalsIgnoreCase(this.command)) {
            this.lastOutput = new TextComponent("#itzlipofutzli");
            this.successCount = 1;
            return true;
         } else {
            this.successCount = 0;
            MinecraftServer var2 = this.getLevel().getServer();
            if(var2 != null && var2.isInitialized() && var2.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
               try {
                  this.lastOutput = null;
                  CommandSourceStack var3 = this.createCommandSourceStack().withCallback((commandContext, var2, var3) -> {
                     if(var2) {
                        ++this.successCount;
                     }

                  });
                  var2.getCommands().performCommand(var3, this.command);
               } catch (Throwable var6) {
                  CrashReport var4 = CrashReport.forThrowable(var6, "Executing command block");
                  CrashReportCategory var5 = var4.addCategory("Command to be executed");
                  var5.setDetail("Command", this::getCommand);
                  var5.setDetail("Name", () -> {
                     return this.getName().getString();
                  });
                  throw new ReportedException(var4);
               }
            }

            if(this.updateLastExecution) {
               this.lastExecution = level.getGameTime();
            } else {
               this.lastExecution = -1L;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public Component getName() {
      return this.name;
   }

   public void setName(Component name) {
      this.name = name;
   }

   public void sendMessage(Component component) {
      if(this.trackOutput) {
         this.lastOutput = (new TextComponent("[" + TIME_FORMAT.format(new Date()) + "] ")).append(component);
         this.onUpdated();
      }

   }

   public abstract ServerLevel getLevel();

   public abstract void onUpdated();

   public void setLastOutput(@Nullable Component lastOutput) {
      this.lastOutput = lastOutput;
   }

   public void setTrackOutput(boolean trackOutput) {
      this.trackOutput = trackOutput;
   }

   public boolean isTrackOutput() {
      return this.trackOutput;
   }

   public boolean usedBy(Player player) {
      if(!player.canUseGameMasterBlocks()) {
         return false;
      } else {
         if(player.getCommandSenderWorld().isClientSide) {
            player.openMinecartCommandBlock(this);
         }

         return true;
      }
   }

   public abstract Vec3 getPosition();

   public abstract CommandSourceStack createCommandSourceStack();

   public boolean acceptsSuccess() {
      return this.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK) && this.trackOutput;
   }

   public boolean acceptsFailure() {
      return this.trackOutput;
   }

   public boolean shouldInformAdmins() {
      return this.getLevel().getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
   }
}
