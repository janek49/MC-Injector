package net.minecraft.advancements;

import net.minecraft.ChatFormatting;

public enum FrameType {
   TASK("task", 0, ChatFormatting.GREEN),
   CHALLENGE("challenge", 26, ChatFormatting.DARK_PURPLE),
   GOAL("goal", 52, ChatFormatting.GREEN);

   private final String name;
   private final int texture;
   private final ChatFormatting chatColor;

   private FrameType(String name, int texture, ChatFormatting chatColor) {
      this.name = name;
      this.texture = texture;
      this.chatColor = chatColor;
   }

   public String getName() {
      return this.name;
   }

   public int getTexture() {
      return this.texture;
   }

   public static FrameType byName(String name) {
      for(FrameType var4 : values()) {
         if(var4.name.equals(name)) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Unknown frame type \'" + name + "\'");
   }

   public ChatFormatting getChatColor() {
      return this.chatColor;
   }
}
