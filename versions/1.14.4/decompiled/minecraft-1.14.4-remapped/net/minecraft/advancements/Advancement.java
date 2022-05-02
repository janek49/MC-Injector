package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.ArrayUtils;

public class Advancement {
   private final Advancement parent;
   private final DisplayInfo display;
   private final AdvancementRewards rewards;
   private final ResourceLocation id;
   private final Map criteria;
   private final String[][] requirements;
   private final Set children = Sets.newLinkedHashSet();
   private final Component chatComponent;

   public Advancement(ResourceLocation id, @Nullable Advancement parent, @Nullable DisplayInfo display, AdvancementRewards rewards, Map map, String[][] requirements) {
      this.id = id;
      this.display = display;
      this.criteria = ImmutableMap.copyOf(map);
      this.parent = parent;
      this.rewards = rewards;
      this.requirements = requirements;
      if(parent != null) {
         parent.addChild(this);
      }

      if(display == null) {
         this.chatComponent = new TextComponent(id.toString());
      } else {
         Component var7 = display.getTitle();
         ChatFormatting var8 = display.getFrame().getChatColor();
         Component var9 = var7.deepCopy().withStyle(var8).append("\n").append(display.getDescription());
         Component var10 = var7.deepCopy().withStyle((style) -> {
            style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var9));
         });
         this.chatComponent = (new TextComponent("[")).append(var10).append("]").withStyle(var8);
      }

   }

   public Advancement.Builder deconstruct() {
      return new Advancement.Builder(this.parent == null?null:this.parent.getId(), this.display, this.rewards, this.criteria, this.requirements);
   }

   @Nullable
   public Advancement getParent() {
      return this.parent;
   }

   @Nullable
   public DisplayInfo getDisplay() {
      return this.display;
   }

   public AdvancementRewards getRewards() {
      return this.rewards;
   }

   public String toString() {
      return "SimpleAdvancement{id=" + this.getId() + ", parent=" + (this.parent == null?"null":this.parent.getId()) + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
   }

   public Iterable getChildren() {
      return this.children;
   }

   public Map getCriteria() {
      return this.criteria;
   }

   public int getMaxCriteraRequired() {
      return this.requirements.length;
   }

   public void addChild(Advancement advancement) {
      this.children.add(advancement);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof Advancement)) {
         return false;
      } else {
         Advancement var2 = (Advancement)object;
         return this.id.equals(var2.id);
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public String[][] getRequirements() {
      return this.requirements;
   }

   public Component getChatComponent() {
      return this.chatComponent;
   }

   public static class Builder {
      private ResourceLocation parentId;
      private Advancement parent;
      private DisplayInfo display;
      private AdvancementRewards rewards;
      private Map criteria;
      private String[][] requirements;
      private RequirementsStrategy requirementsStrategy;

      private Builder(@Nullable ResourceLocation parentId, @Nullable DisplayInfo display, AdvancementRewards rewards, Map criteria, String[][] requirements) {
         this.rewards = AdvancementRewards.EMPTY;
         this.criteria = Maps.newLinkedHashMap();
         this.requirementsStrategy = RequirementsStrategy.AND;
         this.parentId = parentId;
         this.display = display;
         this.rewards = rewards;
         this.criteria = criteria;
         this.requirements = requirements;
      }

      private Builder() {
         this.rewards = AdvancementRewards.EMPTY;
         this.criteria = Maps.newLinkedHashMap();
         this.requirementsStrategy = RequirementsStrategy.AND;
      }

      public static Advancement.Builder advancement() {
         return new Advancement.Builder();
      }

      public Advancement.Builder parent(Advancement parent) {
         this.parent = parent;
         return this;
      }

      public Advancement.Builder parent(ResourceLocation parentId) {
         this.parentId = parentId;
         return this;
      }

      public Advancement.Builder display(ItemStack itemStack, Component var2, Component var3, @Nullable ResourceLocation resourceLocation, FrameType frameType, boolean var6, boolean var7, boolean var8) {
         return this.display(new DisplayInfo(itemStack, var2, var3, resourceLocation, frameType, var6, var7, var8));
      }

      public Advancement.Builder display(ItemLike itemLike, Component var2, Component var3, @Nullable ResourceLocation resourceLocation, FrameType frameType, boolean var6, boolean var7, boolean var8) {
         return this.display(new DisplayInfo(new ItemStack(itemLike.asItem()), var2, var3, resourceLocation, frameType, var6, var7, var8));
      }

      public Advancement.Builder display(DisplayInfo display) {
         this.display = display;
         return this;
      }

      public Advancement.Builder rewards(AdvancementRewards.Builder advancementRewards$Builder) {
         return this.rewards(advancementRewards$Builder.build());
      }

      public Advancement.Builder rewards(AdvancementRewards rewards) {
         this.rewards = rewards;
         return this;
      }

      public Advancement.Builder addCriterion(String string, CriterionTriggerInstance criterionTriggerInstance) {
         return this.addCriterion(string, new Criterion(criterionTriggerInstance));
      }

      public Advancement.Builder addCriterion(String string, Criterion criterion) {
         if(this.criteria.containsKey(string)) {
            throw new IllegalArgumentException("Duplicate criterion " + string);
         } else {
            this.criteria.put(string, criterion);
            return this;
         }
      }

      public Advancement.Builder requirements(RequirementsStrategy requirementsStrategy) {
         this.requirementsStrategy = requirementsStrategy;
         return this;
      }

      public boolean canBuild(Function function) {
         if(this.parentId == null) {
            return true;
         } else {
            if(this.parent == null) {
               this.parent = (Advancement)function.apply(this.parentId);
            }

            return this.parent != null;
         }
      }

      public Advancement build(ResourceLocation resourceLocation) {
         if(!this.canBuild((resourceLocation) -> {
            return null;
         })) {
            throw new IllegalStateException("Tried to build incomplete advancement!");
         } else {
            if(this.requirements == null) {
               this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
            }

            return new Advancement(resourceLocation, this.parent, this.display, this.rewards, this.criteria, this.requirements);
         }
      }

      public Advancement save(Consumer consumer, String string) {
         Advancement advancement = this.build(new ResourceLocation(string));
         consumer.accept(advancement);
         return advancement;
      }

      public JsonObject serializeToJson() {
         if(this.requirements == null) {
            this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
         }

         JsonObject jsonObject = new JsonObject();
         if(this.parent != null) {
            jsonObject.addProperty("parent", this.parent.getId().toString());
         } else if(this.parentId != null) {
            jsonObject.addProperty("parent", this.parentId.toString());
         }

         if(this.display != null) {
            jsonObject.add("display", this.display.serializeToJson());
         }

         jsonObject.add("rewards", this.rewards.serializeToJson());
         JsonObject var2 = new JsonObject();

         for(Entry<String, Criterion> var4 : this.criteria.entrySet()) {
            var2.add((String)var4.getKey(), ((Criterion)var4.getValue()).serializeToJson());
         }

         jsonObject.add("criteria", var2);
         JsonArray var3 = new JsonArray();

         for(String[] vars7 : this.requirements) {
            JsonArray var8 = new JsonArray();

            for(String var12 : vars7) {
               var8.add(var12);
            }

            var3.add(var8);
         }

         jsonObject.add("requirements", var3);
         return jsonObject;
      }

      public void serializeToNetwork(FriendlyByteBuf friendlyByteBuf) {
         if(this.parentId == null) {
            friendlyByteBuf.writeBoolean(false);
         } else {
            friendlyByteBuf.writeBoolean(true);
            friendlyByteBuf.writeResourceLocation(this.parentId);
         }

         if(this.display == null) {
            friendlyByteBuf.writeBoolean(false);
         } else {
            friendlyByteBuf.writeBoolean(true);
            this.display.serializeToNetwork(friendlyByteBuf);
         }

         Criterion.serializeToNetwork(this.criteria, friendlyByteBuf);
         friendlyByteBuf.writeVarInt(this.requirements.length);

         for(String[] vars5 : this.requirements) {
            friendlyByteBuf.writeVarInt(vars5.length);

            for(String var9 : vars5) {
               friendlyByteBuf.writeUtf(var9);
            }
         }

      }

      public String toString() {
         return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
      }

      public static Advancement.Builder fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         ResourceLocation var2 = jsonObject.has("parent")?new ResourceLocation(GsonHelper.getAsString(jsonObject, "parent")):null;
         DisplayInfo var3 = jsonObject.has("display")?DisplayInfo.fromJson(GsonHelper.getAsJsonObject(jsonObject, "display"), jsonDeserializationContext):null;
         AdvancementRewards var4 = (AdvancementRewards)GsonHelper.getAsObject(jsonObject, "rewards", AdvancementRewards.EMPTY, jsonDeserializationContext, AdvancementRewards.class);
         Map<String, Criterion> var5 = Criterion.criteriaFromJson(GsonHelper.getAsJsonObject(jsonObject, "criteria"), jsonDeserializationContext);
         if(var5.isEmpty()) {
            throw new JsonSyntaxException("Advancement criteria cannot be empty");
         } else {
            JsonArray var6 = GsonHelper.getAsJsonArray(jsonObject, "requirements", new JsonArray());
            String[][] vars7 = new String[var6.size()][];

            for(int var8 = 0; var8 < var6.size(); ++var8) {
               JsonArray var9 = GsonHelper.convertToJsonArray(var6.get(var8), "requirements[" + var8 + "]");
               vars7[var8] = new String[var9.size()];

               for(int var10 = 0; var10 < var9.size(); ++var10) {
                  vars7[var8][var10] = GsonHelper.convertToString(var9.get(var10), "requirements[" + var8 + "][" + var10 + "]");
               }
            }

            if(vars7.length == 0) {
               vars7 = new String[var5.size()][];
               int var8 = 0;

               for(String var10 : var5.keySet()) {
                  vars7[var8++] = new String[]{var10};
               }
            }

            for(String[] vars11 : vars7) {
               if(vars11.length == 0 && var5.isEmpty()) {
                  throw new JsonSyntaxException("Requirement entry cannot be empty");
               }

               for(String var15 : vars11) {
                  if(!var5.containsKey(var15)) {
                     throw new JsonSyntaxException("Unknown required criterion \'" + var15 + "\'");
                  }
               }
            }

            for(String var9 : var5.keySet()) {
               boolean var10 = false;

               for(String[] vars14 : vars7) {
                  if(ArrayUtils.contains(vars14, var9)) {
                     var10 = true;
                     break;
                  }
               }

               if(!var10) {
                  throw new JsonSyntaxException("Criterion \'" + var9 + "\' isn\'t a requirement for completion. This isn\'t supported behaviour, all criteria must be required.");
               }
            }

            return new Advancement.Builder(var2, var3, var4, var5, vars7);
         }
      }

      public static Advancement.Builder fromNetwork(FriendlyByteBuf network) {
         ResourceLocation var1 = network.readBoolean()?network.readResourceLocation():null;
         DisplayInfo var2 = network.readBoolean()?DisplayInfo.fromNetwork(network):null;
         Map<String, Criterion> var3 = Criterion.criteriaFromNetwork(network);
         String[][] vars4 = new String[network.readVarInt()][];

         for(int var5 = 0; var5 < vars4.length; ++var5) {
            vars4[var5] = new String[network.readVarInt()];

            for(int var6 = 0; var6 < vars4[var5].length; ++var6) {
               vars4[var5][var6] = network.readUtf(32767);
            }
         }

         return new Advancement.Builder(var1, var2, AdvancementRewards.EMPTY, var3, vars4);
      }

      public Map getCriteria() {
         return this.criteria;
      }
   }
}
