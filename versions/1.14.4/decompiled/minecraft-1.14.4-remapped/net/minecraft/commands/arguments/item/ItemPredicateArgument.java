package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemPredicateArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"stick", "minecraft:stick", "#stick", "#stick{foo=bar}"});
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("arguments.item.tag.unknown", new Object[]{object});
   });

   public static ItemPredicateArgument itemPredicate() {
      return new ItemPredicateArgument();
   }

   public ItemPredicateArgument.Result parse(StringReader stringReader) throws CommandSyntaxException {
      ItemParser var2 = (new ItemParser(stringReader, true)).parse();
      if(var2.getItem() != null) {
         ItemPredicateArgument.ItemPredicate var3 = new ItemPredicateArgument.ItemPredicate(var2.getItem(), var2.getNbt());
         return (commandContext) -> {
            return var3;
         };
      } else {
         ResourceLocation var3 = var2.getTag();
         return (commandContext) -> {
            Tag<Item> var3 = ((CommandSourceStack)commandContext.getSource()).getServer().getTags().getItems().getTag(var3);
            if(var3 == null) {
               throw ERROR_UNKNOWN_TAG.create(var3.toString());
            } else {
               return new ItemPredicateArgument.TagPredicate(var3, var2.getNbt());
            }
         };
      }
   }

   public static Predicate getItemPredicate(CommandContext commandContext, String string) throws CommandSyntaxException {
      return ((ItemPredicateArgument.Result)commandContext.getArgument(string, ItemPredicateArgument.Result.class)).create(commandContext);
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      StringReader var3 = new StringReader(suggestionsBuilder.getInput());
      var3.setCursor(suggestionsBuilder.getStart());
      ItemParser var4 = new ItemParser(var3, true);

      try {
         var4.parse();
      } catch (CommandSyntaxException var6) {
         ;
      }

      return var4.fillSuggestions(suggestionsBuilder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   static class ItemPredicate implements Predicate {
      private final Item item;
      @Nullable
      private final CompoundTag nbt;

      public ItemPredicate(Item item, @Nullable CompoundTag nbt) {
         this.item = item;
         this.nbt = nbt;
      }

      public boolean test(ItemStack itemStack) {
         return itemStack.getItem() == this.item && NbtUtils.compareNbt(this.nbt, itemStack.getTag(), true);
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((ItemStack)var1);
      }
   }

   public interface Result {
      Predicate create(CommandContext var1) throws CommandSyntaxException;
   }

   static class TagPredicate implements Predicate {
      private final Tag tag;
      @Nullable
      private final CompoundTag nbt;

      public TagPredicate(Tag tag, @Nullable CompoundTag nbt) {
         this.tag = tag;
         this.nbt = nbt;
      }

      public boolean test(ItemStack itemStack) {
         return this.tag.contains(itemStack.getItem()) && NbtUtils.compareNbt(this.nbt, itemStack.getTag(), true);
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((ItemStack)var1);
      }
   }
}
