package net.minecraft.server.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;

public class StatsComponent extends JComponent {
   private static final DecimalFormat DECIMAL_FORMAT = (DecimalFormat)Util.make(new DecimalFormat("########0.000"), (decimalFormat) -> {
      decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   });
   private final int[] values = new int[256];
   private int vp;
   private final String[] msgs = new String[11];
   private final MinecraftServer server;
   private final Timer timer;

   public StatsComponent(MinecraftServer server) {
      this.server = server;
      this.setPreferredSize(new Dimension(456, 246));
      this.setMinimumSize(new Dimension(456, 246));
      this.setMaximumSize(new Dimension(456, 246));
      this.timer = new Timer(500, (actionEvent) -> {
         this.tick();
      });
      this.timer.start();
      this.setBackground(Color.BLACK);
   }

   private void tick() {
      long var1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      this.msgs[0] = "Memory use: " + var1 / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
      this.msgs[1] = "Avg tick: " + DECIMAL_FORMAT.format(this.getAverage(this.server.tickTimes) * 1.0E-6D) + " ms";
      this.values[this.vp++ & 255] = (int)(var1 * 100L / Runtime.getRuntime().maxMemory());
      this.repaint();
   }

   private double getAverage(long[] longs) {
      long var2 = 0L;

      for(long var7 : longs) {
         var2 += var7;
      }

      return (double)var2 / (double)longs.length;
   }

   public void paint(Graphics graphics) {
      graphics.setColor(new Color(16777215));
      graphics.fillRect(0, 0, 456, 246);

      for(int var2 = 0; var2 < 256; ++var2) {
         int var3 = this.values[var2 + this.vp & 255];
         graphics.setColor(new Color(var3 + 28 << 16));
         graphics.fillRect(var2, 100 - var3, 1, var3);
      }

      graphics.setColor(Color.BLACK);

      for(int var2 = 0; var2 < this.msgs.length; ++var2) {
         String var3 = this.msgs[var2];
         if(var3 != null) {
            graphics.drawString(var3, 32, 116 + var2 * 16);
         }
      }

   }

   public void close() {
      this.timer.stop();
   }
}
