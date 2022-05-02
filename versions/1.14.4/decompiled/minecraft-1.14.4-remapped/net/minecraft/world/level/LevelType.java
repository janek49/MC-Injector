package net.minecraft.world.level;

import javax.annotation.Nullable;

public class LevelType {
   public static final LevelType[] LEVEL_TYPES = new LevelType[16];
   public static final LevelType NORMAL = (new LevelType(0, "default", 1)).setHasReplacement();
   public static final LevelType FLAT = (new LevelType(1, "flat")).setCustomOptions(true);
   public static final LevelType LARGE_BIOMES = new LevelType(2, "largeBiomes");
   public static final LevelType AMPLIFIED = (new LevelType(3, "amplified")).setHasHelpText();
   public static final LevelType CUSTOMIZED = (new LevelType(4, "customized", "normal", 0)).setCustomOptions(true).setSelectableByUser(false);
   public static final LevelType BUFFET = (new LevelType(5, "buffet")).setCustomOptions(true);
   public static final LevelType DEBUG_ALL_BLOCK_STATES = new LevelType(6, "debug_all_block_states");
   public static final LevelType NORMAL_1_1 = (new LevelType(8, "default_1_1", 0)).setSelectableByUser(false);
   private final int id;
   private final String generatorName;
   private final String generatorSerialization;
   private final int version;
   private boolean selectable;
   private boolean replacement;
   private boolean hasHelpText;
   private boolean hasCustomOptions;

   private LevelType(int var1, String string) {
      this(var1, string, string, 0);
   }

   private LevelType(int var1, String string, int var3) {
      this(var1, string, string, var3);
   }

   private LevelType(int id, String generatorName, String generatorSerialization, int version) {
      this.generatorName = generatorName;
      this.generatorSerialization = generatorSerialization;
      this.version = version;
      this.selectable = true;
      this.id = id;
      LEVEL_TYPES[id] = this;
   }

   public String getName() {
      return this.generatorName;
   }

   public String getSerialization() {
      return this.generatorSerialization;
   }

   public String getDescriptionId() {
      return "generator." + this.generatorName;
   }

   public String getHelpTextId() {
      return this.getDescriptionId() + ".info";
   }

   public int getVersion() {
      return this.version;
   }

   public LevelType getReplacementForVersion(int i) {
      return this == NORMAL && i == 0?NORMAL_1_1:this;
   }

   public boolean hasCustomOptions() {
      return this.hasCustomOptions;
   }

   public LevelType setCustomOptions(boolean customOptions) {
      this.hasCustomOptions = customOptions;
      return this;
   }

   private LevelType setSelectableByUser(boolean selectableByUser) {
      this.selectable = selectableByUser;
      return this;
   }

   public boolean isSelectable() {
      return this.selectable;
   }

   private LevelType setHasReplacement() {
      this.replacement = true;
      return this;
   }

   public boolean hasReplacement() {
      return this.replacement;
   }

   @Nullable
   public static LevelType getLevelType(String string) {
      for(LevelType var4 : LEVEL_TYPES) {
         if(var4 != null && var4.generatorName.equalsIgnoreCase(string)) {
            return var4;
         }
      }

      return null;
   }

   public int getId() {
      return this.id;
   }

   public boolean hasHelpText() {
      return this.hasHelpText;
   }

   private LevelType setHasHelpText() {
      this.hasHelpText = true;
      return this;
   }
}
