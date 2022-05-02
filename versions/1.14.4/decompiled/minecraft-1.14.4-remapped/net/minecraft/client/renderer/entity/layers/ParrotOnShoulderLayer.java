package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

@ClientJarOnly
public class ParrotOnShoulderLayer extends RenderLayer {
   private final ParrotModel model = new ParrotModel();

   public ParrotOnShoulderLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(Player player, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.render(player, var2, var3, var4, var6, var7, var8, true);
      this.render(player, var2, var3, var4, var6, var7, var8, false);
      GlStateManager.disableRescaleNormal();
   }

   private void render(Player player, float var2, float var3, float var4, float var5, float var6, float var7, boolean var8) {
      CompoundTag var9 = var8?player.getShoulderEntityLeft():player.getShoulderEntityRight();
      EntityType.byString(var9.getString("id")).filter((entityType) -> {
         return entityType == EntityType.PARROT;
      }).ifPresent((entityType) -> {
         GlStateManager.pushMatrix();
         GlStateManager.translatef(var8?0.4F:-0.4F, player.isVisuallySneaking()?-1.3F:-1.5F, 0.0F);
         this.bindTexture(ParrotRenderer.PARROT_LOCATIONS[var9.getInt("Variant")]);
         this.model.renderOnShoulder(var2, var3, var5, var6, var7, player.tickCount);
         GlStateManager.popMatrix();
      });
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
