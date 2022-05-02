package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public class ClientboundPlaceGhostRecipePacket implements Packet {
   private int containerId;
   private ResourceLocation recipe;

   public ClientboundPlaceGhostRecipePacket() {
   }

   public ClientboundPlaceGhostRecipePacket(int containerId, Recipe recipe) {
      this.containerId = containerId;
      this.recipe = recipe.getId();
   }

   public ResourceLocation getRecipe() {
      return this.recipe;
   }

   public int getContainerId() {
      return this.containerId;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.containerId = friendlyByteBuf.readByte();
      this.recipe = friendlyByteBuf.readResourceLocation();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.containerId);
      friendlyByteBuf.writeResourceLocation(this.recipe);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handlePlaceRecipe(this);
   }
}
