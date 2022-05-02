package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

@ClientJarOnly
public class KeyMapping implements Comparable {
   private static final Map ALL = Maps.newHashMap();
   private static final Map MAP = Maps.newHashMap();
   private static final Set CATEGORIES = Sets.newHashSet();
   private static final Map CATEGORY_SORT_ORDER = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put("key.categories.movement", Integer.valueOf(1));
      hashMap.put("key.categories.gameplay", Integer.valueOf(2));
      hashMap.put("key.categories.inventory", Integer.valueOf(3));
      hashMap.put("key.categories.creative", Integer.valueOf(4));
      hashMap.put("key.categories.multiplayer", Integer.valueOf(5));
      hashMap.put("key.categories.ui", Integer.valueOf(6));
      hashMap.put("key.categories.misc", Integer.valueOf(7));
   });
   private final String name;
   private final InputConstants.Key defaultKey;
   private final String category;
   private InputConstants.Key key;
   private boolean isDown;
   private int clickCount;

   public static void click(InputConstants.Key inputConstants$Key) {
      KeyMapping var1 = (KeyMapping)MAP.get(inputConstants$Key);
      if(var1 != null) {
         ++var1.clickCount;
      }

   }

   public static void set(InputConstants.Key inputConstants$Key, boolean isDown) {
      KeyMapping var2 = (KeyMapping)MAP.get(inputConstants$Key);
      if(var2 != null) {
         var2.isDown = isDown;
      }

   }

   public static void setAll() {
      for(KeyMapping var1 : ALL.values()) {
         if(var1.key.getType() == InputConstants.Type.KEYSYM && var1.key.getValue() != InputConstants.UNKNOWN.getValue()) {
            var1.isDown = InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), var1.key.getValue());
         }
      }

   }

   public static void releaseAll() {
      for(KeyMapping var1 : ALL.values()) {
         var1.release();
      }

   }

   public static void resetMapping() {
      MAP.clear();

      for(KeyMapping var1 : ALL.values()) {
         MAP.put(var1.key, var1);
      }

   }

   public KeyMapping(String var1, int var2, String var3) {
      this(var1, InputConstants.Type.KEYSYM, var2, var3);
   }

   public KeyMapping(String name, InputConstants.Type inputConstants$Type, int var3, String category) {
      this.name = name;
      this.key = inputConstants$Type.getOrCreate(var3);
      this.defaultKey = this.key;
      this.category = category;
      ALL.put(name, this);
      MAP.put(this.key, this);
      CATEGORIES.add(category);
   }

   public boolean isDown() {
      return this.isDown;
   }

   public String getCategory() {
      return this.category;
   }

   public boolean consumeClick() {
      if(this.clickCount == 0) {
         return false;
      } else {
         --this.clickCount;
         return true;
      }
   }

   private void release() {
      this.clickCount = 0;
      this.isDown = false;
   }

   public String getName() {
      return this.name;
   }

   public InputConstants.Key getDefaultKey() {
      return this.defaultKey;
   }

   public void setKey(InputConstants.Key key) {
      this.key = key;
   }

   public int compareTo(KeyMapping keyMapping) {
      return this.category.equals(keyMapping.category)?I18n.get(this.name, new Object[0]).compareTo(I18n.get(keyMapping.name, new Object[0])):((Integer)CATEGORY_SORT_ORDER.get(this.category)).compareTo((Integer)CATEGORY_SORT_ORDER.get(keyMapping.category));
   }

   public static Supplier createNameSupplier(String string) {
      KeyMapping var1 = (KeyMapping)ALL.get(string);
      return var1 == null?() -> {
         return string;
      }:var1::getTranslatedKeyMessage;
   }

   public boolean same(KeyMapping keyMapping) {
      return this.key.equals(keyMapping.key);
   }

   public boolean isUnbound() {
      return this.key.equals(InputConstants.UNKNOWN);
   }

   public boolean matches(int var1, int var2) {
      return var1 == InputConstants.UNKNOWN.getValue()?this.key.getType() == InputConstants.Type.SCANCODE && this.key.getValue() == var2:this.key.getType() == InputConstants.Type.KEYSYM && this.key.getValue() == var1;
   }

   public boolean matchesMouse(int i) {
      return this.key.getType() == InputConstants.Type.MOUSE && this.key.getValue() == i;
   }

   public String getTranslatedKeyMessage() {
      String string = this.key.getName();
      int var2 = this.key.getValue();
      String var3 = null;
      switch(this.key.getType()) {
      case KEYSYM:
         var3 = InputConstants.translateKeyCode(var2);
         break;
      case SCANCODE:
         var3 = InputConstants.translateScanCode(var2);
         break;
      case MOUSE:
         String var4 = I18n.get(string, new Object[0]);
         var3 = Objects.equals(var4, string)?I18n.get(InputConstants.Type.MOUSE.getDefaultPrefix(), new Object[]{Integer.valueOf(var2 + 1)}):var4;
      }

      return var3 == null?I18n.get(string, new Object[0]):var3;
   }

   public boolean isDefault() {
      return this.key.equals(this.defaultKey);
   }

   public String saveString() {
      return this.key.getName();
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((KeyMapping)var1);
   }
}
