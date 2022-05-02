package net.minecraft.world.level.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class JukeboxBlockEntity extends BlockEntity implements Clearable {
   private ItemStack record = ItemStack.EMPTY;

   public JukeboxBlockEntity() {
      super(BlockEntityType.JUKEBOX);
   }

   public void load(CompoundTag compoundTag) {
      super.load(compoundTag);
      if(compoundTag.contains("RecordItem", 10)) {
         this.setRecord(ItemStack.of(compoundTag.getCompound("RecordItem")));
      }

   }

   public CompoundTag save(CompoundTag compoundTag) {
      super.save(compoundTag);
      if(!this.getRecord().isEmpty()) {
         compoundTag.put("RecordItem", this.getRecord().save(new CompoundTag()));
      }

      return compoundTag;
   }

   public ItemStack getRecord() {
      return this.record;
   }

   public void setRecord(ItemStack record) {
      this.record = record;
      this.setChanged();
   }

   public void clearContent() {
      this.setRecord(ItemStack.EMPTY);
   }
}
