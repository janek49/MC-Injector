package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.client.renderer.entity.AreaEffectCloudRenderer;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.BatRenderer;
import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.CaveSpiderRenderer;
import net.minecraft.client.renderer.entity.ChestedHorseRenderer;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.CodRenderer;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.DefaultRenderer;
import net.minecraft.client.renderer.entity.DolphinRenderer;
import net.minecraft.client.renderer.entity.DragonFireballRenderer;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EndermiteRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EvokerFangsRenderer;
import net.minecraft.client.renderer.entity.EvokerRenderer;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.FireworkEntityRenderer;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.client.renderer.entity.GiantMobRenderer;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.client.renderer.entity.HuskRenderer;
import net.minecraft.client.renderer.entity.IllusionerRenderer;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LavaSlimeRenderer;
import net.minecraft.client.renderer.entity.LeashKnotRenderer;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.entity.LlamaRenderer;
import net.minecraft.client.renderer.entity.LlamaSpitRenderer;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.MushroomCowRenderer;
import net.minecraft.client.renderer.entity.OcelotRenderer;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.client.renderer.entity.PandaRenderer;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.client.renderer.entity.PigZombieRenderer;
import net.minecraft.client.renderer.entity.PillagerRenderer;
import net.minecraft.client.renderer.entity.PolarBearRenderer;
import net.minecraft.client.renderer.entity.PufferfishRenderer;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.client.renderer.entity.RavagerRenderer;
import net.minecraft.client.renderer.entity.SalmonRenderer;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.client.renderer.entity.ShulkerBulletRenderer;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.client.renderer.entity.SilverfishRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.SnowGolemRenderer;
import net.minecraft.client.renderer.entity.SpectralArrowRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.SquidRenderer;
import net.minecraft.client.renderer.entity.StrayRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.client.renderer.entity.TropicalFishRenderer;
import net.minecraft.client.renderer.entity.TurtleRenderer;
import net.minecraft.client.renderer.entity.UndeadHorseRenderer;
import net.minecraft.client.renderer.entity.VexRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.VindicatorRenderer;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.WitchRenderer;
import net.minecraft.client.renderer.entity.WitherBossRenderer;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.ZombieVillagerRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.PigZombie;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@ClientJarOnly
public class EntityRenderDispatcher {
   private final Map renderers = Maps.newHashMap();
   private final Map playerRenderers = Maps.newHashMap();
   private final PlayerRenderer defaultPlayerRenderer;
   private Font font;
   private double xOff;
   private double yOff;
   private double zOff;
   public final TextureManager textureManager;
   public Level level;
   public Camera camera;
   public Entity crosshairPickEntity;
   public float playerRotY;
   public float playerRotX;
   public Options options;
   private boolean solidRender;
   private boolean shouldRenderShadow = true;
   private boolean renderHitBoxes;

   private void register(Class class, EntityRenderer entityRenderer) {
      this.renderers.put(class, entityRenderer);
   }

