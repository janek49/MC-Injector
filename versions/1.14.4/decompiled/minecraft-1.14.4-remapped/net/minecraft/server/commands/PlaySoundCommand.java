package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class PlaySoundCommand {
   private static final SimpleCommandExceptionType ERROR_TOO_FAR = new SimpleCommandExceptionType(new TranslatableComponent("commands.playsound.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> var1 = Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS);

      for(SoundSource var5 : SoundSource.values()) {
         var1.then(source(var5));
      }

      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("playsound").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(var1));
   }

   private static LiteralArgumentBuilder source(SoundSource soundSource) {
      return (LiteralArgumentBuilder)Commands.literal(soundSource.getName()).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((commandContext) -> {
         return playSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), ResourceLocationArgument.getId(commandContext, "sound"), soundSource, ((CommandSourceStack)commandContext.getSource()).getPosition(), 1.0F, 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes((commandContext) -> {
         return playSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), ResourceLocationArgument.getId(commandContext, "sound"), soundSource, Vec3Argument.getVec3(commandContext, "pos"), 1.0F, 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)Commands.argument("volume", FloatArgumentType.floatArg(0.0F)).executes((commandContext) -> {
         return playSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), ResourceLocationArgument.getId(commandContext, "sound"), soundSource, Vec3Argument.getVec3(commandContext, "pos"), ((Float)commandContext.getArgument("volume", Float.class)).floatValue(), 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)Commands.argument("pitch", FloatArgumentType.floatArg(0.0F, 2.0F)).executes((commandContext) -> {
         return playSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), ResourceLocationArgument.getId(commandContext, "sound"), soundSource, Vec3Argument.getVec3(commandContext, "pos"), ((Float)commandContext.getArgument("volume", Float.class)).floatValue(), ((Float)commandContext.getArgument("pitch", Float.class)).floatValue(), 0.0F);
      })).then(Commands.argument("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F)).executes((commandContext) -> {
         return playSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), ResourceLocationArgument.getId(commandContext, "sound"), soundSource, Vec3Argument.getVec3(commandContext, "pos"), ((Float)commandContext.getArgument("volume", Float.class)).floatValue(), ((Float)commandContext.getArgument("pitch", Float.class)).floatValue(), ((Float)commandContext.getArgument("minVolume", Float.class)).floatValue());
      }))))));
   }

   private static int playSound(CommandSourceStack commandSourceStack, Collection collection, ResourceLocation resourceLocation, SoundSource soundSource, Vec3 vec3, float var5, float var6, float var7) throws CommandSyntaxException {
      double var8 = Math.pow(var5 > 1.0F?(double)(var5 * 16.0F):16.0D, 2.0D);
      int var10 = 0;
      Iterator var11 = collection.iterator();

      while(true) {
         ServerPlayer var12;
         Vec3 var21;
         float var22;
         while(true) {
            if(!var11.hasNext()) {
               if(var10 == 0) {
                  throw ERROR_TOO_FAR.create();
               }

               if(collection.size() == 1) {
                  commandSourceStack.sendSuccess(new TranslatableComponent("commands.playsound.success.single", new Object[]{resourceLocation, ((ServerPlayer)collection.iterator().next()).getDisplayName()}), true);
               } else {
                  commandSourceStack.sendSuccess(new TranslatableComponent("commands.playsound.success.single", new Object[]{resourceLocation, ((ServerPlayer)collection.iterator().next()).getDisplayName()}), true);
               }

               return var10;
            }

            var12 = (ServerPlayer)var11.next();
            double var13 = vec3.x - var12.x;
            double var15 = vec3.y - var12.y;
            double var17 = vec3.z - var12.z;
            double var19 = var13 * var13 + var15 * var15 + var17 * var17;
            var21 = vec3;
            var22 = var5;
            if(var19 <= var8) {
               break;
            }

            if(var7 > 0.0F) {
               double var23 = (double)Mth.sqrt(var19);
               var21 = new Vec3(var12.x + var13 / var23 * 2.0D, var12.y + var15 / var23 * 2.0D, var12.z + var17 / var23 * 2.0D);
               var22 = var7;
               break;
            }
         }

         var12.connection.send(new ClientboundCustomSoundPacket(resourceLocation, soundSource, var21, var22, var6));
         ++var10;
      }
   }
}
