package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.banner.BannerTextures;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import org.apache.commons.lang3.StringUtils;

@ClientJarOnly
public class EntityBlockRenderer {
   private static final ShulkerBoxBlockEntity[] SHULKER_BOXES = (ShulkerBoxBlockEntity[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(ShulkerBoxBlockEntity::<init>).toArray((i) -> {
      return new ShulkerBoxBlockEntity[i];
   });
   private static final ShulkerBoxBlockEntity DEFAULT_SHULKER_BOX = new ShulkerBoxBlockEntity((DyeColor)null);
   public static final EntityBlockRenderer instance = new EntityBlockRenderer();
   private final ChestBlockEntity chest = new ChestBlockEntity();
   private final ChestBlockEntity trappedChest = new TrappedChestBlockEntity();
   private final EnderChestBlockEntity enderChest = new EnderChestBlockEntity();
   private final BannerBlockEntity banner = new BannerBlockEntity();
   private final BedBlockEntity bed = new BedBlockEntity();
   private final SkullBlockEntity skull = new SkullBlockEntity();
   private final ConduitBlockEntity conduit = new ConduitBlockEntity();
   private final ShieldModel shieldModel = new ShieldModel();
   private final TridentModel tridentModel = new TridentModel();

   public void renderByItem(ItemStack itemStack) {
      Item var2 = itemStack.getItem();
      if(var2 instanceof BannerItem) {
         this.banner.fromItem(itemStack, ((BannerItem)var2).getColor());
         BlockEntityRenderDispatcher.instance.renderItem(this.banner);
      } else if(var2 instanceof BlockItem && ((BlockItem)var2).getBlock() instanceof BedBlock) {
         this.bed.setColor(((BedBlock)((BlockItem)var2).getBlock()).getColor());
         BlockEntityRenderDispatcher.instance.renderItem(this.bed);
      } else if(var2 == Items.SHIELD) {
         if(itemStack.getTagElement("BlockEntityTag") != null) {
            this.banner.fromItem(itemStack, ShieldItem.getColor(itemStack));
            Minecraft.getInstance().getTextureManager().bind(BannerTextures.SHIELD_CACHE.getTextureLocation(this.banner.getTextureHashName(), this.banner.getPatterns(), this.banner.getColors()));
         } else {
            Minecraft.getInstance().getTextureManager().bind(BannerTextures.NO_PATTERN_SHIELD);
         }

         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.0F, -1.0F, -1.0F);
         this.shieldModel.render();
         if(itemStack.hasFoil()) {
            ShieldModel var10001 = this.shieldModel;
            this.shieldModel.getClass();
            this.renderFoil(var10001::render);
         }

         GlStateManager.popMatrix();
      } else if(var2 instanceof BlockItem && ((BlockItem)var2).getBlock() instanceof AbstractSkullBlock) {
         GameProfile var3 = null;
         if(itemStack.hasTag()) {
            CompoundTag var4 = itemStack.getTag();
            if(var4.contains("SkullOwner", 10)) {
               var3 = NbtUtils.readGameProfile(var4.getCompound("SkullOwner"));
            } else if(var4.contains("SkullOwner", 8) && !StringUtils.isBlank(var4.getString("SkullOwner"))) {
               var3 = new GameProfile((UUID)null, var4.getString("SkullOwner"));
               var3 = SkullBlockEntity.updateGameprofile(var3);
               var4.remove("SkullOwner");
               var4.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), var3));
            }
         }

         if(SkullBlockRenderer.instance != null) {
            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            SkullBlockRenderer.instance.renderSkull(0.0F, 0.0F, 0.0F, (Direction)null, 180.0F, ((AbstractSkullBlock)((BlockItem)var2).getBlock()).getType(), var3, -1, 0.0F);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
         }
      } else if(var2 == Items.TRIDENT) {
         Minecraft.getInstance().getTextureManager().bind(TridentModel.TEXTURE);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.0F, -1.0F, -1.0F);
         this.tridentModel.render();
         if(itemStack.hasFoil()) {
            TridentModel var7 = this.tridentModel;
            this.tridentModel.getClass();
            this.renderFoil(var7::render);
         }

         GlStateManager.popMatrix();
      } else if(var2 instanceof BlockItem && ((BlockItem)var2).getBlock() == Blocks.CONDUIT) {
         BlockEntityRenderDispatcher.instance.renderItem(this.conduit);
      } else if(var2 == Blocks.ENDER_CHEST.asItem()) {
         BlockEntityRenderDispatcher.instance.renderItem(this.enderChest);
      } else if(var2 == Blocks.TRAPPED_CHEST.asItem()) {
         BlockEntityRenderDispatcher.instance.renderItem(this.trappedChest);
      } else if(Block.byItem(var2) instanceof ShulkerBoxBlock) {
         DyeColor var3 = ShulkerBoxBlock.getColorFromItem(var2);
         if(var3 == null) {
            BlockEntityRenderDispatcher.instance.renderItem(DEFAULT_SHULKER_BOX);
         } else {
            BlockEntityRenderDispatcher.instance.renderItem(SHULKER_BOXES[var3.getId()]);
         }
      } else {
         BlockEntityRenderDispatcher.instance.renderItem(this.chest);
      }

   }

   private void renderFoil(Runnable runnable) {
      GlStateManager.color3f(0.5019608F, 0.2509804F, 0.8F);
      Minecraft.getInstance().getTextureManager().bind(ItemRenderer.ENCHANT_GLINT_LOCATION);
      ItemRenderer.renderFoilLayer(Minecraft.getInstance().getTextureManager(), runnable, 1);
   }
}
