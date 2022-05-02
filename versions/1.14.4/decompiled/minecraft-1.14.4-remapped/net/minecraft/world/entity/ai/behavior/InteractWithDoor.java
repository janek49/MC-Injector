package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;

public class InteractWithDoor extends Behavior {
   public InteractWithDoor() {
      super(ImmutableMap.of(MemoryModuleType.PATH, MemoryStatus.VALUE_PRESENT, MemoryModuleType.INTERACTABLE_DOORS, MemoryStatus.VALUE_PRESENT, MemoryModuleType.OPENED_DOORS, MemoryStatus.REGISTERED));
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      Brain<?> var5 = livingEntity.getBrain();
      Path var6 = (Path)var5.getMemory(MemoryModuleType.PATH).get();
      List<GlobalPos> var7 = (List)var5.getMemory(MemoryModuleType.INTERACTABLE_DOORS).get();
      List<BlockPos> var8 = (List)var6.getNodes().stream().map((node) -> {
         return new BlockPos(node.x, node.y, node.z);
      }).collect(Collectors.toList());
      Set<BlockPos> var9 = this.getDoorsThatAreOnMyPath(serverLevel, var7, var8);
      int var10 = var6.getIndex() - 1;
      this.openOrCloseDoors(serverLevel, var8, var9, var10, livingEntity, var5);
   }

   private Set getDoorsThatAreOnMyPath(ServerLevel serverLevel, List var2, List var3) {
      Stream var10000 = var2.stream().filter((globalPos) -> {
         return globalPos.dimension() == serverLevel.getDimension().getType();
      }).map(GlobalPos::pos);
      var3.getClass();
      return (Set)var10000.filter(var3::contains).collect(Collectors.toSet());
   }

   private void openOrCloseDoors(ServerLevel serverLevel, List list, Set set, int var4, LivingEntity livingEntity, Brain brain) {
      set.forEach((blockPos) -> {
         int var5 = list.indexOf(blockPos);
         BlockState var6 = serverLevel.getBlockState(blockPos);
         Block var7 = var6.getBlock();
         if(BlockTags.WOODEN_DOORS.contains(var7) && var7 instanceof DoorBlock) {
            boolean var8 = var5 >= var4;
            ((DoorBlock)var7).setOpen(serverLevel, blockPos, var8);
            GlobalPos var9 = GlobalPos.of(serverLevel.getDimension().getType(), blockPos);
            if(!brain.getMemory(MemoryModuleType.OPENED_DOORS).isPresent() && var8) {
               brain.setMemory(MemoryModuleType.OPENED_DOORS, (Object)Sets.newHashSet(new GlobalPos[]{var9}));
            } else {
               brain.getMemory(MemoryModuleType.OPENED_DOORS).ifPresent((set) -> {
                  if(var8) {
                     set.add(var9);
                  } else {
                     set.remove(var9);
                  }

               });
            }
         }

      });
      closeAllOpenedDoors(serverLevel, list, var4, livingEntity, brain);
   }

   public static void closeAllOpenedDoors(ServerLevel serverLevel, List list, int var2, LivingEntity livingEntity, Brain brain) {
      brain.getMemory(MemoryModuleType.OPENED_DOORS).ifPresent((set) -> {
         Iterator<GlobalPos> var5 = set.iterator();

         while(var5.hasNext()) {
            GlobalPos var6 = (GlobalPos)var5.next();
            BlockPos var7 = var6.pos();
            int var8 = list.indexOf(var7);
            if(serverLevel.getDimension().getType() != var6.dimension()) {
               var5.remove();
            } else {
               BlockState var9 = serverLevel.getBlockState(var7);
               Block var10 = var9.getBlock();
               if(BlockTags.WOODEN_DOORS.contains(var10) && var10 instanceof DoorBlock && var8 < var2 && var7.closerThan(livingEntity.position(), 4.0D)) {
                  ((DoorBlock)var10).setOpen(serverLevel, var7, false);
                  var5.remove();
               }
            }
         }

      });
   }
}
