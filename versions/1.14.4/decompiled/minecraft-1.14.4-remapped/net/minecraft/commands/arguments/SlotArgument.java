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
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;

public class SlotArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"container.5", "12", "weapon"});
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_SLOT = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("slot.unknown", new Object[]{object});
   });
   private static final Map SLOTS = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      for(int var1 = 0; var1 < 54; ++var1) {
         hashMap.put("container." + var1, Integer.valueOf(var1));
      }

      for(int var1 = 0; var1 < 9; ++var1) {
         hashMap.put("hotbar." + var1, Integer.valueOf(var1));
      }

      for(int var1 = 0; var1 < 27; ++var1) {
         hashMap.put("inventory." + var1, Integer.valueOf(9 + var1));
      }

      for(int var1 = 0; var1 < 27; ++var1) {
         hashMap.put("enderchest." + var1, Integer.valueOf(200 + var1));
      }

      for(int var1 = 0; var1 < 8; ++var1) {
         hashMap.put("villager." + var1, Integer.valueOf(300 + var1));
      }

      for(int var1 = 0; var1 < 15; ++var1) {
         hashMap.put("horse." + var1, Integer.valueOf(500 + var1));
      }

      hashMap.put("weapon", Integer.valueOf(98));
      hashMap.put("weapon.mainhand", Integer.valueOf(98));
      hashMap.put("weapon.offhand", Integer.valueOf(99));
      hashMap.put("armor.head", Integer.valueOf(100 + EquipmentSlot.HEAD.getIndex()));
      hashMap.put("armor.chest", Integer.valueOf(100 + EquipmentSlot.CHEST.getIndex()));
      hashMap.put("armor.legs", Integer.valueOf(100 + EquipmentSlot.LEGS.getIndex()));
      hashMap.put("armor.feet", Integer.valueOf(100 + EquipmentSlot.FEET.getIndex()));
      hashMap.put("horse.saddle", Integer.valueOf(400));
      hashMap.put("horse.armor", Integer.valueOf(401));
      hashMap.put("horse.chest", Integer.valueOf(499));
   });

   public static SlotArgument slot() {
      return new SlotArgument();
   }

   public static int getSlot(CommandContext commandContext, String string) {
      return ((Integer)commandContext.getArgument(string, Integer.class)).intValue();
   }

   public Integer parse(StringReader stringReader) throws CommandSyntaxException {
      String var2 = stringReader.readUnquotedString();
      if(!SLOTS.containsKey(var2)) {
         throw ERROR_UNKNOWN_SLOT.create(var2);
      } else {
         return (Integer)SLOTS.get(var2);
      }
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      return SharedSuggestionProvider.suggest((Iterable)SLOTS.keySet(), suggestionsBuilder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
