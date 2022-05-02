package net.minecraft.world.entity.ai.attributes;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.util.Mth;

public class AttributeModifier {
   private final double amount;
   private final AttributeModifier.Operation operation;
   private final Supplier nameGetter;
   private final UUID id;
   private boolean serialize;

   public AttributeModifier(String string, double var2, AttributeModifier.Operation attributeModifier$Operation) {
      this(Mth.createInsecureUUID(ThreadLocalRandom.current()), () -> {
         return string;
      }, var2, attributeModifier$Operation);
   }

   public AttributeModifier(UUID uUID, String string, double var3, AttributeModifier.Operation attributeModifier$Operation) {
      this(uUID, () -> {
         return string;
      }, var3, attributeModifier$Operation);
   }

   public AttributeModifier(UUID id, Supplier nameGetter, double amount, AttributeModifier.Operation operation) {
      this.serialize = true;
      this.id = id;
      this.nameGetter = nameGetter;
      this.amount = amount;
      this.operation = operation;
   }

   public UUID getId() {
      return this.id;
   }

   public String getName() {
      return (String)this.nameGetter.get();
   }

   public AttributeModifier.Operation getOperation() {
      return this.operation;
   }

   public double getAmount() {
      return this.amount;
   }

   public boolean isSerializable() {
      return this.serialize;
   }

   public AttributeModifier setSerialize(boolean serialize) {
      this.serialize = serialize;
      return this;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object != null && this.getClass() == object.getClass()) {
         AttributeModifier var2 = (AttributeModifier)object;
         return Objects.equals(this.id, var2.id);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.id != null?this.id.hashCode():0;
   }

   public String toString() {
      return "AttributeModifier{amount=" + this.amount + ", operation=" + this.operation + ", name=\'" + (String)this.nameGetter.get() + '\'' + ", id=" + this.id + ", serialize=" + this.serialize + '}';
   }

   public static enum Operation {
      ADDITION(0),
      MULTIPLY_BASE(1),
      MULTIPLY_TOTAL(2);

      private static final AttributeModifier.Operation[] OPERATIONS = new AttributeModifier.Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
      private final int value;

      private Operation(int value) {
         this.value = value;
      }

      public int toValue() {
         return this.value;
      }

      public static AttributeModifier.Operation fromValue(int value) {
         if(value >= 0 && value < OPERATIONS.length) {
            return OPERATIONS[value];
         } else {
            throw new IllegalArgumentException("No operation with value " + value);
         }
      }
   }
}
