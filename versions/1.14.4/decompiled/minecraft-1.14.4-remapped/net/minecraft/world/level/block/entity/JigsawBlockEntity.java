package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class JigsawBlockEntity extends BlockEntity {
   private ResourceLocation attachementType;
   private ResourceLocation targetPool;
   private String finalState;

   public JigsawBlockEntity(BlockEntityType blockEntityType) {
      super(blockEntityType);
      this.attachementType = new ResourceLocation("empty");
      this.targetPool = new ResourceLocation("empty");
      this.finalState = "minecraft:air";
   }

   public JigsawBlockEntity() {
      this(BlockEntityType.JIGSAW);
   }

   public ResourceLocation getAttachementType() {
      return this.attachementType;
   }

   public ResourceLocation getTargetPool() {
      return this.targetPool;
   }

   public String getFinalState() {
      return this.finalState;
   }

   public void setAttachementType(ResourceLocation attachementType) {
      this.attachementType = attachementType;
   }

   public void setTargetPool(ResourceLocation targetPool) {
      this.targetPool = targetPool;
   }

   public void setFinalState(String finalState) {
      this.finalState = finalState;
   }

   public CompoundTag save(CompoundTag compoundTag) {
      super.save(compoundTag);
      compoundTag.putString("attachement_type", this.attachementType.toString());
      compoundTag.putString("target_pool", this.targetPool.toString());
      compoundTag.putString("final_state", this.finalState);
      return compoundTag;
   }

   public void load(CompoundTag compoundTag) {
      super.load(compoundTag);
      this.attachementType = new ResourceLocation(compoundTag.getString("attachement_type"));
      this.targetPool = new ResourceLocation(compoundTag.getString("target_pool"));
      this.finalState = compoundTag.getString("final_state");
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 12, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }
}
