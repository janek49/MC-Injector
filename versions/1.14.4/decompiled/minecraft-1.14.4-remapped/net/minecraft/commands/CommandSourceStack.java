package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandSourceStack implements SharedSuggestionProvider {
   public static final SimpleCommandExceptionType ERROR_NOT_PLAYER = new SimpleCommandExceptionType(new TranslatableComponent("permissions.requires.player", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_NOT_ENTITY = new SimpleCommandExceptionType(new TranslatableComponent("permissions.requires.entity", new Object[0]));
   private final CommandSource source;
   private final Vec3 worldPosition;
   private final ServerLevel level;
   private final int permissionLevel;
   private final String textName;
   private final Component displayName;
   private final MinecraftServer server;
   private final boolean silent;
   @Nullable
   private final Entity entity;
   private final ResultConsumer consumer;
   private final EntityAnchorArgument.Anchor anchor;
   private final Vec2 rotation;

   public CommandSourceStack(CommandSource commandSource, Vec3 vec3, Vec2 vec2, ServerLevel serverLevel, int var5, String string, Component component, MinecraftServer minecraftServer, @Nullable Entity entity) {
      this(commandSource, vec3, vec2, serverLevel, var5, string, component, minecraftServer, entity, false, (commandContext, var1, var2) -> {
      }, EntityAnchorArgument.Anchor.FEET);
   }

   protected CommandSourceStack(CommandSource source, Vec3 worldPosition, Vec2 rotation, ServerLevel level, int permissionLevel, String textName, Component displayName, MinecraftServer server, @Nullable Entity entity, boolean silent, ResultConsumer consumer, EntityAnchorArgument.Anchor anchor) {
      this.source = source;
      this.worldPosition = worldPosition;
      this.level = level;
      this.silent = silent;
      this.entity = entity;
      this.permissionLevel = permissionLevel;
      this.textName = textName;
      this.displayName = displayName;
      this.server = server;
      this.consumer = consumer;
      this.anchor = anchor;
      this.rotation = rotation;
   }

   public CommandSourceStack withEntity(Entity entity) {
      return this.entity == entity?this:new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, entity.getName().getString(), entity.getDisplayName(), this.server, entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withPosition(Vec3 vec3) {
      return this.worldPosition.equals(vec3)?this:new CommandSourceStack(this.source, vec3, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withRotation(Vec2 vec2) {
      return this.rotation.equals(vec2)?this:new CommandSourceStack(this.source, this.worldPosition, vec2, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withCallback(ResultConsumer resultConsumer) {
      return this.consumer.equals(resultConsumer)?this:new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, resultConsumer, this.anchor);
   }

   public CommandSourceStack withCallback(ResultConsumer resultConsumer, BinaryOperator binaryOperator) {
      ResultConsumer<CommandSourceStack> resultConsumer = (ResultConsumer)binaryOperator.apply(this.consumer, resultConsumer);
      return this.withCallback(resultConsumer);
   }

   public CommandSourceStack withSuppressedOutput() {
      return this.silent?this:new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, true, this.consumer, this.anchor);
   }

   public CommandSourceStack withPermission(int i) {
      return i == this.permissionLevel?this:new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, i, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withMaximumPermission(int i) {
      return i <= this.permissionLevel?this:new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, i, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withAnchor(EntityAnchorArgument.Anchor entityAnchorArgument$Anchor) {
      return entityAnchorArgument$Anchor == this.anchor?this:new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, entityAnchorArgument$Anchor);
   }

   public CommandSourceStack withLevel(ServerLevel serverLevel) {
      return serverLevel == this.level?this:new CommandSourceStack(this.source, this.worldPosition, this.rotation, serverLevel, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack facing(Entity entity, EntityAnchorArgument.Anchor entityAnchorArgument$Anchor) throws CommandSyntaxException {
      return this.facing(entityAnchorArgument$Anchor.apply(entity));
   }

   public CommandSourceStack facing(Vec3 vec3) throws CommandSyntaxException {
      Vec3 vec3 = this.anchor.apply(this);
      double var3 = vec3.x - vec3.x;
      double var5 = vec3.y - vec3.y;
      double var7 = vec3.z - vec3.z;
      double var9 = (double)Mth.sqrt(var3 * var3 + var7 * var7);
      float var11 = Mth.wrapDegrees((float)(-(Mth.atan2(var5, var9) * 57.2957763671875D)));
      float var12 = Mth.wrapDegrees((float)(Mth.atan2(var7, var3) * 57.2957763671875D) - 90.0F);
      return this.withRotation(new Vec2(var11, var12));
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public String getTextName() {
      return this.textName;
   }

   public boolean hasPermission(int i) {
      return this.permissionLevel >= i;
   }

   public Vec3 getPosition() {
      return this.worldPosition;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   public Entity getEntityOrException() throws CommandSyntaxException {
      if(this.entity == null) {
         throw ERROR_NOT_ENTITY.create();
      } else {
         return this.entity;
      }
   }

   public ServerPlayer getPlayerOrException() throws CommandSyntaxException {
      if(!(this.entity instanceof ServerPlayer)) {
         throw ERROR_NOT_PLAYER.create();
      } else {
         return (ServerPlayer)this.entity;
      }
   }

   public Vec2 getRotation() {
      return this.rotation;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public EntityAnchorArgument.Anchor getAnchor() {
      return this.anchor;
   }

   public void sendSuccess(Component component, boolean var2) {
      if(this.source.acceptsSuccess() && !this.silent) {
         this.source.sendMessage(component);
      }

      if(var2 && this.source.shouldInformAdmins() && !this.silent) {
         this.broadcastToAdmins(component);
      }

   }

   private void broadcastToAdmins(Component component) {
      Component component = (new TranslatableComponent("chat.type.admin", new Object[]{this.getDisplayName(), component})).withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC});
      if(this.server.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
         for(ServerPlayer var4 : this.server.getPlayerList().getPlayers()) {
            if(var4 != this.source && this.server.getPlayerList().isOp(var4.getGameProfile())) {
               var4.sendMessage(component);
            }
         }
      }

      if(this.source != this.server && this.server.getGameRules().getBoolean(GameRules.RULE_LOGADMINCOMMANDS)) {
         this.server.sendMessage(component);
      }

   }

   public void sendFailure(Component component) {
      if(this.source.acceptsFailure() && !this.silent) {
         this.source.sendMessage((new TextComponent("")).append(component).withStyle(ChatFormatting.RED));
      }

   }

   public void onCommandComplete(CommandContext commandContext, boolean var2, int var3) {
      if(this.consumer != null) {
         this.consumer.onCommandComplete(commandContext, var2, var3);
      }

   }

   public Collection getOnlinePlayerNames() {
      return Lists.newArrayList(this.server.getPlayerNames());
   }

   public Collection getAllTeams() {
      return this.server.getScoreboard().getTeamNames();
   }

   public Collection getAvailableSoundEvents() {
      return Registry.SOUND_EVENT.keySet();
   }

   public Stream getRecipeNames() {
      return this.server.getRecipeManager().getRecipeIds();
   }

   public CompletableFuture customSuggestion(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      return null;
   }
}
