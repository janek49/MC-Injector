package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

public class BeaconBlockEntity extends BlockEntity implements MenuProvider, TickableBlockEntity {
   public static final MobEffect[][] BEACON_EFFECTS = new MobEffect[][]{{MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED}, {MobEffects.DAMAGE_RESISTANCE, MobEffects.JUMP}, {MobEffects.DAMAGE_BOOST}, {MobEffects.REGENERATION}};
   private static final Set VALID_EFFECTS = (Set)Arrays.stream(BEACON_EFFECTS).flatMap(Arrays::stream).collect(Collectors.toSet());
   private List beamSections = Lists.newArrayList();
   private List checkingBeamSections = Lists.newArrayList();
   private int levels = 0;
   private int lastCheckY = -1;
   @Nullable
   private MobEffect primaryPower;
   @Nullable
   private MobEffect secondaryPower;
   @Nullable
   private Component name;
   private LockCode lockKey = LockCode.NO_LOCK;
   private final ContainerData dataAccess = new ContainerData() {
      public int get(int i) {
         switch(i) {
         case 0:
            return BeaconBlockEntity.this.levels;
         case 1:
            return MobEffect.getId(BeaconBlockEntity.this.primaryPower);
         case 2:
            return MobEffect.getId(BeaconBlockEntity.this.secondaryPower);
         default:
            return 0;
         }
      }

      public void set(int var1, int var2) {
         switch(var1) {
         case 0:
            BeaconBlockEntity.this.levels = var2;
            break;
         case 1:
            if(!BeaconBlockEntity.this.level.isClientSide && !BeaconBlockEntity.this.beamSections.isEmpty()) {
               BeaconBlockEntity.this.playSound(SoundEvents.BEACON_POWER_SELECT);
            }

            BeaconBlockEntity.this.primaryPower = BeaconBlockEntity.getValidEffectById(var2);
            break;
         case 2:
            BeaconBlockEntity.this.secondaryPower = BeaconBlockEntity.getValidEffectById(var2);
         }

      }

      public int getCount() {
         return 3;
      }
   };

   public BeaconBlockEntity() {
      super(BlockEntityType.BEACON);
   }

   public void tick() {
      int var1 = this.worldPosition.getX();
      int var2 = this.worldPosition.getY();
      int var3 = this.worldPosition.getZ();
      BlockPos var4;
      if(this.lastCheckY < var2) {
         var4 = this.worldPosition;
         this.checkingBeamSections = Lists.newArrayList();
         this.lastCheckY = var4.getY() - 1;
      } else {
         var4 = new BlockPos(var1, this.lastCheckY + 1, var3);
      }

      BeaconBlockEntity.BeaconBeamSection var5 = this.checkingBeamSections.isEmpty()?null:(BeaconBlockEntity.BeaconBeamSection)this.checkingBeamSections.get(this.checkingBeamSections.size() - 1);
      int var6 = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, var1, var3);

      for(int var7 = 0; var7 < 10 && var4.getY() <= var6; ++var7) {
         BlockState var8 = this.level.getBlockState(var4);
         Block var9 = var8.getBlock();
         if(var9 instanceof BeaconBeamBlock) {
            float[] vars10 = ((BeaconBeamBlock)var9).getColor().getTextureDiffuseColors();
            if(this.checkingBeamSections.size() <= 1) {
               var5 = new BeaconBlockEntity.BeaconBeamSection(vars10);
               this.checkingBeamSections.add(var5);
            } else if(var5 != null) {
               if(Arrays.equals(vars10, var5.color)) {
                  var5.increaseHeight();
               } else {
                  var5 = new BeaconBlockEntity.BeaconBeamSection(new float[]{(var5.color[0] + vars10[0]) / 2.0F, (var5.color[1] + vars10[1]) / 2.0F, (var5.color[2] + vars10[2]) / 2.0F});
                  this.checkingBeamSections.add(var5);
               }
            }
         } else {
            if(var5 == null || var8.getLightBlock(this.level, var4) >= 15 && var9 != Blocks.BEDROCK) {
               this.checkingBeamSections.clear();
               this.lastCheckY = var6;
               break;
            }

            var5.increaseHeight();
         }

         var4 = var4.above();
         ++this.lastCheckY;
      }

      int var7 = this.levels;
      if(this.level.getGameTime() % 80L == 0L) {
         if(!this.beamSections.isEmpty()) {
            this.updateBase(var1, var2, var3);
         }

         if(this.levels > 0 && !this.beamSections.isEmpty()) {
            this.applyEffects();
            this.playSound(SoundEvents.BEACON_AMBIENT);
         }
      }

