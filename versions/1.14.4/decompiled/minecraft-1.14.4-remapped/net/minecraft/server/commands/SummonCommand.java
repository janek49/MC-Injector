package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.phys.Vec3;

public class SummonCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.summon.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("summon").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(((RequiredArgumentBuilder)Commands.argument("entity", EntitySummonArgument.id()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((commandContext) -> {
         return spawnEntity((CommandSourceStack)commandContext.getSource(), EntitySummonArgument.getSummonableEntity(commandContext, "entity"), ((CommandSourceStack)commandContext.getSource()).getPosition(), new CompoundTag(), true);
      })).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes((commandContext) -> {
         return spawnEntity((CommandSourceStack)commandContext.getSource(), EntitySummonArgument.getSummonableEntity(commandContext, "entity"), Vec3Argument.getVec3(commandContext, "pos"), new CompoundTag(), true);
      })).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes((commandContext) -> {
         return spawnEntity((CommandSourceStack)commandContext.getSource(), EntitySummonArgument.getSummonableEntity(commandContext, "entity"), Vec3Argument.getVec3(commandContext, "pos"), CompoundTagArgument.getCompoundTag(commandContext, "nbt"), false);
      })))));
   }

   private static int spawnEntity(CommandSourceStack commandSourceStack, ResourceLocation resourceLocation, Vec3 vec3, CompoundTag compoundTag, boolean var4) throws CommandSyntaxException {
      CompoundTag compoundTag = compoundTag.copy();
      compoundTag.putString("id", resourceLocation.toString());
      if(EntityType.getKey(EntityType.LIGHTNING_BOLT).equals(resourceLocation)) {
         LightningBolt var6 = new LightningBolt(commandSourceStack.getLevel(), vec3.x, vec3.y, vec3.z, false);
         commandSourceStack.getLevel().addGlobalEntity(var6);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.summon.success", new Object[]{var6.getDisplayName()}), true);
         return 1;
      } else {
         ServerLevel var6 = commandSourceStack.getLevel();
         Entity var7 = EntityType.loadEntityRecursive(compoundTag, var6, (var2) -> {
            var2.moveTo(vec3.x, vec3.y, vec3.z, var2.yRot, var2.xRot);
            return !var6.addWithUUID(var2)?null:var2;
         });
         if(var7 == null) {
            throw ERROR_FAILED.create();
         } else {
            if(var4 && var7 instanceof Mob) {
               ((Mob)var7).finalizeSpawn(commandSourceStack.getLevel(), commandSourceStack.getLevel().getCurrentDifficultyAt(new BlockPos(var7)), MobSpawnType.COMMAND, (SpawnGroupData)null, (CompoundTag)null);
            }

            commandSourceStack.sendSuccess(new TranslatableComponent("commands.summon.success", new Object[]{var7.getDisplayName()}), true);
            return 1;
         }
      }
   }
}
