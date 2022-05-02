package net.minecraft.client.renderer.block.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.block.model.ItemTransform;

@ClientJarOnly
public class ItemTransforms {
   public static final ItemTransforms NO_TRANSFORMS = new ItemTransforms();
   public static float transX;
   public static float transY;
   public static float transZ;
   public static float rotX;
   public static float rotY;
   public static float rotZ;
   public static float scaleX;
   public static float scaleY;
   public static float scaleZ;
   public final ItemTransform thirdPersonLeftHand;
   public final ItemTransform thirdPersonRightHand;
   public final ItemTransform firstPersonLeftHand;
   public final ItemTransform firstPersonRightHand;
   public final ItemTransform head;
   public final ItemTransform gui;
   public final ItemTransform ground;
   public final ItemTransform fixed;

   private ItemTransforms() {
      this(ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM);
   }

   public ItemTransforms(ItemTransforms itemTransforms) {
      this.thirdPersonLeftHand = itemTransforms.thirdPersonLeftHand;
      this.thirdPersonRightHand = itemTransforms.thirdPersonRightHand;
      this.firstPersonLeftHand = itemTransforms.firstPersonLeftHand;
      this.firstPersonRightHand = itemTransforms.firstPersonRightHand;
      this.head = itemTransforms.head;
      this.gui = itemTransforms.gui;
      this.ground = itemTransforms.ground;
      this.fixed = itemTransforms.fixed;
   }

   public ItemTransforms(ItemTransform thirdPersonLeftHand, ItemTransform thirdPersonRightHand, ItemTransform firstPersonLeftHand, ItemTransform firstPersonRightHand, ItemTransform head, ItemTransform gui, ItemTransform ground, ItemTransform fixed) {
      this.thirdPersonLeftHand = thirdPersonLeftHand;
      this.thirdPersonRightHand = thirdPersonRightHand;
      this.firstPersonLeftHand = firstPersonLeftHand;
      this.firstPersonRightHand = firstPersonRightHand;
      this.head = head;
      this.gui = gui;
      this.ground = ground;
      this.fixed = fixed;
   }

   public void apply(ItemTransforms.TransformType itemTransforms$TransformType) {
      apply(this.getTransform(itemTransforms$TransformType), false);
   }

   public static void apply(ItemTransform itemTransform, boolean var1) {
      if(itemTransform != ItemTransform.NO_TRANSFORM) {
         int var2 = var1?-1:1;
         GlStateManager.translatef((float)var2 * (transX + itemTransform.translation.x()), transY + itemTransform.translation.y(), transZ + itemTransform.translation.z());
         float var3 = rotX + itemTransform.rotation.x();
         float var4 = rotY + itemTransform.rotation.y();
         float var5 = rotZ + itemTransform.rotation.z();
         if(var1) {
            var4 = -var4;
            var5 = -var5;
         }

         GlStateManager.multMatrix(new Matrix4f(new Quaternion(var3, var4, var5, true)));
         GlStateManager.scalef(scaleX + itemTransform.scale.x(), scaleY + itemTransform.scale.y(), scaleZ + itemTransform.scale.z());
      }
   }

   public ItemTransform getTransform(ItemTransforms.TransformType itemTransforms$TransformType) {
      switch(itemTransforms$TransformType) {
      case THIRD_PERSON_LEFT_HAND:
         return this.thirdPersonLeftHand;
      case THIRD_PERSON_RIGHT_HAND:
         return this.thirdPersonRightHand;
      case FIRST_PERSON_LEFT_HAND:
         return this.firstPersonLeftHand;
      case FIRST_PERSON_RIGHT_HAND:
         return this.firstPersonRightHand;
      case HEAD:
         return this.head;
      case GUI:
         return this.gui;
      case GROUND:
         return this.ground;
      case FIXED:
         return this.fixed;
      default:
         return ItemTransform.NO_TRANSFORM;
      }
   }

   public boolean hasTransform(ItemTransforms.TransformType itemTransforms$TransformType) {
      return this.getTransform(itemTransforms$TransformType) != ItemTransform.NO_TRANSFORM;
   }

   @ClientJarOnly
   public static class Deserializer implements JsonDeserializer {
      public ItemTransforms deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = jsonElement.getAsJsonObject();
         ItemTransform var5 = this.getTransform(jsonDeserializationContext, var4, "thirdperson_righthand");
         ItemTransform var6 = this.getTransform(jsonDeserializationContext, var4, "thirdperson_lefthand");
         if(var6 == ItemTransform.NO_TRANSFORM) {
            var6 = var5;
         }

         ItemTransform var7 = this.getTransform(jsonDeserializationContext, var4, "firstperson_righthand");
         ItemTransform var8 = this.getTransform(jsonDeserializationContext, var4, "firstperson_lefthand");
         if(var8 == ItemTransform.NO_TRANSFORM) {
            var8 = var7;
         }

         ItemTransform var9 = this.getTransform(jsonDeserializationContext, var4, "head");
         ItemTransform var10 = this.getTransform(jsonDeserializationContext, var4, "gui");
         ItemTransform var11 = this.getTransform(jsonDeserializationContext, var4, "ground");
         ItemTransform var12 = this.getTransform(jsonDeserializationContext, var4, "fixed");
         return new ItemTransforms(var6, var5, var8, var7, var9, var10, var11, var12);
      }

      private ItemTransform getTransform(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject, String string) {
         return jsonObject.has(string)?(ItemTransform)jsonDeserializationContext.deserialize(jsonObject.get(string), ItemTransform.class):ItemTransform.NO_TRANSFORM;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }

   @ClientJarOnly
   public static enum TransformType {
      NONE,
      THIRD_PERSON_LEFT_HAND,
      THIRD_PERSON_RIGHT_HAND,
      FIRST_PERSON_LEFT_HAND,
      FIRST_PERSON_RIGHT_HAND,
      HEAD,
      GUI,
      GROUND,
      FIXED;
   }
}
