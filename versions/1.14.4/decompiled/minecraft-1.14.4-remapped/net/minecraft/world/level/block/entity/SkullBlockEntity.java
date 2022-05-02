package net.minecraft.world.level.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;

public class SkullBlockEntity extends BlockEntity implements TickableBlockEntity {
   private GameProfile owner;
   private int mouthTickCount;
   private boolean isMovingMouth;
   private static GameProfileCache profileCache;
   private static MinecraftSessionService sessionService;

   public SkullBlockEntity() {
      super(BlockEntityType.SKULL);
   }

   public static void setProfileCache(GameProfileCache profileCache) {
      profileCache = profileCache;
   }

   public static void setSessionService(MinecraftSessionService sessionService) {
      sessionService = sessionService;
   }

   public CompoundTag save(CompoundTag compoundTag) {
      super.save(compoundTag);
      if(this.owner != null) {
         CompoundTag var2 = new CompoundTag();
         NbtUtils.writeGameProfile(var2, this.owner);
         compoundTag.put("Owner", var2);
      }

      return compoundTag;
   }

   public void load(CompoundTag compoundTag) {
      super.load(compoundTag);
      if(compoundTag.contains("Owner", 10)) {
         this.setOwner(NbtUtils.readGameProfile(compoundTag.getCompound("Owner")));
      } else if(compoundTag.contains("ExtraType", 8)) {
         String var2 = compoundTag.getString("ExtraType");
         if(!StringUtil.isNullOrEmpty(var2)) {
            this.setOwner(new GameProfile((UUID)null, var2));
         }
      }

   }

   public void tick() {
      Block var1 = this.getBlockState().getBlock();
      if(var1 == Blocks.DRAGON_HEAD || var1 == Blocks.DRAGON_WALL_HEAD) {
         if(this.level.hasNeighborSignal(this.worldPosition)) {
            this.isMovingMouth = true;
            ++this.mouthTickCount;
         } else {
            this.isMovingMouth = false;
         }
      }

   }

   public float getMouthAnimation(float f) {
      return this.isMovingMouth?(float)this.mouthTickCount + f:(float)this.mouthTickCount;
   }

   @Nullable
   public GameProfile getOwnerProfile() {
      return this.owner;
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 4, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }

   public void setOwner(@Nullable GameProfile owner) {
      this.owner = owner;
      this.updateOwnerProfile();
   }

   private void updateOwnerProfile() {
      this.owner = updateGameprofile(this.owner);
      this.setChanged();
   }

   public static GameProfile updateGameprofile(GameProfile gameProfile) {
      if(gameProfile != null && !StringUtil.isNullOrEmpty(gameProfile.getName())) {
         if(gameProfile.isComplete() && gameProfile.getProperties().containsKey("textures")) {
            return gameProfile;
         } else if(profileCache != null && sessionService != null) {
            GameProfile var1 = profileCache.get(gameProfile.getName());
            if(var1 == null) {
               return gameProfile;
            } else {
               Property var2 = (Property)Iterables.getFirst(var1.getProperties().get("textures"), (Object)null);
               if(var2 == null) {
                  var1 = sessionService.fillProfileProperties(var1, true);
               }

               return var1;
            }
         } else {
            return gameProfile;
         }
      } else {
         return gameProfile;
      }
   }
}
