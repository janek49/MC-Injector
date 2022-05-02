package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.base.Charsets;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.AttackSweepParticle;
import net.minecraft.client.particle.BarrierParticle;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.BubbleColumnUpParticle;
import net.minecraft.client.particle.BubbleParticle;
import net.minecraft.client.particle.BubblePopParticle;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.CritParticle;
import net.minecraft.client.particle.DragonBreathParticle;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.DustParticle;
import net.minecraft.client.particle.EnchantmentTableParticle;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.particle.ExplodeParticle;
import net.minecraft.client.particle.FallingDustParticle;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.particle.HugeExplosionParticle;
import net.minecraft.client.particle.HugeExplosionSeedParticle;
import net.minecraft.client.particle.LargeSmokeParticle;
import net.minecraft.client.particle.LavaParticle;
import net.minecraft.client.particle.MobAppearanceParticle;
import net.minecraft.client.particle.NoteParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDescription;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.PlayerCloudParticle;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.particle.SpitParticle;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SquidInkParticle;
import net.minecraft.client.particle.SuspendedParticle;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.client.particle.TrackingEmitter;
import net.minecraft.client.particle.WakeParticle;
import net.minecraft.client.particle.WaterCurrentDownParticle;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TickableTextureObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@ClientJarOnly
public class ParticleEngine implements PreparableReloadListener {
   private static final List RENDER_ORDER = ImmutableList.of(ParticleRenderType.TERRAIN_SHEET, ParticleRenderType.PARTICLE_SHEET_OPAQUE, ParticleRenderType.PARTICLE_SHEET_LIT, ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT, ParticleRenderType.CUSTOM);
   protected Level level;
   private final Map particles = Maps.newIdentityHashMap();
   private final Queue trackingEmitters = Queues.newArrayDeque();
   private final TextureManager textureManager;
   private final Random random = new Random();
   private final Int2ObjectMap providers = new Int2ObjectOpenHashMap();
   private final Queue particlesToAdd = Queues.newArrayDeque();
   private final Map spriteSets = Maps.newHashMap();
   private final TextureAtlas textureAtlas = new TextureAtlas("textures/particle");

   public ParticleEngine(Level level, TextureManager textureManager) {
      textureManager.register((ResourceLocation)TextureAtlas.LOCATION_PARTICLES, (TickableTextureObject)this.textureAtlas);
      this.level = level;
      this.textureManager = textureManager;
      this.registerProviders();
   }

