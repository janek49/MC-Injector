package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class TeleportCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      LiteralCommandNode<CommandSourceStack> var1 = commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("teleport").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("location", Vec3Argument.vec3()).executes((commandContext) -> {
         return teleportToPos((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates(commandContext, "location"), (Coordinates)null, (TeleportCommand.LookAt)null);
      })).then(Commands.argument("rotation", RotationArgument.rotation()).executes((commandContext) -> {
         return teleportToPos((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates(commandContext, "location"), RotationArgument.getRotation(commandContext, "rotation"), (TeleportCommand.LookAt)null);
      }))).then(((LiteralArgumentBuilder)Commands.literal("facing").then(Commands.literal("entity").then(((RequiredArgumentBuilder)Commands.argument("facingEntity", EntityArgument.entity()).executes((commandContext) -> {
         return teleportToPos((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates(commandContext, "location"), (Coordinates)null, new TeleportCommand.LookAt(EntityArgument.getEntity(commandContext, "facingEntity"), EntityAnchorArgument.Anchor.FEET));
      })).then(Commands.argument("facingAnchor", EntityAnchorArgument.anchor()).executes((commandContext) -> {
         return teleportToPos((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates(commandContext, "location"), (Coordinates)null, new TeleportCommand.LookAt(EntityArgument.getEntity(commandContext, "facingEntity"), EntityAnchorArgument.getAnchor(commandContext, "facingAnchor")));
      }))))).then(Commands.argument("facingLocation", Vec3Argument.vec3()).executes((commandContext) -> {
         return teleportToPos((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates(commandContext, "location"), (Coordinates)null, new TeleportCommand.LookAt(Vec3Argument.getVec3(commandContext, "facingLocation")));
      }))))).then(Commands.argument("destination", EntityArgument.entity()).executes((commandContext) -> {
         return teleportToEntity((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), EntityArgument.getEntity(commandContext, "destination"));
      })))).then(Commands.argument("location", Vec3Argument.vec3()).executes((commandContext) -> {
         return teleportToPos((CommandSourceStack)commandContext.getSource(), Collections.singleton(((CommandSourceStack)commandContext.getSource()).getEntityOrException()), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates(commandContext, "location"), WorldCoordinates.current(), (TeleportCommand.LookAt)null);
      }))).then(Commands.argument("destination", EntityArgument.entity()).executes((commandContext) -> {
         return teleportToEntity((CommandSourceStack)commandContext.getSource(), Collections.singleton(((CommandSourceStack)commandContext.getSource()).getEntityOrException()), EntityArgument.getEntity(commandContext, "destination"));
      })));
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tp").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).redirect(var1));
   }

   private static int teleportToEntity(CommandSourceStack commandSourceStack, Collection collection, Entity entity) {
      for(Entity var4 : collection) {
         performTeleport(commandSourceStack, var4, (ServerLevel)entity.level, entity.x, entity.y, entity.z, EnumSet.noneOf(ClientboundPlayerPositionPacket.RelativeArgument.class), entity.yRot, entity.xRot, (TeleportCommand.LookAt)null);
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.teleport.success.entity.single", new Object[]{((Entity)collection.iterator().next()).getDisplayName(), entity.getDisplayName()}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.teleport.success.entity.multiple", new Object[]{Integer.valueOf(collection.size()), entity.getDisplayName()}), true);
      }

      return collection.size();
   }

   private static int teleportToPos(CommandSourceStack commandSourceStack, Collection collection, ServerLevel serverLevel, Coordinates var3, @Nullable Coordinates var4, @Nullable TeleportCommand.LookAt teleportCommand$LookAt) throws CommandSyntaxException {
      Vec3 var6 = var3.getPosition(commandSourceStack);
      Vec2 var7 = var4 == null?null:var4.getRotation(commandSourceStack);
      Set<ClientboundPlayerPositionPacket.RelativeArgument> var8 = EnumSet.noneOf(ClientboundPlayerPositionPacket.RelativeArgument.class);
      if(var3.isXRelative()) {
         var8.add(ClientboundPlayerPositionPacket.RelativeArgument.X);
      }

      if(var3.isYRelative()) {
         var8.add(ClientboundPlayerPositionPacket.RelativeArgument.Y);
      }

      if(var3.isZRelative()) {
         var8.add(ClientboundPlayerPositionPacket.RelativeArgument.Z);
      }

      if(var4 == null) {
         var8.add(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT);
         var8.add(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT);
      } else {
         if(var4.isXRelative()) {
            var8.add(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT);
         }

         if(var4.isYRelative()) {
            var8.add(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT);
         }
      }

      for(Entity var10 : collection) {
         if(var4 == null) {
            performTeleport(commandSourceStack, var10, serverLevel, var6.x, var6.y, var6.z, var8, var10.yRot, var10.xRot, teleportCommand$LookAt);
         } else {
            performTeleport(commandSourceStack, var10, serverLevel, var6.x, var6.y, var6.z, var8, var7.y, var7.x, teleportCommand$LookAt);
         }
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.teleport.success.location.single", new Object[]{((Entity)collection.iterator().next()).getDisplayName(), Double.valueOf(var6.x), Double.valueOf(var6.y), Double.valueOf(var6.z)}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.teleport.success.location.multiple", new Object[]{Integer.valueOf(collection.size()), Double.valueOf(var6.x), Double.valueOf(var6.y), Double.valueOf(var6.z)}), true);
      }

      return collection.size();
   }

   private static void performTeleport(CommandSourceStack commandSourceStack, Entity entity, ServerLevel serverLevel, double var3, double var5, double var7, Set set, float var10, float var11, @Nullable TeleportCommand.LookAt teleportCommand$LookAt) {
      if(entity instanceof ServerPlayer) {
         ChunkPos var13 = new ChunkPos(new BlockPos(var3, var5, var7));
         serverLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, var13, 1, Integer.valueOf(entity.getId()));
         entity.stopRiding();
         if(((ServerPlayer)entity).isSleeping()) {
            ((ServerPlayer)entity).stopSleepInBed(true, true, false);
         }

         if(serverLevel == entity.level) {
            ((ServerPlayer)entity).connection.teleport(var3, var5, var7, var10, var11, set);
         } else {
            ((ServerPlayer)entity).teleportTo(serverLevel, var3, var5, var7, var10, var11);
         }

         entity.setYHeadRot(var10);
      } else {
         float var13 = Mth.wrapDegrees(var10);
         float var14 = Mth.wrapDegrees(var11);
         var14 = Mth.clamp(var14, -90.0F, 90.0F);
         if(serverLevel == entity.level) {
            entity.moveTo(var3, var5, var7, var13, var14);
            entity.setYHeadRot(var13);
         } else {
            entity.unRide();
            entity.dimension = serverLevel.dimension.getType();
            Entity var15 = entity;
            entity = entity.getType().create(serverLevel);
            if(entity == null) {
               return;
            }

            entity.restoreFrom(var15);
            entity.moveTo(var3, var5, var7, var13, var14);
            entity.setYHeadRot(var13);
            serverLevel.addFromAnotherDimension(entity);
            var15.removed = true;
         }
      }

      if(teleportCommand$LookAt != null) {
         teleportCommand$LookAt.perform(commandSourceStack, entity);
      }

      if(!(entity instanceof LivingEntity) || !((LivingEntity)entity).isFallFlying()) {
         entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
         entity.onGround = true;
      }

   }

   static class LookAt {
      private final Vec3 position;
      private final Entity entity;
      private final EntityAnchorArgument.Anchor anchor;

      public LookAt(Entity entity, EntityAnchorArgument.Anchor anchor) {
         this.entity = entity;
         this.anchor = anchor;
         this.position = anchor.apply(entity);
      }

      public LookAt(Vec3 position) {
         this.entity = null;
         this.position = position;
         this.anchor = null;
      }

      public void perform(CommandSourceStack commandSourceStack, Entity entity) {
         if(this.entity != null) {
            if(entity instanceof ServerPlayer) {
               ((ServerPlayer)entity).lookAt(commandSourceStack.getAnchor(), this.entity, this.anchor);
            } else {
               entity.lookAt(commandSourceStack.getAnchor(), this.position);
            }
         } else {
            entity.lookAt(commandSourceStack.getAnchor(), this.position);
         }

      }
   }
}
