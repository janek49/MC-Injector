package net.minecraft.client.multiplayer;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

@ClientJarOnly
public class ClientSuggestionProvider implements SharedSuggestionProvider {
   private final ClientPacketListener connection;
   private final Minecraft minecraft;
   private int pendingSuggestionsId = -1;
   private CompletableFuture pendingSuggestionsFuture;

   public ClientSuggestionProvider(ClientPacketListener connection, Minecraft minecraft) {
      this.connection = connection;
      this.minecraft = minecraft;
   }

   public Collection getOnlinePlayerNames() {
      List<String> var1 = Lists.newArrayList();

      for(PlayerInfo var3 : this.connection.getOnlinePlayers()) {
         var1.add(var3.getProfile().getName());
      }

      return var1;
   }

   public Collection getSelectedEntities() {
      return (Collection)(this.minecraft.hitResult != null && this.minecraft.hitResult.getType() == HitResult.Type.ENTITY?Collections.singleton(((EntityHitResult)this.minecraft.hitResult).getEntity().getStringUUID()):Collections.emptyList());
   }

   public Collection getAllTeams() {
      return this.connection.getLevel().getScoreboard().getTeamNames();
   }

   public Collection getAvailableSoundEvents() {
      return this.minecraft.getSoundManager().getAvailableSounds();
   }

   public Stream getRecipeNames() {
      return this.connection.getRecipeManager().getRecipeIds();
   }

   public boolean hasPermission(int i) {
      LocalPlayer var2 = this.minecraft.player;
      return var2 != null?var2.hasPermissions(i):i == 0;
   }

   public CompletableFuture customSuggestion(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      if(this.pendingSuggestionsFuture != null) {
         this.pendingSuggestionsFuture.cancel(false);
      }

      this.pendingSuggestionsFuture = new CompletableFuture();
      int var3 = ++this.pendingSuggestionsId;
      this.connection.send((Packet)(new ServerboundCommandSuggestionPacket(var3, commandContext.getInput())));
      return this.pendingSuggestionsFuture;
   }

   private static String prettyPrint(double d) {
      return String.format(Locale.ROOT, "%.2f", new Object[]{Double.valueOf(d)});
   }

   private static String prettyPrint(int i) {
      return Integer.toString(i);
   }

   public Collection getRelevantCoordinates() {
      HitResult var1 = this.minecraft.hitResult;
      if(var1 != null && var1.getType() == HitResult.Type.BLOCK) {
         BlockPos var2 = ((BlockHitResult)var1).getBlockPos();
         return Collections.singleton(new SharedSuggestionProvider.TextCoordinates(prettyPrint(var2.getX()), prettyPrint(var2.getY()), prettyPrint(var2.getZ())));
      } else {
         return super.getRelevantCoordinates();
      }
   }

   public Collection getAbsoluteCoordinates() {
      HitResult var1 = this.minecraft.hitResult;
      if(var1 != null && var1.getType() == HitResult.Type.BLOCK) {
         Vec3 var2 = var1.getLocation();
         return Collections.singleton(new SharedSuggestionProvider.TextCoordinates(prettyPrint(var2.x), prettyPrint(var2.y), prettyPrint(var2.z)));
      } else {
         return super.getAbsoluteCoordinates();
      }
   }

   public void completeCustomSuggestions(int var1, Suggestions suggestions) {
      if(var1 == this.pendingSuggestionsId) {
         this.pendingSuggestionsFuture.complete(suggestions);
         this.pendingSuggestionsFuture = null;
         this.pendingSuggestionsId = -1;
      }

   }
}
