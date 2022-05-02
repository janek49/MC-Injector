package net.minecraft.network.chat;

import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;

public class TextComponent extends BaseComponent {
   private final String text;

   public TextComponent(String text) {
      this.text = text;
   }

   public String getText() {
      return this.text;
   }

   public String getContents() {
      return this.text;
   }

   public TextComponent copy() {
      return new TextComponent(this.text);
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof TextComponent)) {
         return false;
      } else {
         TextComponent var2 = (TextComponent)object;
         return this.text.equals(var2.getText()) && super.equals(object);
      }
   }

   public String toString() {
      return "TextComponent{text=\'" + this.text + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   // $FF: synthetic method
   public Component copy() {
      return this.copy();
   }
}
