package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementCommands {
   private static final SuggestionProvider SUGGEST_ADVANCEMENTS = (commandContext, suggestionsBuilder) -> {
      Collection<Advancement> var2 = ((CommandSourceStack)commandContext.getSource()).getServer().getAdvancements().getAllAdvancements();
      return SharedSuggestionProvider.suggestResource(var2.stream().map(Advancement::getId), suggestionsBuilder);
   };

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("advancement").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.literal("grant").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((commandContext) -> {
         return perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(commandContext, "advancement"), AdvancementCommands.Mode.ONLY));
      })).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((commandContext, suggestionsBuilder) -> {
         return SharedSuggestionProvider.suggest((Iterable)ResourceLocationArgument.getAdvancement(commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder);
      }).executes((commandContext) -> {
         return performCriterion((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), AdvancementCommands.Action.GRANT, ResourceLocationArgument.getAdvancement(commandContext, "advancement"), StringArgumentType.getString(commandContext, "criterion"));
      }))))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((commandContext) -> {
         return perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(commandContext, "advancement"), AdvancementCommands.Mode.FROM));
      })))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((commandContext) -> {
         return perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(commandContext, "advancement"), AdvancementCommands.Mode.UNTIL));
      })))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((commandContext) -> {
         return perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(commandContext, "advancement"), AdvancementCommands.Mode.THROUGH));
      })))).then(Commands.literal("everything").executes((commandContext) -> {
         return perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), AdvancementCommands.Action.GRANT, ((CommandSourceStack)commandContext.getSource()).getServer().getAdvancements().getAllAdvancements());
      }))))).then(Commands.literal("revoke").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((commandContext) -> {
         return perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(commandContext, "advancement"), AdvancementCommands.Mode.ONLY));
      })).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((commandContext, suggestionsBuilder) -> {
         return SharedSuggestionProvider.suggest((Iterable)ResourceLocationArgument.getAdvancement(commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder);
      }).executes((commandContext) -> {
         return performCriterion((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), AdvancementCommands.Action.REVOKE, ResourceLocationArgument.getAdvancement(commandContext, "advancement"), StringArgumentType.getString(commandContext, "criterion"));
      }))))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((commandContext) -> {
         return perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(commandContext, "advancement"), AdvancementCommands.Mode.FROM));
      })))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((commandContext) -> {
         return perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(commandContext, "advancement"), AdvancementCommands.Mode.UNTIL));
      })))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((commandContext) -> {
         return perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(commandContext, "advancement"), AdvancementCommands.Mode.THROUGH));
      })))).then(Commands.literal("everything").executes((commandContext) -> {
         return perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), AdvancementCommands.Action.REVOKE, ((CommandSourceStack)commandContext.getSource()).getServer().getAdvancements().getAllAdvancements());
      })))));
   }

   private static int perform(CommandSourceStack commandSourceStack, Collection var1, AdvancementCommands.Action advancementCommands$Action, Collection var3) {
      int var4 = 0;

      for(ServerPlayer var6 : var1) {
         var4 += advancementCommands$Action.perform(var6, (Iterable)var3);
      }

      if(var4 == 0) {
         if(var3.size() == 1) {
            if(var1.size() == 1) {
               throw new CommandRuntimeException(new TranslatableComponent(advancementCommands$Action.getKey() + ".one.to.one.failure", new Object[]{((Advancement)var3.iterator().next()).getChatComponent(), ((ServerPlayer)var1.iterator().next()).getDisplayName()}));
            } else {
               throw new CommandRuntimeException(new TranslatableComponent(advancementCommands$Action.getKey() + ".one.to.many.failure", new Object[]{((Advancement)var3.iterator().next()).getChatComponent(), Integer.valueOf(var1.size())}));
            }
         } else if(var1.size() == 1) {
            throw new CommandRuntimeException(new TranslatableComponent(advancementCommands$Action.getKey() + ".many.to.one.failure", new Object[]{Integer.valueOf(var3.size()), ((ServerPlayer)var1.iterator().next()).getDisplayName()}));
         } else {
            throw new CommandRuntimeException(new TranslatableComponent(advancementCommands$Action.getKey() + ".many.to.many.failure", new Object[]{Integer.valueOf(var3.size()), Integer.valueOf(var1.size())}));
         }
      } else {
         if(var3.size() == 1) {
            if(var1.size() == 1) {
               commandSourceStack.sendSuccess(new TranslatableComponent(advancementCommands$Action.getKey() + ".one.to.one.success", new Object[]{((Advancement)var3.iterator().next()).getChatComponent(), ((ServerPlayer)var1.iterator().next()).getDisplayName()}), true);
            } else {
               commandSourceStack.sendSuccess(new TranslatableComponent(advancementCommands$Action.getKey() + ".one.to.many.success", new Object[]{((Advancement)var3.iterator().next()).getChatComponent(), Integer.valueOf(var1.size())}), true);
            }
         } else if(var1.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent(advancementCommands$Action.getKey() + ".many.to.one.success", new Object[]{Integer.valueOf(var3.size()), ((ServerPlayer)var1.iterator().next()).getDisplayName()}), true);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent(advancementCommands$Action.getKey() + ".many.to.many.success", new Object[]{Integer.valueOf(var3.size()), Integer.valueOf(var1.size())}), true);
         }

         return var4;
      }
   }

   private static int performCriterion(CommandSourceStack commandSourceStack, Collection collection, AdvancementCommands.Action advancementCommands$Action, Advancement advancement, String string) {
      int var5 = 0;
      if(!advancement.getCriteria().containsKey(string)) {
         throw new CommandRuntimeException(new TranslatableComponent("commands.advancement.criterionNotFound", new Object[]{advancement.getChatComponent(), string}));
      } else {
         for(ServerPlayer var7 : collection) {
            if(advancementCommands$Action.performCriterion(var7, advancement, string)) {
               ++var5;
            }
         }

         if(var5 == 0) {
            if(collection.size() == 1) {
               throw new CommandRuntimeException(new TranslatableComponent(advancementCommands$Action.getKey() + ".criterion.to.one.failure", new Object[]{string, advancement.getChatComponent(), ((ServerPlayer)collection.iterator().next()).getDisplayName()}));
            } else {
               throw new CommandRuntimeException(new TranslatableComponent(advancementCommands$Action.getKey() + ".criterion.to.many.failure", new Object[]{string, advancement.getChatComponent(), Integer.valueOf(collection.size())}));
            }
         } else {
            if(collection.size() == 1) {
               commandSourceStack.sendSuccess(new TranslatableComponent(advancementCommands$Action.getKey() + ".criterion.to.one.success", new Object[]{string, advancement.getChatComponent(), ((ServerPlayer)collection.iterator().next()).getDisplayName()}), true);
            } else {
               commandSourceStack.sendSuccess(new TranslatableComponent(advancementCommands$Action.getKey() + ".criterion.to.many.success", new Object[]{string, advancement.getChatComponent(), Integer.valueOf(collection.size())}), true);
            }

            return var5;
         }
      }
   }

   private static List getAdvancements(Advancement advancement, AdvancementCommands.Mode advancementCommands$Mode) {
      List<Advancement> list = Lists.newArrayList();
      if(advancementCommands$Mode.parents) {
         for(Advancement var3 = advancement.getParent(); var3 != null; var3 = var3.getParent()) {
            list.add(var3);
         }
      }

      list.add(advancement);
      if(advancementCommands$Mode.children) {
         addChildren(advancement, list);
      }

      return list;
   }

   private static void addChildren(Advancement advancement, List list) {
      for(Advancement var3 : advancement.getChildren()) {
         list.add(var3);
         addChildren(var3, list);
      }

   }

   static enum Action {
      GRANT("grant") {
         protected boolean perform(ServerPlayer serverPlayer, Advancement advancement) {
            AdvancementProgress var3 = serverPlayer.getAdvancements().getOrStartProgress(advancement);
            if(var3.isDone()) {
               return false;
            } else {
               for(String var5 : var3.getRemainingCriteria()) {
                  serverPlayer.getAdvancements().award(advancement, var5);
               }

               return true;
            }
         }

         protected boolean performCriterion(ServerPlayer serverPlayer, Advancement advancement, String string) {
            return serverPlayer.getAdvancements().award(advancement, string);
         }
      },
      REVOKE("revoke") {
         protected boolean perform(ServerPlayer serverPlayer, Advancement advancement) {
            AdvancementProgress var3 = serverPlayer.getAdvancements().getOrStartProgress(advancement);
            if(!var3.hasProgress()) {
               return false;
            } else {
               for(String var5 : var3.getCompletedCriteria()) {
                  serverPlayer.getAdvancements().revoke(advancement, var5);
               }

               return true;
            }
         }

         protected boolean performCriterion(ServerPlayer serverPlayer, Advancement advancement, String string) {
            return serverPlayer.getAdvancements().revoke(advancement, string);
         }
      };

      private final String key;

      private Action(String var3) {
         this.key = "commands.advancement." + var3;
      }

      public int perform(ServerPlayer serverPlayer, Iterable iterable) {
         int var3 = 0;

         for(Advancement var5 : iterable) {
            if(this.perform(serverPlayer, var5)) {
               ++var3;
            }
         }

         return var3;
      }

      protected abstract boolean perform(ServerPlayer var1, Advancement var2);

      protected abstract boolean performCriterion(ServerPlayer var1, Advancement var2, String var3);

      protected String getKey() {
         return this.key;
      }
   }

   static enum Mode {
      ONLY(false, false),
      THROUGH(true, true),
      FROM(false, true),
      UNTIL(true, false),
      EVERYTHING(true, true);

      private final boolean parents;
      private final boolean children;

      private Mode(boolean parents, boolean children) {
         this.parents = parents;
         this.children = children;
      }
   }
}
