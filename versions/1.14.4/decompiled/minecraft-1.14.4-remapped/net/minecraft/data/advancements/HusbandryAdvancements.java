package net.minecraft.data.advancements;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.FishingRodHookedTrigger;
import net.minecraft.advancements.critereon.ItemDurabilityTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class HusbandryAdvancements implements Consumer {
   private static final EntityType[] BREEDABLE_ANIMALS = new EntityType[]{EntityType.HORSE, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.CHICKEN, EntityType.WOLF, EntityType.OCELOT, EntityType.RABBIT, EntityType.LLAMA, EntityType.TURTLE, EntityType.CAT, EntityType.PANDA, EntityType.FOX};
   private static final Item[] FISH = new Item[]{Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
   private static final Item[] FISH_BUCKETS = new Item[]{Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
   private static final Item[] EDIBLE_ITEMS = new Item[]{Items.APPLE, Items.MUSHROOM_STEW, Items.BREAD, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKIE, Items.MELON_SLICE, Items.BEEF, Items.COOKED_BEEF, Items.CHICKEN, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.CARROT, Items.POTATO, Items.BAKED_POTATO, Items.POISONOUS_POTATO, Items.GOLDEN_CARROT, Items.PUMPKIN_PIE, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUTTON, Items.COOKED_MUTTON, Items.CHORUS_FRUIT, Items.BEETROOT, Items.BEETROOT_SOUP, Items.DRIED_KELP, Items.SUSPICIOUS_STEW, Items.SWEET_BERRIES};

   public void accept(Consumer consumer) {
      Advancement var2 = Advancement.Builder.advancement().display((ItemLike)Blocks.HAY_BLOCK, new TranslatableComponent("advancements.husbandry.root.title", new Object[0]), new TranslatableComponent("advancements.husbandry.root.description", new Object[0]), new ResourceLocation("textures/gui/advancements/backgrounds/husbandry.png"), FrameType.TASK, false, false, false).addCriterion("consumed_item", (CriterionTriggerInstance)ConsumeItemTrigger.TriggerInstance.usedItem()).save(consumer, "husbandry/root");
      Advancement var3 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.WHEAT, new TranslatableComponent("advancements.husbandry.plant_seed.title", new Object[0]), new TranslatableComponent("advancements.husbandry.plant_seed.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("wheat", (CriterionTriggerInstance)PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.WHEAT)).addCriterion("pumpkin_stem", (CriterionTriggerInstance)PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.PUMPKIN_STEM)).addCriterion("melon_stem", (CriterionTriggerInstance)PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.MELON_STEM)).addCriterion("beetroots", (CriterionTriggerInstance)PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.BEETROOTS)).addCriterion("nether_wart", (CriterionTriggerInstance)PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.NETHER_WART)).save(consumer, "husbandry/plant_seed");
      Advancement var4 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.WHEAT, new TranslatableComponent("advancements.husbandry.breed_an_animal.title", new Object[0]), new TranslatableComponent("advancements.husbandry.breed_an_animal.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("bred", (CriterionTriggerInstance)BredAnimalsTrigger.TriggerInstance.bredAnimals()).save(consumer, "husbandry/breed_an_animal");
      Advancement var5 = this.addFood(Advancement.Builder.advancement()).parent(var3).display((ItemLike)Items.APPLE, new TranslatableComponent("advancements.husbandry.balanced_diet.title", new Object[0]), new TranslatableComponent("advancements.husbandry.balanced_diet.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(consumer, "husbandry/balanced_diet");
      Advancement var6 = Advancement.Builder.advancement().parent(var3).display((ItemLike)Items.DIAMOND_HOE, new TranslatableComponent("advancements.husbandry.break_diamond_hoe.title", new Object[0]), new TranslatableComponent("advancements.husbandry.break_diamond_hoe.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("broke_hoe", (CriterionTriggerInstance)ItemDurabilityTrigger.TriggerInstance.changedDurability(ItemPredicate.Builder.item().of((ItemLike)Items.DIAMOND_HOE).build(), MinMaxBounds.Ints.exactly(0))).save(consumer, "husbandry/break_diamond_hoe");
      Advancement var7 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.LEAD, new TranslatableComponent("advancements.husbandry.tame_an_animal.title", new Object[0]), new TranslatableComponent("advancements.husbandry.tame_an_animal.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("tamed_animal", (CriterionTriggerInstance)TameAnimalTrigger.TriggerInstance.tamedAnimal()).save(consumer, "husbandry/tame_an_animal");
      Advancement var8 = this.addBreedable(Advancement.Builder.advancement()).parent(var4).display((ItemLike)Items.GOLDEN_CARROT, new TranslatableComponent("advancements.husbandry.breed_all_animals.title", new Object[0]), new TranslatableComponent("advancements.husbandry.breed_all_animals.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(consumer, "husbandry/bred_all_animals");
      Advancement var9 = this.addFish(Advancement.Builder.advancement()).parent(var2).requirements(RequirementsStrategy.OR).display((ItemLike)Items.FISHING_ROD, new TranslatableComponent("advancements.husbandry.fishy_business.title", new Object[0]), new TranslatableComponent("advancements.husbandry.fishy_business.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).save(consumer, "husbandry/fishy_business");
      Advancement var10 = this.addFishBuckets(Advancement.Builder.advancement()).parent(var9).requirements(RequirementsStrategy.OR).display((ItemLike)Items.PUFFERFISH_BUCKET, new TranslatableComponent("advancements.husbandry.tactical_fishing.title", new Object[0]), new TranslatableComponent("advancements.husbandry.tactical_fishing.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).save(consumer, "husbandry/tactical_fishing");
      Advancement var11 = this.addCatVariants(Advancement.Builder.advancement()).parent(var7).display((ItemLike)Items.COD, new TranslatableComponent("advancements.husbandry.complete_catalogue.title", new Object[0]), new TranslatableComponent("advancements.husbandry.complete_catalogue.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).save(consumer, "husbandry/complete_catalogue");
   }

   private Advancement.Builder addFood(Advancement.Builder advancement$Builder) {
      for(Item var5 : EDIBLE_ITEMS) {
         advancement$Builder.addCriterion(Registry.ITEM.getKey(var5).getPath(), (CriterionTriggerInstance)ConsumeItemTrigger.TriggerInstance.usedItem(var5));
      }

      return advancement$Builder;
   }

   private Advancement.Builder addBreedable(Advancement.Builder advancement$Builder) {
      for(EntityType<?> var5 : BREEDABLE_ANIMALS) {
         advancement$Builder.addCriterion(EntityType.getKey(var5).toString(), (CriterionTriggerInstance)BredAnimalsTrigger.TriggerInstance.bredAnimals(EntityPredicate.Builder.entity().of(var5)));
      }

      return advancement$Builder;
   }

   private Advancement.Builder addFishBuckets(Advancement.Builder advancement$Builder) {
      for(Item var5 : FISH_BUCKETS) {
         advancement$Builder.addCriterion(Registry.ITEM.getKey(var5).getPath(), (CriterionTriggerInstance)FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of((ItemLike)var5).build()));
      }

      return advancement$Builder;
   }

   private Advancement.Builder addFish(Advancement.Builder advancement$Builder) {
      for(Item var5 : FISH) {
         advancement$Builder.addCriterion(Registry.ITEM.getKey(var5).getPath(), (CriterionTriggerInstance)FishingRodHookedTrigger.TriggerInstance.fishedItem(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.item().of((ItemLike)var5).build()));
      }

      return advancement$Builder;
   }

   private Advancement.Builder addCatVariants(Advancement.Builder advancement$Builder) {
      Cat.TEXTURE_BY_TYPE.forEach((integer, resourceLocation) -> {
         advancement$Builder.addCriterion(resourceLocation.getPath(), (CriterionTriggerInstance)TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().of(resourceLocation).build()));
      });
      return advancement$Builder;
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((Consumer)var1);
   }
}
