package net.minecraft.client.tutorial;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.player.Input;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

@ClientJarOnly
public interface TutorialStepInstance {
   default void clear() {
   }

   default void tick() {
   }

   default void onInput(Input input) {
   }

   default void onMouse(double var1, double var3) {
   }

   default void onLookAt(MultiPlayerLevel multiPlayerLevel, HitResult hitResult) {
   }

   default void onDestroyBlock(MultiPlayerLevel multiPlayerLevel, BlockPos blockPos, BlockState blockState, float var4) {
   }

   default void onOpenInventory() {
   }

   default void onGetItem(ItemStack itemStack) {
   }
}
