package net.minecraft.commands.arguments.blocks;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockPredicateArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}"});
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("arguments.block.tag.unknown", new Object[]{object});
   });

   public static BlockPredicateArgument blockPredicate() {
      return new BlockPredicateArgument();
   }

   public BlockPredicateArgument.Result parse(StringReader stringReader) throws CommandSyntaxException {
      BlockStateParser var2 = (new BlockStateParser(stringReader, true)).parse(true);
      if(var2.getState() != null) {
         BlockPredicateArgument.BlockPredicate var3 = new BlockPredicateArgument.BlockPredicate(var2.getState(), var2.getProperties().keySet(), var2.getNbt());
         return (tagManager) -> {
            return var3;
         };
      } else {
         ResourceLocation var3 = var2.getTag();
         return (tagManager) -> {
            Tag<Block> var3 = tagManager.getBlocks().getTag(var3);
            if(var3 == null) {
               throw ERROR_UNKNOWN_TAG.create(var3.toString());
            } else {
               return new BlockPredicateArgument.TagPredicate(var3, var2.getVagueProperties(), var2.getNbt());
            }
         };
      }
   }

   public static Predicate getBlockPredicate(CommandContext commandContext, String string) throws CommandSyntaxException {
      return ((BlockPredicateArgument.Result)commandContext.getArgument(string, BlockPredicateArgument.Result.class)).create(((CommandSourceStack)commandContext.getSource()).getServer().getTags());
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      StringReader var3 = new StringReader(suggestionsBuilder.getInput());
      var3.setCursor(suggestionsBuilder.getStart());
      BlockStateParser var4 = new BlockStateParser(var3, true);

      try {
         var4.parse(true);
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

   static class BlockPredicate implements Predicate {
      private final BlockState state;
      private final Set properties;
      @Nullable
      private final CompoundTag nbt;

      public BlockPredicate(BlockState state, Set properties, @Nullable CompoundTag nbt) {
         this.state = state;
         this.properties = properties;
         this.nbt = nbt;
      }

      public boolean test(BlockInWorld blockInWorld) {
         BlockState var2 = blockInWorld.getState();
         if(var2.getBlock() != this.state.getBlock()) {
            return false;
         } else {
            for(Property<?> var4 : this.properties) {
               if(var2.getValue(var4) != this.state.getValue(var4)) {
                  return false;
               }
            }

            if(this.nbt == null) {
               return true;
            } else {
               BlockEntity var3 = blockInWorld.getEntity();
               return var3 != null && NbtUtils.compareNbt(this.nbt, var3.save(new CompoundTag()), true);
            }
         }
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((BlockInWorld)var1);
      }
   }

   public interface Result {
      Predicate create(TagManager var1) throws CommandSyntaxException;
   }

   static class TagPredicate implements Predicate {
      private final Tag tag;
      @Nullable
      private final CompoundTag nbt;
      private final Map vagueProperties;

      private TagPredicate(Tag tag, Map vagueProperties, @Nullable CompoundTag nbt) {
         this.tag = tag;
         this.vagueProperties = vagueProperties;
         this.nbt = nbt;
      }

      public boolean test(BlockInWorld blockInWorld) {
         BlockState var2 = blockInWorld.getState();
         if(!var2.is(this.tag)) {
            return false;
         } else {
            for(Entry<String, String> var4 : this.vagueProperties.entrySet()) {
               Property<?> var5 = var2.getBlock().getStateDefinition().getProperty((String)var4.getKey());
               if(var5 == null) {
                  return false;
               }

               Comparable<?> var6 = (Comparable)var5.getValue((String)var4.getValue()).orElse((Object)null);
               if(var6 == null) {
                  return false;
               }

               if(var2.getValue(var5) != var6) {
                  return false;
               }
            }

            if(this.nbt == null) {
               return true;
            } else {
               BlockEntity var3 = blockInWorld.getEntity();
               return var3 != null && NbtUtils.compareNbt(this.nbt, var3.save(new CompoundTag()), true);
            }
         }
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((BlockInWorld)var1);
      }
   }
}
