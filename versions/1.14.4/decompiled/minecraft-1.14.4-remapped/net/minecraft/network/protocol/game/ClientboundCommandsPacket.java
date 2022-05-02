package net.minecraft.network.protocol.game;

import com.google.common.collect.Maps;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundCommandsPacket implements Packet {
   private RootCommandNode root;

   public ClientboundCommandsPacket() {
   }

   public ClientboundCommandsPacket(RootCommandNode root) {
      this.root = root;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      ClientboundCommandsPacket.Entry[] vars2 = new ClientboundCommandsPacket.Entry[friendlyByteBuf.readVarInt()];
      Deque<ClientboundCommandsPacket.Entry> var3 = new ArrayDeque(vars2.length);

      for(int var4 = 0; var4 < vars2.length; ++var4) {
         vars2[var4] = this.readNode(friendlyByteBuf);
         var3.add(vars2[var4]);
      }

      while(!((Deque)var3).isEmpty()) {
         boolean var4 = false;
         Iterator<ClientboundCommandsPacket.Entry> var5 = var3.iterator();

         while(var5.hasNext()) {
            ClientboundCommandsPacket.Entry var6 = (ClientboundCommandsPacket.Entry)var5.next();
            if(var6.build(vars2)) {
               var5.remove();
               var4 = true;
            }
         }

         if(!var4) {
            throw new IllegalStateException("Server sent an impossible command tree");
         }
      }

      this.root = (RootCommandNode)vars2[friendlyByteBuf.readVarInt()].node;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      Map<CommandNode<SharedSuggestionProvider>, Integer> var2 = Maps.newHashMap();
      Deque<CommandNode<SharedSuggestionProvider>> var3 = new ArrayDeque();
      var3.add(this.root);

      while(!((Deque)var3).isEmpty()) {
         CommandNode<SharedSuggestionProvider> var4 = (CommandNode)var3.pollFirst();
         if(!var2.containsKey(var4)) {
            int var5 = var2.size();
            var2.put(var4, Integer.valueOf(var5));
            var3.addAll(var4.getChildren());
            if(var4.getRedirect() != null) {
               var3.add(var4.getRedirect());
            }
         }
      }

      CommandNode<SharedSuggestionProvider>[] vars4 = (CommandNode[])(new CommandNode[var2.size()]);

      for(java.util.Map.Entry<CommandNode<SharedSuggestionProvider>, Integer> var6 : var2.entrySet()) {
         vars4[((Integer)var6.getValue()).intValue()] = (CommandNode)var6.getKey();
      }

      friendlyByteBuf.writeVarInt(vars4.length);

      for(CommandNode<SharedSuggestionProvider> var8 : vars4) {
         this.writeNode(friendlyByteBuf, var8, var2);
      }

      friendlyByteBuf.writeVarInt(((Integer)var2.get(this.root)).intValue());
   }

   private ClientboundCommandsPacket.Entry readNode(FriendlyByteBuf friendlyByteBuf) {
      byte var2 = friendlyByteBuf.readByte();
      int[] vars3 = friendlyByteBuf.readVarIntArray();
      int var4 = (var2 & 8) != 0?friendlyByteBuf.readVarInt():0;
      ArgumentBuilder<SharedSuggestionProvider, ?> var5 = this.createBuilder(friendlyByteBuf, var2);
      return new ClientboundCommandsPacket.Entry(var5, var2, var4, vars3);
   }

   @Nullable
   private ArgumentBuilder createBuilder(FriendlyByteBuf friendlyByteBuf, byte var2) {
      int var3 = var2 & 3;
      if(var3 == 2) {
         String var4 = friendlyByteBuf.readUtf(32767);
         ArgumentType<?> var5 = ArgumentTypes.deserialize(friendlyByteBuf);
         if(var5 == null) {
            return null;
         } else {
            RequiredArgumentBuilder<SharedSuggestionProvider, ?> var6 = RequiredArgumentBuilder.argument(var4, var5);
            if((var2 & 16) != 0) {
               var6.suggests(SuggestionProviders.getProvider(friendlyByteBuf.readResourceLocation()));
            }

            return var6;
         }
      } else {
         return var3 == 1?LiteralArgumentBuilder.literal(friendlyByteBuf.readUtf(32767)):null;
      }
   }

   private void writeNode(FriendlyByteBuf friendlyByteBuf, CommandNode commandNode, Map map) {
      byte var4 = 0;
      if(commandNode.getRedirect() != null) {
         var4 = (byte)(var4 | 8);
      }

      if(commandNode.getCommand() != null) {
         var4 = (byte)(var4 | 4);
      }

      if(commandNode instanceof RootCommandNode) {
         var4 = (byte)(var4 | 0);
      } else if(commandNode instanceof ArgumentCommandNode) {
         var4 = (byte)(var4 | 2);
         if(((ArgumentCommandNode)commandNode).getCustomSuggestions() != null) {
            var4 = (byte)(var4 | 16);
         }
      } else {
         if(!(commandNode instanceof LiteralCommandNode)) {
            throw new UnsupportedOperationException("Unknown node type " + commandNode);
         }

         var4 = (byte)(var4 | 1);
      }

      friendlyByteBuf.writeByte(var4);
      friendlyByteBuf.writeVarInt(commandNode.getChildren().size());

      for(CommandNode<SharedSuggestionProvider> var6 : commandNode.getChildren()) {
         friendlyByteBuf.writeVarInt(((Integer)map.get(var6)).intValue());
      }

      if(commandNode.getRedirect() != null) {
         friendlyByteBuf.writeVarInt(((Integer)map.get(commandNode.getRedirect())).intValue());
      }

      if(commandNode instanceof ArgumentCommandNode) {
         ArgumentCommandNode<SharedSuggestionProvider, ?> var5 = (ArgumentCommandNode)commandNode;
         friendlyByteBuf.writeUtf(var5.getName());
         ArgumentTypes.serialize(friendlyByteBuf, var5.getType());
         if(var5.getCustomSuggestions() != null) {
            friendlyByteBuf.writeResourceLocation(SuggestionProviders.getName(var5.getCustomSuggestions()));
         }
      } else if(commandNode instanceof LiteralCommandNode) {
         friendlyByteBuf.writeUtf(((LiteralCommandNode)commandNode).getLiteral());
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleCommands(this);
   }

   public RootCommandNode getRoot() {
      return this.root;
   }

   static class Entry {
      @Nullable
      private final ArgumentBuilder builder;
      private final byte flags;
      private final int redirect;
      private final int[] children;
      private CommandNode node;

      private Entry(@Nullable ArgumentBuilder builder, byte flags, int redirect, int[] children) {
         this.builder = builder;
         this.flags = flags;
         this.redirect = redirect;
         this.children = children;
      }

      public boolean build(ClientboundCommandsPacket.Entry[] clientboundCommandsPacket$Entrys) {
         if(this.node == null) {
            if(this.builder == null) {
               this.node = new RootCommandNode();
            } else {
               if((this.flags & 8) != 0) {
                  if(clientboundCommandsPacket$Entrys[this.redirect].node == null) {
                     return false;
                  }

                  this.builder.redirect(clientboundCommandsPacket$Entrys[this.redirect].node);
               }

               if((this.flags & 4) != 0) {
                  this.builder.executes((commandContext) -> {
                     return 0;
                  });
               }

               this.node = this.builder.build();
            }
         }

         for(int var5 : this.children) {
            if(clientboundCommandsPacket$Entrys[var5].node == null) {
               return false;
            }
         }

         for(int var5 : this.children) {
            CommandNode<SharedSuggestionProvider> var6 = clientboundCommandsPacket$Entrys[var5].node;
            if(!(var6 instanceof RootCommandNode)) {
               this.node.addChild(var6);
            }
         }

         return true;
      }
   }
}