   private void registerProviders() {
      this.register(ParticleTypes.AMBIENT_ENTITY_EFFECT, (ParticleEngine.SpriteParticleRegistration)(SpellParticle.AmbientMobProvider::<init>));
      this.register(ParticleTypes.ANGRY_VILLAGER, (ParticleEngine.SpriteParticleRegistration)(HeartParticle.AngryVillagerProvider::<init>));
      this.register(ParticleTypes.BARRIER, (ParticleProvider)(new BarrierParticle.Provider()));
      this.register(ParticleTypes.BLOCK, (ParticleProvider)(new TerrainParticle.Provider()));
      this.register(ParticleTypes.BUBBLE, (ParticleEngine.SpriteParticleRegistration)(BubbleParticle.Provider::<init>));
      this.register(ParticleTypes.BUBBLE_COLUMN_UP, (ParticleEngine.SpriteParticleRegistration)(BubbleColumnUpParticle.Provider::<init>));
      this.register(ParticleTypes.BUBBLE_POP, (ParticleEngine.SpriteParticleRegistration)(BubblePopParticle.Provider::<init>));
      this.register(ParticleTypes.CAMPFIRE_COSY_SMOKE, (ParticleEngine.SpriteParticleRegistration)(CampfireSmokeParticle.CosyProvider::<init>));
      this.register(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, (ParticleEngine.SpriteParticleRegistration)(CampfireSmokeParticle.SignalProvider::<init>));
      this.register(ParticleTypes.CLOUD, (ParticleEngine.SpriteParticleRegistration)(PlayerCloudParticle.Provider::<init>));
      this.register(ParticleTypes.COMPOSTER, (ParticleEngine.SpriteParticleRegistration)(SuspendedTownParticle.ComposterFillProvider::<init>));
      this.register(ParticleTypes.CRIT, (ParticleEngine.SpriteParticleRegistration)(CritParticle.Provider::<init>));
      this.register(ParticleTypes.CURRENT_DOWN, (ParticleEngine.SpriteParticleRegistration)(WaterCurrentDownParticle.Provider::<init>));
      this.register(ParticleTypes.DAMAGE_INDICATOR, (ParticleEngine.SpriteParticleRegistration)(CritParticle.DamageIndicatorProvider::<init>));
      this.register(ParticleTypes.DRAGON_BREATH, (ParticleEngine.SpriteParticleRegistration)(DragonBreathParticle.Provider::<init>));
      this.register(ParticleTypes.DOLPHIN, (ParticleEngine.SpriteParticleRegistration)(SuspendedTownParticle.DolphinSpeedProvider::<init>));
      this.register(ParticleTypes.DRIPPING_LAVA, (ParticleEngine.SpriteParticleRegistration)(DripParticle.LavaHangProvider::<init>));
      this.register(ParticleTypes.FALLING_LAVA, (ParticleEngine.SpriteParticleRegistration)(DripParticle.LavaFallProvider::<init>));
      this.register(ParticleTypes.LANDING_LAVA, (ParticleEngine.SpriteParticleRegistration)(DripParticle.LavaLandProvider::<init>));
      this.register(ParticleTypes.DRIPPING_WATER, (ParticleEngine.SpriteParticleRegistration)(DripParticle.WaterHangProvider::<init>));
      this.register(ParticleTypes.FALLING_WATER, (ParticleEngine.SpriteParticleRegistration)(DripParticle.WaterFallProvider::<init>));
      this.register(ParticleTypes.DUST, DustParticle.Provider::<init>);
      this.register(ParticleTypes.EFFECT, (ParticleEngine.SpriteParticleRegistration)(SpellParticle.Provider::<init>));
      this.register(ParticleTypes.ELDER_GUARDIAN, (ParticleProvider)(new MobAppearanceParticle.Provider()));
      this.register(ParticleTypes.ENCHANTED_HIT, (ParticleEngine.SpriteParticleRegistration)(CritParticle.MagicProvider::<init>));
      this.register(ParticleTypes.ENCHANT, (ParticleEngine.SpriteParticleRegistration)(EnchantmentTableParticle.Provider::<init>));
      this.register(ParticleTypes.END_ROD, (ParticleEngine.SpriteParticleRegistration)(EndRodParticle.Provider::<init>));
      this.register(ParticleTypes.ENTITY_EFFECT, (ParticleEngine.SpriteParticleRegistration)(SpellParticle.MobProvider::<init>));
      this.register(ParticleTypes.EXPLOSION_EMITTER, (ParticleProvider)(new HugeExplosionSeedParticle.Provider()));
      this.register(ParticleTypes.EXPLOSION, (ParticleEngine.SpriteParticleRegistration)(HugeExplosionParticle.Provider::<init>));
      this.register(ParticleTypes.FALLING_DUST, FallingDustParticle.Provider::<init>);
      this.register(ParticleTypes.FIREWORK, (ParticleEngine.SpriteParticleRegistration)(FireworkParticles.SparkProvider::<init>));
      this.register(ParticleTypes.FISHING, (ParticleEngine.SpriteParticleRegistration)(WakeParticle.Provider::<init>));
      this.register(ParticleTypes.FLAME, (ParticleEngine.SpriteParticleRegistration)(FlameParticle.Provider::<init>));
      this.register(ParticleTypes.FLASH, (ParticleEngine.SpriteParticleRegistration)(FireworkParticles.FlashProvider::<init>));
      this.register(ParticleTypes.HAPPY_VILLAGER, (ParticleEngine.SpriteParticleRegistration)(SuspendedTownParticle.HappyVillagerProvider::<init>));
      this.register(ParticleTypes.HEART, (ParticleEngine.SpriteParticleRegistration)(HeartParticle.Provider::<init>));
      this.register(ParticleTypes.INSTANT_EFFECT, (ParticleEngine.SpriteParticleRegistration)(SpellParticle.InstantProvider::<init>));
      this.register(ParticleTypes.ITEM, (ParticleProvider)(new BreakingItemParticle.Provider()));
      this.register(ParticleTypes.ITEM_SLIME, (ParticleProvider)(new BreakingItemParticle.SlimeProvider()));
      this.register(ParticleTypes.ITEM_SNOWBALL, (ParticleProvider)(new BreakingItemParticle.SnowballProvider()));
      this.register(ParticleTypes.LARGE_SMOKE, (ParticleEngine.SpriteParticleRegistration)(LargeSmokeParticle.Provider::<init>));
      this.register(ParticleTypes.LAVA, (ParticleEngine.SpriteParticleRegistration)(LavaParticle.Provider::<init>));
      this.register(ParticleTypes.MYCELIUM, (ParticleEngine.SpriteParticleRegistration)(SuspendedTownParticle.Provider::<init>));
      this.register(ParticleTypes.NAUTILUS, (ParticleEngine.SpriteParticleRegistration)(EnchantmentTableParticle.NautilusProvider::<init>));
      this.register(ParticleTypes.NOTE, (ParticleEngine.SpriteParticleRegistration)(NoteParticle.Provider::<init>));
      this.register(ParticleTypes.POOF, (ParticleEngine.SpriteParticleRegistration)(ExplodeParticle.Provider::<init>));
      this.register(ParticleTypes.PORTAL, (ParticleEngine.SpriteParticleRegistration)(PortalParticle.Provider::<init>));
      this.register(ParticleTypes.RAIN, (ParticleEngine.SpriteParticleRegistration)(WaterDropParticle.Provider::<init>));
      this.register(ParticleTypes.SMOKE, (ParticleEngine.SpriteParticleRegistration)(SmokeParticle.Provider::<init>));
      this.register(ParticleTypes.SNEEZE, (ParticleEngine.SpriteParticleRegistration)(PlayerCloudParticle.SneezeProvider::<init>));
      this.register(ParticleTypes.SPIT, (ParticleEngine.SpriteParticleRegistration)(SpitParticle.Provider::<init>));
      this.register(ParticleTypes.SWEEP_ATTACK, (ParticleEngine.SpriteParticleRegistration)(AttackSweepParticle.Provider::<init>));
      this.register(ParticleTypes.TOTEM_OF_UNDYING, (ParticleEngine.SpriteParticleRegistration)(TotemParticle.Provider::<init>));
      this.register(ParticleTypes.SQUID_INK, (ParticleEngine.SpriteParticleRegistration)(SquidInkParticle.Provider::<init>));
      this.register(ParticleTypes.UNDERWATER, (ParticleEngine.SpriteParticleRegistration)(SuspendedParticle.Provider::<init>));
      this.register(ParticleTypes.SPLASH, (ParticleEngine.SpriteParticleRegistration)(SplashParticle.Provider::<init>));
      this.register(ParticleTypes.WITCH, (ParticleEngine.SpriteParticleRegistration)(SpellParticle.WitchProvider::<init>));
   }

