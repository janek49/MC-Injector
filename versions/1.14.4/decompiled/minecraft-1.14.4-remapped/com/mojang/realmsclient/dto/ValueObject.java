package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@ClientJarOnly
public abstract class ValueObject {
   public String toString() {
      StringBuilder var1 = new StringBuilder("{");

      for(Field var5 : this.getClass().getFields()) {
         if(!isStatic(var5)) {
            try {
               var1.append(var5.getName()).append("=").append(var5.get(this)).append(" ");
            } catch (IllegalAccessException var7) {
               ;
            }
         }
      }

      var1.deleteCharAt(var1.length() - 1);
      var1.append('}');
      return var1.toString();
   }

   private static boolean isStatic(Field field) {
      return Modifier.isStatic(field.getModifiers());
   }
}
