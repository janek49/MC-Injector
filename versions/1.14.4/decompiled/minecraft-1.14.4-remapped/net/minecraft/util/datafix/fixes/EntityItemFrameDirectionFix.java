package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class EntityItemFrameDirectionFix extends NamedEntityFix {
   public EntityItemFrameDirectionFix(Schema schema, boolean var2) {
      super(schema, var2, "EntityItemFrameDirectionFix", References.ENTITY, "minecraft:item_frame");
   }

   public Dynamic fixTag(Dynamic dynamic) {
      return dynamic.set("Facing", dynamic.createByte(direction2dTo3d(dynamic.get("Facing").asByte((byte)0))));
   }

   protected Typed fix(Typed typed) {
      return typed.update(DSL.remainderFinder(), this::fixTag);
   }

   private static byte direction2dTo3d(byte b) {
      switch(b) {
      case 0:
         return (byte)3;
      case 1:
         return (byte)4;
      case 2:
      default:
         return (byte)2;
      case 3:
         return (byte)5;
      }
   }
}