   public EntityRenderDispatcher(TextureManager textureManager, ItemRenderer itemRenderer, ReloadableResourceManager reloadableResourceManager) {
      this.textureManager = textureManager;
      this.register(CaveSpider.class, new CaveSpiderRenderer(this));
      this.register(Spider.class, new SpiderRenderer(this));
      this.register(Pig.class, new PigRenderer(this));
      this.register(Sheep.class, new SheepRenderer(this));
      this.register(Cow.class, new CowRenderer(this));
      this.register(MushroomCow.class, new MushroomCowRenderer(this));
      this.register(Wolf.class, new WolfRenderer(this));
      this.register(Chicken.class, new ChickenRenderer(this));
      this.register(Ocelot.class, new OcelotRenderer(this));
      this.register(Rabbit.class, new RabbitRenderer(this));
      this.register(Parrot.class, new ParrotRenderer(this));
      this.register(Turtle.class, new TurtleRenderer(this));
      this.register(Silverfish.class, new SilverfishRenderer(this));
      this.register(Endermite.class, new EndermiteRenderer(this));
      this.register(Creeper.class, new CreeperRenderer(this));
      this.register(EnderMan.class, new EndermanRenderer(this));
      this.register(SnowGolem.class, new SnowGolemRenderer(this));
      this.register(Skeleton.class, new SkeletonRenderer(this));
      this.register(WitherSkeleton.class, new WitherSkeletonRenderer(this));
      this.register(Stray.class, new StrayRenderer(this));
      this.register(Witch.class, new WitchRenderer(this));
      this.register(Blaze.class, new BlazeRenderer(this));
      this.register(PigZombie.class, new PigZombieRenderer(this));
      this.register(Zombie.class, new ZombieRenderer(this));
      this.register(ZombieVillager.class, new ZombieVillagerRenderer(this, reloadableResourceManager));
      this.register(Husk.class, new HuskRenderer(this));
      this.register(Drowned.class, new DrownedRenderer(this));
      this.register(Slime.class, new SlimeRenderer(this));
      this.register(MagmaCube.class, new LavaSlimeRenderer(this));
      this.register(Giant.class, new GiantMobRenderer(this, 6.0F));
      this.register(Ghast.class, new GhastRenderer(this));
      this.register(Squid.class, new SquidRenderer(this));
      this.register(Villager.class, new VillagerRenderer(this, reloadableResourceManager));
      this.register(WanderingTrader.class, new WanderingTraderRenderer(this));
      this.register(IronGolem.class, new IronGolemRenderer(this));
      this.register(Bat.class, new BatRenderer(this));
      this.register(Guardian.class, new GuardianRenderer(this));
      this.register(ElderGuardian.class, new ElderGuardianRenderer(this));
      this.register(Shulker.class, new ShulkerRenderer(this));
      this.register(PolarBear.class, new PolarBearRenderer(this));
      this.register(Evoker.class, new EvokerRenderer(this));
      this.register(Vindicator.class, new VindicatorRenderer(this));
      this.register(Pillager.class, new PillagerRenderer(this));
      this.register(Ravager.class, new RavagerRenderer(this));
      this.register(Vex.class, new VexRenderer(this));
      this.register(Illusioner.class, new IllusionerRenderer(this));
      this.register(Phantom.class, new PhantomRenderer(this));
      this.register(Pufferfish.class, new PufferfishRenderer(this));
      this.register(Salmon.class, new SalmonRenderer(this));
      this.register(Cod.class, new CodRenderer(this));
      this.register(TropicalFish.class, new TropicalFishRenderer(this));
      this.register(Dolphin.class, new DolphinRenderer(this));
      this.register(Panda.class, new PandaRenderer(this));
      this.register(Cat.class, new CatRenderer(this));
      this.register(Fox.class, new FoxRenderer(this));
      this.register(EnderDragon.class, new EnderDragonRenderer(this));
      this.register(EndCrystal.class, new EndCrystalRenderer(this));
      this.register(WitherBoss.class, new WitherBossRenderer(this));
      this.register(Entity.class, new DefaultRenderer(this));
      this.register(Painting.class, new PaintingRenderer(this));
      this.register(ItemFrame.class, new ItemFrameRenderer(this, itemRenderer));
      this.register(LeashFenceKnotEntity.class, new LeashKnotRenderer(this));
      this.register(Arrow.class, new TippableArrowRenderer(this));
      this.register(SpectralArrow.class, new SpectralArrowRenderer(this));
      this.register(ThrownTrident.class, new ThrownTridentRenderer(this));
      this.register(Snowball.class, new ThrownItemRenderer(this, itemRenderer));
      this.register(ThrownEnderpearl.class, new ThrownItemRenderer(this, itemRenderer));
      this.register(EyeOfEnder.class, new ThrownItemRenderer(this, itemRenderer));
      this.register(ThrownEgg.class, new ThrownItemRenderer(this, itemRenderer));
      this.register(ThrownPotion.class, new ThrownItemRenderer(this, itemRenderer));
      this.register(ThrownExperienceBottle.class, new ThrownItemRenderer(this, itemRenderer));
      this.register(FireworkRocketEntity.class, new FireworkEntityRenderer(this, itemRenderer));
      this.register(LargeFireball.class, new ThrownItemRenderer(this, itemRenderer, 3.0F));
      this.register(SmallFireball.class, new ThrownItemRenderer(this, itemRenderer, 0.75F));
      this.register(DragonFireball.class, new DragonFireballRenderer(this));
      this.register(WitherSkull.class, new WitherSkullRenderer(this));
      this.register(ShulkerBullet.class, new ShulkerBulletRenderer(this));
      this.register(ItemEntity.class, new ItemEntityRenderer(this, itemRenderer));
      this.register(ExperienceOrb.class, new ExperienceOrbRenderer(this));
      this.register(PrimedTnt.class, new TntRenderer(this));
      this.register(FallingBlockEntity.class, new FallingBlockRenderer(this));
      this.register(ArmorStand.class, new ArmorStandRenderer(this));
      this.register(EvokerFangs.class, new EvokerFangsRenderer(this));
      this.register(MinecartTNT.class, new TntMinecartRenderer(this));
      this.register(MinecartSpawner.class, new MinecartRenderer(this));
      this.register(AbstractMinecart.class, new MinecartRenderer(this));
      this.register(Boat.class, new BoatRenderer(this));
      this.register(FishingHook.class, new FishingHookRenderer(this));
      this.register(AreaEffectCloud.class, new AreaEffectCloudRenderer(this));
      this.register(Horse.class, new HorseRenderer(this));
      this.register(SkeletonHorse.class, new UndeadHorseRenderer(this));
      this.register(ZombieHorse.class, new UndeadHorseRenderer(this));
      this.register(Mule.class, new ChestedHorseRenderer(this, 0.92F));
      this.register(Donkey.class, new ChestedHorseRenderer(this, 0.87F));
      this.register(Llama.class, new LlamaRenderer(this));
      this.register(TraderLlama.class, new LlamaRenderer(this));
      this.register(LlamaSpit.class, new LlamaSpitRenderer(this));
      this.register(LightningBolt.class, new LightningBoltRenderer(this));
      this.defaultPlayerRenderer = new PlayerRenderer(this);
      this.playerRenderers.put("default", this.defaultPlayerRenderer);
      this.playerRenderers.put("slim", new PlayerRenderer(this, true));
   }

