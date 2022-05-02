package net.minecraft.client.renderer.block;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.EntityBlockRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

@ClientJarOnly
public class AnimatedEntityBlockRenderer {
   public void renderSingleBlock(Block block, float var2) {
      GlStateManager.color4f(var2, var2, var2, 1.0F);
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      EntityBlockRenderer.instance.renderByItem(new ItemStack(block));
   }
}
