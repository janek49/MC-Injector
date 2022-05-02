package net.minecraft.client.renderer.blockentity;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidHeadModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@ClientJarOnly
public class SkullBlockRenderer extends BlockEntityRenderer {
   public static SkullBlockRenderer instance;
   private static final Map MODEL_BY_TYPE = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      SkullModel var1 = new SkullModel(0, 0, 64, 32);
      SkullModel var2 = new HumanoidHeadModel();
      DragonHeadModel var3 = new DragonHeadModel(0.0F);
      hashMap.put(SkullBlock.Types.SKELETON, var1);
      hashMap.put(SkullBlock.Types.WITHER_SKELETON, var1);
      hashMap.put(SkullBlock.Types.PLAYER, var2);
      hashMap.put(SkullBlock.Types.ZOMBIE, var2);
      hashMap.put(SkullBlock.Types.CREEPER, var1);
      hashMap.put(SkullBlock.Types.DRAGON, var3);
   });
   private static final Map SKIN_BY_TYPE = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put(SkullBlock.Types.SKELETON, new ResourceLocation("textures/entity/skeleton/skeleton.png"));
      hashMap.put(SkullBlock.Types.WITHER_SKELETON, new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
      hashMap.put(SkullBlock.Types.ZOMBIE, new ResourceLocation("textures/entity/zombie/zombie.png"));
      hashMap.put(SkullBlock.Types.CREEPER, new ResourceLocation("textures/entity/creeper/creeper.png"));
      hashMap.put(SkullBlock.Types.DRAGON, new ResourceLocation("textures/entity/enderdragon/dragon.png"));
      hashMap.put(SkullBlock.Types.PLAYER, DefaultPlayerSkin.getDefaultSkin());
   });

   public void render(SkullBlockEntity skullBlockEntity, double var2, double var4, double var6, float var8, int var9) {
      float var10 = skullBlockEntity.getMouthAnimation(var8);
      BlockState var11 = skullBlockEntity.getBlockState();
      boolean var12 = var11.getBlock() instanceof WallSkullBlock;
      Direction var13 = var12?(Direction)var11.getValue(WallSkullBlock.FACING):null;
      float var14 = 22.5F * (float)(var12?(2 + var13.get2DDataValue()) * 4:((Integer)var11.getValue(SkullBlock.ROTATION)).intValue());
      this.renderSkull((float)var2, (float)var4, (float)var6, var13, var14, ((AbstractSkullBlock)var11.getBlock()).getType(), skullBlockEntity.getOwnerProfile(), var9, var10);
   }

   public void init(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
      super.init(blockEntityRenderDispatcher);
      instance = this;
   }

   public void renderSkull(float var1, float var2, float var3, @Nullable Direction direction, float var5, SkullBlock.Type skullBlock$Type, @Nullable GameProfile gameProfile, int var8, float var9) {
      SkullModel var10 = (SkullModel)MODEL_BY_TYPE.get(skullBlock$Type);
      if(var8 >= 0) {
         this.bindTexture(BREAKING_LOCATIONS[var8]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 2.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         this.bindTexture(this.getLocation(skullBlock$Type, gameProfile));
      }

      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      if(direction == null) {
         GlStateManager.translatef(var1 + 0.5F, var2, var3 + 0.5F);
      } else {
         switch(direction) {
         case NORTH:
            GlStateManager.translatef(var1 + 0.5F, var2 + 0.25F, var3 + 0.74F);
            break;
         case SOUTH:
            GlStateManager.translatef(var1 + 0.5F, var2 + 0.25F, var3 + 0.26F);
            break;
         case WEST:
            GlStateManager.translatef(var1 + 0.74F, var2 + 0.25F, var3 + 0.5F);
            break;
         case EAST:
         default:
            GlStateManager.translatef(var1 + 0.26F, var2 + 0.25F, var3 + 0.5F);
         }
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlphaTest();
      if(skullBlock$Type == SkullBlock.Types.PLAYER) {
         GlStateManager.setProfile(GlStateManager.Profile.PLAYER_SKIN);
      }

      var10.render(var9, 0.0F, 0.0F, var5, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      if(var8 >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

   }

   private ResourceLocation getLocation(SkullBlock.Type skullBlock$Type, @Nullable GameProfile gameProfile) {
      ResourceLocation resourceLocation = (ResourceLocation)SKIN_BY_TYPE.get(skullBlock$Type);
      if(skullBlock$Type == SkullBlock.Types.PLAYER && gameProfile != null) {
         Minecraft var4 = Minecraft.getInstance();
         Map<Type, MinecraftProfileTexture> var5 = var4.getSkinManager().getInsecureSkinInformation(gameProfile);
         if(var5.containsKey(Type.SKIN)) {
            resourceLocation = var4.getSkinManager().registerTexture((MinecraftProfileTexture)var5.get(Type.SKIN), Type.SKIN);
         } else {
            resourceLocation = DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(gameProfile));
         }
      }

      return resourceLocation;
   }
}
