package net.minecraft.world;

import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public abstract class BossEvent {
   private final UUID id;
   protected Component name;
   protected float percent;
   protected BossEvent.BossBarColor color;
   protected BossEvent.BossBarOverlay overlay;
   protected boolean darkenScreen;
   protected boolean playBossMusic;
   protected boolean createWorldFog;

   public BossEvent(UUID id, Component name, BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay) {
      this.id = id;
      this.name = name;
      this.color = color;
      this.overlay = overlay;
      this.percent = 1.0F;
   }

   public UUID getId() {
      return this.id;
   }

   public Component getName() {
      return this.name;
   }

   public void setName(Component name) {
      this.name = name;
   }

   public float getPercent() {
      return this.percent;
   }

   public void setPercent(float percent) {
      this.percent = percent;
   }

   public BossEvent.BossBarColor getColor() {
      return this.color;
   }

   public void setColor(BossEvent.BossBarColor color) {
      this.color = color;
   }

   public BossEvent.BossBarOverlay getOverlay() {
      return this.overlay;
   }

   public void setOverlay(BossEvent.BossBarOverlay overlay) {
      this.overlay = overlay;
   }

   public boolean shouldDarkenScreen() {
      return this.darkenScreen;
   }

   public BossEvent setDarkenScreen(boolean darkenScreen) {
      this.darkenScreen = darkenScreen;
      return this;
   }

   public boolean shouldPlayBossMusic() {
      return this.playBossMusic;
   }

   public BossEvent setPlayBossMusic(boolean playBossMusic) {
      this.playBossMusic = playBossMusic;
      return this;
   }

   public BossEvent setCreateWorldFog(boolean createWorldFog) {
      this.createWorldFog = createWorldFog;
      return this;
   }

   public boolean shouldCreateWorldFog() {
      return this.createWorldFog;
   }

   public static enum BossBarColor {
      PINK("pink", ChatFormatting.RED),
      BLUE("blue", ChatFormatting.BLUE),
      RED("red", ChatFormatting.DARK_RED),
      GREEN("green", ChatFormatting.GREEN),
      YELLOW("yellow", ChatFormatting.YELLOW),
      PURPLE("purple", ChatFormatting.DARK_BLUE),
      WHITE("white", ChatFormatting.WHITE);

      private final String name;
      private final ChatFormatting formatting;

      private BossBarColor(String name, ChatFormatting formatting) {
         this.name = name;
         this.formatting = formatting;
      }

      public ChatFormatting getFormatting() {
         return this.formatting;
      }

      public String getName() {
         return this.name;
      }

      public static BossEvent.BossBarColor byName(String name) {
         for(BossEvent.BossBarColor var4 : values()) {
            if(var4.name.equals(name)) {
               return var4;
            }
         }

         return WHITE;
      }
   }

   public static enum BossBarOverlay {
      PROGRESS("progress"),
      NOTCHED_6("notched_6"),
      NOTCHED_10("notched_10"),
      NOTCHED_12("notched_12"),
      NOTCHED_20("notched_20");

      private final String name;

      private BossBarOverlay(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public static BossEvent.BossBarOverlay byName(String name) {
         for(BossEvent.BossBarOverlay var4 : values()) {
            if(var4.name.equals(name)) {
               return var4;
            }
         }

         return PROGRESS;
      }
   }
}
