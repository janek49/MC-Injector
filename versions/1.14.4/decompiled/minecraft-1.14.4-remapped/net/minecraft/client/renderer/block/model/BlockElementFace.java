package net.minecraft.client.renderer.block.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;

@ClientJarOnly
public class BlockElementFace {
   public final Direction cullForDirection;
   public final int tintIndex;
   public final String texture;
   public final BlockFaceUV uv;

   public BlockElementFace(@Nullable Direction cullForDirection, int tintIndex, String texture, BlockFaceUV uv) {
      this.cullForDirection = cullForDirection;
      this.tintIndex = tintIndex;
      this.texture = texture;
      this.uv = uv;
   }

   @ClientJarOnly
   public static class Deserializer implements JsonDeserializer {
      public BlockElementFace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = jsonElement.getAsJsonObject();
         Direction var5 = this.getCullFacing(var4);
         int var6 = this.getTintIndex(var4);
         String var7 = this.getTexture(var4);
         BlockFaceUV var8 = (BlockFaceUV)jsonDeserializationContext.deserialize(var4, BlockFaceUV.class);
         return new BlockElementFace(var5, var6, var7, var8);
      }

      protected int getTintIndex(JsonObject jsonObject) {
         return GsonHelper.getAsInt(jsonObject, "tintindex", -1);
      }

      private String getTexture(JsonObject jsonObject) {
         return GsonHelper.getAsString(jsonObject, "texture");
      }

      @Nullable
      private Direction getCullFacing(JsonObject jsonObject) {
         String var2 = GsonHelper.getAsString(jsonObject, "cullface", "");
         return Direction.byName(var2);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
