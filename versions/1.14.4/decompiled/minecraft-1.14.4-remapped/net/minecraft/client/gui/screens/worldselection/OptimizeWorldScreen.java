package net.minecraft.client.gui.screens.worldselection;

import com.fox2code.repacker.ClientJarOnly;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelStorageSource;

@ClientJarOnly
public class OptimizeWorldScreen extends Screen {
   private static final Object2IntMap DIMENSION_COLORS = (Object2IntMap)Util.make(new Object2IntOpenCustomHashMap(Util.identityStrategy()), (object2IntOpenCustomHashMap) -> {
      object2IntOpenCustomHashMap.put(DimensionType.OVERWORLD, -13408734);
      object2IntOpenCustomHashMap.put(DimensionType.NETHER, -10075085);
      object2IntOpenCustomHashMap.put(DimensionType.THE_END, -8943531);
      object2IntOpenCustomHashMap.defaultReturnValue(-2236963);
   });
   private final BooleanConsumer callback;
   private final WorldUpgrader upgrader;

   public OptimizeWorldScreen(BooleanConsumer callback, String string, LevelStorageSource levelStorageSource, boolean var4) {
      super(new TranslatableComponent("optimizeWorld.title", new Object[]{levelStorageSource.getDataTagFor(string).getLevelName()}));
      this.callback = callback;
      this.upgrader = new WorldUpgrader(string, levelStorageSource, levelStorageSource.getDataTagFor(string), var4);
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 150, 200, 20, I18n.get("gui.cancel", new Object[0]), (button) -> {
         this.upgrader.cancel();
         this.callback.accept(false);
      }));
   }

   public void tick() {
      if(this.upgrader.isFinished()) {
         this.callback.accept(true);
      }

   }

   public void removed() {
      this.upgrader.cancel();
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, 16777215);
      int var4 = this.width / 2 - 150;
      int var5 = this.width / 2 + 150;
      int var6 = this.height / 4 + 100;
      int var7 = var6 + 10;
      Font var10001 = this.font;
      String var10002 = this.upgrader.getStatus().getColoredString();
      int var10003 = this.width / 2;
      this.font.getClass();
      this.drawCenteredString(var10001, var10002, var10003, var6 - 9 - 2, 10526880);
      if(this.upgrader.getTotalChunks() > 0) {
         fill(var4 - 1, var6 - 1, var5 + 1, var7 + 1, -16777216);
         this.drawString(this.font, I18n.get("optimizeWorld.info.converted", new Object[]{Integer.valueOf(this.upgrader.getConverted())}), var4, 40, 10526880);
         var10001 = this.font;
         var10002 = I18n.get("optimizeWorld.info.skipped", new Object[]{Integer.valueOf(this.upgrader.getSkipped())});
         this.font.getClass();
         this.drawString(var10001, var10002, var4, 40 + 9 + 3, 10526880);
         var10001 = this.font;
         var10002 = I18n.get("optimizeWorld.info.total", new Object[]{Integer.valueOf(this.upgrader.getTotalChunks())});
         this.font.getClass();
         this.drawString(var10001, var10002, var4, 40 + (9 + 3) * 2, 10526880);
         int var8 = 0;

         for(DimensionType var10 : DimensionType.getAllTypes()) {
            int var11 = Mth.floor(this.upgrader.dimensionProgress(var10) * (float)(var5 - var4));
            fill(var4 + var8, var6, var4 + var8 + var11, var7, DIMENSION_COLORS.getInt(var10));
            var8 += var11;
         }

         int var9 = this.upgrader.getConverted() + this.upgrader.getSkipped();
         var10001 = this.font;
         var10002 = var9 + " / " + this.upgrader.getTotalChunks();
         var10003 = this.width / 2;
         this.font.getClass();
         this.drawCenteredString(var10001, var10002, var10003, var6 + 2 * 9 + 2, 10526880);
         var10001 = this.font;
         var10002 = Mth.floor(this.upgrader.getProgress() * 100.0F) + "%";
         var10003 = this.width / 2;
         int var10004 = var6 + (var7 - var6) / 2;
         this.font.getClass();
         this.drawCenteredString(var10001, var10002, var10003, var10004 - 9 / 2, 10526880);
      }

      super.render(var1, var2, var3);
   }
}
