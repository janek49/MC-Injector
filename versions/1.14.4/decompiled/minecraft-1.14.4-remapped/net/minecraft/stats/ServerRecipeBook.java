package net.minecraft.stats;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.ResourceLocationException;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipeBook extends RecipeBook {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RecipeManager manager;

   public ServerRecipeBook(RecipeManager manager) {
      this.manager = manager;
   }

   public int addRecipes(Collection collection, ServerPlayer serverPlayer) {
      List<ResourceLocation> var3 = Lists.newArrayList();
      int var4 = 0;

      for(Recipe<?> var6 : collection) {
         ResourceLocation var7 = var6.getId();
         if(!this.known.contains(var7) && !var6.isSpecial()) {
            this.add(var7);
            this.addHighlight(var7);
            var3.add(var7);
            CriteriaTriggers.RECIPE_UNLOCKED.trigger(serverPlayer, var6);
            ++var4;
         }
      }

      this.sendRecipes(ClientboundRecipePacket.State.ADD, serverPlayer, var3);
      return var4;
   }

   public int removeRecipes(Collection collection, ServerPlayer serverPlayer) {
      List<ResourceLocation> var3 = Lists.newArrayList();
      int var4 = 0;

      for(Recipe<?> var6 : collection) {
         ResourceLocation var7 = var6.getId();
         if(this.known.contains(var7)) {
            this.remove(var7);
            var3.add(var7);
            ++var4;
         }
      }

      this.sendRecipes(ClientboundRecipePacket.State.REMOVE, serverPlayer, var3);
      return var4;
   }

   private void sendRecipes(ClientboundRecipePacket.State clientboundRecipePacket$State, ServerPlayer serverPlayer, List list) {
      serverPlayer.connection.send(new ClientboundRecipePacket(clientboundRecipePacket$State, list, Collections.emptyList(), this.guiOpen, this.filteringCraftable, this.furnaceGuiOpen, this.furnaceFilteringCraftable));
   }

   public CompoundTag toNbt() {
      CompoundTag compoundTag = new CompoundTag();
      compoundTag.putBoolean("isGuiOpen", this.guiOpen);
      compoundTag.putBoolean("isFilteringCraftable", this.filteringCraftable);
      compoundTag.putBoolean("isFurnaceGuiOpen", this.furnaceGuiOpen);
      compoundTag.putBoolean("isFurnaceFilteringCraftable", this.furnaceFilteringCraftable);
      ListTag var2 = new ListTag();

      for(ResourceLocation var4 : this.known) {
         var2.add(new StringTag(var4.toString()));
      }

      compoundTag.put("recipes", var2);
      ListTag var3 = new ListTag();

      for(ResourceLocation var5 : this.highlight) {
         var3.add(new StringTag(var5.toString()));
      }

      compoundTag.put("toBeDisplayed", var3);
      return compoundTag;
   }

   public void fromNbt(CompoundTag nbt) {
      this.guiOpen = nbt.getBoolean("isGuiOpen");
      this.filteringCraftable = nbt.getBoolean("isFilteringCraftable");
      this.furnaceGuiOpen = nbt.getBoolean("isFurnaceGuiOpen");
      this.furnaceFilteringCraftable = nbt.getBoolean("isFurnaceFilteringCraftable");
      ListTag var2 = nbt.getList("recipes", 8);
      this.loadRecipes(var2, this::add);
      ListTag var3 = nbt.getList("toBeDisplayed", 8);
      this.loadRecipes(var3, this::addHighlight);
   }

   private void loadRecipes(ListTag listTag, Consumer consumer) {
      for(int var3 = 0; var3 < listTag.size(); ++var3) {
         String var4 = listTag.getString(var3);

         try {
            ResourceLocation var5 = new ResourceLocation(var4);
            Optional<? extends Recipe<?>> var6 = this.manager.byKey(var5);
            if(!var6.isPresent()) {
               LOGGER.error("Tried to load unrecognized recipe: {} removed now.", var5);
            } else {
               consumer.accept(var6.get());
            }
         } catch (ResourceLocationException var7) {
            LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", var4);
         }
      }

   }

   public void sendInitialRecipeBook(ServerPlayer serverPlayer) {
      serverPlayer.connection.send(new ClientboundRecipePacket(ClientboundRecipePacket.State.INIT, this.known, this.highlight, this.guiOpen, this.filteringCraftable, this.furnaceGuiOpen, this.furnaceFilteringCraftable));
   }
}
