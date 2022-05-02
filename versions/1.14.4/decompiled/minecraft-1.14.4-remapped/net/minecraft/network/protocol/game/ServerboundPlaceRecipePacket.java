package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public class ServerboundPlaceRecipePacket implements Packet {
   private int containerId;
   private ResourceLocation recipe;
   private boolean shiftDown;

   public ServerboundPlaceRecipePacket() {
   }

   public ServerboundPlaceRecipePacket(int containerId, Recipe recipe, boolean shiftDown) {
      this.containerId = containerId;
      this.recipe = recipe.getId();
      this.shiftDown = shiftDown;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.containerId = friendlyByteBuf.readByte();
      this.recipe = friendlyByteBuf.readResourceLocation();
      this.shiftDown = friendlyByteBuf.readBoolean();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.containerId);
      friendlyByteBuf.writeResourceLocation(this.recipe);
      friendlyByteBuf.writeBoolean(this.shiftDown);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handlePlaceRecipe(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public ResourceLocation getRecipe() {
      return this.recipe;
   }

   public boolean isShiftDown() {
      return this.shiftDown;
   }
}
