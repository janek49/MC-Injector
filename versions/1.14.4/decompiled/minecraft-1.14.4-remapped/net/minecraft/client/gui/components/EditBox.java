package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;

@ClientJarOnly
public class EditBox extends AbstractWidget implements Widget, GuiEventListener {
   private final Font font;
   private String value;
   private int maxLength;
   private int frame;
   private boolean bordered;
   private boolean canLoseFocus;
   private boolean isEditable;
   private boolean shiftPressed;
   private int displayPos;
   private int cursorPos;
   private int highlightPos;
   private int textColor;
   private int textColorUneditable;
   private String suggestion;
   private Consumer responder;
   private Predicate filter;
   private BiFunction formatter;

   public EditBox(Font font, int var2, int var3, int var4, int var5, String string) {
      this(font, var2, var3, var4, var5, (EditBox)null, string);
   }

   public EditBox(Font font, int var2, int var3, int var4, int var5, @Nullable EditBox editBox, String string) {
      super(var2, var3, var4, var5, string);
      this.value = "";
      this.maxLength = 32;
      this.bordered = true;
      this.canLoseFocus = true;
      this.isEditable = true;
      this.textColor = 14737632;
      this.textColorUneditable = 7368816;
      this.filter = Predicates.alwaysTrue();
      this.formatter = (var0, integer) -> {
         return var0;
      };
      this.font = font;
      if(editBox != null) {
         this.setValue(editBox.getValue());
      }

   }

   public void setResponder(Consumer responder) {
      this.responder = responder;
   }

   public void setFormatter(BiFunction formatter) {
      this.formatter = formatter;
   }

   public void tick() {
      ++this.frame;
   }

   protected String getNarrationMessage() {
      String string = this.getMessage();
      return string.isEmpty()?"":I18n.get("gui.narrate.editBox", new Object[]{string, this.value});
   }

   public void setValue(String value) {
      if(this.filter.test(value)) {
         if(value.length() > this.maxLength) {
            this.value = value.substring(0, this.maxLength);
         } else {
            this.value = value;
         }

         this.moveCursorToEnd();
         this.setHighlightPos(this.cursorPos);
         this.onValueChange(value);
      }
   }

   public String getValue() {
      return this.value;
   }

   public String getHighlighted() {
      int var1 = this.cursorPos < this.highlightPos?this.cursorPos:this.highlightPos;
      int var2 = this.cursorPos < this.highlightPos?this.highlightPos:this.cursorPos;
      return this.value.substring(var1, var2);
   }

   public void setFilter(Predicate filter) {
      this.filter = filter;
   }

   public void insertText(String string) {
      String string = "";
      String var3 = SharedConstants.filterText(string);
      int var4 = this.cursorPos < this.highlightPos?this.cursorPos:this.highlightPos;
      int var5 = this.cursorPos < this.highlightPos?this.highlightPos:this.cursorPos;
      int var6 = this.maxLength - this.value.length() - (var4 - var5);
      if(!this.value.isEmpty()) {
         string = string + this.value.substring(0, var4);
      }

      int var7;
      if(var6 < var3.length()) {
         string = string + var3.substring(0, var6);
         var7 = var6;
      } else {
         string = string + var3;
         var7 = var3.length();
      }

      if(!this.value.isEmpty() && var5 < this.value.length()) {
         string = string + this.value.substring(var5);
      }

      if(this.filter.test(string)) {
         this.value = string;
         this.setCursorPosition(var4 + var7);
         this.setHighlightPos(this.cursorPos);
         this.onValueChange(this.value);
      }
   }

   private void onValueChange(String string) {
      if(this.responder != null) {
         this.responder.accept(string);
      }

      this.nextNarration = Util.getMillis() + 500L;
   }

   private void deleteText(int i) {
      if(Screen.hasControlDown()) {
         this.deleteWords(i);
      } else {
         this.deleteChars(i);
      }

   }

