package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class NbtPredicate {
   public static final NbtPredicate ANY = new NbtPredicate((CompoundTag)null);
   @Nullable
   private final CompoundTag tag;

   public NbtPredicate(@Nullable CompoundTag tag) {
      this.tag = tag;
   }

   public boolean matches(ItemStack itemStack) {
      return this == ANY?true:this.matches((Tag)itemStack.getTag());
   }

   public boolean matches(Entity entity) {
      return this == ANY?true:this.matches((Tag)getEntityTagToCompare(entity));
   }

   public boolean matches(@Nullable Tag tag) {
      return tag == null?this == ANY:this.tag == null || NbtUtils.compareNbt(this.tag, tag, true);
   }

   public JsonElement serializeToJson() {
      return (JsonElement)(this != ANY && this.tag != null?new JsonPrimitive(this.tag.toString()):JsonNull.INSTANCE);
   }

   public static NbtPredicate fromJson(@Nullable JsonElement json) {
      if(json != null && !json.isJsonNull()) {
         CompoundTag var1;
         try {
            var1 = TagParser.parseTag(GsonHelper.convertToString(json, "nbt"));
         } catch (CommandSyntaxException var3) {
            throw new JsonSyntaxException("Invalid nbt tag: " + var3.getMessage());
         }

         return new NbtPredicate(var1);
      } else {
         return ANY;
      }
   }

   public static CompoundTag getEntityTagToCompare(Entity entity) {
      CompoundTag compoundTag = entity.saveWithoutId(new CompoundTag());
      if(entity instanceof Player) {
         ItemStack var2 = ((Player)entity).inventory.getSelected();
         if(!var2.isEmpty()) {
            compoundTag.put("SelectedItem", var2.save(new CompoundTag()));
         }
      }

      return compoundTag;
   }
}
