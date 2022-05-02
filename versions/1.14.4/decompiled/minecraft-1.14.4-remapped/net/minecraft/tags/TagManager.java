package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.SynchronizableTagCollection;
import net.minecraft.tags.Tag;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class TagManager implements PreparableReloadListener {
   private final SynchronizableTagCollection blocks = new SynchronizableTagCollection(Registry.BLOCK, "tags/blocks", "block");
   private final SynchronizableTagCollection items = new SynchronizableTagCollection(Registry.ITEM, "tags/items", "item");
   private final SynchronizableTagCollection fluids = new SynchronizableTagCollection(Registry.FLUID, "tags/fluids", "fluid");
   private final SynchronizableTagCollection entityTypes = new SynchronizableTagCollection(Registry.ENTITY_TYPE, "tags/entity_types", "entity_type");

   public SynchronizableTagCollection getBlocks() {
      return this.blocks;
   }

   public SynchronizableTagCollection getItems() {
      return this.items;
   }

   public SynchronizableTagCollection getFluids() {
      return this.fluids;
   }

   public SynchronizableTagCollection getEntityTypes() {
      return this.entityTypes;
   }

   public void serializeToNetwork(FriendlyByteBuf friendlyByteBuf) {
      this.blocks.serializeToNetwork(friendlyByteBuf);
      this.items.serializeToNetwork(friendlyByteBuf);
      this.fluids.serializeToNetwork(friendlyByteBuf);
      this.entityTypes.serializeToNetwork(friendlyByteBuf);
   }

   public static TagManager deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
      TagManager tagManager = new TagManager();
      tagManager.getBlocks().loadFromNetwork(friendlyByteBuf);
      tagManager.getItems().loadFromNetwork(friendlyByteBuf);
      tagManager.getFluids().loadFromNetwork(friendlyByteBuf);
      tagManager.getEntityTypes().loadFromNetwork(friendlyByteBuf);
      return tagManager;
   }

   public CompletableFuture reload(PreparableReloadListener.PreparationBarrier preparableReloadListener$PreparationBarrier, ResourceManager resourceManager, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      CompletableFuture<Map<ResourceLocation, Tag.Builder<Block>>> completableFuture = this.blocks.prepare(resourceManager, var5);
      CompletableFuture<Map<ResourceLocation, Tag.Builder<Item>>> var8 = this.items.prepare(resourceManager, var5);
      CompletableFuture<Map<ResourceLocation, Tag.Builder<Fluid>>> var9 = this.fluids.prepare(resourceManager, var5);
      CompletableFuture<Map<ResourceLocation, Tag.Builder<EntityType<?>>>> var10 = this.entityTypes.prepare(resourceManager, var5);
      CompletableFuture var10000 = completableFuture.thenCombine(var8, Pair::of).thenCombine(var9.thenCombine(var10, Pair::of), (var0, var1) -> {
         return new TagManager.Preparations((Map)var0.getFirst(), (Map)var0.getSecond(), (Map)var1.getFirst(), (Map)var1.getSecond());
      });
      preparableReloadListener$PreparationBarrier.getClass();
      return var10000.thenCompose(preparableReloadListener$PreparationBarrier::wait).thenAcceptAsync((tagManager$Preparations) -> {
         this.blocks.load(tagManager$Preparations.blocks);
         this.items.load(tagManager$Preparations.items);
         this.fluids.load(tagManager$Preparations.fluids);
         this.entityTypes.load(tagManager$Preparations.entityTypes);
         BlockTags.reset(this.blocks);
         ItemTags.reset(this.items);
         FluidTags.reset(this.fluids);
         EntityTypeTags.reset(this.entityTypes);
      }, var6);
   }

   public static class Preparations {
      final Map blocks;
      final Map items;
      final Map fluids;
      final Map entityTypes;

      public Preparations(Map blocks, Map items, Map fluids, Map entityTypes) {
         this.blocks = blocks;
         this.items = items;
         this.fluids = fluids;
         this.entityTypes = entityTypes;
      }
   }
}
