package com.mojang.realmsclient;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Arrays;

@ClientJarOnly
public class KeyCombo {
   private final char[] chars;
   private int matchIndex;
   private final Runnable onCompletion;

   public KeyCombo(char[] chars, Runnable onCompletion) {
      this.onCompletion = onCompletion;
      if(chars.length < 1) {
         throw new IllegalArgumentException("Must have at least one char");
      } else {
         this.chars = chars;
         this.matchIndex = 0;
      }
   }

   public boolean keyPressed(char c) {
      if(c == this.chars[this.matchIndex]) {
         ++this.matchIndex;
         if(this.matchIndex == this.chars.length) {
            this.reset();
            this.onCompletion.run();
            return true;
         } else {
            return false;
         }
      } else {
         this.reset();
         return false;
      }
   }

   public void reset() {
      this.matchIndex = 0;
   }

   public String toString() {
      return "KeyCombo{chars=" + Arrays.toString(this.chars) + ", matchIndex=" + this.matchIndex + '}';
   }
}
