package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class EntitySummonArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"minecraft:pig", "cow"});
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_ENTITY = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("entity.notFound", new Object[]{object});
   });

   public static EntitySummonArgument id() {
      return new EntitySummonArgument();
   }

   public static ResourceLocation getSummonableEntity(CommandContext commandContext, String string) throws CommandSyntaxException {
      return verifyCanSummon((ResourceLocation)commandContext.getArgument(string, ResourceLocation.class));
   }

   private static ResourceLocation verifyCanSummon(ResourceLocation resourceLocation) throws CommandSyntaxException {
      Registry.ENTITY_TYPE.getOptional(resourceLocation).filter(EntityType::canSummon).orElseThrow(() -> {
         return ERROR_UNKNOWN_ENTITY.create(resourceLocation);
      });
      return resourceLocation;
   }

   public ResourceLocation parse(StringReader stringReader) throws CommandSyntaxException {
      return verifyCanSummon(ResourceLocation.read(stringReader));
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
