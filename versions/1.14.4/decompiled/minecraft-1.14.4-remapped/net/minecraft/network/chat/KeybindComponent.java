package net.minecraft.network.chat;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;

public class KeybindComponent extends BaseComponent {
   public static Function keyResolver = (string) -> {
      return () -> {
         return string;
      };
   };
   private final String name;
   private Supplier nameResolver;

   public KeybindComponent(String name) {
      this.name = name;
   }

   public String getContents() {
      if(this.nameResolver == null) {
         this.nameResolver = (Supplier)keyResolver.apply(this.name);
      }

      return (String)this.nameResolver.get();
   }

   public KeybindComponent copy() {
      return new KeybindComponent(this.name);
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof KeybindComponent)) {
         return false;
      } else {
         KeybindComponent var2 = (KeybindComponent)object;
         return this.name.equals(var2.name) && super.equals(object);
      }
   }

   public String toString() {
      return "KeybindComponent{keybind=\'" + this.name + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   public String getName() {
      return this.name;
   }

   // $FF: synthetic method
   public Component copy() {
      return this.copy();
   }
}
