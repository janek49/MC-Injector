package net.minecraft.commands.arguments.blocks;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockInput implements Predicate {
   private final BlockState state;
   private final Set properties;
   @Nullable
   private final CompoundTag tag;

   public BlockInput(BlockState state, Set properties, @Nullable CompoundTag tag) {
      this.state = state;
      this.properties = properties;
      this.tag = tag;
   }

   public BlockState getState() {
      return this.state;
   }

   public boolean test(BlockInWorld blockInWorld) {
      BlockState var2 = blockInWorld.getState();
      if(var2.getBlock() != this.state.getBlock()) {
         return false;
      } else {
         for(Property<?> var4 : this.properties) {
            if(var2.getValue(var4) != this.state.getValue(var4)) {
               return false;
            }
         }

         if(this.tag == null) {
            return true;
         } else {
            BlockEntity var3 = blockInWorld.getEntity();
            return var3 != null && NbtUtils.compareNbt(this.tag, var3.save(new CompoundTag()), true);
         }
      }
   }

   public boolean place(ServerLevel serverLevel, BlockPos blockPos, int var3) {
      if(!serverLevel.setBlock(blockPos, this.state, var3)) {
         return false;
      } else {
         if(this.tag != null) {
            BlockEntity var4 = serverLevel.getBlockEntity(blockPos);
            if(var4 != null) {
               CompoundTag var5 = this.tag.copy();
               var5.putInt("x", blockPos.getX());
               var5.putInt("y", blockPos.getY());
               var5.putInt("z", blockPos.getZ());
               var4.load(var5);
            }
         }

         return true;
      }
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((BlockInWorld)var1);
   }
}
