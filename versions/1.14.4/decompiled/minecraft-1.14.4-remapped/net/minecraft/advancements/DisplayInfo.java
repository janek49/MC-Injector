package net.minecraft.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.advancements.FrameType;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DisplayInfo {
   private final Component title;
   private final Component description;
   private final ItemStack icon;
   private final ResourceLocation background;
   private final FrameType frame;
   private final boolean showToast;
   private final boolean announceChat;
   private final boolean hidden;
   private float x;
   private float y;

   public DisplayInfo(ItemStack icon, Component title, Component description, @Nullable ResourceLocation background, FrameType frame, boolean showToast, boolean announceChat, boolean hidden) {
      this.title = title;
      this.description = description;
      this.icon = icon;
      this.background = background;
      this.frame = frame;
      this.showToast = showToast;
      this.announceChat = announceChat;
      this.hidden = hidden;
   }

   public void setLocation(float x, float y) {
      this.x = x;
      this.y = y;
   }

   public Component getTitle() {
      return this.title;
   }

   public Component getDescription() {
      return this.description;
   }

   public ItemStack getIcon() {
      return this.icon;
   }

   @Nullable
   public ResourceLocation getBackground() {
      return this.background;
   }

   public FrameType getFrame() {
      return this.frame;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public boolean shouldShowToast() {
      return this.showToast;
   }

   public boolean shouldAnnounceChat() {
      return this.announceChat;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public static DisplayInfo fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      Component var2 = (Component)GsonHelper.getAsObject(jsonObject, "title", jsonDeserializationContext, Component.class);
      Component var3 = (Component)GsonHelper.getAsObject(jsonObject, "description", jsonDeserializationContext, Component.class);
      if(var2 != null && var3 != null) {
         ItemStack var4 = getIcon(GsonHelper.getAsJsonObject(jsonObject, "icon"));
         ResourceLocation var5 = jsonObject.has("background")?new ResourceLocation(GsonHelper.getAsString(jsonObject, "background")):null;
         FrameType var6 = jsonObject.has("frame")?FrameType.byName(GsonHelper.getAsString(jsonObject, "frame")):FrameType.TASK;
         boolean var7 = GsonHelper.getAsBoolean(jsonObject, "show_toast", true);
         boolean var8 = GsonHelper.getAsBoolean(jsonObject, "announce_to_chat", true);
         boolean var9 = GsonHelper.getAsBoolean(jsonObject, "hidden", false);
         return new DisplayInfo(var4, var2, var3, var5, var6, var7, var8, var9);
      } else {
         throw new JsonSyntaxException("Both title and description must be set");
      }
   }

   private static ItemStack getIcon(JsonObject jsonObject) {
      if(!jsonObject.has("item")) {
         throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add \'item\' key)");
      } else {
         Item var1 = GsonHelper.getAsItem(jsonObject, "item");
         if(jsonObject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
         } else {
            ItemStack var2 = new ItemStack(var1);
            if(jsonObject.has("nbt")) {
               try {
                  CompoundTag var3 = TagParser.parseTag(GsonHelper.convertToString(jsonObject.get("nbt"), "nbt"));
                  var2.setTag(var3);
               } catch (CommandSyntaxException var4) {
                  throw new JsonSyntaxException("Invalid nbt tag: " + var4.getMessage());
               }
            }

            return var2;
         }
      }
   }

   public void serializeToNetwork(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.writeComponent(this.title);
      friendlyByteBuf.writeComponent(this.description);
      friendlyByteBuf.writeItem(this.icon);
      friendlyByteBuf.writeEnum(this.frame);
      int var2 = 0;
      if(this.background != null) {
         var2 |= 1;
      }

      if(this.showToast) {
         var2 |= 2;
      }

      if(this.hidden) {
         var2 |= 4;
      }

      friendlyByteBuf.writeInt(var2);
      if(this.background != null) {
         friendlyByteBuf.writeResourceLocation(this.background);
      }

      friendlyByteBuf.writeFloat(this.x);
      friendlyByteBuf.writeFloat(this.y);
   }

   public static DisplayInfo fromNetwork(FriendlyByteBuf network) {
      Component var1 = network.readComponent();
      Component var2 = network.readComponent();
      ItemStack var3 = network.readItem();
      FrameType var4 = (FrameType)network.readEnum(FrameType.class);
      int var5 = network.readInt();
      ResourceLocation var6 = (var5 & 1) != 0?network.readResourceLocation():null;
      boolean var7 = (var5 & 2) != 0;
      boolean var8 = (var5 & 4) != 0;
      DisplayInfo var9 = new DisplayInfo(var3, var1, var2, var6, var4, var7, false, var8);
      var9.setLocation(network.readFloat(), network.readFloat());
      return var9;
   }

   public JsonElement serializeToJson() {
      JsonObject var1 = new JsonObject();
      var1.add("icon", this.serializeIcon());
      var1.add("title", Component.Serializer.toJsonTree(this.title));
      var1.add("description", Component.Serializer.toJsonTree(this.description));
      var1.addProperty("frame", this.frame.getName());
      var1.addProperty("show_toast", Boolean.valueOf(this.showToast));
      var1.addProperty("announce_to_chat", Boolean.valueOf(this.announceChat));
      var1.addProperty("hidden", Boolean.valueOf(this.hidden));
      if(this.background != null) {
         var1.addProperty("background", this.background.toString());
      }

      return var1;
   }

   private JsonObject serializeIcon() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("item", Registry.ITEM.getKey(this.icon.getItem()).toString());
      if(this.icon.hasTag()) {
         jsonObject.addProperty("nbt", this.icon.getTag().toString());
      }

      return jsonObject;
   }
}