   public void setPosition(double xOff, double yOff, double zOff) {
      this.xOff = xOff;
      this.yOff = yOff;
      this.zOff = zOff;
   }

   public EntityRenderer getRenderer(Class class) {
      EntityRenderer<? extends Entity> entityRenderer = (EntityRenderer)this.renderers.get(class);
      if(entityRenderer == null && class != Entity.class) {
         entityRenderer = this.getRenderer(class.getSuperclass());
         this.renderers.put(class, entityRenderer);
      }

      return entityRenderer;
   }

   @Nullable
   public EntityRenderer getRenderer(Entity entity) {
      if(entity instanceof AbstractClientPlayer) {
         String var2 = ((AbstractClientPlayer)entity).getModelName();
         PlayerRenderer var3 = (PlayerRenderer)this.playerRenderers.get(var2);
         return var3 != null?var3:this.defaultPlayerRenderer;
      } else {
         return this.getRenderer(entity.getClass());
      }
   }

   public void prepare(Level level, Font font, Camera camera, Entity crosshairPickEntity, Options options) {
      this.level = level;
      this.options = options;
      this.camera = camera;
      this.crosshairPickEntity = crosshairPickEntity;
      this.font = font;
      if(camera.getEntity() instanceof LivingEntity && ((LivingEntity)camera.getEntity()).isSleeping()) {
         Direction var6 = ((LivingEntity)camera.getEntity()).getBedOrientation();
         if(var6 != null) {
            this.playerRotY = var6.getOpposite().toYRot();
            this.playerRotX = 0.0F;
         }
      } else {
         this.playerRotY = camera.getYRot();
         this.playerRotX = camera.getXRot();
      }

   }