   private void register(ParticleType particleType, ParticleProvider particleProvider) {
      this.providers.put(Registry.PARTICLE_TYPE.getId(particleType), particleProvider);
   }

   private void register(ParticleType particleType, ParticleEngine.SpriteParticleRegistration particleEngine$SpriteParticleRegistration) {
      ParticleEngine.MutableSpriteSet var3 = new ParticleEngine.MutableSpriteSet();
      this.spriteSets.put(Registry.PARTICLE_TYPE.getKey(particleType), var3);
      this.providers.put(Registry.PARTICLE_TYPE.getId(particleType), particleEngine$SpriteParticleRegistration.create(var3));
   }

   public CompletableFuture reload(PreparableReloadListener.PreparationBarrier preparableReloadListener$PreparationBarrier, ResourceManager resourceManager, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      Map<ResourceLocation, List<ResourceLocation>> var7 = Maps.newConcurrentMap();
      CompletableFuture<?>[] vars8 = (CompletableFuture[])Registry.PARTICLE_TYPE.keySet().stream().map((resourceLocation) -> {
         return CompletableFuture.runAsync(() -> {
            this.loadParticleDescription(resourceManager, resourceLocation, var7);
         }, var5);
      }).toArray((i) -> {
         return new CompletableFuture[i];
      });
      CompletableFuture var10000 = CompletableFuture.allOf(vars8).thenApplyAsync((void) -> {
         var3.startTick();
         var3.push("stitching");
         Set<ResourceLocation> var5 = (Set)var7.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
         TextureAtlas.Preparations var6 = this.textureAtlas.prepareToStitch(resourceManager, var5, var3);
         var3.pop();
         var3.endTick();
         return var6;
      }, var5);
      preparableReloadListener$PreparationBarrier.getClass();
      return var10000.thenCompose(preparableReloadListener$PreparationBarrier::wait).thenAcceptAsync((textureAtlas$Preparations) -> {
         var4.startTick();
         var4.push("upload");
         this.textureAtlas.reload(textureAtlas$Preparations);
         var4.popPush("bindSpriteSets");
         TextureAtlasSprite var4 = this.textureAtlas.getSprite(MissingTextureAtlasSprite.getLocation());
         var7.forEach((resourceLocation, list) -> {
            ImmutableList var10000;
            if(list.isEmpty()) {
               var10000 = ImmutableList.of(var4x);
            } else {
               Stream var5 = list.stream();
               TextureAtlas var10001 = this.textureAtlas;
               this.textureAtlas.getClass();
               var10000 = (ImmutableList)var5.map(var10001::getSprite).collect(ImmutableList.toImmutableList());
            }

            ImmutableList<TextureAtlasSprite> var4 = var10000;
            ((ParticleEngine.MutableSpriteSet)this.spriteSets.get(resourceLocation)).rebind(var4);
         });
         var4.pop();
         var4.endTick();
      }, var6);
   }