      if(this.lastCheckY >= var6) {
         this.lastCheckY = -1;
         boolean var8 = var7 > 0;
         this.beamSections = this.checkingBeamSections;
         if(!this.level.isClientSide) {
            boolean var9 = this.levels > 0;
            if(!var8 && var9) {
               this.playSound(SoundEvents.BEACON_ACTIVATE);

               for(ServerPlayer var11 : this.level.getEntitiesOfClass(ServerPlayer.class, (new AABB((double)var1, (double)var2, (double)var3, (double)var1, (double)(var2 - 4), (double)var3)).inflate(10.0D, 5.0D, 10.0D))) {
                  CriteriaTriggers.CONSTRUCT_BEACON.trigger(var11, this);
               }
            } else if(var8 && !var9) {
               this.playSound(SoundEvents.BEACON_DEACTIVATE);
            }
         }
      }

   }

   private void updateBase(int var1, int var2, int var3) {
      this.levels = 0;

      for(int var4 = 1; var4 <= 4; this.levels = var4++) {
         int var5 = var2 - var4;
         if(var5 < 0) {
            break;
         }

         boolean var6 = true;

         for(int var7 = var1 - var4; var7 <= var1 + var4 && var6; ++var7) {
            for(int var8 = var3 - var4; var8 <= var3 + var4; ++var8) {
               Block var9 = this.level.getBlockState(new BlockPos(var7, var5, var8)).getBlock();
               if(var9 != Blocks.EMERALD_BLOCK && var9 != Blocks.GOLD_BLOCK && var9 != Blocks.DIAMOND_BLOCK && var9 != Blocks.IRON_BLOCK) {
                  var6 = false;
                  break;
               }
            }
         }

         if(!var6) {
            break;
         }
      }

   }

   public void setRemoved() {
      this.playSound(SoundEvents.BEACON_DEACTIVATE);
      super.setRemoved();
   }

   private void applyEffects() {
      if(!this.level.isClientSide && this.primaryPower != null) {
         double var1 = (double)(this.levels * 10 + 10);
         int var3 = 0;
         if(this.levels >= 4 && this.primaryPower == this.secondaryPower) {
            var3 = 1;
         }

         int var4 = (9 + this.levels * 2) * 20;
         AABB var5 = (new AABB(this.worldPosition)).inflate(var1).expandTowards(0.0D, (double)this.level.getMaxBuildHeight(), 0.0D);
         List<Player> var6 = this.level.getEntitiesOfClass(Player.class, var5);

         for(Player var8 : var6) {
            var8.addEffect(new MobEffectInstance(this.primaryPower, var4, var3, true, true));
         }

         if(this.levels >= 4 && this.primaryPower != this.secondaryPower && this.secondaryPower != null) {
            for(Player var8 : var6) {
               var8.addEffect(new MobEffectInstance(this.secondaryPower, var4, 0, true, true));
            }
         }

      }
   }

   public void playSound(SoundEvent soundEvent) {
      this.level.playSound((Player)null, (BlockPos)this.worldPosition, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   public List getBeamSections() {
      return (List)(this.levels == 0?ImmutableList.of():this.beamSections);
   }

   public int getLevels() {
      return this.levels;
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 3, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }

   public double getViewDistance() {
      return 65536.0D;
   }

   @Nullable
   private static MobEffect getValidEffectById(int i) {
      MobEffect mobEffect = MobEffect.byId(i);
      return VALID_EFFECTS.contains(mobEffect)?mobEffect:null;
   }

   public void load(CompoundTag compoundTag) {
      super.load(compoundTag);
      this.primaryPower = getValidEffectById(compoundTag.getInt("Primary"));
      this.secondaryPower = getValidEffectById(compoundTag.getInt("Secondary"));
      if(compoundTag.contains("CustomName", 8)) {
         this.name = Component.Serializer.fromJson(compoundTag.getString("CustomName"));
      }

      this.lockKey = LockCode.fromTag(compoundTag);
   }

   public CompoundTag save(CompoundTag compoundTag) {
      super.save(compoundTag);
      compoundTag.putInt("Primary", MobEffect.getId(this.primaryPower));
      compoundTag.putInt("Secondary", MobEffect.getId(this.secondaryPower));
      compoundTag.putInt("Levels", this.levels);
      if(this.name != null) {
         compoundTag.putString("CustomName", Component.Serializer.toJson(this.name));
      }

      this.lockKey.addToTag(compoundTag);
      return compoundTag;
   }

   public void setCustomName(@Nullable Component customName) {
      this.name = customName;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int var1, Inventory inventory, Player player) {
      return BaseContainerBlockEntity.canUnlock(player, this.lockKey, this.getDisplayName())?new BeaconMenu(var1, inventory, this.dataAccess, ContainerLevelAccess.create(this.level, this.getBlockPos())):null;
   }

   public Component getDisplayName() {
      return (Component)(this.name != null?this.name:new TranslatableComponent("container.beacon", new Object[0]));
   }

   public static class BeaconBeamSection {
      private final float[] color;
      private int height;

      public BeaconBeamSection(float[] color) {
         this.color = color;
         this.height = 1;
      }

      protected void increaseHeight() {
         ++this.height;
      }

      public float[] getColor() {
         return this.color;
      }

      public int getHeight() {
         return this.height;
      }
   }
}