   public void setPlayerRotY(float playerRotY) {
      this.playerRotY = playerRotY;
   }

   public boolean shouldRenderShadow() {
      return this.shouldRenderShadow;
   }

   public void setRenderShadow(boolean renderShadow) {
      this.shouldRenderShadow = renderShadow;
   }

   public void setRenderHitBoxes(boolean renderHitBoxes) {
      this.renderHitBoxes = renderHitBoxes;
   }

   public boolean shouldRenderHitBoxes() {
      return this.renderHitBoxes;
   }

   public boolean hasSecondPass(Entity entity) {
      return this.getRenderer(entity).hasSecondPass();
   }

   public boolean shouldRender(Entity entity, Culler culler, double var3, double var5, double var7) {
      EntityRenderer<Entity> var9 = this.getRenderer(entity);
      return var9 != null && var9.shouldRender(entity, culler, var3, var5, var7);
   }

   public void render(Entity entity, float var2, boolean var3) {
      if(entity.tickCount == 0) {
         entity.xOld = entity.x;
         entity.yOld = entity.y;
         entity.zOld = entity.z;
      }

      double var4 = Mth.lerp((double)var2, entity.xOld, entity.x);
      double var6 = Mth.lerp((double)var2, entity.yOld, entity.y);
      double var8 = Mth.lerp((double)var2, entity.zOld, entity.z);
      float var10 = Mth.lerp(var2, entity.yRotO, entity.yRot);
      int var11 = entity.getLightColor();
      if(entity.isOnFire()) {
         var11 = 15728880;
      }

      int var12 = var11 % 65536;
      int var13 = var11 / 65536;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)var12, (float)var13);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.render(entity, var4 - this.xOff, var6 - this.yOff, var8 - this.zOff, var10, var2, var3);
   }

   public void render(Entity entity, double var2, double var4, double var6, float var8, float var9, boolean var10) {
      EntityRenderer<Entity> var11 = null;

      try {
         var11 = this.getRenderer(entity);
         if(var11 != null && this.textureManager != null) {
            try {
               var11.setSolidRender(this.solidRender);
               var11.render(entity, var2, var4, var6, var8, var9);
            } catch (Throwable var17) {
               throw new ReportedException(CrashReport.forThrowable(var17, "Rendering entity in world"));
            }

            try {
               if(!this.solidRender) {
                  var11.postRender(entity, var2, var4, var6, var8, var9);
               }
            } catch (Throwable var18) {
               throw new ReportedException(CrashReport.forThrowable(var18, "Post-rendering entity in world"));
            }

            if(this.renderHitBoxes && !entity.isInvisible() && !var10 && !Minecraft.getInstance().showOnlyReducedInfo()) {
               try {
                  this.renderHitbox(entity, var2, var4, var6, var8, var9);
               } catch (Throwable var16) {
                  throw new ReportedException(CrashReport.forThrowable(var16, "Rendering entity hitbox in world"));
               }
            }
         }

      } catch (Throwable var19) {
         CrashReport var13 = CrashReport.forThrowable(var19, "Rendering entity in world");
         CrashReportCategory var14 = var13.addCategory("Entity being rendered");
         entity.fillCrashReportCategory(var14);
         CrashReportCategory var15 = var13.addCategory("Renderer details");
         var15.setDetail("Assigned renderer", (Object)var11);
         var15.setDetail("Location", (Object)CrashReportCategory.formatLocation(var2, var4, var6));
         var15.setDetail("Rotation", (Object)Float.valueOf(var8));
         var15.setDetail("Delta", (Object)Float.valueOf(var9));
         throw new ReportedException(var13);
      }
   }

   public void renderSecondPass(Entity entity, float var2) {
      if(entity.tickCount == 0) {
         entity.xOld = entity.x;
         entity.yOld = entity.y;
         entity.zOld = entity.z;
      }

      double var3 = Mth.lerp((double)var2, entity.xOld, entity.x);
      double var5 = Mth.lerp((double)var2, entity.yOld, entity.y);
      double var7 = Mth.lerp((double)var2, entity.zOld, entity.z);
      float var9 = Mth.lerp(var2, entity.yRotO, entity.yRot);
      int var10 = entity.getLightColor();
      if(entity.isOnFire()) {
         var10 = 15728880;
      }

      int var11 = var10 % 65536;
      int var12 = var10 / 65536;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)var11, (float)var12);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      EntityRenderer<Entity> var13 = this.getRenderer(entity);
      if(var13 != null && this.textureManager != null) {
         var13.renderSecondPass(entity, var3 - this.xOff, var5 - this.yOff, var7 - this.zOff, var9, var2);
      }

   }

   private void renderHitbox(Entity entity, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.depthMask(false);
      GlStateManager.disableTexture();
      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      GlStateManager.disableBlend();
      float var10 = entity.getBbWidth() / 2.0F;
      AABB var11 = entity.getBoundingBox();
      LevelRenderer.renderLineBox(var11.minX - entity.x + var2, var11.minY - entity.y + var4, var11.minZ - entity.z + var6, var11.maxX - entity.x + var2, var11.maxY - entity.y + var4, var11.maxZ - entity.z + var6, 1.0F, 1.0F, 1.0F, 1.0F);
      if(entity instanceof EnderDragon) {
         for(EnderDragonPart var15 : ((EnderDragon)entity).getSubEntities()) {
            double var16 = (var15.x - var15.xo) * (double)var9;
            double var18 = (var15.y - var15.yo) * (double)var9;
            double var20 = (var15.z - var15.zo) * (double)var9;
            AABB var22 = var15.getBoundingBox();
            LevelRenderer.renderLineBox(var22.minX - this.xOff + var16, var22.minY - this.yOff + var18, var22.minZ - this.zOff + var20, var22.maxX - this.xOff + var16, var22.maxY - this.yOff + var18, var22.maxZ - this.zOff + var20, 0.25F, 1.0F, 0.0F, 1.0F);
         }
      }

      if(entity instanceof LivingEntity) {
         float var12 = 0.01F;
         LevelRenderer.renderLineBox(var2 - (double)var10, var4 + (double)entity.getEyeHeight() - 0.009999999776482582D, var6 - (double)var10, var2 + (double)var10, var4 + (double)entity.getEyeHeight() + 0.009999999776482582D, var6 + (double)var10, 1.0F, 0.0F, 0.0F, 1.0F);
      }

      Tesselator var12 = Tesselator.getInstance();
      BufferBuilder var13 = var12.getBuilder();
      Vec3 var14 = entity.getViewVector(var9);
      var13.begin(3, DefaultVertexFormat.POSITION_COLOR);
      var13.vertex(var2, var4 + (double)entity.getEyeHeight(), var6).color(0, 0, 255, 255).endVertex();
      var13.vertex(var2 + var14.x * 2.0D, var4 + (double)entity.getEyeHeight() + var14.y * 2.0D, var6 + var14.z * 2.0D).color(0, 0, 255, 255).endVertex();
      var12.end();
      GlStateManager.enableTexture();
      GlStateManager.enableLighting();
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
   }

   public void setLevel(@Nullable Level level) {
      this.level = level;
      if(level == null) {
         this.camera = null;
      }

   }

   public double distanceToSqr(double var1, double var3, double var5) {
      return this.camera.getPosition().distanceToSqr(var1, var3, var5);
   }

   public Font getFont() {
      return this.font;
   }

   public void setSolidRendering(boolean solidRendering) {
      this.solidRender = solidRendering;
   }
}
