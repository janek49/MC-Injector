package net.minecraft.client.multiplayer;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;

@ClientJarOnly
public class PlayerInfo {
   private final GameProfile profile;
   private final Map textureLocations = Maps.newEnumMap(Type.class);
   private GameType gameMode;
   private int latency;
   private boolean pendingTextures;
   private String skinModel;
   private Component tabListDisplayName;
   private int lastHealth;
   private int displayHealth;
   private long lastHealthTime;
   private long healthBlinkTime;
   private long renderVisibilityId;

   public PlayerInfo(GameProfile profile) {
      this.profile = profile;
   }

   public PlayerInfo(ClientboundPlayerInfoPacket.PlayerUpdate clientboundPlayerInfoPacket$PlayerUpdate) {
      this.profile = clientboundPlayerInfoPacket$PlayerUpdate.getProfile();
      this.gameMode = clientboundPlayerInfoPacket$PlayerUpdate.getGameMode();
      this.latency = clientboundPlayerInfoPacket$PlayerUpdate.getLatency();
      this.tabListDisplayName = clientboundPlayerInfoPacket$PlayerUpdate.getDisplayName();
   }

   public GameProfile getProfile() {
      return this.profile;
   }

   public GameType getGameMode() {
      return this.gameMode;
   }

   protected void setGameMode(GameType gameMode) {
      this.gameMode = gameMode;
   }

   public int getLatency() {
      return this.latency;
   }

   protected void setLatency(int latency) {
      this.latency = latency;
   }

   public boolean isSkinLoaded() {
      return this.getSkinLocation() != null;
   }

   public String getModelName() {
      return this.skinModel == null?DefaultPlayerSkin.getSkinModelName(this.profile.getId()):this.skinModel;
   }

   public ResourceLocation getSkinLocation() {
      this.registerTextures();
      return (ResourceLocation)MoreObjects.firstNonNull(this.textureLocations.get(Type.SKIN), DefaultPlayerSkin.getDefaultSkin(this.profile.getId()));
   }

   @Nullable
   public ResourceLocation getCapeLocation() {
      this.registerTextures();
      return (ResourceLocation)this.textureLocations.get(Type.CAPE);
   }

   @Nullable
   public ResourceLocation getElytraLocation() {
      this.registerTextures();
      return (ResourceLocation)this.textureLocations.get(Type.ELYTRA);
   }

   @Nullable
   public PlayerTeam getTeam() {
      return Minecraft.getInstance().level.getScoreboard().getPlayersTeam(this.getProfile().getName());
   }

   protected void registerTextures() {
      synchronized(this) {
         if(!this.pendingTextures) {
            this.pendingTextures = true;
            Minecraft.getInstance().getSkinManager().registerSkins(this.profile, (minecraftProfileTexture$Type, resourceLocation, minecraftProfileTexture) -> {
               switch(minecraftProfileTexture$Type) {
               case SKIN:
                  this.textureLocations.put(Type.SKIN, resourceLocation);
                  this.skinModel = minecraftProfileTexture.getMetadata("model");
                  if(this.skinModel == null) {
                     this.skinModel = "default";
                  }
                  break;
               case CAPE:
                  this.textureLocations.put(Type.CAPE, resourceLocation);
                  break;
               case ELYTRA:
                  this.textureLocations.put(Type.ELYTRA, resourceLocation);
               }

            }, true);
         }

      }
   }

   public void setTabListDisplayName(@Nullable Component tabListDisplayName) {
      this.tabListDisplayName = tabListDisplayName;
   }

   @Nullable
   public Component getTabListDisplayName() {
      return this.tabListDisplayName;
   }

   public int getLastHealth() {
      return this.lastHealth;
   }

   public void setLastHealth(int lastHealth) {
      this.lastHealth = lastHealth;
   }

   public int getDisplayHealth() {
      return this.displayHealth;
   }

   public void setDisplayHealth(int displayHealth) {
      this.displayHealth = displayHealth;
   }

   public long getLastHealthTime() {
      return this.lastHealthTime;
   }

   public void setLastHealthTime(long lastHealthTime) {
      this.lastHealthTime = lastHealthTime;
   }

   public long getHealthBlinkTime() {
      return this.healthBlinkTime;
   }

   public void setHealthBlinkTime(long healthBlinkTime) {
      this.healthBlinkTime = healthBlinkTime;
   }

   public long getRenderVisibilityId() {
      return this.renderVisibilityId;
   }

   public void setRenderVisibilityId(long renderVisibilityId) {
      this.renderVisibilityId = renderVisibilityId;
   }
}
