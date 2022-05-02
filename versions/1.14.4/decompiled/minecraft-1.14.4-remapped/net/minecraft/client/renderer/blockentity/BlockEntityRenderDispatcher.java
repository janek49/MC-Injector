package net.minecraft.client.renderer.blockentity;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.CampfireRenderer;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.renderer.blockentity.LecternRenderer;
import net.minecraft.client.renderer.blockentity.PistonHeadRenderer;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.SpawnerRenderer;
import net.minecraft.client.renderer.blockentity.StructureBlockRenderer;
import net.minecraft.client.renderer.blockentity.TheEndGatewayRenderer;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.phys.HitResult;

@ClientJarOnly
public class BlockEntityRenderDispatcher {
   private final Map renderers = Maps.newHashMap();
   public static final BlockEntityRenderDispatcher instance = new BlockEntityRenderDispatcher();
   private Font font;
   public static double xOff;
   public static double yOff;
   public static double zOff;
   public TextureManager textureManager;
   public Level level;
   public Camera camera;
   public HitResult cameraHitResult;

   private BlockEntityRenderDispatcher() {
      this.renderers.put(SignBlockEntity.class, new SignRenderer());
      this.renderers.put(SpawnerBlockEntity.class, new SpawnerRenderer());
      this.renderers.put(PistonMovingBlockEntity.class, new PistonHeadRenderer());
      this.renderers.put(ChestBlockEntity.class, new ChestRenderer());
      this.renderers.put(EnderChestBlockEntity.class, new ChestRenderer());
      this.renderers.put(EnchantmentTableBlockEntity.class, new EnchantTableRenderer());
      this.renderers.put(LecternBlockEntity.class, new LecternRenderer());
      this.renderers.put(TheEndPortalBlockEntity.class, new TheEndPortalRenderer());
      this.renderers.put(TheEndGatewayBlockEntity.class, new TheEndGatewayRenderer());
      this.renderers.put(BeaconBlockEntity.class, new BeaconRenderer());
      this.renderers.put(SkullBlockEntity.class, new SkullBlockRenderer());
      this.renderers.put(BannerBlockEntity.class, new BannerRenderer());
      this.renderers.put(StructureBlockEntity.class, new StructureBlockRenderer());
      this.renderers.put(ShulkerBoxBlockEntity.class, new ShulkerBoxRenderer(new ShulkerModel()));
      this.renderers.put(BedBlockEntity.class, new BedRenderer());
      this.renderers.put(ConduitBlockEntity.class, new ConduitRenderer());
      this.renderers.put(BellBlockEntity.class, new BellRenderer());
      this.renderers.put(CampfireBlockEntity.class, new CampfireRenderer());

      for(BlockEntityRenderer<?> var2 : this.renderers.values()) {
         var2.init(this);
      }

   }

   public BlockEntityRenderer getRenderer(Class class) {
      BlockEntityRenderer<? extends BlockEntity> blockEntityRenderer = (BlockEntityRenderer)this.renderers.get(class);
      if(blockEntityRenderer == null && class != BlockEntity.class) {
         blockEntityRenderer = this.getRenderer(class.getSuperclass());
         this.renderers.put(class, blockEntityRenderer);
      }

      return blockEntityRenderer;
   }

   @Nullable
   public BlockEntityRenderer getRenderer(@Nullable BlockEntity blockEntity) {
      return blockEntity == null?null:this.getRenderer(blockEntity.getClass());
   }

   public void prepare(Level level, TextureManager textureManager, Font font, Camera camera, HitResult cameraHitResult) {
      if(this.level != level) {
         this.setLevel(level);
      }

      this.textureManager = textureManager;
      this.camera = camera;
      this.font = font;
      this.cameraHitResult = cameraHitResult;
   }

   public void render(BlockEntity blockEntity, float var2, int var3) {
      if(blockEntity.distanceToSqr(this.camera.getPosition().x, this.camera.getPosition().y, this.camera.getPosition().z) < blockEntity.getViewDistance()) {
         Lighting.turnOn();
         int var4 = this.level.getLightColor(blockEntity.getBlockPos(), 0);
         int var5 = var4 % 65536;
         int var6 = var4 / 65536;
         GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)var5, (float)var6);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         BlockPos var7 = blockEntity.getBlockPos();
         this.render(blockEntity, (double)var7.getX() - xOff, (double)var7.getY() - yOff, (double)var7.getZ() - zOff, var2, var3, false);
      }

   }

   public void render(BlockEntity blockEntity, double var2, double var4, double var6, float var8) {
      this.render(blockEntity, var2, var4, var6, var8, -1, false);
   }

   public void renderItem(BlockEntity blockEntity) {
      this.render(blockEntity, 0.0D, 0.0D, 0.0D, 0.0F, -1, true);
   }

   public void render(BlockEntity blockEntity, double var2, double var4, double var6, float var8, int var9, boolean var10) {
      BlockEntityRenderer<BlockEntity> var11 = this.getRenderer(blockEntity);
      if(var11 != null) {
         try {
            if(var10 || blockEntity.hasLevel() && blockEntity.getType().isValid(blockEntity.getBlockState().getBlock())) {
               var11.render(blockEntity, var2, var4, var6, var8, var9);
            }
         } catch (Throwable var15) {
            CrashReport var13 = CrashReport.forThrowable(var15, "Rendering Block Entity");
            CrashReportCategory var14 = var13.addCategory("Block Entity Details");
            blockEntity.fillCrashReportCategory(var14);
            throw new ReportedException(var13);
         }
      }

   }

   public void setLevel(@Nullable Level level) {
      this.level = level;
      if(level == null) {
         this.camera = null;
      }

   }

   public Font getFont() {
      return this.font;
   }
}
