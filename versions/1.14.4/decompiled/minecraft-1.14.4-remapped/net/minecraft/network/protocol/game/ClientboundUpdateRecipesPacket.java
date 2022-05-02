package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ClientboundUpdateRecipesPacket implements Packet {
   private List recipes;

   public ClientboundUpdateRecipesPacket() {
   }

   public ClientboundUpdateRecipesPacket(Collection collection) {
      this.recipes = Lists.newArrayList(collection);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleUpdateRecipes(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.recipes = Lists.newArrayList();
      int var2 = friendlyByteBuf.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.recipes.add(fromNetwork(friendlyByteBuf));
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.recipes.size());

      for(Recipe<?> var3 : this.recipes) {
         toNetwork(var3, friendlyByteBuf);
      }

   }

   public List getRecipes() {
      return this.recipes;
   }

   public static Recipe fromNetwork(FriendlyByteBuf network) {
      ResourceLocation var1 = network.readResourceLocation();
      ResourceLocation var2 = network.readResourceLocation();
      return ((RecipeSerializer)Registry.RECIPE_SERIALIZER.getOptional(var1).orElseThrow(() -> {
         return new IllegalArgumentException("Unknown recipe serializer " + var1);
      })).fromNetwork(var2, network);
   }

   public static void toNetwork(Recipe recipe, FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.writeResourceLocation(Registry.RECIPE_SERIALIZER.getKey(recipe.getSerializer()));
      friendlyByteBuf.writeResourceLocation(recipe.getId());
      recipe.getSerializer().toNetwork(friendlyByteBuf, recipe);
   }
}
