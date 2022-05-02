package net.minecraft.world.entity.decoration;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddPaintingPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class Painting extends HangingEntity {
   public Motive motive;

   public Painting(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public Painting(Level level, BlockPos blockPos, Direction direction) {
      super(EntityType.PAINTING, level, blockPos);
      List<Motive> var4 = Lists.newArrayList();
      int var5 = 0;

      for(Motive var7 : Registry.MOTIVE) {
         this.motive = var7;
         this.setDirection(direction);
         if(this.survives()) {
            var4.add(var7);
            int var8 = var7.getWidth() * var7.getHeight();
            if(var8 > var5) {
               var5 = var8;
            }
         }
      }

      if(!var4.isEmpty()) {
         Iterator<Motive> var6 = var4.iterator();

         while(var6.hasNext()) {
            Motive var7 = (Motive)var6.next();
            if(var7.getWidth() * var7.getHeight() < var5) {
               var6.remove();
            }
         }

         this.motive = (Motive)var4.get(this.random.nextInt(var4.size()));
      }

      this.setDirection(direction);
   }

   public Painting(Level level, BlockPos blockPos, Direction direction, Motive motive) {
      this(level, blockPos, direction);
      this.motive = motive;
      this.setDirection(direction);
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      compoundTag.putString("Motive", Registry.MOTIVE.getKey(this.motive).toString());
      super.addAdditionalSaveData(compoundTag);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      this.motive = (Motive)Registry.MOTIVE.get(ResourceLocation.tryParse(compoundTag.getString("Motive")));
      super.readAdditionalSaveData(compoundTag);
   }

   public int getWidth() {
      return this.motive == null?1:this.motive.getWidth();
   }

   public int getHeight() {
      return this.motive == null?1:this.motive.getHeight();
   }

   public void dropItem(@Nullable Entity entity) {
      if(this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
         if(entity instanceof Player) {
            Player var2 = (Player)entity;
            if(var2.abilities.instabuild) {
               return;
            }
         }

         this.spawnAtLocation(Items.PAINTING);
      }
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
   }

   public void moveTo(double var1, double var3, double var5, float var7, float var8) {
      this.setPos(var1, var3, var5);
   }

   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      BlockPos var11 = this.pos.offset(var1 - this.x, var3 - this.y, var5 - this.z);
      this.setPos((double)var11.getX(), (double)var11.getY(), (double)var11.getZ());
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddPaintingPacket(this);
   }
}
