package net.minecraft.world.level;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Abilities;

public enum GameType {
   NOT_SET(-1, ""),
   SURVIVAL(0, "survival"),
   CREATIVE(1, "creative"),
   ADVENTURE(2, "adventure"),
   SPECTATOR(3, "spectator");

   private final int id;
   private final String name;

   private GameType(int id, String name) {
      this.id = id;
      this.name = name;
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Component getDisplayName() {
      return new TranslatableComponent("gameMode." + this.name, new Object[0]);
   }

   public void updatePlayerAbilities(Abilities abilities) {
      if(this == CREATIVE) {
         abilities.mayfly = true;
         abilities.instabuild = true;
         abilities.invulnerable = true;
      } else if(this == SPECTATOR) {
         abilities.mayfly = true;
         abilities.instabuild = false;
         abilities.invulnerable = true;
         abilities.flying = true;
      } else {
         abilities.mayfly = false;
         abilities.instabuild = false;
         abilities.invulnerable = false;
         abilities.flying = false;
      }

      abilities.mayBuild = !this.isBlockPlacingRestricted();
   }

   public boolean isBlockPlacingRestricted() {
      return this == ADVENTURE || this == SPECTATOR;
   }

   public boolean isCreative() {
      return this == CREATIVE;
   }

   public boolean isSurvival() {
      return this == SURVIVAL || this == ADVENTURE;
   }

   public static GameType byId(int id) {
      return byId(id, SURVIVAL);
   }

   public static GameType byId(int var0, GameType var1) {
      for(GameType var5 : values()) {
         if(var5.id == var0) {
            return var5;
         }
      }

      return var1;
   }

   public static GameType byName(String name) {
      return byName(name, SURVIVAL);
   }

   public static GameType byName(String string, GameType var1) {
      for(GameType var5 : values()) {
         if(var5.name.equals(string)) {
            return var5;
         }
      }

      return var1;
   }
}
