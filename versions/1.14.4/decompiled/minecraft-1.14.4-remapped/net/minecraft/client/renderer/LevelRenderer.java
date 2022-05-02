package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector4f;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ChunkRenderList;
import net.minecraft.client.renderer.OffsettedRenderList;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunkFactory;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.client.renderer.culling.FrustumCuller;
import net.minecraft.client.renderer.culling.FrustumData;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class LevelRenderer implements AutoCloseable, ResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation MOON_LOCATION = new ResourceLocation("textures/environment/moon_phases.png");
   private static final ResourceLocation SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");
   private static final ResourceLocation CLOUDS_LOCATION = new ResourceLocation("textures/environment/clouds.png");
   private static final ResourceLocation END_SKY_LOCATION = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation FORCEFIELD_LOCATION = new ResourceLocation("textures/misc/forcefield.png");
   public static final Direction[] DIRECTIONS = Direction.values();
   private final Minecraft minecraft;
   private final TextureManager textureManager;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private MultiPlayerLevel level;
   private Set chunksToCompile = Sets.newLinkedHashSet();
   private List renderChunks = Lists.newArrayListWithCapacity(69696);
   private final Set globalBlockEntities = Sets.newHashSet();
   private ViewArea viewArea;
   private int starList = -1;
   private int skyList = -1;
   private int darkList = -1;
   private final VertexFormat skyFormat;
   private VertexBuffer starBuffer;
   private VertexBuffer skyBuffer;
   private VertexBuffer darkBuffer;
   private final int CLOUD_VERTEX_SIZE = 28;
   private boolean generateClouds = true;
   private int cloudList = -1;
   private VertexBuffer cloudBuffer;
   private int ticks;
   private final Map destroyingBlocks = Maps.newHashMap();
   private final Map playingRecords = Maps.newHashMap();
   private final TextureAtlasSprite[] breakingTextures = new TextureAtlasSprite[10];
   private RenderTarget entityTarget;
   private PostChain entityEffect;
   private double lastCameraX = Double.MIN_VALUE;
   private double lastCameraY = Double.MIN_VALUE;
   private double lastCameraZ = Double.MIN_VALUE;
   private int lastCameraChunkX = Integer.MIN_VALUE;
   private int lastCameraChunkY = Integer.MIN_VALUE;
   private int lastCameraChunkZ = Integer.MIN_VALUE;
   private double prevCamX = Double.MIN_VALUE;
   private double prevCamY = Double.MIN_VALUE;
   private double prevCamZ = Double.MIN_VALUE;
   private double prevCamRotX = Double.MIN_VALUE;
   private double prevCamRotY = Double.MIN_VALUE;
   private int prevCloudX = Integer.MIN_VALUE;
   private int prevCloudY = Integer.MIN_VALUE;
   private int prevCloudZ = Integer.MIN_VALUE;
   private Vec3 prevCloudColor = Vec3.ZERO;
   private CloudStatus prevCloudsType;
   private ChunkRenderDispatcher chunkRenderDispatcher;
   private ChunkRenderList renderList;
   private int lastViewDistance = -1;
   private int noEntityRenderFrames = 2;
   private int renderedEntities;
   private int culledEntities;
   private boolean captureFrustum;
   private FrustumData capturedFrustum;
   private final Vector4f[] frustumPoints = new Vector4f[8];
   private final Vector3d frustumPos = new Vector3d(0.0D, 0.0D, 0.0D);
   private boolean usingVbo;
   private RenderChunkFactory renderChunkFactory;
   private double xTransparentOld;
   private double yTransparentOld;
   private double zTransparentOld;
   private boolean needsUpdate = true;
   private boolean hadRenderedEntityOutlines;

   public LevelRenderer(Minecraft minecraft) {
      this.minecraft = minecraft;
      this.entityRenderDispatcher = minecraft.getEntityRenderDispatcher();
      this.textureManager = minecraft.getTextureManager();
      this.usingVbo = GLX.useVbo();
      if(this.usingVbo) {
         this.renderList = new VboRenderList();
         this.renderChunkFactory = RenderChunk::<init>;
      } else {
         this.renderList = new OffsettedRenderList();
         this.renderChunkFactory = ListedRenderChunk::<init>;
      }

      this.skyFormat = new VertexFormat();
      this.skyFormat.addElement(new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3));
      this.createStars();
      this.createLightSky();
      this.createDarkSky();
   }

   public void close() {
      if(this.entityEffect != null) {
         this.entityEffect.close();
      }

   }

   public void onResourceManagerReload(ResourceManager resourceManager) {
      this.textureManager.bind(FORCEFIELD_LOCATION);
      GlStateManager.texParameter(3553, 10242, 10497);
      GlStateManager.texParameter(3553, 10243, 10497);
      GlStateManager.bindTexture(0);
      this.setupBreakingTextureSprites();
      this.initOutline();
   }

   private void setupBreakingTextureSprites() {
      TextureAtlas var1 = this.minecraft.getTextureAtlas();
      this.breakingTextures[0] = var1.getSprite(ModelBakery.DESTROY_STAGE_0);
      this.breakingTextures[1] = var1.getSprite(ModelBakery.DESTROY_STAGE_1);
      this.breakingTextures[2] = var1.getSprite(ModelBakery.DESTROY_STAGE_2);
      this.breakingTextures[3] = var1.getSprite(ModelBakery.DESTROY_STAGE_3);
      this.breakingTextures[4] = var1.getSprite(ModelBakery.DESTROY_STAGE_4);
      this.breakingTextures[5] = var1.getSprite(ModelBakery.DESTROY_STAGE_5);
      this.breakingTextures[6] = var1.getSprite(ModelBakery.DESTROY_STAGE_6);
      this.breakingTextures[7] = var1.getSprite(ModelBakery.DESTROY_STAGE_7);
      this.breakingTextures[8] = var1.getSprite(ModelBakery.DESTROY_STAGE_8);
      this.breakingTextures[9] = var1.getSprite(ModelBakery.DESTROY_STAGE_9);
   }

   public void initOutline() {
      if(GLX.usePostProcess) {
         if(ProgramManager.getInstance() == null) {
            ProgramManager.createInstance();
         }

         if(this.entityEffect != null) {
            this.entityEffect.close();
         }

         ResourceLocation var1 = new ResourceLocation("shaders/post/entity_outline.json");

         try {
            this.entityEffect = new PostChain(this.minecraft.getTextureManager(), this.minecraft.getResourceManager(), this.minecraft.getMainRenderTarget(), var1);
            this.entityEffect.resize(this.minecraft.window.getWidth(), this.minecraft.window.getHeight());
            this.entityTarget = this.entityEffect.getTempTarget("final");
         } catch (IOException var3) {
            LOGGER.warn("Failed to load shader: {}", var1, var3);
            this.entityEffect = null;
            this.entityTarget = null;
         } catch (JsonSyntaxException var4) {
            LOGGER.warn("Failed to load shader: {}", var1, var4);
            this.entityEffect = null;
            this.entityTarget = null;
         }
      } else {
         this.entityEffect = null;
         this.entityTarget = null;
      }

   }

   public void doEntityOutline() {
      if(this.shouldShowEntityOutlines()) {
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         this.entityTarget.blitToScreen(this.minecraft.window.getWidth(), this.minecraft.window.getHeight(), false);
         GlStateManager.disableBlend();
      }

   }

   protected boolean shouldShowEntityOutlines() {
      return this.entityTarget != null && this.entityEffect != null && this.minecraft.player != null;
   }

   private void createDarkSky() {
      Tesselator var1 = Tesselator.getInstance();
      BufferBuilder var2 = var1.getBuilder();
      if(this.darkBuffer != null) {
         this.darkBuffer.delete();
      }

      if(this.darkList >= 0) {
         MemoryTracker.releaseList(this.darkList);
         this.darkList = -1;
      }

      if(this.usingVbo) {
         this.darkBuffer = new VertexBuffer(this.skyFormat);
         this.drawSkyHemisphere(var2, -16.0F, true);
         var2.end();
         var2.clear();
         this.darkBuffer.upload(var2.getBuffer());
      } else {
         this.darkList = MemoryTracker.genLists(1);
         GlStateManager.newList(this.darkList, 4864);
         this.drawSkyHemisphere(var2, -16.0F, true);
         var1.end();
         GlStateManager.endList();
      }

   }

   private void createLightSky() {
      Tesselator var1 = Tesselator.getInstance();
      BufferBuilder var2 = var1.getBuilder();
      if(this.skyBuffer != null) {
         this.skyBuffer.delete();
      }

      if(this.skyList >= 0) {
         MemoryTracker.releaseList(this.skyList);
         this.skyList = -1;
      }

      if(this.usingVbo) {
         this.skyBuffer = new VertexBuffer(this.skyFormat);
         this.drawSkyHemisphere(var2, 16.0F, false);
         var2.end();
         var2.clear();
         this.skyBuffer.upload(var2.getBuffer());
      } else {
         this.skyList = MemoryTracker.genLists(1);
         GlStateManager.newList(this.skyList, 4864);
         this.drawSkyHemisphere(var2, 16.0F, false);
         var1.end();
         GlStateManager.endList();
      }

   }

   private void drawSkyHemisphere(BufferBuilder bufferBuilder, float var2, boolean var3) {
      int var4 = 64;
      int var5 = 6;
      bufferBuilder.begin(7, DefaultVertexFormat.POSITION);

      for(int var6 = -384; var6 <= 384; var6 += 64) {
         for(int var7 = -384; var7 <= 384; var7 += 64) {
            float var8 = (float)var6;
            float var9 = (float)(var6 + 64);
            if(var3) {
               var9 = (float)var6;
               var8 = (float)(var6 + 64);
            }

            bufferBuilder.vertex((double)var8, (double)var2, (double)var7).endVertex();
            bufferBuilder.vertex((double)var9, (double)var2, (double)var7).endVertex();
            bufferBuilder.vertex((double)var9, (double)var2, (double)(var7 + 64)).endVertex();
            bufferBuilder.vertex((double)var8, (double)var2, (double)(var7 + 64)).endVertex();
         }
      }

   }

   private void createStars() {
      Tesselator var1 = Tesselator.getInstance();
      BufferBuilder var2 = var1.getBuilder();
      if(this.starBuffer != null) {
         this.starBuffer.delete();
      }

      if(this.starList >= 0) {
         MemoryTracker.releaseList(this.starList);
         this.starList = -1;
      }

      if(this.usingVbo) {
         this.starBuffer = new VertexBuffer(this.skyFormat);
         this.drawStars(var2);
         var2.end();
         var2.clear();
         this.starBuffer.upload(var2.getBuffer());
      } else {
         this.starList = MemoryTracker.genLists(1);
         GlStateManager.pushMatrix();
         GlStateManager.newList(this.starList, 4864);
         this.drawStars(var2);
         var1.end();
         GlStateManager.endList();
         GlStateManager.popMatrix();
      }

   }

   private void drawStars(BufferBuilder bufferBuilder) {
      Random var2 = new Random(10842L);
      bufferBuilder.begin(7, DefaultVertexFormat.POSITION);

      for(int var3 = 0; var3 < 1500; ++var3) {
         double var4 = (double)(var2.nextFloat() * 2.0F - 1.0F);
         double var6 = (double)(var2.nextFloat() * 2.0F - 1.0F);
         double var8 = (double)(var2.nextFloat() * 2.0F - 1.0F);
         double var10 = (double)(0.15F + var2.nextFloat() * 0.1F);
         double var12 = var4 * var4 + var6 * var6 + var8 * var8;
         if(var12 < 1.0D && var12 > 0.01D) {
            var12 = 1.0D / Math.sqrt(var12);
            var4 = var4 * var12;
            var6 = var6 * var12;
            var8 = var8 * var12;
            double var14 = var4 * 100.0D;
            double var16 = var6 * 100.0D;
            double var18 = var8 * 100.0D;
            double var20 = Math.atan2(var4, var8);
            double var22 = Math.sin(var20);
            double var24 = Math.cos(var20);
            double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
            double var28 = Math.sin(var26);
            double var30 = Math.cos(var26);
            double var32 = var2.nextDouble() * 3.141592653589793D * 2.0D;
            double var34 = Math.sin(var32);
            double var36 = Math.cos(var32);

            for(int var38 = 0; var38 < 4; ++var38) {
               double var39 = 0.0D;
               double var41 = (double)((var38 & 2) - 1) * var10;
               double var43 = (double)((var38 + 1 & 2) - 1) * var10;
               double var45 = 0.0D;
               double var47 = var41 * var36 - var43 * var34;
               double var49 = var43 * var36 + var41 * var34;
               double var53 = var47 * var28 + 0.0D * var30;
               double var55 = 0.0D * var28 - var47 * var30;
               double var57 = var55 * var22 - var49 * var24;
               double var61 = var49 * var22 + var55 * var24;
               bufferBuilder.vertex(var14 + var57, var16 + var53, var18 + var61).endVertex();
            }
         }
      }

   }

   public void setLevel(@Nullable MultiPlayerLevel level) {
      this.lastCameraX = Double.MIN_VALUE;
      this.lastCameraY = Double.MIN_VALUE;
      this.lastCameraZ = Double.MIN_VALUE;
      this.lastCameraChunkX = Integer.MIN_VALUE;
      this.lastCameraChunkY = Integer.MIN_VALUE;
      this.lastCameraChunkZ = Integer.MIN_VALUE;
      this.entityRenderDispatcher.setLevel(level);
      this.level = level;
      if(level != null) {
         this.allChanged();
      } else {
         this.chunksToCompile.clear();
         this.renderChunks.clear();
         if(this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
            this.viewArea = null;
         }

         if(this.chunkRenderDispatcher != null) {
            this.chunkRenderDispatcher.dispose();
         }

         this.chunkRenderDispatcher = null;
         this.globalBlockEntities.clear();
      }

   }

   public void allChanged() {
      if(this.level != null) {
         if(this.chunkRenderDispatcher == null) {
            this.chunkRenderDispatcher = new ChunkRenderDispatcher(this.minecraft.is64Bit());
         }

         this.needsUpdate = true;
         this.generateClouds = true;
         LeavesBlock.setFancy(this.minecraft.options.fancyGraphics);
         this.lastViewDistance = this.minecraft.options.renderDistance;
         boolean var1 = this.usingVbo;
         this.usingVbo = GLX.useVbo();
         if(var1 && !this.usingVbo) {
            this.renderList = new OffsettedRenderList();
            this.renderChunkFactory = ListedRenderChunk::<init>;
         } else if(!var1 && this.usingVbo) {
            this.renderList = new VboRenderList();
            this.renderChunkFactory = RenderChunk::<init>;
         }

         if(var1 != this.usingVbo) {
            this.createStars();
            this.createLightSky();
            this.createDarkSky();
         }

         if(this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
         }

         this.resetChunksToCompile();
         synchronized(this.globalBlockEntities) {
            this.globalBlockEntities.clear();
         }

         this.viewArea = new ViewArea(this.level, this.minecraft.options.renderDistance, this, this.renderChunkFactory);
         if(this.level != null) {
            Entity var2 = this.minecraft.getCameraEntity();
            if(var2 != null) {
               this.viewArea.repositionCamera(var2.x, var2.z);
            }
         }

         this.noEntityRenderFrames = 2;
      }
   }

   protected void resetChunksToCompile() {
      this.chunksToCompile.clear();
      this.chunkRenderDispatcher.blockUntilClear();
   }

   public void resize(int var1, int var2) {
      this.needsUpdate();
      if(GLX.usePostProcess) {
         if(this.entityEffect != null) {
            this.entityEffect.resize(var1, var2);
         }

      }
   }

   public void prepare(Camera camera) {
      BlockEntityRenderDispatcher.instance.prepare(this.level, this.minecraft.getTextureManager(), this.minecraft.font, camera, this.minecraft.hitResult);
      this.entityRenderDispatcher.prepare(this.level, this.minecraft.font, camera, this.minecraft.crosshairPickEntity, this.minecraft.options);
   }

   public void renderEntities(Camera camera, Culler culler, float var3) {
      if(this.noEntityRenderFrames > 0) {
         --this.noEntityRenderFrames;
      } else {
         double var4 = camera.getPosition().x;
         double var6 = camera.getPosition().y;
         double var8 = camera.getPosition().z;
         this.level.getProfiler().push("prepare");
         this.renderedEntities = 0;
         this.culledEntities = 0;
         double var10 = camera.getPosition().x;
         double var12 = camera.getPosition().y;
         double var14 = camera.getPosition().z;
         BlockEntityRenderDispatcher.xOff = var10;
         BlockEntityRenderDispatcher.yOff = var12;
         BlockEntityRenderDispatcher.zOff = var14;
         this.entityRenderDispatcher.setPosition(var10, var12, var14);
         this.minecraft.gameRenderer.turnOnLightLayer();
         this.level.getProfiler().popPush("entities");
         List<Entity> var16 = Lists.newArrayList();
         List<Entity> var17 = Lists.newArrayList();

         for(Entity var19 : this.level.entitiesForRendering()) {
            if((this.entityRenderDispatcher.shouldRender(var19, culler, var4, var6, var8) || var19.hasIndirectPassenger(this.minecraft.player)) && (var19 != camera.getEntity() || camera.isDetached() || camera.getEntity() instanceof LivingEntity && ((LivingEntity)camera.getEntity()).isSleeping())) {
               ++this.renderedEntities;
               this.entityRenderDispatcher.render(var19, var3, false);
               if(var19.isGlowing() || var19 instanceof Player && this.minecraft.player.isSpectator() && this.minecraft.options.keySpectatorOutlines.isDown()) {
                  var16.add(var19);
               }

               if(this.entityRenderDispatcher.hasSecondPass(var19)) {
                  var17.add(var19);
               }
            }
         }

         if(!var17.isEmpty()) {
            for(Entity var19 : var17) {
               this.entityRenderDispatcher.renderSecondPass(var19, var3);
            }
         }

         if(this.shouldShowEntityOutlines() && (!var16.isEmpty() || this.hadRenderedEntityOutlines)) {
            this.level.getProfiler().popPush("entityOutlines");
            this.entityTarget.clear(Minecraft.ON_OSX);
            this.hadRenderedEntityOutlines = !var16.isEmpty();
            if(!var16.isEmpty()) {
               GlStateManager.depthFunc(519);
               GlStateManager.disableFog();
               this.entityTarget.bindWrite(false);
               Lighting.turnOff();
               this.entityRenderDispatcher.setSolidRendering(true);

               for(int var18 = 0; var18 < ((List)var16).size(); ++var18) {
                  this.entityRenderDispatcher.render((Entity)var16.get(var18), var3, false);
               }

               this.entityRenderDispatcher.setSolidRendering(false);
               Lighting.turnOn();
               GlStateManager.depthMask(false);
               this.entityEffect.process(var3);
               GlStateManager.enableLighting();
               GlStateManager.depthMask(true);
               GlStateManager.enableFog();
               GlStateManager.enableBlend();
               GlStateManager.enableColorMaterial();
               GlStateManager.depthFunc(515);
               GlStateManager.enableDepthTest();
               GlStateManager.enableAlphaTest();
            }

            this.minecraft.getMainRenderTarget().bindWrite(false);
         }

         this.level.getProfiler().popPush("blockentities");
         Lighting.turnOn();

         for(LevelRenderer.RenderChunkInfo var19 : this.renderChunks) {
            List<BlockEntity> var20 = var19.chunk.getCompiledChunk().getRenderableBlockEntities();
            if(!var20.isEmpty()) {
               for(BlockEntity var22 : var20) {
                  BlockEntityRenderDispatcher.instance.render(var22, var3, -1);
               }
            }
         }

         synchronized(this.globalBlockEntities) {
            for(BlockEntity var20 : this.globalBlockEntities) {
               BlockEntityRenderDispatcher.instance.render(var20, var3, -1);
            }
         }

         this.setupDestroyState();

         for(BlockDestructionProgress var19 : this.destroyingBlocks.values()) {
            BlockPos var20 = var19.getPos();
            BlockState var21 = this.level.getBlockState(var20);
            if(var21.getBlock().isEntityBlock()) {
               BlockEntity var22 = this.level.getBlockEntity(var20);
               if(var22 instanceof ChestBlockEntity && var21.getValue(ChestBlock.TYPE) == ChestType.LEFT) {
                  var20 = var20.relative(((Direction)var21.getValue(ChestBlock.FACING)).getClockWise());
                  var22 = this.level.getBlockEntity(var20);
               }

               if(var22 != null && var21.hasCustomBreakingProgress()) {
                  BlockEntityRenderDispatcher.instance.render(var22, var3, var19.getProgress());
               }
            }
         }

         this.restoreDestroyState();
         this.minecraft.gameRenderer.turnOffLightLayer();
         this.minecraft.getProfiler().pop();
      }
   }

   public String getChunkStatistics() {
      int var1 = this.viewArea.chunks.length;
      int var2 = this.countRenderedChunks();
      return String.format("C: %d/%d %sD: %d, %s", new Object[]{Integer.valueOf(var2), Integer.valueOf(var1), this.minecraft.smartCull?"(s) ":"", Integer.valueOf(this.lastViewDistance), this.chunkRenderDispatcher == null?"null":this.chunkRenderDispatcher.getStats()});
   }

   protected int countRenderedChunks() {
      int var1 = 0;

      for(LevelRenderer.RenderChunkInfo var3 : this.renderChunks) {
         CompiledChunk var4 = var3.chunk.compiled;
         if(var4 != CompiledChunk.UNCOMPILED && !var4.hasNoRenderableLayers()) {
            ++var1;
         }
      }

      return var1;
   }

   public String getEntityStatistics() {
      return "E: " + this.renderedEntities + "/" + this.level.getEntityCount() + ", B: " + this.culledEntities;
   }

   public void setupRender(Camera camera, Culler culler, int var3, boolean var4) {
      if(this.minecraft.options.renderDistance != this.lastViewDistance) {
         this.allChanged();
      }

      this.level.getProfiler().push("camera");
      double var5 = this.minecraft.player.x - this.lastCameraX;
      double var7 = this.minecraft.player.y - this.lastCameraY;
      double var9 = this.minecraft.player.z - this.lastCameraZ;
      if(this.lastCameraChunkX != this.minecraft.player.xChunk || this.lastCameraChunkY != this.minecraft.player.yChunk || this.lastCameraChunkZ != this.minecraft.player.zChunk || var5 * var5 + var7 * var7 + var9 * var9 > 16.0D) {
         this.lastCameraX = this.minecraft.player.x;
         this.lastCameraY = this.minecraft.player.y;
         this.lastCameraZ = this.minecraft.player.z;
         this.lastCameraChunkX = this.minecraft.player.xChunk;
         this.lastCameraChunkY = this.minecraft.player.yChunk;
         this.lastCameraChunkZ = this.minecraft.player.zChunk;
         this.viewArea.repositionCamera(this.minecraft.player.x, this.minecraft.player.z);
      }

      this.level.getProfiler().popPush("renderlistcamera");
      this.renderList.setCameraLocation(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);
      this.chunkRenderDispatcher.setCamera(camera.getPosition());
      this.level.getProfiler().popPush("cull");
      if(this.capturedFrustum != null) {
         FrustumCuller var11 = new FrustumCuller(this.capturedFrustum);
         var11.prepare(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z);
         culler = var11;
      }

      this.minecraft.getProfiler().popPush("culling");
      BlockPos var11 = camera.getBlockPosition();
      RenderChunk var12 = this.viewArea.getRenderChunkAt(var11);
      BlockPos var13 = new BlockPos(Mth.floor(camera.getPosition().x / 16.0D) * 16, Mth.floor(camera.getPosition().y / 16.0D) * 16, Mth.floor(camera.getPosition().z / 16.0D) * 16);
      float var14 = camera.getXRot();
      float var15 = camera.getYRot();
      this.needsUpdate = this.needsUpdate || !this.chunksToCompile.isEmpty() || camera.getPosition().x != this.prevCamX || camera.getPosition().y != this.prevCamY || camera.getPosition().z != this.prevCamZ || (double)var14 != this.prevCamRotX || (double)var15 != this.prevCamRotY;
      this.prevCamX = camera.getPosition().x;
      this.prevCamY = camera.getPosition().y;
      this.prevCamZ = camera.getPosition().z;
      this.prevCamRotX = (double)var14;
      this.prevCamRotY = (double)var15;
      boolean var16 = this.capturedFrustum != null;
      this.minecraft.getProfiler().popPush("update");
      if(!var16 && this.needsUpdate) {
         this.needsUpdate = false;
         this.renderChunks = Lists.newArrayList();
         Queue<LevelRenderer.RenderChunkInfo> var17 = Queues.newArrayDeque();
         Entity.setViewScale(Mth.clamp((double)this.minecraft.options.renderDistance / 8.0D, 1.0D, 2.5D));
         boolean var18 = this.minecraft.smartCull;
         if(var12 != null) {
            boolean var19 = false;
            LevelRenderer.RenderChunkInfo var20 = new LevelRenderer.RenderChunkInfo(var12, (Direction)null, 0);
            Set<Direction> var21 = this.getVisibleDirections(var11);
            if(var21.size() == 1) {
               Vec3 var22 = camera.getLookVector();
               Direction var23 = Direction.getNearest(var22.x, var22.y, var22.z).getOpposite();
               var21.remove(var23);
            }

            if(var21.isEmpty()) {
               var19 = true;
            }

            if(var19 && !var4) {
               this.renderChunks.add(var20);
            } else {
               if(var4 && this.level.getBlockState(var11).isSolidRender(this.level, var11)) {
                  var18 = false;
               }

               var12.setFrame(var3);
               var17.add(var20);
            }
         } else {
            int var19 = var11.getY() > 0?248:8;

            for(int var20 = -this.lastViewDistance; var20 <= this.lastViewDistance; ++var20) {
               for(int var21 = -this.lastViewDistance; var21 <= this.lastViewDistance; ++var21) {
                  RenderChunk var22 = this.viewArea.getRenderChunkAt(new BlockPos((var20 << 4) + 8, var19, (var21 << 4) + 8));
                  if(var22 != null && ((Culler)culler).isVisible(var22.bb)) {
                     var22.setFrame(var3);
                     var17.add(new LevelRenderer.RenderChunkInfo(var22, (Direction)null, 0));
                  }
               }
            }
         }

         this.minecraft.getProfiler().push("iteration");

         while(!((Queue)var17).isEmpty()) {
            LevelRenderer.RenderChunkInfo var19 = (LevelRenderer.RenderChunkInfo)var17.poll();
            RenderChunk var20 = var19.chunk;
            Direction var21 = var19.sourceDirection;
            this.renderChunks.add(var19);

            for(Direction var25 : DIRECTIONS) {
               RenderChunk var26 = this.getRelativeFrom(var13, var20, var25);
               if((!var18 || !var19.hasDirection(var25.getOpposite())) && (!var18 || var21 == null || var20.getCompiledChunk().facesCanSeeEachother(var21.getOpposite(), var25)) && var26 != null && var26.hasAllNeighbors() && var26.setFrame(var3) && ((Culler)culler).isVisible(var26.bb)) {
                  LevelRenderer.RenderChunkInfo var27 = new LevelRenderer.RenderChunkInfo(var26, var25, var19.step + 1);
                  var27.setDirections(var19.directions, var25);
                  var17.add(var27);
               }
            }
         }

         this.minecraft.getProfiler().pop();
      }

      this.minecraft.getProfiler().popPush("captureFrustum");
      if(this.captureFrustum) {
         this.captureFrustum(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);
         this.captureFrustum = false;
      }

      this.minecraft.getProfiler().popPush("rebuildNear");
      Set<RenderChunk> var17 = this.chunksToCompile;
      this.chunksToCompile = Sets.newLinkedHashSet();

      for(LevelRenderer.RenderChunkInfo var19 : this.renderChunks) {
         RenderChunk var20 = var19.chunk;
         if(var20.isDirty() || var17.contains(var20)) {
            this.needsUpdate = true;
            BlockPos var21 = var20.getOrigin().offset(8, 8, 8);
            boolean var22 = var21.distSqr(var11) < 768.0D;
            if(!var20.isDirtyFromPlayer() && !var22) {
               this.chunksToCompile.add(var20);
            } else {
               this.minecraft.getProfiler().push("build near");
               this.chunkRenderDispatcher.rebuildChunkSync(var20);
               var20.setNotDirty();
               this.minecraft.getProfiler().pop();
            }
         }
      }

      this.chunksToCompile.addAll(var17);
      this.minecraft.getProfiler().pop();
   }

   private Set getVisibleDirections(BlockPos blockPos) {
      VisGraph var2 = new VisGraph();
      BlockPos var3 = new BlockPos(blockPos.getX() >> 4 << 4, blockPos.getY() >> 4 << 4, blockPos.getZ() >> 4 << 4);
      LevelChunk var4 = this.level.getChunkAt(var3);

      for(BlockPos var6 : BlockPos.betweenClosed(var3, var3.offset(15, 15, 15))) {
         if(var4.getBlockState(var6).isSolidRender(this.level, var6)) {
            var2.setOpaque(var6);
         }
      }

      return var2.floodFill(blockPos);
   }

   @Nullable
   private RenderChunk getRelativeFrom(BlockPos blockPos, RenderChunk var2, Direction direction) {
      BlockPos blockPos = var2.getRelativeOrigin(direction);
      return Mth.abs(blockPos.getX() - blockPos.getX()) > this.lastViewDistance * 16?null:(blockPos.getY() >= 0 && blockPos.getY() < 256?(Mth.abs(blockPos.getZ() - blockPos.getZ()) > this.lastViewDistance * 16?null:this.viewArea.getRenderChunkAt(blockPos)):null);
   }

   private void captureFrustum(double var1, double var3, double var5) {
   }

   public int render(BlockLayer blockLayer, Camera camera) {
      Lighting.turnOff();
      if(blockLayer == BlockLayer.TRANSLUCENT) {
         this.minecraft.getProfiler().push("translucent_sort");
         double var3 = camera.getPosition().x - this.xTransparentOld;
         double var5 = camera.getPosition().y - this.yTransparentOld;
         double var7 = camera.getPosition().z - this.zTransparentOld;
         if(var3 * var3 + var5 * var5 + var7 * var7 > 1.0D) {
            this.xTransparentOld = camera.getPosition().x;
            this.yTransparentOld = camera.getPosition().y;
            this.zTransparentOld = camera.getPosition().z;
            int var9 = 0;

            for(LevelRenderer.RenderChunkInfo var11 : this.renderChunks) {
               if(var11.chunk.compiled.hasLayer(blockLayer) && var9++ < 15) {
                  this.chunkRenderDispatcher.resortChunkTransparencyAsync(var11.chunk);
               }
            }
         }

         this.minecraft.getProfiler().pop();
      }

      this.minecraft.getProfiler().push("filterempty");
      int var3 = 0;
      boolean var4 = blockLayer == BlockLayer.TRANSLUCENT;
      int var5 = var4?this.renderChunks.size() - 1:0;
      int var6 = var4?-1:this.renderChunks.size();
      int var7 = var4?-1:1;

      for(int var8 = var5; var8 != var6; var8 += var7) {
         RenderChunk var9 = ((LevelRenderer.RenderChunkInfo)this.renderChunks.get(var8)).chunk;
         if(!var9.getCompiledChunk().isEmpty(blockLayer)) {
            ++var3;
            this.renderList.add(var9, blockLayer);
         }
      }

      this.minecraft.getProfiler().popPush(() -> {
         return "render_" + blockLayer;
      });
      this.renderSameAsLast(blockLayer);
      this.minecraft.getProfiler().pop();
      return var3;
   }

   private void renderSameAsLast(BlockLayer blockLayer) {
      this.minecraft.gameRenderer.turnOnLightLayer();
      if(GLX.useVbo()) {
         GlStateManager.enableClientState('聴');
         GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
         GlStateManager.enableClientState('聸');
         GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
         GlStateManager.enableClientState('聸');
         GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
         GlStateManager.enableClientState('聶');
      }

      this.renderList.render(blockLayer);
      if(GLX.useVbo()) {
         for(VertexFormatElement var4 : DefaultVertexFormat.BLOCK.getElements()) {
            VertexFormatElement.Usage var5 = var4.getUsage();
            int var6 = var4.getIndex();
            switch(var5) {
            case POSITION:
               GlStateManager.disableClientState('聴');
               break;
            case UV:
               GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + var6);
               GlStateManager.disableClientState('聸');
               GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
               break;
            case COLOR:
               GlStateManager.disableClientState('聶');
               GlStateManager.clearCurrentColor();
            }
         }
      }

      this.minecraft.gameRenderer.turnOffLightLayer();
   }

   private void updateBlockDestruction(Iterator iterator) {
      while(iterator.hasNext()) {
         BlockDestructionProgress var2 = (BlockDestructionProgress)iterator.next();
         int var3 = var2.getUpdatedRenderTick();
         if(this.ticks - var3 > 400) {
            iterator.remove();
         }
      }

   }

   public void tick() {
      ++this.ticks;
      if(this.ticks % 20 == 0) {
         this.updateBlockDestruction(this.destroyingBlocks.values().iterator());
      }

   }

   private void renderEndSky() {
      GlStateManager.disableFog();
      GlStateManager.disableAlphaTest();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      Lighting.turnOff();
      GlStateManager.depthMask(false);
      this.textureManager.bind(END_SKY_LOCATION);
      Tesselator var1 = Tesselator.getInstance();
      BufferBuilder var2 = var1.getBuilder();

      for(int var3 = 0; var3 < 6; ++var3) {
         GlStateManager.pushMatrix();
         if(var3 == 1) {
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         }

         if(var3 == 2) {
            GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
         }

         if(var3 == 3) {
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
         }

         if(var3 == 4) {
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
         }

         if(var3 == 5) {
            GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
         }

         var2.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
         var2.vertex(-100.0D, -100.0D, -100.0D).uv(0.0D, 0.0D).color(40, 40, 40, 255).endVertex();
         var2.vertex(-100.0D, -100.0D, 100.0D).uv(0.0D, 16.0D).color(40, 40, 40, 255).endVertex();
         var2.vertex(100.0D, -100.0D, 100.0D).uv(16.0D, 16.0D).color(40, 40, 40, 255).endVertex();
         var2.vertex(100.0D, -100.0D, -100.0D).uv(16.0D, 0.0D).color(40, 40, 40, 255).endVertex();
         var1.end();
         GlStateManager.popMatrix();
      }

      GlStateManager.depthMask(true);
      GlStateManager.enableTexture();
      GlStateManager.disableBlend();
      GlStateManager.enableAlphaTest();
   }

   public void renderSky(float f) {
      if(this.minecraft.level.dimension.getType() == DimensionType.THE_END) {
         this.renderEndSky();
      } else if(this.minecraft.level.dimension.isNaturalDimension()) {
         GlStateManager.disableTexture();
         Vec3 var2 = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getBlockPosition(), f);
         float var3 = (float)var2.x;
         float var4 = (float)var2.y;
         float var5 = (float)var2.z;
         GlStateManager.color3f(var3, var4, var5);
         Tesselator var6 = Tesselator.getInstance();
         BufferBuilder var7 = var6.getBuilder();
         GlStateManager.depthMask(false);
         GlStateManager.enableFog();
         GlStateManager.color3f(var3, var4, var5);
         if(this.usingVbo) {
            this.skyBuffer.bind();
            GlStateManager.enableClientState('聴');
            GlStateManager.vertexPointer(3, 5126, 12, 0);
            this.skyBuffer.draw(7);
            VertexBuffer.unbind();
            GlStateManager.disableClientState('聴');
         } else {
            GlStateManager.callList(this.skyList);
         }

         GlStateManager.disableFog();
         GlStateManager.disableAlphaTest();
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         Lighting.turnOff();
         float[] vars8 = this.level.dimension.getSunriseColor(this.level.getTimeOfDay(f), f);
         if(vars8 != null) {
            GlStateManager.disableTexture();
            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(Mth.sin(this.level.getSunAngle(f)) < 0.0F?180.0F:0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
            float var9 = vars8[0];
            float var10 = vars8[1];
            float var11 = vars8[2];
            var7.begin(6, DefaultVertexFormat.POSITION_COLOR);
            var7.vertex(0.0D, 100.0D, 0.0D).color(var9, var10, var11, vars8[3]).endVertex();
            int var12 = 16;

            for(int var13 = 0; var13 <= 16; ++var13) {
               float var14 = (float)var13 * 6.2831855F / 16.0F;
               float var15 = Mth.sin(var14);
               float var16 = Mth.cos(var14);
               var7.vertex((double)(var15 * 120.0F), (double)(var16 * 120.0F), (double)(-var16 * 40.0F * vars8[3])).color(vars8[0], vars8[1], vars8[2], 0.0F).endVertex();
            }

            var6.end();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(7424);
         }

         GlStateManager.enableTexture();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.pushMatrix();
         float var9 = 1.0F - this.level.getRainLevel(f);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, var9);
         GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(this.level.getTimeOfDay(f) * 360.0F, 1.0F, 0.0F, 0.0F);
         float var10 = 30.0F;
         this.textureManager.bind(SUN_LOCATION);
         var7.begin(7, DefaultVertexFormat.POSITION_TEX);
         var7.vertex((double)(-var10), 100.0D, (double)(-var10)).uv(0.0D, 0.0D).endVertex();
         var7.vertex((double)var10, 100.0D, (double)(-var10)).uv(1.0D, 0.0D).endVertex();
         var7.vertex((double)var10, 100.0D, (double)var10).uv(1.0D, 1.0D).endVertex();
         var7.vertex((double)(-var10), 100.0D, (double)var10).uv(0.0D, 1.0D).endVertex();
         var6.end();
         var10 = 20.0F;
         this.textureManager.bind(MOON_LOCATION);
         int var11 = this.level.getMoonPhase();
         int var12 = var11 % 4;
         int var13 = var11 / 4 % 2;
         float var14 = (float)(var12 + 0) / 4.0F;
         float var15 = (float)(var13 + 0) / 2.0F;
         float var16 = (float)(var12 + 1) / 4.0F;
         float var17 = (float)(var13 + 1) / 2.0F;
         var7.begin(7, DefaultVertexFormat.POSITION_TEX);
         var7.vertex((double)(-var10), -100.0D, (double)var10).uv((double)var16, (double)var17).endVertex();
         var7.vertex((double)var10, -100.0D, (double)var10).uv((double)var14, (double)var17).endVertex();
         var7.vertex((double)var10, -100.0D, (double)(-var10)).uv((double)var14, (double)var15).endVertex();
         var7.vertex((double)(-var10), -100.0D, (double)(-var10)).uv((double)var16, (double)var15).endVertex();
         var6.end();
         GlStateManager.disableTexture();
         float var18 = this.level.getStarBrightness(f) * var9;
         if(var18 > 0.0F) {
            GlStateManager.color4f(var18, var18, var18, var18);
            if(this.usingVbo) {
               this.starBuffer.bind();
               GlStateManager.enableClientState('聴');
               GlStateManager.vertexPointer(3, 5126, 12, 0);
               this.starBuffer.draw(7);
               VertexBuffer.unbind();
               GlStateManager.disableClientState('聴');
            } else {
               GlStateManager.callList(this.starList);
            }
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.enableAlphaTest();
         GlStateManager.enableFog();
         GlStateManager.popMatrix();
         GlStateManager.disableTexture();
         GlStateManager.color3f(0.0F, 0.0F, 0.0F);
         double var9 = this.minecraft.player.getEyePosition(f).y - this.level.getHorizonHeight();
         if(var9 < 0.0D) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0F, 12.0F, 0.0F);
            if(this.usingVbo) {
               this.darkBuffer.bind();
               GlStateManager.enableClientState('聴');
               GlStateManager.vertexPointer(3, 5126, 12, 0);
               this.darkBuffer.draw(7);
               VertexBuffer.unbind();
               GlStateManager.disableClientState('聴');
            } else {
               GlStateManager.callList(this.darkList);
            }

            GlStateManager.popMatrix();
         }

         if(this.level.dimension.hasGround()) {
            GlStateManager.color3f(var3 * 0.2F + 0.04F, var4 * 0.2F + 0.04F, var5 * 0.6F + 0.1F);
         } else {
            GlStateManager.color3f(var3, var4, var5);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, -((float)(var9 - 16.0D)), 0.0F);
         GlStateManager.callList(this.darkList);
         GlStateManager.popMatrix();
         GlStateManager.enableTexture();
         GlStateManager.depthMask(true);
      }
   }

   public void renderClouds(float var1, double var2, double var4, double var6) {
      if(this.minecraft.level.dimension.isNaturalDimension()) {
         float var8 = 12.0F;
         float var9 = 4.0F;
         double var10 = 2.0E-4D;
         double var12 = (double)(((float)this.ticks + var1) * 0.03F);
         double var14 = (var2 + var12) / 12.0D;
         double var16 = (double)(this.level.dimension.getCloudHeight() - (float)var4 + 0.33F);
         double var18 = var6 / 12.0D + 0.33000001311302185D;
         var14 = var14 - (double)(Mth.floor(var14 / 2048.0D) * 2048);
         var18 = var18 - (double)(Mth.floor(var18 / 2048.0D) * 2048);
         float var20 = (float)(var14 - (double)Mth.floor(var14));
         float var21 = (float)(var16 / 4.0D - (double)Mth.floor(var16 / 4.0D)) * 4.0F;
         float var22 = (float)(var18 - (double)Mth.floor(var18));
         Vec3 var23 = this.level.getCloudColor(var1);
         int var24 = (int)Math.floor(var14);
         int var25 = (int)Math.floor(var16 / 4.0D);
         int var26 = (int)Math.floor(var18);
         if(var24 != this.prevCloudX || var25 != this.prevCloudY || var26 != this.prevCloudZ || this.minecraft.options.getCloudsType() != this.prevCloudsType || this.prevCloudColor.distanceToSqr(var23) > 2.0E-4D) {
            this.prevCloudX = var24;
            this.prevCloudY = var25;
            this.prevCloudZ = var26;
            this.prevCloudColor = var23;
            this.prevCloudsType = this.minecraft.options.getCloudsType();
            this.generateClouds = true;
         }

         if(this.generateClouds) {
            this.generateClouds = false;
            Tesselator var27 = Tesselator.getInstance();
            BufferBuilder var28 = var27.getBuilder();
            if(this.cloudBuffer != null) {
               this.cloudBuffer.delete();
            }

            if(this.cloudList >= 0) {
               MemoryTracker.releaseList(this.cloudList);
               this.cloudList = -1;
            }

            if(this.usingVbo) {
               this.cloudBuffer = new VertexBuffer(DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
               this.buildClouds(var28, var14, var16, var18, var23);
               var28.end();
               var28.clear();
               this.cloudBuffer.upload(var28.getBuffer());
            } else {
               this.cloudList = MemoryTracker.genLists(1);
               GlStateManager.newList(this.cloudList, 4864);
               this.buildClouds(var28, var14, var16, var18, var23);
               var27.end();
               GlStateManager.endList();
            }
         }

         GlStateManager.disableCull();
         this.textureManager.bind(CLOUDS_LOCATION);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(12.0F, 1.0F, 12.0F);
         GlStateManager.translatef(-var20, var21, -var22);
         if(this.usingVbo && this.cloudBuffer != null) {
            this.cloudBuffer.bind();
            GlStateManager.enableClientState('聴');
            GlStateManager.enableClientState('聸');
            GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
            GlStateManager.enableClientState('聶');
            GlStateManager.enableClientState('聵');
            GlStateManager.vertexPointer(3, 5126, 28, 0);
            GlStateManager.texCoordPointer(2, 5126, 28, 12);
            GlStateManager.colorPointer(4, 5121, 28, 20);
            GlStateManager.normalPointer(5120, 28, 24);
            int var27 = this.prevCloudsType == CloudStatus.FANCY?0:1;

            for(int var28 = var27; var28 < 2; ++var28) {
               if(var28 == 0) {
                  GlStateManager.colorMask(false, false, false, false);
               } else {
                  GlStateManager.colorMask(true, true, true, true);
               }

               this.cloudBuffer.draw(7);
            }

            VertexBuffer.unbind();
            GlStateManager.disableClientState('聴');
            GlStateManager.disableClientState('聸');
            GlStateManager.disableClientState('聶');
            GlStateManager.disableClientState('聵');
         } else if(this.cloudList >= 0) {
            int var27 = this.prevCloudsType == CloudStatus.FANCY?0:1;

            for(int var28 = var27; var28 < 2; ++var28) {
               if(var28 == 0) {
                  GlStateManager.colorMask(false, false, false, false);
               } else {
                  GlStateManager.colorMask(true, true, true, true);
               }

               GlStateManager.callList(this.cloudList);
            }
         }

         GlStateManager.popMatrix();
         GlStateManager.clearCurrentColor();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.enableCull();
      }
   }

   private void buildClouds(BufferBuilder bufferBuilder, double var2, double var4, double var6, Vec3 vec3) {
      float var9 = 4.0F;
      float var10 = 0.00390625F;
      int var11 = 8;
      int var12 = 4;
      float var13 = 9.765625E-4F;
      float var14 = (float)Mth.floor(var2) * 0.00390625F;
      float var15 = (float)Mth.floor(var6) * 0.00390625F;
      float var16 = (float)vec3.x;
      float var17 = (float)vec3.y;
      float var18 = (float)vec3.z;
      float var19 = var16 * 0.9F;
      float var20 = var17 * 0.9F;
      float var21 = var18 * 0.9F;
      float var22 = var16 * 0.7F;
      float var23 = var17 * 0.7F;
      float var24 = var18 * 0.7F;
      float var25 = var16 * 0.8F;
      float var26 = var17 * 0.8F;
      float var27 = var18 * 0.8F;
      bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
      float var28 = (float)Math.floor(var4 / 4.0D) * 4.0F;
      if(this.prevCloudsType == CloudStatus.FANCY) {
         for(int var29 = -3; var29 <= 4; ++var29) {
            for(int var30 = -3; var30 <= 4; ++var30) {
               float var31 = (float)(var29 * 8);
               float var32 = (float)(var30 * 8);
               if(var28 > -5.0F) {
                  bufferBuilder.vertex((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 8.0F)).uv((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).color(var22, var23, var24, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                  bufferBuilder.vertex((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + 8.0F)).uv((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).color(var22, var23, var24, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                  bufferBuilder.vertex((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + 0.0F)).uv((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).color(var22, var23, var24, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                  bufferBuilder.vertex((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 0.0F)).uv((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).color(var22, var23, var24, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               }

               if(var28 <= 5.0F) {
                  bufferBuilder.vertex((double)(var31 + 0.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 8.0F)).uv((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).color(var16, var17, var18, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                  bufferBuilder.vertex((double)(var31 + 8.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 8.0F)).uv((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).color(var16, var17, var18, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                  bufferBuilder.vertex((double)(var31 + 8.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 0.0F)).uv((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).color(var16, var17, var18, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                  bufferBuilder.vertex((double)(var31 + 0.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 0.0F)).uv((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).color(var16, var17, var18, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
               }

               if(var29 > -1) {
                  for(int var33 = 0; var33 < 8; ++var33) {
                     bufferBuilder.vertex((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 8.0F)).uv((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).color(var19, var20, var21, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                     bufferBuilder.vertex((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + 8.0F)).uv((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).color(var19, var20, var21, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                     bufferBuilder.vertex((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + 0.0F)).uv((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).color(var19, var20, var21, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                     bufferBuilder.vertex((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 0.0F)).uv((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).color(var19, var20, var21, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                  }
               }

               if(var29 <= 1) {
                  for(int var33 = 0; var33 < 8; ++var33) {
                     bufferBuilder.vertex((double)(var31 + (float)var33 + 1.0F - 9.765625E-4F), (double)(var28 + 0.0F), (double)(var32 + 8.0F)).uv((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).color(var19, var20, var21, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                     bufferBuilder.vertex((double)(var31 + (float)var33 + 1.0F - 9.765625E-4F), (double)(var28 + 4.0F), (double)(var32 + 8.0F)).uv((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).color(var19, var20, var21, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                     bufferBuilder.vertex((double)(var31 + (float)var33 + 1.0F - 9.765625E-4F), (double)(var28 + 4.0F), (double)(var32 + 0.0F)).uv((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).color(var19, var20, var21, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                     bufferBuilder.vertex((double)(var31 + (float)var33 + 1.0F - 9.765625E-4F), (double)(var28 + 0.0F), (double)(var32 + 0.0F)).uv((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).color(var19, var20, var21, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                  }
               }

               if(var30 > -1) {
                  for(int var33 = 0; var33 < 8; ++var33) {
                     bufferBuilder.vertex((double)(var31 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var33 + 0.0F)).uv((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                     bufferBuilder.vertex((double)(var31 + 8.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var33 + 0.0F)).uv((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                     bufferBuilder.vertex((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var33 + 0.0F)).uv((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                     bufferBuilder.vertex((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var33 + 0.0F)).uv((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                  }
               }

               if(var30 <= 1) {
                  for(int var33 = 0; var33 < 8; ++var33) {
                     bufferBuilder.vertex((double)(var31 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var33 + 1.0F - 9.765625E-4F)).uv((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                     bufferBuilder.vertex((double)(var31 + 8.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var33 + 1.0F - 9.765625E-4F)).uv((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                     bufferBuilder.vertex((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var33 + 1.0F - 9.765625E-4F)).uv((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                     bufferBuilder.vertex((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var33 + 1.0F - 9.765625E-4F)).uv((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                  }
               }
            }
         }
      } else {
         int var29 = 1;
         int var30 = 32;

         for(int var31 = -32; var31 < 32; var31 += 32) {
            for(int var32 = -32; var32 < 32; var32 += 32) {
               bufferBuilder.vertex((double)(var31 + 0), (double)var28, (double)(var32 + 32)).uv((double)((float)(var31 + 0) * 0.00390625F + var14), (double)((float)(var32 + 32) * 0.00390625F + var15)).color(var16, var17, var18, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               bufferBuilder.vertex((double)(var31 + 32), (double)var28, (double)(var32 + 32)).uv((double)((float)(var31 + 32) * 0.00390625F + var14), (double)((float)(var32 + 32) * 0.00390625F + var15)).color(var16, var17, var18, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               bufferBuilder.vertex((double)(var31 + 32), (double)var28, (double)(var32 + 0)).uv((double)((float)(var31 + 32) * 0.00390625F + var14), (double)((float)(var32 + 0) * 0.00390625F + var15)).color(var16, var17, var18, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               bufferBuilder.vertex((double)(var31 + 0), (double)var28, (double)(var32 + 0)).uv((double)((float)(var31 + 0) * 0.00390625F + var14), (double)((float)(var32 + 0) * 0.00390625F + var15)).color(var16, var17, var18, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
            }
         }
      }

   }

   public void compileChunksUntil(long l) {
      this.needsUpdate |= this.chunkRenderDispatcher.uploadAllPendingUploadsUntil(l);
      if(!this.chunksToCompile.isEmpty()) {
         Iterator<RenderChunk> var3 = this.chunksToCompile.iterator();

         while(var3.hasNext()) {
            RenderChunk var4 = (RenderChunk)var3.next();
            boolean var5;
            if(var4.isDirtyFromPlayer()) {
               var5 = this.chunkRenderDispatcher.rebuildChunkSync(var4);
            } else {
               var5 = this.chunkRenderDispatcher.rebuildChunkAsync(var4);
            }

            if(!var5) {
               break;
            }

            var4.setNotDirty();
            var3.remove();
            long var6 = l - Util.getNanos();
            if(var6 < 0L) {
               break;
            }
         }
      }

   }

   public void renderWorldBounds(Camera camera, float var2) {
      Tesselator var3 = Tesselator.getInstance();
      BufferBuilder var4 = var3.getBuilder();
      WorldBorder var5 = this.level.getWorldBorder();
      double var6 = (double)(this.minecraft.options.renderDistance * 16);
      if(camera.getPosition().x >= var5.getMaxX() - var6 || camera.getPosition().x <= var5.getMinX() + var6 || camera.getPosition().z >= var5.getMaxZ() - var6 || camera.getPosition().z <= var5.getMinZ() + var6) {
         double var8 = 1.0D - var5.getDistanceToBorder(camera.getPosition().x, camera.getPosition().z) / var6;
         var8 = Math.pow(var8, 4.0D);
         double var10 = camera.getPosition().x;
         double var12 = camera.getPosition().y;
         double var14 = camera.getPosition().z;
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         this.textureManager.bind(FORCEFIELD_LOCATION);
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         int var16 = var5.getStatus().getColor();
         float var17 = (float)(var16 >> 16 & 255) / 255.0F;
         float var18 = (float)(var16 >> 8 & 255) / 255.0F;
         float var19 = (float)(var16 & 255) / 255.0F;
         GlStateManager.color4f(var17, var18, var19, (float)var8);
         GlStateManager.polygonOffset(-3.0F, -3.0F);
         GlStateManager.enablePolygonOffset();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableAlphaTest();
         GlStateManager.disableCull();
         float var20 = (float)(Util.getMillis() % 3000L) / 3000.0F;
         float var21 = 0.0F;
         float var22 = 0.0F;
         float var23 = 128.0F;
         var4.begin(7, DefaultVertexFormat.POSITION_TEX);
         var4.offset(-var10, -var12, -var14);
         double var24 = Math.max((double)Mth.floor(var14 - var6), var5.getMinZ());
         double var26 = Math.min((double)Mth.ceil(var14 + var6), var5.getMaxZ());
         if(var10 > var5.getMaxX() - var6) {
            float var28 = 0.0F;

            for(double var29 = var24; var29 < var26; var28 += 0.5F) {
               double var31 = Math.min(1.0D, var26 - var29);
               float var33 = (float)var31 * 0.5F;
               var4.vertex(var5.getMaxX(), 256.0D, var29).uv((double)(var20 + var28), (double)(var20 + 0.0F)).endVertex();
               var4.vertex(var5.getMaxX(), 256.0D, var29 + var31).uv((double)(var20 + var33 + var28), (double)(var20 + 0.0F)).endVertex();
               var4.vertex(var5.getMaxX(), 0.0D, var29 + var31).uv((double)(var20 + var33 + var28), (double)(var20 + 128.0F)).endVertex();
               var4.vertex(var5.getMaxX(), 0.0D, var29).uv((double)(var20 + var28), (double)(var20 + 128.0F)).endVertex();
               ++var29;
            }
         }

         if(var10 < var5.getMinX() + var6) {
            float var28 = 0.0F;

            for(double var29 = var24; var29 < var26; var28 += 0.5F) {
               double var31 = Math.min(1.0D, var26 - var29);
               float var33 = (float)var31 * 0.5F;
               var4.vertex(var5.getMinX(), 256.0D, var29).uv((double)(var20 + var28), (double)(var20 + 0.0F)).endVertex();
               var4.vertex(var5.getMinX(), 256.0D, var29 + var31).uv((double)(var20 + var33 + var28), (double)(var20 + 0.0F)).endVertex();
               var4.vertex(var5.getMinX(), 0.0D, var29 + var31).uv((double)(var20 + var33 + var28), (double)(var20 + 128.0F)).endVertex();
               var4.vertex(var5.getMinX(), 0.0D, var29).uv((double)(var20 + var28), (double)(var20 + 128.0F)).endVertex();
               ++var29;
            }
         }

         var24 = Math.max((double)Mth.floor(var10 - var6), var5.getMinX());
         var26 = Math.min((double)Mth.ceil(var10 + var6), var5.getMaxX());
         if(var14 > var5.getMaxZ() - var6) {
            float var28 = 0.0F;

            for(double var29 = var24; var29 < var26; var28 += 0.5F) {
               double var31 = Math.min(1.0D, var26 - var29);
               float var33 = (float)var31 * 0.5F;
               var4.vertex(var29, 256.0D, var5.getMaxZ()).uv((double)(var20 + var28), (double)(var20 + 0.0F)).endVertex();
               var4.vertex(var29 + var31, 256.0D, var5.getMaxZ()).uv((double)(var20 + var33 + var28), (double)(var20 + 0.0F)).endVertex();
               var4.vertex(var29 + var31, 0.0D, var5.getMaxZ()).uv((double)(var20 + var33 + var28), (double)(var20 + 128.0F)).endVertex();
               var4.vertex(var29, 0.0D, var5.getMaxZ()).uv((double)(var20 + var28), (double)(var20 + 128.0F)).endVertex();
               ++var29;
            }
         }

         if(var14 < var5.getMinZ() + var6) {
            float var28 = 0.0F;

            for(double var29 = var24; var29 < var26; var28 += 0.5F) {
               double var31 = Math.min(1.0D, var26 - var29);
               float var33 = (float)var31 * 0.5F;
               var4.vertex(var29, 256.0D, var5.getMinZ()).uv((double)(var20 + var28), (double)(var20 + 0.0F)).endVertex();
               var4.vertex(var29 + var31, 256.0D, var5.getMinZ()).uv((double)(var20 + var33 + var28), (double)(var20 + 0.0F)).endVertex();
               var4.vertex(var29 + var31, 0.0D, var5.getMinZ()).uv((double)(var20 + var33 + var28), (double)(var20 + 128.0F)).endVertex();
               var4.vertex(var29, 0.0D, var5.getMinZ()).uv((double)(var20 + var28), (double)(var20 + 128.0F)).endVertex();
               ++var29;
            }
         }

         var3.end();
         var4.offset(0.0D, 0.0D, 0.0D);
         GlStateManager.enableCull();
         GlStateManager.disableAlphaTest();
         GlStateManager.polygonOffset(0.0F, 0.0F);
         GlStateManager.disablePolygonOffset();
         GlStateManager.enableAlphaTest();
         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
      }
   }

   private void setupDestroyState() {
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.enableBlend();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.polygonOffset(-1.0F, -10.0F);
      GlStateManager.enablePolygonOffset();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableAlphaTest();
      GlStateManager.pushMatrix();
   }

   private void restoreDestroyState() {
      GlStateManager.disableAlphaTest();
      GlStateManager.polygonOffset(0.0F, 0.0F);
      GlStateManager.disablePolygonOffset();
      GlStateManager.enableAlphaTest();
      GlStateManager.depthMask(true);
      GlStateManager.popMatrix();
   }

   public void renderDestroyAnimation(Tesselator tesselator, BufferBuilder bufferBuilder, Camera camera) {
      double var4 = camera.getPosition().x;
      double var6 = camera.getPosition().y;
      double var8 = camera.getPosition().z;
      if(!this.destroyingBlocks.isEmpty()) {
         this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
         this.setupDestroyState();
         bufferBuilder.begin(7, DefaultVertexFormat.BLOCK);
         bufferBuilder.offset(-var4, -var6, -var8);
         bufferBuilder.noColor();
         Iterator<BlockDestructionProgress> var10 = this.destroyingBlocks.values().iterator();

         while(var10.hasNext()) {
            BlockDestructionProgress var11 = (BlockDestructionProgress)var10.next();
            BlockPos var12 = var11.getPos();
            Block var13 = this.level.getBlockState(var12).getBlock();
            if(!(var13 instanceof ChestBlock) && !(var13 instanceof EnderChestBlock) && !(var13 instanceof SignBlock) && !(var13 instanceof AbstractSkullBlock)) {
               double var14 = (double)var12.getX() - var4;
               double var16 = (double)var12.getY() - var6;
               double var18 = (double)var12.getZ() - var8;
               if(var14 * var14 + var16 * var16 + var18 * var18 > 1024.0D) {
                  var10.remove();
               } else {
                  BlockState var20 = this.level.getBlockState(var12);
                  if(!var20.isAir()) {
                     int var21 = var11.getProgress();
                     TextureAtlasSprite var22 = this.breakingTextures[var21];
                     BlockRenderDispatcher var23 = this.minecraft.getBlockRenderer();
                     var23.renderBreakingTexture(var20, var12, var22, this.level);
                  }
               }
            }
         }

         tesselator.end();
         bufferBuilder.offset(0.0D, 0.0D, 0.0D);
         this.restoreDestroyState();
      }

   }

   public void renderHitOutline(Camera camera, HitResult hitResult, int var3) {
      if(var3 == 0 && hitResult.getType() == HitResult.Type.BLOCK) {
         BlockPos var4 = ((BlockHitResult)hitResult).getBlockPos();
         BlockState var5 = this.level.getBlockState(var4);
         if(!var5.isAir() && this.level.getWorldBorder().isWithinBounds(var4)) {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.lineWidth(Math.max(2.5F, (float)this.minecraft.window.getWidth() / 1920.0F * 2.5F));
            GlStateManager.disableTexture();
            GlStateManager.depthMask(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1.0F, 1.0F, 0.999F);
            double var6 = camera.getPosition().x;
            double var8 = camera.getPosition().y;
            double var10 = camera.getPosition().z;
            renderShape(var5.getShape(this.level, var4, CollisionContext.of(camera.getEntity())), (double)var4.getX() - var6, (double)var4.getY() - var8, (double)var4.getZ() - var10, 0.0F, 0.0F, 0.0F, 0.4F);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();
         }
      }

   }

   public static void renderVoxelShape(VoxelShape voxelShape, double var1, double var3, double var5, float var7, float var8, float var9, float var10) {
      List<AABB> var11 = voxelShape.toAabbs();
      int var12 = Mth.ceil((double)var11.size() / 3.0D);

      for(int var13 = 0; var13 < var11.size(); ++var13) {
         AABB var14 = (AABB)var11.get(var13);
         float var15 = ((float)var13 % (float)var12 + 1.0F) / (float)var12;
         float var16 = (float)(var13 / var12);
         float var17 = var15 * (float)(var16 == 0.0F?1:0);
         float var18 = var15 * (float)(var16 == 1.0F?1:0);
         float var19 = var15 * (float)(var16 == 2.0F?1:0);
         renderShape(Shapes.create(var14.move(0.0D, 0.0D, 0.0D)), var1, var3, var5, var17, var18, var19, 1.0F);
      }

   }

   public static void renderShape(VoxelShape voxelShape, double var1, double var3, double var5, float var7, float var8, float var9, float var10) {
      Tesselator var11 = Tesselator.getInstance();
      BufferBuilder var12 = var11.getBuilder();
      var12.begin(1, DefaultVertexFormat.POSITION_COLOR);
      voxelShape.forAllEdges((var11, var13, var15, var17, var19, var21) -> {
         var12.vertex(var11 + var1, var13 + var3, var15 + var5).color(var7, var8, var9, var10).endVertex();
         var12.vertex(var17 + var1, var19 + var3, var21 + var5).color(var7, var8, var9, var10).endVertex();
      });
      var11.end();
   }

   public static void renderLineBox(AABB aABB, float var1, float var2, float var3, float var4) {
      renderLineBox(aABB.minX, aABB.minY, aABB.minZ, aABB.maxX, aABB.maxY, aABB.maxZ, var1, var2, var3, var4);
   }

   public static void renderLineBox(double var0, double var2, double var4, double var6, double var8, double var10, float var12, float var13, float var14, float var15) {
      Tesselator var16 = Tesselator.getInstance();
      BufferBuilder var17 = var16.getBuilder();
      var17.begin(3, DefaultVertexFormat.POSITION_COLOR);
      addChainedLineBoxVertices(var17, var0, var2, var4, var6, var8, var10, var12, var13, var14, var15);
      var16.end();
   }

   public static void addChainedLineBoxVertices(BufferBuilder bufferBuilder, double var1, double var3, double var5, double var7, double var9, double var11, float var13, float var14, float var15, float var16) {
      bufferBuilder.vertex(var1, var3, var5).color(var13, var14, var15, 0.0F).endVertex();
      bufferBuilder.vertex(var1, var3, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var3, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var3, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var3, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var3, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var9, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var9, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var9, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var9, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var9, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var9, var11).color(var13, var14, var15, 0.0F).endVertex();
      bufferBuilder.vertex(var1, var3, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var9, var11).color(var13, var14, var15, 0.0F).endVertex();
      bufferBuilder.vertex(var7, var3, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var9, var5).color(var13, var14, var15, 0.0F).endVertex();
      bufferBuilder.vertex(var7, var3, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var3, var5).color(var13, var14, var15, 0.0F).endVertex();
   }

   public static void addChainedFilledBoxVertices(BufferBuilder bufferBuilder, double var1, double var3, double var5, double var7, double var9, double var11, float var13, float var14, float var15, float var16) {
      bufferBuilder.vertex(var1, var3, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var3, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var3, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var3, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var9, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var9, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var9, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var3, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var9, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var3, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var3, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var3, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var9, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var9, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var9, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var3, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var9, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var3, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var3, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var3, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var3, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var3, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var3, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var9, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var9, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var1, var9, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var9, var5).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var9, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var9, var11).color(var13, var14, var15, var16).endVertex();
      bufferBuilder.vertex(var7, var9, var11).color(var13, var14, var15, var16).endVertex();
   }

   public void blockChanged(BlockGetter blockGetter, BlockPos blockPos, BlockState var3, BlockState var4, int var5) {
      this.setBlockDirty(blockPos, (var5 & 8) != 0);
   }

   private void setBlockDirty(BlockPos blockPos, boolean var2) {
      for(int var3 = blockPos.getZ() - 1; var3 <= blockPos.getZ() + 1; ++var3) {
         for(int var4 = blockPos.getX() - 1; var4 <= blockPos.getX() + 1; ++var4) {
            for(int var5 = blockPos.getY() - 1; var5 <= blockPos.getY() + 1; ++var5) {
               this.setSectionDirty(var4 >> 4, var5 >> 4, var3 >> 4, var2);
            }
         }
      }

   }

   public void setBlocksDirty(int var1, int var2, int var3, int var4, int var5, int var6) {
      for(int var7 = var3 - 1; var7 <= var6 + 1; ++var7) {
         for(int var8 = var1 - 1; var8 <= var4 + 1; ++var8) {
            for(int var9 = var2 - 1; var9 <= var5 + 1; ++var9) {
               this.setSectionDirty(var8 >> 4, var9 >> 4, var7 >> 4);
            }
         }
      }

   }

   public void setBlockDirty(BlockPos blockPos, BlockState var2, BlockState var3) {
      if(this.minecraft.getModelManager().requiresRender(var2, var3)) {
         this.setBlocksDirty(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
      }

   }

   public void setSectionDirtyWithNeighbors(int var1, int var2, int var3) {
      for(int var4 = var3 - 1; var4 <= var3 + 1; ++var4) {
         for(int var5 = var1 - 1; var5 <= var1 + 1; ++var5) {
            for(int var6 = var2 - 1; var6 <= var2 + 1; ++var6) {
               this.setSectionDirty(var5, var6, var4);
            }
         }
      }

   }

   public void setSectionDirty(int var1, int var2, int var3) {
      this.setSectionDirty(var1, var2, var3, false);
   }

   private void setSectionDirty(int var1, int var2, int var3, boolean var4) {
      this.viewArea.setDirty(var1, var2, var3, var4);
   }

   public void playStreamingMusic(@Nullable SoundEvent soundEvent, BlockPos blockPos) {
      SoundInstance var3 = (SoundInstance)this.playingRecords.get(blockPos);
      if(var3 != null) {
         this.minecraft.getSoundManager().stop(var3);
         this.playingRecords.remove(blockPos);
      }

      if(soundEvent != null) {
         RecordItem var4 = RecordItem.getBySound(soundEvent);
         if(var4 != null) {
            this.minecraft.gui.setNowPlaying(var4.getDisplayName().getColoredString());
         }

         SimpleSoundInstance var5 = SimpleSoundInstance.forRecord(soundEvent, (float)blockPos.getX(), (float)blockPos.getY(), (float)blockPos.getZ());
         this.playingRecords.put(blockPos, var5);
         this.minecraft.getSoundManager().play(var5);
      }

      this.notifyNearbyEntities(this.level, blockPos, soundEvent != null);
   }

   private void notifyNearbyEntities(Level level, BlockPos blockPos, boolean var3) {
      for(LivingEntity var6 : level.getEntitiesOfClass(LivingEntity.class, (new AABB(blockPos)).inflate(3.0D))) {
         var6.setRecordPlayingNearby(blockPos, var3);
      }

   }

   public void addParticle(ParticleOptions particleOptions, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      this.addParticle(particleOptions, var2, false, var3, var5, var7, var9, var11, var13);
   }

   public void addParticle(ParticleOptions particleOptions, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14) {
      try {
         this.addParticleInternal(particleOptions, var2, var3, var4, var6, var8, var10, var12, var14);
      } catch (Throwable var19) {
         CrashReport var17 = CrashReport.forThrowable(var19, "Exception while adding particle");
         CrashReportCategory var18 = var17.addCategory("Particle being added");
         var18.setDetail("ID", (Object)Registry.PARTICLE_TYPE.getKey(particleOptions.getType()));
         var18.setDetail("Parameters", (Object)particleOptions.writeToString());
         var18.setDetail("Position", () -> {
            return CrashReportCategory.formatLocation(var4, var6, var8);
         });
         throw new ReportedException(var17);
      }
   }

   private void addParticle(ParticleOptions particleOptions, double var2, double var4, double var6, double var8, double var10, double var12) {
      this.addParticle(particleOptions, particleOptions.getType().getOverrideLimiter(), var2, var4, var6, var8, var10, var12);
   }

   @Nullable
   private Particle addParticleInternal(ParticleOptions particleOptions, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      return this.addParticleInternal(particleOptions, var2, false, var3, var5, var7, var9, var11, var13);
   }

   @Nullable
   private Particle addParticleInternal(ParticleOptions particleOptions, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14) {
      Camera var16 = this.minecraft.gameRenderer.getMainCamera();
      if(this.minecraft != null && var16.isInitialized() && this.minecraft.particleEngine != null) {
         ParticleStatus var17 = this.calculateParticleLevel(var3);
         return var2?this.minecraft.particleEngine.createParticle(particleOptions, var4, var6, var8, var10, var12, var14):(var16.getPosition().distanceToSqr(var4, var6, var8) > 1024.0D?null:(var17 == ParticleStatus.MINIMAL?null:this.minecraft.particleEngine.createParticle(particleOptions, var4, var6, var8, var10, var12, var14)));
      } else {
         return null;
      }
   }

   private ParticleStatus calculateParticleLevel(boolean b) {
      ParticleStatus particleStatus = this.minecraft.options.particles;
      if(b && particleStatus == ParticleStatus.MINIMAL && this.level.random.nextInt(10) == 0) {
         particleStatus = ParticleStatus.DECREASED;
      }

      if(particleStatus == ParticleStatus.DECREASED && this.level.random.nextInt(3) == 0) {
         particleStatus = ParticleStatus.MINIMAL;
      }

      return particleStatus;
   }

   public void clear() {
   }

   public void globalLevelEvent(int var1, BlockPos blockPos, int var3) {
      switch(var1) {
      case 1023:
      case 1028:
      case 1038:
         Camera var4 = this.minecraft.gameRenderer.getMainCamera();
         if(var4.isInitialized()) {
            double var5 = (double)blockPos.getX() - var4.getPosition().x;
            double var7 = (double)blockPos.getY() - var4.getPosition().y;
            double var9 = (double)blockPos.getZ() - var4.getPosition().z;
            double var11 = Math.sqrt(var5 * var5 + var7 * var7 + var9 * var9);
            double var13 = var4.getPosition().x;
            double var15 = var4.getPosition().y;
            double var17 = var4.getPosition().z;
            if(var11 > 0.0D) {
               var13 += var5 / var11 * 2.0D;
               var15 += var7 / var11 * 2.0D;
               var17 += var9 / var11 * 2.0D;
            }

            if(var1 == 1023) {
               this.level.playLocalSound(var13, var15, var17, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0F, 1.0F, false);
            } else if(var1 == 1038) {
               this.level.playLocalSound(var13, var15, var17, SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 1.0F, 1.0F, false);
            } else {
               this.level.playLocalSound(var13, var15, var17, SoundEvents.ENDER_DRAGON_DEATH, SoundSource.HOSTILE, 5.0F, 1.0F, false);
            }
         }
      default:
      }
   }

   public void levelEvent(Player player, int var2, BlockPos blockPos, int var4) {
      Random var5 = this.level.random;
      switch(var2) {
      case 1000:
         this.level.playLocalSound(blockPos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1001:
         this.level.playLocalSound(blockPos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0F, 1.2F, false);
         break;
      case 1002:
         this.level.playLocalSound(blockPos, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 1.0F, 1.2F, false);
         break;
      case 1003:
         this.level.playLocalSound(blockPos, SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 1.0F, 1.2F, false);
         break;
      case 1004:
         this.level.playLocalSound(blockPos, SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.2F, false);
         break;
      case 1005:
         this.level.playLocalSound(blockPos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1006:
         this.level.playLocalSound(blockPos, SoundEvents.WOODEN_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1007:
         this.level.playLocalSound(blockPos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1008:
         this.level.playLocalSound(blockPos, SoundEvents.FENCE_GATE_OPEN, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1009:
         this.level.playLocalSound(blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (var5.nextFloat() - var5.nextFloat()) * 0.8F, false);
         break;
      case 1010:
         if(Item.byId(var4) instanceof RecordItem) {
            this.playStreamingMusic(((RecordItem)Item.byId(var4)).getSound(), blockPos);
         } else {
            this.playStreamingMusic((SoundEvent)null, blockPos);
         }
         break;
      case 1011:
         this.level.playLocalSound(blockPos, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1012:
         this.level.playLocalSound(blockPos, SoundEvents.WOODEN_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1013:
         this.level.playLocalSound(blockPos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1014:
         this.level.playLocalSound(blockPos, SoundEvents.FENCE_GATE_CLOSE, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1015:
         this.level.playLocalSound(blockPos, SoundEvents.GHAST_WARN, SoundSource.HOSTILE, 10.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1016:
         this.level.playLocalSound(blockPos, SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 10.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1017:
         this.level.playLocalSound(blockPos, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 10.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1018:
         this.level.playLocalSound(blockPos, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1019:
         this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1020:
         this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1021:
         this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1022:
         this.level.playLocalSound(blockPos, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1024:
         this.level.playLocalSound(blockPos, SoundEvents.WITHER_SHOOT, SoundSource.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1025:
         this.level.playLocalSound(blockPos, SoundEvents.BAT_TAKEOFF, SoundSource.NEUTRAL, 0.05F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1026:
         this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1027:
         this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.NEUTRAL, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1029:
         this.level.playLocalSound(blockPos, SoundEvents.ANVIL_DESTROY, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1030:
         this.level.playLocalSound(blockPos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1031:
         this.level.playLocalSound(blockPos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.3F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1032:
         this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PORTAL_TRAVEL, var5.nextFloat() * 0.4F + 0.8F));
         break;
      case 1033:
         this.level.playLocalSound(blockPos, SoundEvents.CHORUS_FLOWER_GROW, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1034:
         this.level.playLocalSound(blockPos, SoundEvents.CHORUS_FLOWER_DEATH, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1035:
         this.level.playLocalSound(blockPos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1036:
         this.level.playLocalSound(blockPos, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1037:
         this.level.playLocalSound(blockPos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1039:
         this.level.playLocalSound(blockPos, SoundEvents.PHANTOM_BITE, SoundSource.HOSTILE, 0.3F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1040:
         this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.NEUTRAL, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1041:
         this.level.playLocalSound(blockPos, SoundEvents.HUSK_CONVERTED_TO_ZOMBIE, SoundSource.NEUTRAL, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1042:
         this.level.playLocalSound(blockPos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1043:
         this.level.playLocalSound(blockPos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1500:
         ComposterBlock.handleFill(this.level, blockPos, var4 > 0);
         break;
      case 1501:
         this.level.playLocalSound(blockPos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (this.level.getRandom().nextFloat() - this.level.getRandom().nextFloat()) * 0.8F, false);

         for(int var7 = 0; var7 < 8; ++var7) {
            this.level.addParticle(ParticleTypes.LARGE_SMOKE, (double)blockPos.getX() + Math.random(), (double)blockPos.getY() + 1.2D, (double)blockPos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
         }

         return;
      case 1502:
         this.level.playLocalSound(blockPos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5F, 2.6F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.8F, false);

         for(int var7 = 0; var7 < 5; ++var7) {
            double var8 = (double)blockPos.getX() + var5.nextDouble() * 0.6D + 0.2D;
            double var10 = (double)blockPos.getY() + var5.nextDouble() * 0.6D + 0.2D;
            double var12 = (double)blockPos.getZ() + var5.nextDouble() * 0.6D + 0.2D;
            this.level.addParticle(ParticleTypes.SMOKE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
         }

         return;
      case 1503:
         this.level.playLocalSound(blockPos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);

         for(int var7 = 0; var7 < 16; ++var7) {
            double var8 = (double)((float)blockPos.getX() + (5.0F + var5.nextFloat() * 6.0F) / 16.0F);
            double var10 = (double)((float)blockPos.getY() + 0.8125F);
            double var12 = (double)((float)blockPos.getZ() + (5.0F + var5.nextFloat() * 6.0F) / 16.0F);
            double var14 = 0.0D;
            double var16 = 0.0D;
            double var18 = 0.0D;
            this.level.addParticle(ParticleTypes.SMOKE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
         }

         return;
      case 2000:
         Direction var6 = Direction.from3DDataValue(var4);
         int var7 = var6.getStepX();
         int var8 = var6.getStepY();
         int var9 = var6.getStepZ();
         double var10 = (double)blockPos.getX() + (double)var7 * 0.6D + 0.5D;
         double var12 = (double)blockPos.getY() + (double)var8 * 0.6D + 0.5D;
         double var14 = (double)blockPos.getZ() + (double)var9 * 0.6D + 0.5D;

         for(int var16 = 0; var16 < 10; ++var16) {
            double var17 = var5.nextDouble() * 0.2D + 0.01D;
            double var19 = var10 + (double)var7 * 0.01D + (var5.nextDouble() - 0.5D) * (double)var9 * 0.5D;
            double var21 = var12 + (double)var8 * 0.01D + (var5.nextDouble() - 0.5D) * (double)var8 * 0.5D;
            double var23 = var14 + (double)var9 * 0.01D + (var5.nextDouble() - 0.5D) * (double)var7 * 0.5D;
            double var25 = (double)var7 * var17 + var5.nextGaussian() * 0.01D;
            double var27 = (double)var8 * var17 + var5.nextGaussian() * 0.01D;
            double var29 = (double)var9 * var17 + var5.nextGaussian() * 0.01D;
            this.addParticle(ParticleTypes.SMOKE, var19, var21, var23, var25, var27, var29);
         }

         return;
      case 2001:
         BlockState var6 = Block.stateById(var4);
         if(!var6.isAir()) {
            SoundType var7 = var6.getSoundType();
            this.level.playLocalSound(blockPos, var7.getBreakSound(), SoundSource.BLOCKS, (var7.getVolume() + 1.0F) / 2.0F, var7.getPitch() * 0.8F, false);
         }

         this.minecraft.particleEngine.destroy(blockPos, var6);
         break;
      case 2002:
      case 2007:
         double var6 = (double)blockPos.getX();
         double var8 = (double)blockPos.getY();
         double var10 = (double)blockPos.getZ();

         for(int var12 = 0; var12 < 8; ++var12) {
            this.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), var6, var8, var10, var5.nextGaussian() * 0.15D, var5.nextDouble() * 0.2D, var5.nextGaussian() * 0.15D);
         }

         float var12 = (float)(var4 >> 16 & 255) / 255.0F;
         float var13 = (float)(var4 >> 8 & 255) / 255.0F;
         float var14 = (float)(var4 >> 0 & 255) / 255.0F;
         ParticleOptions var15 = var2 == 2007?ParticleTypes.INSTANT_EFFECT:ParticleTypes.EFFECT;

         for(int var16 = 0; var16 < 100; ++var16) {
            double var17 = var5.nextDouble() * 4.0D;
            double var19 = var5.nextDouble() * 3.141592653589793D * 2.0D;
            double var21 = Math.cos(var19) * var17;
            double var23 = 0.01D + var5.nextDouble() * 0.5D;
            double var25 = Math.sin(var19) * var17;
            Particle var27 = this.addParticleInternal(var15, var15.getType().getOverrideLimiter(), var6 + var21 * 0.1D, var8 + 0.3D, var10 + var25 * 0.1D, var21, var23, var25);
            if(var27 != null) {
               float var28 = 0.75F + var5.nextFloat() * 0.25F;
               var27.setColor(var12 * var28, var13 * var28, var14 * var28);
               var27.setPower((float)var17);
            }
         }

         this.level.playLocalSound(blockPos, SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 2003:
         double var6 = (double)blockPos.getX() + 0.5D;
         double var8 = (double)blockPos.getY();
         double var10 = (double)blockPos.getZ() + 0.5D;

         for(int var12 = 0; var12 < 8; ++var12) {
            this.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), var6, var8, var10, var5.nextGaussian() * 0.15D, var5.nextDouble() * 0.2D, var5.nextGaussian() * 0.15D);
         }

         for(double var12 = 0.0D; var12 < 6.283185307179586D; var12 += 0.15707963267948966D) {
            this.addParticle(ParticleTypes.PORTAL, var6 + Math.cos(var12) * 5.0D, var8 - 0.4D, var10 + Math.sin(var12) * 5.0D, Math.cos(var12) * -5.0D, 0.0D, Math.sin(var12) * -5.0D);
            this.addParticle(ParticleTypes.PORTAL, var6 + Math.cos(var12) * 5.0D, var8 - 0.4D, var10 + Math.sin(var12) * 5.0D, Math.cos(var12) * -7.0D, 0.0D, Math.sin(var12) * -7.0D);
         }

         return;
      case 2004:
         for(int var7 = 0; var7 < 20; ++var7) {
            double var8 = (double)blockPos.getX() + 0.5D + ((double)this.level.random.nextFloat() - 0.5D) * 2.0D;
            double var10 = (double)blockPos.getY() + 0.5D + ((double)this.level.random.nextFloat() - 0.5D) * 2.0D;
            double var12 = (double)blockPos.getZ() + 0.5D + ((double)this.level.random.nextFloat() - 0.5D) * 2.0D;
            this.level.addParticle(ParticleTypes.SMOKE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
            this.level.addParticle(ParticleTypes.FLAME, var8, var10, var12, 0.0D, 0.0D, 0.0D);
         }

         return;
      case 2005:
         BoneMealItem.addGrowthParticles(this.level, blockPos, var4);
         break;
      case 2006:
         for(int var7 = 0; var7 < 200; ++var7) {
            float var8 = var5.nextFloat() * 4.0F;
            float var9 = var5.nextFloat() * 6.2831855F;
            double var10 = (double)(Mth.cos(var9) * var8);
            double var12 = 0.01D + var5.nextDouble() * 0.5D;
            double var14 = (double)(Mth.sin(var9) * var8);
            Particle var16 = this.addParticleInternal(ParticleTypes.DRAGON_BREATH, false, (double)blockPos.getX() + var10 * 0.1D, (double)blockPos.getY() + 0.3D, (double)blockPos.getZ() + var14 * 0.1D, var10, var12, var14);
            if(var16 != null) {
               var16.setPower(var8);
            }
         }

         this.level.playLocalSound(blockPos, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 2008:
         this.level.addParticle(ParticleTypes.EXPLOSION, (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
         break;
      case 3000:
         this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
         this.level.playLocalSound(blockPos, SoundEvents.END_GATEWAY_SPAWN, SoundSource.BLOCKS, 10.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
         break;
      case 3001:
         this.level.playLocalSound(blockPos, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 64.0F, 0.8F + this.level.random.nextFloat() * 0.3F, false);
      }

   }

   public void destroyBlockProgress(int var1, BlockPos blockPos, int var3) {
      if(var3 >= 0 && var3 < 10) {
         BlockDestructionProgress var4 = (BlockDestructionProgress)this.destroyingBlocks.get(Integer.valueOf(var1));
         if(var4 == null || var4.getPos().getX() != blockPos.getX() || var4.getPos().getY() != blockPos.getY() || var4.getPos().getZ() != blockPos.getZ()) {
            var4 = new BlockDestructionProgress(var1, blockPos);
            this.destroyingBlocks.put(Integer.valueOf(var1), var4);
         }

         var4.setProgress(var3);
         var4.updateTick(this.ticks);
      } else {
         this.destroyingBlocks.remove(Integer.valueOf(var1));
      }

   }

   public boolean hasRenderedAllChunks() {
      return this.chunksToCompile.isEmpty() && this.chunkRenderDispatcher.isQueueEmpty();
   }

   public void needsUpdate() {
      this.needsUpdate = true;
      this.generateClouds = true;
   }

   public void updateGlobalBlockEntities(Collection var1, Collection var2) {
      synchronized(this.globalBlockEntities) {
         this.globalBlockEntities.removeAll(var1);
         this.globalBlockEntities.addAll(var2);
      }
   }

   @ClientJarOnly
   class RenderChunkInfo {
      private final RenderChunk chunk;
      private final Direction sourceDirection;
      private byte directions;
      private final int step;

      private RenderChunkInfo(RenderChunk chunk, Direction sourceDirection, @Nullable int step) {
         this.chunk = chunk;
         this.sourceDirection = sourceDirection;
         this.step = step;
      }

      public void setDirections(byte var1, Direction direction) {
         this.directions = (byte)(this.directions | var1 | 1 << direction.ordinal());
      }

      public boolean hasDirection(Direction direction) {
         return (this.directions & 1 << direction.ordinal()) > 0;
      }
   }
}
