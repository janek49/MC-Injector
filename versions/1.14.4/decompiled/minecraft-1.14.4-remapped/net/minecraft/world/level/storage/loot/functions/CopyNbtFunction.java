package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNbtFunction extends LootItemConditionalFunction {
   private final CopyNbtFunction.DataSource source;
   private final List operations;
   private static final Function ENTITY_GETTER = NbtPredicate::getEntityTagToCompare;
   private static final Function BLOCK_ENTITY_GETTER = (blockEntity) -> {
      return blockEntity.save(new CompoundTag());
   };

   private CopyNbtFunction(LootItemCondition[] lootItemConditions, CopyNbtFunction.DataSource source, List list) {
      super(lootItemConditions);
      this.source = source;
      this.operations = ImmutableList.copyOf(list);
   }

   private static NbtPathArgument.NbtPath compileNbtPath(String string) {
      try {
         return (new NbtPathArgument()).parse(new StringReader(string));
      } catch (CommandSyntaxException var2) {
         throw new IllegalArgumentException("Failed to parse path " + string, var2);
      }
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(this.source.param);
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      Tag var3 = (Tag)this.source.getter.apply(lootContext);
      if(var3 != null) {
         this.operations.forEach((copyNbtFunction$CopyOperation) -> {
            copyNbtFunction$CopyOperation.apply(var1::getOrCreateTag, var3);
         });
      }

      return var1;
   }

   public static CopyNbtFunction.Builder copyData(CopyNbtFunction.DataSource copyNbtFunction$DataSource) {
      return new CopyNbtFunction.Builder(copyNbtFunction$DataSource);
   }

   public static class Builder extends LootItemConditionalFunction.Builder {
      private final CopyNbtFunction.DataSource source;
      private final List ops;

      private Builder(CopyNbtFunction.DataSource source) {
         this.ops = Lists.newArrayList();
         this.source = source;
      }

      public CopyNbtFunction.Builder copy(String var1, String var2, CopyNbtFunction.MergeStrategy copyNbtFunction$MergeStrategy) {
         this.ops.add(new CopyNbtFunction.CopyOperation(var1, var2, copyNbtFunction$MergeStrategy));
         return this;
      }

      public CopyNbtFunction.Builder copy(String var1, String var2) {
         return this.copy(var1, var2, CopyNbtFunction.MergeStrategy.REPLACE);
      }

      protected CopyNbtFunction.Builder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return new CopyNbtFunction(this.getConditions(), this.source, this.ops);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   static class CopyOperation {
      private final String sourcePathText;
      private final NbtPathArgument.NbtPath sourcePath;
      private final String targetPathText;
      private final NbtPathArgument.NbtPath targetPath;
      private final CopyNbtFunction.MergeStrategy op;

      private CopyOperation(String sourcePathText, String targetPathText, CopyNbtFunction.MergeStrategy op) {
         this.sourcePathText = sourcePathText;
         this.sourcePath = CopyNbtFunction.compileNbtPath(sourcePathText);
         this.targetPathText = targetPathText;
         this.targetPath = CopyNbtFunction.compileNbtPath(targetPathText);
         this.op = op;
      }

      public void apply(Supplier supplier, Tag tag) {
         try {
            List<Tag> var3 = this.sourcePath.get(tag);
            if(!var3.isEmpty()) {
               this.op.merge((Tag)supplier.get(), this.targetPath, var3);
            }
         } catch (CommandSyntaxException var4) {
            ;
         }

      }

      public JsonObject toJson() {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("source", this.sourcePathText);
         jsonObject.addProperty("target", this.targetPathText);
         jsonObject.addProperty("op", this.op.name);
         return jsonObject;
      }

      public static CopyNbtFunction.CopyOperation fromJson(JsonObject json) {
         String var1 = GsonHelper.getAsString(json, "source");
         String var2 = GsonHelper.getAsString(json, "target");
         CopyNbtFunction.MergeStrategy var3 = CopyNbtFunction.MergeStrategy.getByName(GsonHelper.getAsString(json, "op"));
         return new CopyNbtFunction.CopyOperation(var1, var2, var3);
      }
   }

   public static enum DataSource {
      THIS("this", LootContextParams.THIS_ENTITY, CopyNbtFunction.ENTITY_GETTER),
      KILLER("killer", LootContextParams.KILLER_ENTITY, CopyNbtFunction.ENTITY_GETTER),
      KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER, CopyNbtFunction.ENTITY_GETTER),
      BLOCK_ENTITY("block_entity", LootContextParams.BLOCK_ENTITY, CopyNbtFunction.BLOCK_ENTITY_GETTER);

      public final String name;
      public final LootContextParam param;
      public final Function getter;

      private DataSource(String name, LootContextParam param, Function getter) {
         this.name = name;
         this.param = param;
         this.getter = (lootContext) -> {
            T var3 = lootContext.getParamOrNull(param);
            return var3 != null?(Tag)getter.apply(var3):null;
         };
      }

      public static CopyNbtFunction.DataSource getByName(String name) {
         for(CopyNbtFunction.DataSource var4 : values()) {
            if(var4.name.equals(name)) {
               return var4;
            }
         }

         throw new IllegalArgumentException("Invalid tag source " + name);
      }
   }

   public static enum MergeStrategy {
      REPLACE("replace") {
         public void merge(Tag tag, NbtPathArgument.NbtPath nbtPathArgument$NbtPath, List list) throws CommandSyntaxException {
            Tag var10002 = (Tag)Iterables.getLast(list);
            nbtPathArgument$NbtPath.set(tag, var10002::copy);
         }
      },
      APPEND("append") {
         public void merge(Tag tag, NbtPathArgument.NbtPath nbtPathArgument$NbtPath, List list) throws CommandSyntaxException {
            List<Tag> list = nbtPathArgument$NbtPath.getOrCreate(tag, ListTag::<init>);
            list.forEach((tag) -> {
               if(tag instanceof ListTag) {
                  list.forEach((var1) -> {
                     ((ListTag)tag).add(var1.copy());
                  });
               }

            });
         }
      },
      MERGE("merge") {
         public void merge(Tag tag, NbtPathArgument.NbtPath nbtPathArgument$NbtPath, List list) throws CommandSyntaxException {
            List<Tag> list = nbtPathArgument$NbtPath.getOrCreate(tag, CompoundTag::<init>);
            list.forEach((tag) -> {
               if(tag instanceof CompoundTag) {
                  list.forEach((var1) -> {
                     if(var1 instanceof CompoundTag) {
                        ((CompoundTag)tag).merge((CompoundTag)var1);
                     }

                  });
               }

            });
         }
      };

      private final String name;

      public abstract void merge(Tag var1, NbtPathArgument.NbtPath var2, List var3) throws CommandSyntaxException;

      private MergeStrategy(String name) {
         this.name = name;
      }

      public static CopyNbtFunction.MergeStrategy getByName(String name) {
         for(CopyNbtFunction.MergeStrategy var4 : values()) {
            if(var4.name.equals(name)) {
               return var4;
            }
         }

         throw new IllegalArgumentException("Invalid merge strategy" + name);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      public Serializer() {
         super(new ResourceLocation("copy_nbt"), CopyNbtFunction.class);
      }

      public void serialize(JsonObject jsonObject, CopyNbtFunction copyNbtFunction, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)copyNbtFunction, jsonSerializationContext);
         jsonObject.addProperty("source", copyNbtFunction.source.name);
         JsonArray var4 = new JsonArray();
         copyNbtFunction.operations.stream().map(CopyNbtFunction.CopyOperation::toJson).forEach(var4::add);
         jsonObject.add("ops", var4);
      }

      public CopyNbtFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         CopyNbtFunction.DataSource var4 = CopyNbtFunction.DataSource.getByName(GsonHelper.getAsString(jsonObject, "source"));
         List<CopyNbtFunction.CopyOperation> var5 = Lists.newArrayList();

         for(JsonElement var8 : GsonHelper.getAsJsonArray(jsonObject, "ops")) {
            JsonObject var9 = GsonHelper.convertToJsonObject(var8, "op");
            var5.add(CopyNbtFunction.CopyOperation.fromJson(var9));
         }

         return new CopyNbtFunction(lootItemConditions, var4, var5);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
