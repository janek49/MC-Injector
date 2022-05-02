package net.minecraft.world.level.dimension;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.io.File;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Serializable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.dimension.NetherDimension;
import net.minecraft.world.level.dimension.NormalDimension;
import net.minecraft.world.level.dimension.end.TheEndDimension;

public class DimensionType implements Serializable {
   public static final DimensionType OVERWORLD = register("overworld", new DimensionType(1, "", "", NormalDimension::<init>, true));
   public static final DimensionType NETHER = register("the_nether", new DimensionType(0, "_nether", "DIM-1", NetherDimension::<init>, false));
   public static final DimensionType THE_END = register("the_end", new DimensionType(2, "_end", "DIM1", TheEndDimension::<init>, false));
   private final int id;
   private final String fileSuffix;
   private final String folder;
   private final BiFunction factory;
   private final boolean hasSkylight;

   private static DimensionType register(String string, DimensionType var1) {
      return (DimensionType)Registry.registerMapping(Registry.DIMENSION_TYPE, var1.id, string, var1);
   }

   protected DimensionType(int id, String fileSuffix, String folder, BiFunction factory, boolean hasSkylight) {
      this.id = id;
      this.fileSuffix = fileSuffix;
      this.folder = folder;
      this.factory = factory;
      this.hasSkylight = hasSkylight;
   }

   public static DimensionType of(Dynamic dynamic) {
      return (DimensionType)Registry.DIMENSION_TYPE.get(new ResourceLocation(dynamic.asString("")));
   }

   public static Iterable getAllTypes() {
      return Registry.DIMENSION_TYPE;
   }

   public int getId() {
      return this.id + -1;
   }

   public String getFileSuffix() {
      return this.fileSuffix;
   }

   public File getStorageFolder(File file) {
      return this.folder.isEmpty()?file:new File(file, this.folder);
   }

   public Dimension create(Level level) {
      return (Dimension)this.factory.apply(level, this);
   }

   public String toString() {
      return getName(this).toString();
   }

   @Nullable
   public static DimensionType getById(int id) {
      return (DimensionType)Registry.DIMENSION_TYPE.byId(id - -1);
   }

   @Nullable
   public static DimensionType getByName(ResourceLocation name) {
      return (DimensionType)Registry.DIMENSION_TYPE.get(name);
   }

   @Nullable
   public static ResourceLocation getName(DimensionType dimensionType) {
      return Registry.DIMENSION_TYPE.getKey(dimensionType);
   }

   public boolean hasSkyLight() {
      return this.hasSkylight;
   }

   public Object serialize(DynamicOps dynamicOps) {
      return dynamicOps.createString(Registry.DIMENSION_TYPE.getKey(this).toString());
   }
}
