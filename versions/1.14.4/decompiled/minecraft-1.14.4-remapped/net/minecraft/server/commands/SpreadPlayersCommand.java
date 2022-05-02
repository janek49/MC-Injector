package net.minecraft.server.commands;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType.Function;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.scores.Team;

public class SpreadPlayersCommand {
   private static final Dynamic4CommandExceptionType ERROR_FAILED_TO_SPREAD_TEAMS = new Dynamic4CommandExceptionType((var0, var1, var2, var3) -> {
      return new TranslatableComponent("commands.spreadplayers.failed.teams", new Object[]{var0, var1, var2, var3});
   });
   private static final Dynamic4CommandExceptionType ERROR_FAILED_TO_SPREAD_ENTITIES = new Dynamic4CommandExceptionType((var0, var1, var2, var3) -> {
      return new TranslatableComponent("commands.spreadplayers.failed.entities", new Object[]{var0, var1, var2, var3});
   });

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spreadplayers").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.argument("center", Vec2Argument.vec2()).then(Commands.argument("spreadDistance", FloatArgumentType.floatArg(0.0F)).then(Commands.argument("maxRange", FloatArgumentType.floatArg(1.0F)).then(Commands.argument("respectTeams", BoolArgumentType.bool()).then(Commands.argument("targets", EntityArgument.entities()).executes((commandContext) -> {
         return spreadPlayers((CommandSourceStack)commandContext.getSource(), Vec2Argument.getVec2(commandContext, "center"), FloatArgumentType.getFloat(commandContext, "spreadDistance"), FloatArgumentType.getFloat(commandContext, "maxRange"), BoolArgumentType.getBool(commandContext, "respectTeams"), EntityArgument.getEntities(commandContext, "targets"));
      })))))));
   }

   private static int spreadPlayers(CommandSourceStack commandSourceStack, Vec2 vec2, float var2, float var3, boolean var4, Collection collection) throws CommandSyntaxException {
      Random var6 = new Random();
      double var7 = (double)(vec2.x - var3);
      double var9 = (double)(vec2.y - var3);
      double var11 = (double)(vec2.x + var3);
      double var13 = (double)(vec2.y + var3);
      SpreadPlayersCommand.Position[] vars15 = createInitialPositions(var6, var4?getNumberOfTeams(collection):collection.size(), var7, var9, var11, var13);
      spreadPositions(vec2, (double)var2, commandSourceStack.getLevel(), var6, var7, var9, var11, var13, vars15, var4);
      double var16 = setPlayerPositions(collection, commandSourceStack.getLevel(), vars15, var4);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.spreadplayers.success." + (var4?"teams":"entities"), new Object[]{Integer.valueOf(vars15.length), Float.valueOf(vec2.x), Float.valueOf(vec2.y), String.format(Locale.ROOT, "%.2f", new Object[]{Double.valueOf(var16)})}), true);
      return vars15.length;
   }

   private static int getNumberOfTeams(Collection collection) {
      Set<Team> var1 = Sets.newHashSet();

      for(Entity var3 : collection) {
         if(var3 instanceof Player) {
            var1.add(var3.getTeam());
         } else {
            var1.add((Object)null);
         }
      }

      return var1.size();
   }

   private static void spreadPositions(Vec2 vec2, double var1, ServerLevel serverLevel, Random random, double var5, double var7, double var9, double var11, SpreadPlayersCommand.Position[] spreadPlayersCommand$Positions, boolean var14) throws CommandSyntaxException {
      boolean var15 = true;
      double var17 = 3.4028234663852886E38D;

      int var16;
      for(var16 = 0; var16 < 10000 && var15; ++var16) {
         var15 = false;
         var17 = 3.4028234663852886E38D;

         for(int var19 = 0; var19 < spreadPlayersCommand$Positions.length; ++var19) {
            SpreadPlayersCommand.Position var20 = spreadPlayersCommand$Positions[var19];
            int var21 = 0;
            SpreadPlayersCommand.Position var22 = new SpreadPlayersCommand.Position();

            for(int var23 = 0; var23 < spreadPlayersCommand$Positions.length; ++var23) {
               if(var19 != var23) {
                  SpreadPlayersCommand.Position var24 = spreadPlayersCommand$Positions[var23];
                  double var25 = var20.dist(var24);
                  var17 = Math.min(var25, var17);
                  if(var25 < var1) {
                     ++var21;
                     var22.x = var22.x + (var24.x - var20.x);
                     var22.z = var22.z + (var24.z - var20.z);
                  }
               }
            }

            if(var21 > 0) {
               var22.x = var22.x / (double)var21;
               var22.z = var22.z / (double)var21;
               double var23 = (double)var22.getLength();
               if(var23 > 0.0D) {
                  var22.normalize();
                  var20.moveAway(var22);
               } else {
                  var20.randomize(random, var5, var7, var9, var11);
               }

               var15 = true;
            }

            if(var20.clamp(var5, var7, var9, var11)) {
               var15 = true;
            }
         }

         if(!var15) {
            for(SpreadPlayersCommand.Position var22 : spreadPlayersCommand$Positions) {
               if(!var22.isSafe(serverLevel)) {
                  var22.randomize(random, var5, var7, var9, var11);
                  var15 = true;
               }
            }
         }
      }

      if(var17 == 3.4028234663852886E38D) {
         var17 = 0.0D;
      }

      if(var16 >= 10000) {
         if(var14) {
            throw ERROR_FAILED_TO_SPREAD_TEAMS.create(Integer.valueOf(spreadPlayersCommand$Positions.length), Float.valueOf(vec2.x), Float.valueOf(vec2.y), String.format(Locale.ROOT, "%.2f", new Object[]{Double.valueOf(var17)}));
         } else {
            throw ERROR_FAILED_TO_SPREAD_ENTITIES.create(Integer.valueOf(spreadPlayersCommand$Positions.length), Float.valueOf(vec2.x), Float.valueOf(vec2.y), String.format(Locale.ROOT, "%.2f", new Object[]{Double.valueOf(var17)}));
         }
      }
   }

   private static double setPlayerPositions(Collection collection, ServerLevel serverLevel, SpreadPlayersCommand.Position[] spreadPlayersCommand$Positions, boolean var3) {
      double var4 = 0.0D;
      int var6 = 0;
      Map<Team, SpreadPlayersCommand.Position> var7 = Maps.newHashMap();

      for(Entity var9 : collection) {
         SpreadPlayersCommand.Position var10;
         if(var3) {
            Team var11 = var9 instanceof Player?var9.getTeam():null;
            if(!var7.containsKey(var11)) {
               var7.put(var11, spreadPlayersCommand$Positions[var6++]);
            }

            var10 = (SpreadPlayersCommand.Position)var7.get(var11);
         } else {
            var10 = spreadPlayersCommand$Positions[var6++];
         }

         var9.teleportToWithTicket((double)((float)Mth.floor(var10.x) + 0.5F), (double)var10.getSpawnY(serverLevel), (double)Mth.floor(var10.z) + 0.5D);
         double var11 = Double.MAX_VALUE;

         for(SpreadPlayersCommand.Position var16 : spreadPlayersCommand$Positions) {
            if(var10 != var16) {
               double var17 = var10.dist(var16);
               var11 = Math.min(var17, var11);
            }
         }

         var4 += var11;
      }

      if(collection.size() < 2) {
         return 0.0D;
      } else {
         var4 = var4 / (double)collection.size();
         return var4;
      }
   }

   private static SpreadPlayersCommand.Position[] createInitialPositions(Random random, int var1, double var2, double var4, double var6, double var8) {
      SpreadPlayersCommand.Position[] spreadPlayersCommand$Positions = new SpreadPlayersCommand.Position[var1];

      for(int var11 = 0; var11 < spreadPlayersCommand$Positions.length; ++var11) {
         SpreadPlayersCommand.Position var12 = new SpreadPlayersCommand.Position();
         var12.randomize(random, var2, var4, var6, var8);
         spreadPlayersCommand$Positions[var11] = var12;
      }

      return spreadPlayersCommand$Positions;
   }

   static class Position {
      private double x;
      private double z;

      double dist(SpreadPlayersCommand.Position spreadPlayersCommand$Position) {
         double var2 = this.x - spreadPlayersCommand$Position.x;
         double var4 = this.z - spreadPlayersCommand$Position.z;
         return Math.sqrt(var2 * var2 + var4 * var4);
      }

      void normalize() {
         double var1 = (double)this.getLength();
         this.x /= var1;
         this.z /= var1;
      }

      float getLength() {
         return Mth.sqrt(this.x * this.x + this.z * this.z);
      }

      public void moveAway(SpreadPlayersCommand.Position spreadPlayersCommand$Position) {
         this.x -= spreadPlayersCommand$Position.x;
         this.z -= spreadPlayersCommand$Position.z;
      }

      public boolean clamp(double x, double z, double x, double z) {
         boolean var9 = false;
         if(this.x < x) {
            this.x = x;
            var9 = true;
         } else if(this.x > x) {
            this.x = x;
            var9 = true;
         }

         if(this.z < z) {
            this.z = z;
            var9 = true;
         } else if(this.z > z) {
            this.z = z;
            var9 = true;
         }

         return var9;
      }

      public int getSpawnY(BlockGetter blockGetter) {
         BlockPos var2 = new BlockPos(this.x, 256.0D, this.z);

         while(var2.getY() > 0) {
            var2 = var2.below();
            if(!blockGetter.getBlockState(var2).isAir()) {
               return var2.getY() + 1;
            }
         }

         return 257;
      }

      public boolean isSafe(BlockGetter blockGetter) {
         BlockPos var2 = new BlockPos(this.x, 256.0D, this.z);

         while(var2.getY() > 0) {
            var2 = var2.below();
            BlockState var3 = blockGetter.getBlockState(var2);
            if(!var3.isAir()) {
               Material var4 = var3.getMaterial();
               return !var4.isLiquid() && var4 != Material.FIRE;
            }
         }

         return false;
      }

      public void randomize(Random random, double var2, double var4, double var6, double var8) {
         this.x = Mth.nextDouble(random, var2, var6);
         this.z = Mth.nextDouble(random, var4, var8);
      }
   }
}