   public void deleteWords(int i) {
      if(!this.value.isEmpty()) {
         if(this.highlightPos != this.cursorPos) {
            this.insertText("");
         } else {
            this.deleteChars(this.getWordPosition(i) - this.cursorPos);
         }
      }
   }

   public void deleteChars(int i) {
      if(!this.value.isEmpty()) {
         if(this.highlightPos != this.cursorPos) {
            this.insertText("");
         } else {
            boolean var2 = i < 0;
            int var3 = var2?this.cursorPos + i:this.cursorPos;
            int var4 = var2?this.cursorPos:this.cursorPos + i;
            String var5 = "";
            if(var3 >= 0) {
               var5 = this.value.substring(0, var3);
            }

            if(var4 < this.value.length()) {
               var5 = var5 + this.value.substring(var4);
            }

            if(this.filter.test(var5)) {
               this.value = var5;
               if(var2) {
                  this.moveCursor(i);
               }

               this.onValueChange(this.value);
            }
         }
      }
   }

   public int getWordPosition(int i) {
      return this.getWordPosition(i, this.getCursorPosition());
   }

   private int getWordPosition(int var1, int var2) {
      return this.getWordPosition(var1, var2, true);
   }

   private int getWordPosition(int var1, int var2, boolean var3) {
      int var4 = var2;
      boolean var5 = var1 < 0;
      int var6 = Math.abs(var1);

      for(int var7 = 0; var7 < var6; ++var7) {
         if(!var5) {
            int var8 = this.value.length();
            var4 = this.value.indexOf(32, var4);
            if(var4 == -1) {
               var4 = var8;
            } else {
               while(var3 && var4 < var8 && this.value.charAt(var4) == 32) {
                  ++var4;
               }
            }
         } else {
            while(var3 && var4 > 0 && this.value.charAt(var4 - 1) == 32) {
               --var4;
            }

            while(var4 > 0 && this.value.charAt(var4 - 1) != 32) {
               --var4;
            }
         }
      }

      return var4;
   }

   public void moveCursor(int i) {
      this.moveCursorTo(this.cursorPos + i);
   }

   public void moveCursorTo(int cursorPosition) {
      this.setCursorPosition(cursorPosition);
      if(!this.shiftPressed) {
         this.setHighlightPos(this.cursorPos);
      }

      this.onValueChange(this.value);
   }

   public void setCursorPosition(int cursorPosition) {
      this.cursorPos = Mth.clamp(cursorPosition, 0, this.value.length());
   }

   public void moveCursorToStart() {
      this.moveCursorTo(0);
   }

   public void moveCursorToEnd() {
      this.moveCursorTo(this.value.length());
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(!this.canConsumeInput()) {
         return false;
      } else {
         this.shiftPressed = Screen.hasShiftDown();
         if(Screen.isSelectAll(var1)) {
            this.moveCursorToEnd();
            this.setHighlightPos(0);
            return true;
         } else if(Screen.isCopy(var1)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            return true;
         } else if(Screen.isPaste(var1)) {
            if(this.isEditable) {
               this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            }

            return true;
         } else if(Screen.isCut(var1)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            if(this.isEditable) {
               this.insertText("");
            }

            return true;
         } else {
            switch(var1) {
            case 259:
               if(this.isEditable) {
                  this.shiftPressed = false;
                  this.deleteText(-1);
                  this.shiftPressed = Screen.hasShiftDown();
               }

               return true;
            case 260:
            case 264:
            case 265:
            case 266:
            case 267:
            default:
               return false;
            case 261:
               if(this.isEditable) {
                  this.shiftPressed = false;
                  this.deleteText(1);
                  this.shiftPressed = Screen.hasShiftDown();
               }

               return true;
            case 262:
               if(Screen.hasControlDown()) {
                  this.moveCursorTo(this.getWordPosition(1));
               } else {
                  this.moveCursor(1);
               }

               return true;
            case 263:
               if(Screen.hasControlDown()) {
                  this.moveCursorTo(this.getWordPosition(-1));
               } else {
                  this.moveCursor(-1);
               }

               return true;
            case 268:
               this.moveCursorToStart();
               return true;
            case 269:
               this.moveCursorToEnd();
               return true;
            }
         }
      }
   }

