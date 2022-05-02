package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public class ClientboundRecipePacket implements Packet {
   private ClientboundRecipePacket.State state;
   private List recipes;
   private List toHighlight;
   private boolean guiOpen;
   private boolean filteringCraftable;
   private boolean furnaceGuiOpen;
   private boolean furnaceFilteringCraftable;

   public ClientboundRecipePacket() {
   }

   public ClientboundRecipePacket(ClientboundRecipePacket.State state, Collection var2, Collection var3, boolean guiOpen, boolean filteringCraftable, boolean furnaceGuiOpen, boolean furnaceFilteringCraftable) {
      this.state = state;
      this.recipes = ImmutableList.copyOf(var2);
      this.toHighlight = ImmutableList.copyOf(var3);
      this.guiOpen = guiOpen;
      this.filteringCraftable = filteringCraftable;
      this.furnaceGuiOpen = furnaceGuiOpen;
      this.furnaceFilteringCraftable = furnaceFilteringCraftable;
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleAddOrRemoveRecipes(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.state = (ClientboundRecipePacket.State)friendlyByteBuf.readEnum(ClientboundRecipePacket.State.class);
      this.guiOpen = friendlyByteBuf.readBoolean();
      this.filteringCraftable = friendlyByteBuf.readBoolean();
      this.furnaceGuiOpen = friendlyByteBuf.readBoolean();
      this.furnaceFilteringCraftable = friendlyByteBuf.readBoolean();
      int var2 = friendlyByteBuf.readVarInt();
      this.recipes = Lists.newArrayList();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.recipes.add(friendlyByteBuf.readResourceLocation());
      }

      if(this.state == ClientboundRecipePacket.State.INIT) {
         var2 = friendlyByteBuf.readVarInt();
         this.toHighlight = Lists.newArrayList();

         for(int var3 = 0; var3 < var2; ++var3) {
            this.toHighlight.add(friendlyByteBuf.readResourceLocation());
         }
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeEnum(this.state);
      friendlyByteBuf.writeBoolean(this.guiOpen);
      friendlyByteBuf.writeBoolean(this.filteringCraftable);
      friendlyByteBuf.writeBoolean(this.furnaceGuiOpen);
      friendlyByteBuf.writeBoolean(this.furnaceFilteringCraftable);
      friendlyByteBuf.writeVarInt(this.recipes.size());

      for(ResourceLocation var3 : this.recipes) {
         friendlyByteBuf.writeResourceLocation(var3);
      }

      if(this.state == ClientboundRecipePacket.State.INIT) {
         friendlyByteBuf.writeVarInt(this.toHighlight.size());

         for(ResourceLocation var3 : this.toHighlight) {
            friendlyByteBuf.writeResourceLocation(var3);
         }
      }

   }

   public List getRecipes() {
      return this.recipes;
   }

   public List getHighlights() {
      return this.toHighlight;
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

   public ClientboundRecipePacket.State getState() {
      return this.state;
   }

   public static enum State {
      INIT,
      ADD,
      REMOVE;
   }
}
