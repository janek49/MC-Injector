package net.minecraft.commands.arguments.selector.options;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.WrappedMinMaxBounds;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class EntitySelectorOptions {
   private static final Map OPTIONS = Maps.newHashMap();
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_OPTION = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.entity.options.unknown", new Object[]{object});
   });
   public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_OPTION = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.entity.options.inapplicable", new Object[]{object});
   });
   public static final SimpleCommandExceptionType ERROR_RANGE_NEGATIVE = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.options.distance.negative", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_LEVEL_NEGATIVE = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.options.level.negative", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_LIMIT_TOO_SMALL = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.options.limit.toosmall", new Object[0]));
   public static final DynamicCommandExceptionType ERROR_SORT_UNKNOWN = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.entity.options.sort.irreversible", new Object[]{object});
   });
   public static final DynamicCommandExceptionType ERROR_GAME_MODE_INVALID = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.entity.options.mode.invalid", new Object[]{object});
   });
   public static final DynamicCommandExceptionType ERROR_ENTITY_TYPE_INVALID = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.entity.options.type.invalid", new Object[]{object});
   });

   private static void register(String string, EntitySelectorOptions.Modifier entitySelectorOptions$Modifier, Predicate predicate, Component component) {
      OPTIONS.put(string, new EntitySelectorOptions.Option(entitySelectorOptions$Modifier, predicate, component));
   }

   public static void bootStrap() {
      if(OPTIONS.isEmpty()) {
         register("name", (entitySelectorParser) -> {
            int var1 = entitySelectorParser.getReader().getCursor();
            boolean var2 = entitySelectorParser.shouldInvertValue();
            String var3 = entitySelectorParser.getReader().readString();
            if(entitySelectorParser.hasNameNotEquals() && !var2) {
               entitySelectorParser.getReader().setCursor(var1);
               throw ERROR_INAPPLICABLE_OPTION.createWithContext(entitySelectorParser.getReader(), "name");
            } else {
               if(var2) {
                  entitySelectorParser.setHasNameNotEquals(true);
               } else {
                  entitySelectorParser.setHasNameEquals(true);
               }

               entitySelectorParser.addPredicate((entity) -> {
                  return entity.getName().getContents().equals(var3) != var2;
               });
            }
         }, (entitySelectorParser) -> {
            return !entitySelectorParser.hasNameEquals();
         }, new TranslatableComponent("argument.entity.options.name.description", new Object[0]));
         register("distance", (entitySelectorParser) -> {
            int var1 = entitySelectorParser.getReader().getCursor();
            MinMaxBounds.Floats var2 = MinMaxBounds.Floats.fromReader(entitySelectorParser.getReader());
            if((var2.getMin() == null || ((Float)var2.getMin()).floatValue() >= 0.0F) && (var2.getMax() == null || ((Float)var2.getMax()).floatValue() >= 0.0F)) {
               entitySelectorParser.setDistance(var2);
               entitySelectorParser.setWorldLimited();
            } else {
               entitySelectorParser.getReader().setCursor(var1);
               throw ERROR_RANGE_NEGATIVE.createWithContext(entitySelectorParser.getReader());
            }
         }, (entitySelectorParser) -> {
            return entitySelectorParser.getDistance().isAny();
         }, new TranslatableComponent("argument.entity.options.distance.description", new Object[0]));
         register("level", (entitySelectorParser) -> {
            int var1 = entitySelectorParser.getReader().getCursor();
            MinMaxBounds.Ints var2 = MinMaxBounds.Ints.fromReader(entitySelectorParser.getReader());
            if((var2.getMin() == null || ((Integer)var2.getMin()).intValue() >= 0) && (var2.getMax() == null || ((Integer)var2.getMax()).intValue() >= 0)) {
               entitySelectorParser.setLevel(var2);
               entitySelectorParser.setIncludesEntities(false);
            } else {
               entitySelectorParser.getReader().setCursor(var1);
               throw ERROR_LEVEL_NEGATIVE.createWithContext(entitySelectorParser.getReader());
            }
         }, (entitySelectorParser) -> {
            return entitySelectorParser.getLevel().isAny();
         }, new TranslatableComponent("argument.entity.options.level.description", new Object[0]));
         register("x", (entitySelectorParser) -> {
            entitySelectorParser.setWorldLimited();
            entitySelectorParser.setX(entitySelectorParser.getReader().readDouble());
         }, (entitySelectorParser) -> {
            return entitySelectorParser.getX() == null;
         }, new TranslatableComponent("argument.entity.options.x.description", new Object[0]));
         register("y", (entitySelectorParser) -> {
            entitySelectorParser.setWorldLimited();
            entitySelectorParser.setY(entitySelectorParser.getReader().readDouble());
         }, (entitySelectorParser) -> {
            return entitySelectorParser.getY() == null;
         }, new TranslatableComponent("argument.entity.options.y.description", new Object[0]));
         register("z", (entitySelectorParser) -> {
            entitySelectorParser.setWorldLimited();
            entitySelectorParser.setZ(entitySelectorParser.getReader().readDouble());
         }, (entitySelectorParser) -> {
            return entitySelectorParser.getZ() == null;
         }, new TranslatableComponent("argument.entity.options.z.description", new Object[0]));
         register("dx", (entitySelectorParser) -> {
            entitySelectorParser.setWorldLimited();
            entitySelectorParser.setDeltaX(entitySelectorParser.getReader().readDouble());
         }, (entitySelectorParser) -> {
            return entitySelectorParser.getDeltaX() == null;
         }, new TranslatableComponent("argument.entity.options.dx.description", new Object[0]));
         register("dy", (entitySelectorParser) -> {
            entitySelectorParser.setWorldLimited();
            entitySelectorParser.setDeltaY(entitySelectorParser.getReader().readDouble());
         }, (entitySelectorParser) -> {
            return entitySelectorParser.getDeltaY() == null;
         }, new TranslatableComponent("argument.entity.options.dy.description", new Object[0]));
         register("dz", (entitySelectorParser) -> {
            entitySelectorParser.setWorldLimited();
            entitySelectorParser.setDeltaZ(entitySelectorParser.getReader().readDouble());
         }, (entitySelectorParser) -> {
            return entitySelectorParser.getDeltaZ() == null;
         }, new TranslatableComponent("argument.entity.options.dz.description", new Object[0]));
         register("x_rotation", (entitySelectorParser) -> {
            entitySelectorParser.setRotX(WrappedMinMaxBounds.fromReader(entitySelectorParser.getReader(), true, Mth::wrapDegrees));
         }, (entitySelectorParser) -> {
            return entitySelectorParser.getRotX() == WrappedMinMaxBounds.ANY;
         }, new TranslatableComponent("argument.entity.options.x_rotation.description", new Object[0]));
         register("y_rotation", (entitySelectorParser) -> {
            entitySelectorParser.setRotY(WrappedMinMaxBounds.fromReader(entitySelectorParser.getReader(), true, Mth::wrapDegrees));
         }, (entitySelectorParser) -> {
            return entitySelectorParser.getRotY() == WrappedMinMaxBounds.ANY;
         }, new TranslatableComponent("argument.entity.options.y_rotation.description", new Object[0]));
         register("limit", (entitySelectorParser) -> {
            int var1 = entitySelectorParser.getReader().getCursor();
            int var2 = entitySelectorParser.getReader().readInt();
            if(var2 < 1) {
               entitySelectorParser.getReader().setCursor(var1);
               throw ERROR_LIMIT_TOO_SMALL.createWithContext(entitySelectorParser.getReader());
            } else {
               entitySelectorParser.setMaxResults(var2);
               entitySelectorParser.setLimited(true);
            }
         }, (entitySelectorParser) -> {
            return !entitySelectorParser.isCurrentEntity() && !entitySelectorParser.isLimited();
         }, new TranslatableComponent("argument.entity.options.limit.description", new Object[0]));
         register("sort", (entitySelectorParser) -> {
            int var1 = entitySelectorParser.getReader().getCursor();
            String var2 = entitySelectorParser.getReader().readUnquotedString();
            entitySelectorParser.setSuggestions((suggestionsBuilder, consumer) -> {
               return SharedSuggestionProvider.suggest((Iterable)Arrays.asList(new String[]{"nearest", "furthest", "random", "arbitrary"}), suggestionsBuilder);
            });
            byte var5 = -1;
            switch(var2.hashCode()) {
            case -938285885:
               if(var2.equals("random")) {
                  var5 = 2;
               }
               break;
            case 1510793967:
               if(var2.equals("furthest")) {
                  var5 = 1;
               }
               break;
            case 1780188658:
               if(var2.equals("arbitrary")) {
                  var5 = 3;
               }
               break;
            case 1825779806:
               if(var2.equals("nearest")) {
                  var5 = 0;
               }
            }

            BiConsumer<Vec3, List<? extends Entity>> var3;
            switch(var5) {
            case 0:
               var3 = EntitySelectorParser.ORDER_NEAREST;
               break;
            case 1:
               var3 = EntitySelectorParser.ORDER_FURTHEST;
               break;
            case 2:
               var3 = EntitySelectorParser.ORDER_RANDOM;
               break;
            case 3:
               var3 = EntitySelectorParser.ORDER_ARBITRARY;
               break;
            default:
               entitySelectorParser.getReader().setCursor(var1);
               throw ERROR_SORT_UNKNOWN.createWithContext(entitySelectorParser.getReader(), var2);
            }

            entitySelectorParser.setOrder(var3);
            entitySelectorParser.setSorted(true);
         }, (entitySelectorParser) -> {
            return !entitySelectorParser.isCurrentEntity() && !entitySelectorParser.isSorted();
         }, new TranslatableComponent("argument.entity.options.sort.description", new Object[0]));
         register("gamemode", (entitySelectorParser) -> {
            entitySelectorParser.setSuggestions((suggestionsBuilder, consumer) -> {
               String var3 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
               boolean var4 = !entitySelectorParser.hasGamemodeNotEquals();
               boolean var5 = true;
               if(!var3.isEmpty()) {
                  if(var3.charAt(0) == 33) {
                     var4 = false;
                     var3 = var3.substring(1);
                  } else {
                     var5 = false;
                  }
               }

               for(GameType var9 : GameType.values()) {
                  if(var9 != GameType.NOT_SET && var9.getName().toLowerCase(Locale.ROOT).startsWith(var3)) {
                     if(var5) {
                        suggestionsBuilder.suggest('!' + var9.getName());
                     }

                     if(var4) {
                        suggestionsBuilder.suggest(var9.getName());
                     }
                  }
               }

               return suggestionsBuilder.buildFuture();
            });
            int var1 = entitySelectorParser.getReader().getCursor();
            boolean var2 = entitySelectorParser.shouldInvertValue();
            if(entitySelectorParser.hasGamemodeNotEquals() && !var2) {
               entitySelectorParser.getReader().setCursor(var1);
               throw ERROR_INAPPLICABLE_OPTION.createWithContext(entitySelectorParser.getReader(), "gamemode");
            } else {
               String var3 = entitySelectorParser.getReader().readUnquotedString();
               GameType var4 = GameType.byName(var3, GameType.NOT_SET);
               if(var4 == GameType.NOT_SET) {
                  entitySelectorParser.getReader().setCursor(var1);
                  throw ERROR_GAME_MODE_INVALID.createWithContext(entitySelectorParser.getReader(), var3);
               } else {
                  entitySelectorParser.setIncludesEntities(false);
                  entitySelectorParser.addPredicate((entity) -> {
                     if(!(entity instanceof ServerPlayer)) {
                        return false;
                     } else {
                        GameType gameType = ((ServerPlayer)entity).gameMode.getGameModeForPlayer();
                        return var2?gameType != var4:gameType == var4;
                     }
                  });
                  if(var2) {
                     entitySelectorParser.setHasGamemodeNotEquals(true);
                  } else {
                     entitySelectorParser.setHasGamemodeEquals(true);
                  }

               }
            }
         }, (entitySelectorParser) -> {
            return !entitySelectorParser.hasGamemodeEquals();
         }, new TranslatableComponent("argument.entity.options.gamemode.description", new Object[0]));
         register("team", (entitySelectorParser) -> {
            boolean var1 = entitySelectorParser.shouldInvertValue();
            String var2 = entitySelectorParser.getReader().readUnquotedString();
            entitySelectorParser.addPredicate((entity) -> {
               if(!(entity instanceof LivingEntity)) {
                  return false;
               } else {
                  Team var3 = entity.getTeam();
                  String var4 = var3 == null?"":var3.getName();
                  return var4.equals(var2) != var1;
               }
            });
            if(var1) {
               entitySelectorParser.setHasTeamNotEquals(true);
            } else {
               entitySelectorParser.setHasTeamEquals(true);
            }

         }, (entitySelectorParser) -> {
            return !entitySelectorParser.hasTeamEquals();
         }, new TranslatableComponent("argument.entity.options.team.description", new Object[0]));
         register("type", (entitySelectorParser) -> {
            entitySelectorParser.setSuggestions((suggestionsBuilder, consumer) -> {
               SharedSuggestionProvider.suggestResource(Registry.ENTITY_TYPE.keySet(), suggestionsBuilder, String.valueOf('!'));
               SharedSuggestionProvider.suggestResource(EntityTypeTags.getAllTags().getAvailableTags(), suggestionsBuilder, "!#");
               if(!entitySelectorParser.isTypeLimitedInversely()) {
                  SharedSuggestionProvider.suggestResource((Iterable)Registry.ENTITY_TYPE.keySet(), suggestionsBuilder);
                  SharedSuggestionProvider.suggestResource(EntityTypeTags.getAllTags().getAvailableTags(), suggestionsBuilder, String.valueOf('#'));
               }

               return suggestionsBuilder.buildFuture();
            });
            int var1 = entitySelectorParser.getReader().getCursor();
            boolean var2 = entitySelectorParser.shouldInvertValue();
            if(entitySelectorParser.isTypeLimitedInversely() && !var2) {
               entitySelectorParser.getReader().setCursor(var1);
               throw ERROR_INAPPLICABLE_OPTION.createWithContext(entitySelectorParser.getReader(), "type");
            } else {
               if(var2) {
                  entitySelectorParser.setTypeLimitedInversely();
               }

               if(entitySelectorParser.isTag()) {
                  ResourceLocation var3 = ResourceLocation.read(entitySelectorParser.getReader());
                  Tag<EntityType<?>> var4 = EntityTypeTags.getAllTags().getTag(var3);
                  if(var4 == null) {
                     entitySelectorParser.getReader().setCursor(var1);
                     throw ERROR_ENTITY_TYPE_INVALID.createWithContext(entitySelectorParser.getReader(), var3.toString());
                  }

                  entitySelectorParser.addPredicate((entity) -> {
                     return var4x.contains(entity.getType()) != var2;
                  });
               } else {
                  ResourceLocation var3 = ResourceLocation.read(entitySelectorParser.getReader());
                  EntityType<?> var4 = (EntityType)Registry.ENTITY_TYPE.getOptional(var3).orElseThrow(() -> {
                     entitySelectorParser.getReader().setCursor(var1);
                     return ERROR_ENTITY_TYPE_INVALID.createWithContext(entitySelectorParser.getReader(), var3x.toString());
                  });
                  if(Objects.equals(EntityType.PLAYER, var4) && !var2) {
                     entitySelectorParser.setIncludesEntities(false);
                  }

                  entitySelectorParser.addPredicate((entity) -> {
                     return Objects.equals(var4, entity.getType()) != var2;
                  });
                  if(!var2) {
                     entitySelectorParser.limitToType(var4);
                  }
               }

            }
         }, (entitySelectorParser) -> {
            return !entitySelectorParser.isTypeLimited();
         }, new TranslatableComponent("argument.entity.options.type.description", new Object[0]));
         register("tag", (entitySelectorParser) -> {
            boolean var1 = entitySelectorParser.shouldInvertValue();
            String var2 = entitySelectorParser.getReader().readUnquotedString();
            entitySelectorParser.addPredicate((entity) -> {
               return "".equals(var2)?entity.getTags().isEmpty() != var1:entity.getTags().contains(var2) != var1;
            });
         }, (entitySelectorParser) -> {
            return true;
         }, new TranslatableComponent("argument.entity.options.tag.description", new Object[0]));
         register("nbt", (entitySelectorParser) -> {
            boolean var1 = entitySelectorParser.shouldInvertValue();
            CompoundTag var2 = (new TagParser(entitySelectorParser.getReader())).readStruct();
            entitySelectorParser.addPredicate((entity) -> {
               CompoundTag compoundTag = entity.saveWithoutId(new CompoundTag());
               if(entity instanceof ServerPlayer) {
                  ItemStack var4 = ((ServerPlayer)entity).inventory.getSelected();
                  if(!var4.isEmpty()) {
                     compoundTag.put("SelectedItem", var4.save(new CompoundTag()));
                  }
               }

               return NbtUtils.compareNbt(var2, compoundTag, true) != var1;
            });
         }, (entitySelectorParser) -> {
            return true;
         }, new TranslatableComponent("argument.entity.options.nbt.description", new Object[0]));
         register("scores", (entitySelectorParser) -> {
            StringReader var1 = entitySelectorParser.getReader();
            Map<String, MinMaxBounds.Ints> var2 = Maps.newHashMap();
            var1.expect('{');
            var1.skipWhitespace();

            while(var1.canRead() && var1.peek() != 125) {
               var1.skipWhitespace();
               String var3 = var1.readUnquotedString();
               var1.skipWhitespace();
               var1.expect('=');
               var1.skipWhitespace();
               MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromReader(var1);
               var2.put(var3, var4);
               var1.skipWhitespace();
               if(var1.canRead() && var1.peek() == 44) {
                  var1.skip();
               }
            }

            var1.expect('}');
            if(!var2.isEmpty()) {
               entitySelectorParser.addPredicate((entity) -> {
                  Scoreboard var2 = entity.getServer().getScoreboard();
                  String var3 = entity.getScoreboardName();

                  for(Entry<String, MinMaxBounds.Ints> var5 : var2.entrySet()) {
                     Objective var6 = var2.getObjective((String)var5.getKey());
                     if(var6 == null) {
                        return false;
                     }

                     if(!var2.hasPlayerScore(var3, var6)) {
                        return false;
                     }

                     Score var7 = var2.getOrCreatePlayerScore(var3, var6);
                     int var8 = var7.getScore();
                     if(!((MinMaxBounds.Ints)var5.getValue()).matches(var8)) {
                        return false;
                     }
                  }

                  return true;
               });
            }

            entitySelectorParser.setHasScores(true);
         }, (entitySelectorParser) -> {
            return !entitySelectorParser.hasScores();
         }, new TranslatableComponent("argument.entity.options.scores.description", new Object[0]));
         register("advancements", (entitySelectorParser) -> {
            StringReader var1 = entitySelectorParser.getReader();
            Map<ResourceLocation, Predicate<AdvancementProgress>> var2 = Maps.newHashMap();
            var1.expect('{');
            var1.skipWhitespace();

            while(var1.canRead() && var1.peek() != 125) {
               var1.skipWhitespace();
               ResourceLocation var3 = ResourceLocation.read(var1);
               var1.skipWhitespace();
               var1.expect('=');
               var1.skipWhitespace();
               if(var1.canRead() && var1.peek() == 123) {
                  Map<String, Predicate<CriterionProgress>> var4 = Maps.newHashMap();
                  var1.skipWhitespace();
                  var1.expect('{');
                  var1.skipWhitespace();

                  while(var1.canRead() && var1.peek() != 125) {
                     var1.skipWhitespace();
                     String var5 = var1.readUnquotedString();
                     var1.skipWhitespace();
                     var1.expect('=');
                     var1.skipWhitespace();
                     boolean var6 = var1.readBoolean();
                     var4.put(var5, (criterionProgress) -> {
                        return criterionProgress.isDone() == var6;
                     });
                     var1.skipWhitespace();
                     if(var1.canRead() && var1.peek() == 44) {
                        var1.skip();
                     }
                  }

                  var1.skipWhitespace();
                  var1.expect('}');
                  var1.skipWhitespace();
                  var2.put(var3, (advancementProgress) -> {
                     for(Entry<String, Predicate<CriterionProgress>> var3 : var4.entrySet()) {
                        CriterionProgress var4 = advancementProgress.getCriterion((String)var3.getKey());
                        if(var4 == null || !((Predicate)var3.getValue()).test(var4)) {
                           return false;
                        }
                     }

                     return true;
                  });
               } else {
                  boolean var4 = var1.readBoolean();
                  var2.put(var3, (advancementProgress) -> {
                     return advancementProgress.isDone() == var4x;
                  });
               }

               var1.skipWhitespace();
               if(var1.canRead() && var1.peek() == 44) {
                  var1.skip();
               }
            }

            var1.expect('}');
            if(!var2.isEmpty()) {
               entitySelectorParser.addPredicate((entity) -> {
                  if(!(entity instanceof ServerPlayer)) {
                     return false;
                  } else {
                     ServerPlayer var2 = (ServerPlayer)entity;
                     PlayerAdvancements var3 = var2.getAdvancements();
                     ServerAdvancementManager var4 = var2.getServer().getAdvancements();

                     for(Entry<ResourceLocation, Predicate<AdvancementProgress>> var6 : var2.entrySet()) {
                        Advancement var7 = var4.getAdvancement((ResourceLocation)var6.getKey());
                        if(var7 == null || !((Predicate)var6.getValue()).test(var3.getOrStartProgress(var7))) {
                           return false;
                        }
                     }

                     return true;
                  }
               });
               entitySelectorParser.setIncludesEntities(false);
            }

            entitySelectorParser.setHasAdvancements(true);
         }, (entitySelectorParser) -> {
            return !entitySelectorParser.hasAdvancements();
         }, new TranslatableComponent("argument.entity.options.advancements.description", new Object[0]));
      }
   }

   public static EntitySelectorOptions.Modifier get(EntitySelectorParser entitySelectorParser, String string, int var2) throws CommandSyntaxException {
      EntitySelectorOptions.Option var3 = (EntitySelectorOptions.Option)OPTIONS.get(string);
      if(var3 != null) {
         if(var3.predicate.test(entitySelectorParser)) {
            return var3.modifier;
         } else {
            throw ERROR_INAPPLICABLE_OPTION.createWithContext(entitySelectorParser.getReader(), string);
         }
      } else {
         entitySelectorParser.getReader().setCursor(var2);
         throw ERROR_UNKNOWN_OPTION.createWithContext(entitySelectorParser.getReader(), string);
      }
   }

   public static void suggestNames(EntitySelectorParser entitySelectorParser, SuggestionsBuilder suggestionsBuilder) {
      String var2 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

      for(Entry<String, EntitySelectorOptions.Option> var4 : OPTIONS.entrySet()) {
         if(((EntitySelectorOptions.Option)var4.getValue()).predicate.test(entitySelectorParser) && ((String)var4.getKey()).toLowerCase(Locale.ROOT).startsWith(var2)) {
            suggestionsBuilder.suggest((String)var4.getKey() + '=', ((EntitySelectorOptions.Option)var4.getValue()).description);
         }
      }

   }

   public interface Modifier {
      void handle(EntitySelectorParser var1) throws CommandSyntaxException;
   }

   static class Option {
      public final EntitySelectorOptions.Modifier modifier;
      public final Predicate predicate;
      public final Component description;

      private Option(EntitySelectorOptions.Modifier modifier, Predicate predicate, Component description) {
         this.modifier = modifier;
         this.predicate = predicate;
         this.description = description;
      }
   }
}
