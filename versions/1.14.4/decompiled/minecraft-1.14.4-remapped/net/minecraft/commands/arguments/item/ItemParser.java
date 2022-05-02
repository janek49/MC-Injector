package net.minecraft.commands.arguments.item;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

public class ItemParser {
   public static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(new TranslatableComponent("argument.item.tag.disallowed", new Object[0]));
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.item.id.invalid", new Object[]{object});
   });
   private static final Function SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
   private final StringReader reader;
   private final boolean forTesting;
   private final Map properties = Maps.newHashMap();
   private Item item;
   @Nullable
   private CompoundTag nbt;
   private ResourceLocation tag = new ResourceLocation("");
   private int tagCursor;
   private Function suggestions;

   public ItemParser(StringReader reader, boolean forTesting) {
      this.suggestions = SUGGEST_NOTHING;
      this.reader = reader;
      this.forTesting = forTesting;
   }

   public Item getItem() {
      return this.item;
   }

   @Nullable
   public CompoundTag getNbt() {
      return this.nbt;
   }

   public ResourceLocation getTag() {
      return this.tag;
   }

   public void readItem() throws CommandSyntaxException {
      int var1 = this.reader.getCursor();
      ResourceLocation var2 = ResourceLocation.read(this.reader);
      this.item = (Item)Registry.ITEM.getOptional(var2).orElseThrow(() -> {
         this.reader.setCursor(var1);
         return ERROR_UNKNOWN_ITEM.createWithContext(this.reader, var2.toString());
      });
   }

   public void readTag() throws CommandSyntaxException {
      if(!this.forTesting) {
         throw ERROR_NO_TAGS_ALLOWED.create();
      } else {
         this.suggestions = this::suggestTag;
         this.reader.expect('#');
         this.tagCursor = this.reader.getCursor();
         this.tag = ResourceLocation.read(this.reader);
      }
   }

   public void readNbt() throws CommandSyntaxException {
      this.nbt = (new TagParser(this.reader)).readStruct();
   }

   public ItemParser parse() throws CommandSyntaxException {
      this.suggestions = this::suggestItemIdOrTag;
      if(this.reader.canRead() && this.reader.peek() == 35) {
         this.readTag();
      } else {
         this.readItem();
         this.suggestions = this::suggestOpenNbt;
      }

      if(this.reader.canRead() && this.reader.peek() == 123) {
         this.suggestions = SUGGEST_NOTHING;
         this.readNbt();
      }

      return this;
   }

   private CompletableFuture suggestOpenNbt(SuggestionsBuilder suggestionsBuilder) {
      if(suggestionsBuilder.getRemaining().isEmpty()) {
         suggestionsBuilder.suggest(String.valueOf('{'));
      }

      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture suggestTag(SuggestionsBuilder suggestionsBuilder) {
      return SharedSuggestionProvider.suggestResource((Iterable)ItemTags.getAllTags().getAvailableTags(), suggestionsBuilder.createOffset(this.tagCursor));
   }

   private CompletableFuture suggestItemIdOrTag(SuggestionsBuilder suggestionsBuilder) {
      if(this.forTesting) {
         SharedSuggestionProvider.suggestResource(ItemTags.getAllTags().getAvailableTags(), suggestionsBuilder, String.valueOf('#'));
      }

      return SharedSuggestionProvider.suggestResource((Iterable)Registry.ITEM.keySet(), suggestionsBuilder);
   }

   public CompletableFuture fillSuggestions(SuggestionsBuilder suggestionsBuilder) {
      return (CompletableFuture)this.suggestions.apply(suggestionsBuilder.createOffset(this.reader.getCursor()));
   }
}
