package net.minecraft.data.structures;

import net.minecraft.data.structures.SnbtToNbt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class StructureUpdater implements SnbtToNbt.Filter {
   public CompoundTag apply(String string, CompoundTag var2) {
      return string.startsWith("data/minecraft/structures/")?updateStructure(patchVersion(var2)):var2;
   }

   private static CompoundTag patchVersion(CompoundTag compoundTag) {
      if(!compoundTag.contains("DataVersion", 99)) {
         compoundTag.putInt("DataVersion", 500);
      }

      return compoundTag;
   }

   private static CompoundTag updateStructure(CompoundTag compoundTag) {
      StructureTemplate var1 = new StructureTemplate();
      var1.load(NbtUtils.update(DataFixers.getDataFixer(), DataFixTypes.STRUCTURE, compoundTag, compoundTag.getInt("DataVersion")));
      return var1.save(new CompoundTag());
   }
}
