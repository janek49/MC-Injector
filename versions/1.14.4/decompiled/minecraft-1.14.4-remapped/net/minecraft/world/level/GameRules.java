package net.minecraft.world.level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRules {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map GAME_RULE_TYPES = Maps.newTreeMap(Comparator.comparing((gameRules$Key) -> {
      return gameRules$Key.id;
   }));
   public static final GameRules.Key RULE_DOFIRETICK = register("doFireTick", GameRules.BooleanValue.create(true));
   public static final GameRules.Key RULE_MOBGRIEFING = register("mobGriefing", GameRules.BooleanValue.create(true));
   public static final GameRules.Key RULE_KEEPINVENTORY = register("keepInventory", GameRules.BooleanValue.create(false));
   public static final GameRules.Key RULE_DOMOBSPAWNING = register("doMobSpawning", GameRules.BooleanValue.create(true));
   public static final GameRules.Key RULE_DOMOBLOOT = register("doMobLoot", GameRules.BooleanValue.create(true));
   public static final GameRules.Key RULE_DOBLOCKDROPS = register("doTileDrops", GameRules.BooleanValue.create(true));
   public static final GameRules.Key RULE_DOENTITYDROPS = register("doEntityDrops", GameRules.BooleanValue.create(true));
   public static final GameRules.Key RULE_COMMANDBLOCKOUTPUT = register("commandBlockOutput", GameRules.BooleanValue.create(true));
   public static final GameRules.Key RULE_NATURAL_REGENERATION = register("naturalRegeneration", GameRules.BooleanValue.create(true));
   public static final GameRules.Key RULE_DAYLIGHT = register("doDaylightCycle", GameRules.BooleanValue.create(true));
   public static final GameRules.Key RULE_LOGADMINCOMMANDS = register("logAdminCommands", GameRules.BooleanValue.create(true));
   public static final GameRules.Key RULE_SHOWDEATHMESSAGES = register("showDeathMessages", GameRules.BooleanValue.create(true));
   public static final GameRules.Key RULE_RANDOMTICKING = register("randomTickSpeed", GameRules.IntegerValue.create(3));
   public static final GameRules.Key RULE_SENDCOMMANDFEEDBACK = register("sendCommandFeedback", GameRules.BooleanValue.create(true));
   public static final GameRules.Key RULE_REDUCEDDEBUGINFO = register("reducedDebugInfo", GameRules.BooleanValue.create(false, (minecraftServer, gameRules$BooleanValue) -> {
      byte var2 = (byte)(gameRules$BooleanValue.get()?22:23);

      for(ServerPlayer var4 : minecraftServer.getPlayerList().getPlayers()) {
         var4.connection.send(new ClientboundEntityEventPacket(var4, var2));
      }

   }));
   public static final GameRules.Key RULE_SPECTATORSGENERATECHUNKS = register("spectatorsGenerateChunks", GameRules.BooleanValue.create(true));
   public static final GameRules.Key RULE_SPAWN_RADIUS = register("spawnRadius", GameRules.IntegerValue.create(10));
   public static final GameRules.Key RULE_DISABLE_ELYTRA_MOVEMENT_CHECK = register("disableElytraMovementCheck", GameRules.BooleanValue.create(false));
   public static final GameRules.Key RULE_MAX_ENTITY_CRAMMING = register("maxEntityCramming", GameRules.IntegerValue.create(24));
   public static final GameRules.Key RULE_WEATHER_CYCLE = register("doWeatherCycle", GameRules.BooleanValue.create(true));
   public static final GameRules.Key RULE_LIMITED_CRAFTING = register("doLimitedCrafting", GameRules.BooleanValue.create(false));
   public static final GameRules.Key RULE_MAX_COMMAND_CHAIN_LENGTH = register("maxCommandChainLength", GameRules.IntegerValue.create(65536));
   public static final GameRules.Key RULE_ANNOUNCE_ADVANCEMENTS = register("announceAdvancements", GameRules.BooleanValue.create(true));
   public static final GameRules.Key RULE_DISABLE_RAIDS = register("disableRaids", GameRules.BooleanValue.create(false));
   private final Map rules;

   private static GameRules.Key register(String string, GameRules.Type gameRules$Type) {
      GameRules.Key<T> gameRules$Key = new GameRules.Key(string);
      GameRules.Type<?> var3 = (GameRules.Type)GAME_RULE_TYPES.put(gameRules$Key, gameRules$Type);
      if(var3 != null) {
         throw new IllegalStateException("Duplicate game rule registration for " + string);
      } else {
         return gameRules$Key;
      }
   }

   public GameRules() {
      this.rules = (Map)GAME_RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (map$Entry) -> {
         return ((GameRules.Type)map$Entry.getValue()).createRule();
      }));
   }

   public GameRules.Value getRule(GameRules.Key gameRules$Key) {
      return (GameRules.Value)this.rules.get(gameRules$Key);
   }

   public CompoundTag createTag() {
      CompoundTag compoundTag = new CompoundTag();
      this.rules.forEach((gameRules$Key, gameRules$Value) -> {
         compoundTag.putString(gameRules$Key.id, gameRules$Value.serialize());
      });
      return compoundTag;
   }

   public void loadFromTag(CompoundTag compoundTag) {
      this.rules.forEach((gameRules$Key, gameRules$Value) -> {
         gameRules$Value.deserialize(compoundTag.getString(gameRules$Key.id));
      });
   }

   public static void visitGameRuleTypes(GameRules.GameRuleTypeVisitor gameRules$GameRuleTypeVisitor) {
      GAME_RULE_TYPES.forEach((gameRules$Key, gameRules$Type) -> {
         cap(gameRules$GameRuleTypeVisitor, gameRules$Key, gameRules$Type);
      });
   }

   private static void cap(GameRules.GameRuleTypeVisitor gameRules$GameRuleTypeVisitor, GameRules.Key gameRules$Key, GameRules.Type gameRules$Type) {
      gameRules$GameRuleTypeVisitor.visit(gameRules$Key, gameRules$Type);
   }

   public boolean getBoolean(GameRules.Key gameRules$Key) {
      return ((GameRules.BooleanValue)this.getRule(gameRules$Key)).get();
   }

   public int getInt(GameRules.Key gameRules$Key) {
      return ((GameRules.IntegerValue)this.getRule(gameRules$Key)).get();
   }

   public static class BooleanValue extends GameRules.Value {
      private boolean value;

      private static GameRules.Type create(boolean var0, BiConsumer biConsumer) {
         return new GameRules.Type(BoolArgumentType::bool, (gameRules$Type) -> {
            return new GameRules.BooleanValue(gameRules$Type, var0);
         }, biConsumer);
      }

      private static GameRules.Type create(boolean b) {
         return create(b, (minecraftServer, gameRules$BooleanValue) -> {
         });
      }

      public BooleanValue(GameRules.Type gameRules$Type, boolean value) {
         super(gameRules$Type);
         this.value = value;
      }

      protected void updateFromArgument(CommandContext commandContext, String string) {
         this.value = BoolArgumentType.getBool(commandContext, string);
      }

      public boolean get() {
         return this.value;
      }

      public void set(boolean value, @Nullable MinecraftServer minecraftServer) {
         this.value = value;
         this.onChanged(minecraftServer);
      }

      protected String serialize() {
         return Boolean.toString(this.value);
      }

      protected void deserialize(String string) {
         this.value = Boolean.parseBoolean(string);
      }

      public int getCommandResult() {
         return this.value?1:0;
      }

      protected GameRules.BooleanValue getSelf() {
         return this;
      }

      // $FF: synthetic method
      protected GameRules.Value getSelf() {
         return this.getSelf();
      }
   }

   @FunctionalInterface
   public interface GameRuleTypeVisitor {
      void visit(GameRules.Key var1, GameRules.Type var2);
   }

   public static class IntegerValue extends GameRules.Value {
      private int value;

      private static GameRules.Type create(int var0, BiConsumer biConsumer) {
         return new GameRules.Type(IntegerArgumentType::integer, (gameRules$Type) -> {
            return new GameRules.IntegerValue(gameRules$Type, var0);
         }, biConsumer);
      }

      private static GameRules.Type create(int i) {
         return create(i, (minecraftServer, gameRules$IntegerValue) -> {
         });
      }

      public IntegerValue(GameRules.Type gameRules$Type, int value) {
         super(gameRules$Type);
         this.value = value;
      }

      protected void updateFromArgument(CommandContext commandContext, String string) {
         this.value = IntegerArgumentType.getInteger(commandContext, string);
      }

      public int get() {
         return this.value;
      }

      protected String serialize() {
         return Integer.toString(this.value);
      }

      protected void deserialize(String string) {
         this.value = safeParse(string);
      }

      private static int safeParse(String string) {
         if(!string.isEmpty()) {
            try {
               return Integer.parseInt(string);
            } catch (NumberFormatException var2) {
               GameRules.LOGGER.warn("Failed to parse integer {}", string);
            }
         }

         return 0;
      }

      public int getCommandResult() {
         return this.value;
      }

      protected GameRules.IntegerValue getSelf() {
         return this;
      }

      // $FF: synthetic method
      protected GameRules.Value getSelf() {
         return this.getSelf();
      }
   }

   public static final class Key {
      private final String id;

      public Key(String id) {
         this.id = id;
      }

      public String toString() {
         return this.id;
      }

      public boolean equals(Object object) {
         return this == object?true:object instanceof GameRules.Key && ((GameRules.Key)object).id.equals(this.id);
      }

      public int hashCode() {
         return this.id.hashCode();
      }

      public String getId() {
         return this.id;
      }
   }

   public static class Type {
      private final Supplier argument;
      private final Function constructor;
      private final BiConsumer callback;

      private Type(Supplier argument, Function constructor, BiConsumer callback) {
         this.argument = argument;
         this.constructor = constructor;
         this.callback = callback;
      }

      public RequiredArgumentBuilder createArgument(String string) {
         return Commands.argument(string, (ArgumentType)this.argument.get());
      }

      public GameRules.Value createRule() {
         return (GameRules.Value)this.constructor.apply(this);
      }
   }

   public abstract static class Value {
      private final GameRules.Type type;

      public Value(GameRules.Type type) {
         this.type = type;
      }

      protected abstract void updateFromArgument(CommandContext var1, String var2);

      public void setFromArgument(CommandContext commandContext, String string) {
         this.updateFromArgument(commandContext, string);
         this.onChanged(((CommandSourceStack)commandContext.getSource()).getServer());
      }

      protected void onChanged(@Nullable MinecraftServer minecraftServer) {
         if(minecraftServer != null) {
            this.type.callback.accept(minecraftServer, this.getSelf());
         }

      }

      protected abstract void deserialize(String var1);

      protected abstract String serialize();

      public String toString() {
         return this.serialize();
      }

      public abstract int getCommandResult();

      protected abstract GameRules.Value getSelf();
   }
}
