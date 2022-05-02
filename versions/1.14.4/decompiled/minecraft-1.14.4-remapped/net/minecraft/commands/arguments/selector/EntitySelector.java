package net.minecraft.commands.arguments.selector;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntitySelector {
   private final int maxResults;
   private final boolean includesEntities;
   private final boolean worldLimited;
   private final Predicate predicate;
   private final MinMaxBounds.Floats range;
   private final Function position;
   @Nullable
   private final AABB aabb;
   private final BiConsumer order;
   private final boolean currentEntity;
   @Nullable
   private final String playerName;
   @Nullable
   private final UUID entityUUID;
   @Nullable
   private final EntityType type;
   private final boolean usesSelector;

   public EntitySelector(int maxResults, boolean includesEntities, boolean worldLimited, Predicate predicate, MinMaxBounds.Floats range, Function position, @Nullable AABB aabb, BiConsumer order, boolean currentEntity, @Nullable String playerName, @Nullable UUID entityUUID, @Nullable EntityType type, boolean usesSelector) {
      this.maxResults = maxResults;
      this.includesEntities = includesEntities;
      this.worldLimited = worldLimited;
      this.predicate = predicate;
      this.range = range;
      this.position = position;
      this.aabb = aabb;
      this.order = order;
      this.currentEntity = currentEntity;
      this.playerName = playerName;
      this.entityUUID = entityUUID;
      this.type = type;
      this.usesSelector = usesSelector;
   }

   public int getMaxResults() {
      return this.maxResults;
   }

   public boolean includesEntities() {
      return this.includesEntities;
   }

   public boolean isSelfSelector() {
      return this.currentEntity;
   }

   public boolean isWorldLimited() {
      return this.worldLimited;
   }

   private void checkPermissions(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
      if(this.usesSelector && !commandSourceStack.hasPermission(2)) {
         throw EntityArgument.ERROR_SELECTORS_NOT_ALLOWED.create();
      }
   }

   public Entity findSingleEntity(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
      this.checkPermissions(commandSourceStack);
      List<? extends Entity> var2 = this.findEntities(commandSourceStack);
      if(var2.isEmpty()) {
         throw EntityArgument.NO_ENTITIES_FOUND.create();
      } else if(var2.size() > 1) {
         throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
      } else {
         return (Entity)var2.get(0);
      }
   }

   public List findEntities(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
      this.checkPermissions(commandSourceStack);
      if(!this.includesEntities) {
         return this.findPlayers(commandSourceStack);
      } else if(this.playerName != null) {
         ServerPlayer var2 = commandSourceStack.getServer().getPlayerList().getPlayerByName(this.playerName);
         return (List)(var2 == null?Collections.emptyList():Lists.newArrayList(new ServerPlayer[]{var2}));
      } else if(this.entityUUID != null) {
         for(ServerLevel var3 : commandSourceStack.getServer().getAllLevels()) {
            Entity var4 = var3.getEntity(this.entityUUID);
            if(var4 != null) {
               return Lists.newArrayList(new Entity[]{var4});
            }
         }

         return Collections.emptyList();
      } else {
         Vec3 var2 = (Vec3)this.position.apply(commandSourceStack.getPosition());
         Predicate<Entity> var3 = this.getPredicate(var2);
         if(this.currentEntity) {
            return (List)(commandSourceStack.getEntity() != null && var3.test(commandSourceStack.getEntity())?Lists.newArrayList(new Entity[]{commandSourceStack.getEntity()}):Collections.emptyList());
         } else {
            List<Entity> var4 = Lists.newArrayList();
            if(this.isWorldLimited()) {
               this.addEntities(var4, commandSourceStack.getLevel(), var2, var3);
            } else {
               for(ServerLevel var6 : commandSourceStack.getServer().getAllLevels()) {
                  this.addEntities(var4, var6, var2, var3);
               }
            }

            return this.sortAndLimit(var2, var4);
         }
      }
   }

   private void addEntities(List list, ServerLevel serverLevel, Vec3 vec3, Predicate predicate) {
      if(this.aabb != null) {
         list.addAll(serverLevel.getEntities(this.type, this.aabb.move(vec3), predicate));
      } else {
         list.addAll(serverLevel.getEntities(this.type, predicate));
      }

   }

   public ServerPlayer findSinglePlayer(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
      this.checkPermissions(commandSourceStack);
      List<ServerPlayer> var2 = this.findPlayers(commandSourceStack);
      if(var2.size() != 1) {
         throw EntityArgument.NO_PLAYERS_FOUND.create();
      } else {
         return (ServerPlayer)var2.get(0);
      }
   }

   public List findPlayers(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
      this.checkPermissions(commandSourceStack);
      if(this.playerName != null) {
         ServerPlayer var2 = commandSourceStack.getServer().getPlayerList().getPlayerByName(this.playerName);
         return (List)(var2 == null?Collections.emptyList():Lists.newArrayList(new ServerPlayer[]{var2}));
      } else if(this.entityUUID != null) {
         ServerPlayer var2 = commandSourceStack.getServer().getPlayerList().getPlayer(this.entityUUID);
         return (List)(var2 == null?Collections.emptyList():Lists.newArrayList(new ServerPlayer[]{var2}));
      } else {
         Vec3 var2 = (Vec3)this.position.apply(commandSourceStack.getPosition());
         Predicate<Entity> var3 = this.getPredicate(var2);
         if(this.currentEntity) {
            if(commandSourceStack.getEntity() instanceof ServerPlayer) {
               ServerPlayer var4 = (ServerPlayer)commandSourceStack.getEntity();
               if(var3.test(var4)) {
                  return Lists.newArrayList(new ServerPlayer[]{var4});
               }
            }

            return Collections.emptyList();
         } else {
            List<ServerPlayer> var4;
            if(this.isWorldLimited()) {
               ServerLevel var10000 = commandSourceStack.getLevel();
               var3.getClass();
               var4 = var10000.getPlayers(var3::test);
            } else {
               var4 = Lists.newArrayList();

               for(ServerPlayer var6 : commandSourceStack.getServer().getPlayerList().getPlayers()) {
                  if(var3.test(var6)) {
                     var4.add(var6);
                  }
               }
            }

            return this.sortAndLimit(var2, var4);
         }
      }
   }

   private Predicate getPredicate(Vec3 vec3) {
      Predicate<Entity> predicate = this.predicate;
      if(this.aabb != null) {
         AABB var3 = this.aabb.move(vec3);
         predicate = predicate.and((entity) -> {
            return var3.intersects(entity.getBoundingBox());
         });
      }

      if(!this.range.isAny()) {
         predicate = predicate.and((entity) -> {
            return this.range.matchesSqr(entity.distanceToSqr(vec3));
         });
      }

      return predicate;
   }

   private List sortAndLimit(Vec3 vec3, List var2) {
      if(var2.size() > 1) {
         this.order.accept(vec3, var2);
      }

      return var2.subList(0, Math.min(this.maxResults, var2.size()));
   }

   public static Component joinNames(List list) {
      return ComponentUtils.formatList(list, Entity::getDisplayName);
   }
}
