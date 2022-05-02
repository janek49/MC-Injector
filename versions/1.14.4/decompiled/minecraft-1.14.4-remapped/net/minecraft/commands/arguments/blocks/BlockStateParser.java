package net.minecraft.commands.arguments.blocks;

import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockStateParser {
   public static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(new TranslatableComponent("argument.block.tag.disallowed", new Object[0]));
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_BLOCK = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.block.id.invalid", new Object[]{object});
   });
   public static final Dynamic2CommandExceptionType ERROR_UNKNOWN_PROPERTY = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.block.property.unknown", new Object[]{var0, var1});
   });
   public static final Dynamic2CommandExceptionType ERROR_DUPLICATE_PROPERTY = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.block.property.duplicate", new Object[]{var1, var0});
   });
   public static final Dynamic3CommandExceptionType ERROR_INVALID_VALUE = new Dynamic3CommandExceptionType((var0, var1, var2) -> {
      return new TranslatableComponent("argument.block.property.invalid", new Object[]{var0, var2, var1});
   });
   public static final Dynamic2CommandExceptionType ERROR_EXPECTED_VALUE = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.block.property.novalue", new Object[]{var0, var1});
   });
   public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_PROPERTIES = new SimpleCommandExceptionType(new TranslatableComponent("argument.block.property.unclosed", new Object[0]));
   private static final Function SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
   private final StringReader reader;
   private final boolean forTesting;
   private final Map properties = Maps.newHashMap();
   private final Map vagueProperties = Maps.newHashMap();
   private ResourceLocation id = new ResourceLocation("");
   private StateDefinition definition;
   private BlockState state;
   @Nullable
   private CompoundTag nbt;
   private ResourceLocation tag = new ResourceLocation("");
   private int tagCursor;
   private Function suggestions;

   public BlockStateParser(StringReader reader, boolean forTesting) {
      this.suggestions = SUGGEST_NOTHING;
      this.reader = reader;
      this.forTesting = forTesting;
   }

   public Map getProperties() {
      return this.properties;
   }

   @Nullable
   public BlockState getState() {
      return this.state;
   }

   @Nullable
   public CompoundTag getNbt() {
      return this.nbt;
   }

   @Nullable
   public ResourceLocation getTag() {
      return this.tag;
   }

   public BlockStateParser parse(boolean b) throws CommandSyntaxException {
      this.suggestions = this::suggestBlockIdOrTag;
      if(this.reader.canRead() && this.reader.peek() == 35) {
         this.readTag();
         this.suggestions = this::suggestOpenVaguePropertiesOrNbt;
         if(this.reader.canRead() && this.reader.peek() == 91) {
            this.readVagueProperties();
            this.suggestions = this::suggestOpenNbt;
         }
      } else {
         this.readBlock();
         this.suggestions = this::suggestOpenPropertiesOrNbt;
         if(this.reader.canRead() && this.reader.peek() == 91) {
            this.readProperties();
            this.suggestions = this::suggestOpenNbt;
         }
      }

      if(b && this.reader.canRead() && this.reader.peek() == 123) {
         this.suggestions = SUGGEST_NOTHING;
         this.readNbt();
      }

      return this;
   }

   private CompletableFuture suggestPropertyNameOrEnd(SuggestionsBuilder suggestionsBuilder) {
      if(suggestionsBuilder.getRemaining().isEmpty()) {
         suggestionsBuilder.suggest(String.valueOf(']'));
      }

      return this.suggestPropertyName(suggestionsBuilder);
   }

   private CompletableFuture suggestVaguePropertyNameOrEnd(SuggestionsBuilder suggestionsBuilder) {
      if(suggestionsBuilder.getRemaining().isEmpty()) {
         suggestionsBuilder.suggest(String.valueOf(']'));
      }

      return this.suggestVaguePropertyName(suggestionsBuilder);
   }

   private CompletableFuture suggestPropertyName(SuggestionsBuilder suggestionsBuilder) {
      String var2 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

      for(Property<?> var4 : this.state.getProperties()) {
         if(!this.properties.containsKey(var4) && var4.getName().startsWith(var2)) {
            suggestionsBuilder.suggest(var4.getName() + '=');
         }
      }

      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture suggestVaguePropertyName(SuggestionsBuilder suggestionsBuilder) {
      String var2 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
      if(this.tag != null && !this.tag.getPath().isEmpty()) {
         Tag<Block> var3 = BlockTags.getAllTags().getTag(this.tag);
         if(var3 != null) {
            for(Block var5 : var3.getValues()) {
               for(Property<?> var7 : var5.getStateDefinition().getProperties()) {
                  if(!this.vagueProperties.containsKey(var7.getName()) && var7.getName().startsWith(var2)) {
                     suggestionsBuilder.suggest(var7.getName() + '=');
                  }
               }
            }
         }
      }

      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture suggestOpenNbt(SuggestionsBuilder suggestionsBuilder) {
      if(suggestionsBuilder.getRemaining().isEmpty() && this.hasBlockEntity()) {
         suggestionsBuilder.suggest(String.valueOf('{'));
      }

      return suggestionsBuilder.buildFuture();
   }

   private boolean hasBlockEntity() {
      if(this.state != null) {
         return this.state.getBlock().isEntityBlock();
      } else {
         if(this.tag != null) {
            Tag<Block> var1 = BlockTags.getAllTags().getTag(this.tag);
            if(var1 != null) {
               for(Block var3 : var1.getValues()) {
                  if(var3.isEntityBlock()) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private CompletableFuture suggestEquals(SuggestionsBuilder suggestionsBuilder) {
      if(suggestionsBuilder.getRemaining().isEmpty()) {
         suggestionsBuilder.suggest(String.valueOf('='));
      }

      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture suggestNextPropertyOrEnd(SuggestionsBuilder suggestionsBuilder) {
      if(suggestionsBuilder.getRemaining().isEmpty()) {
         suggestionsBuilder.suggest(String.valueOf(']'));
      }

      if(suggestionsBuilder.getRemaining().isEmpty() && this.properties.size() < this.state.getProperties().size()) {
         suggestionsBuilder.suggest(String.valueOf(','));
      }

      return suggestionsBuilder.buildFuture();
   }

   private static SuggestionsBuilder addSuggestions(SuggestionsBuilder var0, Property property) {
      for(T var3 : property.getPossibleValues()) {
         if(var3 instanceof Integer) {
            var0.suggest(((Integer)var3).intValue());
         } else {
            var0.suggest(property.getName(var3));
         }
      }

      return var0;
   }

   private CompletableFuture suggestVaguePropertyValue(SuggestionsBuilder suggestionsBuilder, String string) {
      boolean var3 = false;
      if(this.tag != null && !this.tag.getPath().isEmpty()) {
         Tag<Block> var4 = BlockTags.getAllTags().getTag(this.tag);
         if(var4 != null) {
            label61:
            for(Block var6 : var4.getValues()) {
               Property<?> var7 = var6.getStateDefinition().getProperty(string);
               if(var7 != null) {
                  addSuggestions(suggestionsBuilder, var7);
               }

               if(!var3) {
                  Iterator var8 = var6.getStateDefinition().getProperties().iterator();

                  while(true) {
                     if(!var8.hasNext()) {
                        continue label61;
                     }

                     Property<?> var9 = (Property)var8.next();
                     if(!this.vagueProperties.containsKey(var9.getName())) {
                        break;
                     }
                  }

                  var3 = true;
               }
            }
         }
      }

      if(var3) {
         suggestionsBuilder.suggest(String.valueOf(','));
      }

      suggestionsBuilder.suggest(String.valueOf(']'));
      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture suggestOpenVaguePropertiesOrNbt(SuggestionsBuilder suggestionsBuilder) {
      if(suggestionsBuilder.getRemaining().isEmpty()) {
         Tag<Block> var2 = BlockTags.getAllTags().getTag(this.tag);
         if(var2 != null) {
            boolean var3 = false;
            boolean var4 = false;

            for(Block var6 : var2.getValues()) {
               var3 |= !var6.getStateDefinition().getProperties().isEmpty();
               var4 |= var6.isEntityBlock();
               if(var3 && var4) {
                  break;
               }
            }

            if(var3) {
               suggestionsBuilder.suggest(String.valueOf('['));
            }

            if(var4) {
               suggestionsBuilder.suggest(String.valueOf('{'));
            }
         }
      }

      return this.suggestTag(suggestionsBuilder);
   }

   private CompletableFuture suggestOpenPropertiesOrNbt(SuggestionsBuilder suggestionsBuilder) {
      if(suggestionsBuilder.getRemaining().isEmpty()) {
         if(!this.state.getBlock().getStateDefinition().getProperties().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf('['));
         }

         if(this.state.getBlock().isEntityBlock()) {
            suggestionsBuilder.suggest(String.valueOf('{'));
         }
      }

      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture suggestTag(SuggestionsBuilder suggestionsBuilder) {
      return SharedSuggestionProvider.suggestResource((Iterable)BlockTags.getAllTags().getAvailableTags(), suggestionsBuilder.createOffset(this.tagCursor).add(suggestionsBuilder));
   }

   private CompletableFuture suggestBlockIdOrTag(SuggestionsBuilder suggestionsBuilder) {
      if(this.forTesting) {
         SharedSuggestionProvider.suggestResource(BlockTags.getAllTags().getAvailableTags(), suggestionsBuilder, String.valueOf('#'));
      }

      SharedSuggestionProvider.suggestResource((Iterable)Registry.BLOCK.keySet(), suggestionsBuilder);
      return suggestionsBuilder.buildFuture();
   }

   public void readBlock() throws CommandSyntaxException {
      int var1 = this.reader.getCursor();
      this.id = ResourceLocation.read(this.reader);
      Block var2 = (Block)Registry.BLOCK.getOptional(this.id).orElseThrow(() -> {
         this.reader.setCursor(var1);
         return ERROR_UNKNOWN_BLOCK.createWithContext(this.reader, this.id.toString());
      });
      this.definition = var2.getStateDefinition();
      this.state = var2.defaultBlockState();
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

   public void readProperties() throws CommandSyntaxException {
      this.reader.skip();
      this.suggestions = this::suggestPropertyNameOrEnd;
      this.reader.skipWhitespace();

      while(true) {
         if(this.reader.canRead() && this.reader.peek() != 93) {
            this.reader.skipWhitespace();
            int var1 = this.reader.getCursor();
            String var2 = this.reader.readString();
            Property<?> var3 = this.definition.getProperty(var2);
            if(var3 == null) {
               this.reader.setCursor(var1);
               throw ERROR_UNKNOWN_PROPERTY.createWithContext(this.reader, this.id.toString(), var2);
            }

            if(this.properties.containsKey(var3)) {
               this.reader.setCursor(var1);
               throw ERROR_DUPLICATE_PROPERTY.createWithContext(this.reader, this.id.toString(), var2);
            }

            this.reader.skipWhitespace();
            this.suggestions = this::suggestEquals;
            if(!this.reader.canRead() || this.reader.peek() != 61) {
               throw ERROR_EXPECTED_VALUE.createWithContext(this.reader, this.id.toString(), var2);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = (suggestionsBuilder) -> {
               return addSuggestions(suggestionsBuilder, var3).buildFuture();
            };
            int var4 = this.reader.getCursor();
            this.setValue(var3, this.reader.readString(), var4);
            this.suggestions = this::suggestNextPropertyOrEnd;
            this.reader.skipWhitespace();
            if(!this.reader.canRead()) {
               continue;
            }

            if(this.reader.peek() == 44) {
               this.reader.skip();
               this.suggestions = this::suggestPropertyName;
               continue;
            }

            if(this.reader.peek() != 93) {
               throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
            }
         }

         if(this.reader.canRead()) {
            this.reader.skip();
            return;
         }

         throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
      }
   }

   public void readVagueProperties() throws CommandSyntaxException {
      this.reader.skip();
      this.suggestions = this::suggestVaguePropertyNameOrEnd;
      int var1 = -1;
      this.reader.skipWhitespace();

      while(true) {
         if(this.reader.canRead() && this.reader.peek() != 93) {
            this.reader.skipWhitespace();
            int var2 = this.reader.getCursor();
            String var3 = this.reader.readString();
            if(this.vagueProperties.containsKey(var3)) {
               this.reader.setCursor(var2);
               throw ERROR_DUPLICATE_PROPERTY.createWithContext(this.reader, this.id.toString(), var3);
            }

            this.reader.skipWhitespace();
            if(!this.reader.canRead() || this.reader.peek() != 61) {
               this.reader.setCursor(var2);
               throw ERROR_EXPECTED_VALUE.createWithContext(this.reader, this.id.toString(), var3);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = (suggestionsBuilder) -> {
               return this.suggestVaguePropertyValue(suggestionsBuilder, var3);
            };
            var1 = this.reader.getCursor();
            String var4 = this.reader.readString();
            this.vagueProperties.put(var3, var4);
            this.reader.skipWhitespace();
            if(!this.reader.canRead()) {
               continue;
            }

            var1 = -1;
            if(this.reader.peek() == 44) {
               this.reader.skip();
               this.suggestions = this::suggestVaguePropertyName;
               continue;
            }

            if(this.reader.peek() != 93) {
               throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
            }
         }

         if(this.reader.canRead()) {
            this.reader.skip();
            return;
         }

         if(var1 >= 0) {
            this.reader.setCursor(var1);
         }

         throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
      }
   }

   public void readNbt() throws CommandSyntaxException {
      this.nbt = (new TagParser(this.reader)).readStruct();
   }

   private void setValue(Property property, String string, int var3) throws CommandSyntaxException {
      Optional<T> var4 = property.getValue(string);
      if(var4.isPresent()) {
         this.state = (BlockState)this.state.setValue(property, (Comparable)var4.get());
         this.properties.put(property, var4.get());
      } else {
         this.reader.setCursor(var3);
         throw ERROR_INVALID_VALUE.createWithContext(this.reader, this.id.toString(), property.getName(), string);
      }
   }

   public static String serialize(BlockState blockState) {
      StringBuilder var1 = new StringBuilder(Registry.BLOCK.getKey(blockState.getBlock()).toString());
      if(!blockState.getProperties().isEmpty()) {
         var1.append('[');
         boolean var2 = false;

         for(UnmodifiableIterator var3 = blockState.getValues().entrySet().iterator(); var3.hasNext(); var2 = true) {
            Entry<Property<?>, Comparable<?>> var4 = (Entry)var3.next();
            if(var2) {
               var1.append(',');
            }

            appendProperty(var1, (Property)var4.getKey(), (Comparable)var4.getValue());
         }

         var1.append(']');
      }

      return var1.toString();
   }

   private static void appendProperty(StringBuilder stringBuilder, Property property, Comparable comparable) {
      stringBuilder.append(property.getName());
      stringBuilder.append('=');
      stringBuilder.append(property.getName(comparable));
   }

   public CompletableFuture fillSuggestions(SuggestionsBuilder suggestionsBuilder) {
      return (CompletableFuture)this.suggestions.apply(suggestionsBuilder.createOffset(this.reader.getCursor()));
   }

   public Map getVagueProperties() {
      return this.vagueProperties;
   }
}
