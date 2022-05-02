package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public class ServerboundRecipeBookUpdatePacket implements Packet {
   private ServerboundRecipeBookUpdatePacket.Purpose purpose;
   private ResourceLocation recipe;
   private boolean guiOpen;
   private boolean filteringCraftable;
   private boolean furnaceGuiOpen;
   private boolean furnaceFilteringCraftable;
   private boolean blastFurnaceGuiOpen;
   private boolean blastFurnaceFilteringCraftable;
   private boolean smokerGuiOpen;
   private boolean smokerFilteringCraftable;

   public ServerboundRecipeBookUpdatePacket() {
   }

   public ServerboundRecipeBookUpdatePacket(Recipe recipe) {
      this.purpose = ServerboundRecipeBookUpdatePacket.Purpose.SHOWN;
      this.recipe = recipe.getId();
   }

   public ServerboundRecipeBookUpdatePacket(boolean guiOpen, boolean filteringCraftable, boolean furnaceGuiOpen, boolean furnaceFilteringCraftable, boolean blastFurnaceGuiOpen, boolean blastFurnaceFilteringCraftable) {
      this.purpose = ServerboundRecipeBookUpdatePacket.Purpose.SETTINGS;
      this.guiOpen = guiOpen;
      this.filteringCraftable = filteringCraftable;
      this.furnaceGuiOpen = furnaceGuiOpen;
      this.furnaceFilteringCraftable = furnaceFilteringCraftable;
      this.blastFurnaceGuiOpen = blastFurnaceGuiOpen;
      this.blastFurnaceFilteringCraftable = blastFurnaceFilteringCraftable;
      this.smokerGuiOpen = blastFurnaceGuiOpen;
      this.smokerFilteringCraftable = blastFurnaceFilteringCraftable;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.purpose = (ServerboundRecipeBookUpdatePacket.Purpose)friendlyByteBuf.readEnum(ServerboundRecipeBookUpdatePacket.Purpose.class);
      if(this.purpose == ServerboundRecipeBookUpdatePacket.Purpose.SHOWN) {
         this.recipe = friendlyByteBuf.readResourceLocation();
      } else if(this.purpose == ServerboundRecipeBookUpdatePacket.Purpose.SETTINGS) {
         this.guiOpen = friendlyByteBuf.readBoolean();
         this.filteringCraftable = friendlyByteBuf.readBoolean();
         this.furnaceGuiOpen = friendlyByteBuf.readBoolean();
         this.furnaceFilteringCraftable = friendlyByteBuf.readBoolean();
         this.blastFurnaceGuiOpen = friendlyByteBuf.readBoolean();
         this.blastFurnaceFilteringCraftable = friendlyByteBuf.readBoolean();
         this.smokerGuiOpen = friendlyByteBuf.readBoolean();
         this.smokerFilteringCraftable = friendlyByteBuf.readBoolean();
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeEnum(this.purpose);
      if(this.purpose == ServerboundRecipeBookUpdatePacket.Purpose.SHOWN) {
         friendlyByteBuf.writeResourceLocation(this.recipe);
      } else if(this.purpose == ServerboundRecipeBookUpdatePacket.Purpose.SETTINGS) {
         friendlyByteBuf.writeBoolean(this.guiOpen);
         friendlyByteBuf.writeBoolean(this.filteringCraftable);
         friendlyByteBuf.writeBoolean(this.furnaceGuiOpen);
         friendlyByteBuf.writeBoolean(this.furnaceFilteringCraftable);
         friendlyByteBuf.writeBoolean(this.blastFurnaceGuiOpen);
         friendlyByteBuf.writeBoolean(this.blastFurnaceFilteringCraftable);
         friendlyByteBuf.writeBoolean(this.smokerGuiOpen);
         friendlyByteBuf.writeBoolean(this.smokerFilteringCraftable);
      }

   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleRecipeBookUpdatePacket(this);
   }

   public ServerboundRecipeBookUpdatePacket.Purpose getPurpose() {
      return this.purpose;
   }

   public ResourceLocation getRecipe() {
      return this.recipe;
   }

   public boolean isGuiOpen() {
      return this.guiOpen;
   }

   public boolean isFilteringCraftable() {
      return this.filteringCraftable;
   }

   public boolean isFurnaceGuiOpen() {
      return this.furnaceGuiOpen;
   }

   public boolean isFurnaceFilteringCraftable() {
      return this.furnaceFilteringCraftable;
   }

   public boolean isBlastFurnaceGuiOpen() {
      return this.blastFurnaceGuiOpen;
   }

   public boolean isBlastFurnaceFilteringCraftable() {
      return this.blastFurnaceFilteringCraftable;
   }

   public boolean isSmokerGuiOpen() {
      return this.smokerGuiOpen;
   }

   public boolean isSmokerFilteringCraftable() {
      return this.smokerFilteringCraftable;
   }

   public static enum Purpose {
      SHOWN,
      SETTINGS;
   }
}
