package net.minecraft.commands.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityAnchorArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"eyes", "feet"});
   private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.anchor.invalid", new Object[]{object});
   });

   public static EntityAnchorArgument.Anchor getAnchor(CommandContext commandContext, String string) {
      return (EntityAnchorArgument.Anchor)commandContext.getArgument(string, EntityAnchorArgument.Anchor.class);
   }

   public static EntityAnchorArgument anchor() {
      return new EntityAnchorArgument();
   }

   public EntityAnchorArgument.Anchor parse(StringReader stringReader) throws CommandSyntaxException {
      int var2 = stringReader.getCursor();
      String var3 = stringReader.readUnquotedString();
      EntityAnchorArgument.Anchor var4 = EntityAnchorArgument.Anchor.getByName(var3);
      if(var4 == null) {
         stringReader.setCursor(var2);
         throw ERROR_INVALID.createWithContext(stringReader, var3);
      } else {
         return var4;
      }
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      return SharedSuggestionProvider.suggest((Iterable)EntityAnchorArgument.Anchor.BY_NAME.keySet(), suggestionsBuilder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static enum Anchor {
      FEET("feet", (var0, entity) -> {
         return var0;
      }),
      EYES("eyes", (var0, entity) -> {
         return new Vec3(var0.x, var0.y + (double)entity.getEyeHeight(), var0.z);
      });

      private static final Map BY_NAME = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
         for(EntityAnchorArgument.Anchor var4 : values()) {
            hashMap.put(var4.name, var4);
         }

      });
      private final String name;
      private final BiFunction transform;

      private Anchor(String name, BiFunction transform) {
         this.name = name;
         this.transform = transform;
      }

      @Nullable
      public static EntityAnchorArgument.Anchor getByName(String name) {
         return (EntityAnchorArgument.Anchor)BY_NAME.get(name);
      }

      public Vec3 apply(Entity entity) {
         return (Vec3)this.transform.apply(new Vec3(entity.x, entity.y, entity.z), entity);
      }

      public Vec3 apply(CommandSourceStack commandSourceStack) {
         Entity var2 = commandSourceStack.getEntity();
         return var2 == null?commandSourceStack.getPosition():(Vec3)this.transform.apply(commandSourceStack.getPosition(), var2);
      }
   }
}
