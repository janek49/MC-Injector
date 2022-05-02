package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

public class ResourceLocationArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"foo", "foo:bar", "012"});
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_ID = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.id.unknown", new Object[]{object});
   });
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_ADVANCEMENT = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("advancement.advancementNotFound", new Object[]{object});
   });
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("recipe.notFound", new Object[]{object});
   });

   public static ResourceLocationArgument id() {
      return new ResourceLocationArgument();
   }

   public static Advancement getAdvancement(CommandContext commandContext, String string) throws CommandSyntaxException {
      ResourceLocation var2 = (ResourceLocation)commandContext.getArgument(string, ResourceLocation.class);
      Advancement var3 = ((CommandSourceStack)commandContext.getSource()).getServer().getAdvancements().getAdvancement(var2);
      if(var3 == null) {
         throw ERROR_UNKNOWN_ADVANCEMENT.create(var2);
      } else {
         return var3;
      }
   }

   public static Recipe getRecipe(CommandContext commandContext, String string) throws CommandSyntaxException {
      RecipeManager var2 = ((CommandSourceStack)commandContext.getSource()).getServer().getRecipeManager();
      ResourceLocation var3 = (ResourceLocation)commandContext.getArgument(string, ResourceLocation.class);
      return (Recipe)var2.byKey(var3).orElseThrow(() -> {
         return ERROR_UNKNOWN_RECIPE.create(var3);
      });
   }

   public static ResourceLocation getId(CommandContext commandContext, String string) {
      return (ResourceLocation)commandContext.getArgument(string, ResourceLocation.class);
   }

   public ResourceLocation parse(StringReader stringReader) throws CommandSyntaxException {
      return ResourceLocation.read(stringReader);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
