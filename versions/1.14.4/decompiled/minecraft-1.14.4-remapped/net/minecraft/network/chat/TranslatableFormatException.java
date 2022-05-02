package net.minecraft.network.chat;

import net.minecraft.network.chat.TranslatableComponent;

public class TranslatableFormatException extends IllegalArgumentException {
   public TranslatableFormatException(TranslatableComponent translatableComponent, String string) {
      super(String.format("Error parsing: %s: %s", new Object[]{translatableComponent, string}));
   }

   public TranslatableFormatException(TranslatableComponent translatableComponent, int var2) {
      super(String.format("Invalid index %d requested for %s", new Object[]{Integer.valueOf(var2), translatableComponent}));
   }

   public TranslatableFormatException(TranslatableComponent translatableComponent, Throwable throwable) {
      super(String.format("Error while parsing: %s", new Object[]{translatableComponent}), throwable);
   }
}
