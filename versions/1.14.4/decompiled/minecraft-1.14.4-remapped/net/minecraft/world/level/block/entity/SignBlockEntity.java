package net.minecraft.world.level.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class SignBlockEntity extends BlockEntity {
   public final Component[] messages = new Component[]{new TextComponent(""), new TextComponent(""), new TextComponent(""), new TextComponent("")};
   private boolean showCursor;
   private int selectedLine = -1;
   private int cursorPos = -1;
   private int selectionPos = -1;
   private boolean isEditable = true;
   private Player playerWhoMayEdit;
   private final String[] renderMessages = new String[4];
   private DyeColor color = DyeColor.BLACK;

   public SignBlockEntity() {
      super(BlockEntityType.SIGN);
   }

   public CompoundTag save(CompoundTag compoundTag) {
      super.save(compoundTag);

      for(int var2 = 0; var2 < 4; ++var2) {
         String var3 = Component.Serializer.toJson(this.messages[var2]);
         compoundTag.putString("Text" + (var2 + 1), var3);
      }

      compoundTag.putString("Color", this.color.getName());
      return compoundTag;
   }

   public void load(CompoundTag compoundTag) {
      this.isEditable = false;
      super.load(compoundTag);
      this.color = DyeColor.byName(compoundTag.getString("Color"), DyeColor.BLACK);

      for(int var2 = 0; var2 < 4; ++var2) {
         String var3 = compoundTag.getString("Text" + (var2 + 1));
         Component var4 = Component.Serializer.fromJson(var3.isEmpty()?"\"\"":var3);
         if(this.level instanceof ServerLevel) {
            try {
               this.messages[var2] = ComponentUtils.updateForEntity(this.createCommandSourceStack((ServerPlayer)null), var4, (Entity)null, 0);
            } catch (CommandSyntaxException var6) {
               this.messages[var2] = var4;
            }
         } else {
            this.messages[var2] = var4;
         }

         this.renderMessages[var2] = null;
      }

   }

   public Component getMessage(int i) {
      return this.messages[i];
   }

   public void setMessage(int var1, Component component) {
      this.messages[var1] = component;
      this.renderMessages[var1] = null;
   }

   @Nullable
   public String getRenderMessage(int var1, Function function) {
      if(this.renderMessages[var1] == null && this.messages[var1] != null) {
         this.renderMessages[var1] = (String)function.apply(this.messages[var1]);
      }

      return this.renderMessages[var1];
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 9, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public boolean isEditable() {
      return this.isEditable;
   }

   public void setEditable(boolean editable) {
      this.isEditable = editable;
      if(!editable) {
         this.playerWhoMayEdit = null;
      }

   }

   public void setAllowedPlayerEditor(Player allowedPlayerEditor) {
      this.playerWhoMayEdit = allowedPlayerEditor;
   }

   public Player getPlayerWhoMayEdit() {
      return this.playerWhoMayEdit;
   }

   public boolean executeClickCommands(Player player) {
      for(Component var5 : this.messages) {
         Style var6 = var5 == null?null:var5.getStyle();
         if(var6 != null && var6.getClickEvent() != null) {
            ClickEvent var7 = var6.getClickEvent();
            if(var7.getAction() == ClickEvent.Action.RUN_COMMAND) {
               player.getServer().getCommands().performCommand(this.createCommandSourceStack((ServerPlayer)player), var7.getValue());
            }
         }
      }

      return true;
   }

   public CommandSourceStack createCommandSourceStack(@Nullable ServerPlayer serverPlayer) {
      String var2 = serverPlayer == null?"Sign":serverPlayer.getName().getString();
      Component var3 = (Component)(serverPlayer == null?new TextComponent("Sign"):serverPlayer.getDisplayName());
      return new CommandSourceStack(CommandSource.NULL, new Vec3((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D), Vec2.ZERO, (ServerLevel)this.level, 2, var2, var3, this.level.getServer(), serverPlayer);
   }

   public DyeColor getColor() {
      return this.color;
   }

   public boolean setColor(DyeColor color) {
      if(color != this.getColor()) {
         this.color = color;
         this.setChanged();
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
         return true;
      } else {
         return false;
      }
   }

   public void setCursorInfo(int selectedLine, int cursorPos, int selectionPos, boolean showCursor) {
      this.selectedLine = selectedLine;
      this.cursorPos = cursorPos;
      this.selectionPos = selectionPos;
      this.showCursor = showCursor;
   }

   public void resetCursorInfo() {
      this.selectedLine = -1;
      this.cursorPos = -1;
      this.selectionPos = -1;
      this.showCursor = false;
   }

   public boolean isShowCursor() {
      return this.showCursor;
   }

   public int getSelectedLine() {
      return this.selectedLine;
   }

   public int getCursorPos() {
      return this.cursorPos;
   }

   public int getSelectionPos() {
      return this.selectionPos;
   }
}
