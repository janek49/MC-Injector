package net.minecraft.client.gui.font;

import com.fox2code.repacker.ClientJarOnly;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;

@ClientJarOnly
public class TextFieldHelper {
   private final Minecraft minecraft;
   private final Font font;
   private final Supplier getMessageFn;
   private final Consumer setMessageFn;
   private final int maxWidth;
   private int cursorPos;
   private int selectionPos;

   public TextFieldHelper(Minecraft minecraft, Supplier getMessageFn, Consumer setMessageFn, int maxWidth) {
      this.minecraft = minecraft;
      this.font = minecraft.font;
      this.getMessageFn = getMessageFn;
      this.setMessageFn = setMessageFn;
      this.maxWidth = maxWidth;
      this.setEnd();
   }

   public boolean charTyped(char c) {
      if(SharedConstants.isAllowedChatCharacter(c)) {
         this.insertText(Character.toString(c));
      }

      return true;
   }

   private void insertText(String string) {
      if(this.selectionPos != this.cursorPos) {
         this.deleteSelection();
      }

      String string = (String)this.getMessageFn.get();
      this.cursorPos = Mth.clamp(this.cursorPos, 0, string.length());
      String var3 = (new StringBuilder(string)).insert(this.cursorPos, string).toString();
      if(this.font.width(var3) <= this.maxWidth) {
         this.setMessageFn.accept(var3);
         this.selectionPos = this.cursorPos = Math.min(var3.length(), this.cursorPos + string.length());
      }

   }

   public boolean keyPressed(int i) {
      String var2 = (String)this.getMessageFn.get();
      if(Screen.isSelectAll(i)) {
         this.selectionPos = 0;
         this.cursorPos = var2.length();
         return true;
      } else if(Screen.isCopy(i)) {
         this.minecraft.keyboardHandler.setClipboard(this.getSelected());
         return true;
      } else if(Screen.isPaste(i)) {
         this.insertText(SharedConstants.filterText(ChatFormatting.stripFormatting(this.minecraft.keyboardHandler.getClipboard().replaceAll("\\r", ""))));
         this.selectionPos = this.cursorPos;
         return true;
      } else if(Screen.isCut(i)) {
         this.minecraft.keyboardHandler.setClipboard(this.getSelected());
         this.deleteSelection();
         return true;
      } else if(i == 259) {
         if(!var2.isEmpty()) {
            if(this.selectionPos != this.cursorPos) {
               this.deleteSelection();
            } else if(this.cursorPos > 0) {
               var2 = (new StringBuilder(var2)).deleteCharAt(Math.max(0, this.cursorPos - 1)).toString();
               this.selectionPos = this.cursorPos = Math.max(0, this.cursorPos - 1);
               this.setMessageFn.accept(var2);
            }
         }

         return true;
      } else if(i == 261) {
         if(!var2.isEmpty()) {
            if(this.selectionPos != this.cursorPos) {
               this.deleteSelection();
            } else if(this.cursorPos < var2.length()) {
               var2 = (new StringBuilder(var2)).deleteCharAt(Math.max(0, this.cursorPos)).toString();
               this.setMessageFn.accept(var2);
            }
         }

         return true;
      } else if(i == 263) {
         int var3 = this.font.isBidirectional()?1:-1;
         if(Screen.hasControlDown()) {
            this.cursorPos = this.font.getWordPosition(var2, var3, this.cursorPos, true);
         } else {
            this.cursorPos = Math.max(0, Math.min(var2.length(), this.cursorPos + var3));
         }

         if(!Screen.hasShiftDown()) {
            this.selectionPos = this.cursorPos;
         }

         return true;
      } else if(i == 262) {
         int var3 = this.font.isBidirectional()?-1:1;
         if(Screen.hasControlDown()) {
            this.cursorPos = this.font.getWordPosition(var2, var3, this.cursorPos, true);
         } else {
            this.cursorPos = Math.max(0, Math.min(var2.length(), this.cursorPos + var3));
         }

         if(!Screen.hasShiftDown()) {
            this.selectionPos = this.cursorPos;
         }

         return true;
      } else if(i == 268) {
         this.cursorPos = 0;
         if(!Screen.hasShiftDown()) {
            this.selectionPos = this.cursorPos;
         }

         return true;
      } else if(i == 269) {
         this.cursorPos = ((String)this.getMessageFn.get()).length();
         if(!Screen.hasShiftDown()) {
            this.selectionPos = this.cursorPos;
         }

         return true;
      } else {
         return false;
      }
   }

   private String getSelected() {
      String string = (String)this.getMessageFn.get();
      int var2 = Math.min(this.cursorPos, this.selectionPos);
      int var3 = Math.max(this.cursorPos, this.selectionPos);
      return string.substring(var2, var3);
   }

   private void deleteSelection() {
      if(this.selectionPos != this.cursorPos) {
         String var1 = (String)this.getMessageFn.get();
         int var2 = Math.min(this.cursorPos, this.selectionPos);
         int var3 = Math.max(this.cursorPos, this.selectionPos);
         String var4 = var1.substring(0, var2) + var1.substring(var3);
         this.cursorPos = var2;
         this.selectionPos = this.cursorPos;
         this.setMessageFn.accept(var4);
      }
   }

   public void setEnd() {
      this.selectionPos = this.cursorPos = ((String)this.getMessageFn.get()).length();
   }

   public int getCursorPos() {
      return this.cursorPos;
   }

   public int getSelectionPos() {
      return this.selectionPos;
   }
}