   public void close() {
      this.textureAtlas.clearTextureData();
   }

   private void loadParticleDescription(ResourceManager resourceManager, ResourceLocation resourceLocation, Map map) {
      ResourceLocation resourceLocation = new ResourceLocation(resourceLocation.getNamespace(), "particles/" + resourceLocation.getPath() + ".json");

      try {
         Resource var5 = resourceManager.getResource(resourceLocation);
         Throwable var6 = null;

         try {
            Reader var7 = new InputStreamReader(var5.getInputStream(), Charsets.UTF_8);
            Throwable var8 = null;

            try {
               ParticleDescription var9 = ParticleDescription.fromJson(GsonHelper.parse(var7));
               List<ResourceLocation> var10 = var9.getTextures();
               boolean var11 = this.spriteSets.containsKey(resourceLocation);
               if(var10 == null) {
                  if(var11) {
                     throw new IllegalStateException("Missing texture list for particle " + resourceLocation);
                  }
               } else {
                  if(!var11) {
                     throw new IllegalStateException("Redundant texture list for particle " + resourceLocation);
                  }

                  map.put(resourceLocation, var10);
               }
            } catch (Throwable var35) {
               var8 = var35;
               throw var35;
            } finally {
               if(var7 != null) {
                  if(var8 != null) {
                     try {
                        var7.close();
                     } catch (Throwable var34) {
                        var8.addSuppressed(var34);
                     }
                  } else {
                     var7.close();
                  }
               }

            }
         } catch (Throwable var37) {
            var6 = var37;
            throw var37;
         } finally {
            if(var5 != null) {
               if(var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var33) {
                     var6.addSuppressed(var33);
                  }
               } else {
                  var5.close();
               }
            }

         }

      } catch (IOException var39) {
         throw new IllegalStateException("Failed to load description for particle " + resourceLocation, var39);
      }
   }

   public void createTrackingEmitter(Entity entity, ParticleOptions particleOptions) {
      this.trackingEmitters.add(new TrackingEmitter(this.level, entity, particleOptions));
   }

   public void createTrackingEmitter(Entity entity, ParticleOptions particleOptions, int var3) {
      this.trackingEmitters.add(new TrackingEmitter(this.level, entity, particleOptions, var3));
   }

   @Nullable
   public Particle createParticle(ParticleOptions particleOptions, double var2, double var4, double var6, double var8, double var10, double var12) {
      Particle particle = this.makeParticle(particleOptions, var2, var4, var6, var8, var10, var12);
      if(particle != null) {
         this.add(particle);
         return particle;
      } else {
         return null;
      }
   }

   @Nullable
   private Particle makeParticle(ParticleOptions particleOptions, double var2, double var4, double var6, double var8, double var10, double var12) {
      ParticleProvider<T> var14 = (ParticleProvider)this.providers.get(Registry.PARTICLE_TYPE.getId(particleOptions.getType()));
      return var14 == null?null:var14.createParticle(particleOptions, this.level, var2, var4, var6, var8, var10, var12);
   }

   public void add(Particle particle) {
      this.particlesToAdd.add(particle);
   }

   public void tick() {
      this.particles.forEach((particleRenderType, queue) -> {
         this.level.getProfiler().push(particleRenderType.toString());
         this.tickParticleList(queue);
         this.level.getProfiler().pop();
      });
      if(!this.trackingEmitters.isEmpty()) {
         List<TrackingEmitter> var1 = Lists.newArrayList();

         for(TrackingEmitter var3 : this.trackingEmitters) {
            var3.tick();
            if(!var3.isAlive()) {
               var1.add(var3);
            }
         }

         this.trackingEmitters.removeAll(var1);
      }

      Particle var1;
      if(!this.particlesToAdd.isEmpty()) {
         while((var1 = (Particle)this.particlesToAdd.poll()) != null) {
            ((Queue)this.particles.computeIfAbsent(var1.getRenderType(), (particleRenderType) -> {
               return EvictingQueue.create(16384);
            })).add(var1);
         }
      }

   }

   private void tickParticleList(Collection collection) {
      if(!collection.isEmpty()) {
         Iterator<Particle> var2 = collection.iterator();

         while(var2.hasNext()) {
            Particle var3 = (Particle)var2.next();
            this.tickParticle(var3);
            if(!var3.isAlive()) {
               var2.remove();
            }
         }
      }

   }

   private void tickParticle(Particle particle) {
      try {
         particle.tick();
      } catch (Throwable var5) {
         CrashReport var3 = CrashReport.forThrowable(var5, "Ticking Particle");
         CrashReportCategory var4 = var3.addCategory("Particle being ticked");
         var4.setDetail("Particle", particle::toString);
         ParticleRenderType var10002 = particle.getRenderType();
         var4.setDetail("Particle Type", var10002::toString);
         throw new ReportedException(var3);
      }
   }

   public void render(Camera camera, float var2) {
      float var3 = Mth.cos(camera.getYRot() * 0.017453292F);
      float var4 = Mth.sin(camera.getYRot() * 0.017453292F);
      float var5 = -var4 * Mth.sin(camera.getXRot() * 0.017453292F);
      float var6 = var3 * Mth.sin(camera.getXRot() * 0.017453292F);
      float var7 = Mth.cos(camera.getXRot() * 0.017453292F);
      Particle.xOff = camera.getPosition().x;
      Particle.yOff = camera.getPosition().y;
      Particle.zOff = camera.getPosition().z;

      for(ParticleRenderType var9 : RENDER_ORDER) {
         Iterable<Particle> var10 = (Iterable)this.particles.get(var9);
         if(var10 != null) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Tesselator var11 = Tesselator.getInstance();
            BufferBuilder var12 = var11.getBuilder();
            var9.begin(var12, this.textureManager);

            for(Particle var14 : var10) {
               try {
                  var14.render(var12, camera, var2, var3, var7, var4, var5, var6);
               } catch (Throwable var18) {
                  CrashReport var16 = CrashReport.forThrowable(var18, "Rendering Particle");
                  CrashReportCategory var17 = var16.addCategory("Particle being rendered");
                  var17.setDetail("Particle", var14::toString);
                  var17.setDetail("Particle Type", var9::toString);
                  throw new ReportedException(var16);
               }
            }

            var9.end(var11);
         }
      }

      GlStateManager.depthMask(true);
      GlStateManager.disableBlend();
      GlStateManager.alphaFunc(516, 0.1F);
   }

   public void setLevel(@Nullable Level level) {
      this.level = level;
      this.particles.clear();
      this.trackingEmitters.clear();
   }

   public void destroy(BlockPos blockPos, BlockState blockState) {
      if(!blockState.isAir()) {
         VoxelShape var3 = blockState.getShape(this.level, blockPos);
         double var4 = 0.25D;
         var3.forAllBoxes((var3, var5, var7, var9, var11, var13) -> {
            double var15 = Math.min(1.0D, var9 - var3);
            double var17 = Math.min(1.0D, var11 - var5);
            double var19 = Math.min(1.0D, var13 - var7);
            int var21 = Math.max(2, Mth.ceil(var15 / 0.25D));
            int var22 = Math.max(2, Mth.ceil(var17 / 0.25D));
            int var23 = Math.max(2, Mth.ceil(var19 / 0.25D));

            for(int var24 = 0; var24 < var21; ++var24) {
               for(int var25 = 0; var25 < var22; ++var25) {
                  for(int var26 = 0; var26 < var23; ++var26) {
                     double var27 = ((double)var24 + 0.5D) / (double)var21;
                     double var29 = ((double)var25 + 0.5D) / (double)var22;
                     double var31 = ((double)var26 + 0.5D) / (double)var23;
                     double var33 = var27 * var15 + var3;
                     double var35 = var29 * var17 + var5;
                     double var37 = var31 * var19 + var7;
                     this.add((new TerrainParticle(this.level, (double)blockPos.getX() + var33, (double)blockPos.getY() + var35, (double)blockPos.getZ() + var37, var27 - 0.5D, var29 - 0.5D, var31 - 0.5D, blockState)).init(blockPos));
                  }
               }
            }

         });
      }
   }

   public void crack(BlockPos blockPos, Direction direction) {
      BlockState var3 = this.level.getBlockState(blockPos);
      if(var3.getRenderShape() != RenderShape.INVISIBLE) {
         int var4 = blockPos.getX();
         int var5 = blockPos.getY();
         int var6 = blockPos.getZ();
         float var7 = 0.1F;
         AABB var8 = var3.getShape(this.level, blockPos).bounds();
         double var9 = (double)var4 + this.random.nextDouble() * (var8.maxX - var8.minX - 0.20000000298023224D) + 0.10000000149011612D + var8.minX;
         double var11 = (double)var5 + this.random.nextDouble() * (var8.maxY - var8.minY - 0.20000000298023224D) + 0.10000000149011612D + var8.minY;
         double var13 = (double)var6 + this.random.nextDouble() * (var8.maxZ - var8.minZ - 0.20000000298023224D) + 0.10000000149011612D + var8.minZ;
         if(direction == Direction.DOWN) {
            var11 = (double)var5 + var8.minY - 0.10000000149011612D;
         }

         if(direction == Direction.UP) {
            var11 = (double)var5 + var8.maxY + 0.10000000149011612D;
         }

         if(direction == Direction.NORTH) {
            var13 = (double)var6 + var8.minZ - 0.10000000149011612D;
         }

         if(direction == Direction.SOUTH) {
            var13 = (double)var6 + var8.maxZ + 0.10000000149011612D;
         }

         if(direction == Direction.WEST) {
            var9 = (double)var4 + var8.minX - 0.10000000149011612D;
         }

         if(direction == Direction.EAST) {
            var9 = (double)var4 + var8.maxX + 0.10000000149011612D;
         }

         this.add((new TerrainParticle(this.level, var9, var11, var13, 0.0D, 0.0D, 0.0D, var3)).init(blockPos).setPower(0.2F).scale(0.6F));
      }
   }

   public String countParticles() {
      return String.valueOf(this.particles.values().stream().mapToInt(Collection::size).sum());
   }

   @ClientJarOnly
   class MutableSpriteSet implements SpriteSet {
      private List sprites;

      private MutableSpriteSet() {
      }

      public TextureAtlasSprite get(int var1, int var2) {
         return (TextureAtlasSprite)this.sprites.get(var1 * (this.sprites.size() - 1) / var2);
      }

      public TextureAtlasSprite get(Random random) {
         return (TextureAtlasSprite)this.sprites.get(random.nextInt(this.sprites.size()));
      }

      public void rebind(List list) {
         this.sprites = ImmutableList.copyOf(list);
      }
   }

   @FunctionalInterface
   @ClientJarOnly
   interface SpriteParticleRegistration {
      ParticleProvider create(SpriteSet var1);
   }
}