   public boolean canConsumeInput() {
      return this.isVisible() && this.isFocused() && this.isEditable();
   }

   public boolean charTyped(char var1, int var2) {
      if(!this.canConsumeInput()) {
         return false;
      } else if(SharedConstants.isAllowedChatCharacter(var1)) {
         if(this.isEditable) {
            this.insertText(Character.toString(var1));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if(!this.isVisible()) {
         return false;
      } else {
         boolean var6 = var1 >= (double)this.x && var1 < (double)(this.x + this.width) && var3 >= (double)this.y && var3 < (double)(this.y + this.height);
         if(this.canLoseFocus) {
            this.setFocus(var6);
         }

         if(this.isFocused() && var6 && var5 == 0) {
            int var7 = Mth.floor(var1) - this.x;
            if(this.bordered) {
               var7 -= 4;
            }

            String var8 = this.font.substrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            this.moveCursorTo(this.font.substrByWidth(var8, var7).length() + this.displayPos);
            return true;
         } else {
            return false;
         }
      }
   }

   public void setFocus(boolean focus) {
      super.setFocused(focus);
   }

   public void renderButton(int var1, int var2, float var3) {
      if(this.isVisible()) {
         if(this.isBordered()) {
            fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
            fill(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
         }

         int var4 = this.isEditable?this.textColor:this.textColorUneditable;
         int var5 = this.cursorPos - this.displayPos;
         int var6 = this.highlightPos - this.displayPos;
         String var7 = this.font.substrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
         boolean var8 = var5 >= 0 && var5 <= var7.length();
         boolean var9 = this.isFocused() && this.frame / 6 % 2 == 0 && var8;
         int var10 = this.bordered?this.x + 4:this.x;
         int var11 = this.bordered?this.y + (this.height - 8) / 2:this.y;
         int var12 = var10;
         if(var6 > var7.length()) {
            var6 = var7.length();
         }

         if(!var7.isEmpty()) {
            String var13 = var8?var7.substring(0, var5):var7;
            var12 = this.font.drawShadow((String)this.formatter.apply(var13, Integer.valueOf(this.displayPos)), (float)var10, (float)var11, var4);
         }

         boolean var13 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
         int var14 = var12;
         if(!var8) {
            var14 = var5 > 0?var10 + this.width:var10;
         } else if(var13) {
            var14 = var12 - 1;
            --var12;
         }

         if(!var7.isEmpty() && var8 && var5 < var7.length()) {
            this.font.drawShadow((String)this.formatter.apply(var7.substring(var5), Integer.valueOf(this.cursorPos)), (float)var12, (float)var11, var4);
         }

         if(!var13 && this.suggestion != null) {
            this.font.drawShadow(this.suggestion, (float)(var14 - 1), (float)var11, -8355712);
         }

         if(var9) {
            if(var13) {
               int var10001 = var11 - 1;
               int var10002 = var14 + 1;
               int var10003 = var11 + 1;
               this.font.getClass();
               GuiComponent.fill(var14, var10001, var10002, var10003 + 9, -3092272);
            } else {
               this.font.drawShadow("_", (float)var14, (float)var11, var4);
            }
         }

         if(var6 != var5) {
            int var15 = var10 + this.font.width(var7.substring(0, var6));
            int var17 = var11 - 1;
            int var18 = var15 - 1;
            int var10004 = var11 + 1;
            this.font.getClass();
            this.renderHighlight(var14, var17, var18, var10004 + 9);
         }

      }
   }

   private void renderHighlight(int var1, int var2, int var3, int var4) {
      if(var1 < var3) {
         int var5 = var1;
         var1 = var3;
         var3 = var5;
      }

      if(var2 < var4) {
         int var5 = var2;
         var2 = var4;
         var4 = var5;
      }

      if(var3 > this.x + this.width) {
         var3 = this.x + this.width;
      }

      if(var1 > this.x + this.width) {
         var1 = this.x + this.width;
      }

      Tesselator var5 = Tesselator.getInstance();
      BufferBuilder var6 = var5.getBuilder();
      GlStateManager.color4f(0.0F, 0.0F, 255.0F, 255.0F);
      GlStateManager.disableTexture();
      GlStateManager.enableColorLogicOp();
      GlStateManager.logicOp(GlStateManager.LogicOp.OR_REVERSE);
      var6.begin(7, DefaultVertexFormat.POSITION);
      var6.vertex((double)var1, (double)var4, 0.0D).endVertex();
      var6.vertex((double)var3, (double)var4, 0.0D).endVertex();
      var6.vertex((double)var3, (double)var2, 0.0D).endVertex();
      var6.vertex((double)var1, (double)var2, 0.0D).endVertex();
      var5.end();
      GlStateManager.disableColorLogicOp();
      GlStateManager.enableTexture();
   }

   public void setMaxLength(int maxLength) {
      this.maxLength = maxLength;
      if(this.value.length() > maxLength) {
         this.value = this.value.substring(0, maxLength);
         this.onValueChange(this.value);
      }

   }

   private int getMaxLength() {
      return this.maxLength;
   }

   public int getCursorPosition() {
      return this.cursorPos;
   }

   private boolean isBordered() {
      return this.bordered;
   }

   public void setBordered(boolean bordered) {
      this.bordered = bordered;
   }

   public void setTextColor(int textColor) {
      this.textColor = textColor;
   }

   public void setTextColorUneditable(int textColorUneditable) {
      this.textColorUneditable = textColorUneditable;
   }

   public boolean changeFocus(boolean b) {
      return this.visible && this.isEditable?super.changeFocus(b):false;
   }

   public boolean isMouseOver(double var1, double var3) {
      return this.visible && var1 >= (double)this.x && var1 < (double)(this.x + this.width) && var3 >= (double)this.y && var3 < (double)(this.y + this.height);
   }

   protected void onFocusedChanged(boolean b) {
      if(b) {
         this.frame = 0;
      }

   }

   private boolean isEditable() {
      return this.isEditable;
   }

   public void setEditable(boolean editable) {
      this.isEditable = editable;
   }

   public int getInnerWidth() {
      return this.isBordered()?this.width - 8:this.width;
   }

   public void setHighlightPos(int highlightPos) {
      int var2 = this.value.length();
      this.highlightPos = Mth.clamp(highlightPos, 0, var2);
      if(this.font != null) {
         if(this.displayPos > var2) {
            this.displayPos = var2;
         }

         int var3 = this.getInnerWidth();
         String var4 = this.font.substrByWidth(this.value.substring(this.displayPos), var3);
         int var5 = var4.length() + this.displayPos;
         if(this.highlightPos == this.displayPos) {
            this.displayPos -= this.font.substrByWidth(this.value, var3, true).length();
         }

         if(this.highlightPos > var5) {
            this.displayPos += this.highlightPos - var5;
         } else if(this.highlightPos <= this.displayPos) {
            this.displayPos -= this.displayPos - this.highlightPos;
         }

         this.displayPos = Mth.clamp(this.displayPos, 0, var2);
      }

   }

   public void setCanLoseFocus(boolean canLoseFocus) {
      this.canLoseFocus = canLoseFocus;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public void setSuggestion(@Nullable String suggestion) {
      this.suggestion = suggestion;
   }

   public int getScreenX(int i) {
      return i > this.value.length()?this.x:this.x + this.font.width(this.value.substring(0, i));
   }

   public void setX(int x) {
      this.x = x;
   }
}
