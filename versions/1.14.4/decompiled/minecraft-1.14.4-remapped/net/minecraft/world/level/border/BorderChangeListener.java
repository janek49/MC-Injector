package net.minecraft.world.level.border;

import net.minecraft.world.level.border.WorldBorder;

public interface BorderChangeListener {
   void onBorderSizeSet(WorldBorder var1, double var2);

   void onBorderSizeLerping(WorldBorder var1, double var2, double var4, long var6);

   void onBorderCenterSet(WorldBorder var1, double var2, double var4);

   void onBorderSetWarningTime(WorldBorder var1, int var2);

   void onBorderSetWarningBlocks(WorldBorder var1, int var2);

   void onBorderSetDamagePerBlock(WorldBorder var1, double var2);

   void onBorderSetDamageSafeZOne(WorldBorder var1, double var2);

   public static class DelegateBorderChangeListener implements BorderChangeListener {
      private final WorldBorder worldBorder;

      public DelegateBorderChangeListener(WorldBorder worldBorder) {
         this.worldBorder = worldBorder;
      }

      public void onBorderSizeSet(WorldBorder worldBorder, double var2) {
         this.worldBorder.setSize(var2);
      }

      public void onBorderSizeLerping(WorldBorder worldBorder, double var2, double var4, long var6) {
         this.worldBorder.lerpSizeBetween(var2, var4, var6);
      }

      public void onBorderCenterSet(WorldBorder worldBorder, double var2, double var4) {
         this.worldBorder.setCenter(var2, var4);
      }

      public void onBorderSetWarningTime(WorldBorder worldBorder, int var2) {
         this.worldBorder.setWarningTime(var2);
      }

      public void onBorderSetWarningBlocks(WorldBorder worldBorder, int var2) {
         this.worldBorder.setWarningBlocks(var2);
      }

      public void onBorderSetDamagePerBlock(WorldBorder worldBorder, double var2) {
         this.worldBorder.setDamagePerBlock(var2);
      }

      public void onBorderSetDamageSafeZOne(WorldBorder worldBorder, double var2) {
         this.worldBorder.setDamageSafeZone(var2);
      }
   }
}
