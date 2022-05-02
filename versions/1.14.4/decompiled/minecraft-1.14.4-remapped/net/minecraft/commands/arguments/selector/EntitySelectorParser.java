package net.minecraft.commands.arguments.selector;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.WrappedMinMaxBounds;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntitySelectorParser {
   public static final SimpleCommandExceptionType ERROR_INVALID_NAME_OR_UUID = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.invalid", new Object[0]));
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_SELECTOR_TYPE = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.entity.selector.unknown", new Object[]{object});
   });
   public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.selector.not_allowed", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_MISSING_SELECTOR_TYPE = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.selector.missing", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_OPTIONS = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.options.unterminated", new Object[0]));
   public static final DynamicCommandExceptionType ERROR_EXPECTED_OPTION_VALUE = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.entity.options.valueless", new Object[]{object});
   });
   public static final BiConsumer ORDER_ARBITRARY = (vec3, list) -> {
   };
   public static final BiConsumer ORDER_NEAREST = (vec3, list) -> {
      list.sort((var1, var2) -> {
         return Doubles.compare(var1.distanceToSqr(vec3), var2.distanceToSqr(vec3));
      });
   };
   public static final BiConsumer ORDER_FURTHEST = (vec3, list) -> {
      list.sort((var1, var2) -> {
         return Doubles.compare(var2.distanceToSqr(vec3), var1.distanceToSqr(vec3));
      });
   };
   public static final BiConsumer ORDER_RANDOM = (vec3, list) -> {
      Collections.shuffle(list);
   };
   public static final BiFunction SUGGEST_NOTHING = (suggestionsBuilder, consumer) -> {
      return suggestionsBuilder.buildFuture();
   };
   private final StringReader reader;
   private final boolean allowSelectors;
   private int maxResults;
   private boolean includesEntities;
   private boolean worldLimited;
   private MinMaxBounds.Floats distance;
   private MinMaxBounds.Ints level;
   @Nullable
   private Double x;
   @Nullable
   private Double y;
   @Nullable
   private Double z;
   @Nullable
   private Double deltaX;
   @Nullable
   private Double deltaY;
   @Nullable
   private Double deltaZ;
   private WrappedMinMaxBounds rotX;
   private WrappedMinMaxBounds rotY;
   private Predicate predicate;
   private BiConsumer order;
   private boolean currentEntity;
   @Nullable
   private String playerName;
   private int startPosition;
   @Nullable
   private UUID entityUUID;
   private BiFunction suggestions;
   private boolean hasNameEquals;
   private boolean hasNameNotEquals;
   private boolean isLimited;
   private boolean isSorted;
   private boolean hasGamemodeEquals;
   private boolean hasGamemodeNotEquals;
   private boolean hasTeamEquals;
   private boolean hasTeamNotEquals;
   @Nullable
   private EntityType type;
   private boolean typeInverse;
   private boolean hasScores;
   private boolean hasAdvancements;
   private boolean usesSelectors;

   public EntitySelectorParser(StringReader stringReader) {
      this(stringReader, true);
   }

   public EntitySelectorParser(StringReader reader, boolean allowSelectors) {
      this.distance = MinMaxBounds.Floats.ANY;
      this.level = MinMaxBounds.Ints.ANY;
      this.rotX = WrappedMinMaxBounds.ANY;
      this.rotY = WrappedMinMaxBounds.ANY;
      this.predicate = (entity) -> {
         return true;
      };
      this.order = ORDER_ARBITRARY;
      this.suggestions = SUGGEST_NOTHING;
      this.reader = reader;
      this.allowSelectors = allowSelectors;
   }

   public EntitySelector getSelector() {
      AABB var1;
      if(this.deltaX == null && this.deltaY == null && this.deltaZ == null) {
         if(this.distance.getMax() != null) {
            float var2 = ((Float)this.distance.getMax()).floatValue();
            var1 = new AABB((double)(-var2), (double)(-var2), (double)(-var2), (double)(var2 + 1.0F), (double)(var2 + 1.0F), (double)(var2 + 1.0F));
         } else {
            var1 = null;
         }
      } else {
         var1 = this.createAabb(this.deltaX == null?0.0D:this.deltaX.doubleValue(), this.deltaY == null?0.0D:this.deltaY.doubleValue(), this.deltaZ == null?0.0D:this.deltaZ.doubleValue());
      }

      Function<Vec3, Vec3> var2;
      if(this.x == null && this.y == null && this.z == null) {
         var2 = (vec3) -> {
            return vec3;
         };
      } else {
         var2 = (vec3) -> {
            return new Vec3(this.x == null?vec3.x:this.x.doubleValue(), this.y == null?vec3.y:this.y.doubleValue(), this.z == null?vec3.z:this.z.doubleValue());
         };
      }

      return new EntitySelector(this.maxResults, this.includesEntities, this.worldLimited, this.predicate, this.distance, var2, var1, this.order, this.currentEntity, this.playerName, this.entityUUID, this.type, this.usesSelectors);
   }

   private AABB createAabb(double var1, double var3, double var5) {
      boolean var7 = var1 < 0.0D;
      boolean var8 = var3 < 0.0D;
      boolean var9 = var5 < 0.0D;
      double var10 = var7?var1:0.0D;
      double var12 = var8?var3:0.0D;
      double var14 = var9?var5:0.0D;
      double var16 = (var7?0.0D:var1) + 1.0D;
      double var18 = (var8?0.0D:var3) + 1.0D;
      double var20 = (var9?0.0D:var5) + 1.0D;
      return new AABB(var10, var12, var14, var16, var18, var20);
   }

   private void finalizePredicates() {
      if(this.rotX != WrappedMinMaxBounds.ANY) {
         this.predicate = this.predicate.and(this.createRotationPredicate(this.rotX, (entity) -> {
            return (double)entity.xRot;
         }));
      }

      if(this.rotY != WrappedMinMaxBounds.ANY) {
         this.predicate = this.predicate.and(this.createRotationPredicate(this.rotY, (entity) -> {
            return (double)entity.yRot;
         }));
      }

      if(!this.level.isAny()) {
         this.predicate = this.predicate.and((entity) -> {
            return !(entity instanceof ServerPlayer)?false:this.level.matches(((ServerPlayer)entity).experienceLevel);
         });
      }

   }

   private Predicate createRotationPredicate(WrappedMinMaxBounds wrappedMinMaxBounds, ToDoubleFunction toDoubleFunction) {
      double var3 = (double)Mth.wrapDegrees(wrappedMinMaxBounds.getMin() == null?0.0F:wrappedMinMaxBounds.getMin().floatValue());
      double var5 = (double)Mth.wrapDegrees(wrappedMinMaxBounds.getMax() == null?359.0F:wrappedMinMaxBounds.getMax().floatValue());
      return (entity) -> {
         double var6 = Mth.wrapDegrees(toDoubleFunction.applyAsDouble(entity));
         return var3 > var5?var6 >= var3 || var6 <= var5:var6 >= var3 && var6 <= var5;
      };
   }

   protected void parseSelector() throws CommandSyntaxException {
      this.usesSelectors = true;
      this.suggestions = this::suggestSelector;
      if(!this.reader.canRead()) {
         throw ERROR_MISSING_SELECTOR_TYPE.createWithContext(this.reader);
      } else {
         int var1 = this.reader.getCursor();
         char var2 = this.reader.read();
         if(var2 == 112) {
            this.maxResults = 1;
            this.includesEntities = false;
            this.order = ORDER_NEAREST;
            this.limitToType(EntityType.PLAYER);
         } else if(var2 == 97) {
            this.maxResults = Integer.MAX_VALUE;
            this.includesEntities = false;
            this.order = ORDER_ARBITRARY;
            this.limitToType(EntityType.PLAYER);
         } else if(var2 == 114) {
            this.maxResults = 1;
            this.includesEntities = false;
            this.order = ORDER_RANDOM;
            this.limitToType(EntityType.PLAYER);
         } else if(var2 == 115) {
            this.maxResults = 1;
            this.includesEntities = true;
            this.currentEntity = true;
         } else {
            if(var2 != 101) {
               this.reader.setCursor(var1);
               throw ERROR_UNKNOWN_SELECTOR_TYPE.createWithContext(this.reader, '@' + String.valueOf(var2));
            }

            this.maxResults = Integer.MAX_VALUE;
            this.includesEntities = true;
            this.order = ORDER_ARBITRARY;
            this.predicate = Entity::isAlive;
         }

         this.suggestions = this::suggestOpenOptions;
         if(this.reader.canRead() && this.reader.peek() == 91) {
            this.reader.skip();
            this.suggestions = this::suggestOptionsKeyOrClose;
            this.parseOptions();
         }

      }
   }

   protected void parseNameOrUUID() throws CommandSyntaxException {
      if(this.reader.canRead()) {
         this.suggestions = this::suggestName;
      }

      int var1 = this.reader.getCursor();
      String var2 = this.reader.readString();

      try {
         this.entityUUID = UUID.fromString(var2);
         this.includesEntities = true;
      } catch (IllegalArgumentException var4) {
         if(var2.isEmpty() || var2.length() > 16) {
            this.reader.setCursor(var1);
            throw ERROR_INVALID_NAME_OR_UUID.createWithContext(this.reader);
         }

         this.includesEntities = false;
         this.playerName = var2;
      }

      this.maxResults = 1;
   }

   protected void parseOptions() throws CommandSyntaxException {
      this.suggestions = this::suggestOptionsKey;
      this.reader.skipWhitespace();

      while(true) {
         if(this.reader.canRead() && this.reader.peek() != 93) {
            this.reader.skipWhitespace();
            int var1 = this.reader.getCursor();
            String var2 = this.reader.readString();
            EntitySelectorOptions.Modifier var3 = EntitySelectorOptions.get(this, var2, var1);
            this.reader.skipWhitespace();
            if(!this.reader.canRead() || this.reader.peek() != 61) {
               this.reader.setCursor(var1);
               throw ERROR_EXPECTED_OPTION_VALUE.createWithContext(this.reader, var2);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = SUGGEST_NOTHING;
            var3.handle(this);
            this.reader.skipWhitespace();
            this.suggestions = this::suggestOptionsNextOrClose;
            if(!this.reader.canRead()) {
               continue;
            }

            if(this.reader.peek() == 44) {
               this.reader.skip();
               this.suggestions = this::suggestOptionsKey;
               continue;
            }

            if(this.reader.peek() != 93) {
               throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
            }
         }

         if(this.reader.canRead()) {
            this.reader.skip();
            this.suggestions = SUGGEST_NOTHING;
            return;
         }

         throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
      }
   }

   public boolean shouldInvertValue() {
      this.reader.skipWhitespace();
      if(this.reader.canRead() && this.reader.peek() == 33) {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   public boolean isTag() {
      this.reader.skipWhitespace();
      if(this.reader.canRead() && this.reader.peek() == 35) {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   public StringReader getReader() {
      return this.reader;
   }

   public void addPredicate(Predicate predicate) {
      this.predicate = this.predicate.and(predicate);
   }

   public void setWorldLimited() {
      this.worldLimited = true;
   }

   public MinMaxBounds.Floats getDistance() {
      return this.distance;
   }

   public void setDistance(MinMaxBounds.Floats distance) {
      this.distance = distance;
   }

   public MinMaxBounds.Ints getLevel() {
      return this.level;
   }

   public void setLevel(MinMaxBounds.Ints level) {
      this.level = level;
   }

   public WrappedMinMaxBounds getRotX() {
      return this.rotX;
   }

   public void setRotX(WrappedMinMaxBounds rotX) {
      this.rotX = rotX;
   }

   public WrappedMinMaxBounds getRotY() {
      return this.rotY;
   }

   public void setRotY(WrappedMinMaxBounds rotY) {
      this.rotY = rotY;
   }

   @Nullable
   public Double getX() {
      return this.x;
   }

   @Nullable
   public Double getY() {
      return this.y;
   }

   @Nullable
   public Double getZ() {
      return this.z;
   }

   public void setX(double x) {
      this.x = Double.valueOf(x);
   }

   public void setY(double y) {
      this.y = Double.valueOf(y);
   }

   public void setZ(double z) {
      this.z = Double.valueOf(z);
   }

   public void setDeltaX(double deltaX) {
      this.deltaX = Double.valueOf(deltaX);
   }

   public void setDeltaY(double deltaY) {
      this.deltaY = Double.valueOf(deltaY);
   }

   public void setDeltaZ(double deltaZ) {
      this.deltaZ = Double.valueOf(deltaZ);
   }

   @Nullable
   public Double getDeltaX() {
      return this.deltaX;
   }

   @Nullable
   public Double getDeltaY() {
      return this.deltaY;
   }

   @Nullable
   public Double getDeltaZ() {
      return this.deltaZ;
   }

   public void setMaxResults(int maxResults) {
      this.maxResults = maxResults;
   }

   public void setIncludesEntities(boolean includesEntities) {
      this.includesEntities = includesEntities;
   }

   public void setOrder(BiConsumer order) {
      this.order = order;
   }

   public EntitySelector parse() throws CommandSyntaxException {
      this.startPosition = this.reader.getCursor();
      this.suggestions = this::suggestNameOrSelector;
      if(this.reader.canRead() && this.reader.peek() == 64) {
         if(!this.allowSelectors) {
            throw ERROR_SELECTORS_NOT_ALLOWED.createWithContext(this.reader);
         }

         this.reader.skip();
         this.parseSelector();
      } else {
         this.parseNameOrUUID();
      }

      this.finalizePredicates();
      return this.getSelector();
   }

   private static void fillSelectorSuggestions(SuggestionsBuilder suggestionsBuilder) {
      suggestionsBuilder.suggest("@p", new TranslatableComponent("argument.entity.selector.nearestPlayer", new Object[0]));
      suggestionsBuilder.suggest("@a", new TranslatableComponent("argument.entity.selector.allPlayers", new Object[0]));
      suggestionsBuilder.suggest("@r", new TranslatableComponent("argument.entity.selector.randomPlayer", new Object[0]));
      suggestionsBuilder.suggest("@s", new TranslatableComponent("argument.entity.selector.self", new Object[0]));
      suggestionsBuilder.suggest("@e", new TranslatableComponent("argument.entity.selector.allEntities", new Object[0]));
   }

   private CompletableFuture suggestNameOrSelector(SuggestionsBuilder suggestionsBuilder, Consumer consumer) {
      consumer.accept(suggestionsBuilder);
      if(this.allowSelectors) {
         fillSelectorSuggestions(suggestionsBuilder);
      }

      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture suggestName(SuggestionsBuilder suggestionsBuilder, Consumer consumer) {
      SuggestionsBuilder suggestionsBuilder = suggestionsBuilder.createOffset(this.startPosition);
      consumer.accept(suggestionsBuilder);
      return suggestionsBuilder.add(suggestionsBuilder).buildFuture();
   }

   private CompletableFuture suggestSelector(SuggestionsBuilder suggestionsBuilder, Consumer consumer) {
      SuggestionsBuilder suggestionsBuilder = suggestionsBuilder.createOffset(suggestionsBuilder.getStart() - 1);
      fillSelectorSuggestions(suggestionsBuilder);
      suggestionsBuilder.add(suggestionsBuilder);
      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture suggestOpenOptions(SuggestionsBuilder suggestionsBuilder, Consumer consumer) {
      suggestionsBuilder.suggest(String.valueOf('['));
      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture suggestOptionsKeyOrClose(SuggestionsBuilder suggestionsBuilder, Consumer consumer) {
      suggestionsBuilder.suggest(String.valueOf(']'));
      EntitySelectorOptions.suggestNames(this, suggestionsBuilder);
      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture suggestOptionsKey(SuggestionsBuilder suggestionsBuilder, Consumer consumer) {
      EntitySelectorOptions.suggestNames(this, suggestionsBuilder);
      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture suggestOptionsNextOrClose(SuggestionsBuilder suggestionsBuilder, Consumer consumer) {
      suggestionsBuilder.suggest(String.valueOf(','));
      suggestionsBuilder.suggest(String.valueOf(']'));
      return suggestionsBuilder.buildFuture();
   }

   public boolean isCurrentEntity() {
      return this.currentEntity;
   }

   public void setSuggestions(BiFunction suggestions) {
      this.suggestions = suggestions;
   }

   public CompletableFuture fillSuggestions(SuggestionsBuilder suggestionsBuilder, Consumer consumer) {
      return (CompletableFuture)this.suggestions.apply(suggestionsBuilder.createOffset(this.reader.getCursor()), consumer);
   }

   public boolean hasNameEquals() {
      return this.hasNameEquals;
   }

   public void setHasNameEquals(boolean hasNameEquals) {
      this.hasNameEquals = hasNameEquals;
   }

   public boolean hasNameNotEquals() {
      return this.hasNameNotEquals;
   }

   public void setHasNameNotEquals(boolean hasNameNotEquals) {
      this.hasNameNotEquals = hasNameNotEquals;
   }

   public boolean isLimited() {
      return this.isLimited;
   }

   public void setLimited(boolean limited) {
      this.isLimited = limited;
   }

   public boolean isSorted() {
      return this.isSorted;
   }

   public void setSorted(boolean sorted) {
      this.isSorted = sorted;
   }

   public boolean hasGamemodeEquals() {
      return this.hasGamemodeEquals;
   }

   public void setHasGamemodeEquals(boolean hasGamemodeEquals) {
      this.hasGamemodeEquals = hasGamemodeEquals;
   }

   public boolean hasGamemodeNotEquals() {
      return this.hasGamemodeNotEquals;
   }

   public void setHasGamemodeNotEquals(boolean hasGamemodeNotEquals) {
      this.hasGamemodeNotEquals = hasGamemodeNotEquals;
   }

   public boolean hasTeamEquals() {
      return this.hasTeamEquals;
   }

   public void setHasTeamEquals(boolean hasTeamEquals) {
      this.hasTeamEquals = hasTeamEquals;
   }

   public void setHasTeamNotEquals(boolean hasTeamNotEquals) {
      this.hasTeamNotEquals = hasTeamNotEquals;
   }

   public void limitToType(EntityType type) {
      this.type = type;
   }

   public void setTypeLimitedInversely() {
      this.typeInverse = true;
   }

   public boolean isTypeLimited() {
      return this.type != null;
   }

   public boolean isTypeLimitedInversely() {
      return this.typeInverse;
   }

   public boolean hasScores() {
      return this.hasScores;
   }

   public void setHasScores(boolean hasScores) {
      this.hasScores = hasScores;
   }

   public boolean hasAdvancements() {
      return this.hasAdvancements;
   }

   public void setHasAdvancements(boolean hasAdvancements) {
      this.hasAdvancements = hasAdvancements;
   }
}
