package net.minecraft.client.renderer.block.model;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;

@ClientJarOnly
public class FaceBakery {
   private static final float RESCALE_22_5 = 1.0F / (float)Math.cos(0.39269909262657166D) - 1.0F;
   private static final float RESCALE_45 = 1.0F / (float)Math.cos(0.7853981852531433D) - 1.0F;
   private static final FaceBakery.Rotation[] BY_INDEX = new FaceBakery.Rotation[BlockModelRotation.values().length * Direction.values().length];
   private static final FaceBakery.Rotation ROT_0 = new FaceBakery.Rotation(null) {
      BlockFaceUV apply(float var1, float var2, float var3, float var4) {
         return new BlockFaceUV(new float[]{var1, var2, var3, var4}, 0);
      }
   };
   private static final FaceBakery.Rotation ROT_90 = new FaceBakery.Rotation(null) {
      BlockFaceUV apply(float var1, float var2, float var3, float var4) {
         return new BlockFaceUV(new float[]{var4, 16.0F - var1, var2, 16.0F - var3}, 270);
      }
   };
   private static final FaceBakery.Rotation ROT_180 = new FaceBakery.Rotation(null) {
      BlockFaceUV apply(float var1, float var2, float var3, float var4) {
         return new BlockFaceUV(new float[]{16.0F - var1, 16.0F - var2, 16.0F - var3, 16.0F - var4}, 0);
      }
   };
   private static final FaceBakery.Rotation ROT_270 = new FaceBakery.Rotation(null) {
      BlockFaceUV apply(float var1, float var2, float var3, float var4) {
         return new BlockFaceUV(new float[]{16.0F - var2, var3, 16.0F - var4, var1}, 90);
      }
   };

   public BakedQuad bakeQuad(Vector3f var1, Vector3f var2, BlockElementFace blockElementFace, TextureAtlasSprite textureAtlasSprite, Direction direction, ModelState modelState, @Nullable BlockElementRotation blockElementRotation, boolean var8) {
      BlockFaceUV var9 = blockElementFace.uv;
      if(modelState.isUvLocked()) {
         var9 = this.recomputeUVs(blockElementFace.uv, direction, modelState.getRotation());
      }

      float[] vars10 = new float[var9.uvs.length];
      System.arraycopy(var9.uvs, 0, vars10, 0, vars10.length);
      float var11 = (float)textureAtlasSprite.getWidth() / (textureAtlasSprite.getU1() - textureAtlasSprite.getU0());
      float var12 = (float)textureAtlasSprite.getHeight() / (textureAtlasSprite.getV1() - textureAtlasSprite.getV0());
      float var13 = 4.0F / Math.max(var12, var11);
      float var14 = (var9.uvs[0] + var9.uvs[0] + var9.uvs[2] + var9.uvs[2]) / 4.0F;
      float var15 = (var9.uvs[1] + var9.uvs[1] + var9.uvs[3] + var9.uvs[3]) / 4.0F;
      var9.uvs[0] = Mth.lerp(var13, var9.uvs[0], var14);
      var9.uvs[2] = Mth.lerp(var13, var9.uvs[2], var14);
      var9.uvs[1] = Mth.lerp(var13, var9.uvs[1], var15);
      var9.uvs[3] = Mth.lerp(var13, var9.uvs[3], var15);
      int[] vars16 = this.makeVertices(var9, textureAtlasSprite, direction, this.setupShape(var1, var2), modelState.getRotation(), blockElementRotation, var8);
      Direction var17 = calculateFacing(vars16);
      System.arraycopy(vars10, 0, var9.uvs, 0, vars10.length);
      if(blockElementRotation == null) {
         this.recalculateWinding(vars16, var17);
      }

      return new BakedQuad(vars16, blockElementFace.tintIndex, var17, textureAtlasSprite);
   }

   private BlockFaceUV recomputeUVs(BlockFaceUV var1, Direction direction, BlockModelRotation blockModelRotation) {
      return BY_INDEX[getIndex(blockModelRotation, direction)].recompute(var1);
   }

   private int[] makeVertices(BlockFaceUV blockFaceUV, TextureAtlasSprite textureAtlasSprite, Direction direction, float[] floats, BlockModelRotation blockModelRotation, @Nullable BlockElementRotation blockElementRotation, boolean var7) {
      int[] ints = new int[28];

      for(int var9 = 0; var9 < 4; ++var9) {
         this.bakeVertex(ints, var9, direction, blockFaceUV, floats, textureAtlasSprite, blockModelRotation, blockElementRotation, var7);
      }

      return ints;
   }

   private int getShadeValue(Direction direction) {
      float var2 = this.getShade(direction);
      int var3 = Mth.clamp((int)(var2 * 255.0F), 0, 255);
      return -16777216 | var3 << 16 | var3 << 8 | var3;
   }

   private float getShade(Direction direction) {
      switch(direction) {
      case DOWN:
         return 0.5F;
      case UP:
         return 1.0F;
      case NORTH:
      case SOUTH:
         return 0.8F;
      case WEST:
      case EAST:
         return 0.6F;
      default:
         return 1.0F;
      }
   }

   private float[] setupShape(Vector3f var1, Vector3f var2) {
      float[] floats = new float[Direction.values().length];
      floats[FaceInfo.Constants.MIN_X] = var1.x() / 16.0F;
      floats[FaceInfo.Constants.MIN_Y] = var1.y() / 16.0F;
      floats[FaceInfo.Constants.MIN_Z] = var1.z() / 16.0F;
      floats[FaceInfo.Constants.MAX_X] = var2.x() / 16.0F;
      floats[FaceInfo.Constants.MAX_Y] = var2.y() / 16.0F;
      floats[FaceInfo.Constants.MAX_Z] = var2.z() / 16.0F;
      return floats;
   }

   private void bakeVertex(int[] ints, int var2, Direction direction, BlockFaceUV blockFaceUV, float[] floats, TextureAtlasSprite textureAtlasSprite, BlockModelRotation blockModelRotation, @Nullable BlockElementRotation blockElementRotation, boolean var9) {
      Direction direction = blockModelRotation.rotate(direction);
      int var11 = var9?this.getShadeValue(direction):-1;
      FaceInfo.VertexInfo var12 = FaceInfo.fromFacing(direction).getVertexInfo(var2);
      Vector3f var13 = new Vector3f(floats[var12.xFace], floats[var12.yFace], floats[var12.zFace]);
      this.applyElementRotation(var13, blockElementRotation);
      int var14 = this.applyModelRotation(var13, direction, var2, blockModelRotation);
      this.fillVertex(ints, var14, var2, var13, var11, textureAtlasSprite, blockFaceUV);
   }

   private void fillVertex(int[] ints, int var2, int var3, Vector3f vector3f, int var5, TextureAtlasSprite textureAtlasSprite, BlockFaceUV blockFaceUV) {
      int var8 = var2 * 7;
      ints[var8] = Float.floatToRawIntBits(vector3f.x());
      ints[var8 + 1] = Float.floatToRawIntBits(vector3f.y());
      ints[var8 + 2] = Float.floatToRawIntBits(vector3f.z());
      ints[var8 + 3] = var5;
      ints[var8 + 4] = Float.floatToRawIntBits(textureAtlasSprite.getU((double)blockFaceUV.getU(var3)));
      ints[var8 + 4 + 1] = Float.floatToRawIntBits(textureAtlasSprite.getV((double)blockFaceUV.getV(var3)));
   }

   private void applyElementRotation(Vector3f vector3f, @Nullable BlockElementRotation blockElementRotation) {
      if(blockElementRotation != null) {
         Vector3f vector3f;
         Vector3f var4;
         switch(blockElementRotation.axis) {
         case X:
            vector3f = new Vector3f(1.0F, 0.0F, 0.0F);
            var4 = new Vector3f(0.0F, 1.0F, 1.0F);
            break;
         case Y:
            vector3f = new Vector3f(0.0F, 1.0F, 0.0F);
            var4 = new Vector3f(1.0F, 0.0F, 1.0F);
            break;
         case Z:
            vector3f = new Vector3f(0.0F, 0.0F, 1.0F);
            var4 = new Vector3f(1.0F, 1.0F, 0.0F);
            break;
         default:
            throw new IllegalArgumentException("There are only 3 axes");
         }

         Quaternion var5 = new Quaternion(vector3f, blockElementRotation.angle, true);
         if(blockElementRotation.rescale) {
            if(Math.abs(blockElementRotation.angle) == 22.5F) {
               var4.mul(RESCALE_22_5);
            } else {
               var4.mul(RESCALE_45);
            }

            var4.add(1.0F, 1.0F, 1.0F);
         } else {
            var4.set(1.0F, 1.0F, 1.0F);
         }

         this.rotateVertexBy(vector3f, new Vector3f(blockElementRotation.origin), var5, var4);
      }
   }

   public int applyModelRotation(Vector3f vector3f, Direction direction, int var3, BlockModelRotation blockModelRotation) {
      if(blockModelRotation == BlockModelRotation.X0_Y0) {
         return var3;
      } else {
         this.rotateVertexBy(vector3f, new Vector3f(0.5F, 0.5F, 0.5F), blockModelRotation.getRotationQuaternion(), new Vector3f(1.0F, 1.0F, 1.0F));
         return blockModelRotation.rotateVertexIndex(direction, var3);
      }
   }

   private void rotateVertexBy(Vector3f var1, Vector3f var2, Quaternion quaternion, Vector3f var4) {
      Vector4f var5 = new Vector4f(var1.x() - var2.x(), var1.y() - var2.y(), var1.z() - var2.z(), 1.0F);
      var5.transform(quaternion);
      var5.mul(var4);
      var1.set(var5.x() + var2.x(), var5.y() + var2.y(), var5.z() + var2.z());
   }

   public static Direction calculateFacing(int[] ints) {
      Vector3f var1 = new Vector3f(Float.intBitsToFloat(ints[0]), Float.intBitsToFloat(ints[1]), Float.intBitsToFloat(ints[2]));
      Vector3f var2 = new Vector3f(Float.intBitsToFloat(ints[7]), Float.intBitsToFloat(ints[8]), Float.intBitsToFloat(ints[9]));
      Vector3f var3 = new Vector3f(Float.intBitsToFloat(ints[14]), Float.intBitsToFloat(ints[15]), Float.intBitsToFloat(ints[16]));
      Vector3f var4 = new Vector3f(var1);
      var4.sub(var2);
      Vector3f var5 = new Vector3f(var3);
      var5.sub(var2);
      Vector3f var6 = new Vector3f(var5);
      var6.cross(var4);
      var6.normalize();
      Direction var7 = null;
      float var8 = 0.0F;

      for(Direction var12 : Direction.values()) {
         Vec3i var13 = var12.getNormal();
         Vector3f var14 = new Vector3f((float)var13.getX(), (float)var13.getY(), (float)var13.getZ());
         float var15 = var6.dot(var14);
         if(var15 >= 0.0F && var15 > var8) {
            var8 = var15;
            var7 = var12;
         }
      }

      if(var7 == null) {
         return Direction.UP;
      } else {
         return var7;
      }
   }

   private void recalculateWinding(int[] ints, Direction direction) {
      int[] ints = new int[ints.length];
      System.arraycopy(ints, 0, ints, 0, ints.length);
      float[] vars4 = new float[Direction.values().length];
      vars4[FaceInfo.Constants.MIN_X] = 999.0F;
      vars4[FaceInfo.Constants.MIN_Y] = 999.0F;
      vars4[FaceInfo.Constants.MIN_Z] = 999.0F;
      vars4[FaceInfo.Constants.MAX_X] = -999.0F;
      vars4[FaceInfo.Constants.MAX_Y] = -999.0F;
      vars4[FaceInfo.Constants.MAX_Z] = -999.0F;

      for(int var5 = 0; var5 < 4; ++var5) {
         int var6 = 7 * var5;
         float var7 = Float.intBitsToFloat(ints[var6]);
         float var8 = Float.intBitsToFloat(ints[var6 + 1]);
         float var9 = Float.intBitsToFloat(ints[var6 + 2]);
         if(var7 < vars4[FaceInfo.Constants.MIN_X]) {
            vars4[FaceInfo.Constants.MIN_X] = var7;
         }

         if(var8 < vars4[FaceInfo.Constants.MIN_Y]) {
            vars4[FaceInfo.Constants.MIN_Y] = var8;
         }

         if(var9 < vars4[FaceInfo.Constants.MIN_Z]) {
            vars4[FaceInfo.Constants.MIN_Z] = var9;
         }

         if(var7 > vars4[FaceInfo.Constants.MAX_X]) {
            vars4[FaceInfo.Constants.MAX_X] = var7;
         }

         if(var8 > vars4[FaceInfo.Constants.MAX_Y]) {
            vars4[FaceInfo.Constants.MAX_Y] = var8;
         }

         if(var9 > vars4[FaceInfo.Constants.MAX_Z]) {
            vars4[FaceInfo.Constants.MAX_Z] = var9;
         }
      }

      FaceInfo var5 = FaceInfo.fromFacing(direction);

      for(int var6 = 0; var6 < 4; ++var6) {
         int var7 = 7 * var6;
         FaceInfo.VertexInfo var8 = var5.getVertexInfo(var6);
         float var9 = vars4[var8.xFace];
         float var10 = vars4[var8.yFace];
         float var11 = vars4[var8.zFace];
         ints[var7] = Float.floatToRawIntBits(var9);
         ints[var7 + 1] = Float.floatToRawIntBits(var10);
         ints[var7 + 2] = Float.floatToRawIntBits(var11);

         for(int var12 = 0; var12 < 4; ++var12) {
            int var13 = 7 * var12;
            float var14 = Float.intBitsToFloat(ints[var13]);
            float var15 = Float.intBitsToFloat(ints[var13 + 1]);
            float var16 = Float.intBitsToFloat(ints[var13 + 2]);
            if(Mth.equal(var9, var14) && Mth.equal(var10, var15) && Mth.equal(var11, var16)) {
               ints[var7 + 4] = ints[var13 + 4];
               ints[var7 + 4 + 1] = ints[var13 + 4 + 1];
            }
         }
      }

   }

   private static void register(BlockModelRotation blockModelRotation, Direction direction, FaceBakery.Rotation faceBakery$Rotation) {
      BY_INDEX[getIndex(blockModelRotation, direction)] = faceBakery$Rotation;
   }

   private static int getIndex(BlockModelRotation blockModelRotation, Direction direction) {
      return BlockModelRotation.values().length * direction.ordinal() + blockModelRotation.ordinal();
   }

   static {
      register(BlockModelRotation.X0_Y0, Direction.DOWN, ROT_0);
      register(BlockModelRotation.X0_Y0, Direction.EAST, ROT_0);
      register(BlockModelRotation.X0_Y0, Direction.NORTH, ROT_0);
      register(BlockModelRotation.X0_Y0, Direction.SOUTH, ROT_0);
      register(BlockModelRotation.X0_Y0, Direction.UP, ROT_0);
      register(BlockModelRotation.X0_Y0, Direction.WEST, ROT_0);
      register(BlockModelRotation.X0_Y90, Direction.EAST, ROT_0);
      register(BlockModelRotation.X0_Y90, Direction.NORTH, ROT_0);
      register(BlockModelRotation.X0_Y90, Direction.SOUTH, ROT_0);
      register(BlockModelRotation.X0_Y90, Direction.WEST, ROT_0);
      register(BlockModelRotation.X0_Y180, Direction.EAST, ROT_0);
      register(BlockModelRotation.X0_Y180, Direction.NORTH, ROT_0);
      register(BlockModelRotation.X0_Y180, Direction.SOUTH, ROT_0);
      register(BlockModelRotation.X0_Y180, Direction.WEST, ROT_0);
      register(BlockModelRotation.X0_Y270, Direction.EAST, ROT_0);
      register(BlockModelRotation.X0_Y270, Direction.NORTH, ROT_0);
      register(BlockModelRotation.X0_Y270, Direction.SOUTH, ROT_0);
      register(BlockModelRotation.X0_Y270, Direction.WEST, ROT_0);
      register(BlockModelRotation.X90_Y0, Direction.DOWN, ROT_0);
      register(BlockModelRotation.X90_Y0, Direction.SOUTH, ROT_0);
      register(BlockModelRotation.X90_Y90, Direction.DOWN, ROT_0);
      register(BlockModelRotation.X90_Y180, Direction.DOWN, ROT_0);
      register(BlockModelRotation.X90_Y180, Direction.NORTH, ROT_0);
      register(BlockModelRotation.X90_Y270, Direction.DOWN, ROT_0);
      register(BlockModelRotation.X180_Y0, Direction.DOWN, ROT_0);
      register(BlockModelRotation.X180_Y0, Direction.UP, ROT_0);
      register(BlockModelRotation.X270_Y0, Direction.SOUTH, ROT_0);
      register(BlockModelRotation.X270_Y0, Direction.UP, ROT_0);
      register(BlockModelRotation.X270_Y90, Direction.UP, ROT_0);
      register(BlockModelRotation.X270_Y180, Direction.NORTH, ROT_0);
      register(BlockModelRotation.X270_Y180, Direction.UP, ROT_0);
      register(BlockModelRotation.X270_Y270, Direction.UP, ROT_0);
      register(BlockModelRotation.X0_Y270, Direction.UP, ROT_90);
      register(BlockModelRotation.X0_Y90, Direction.DOWN, ROT_90);
      register(BlockModelRotation.X90_Y0, Direction.WEST, ROT_90);
      register(BlockModelRotation.X90_Y90, Direction.WEST, ROT_90);
      register(BlockModelRotation.X90_Y180, Direction.WEST, ROT_90);
      register(BlockModelRotation.X90_Y270, Direction.NORTH, ROT_90);
      register(BlockModelRotation.X90_Y270, Direction.SOUTH, ROT_90);
      register(BlockModelRotation.X90_Y270, Direction.WEST, ROT_90);
      register(BlockModelRotation.X180_Y90, Direction.UP, ROT_90);
      register(BlockModelRotation.X180_Y270, Direction.DOWN, ROT_90);
      register(BlockModelRotation.X270_Y0, Direction.EAST, ROT_90);
      register(BlockModelRotation.X270_Y90, Direction.EAST, ROT_90);
      register(BlockModelRotation.X270_Y90, Direction.NORTH, ROT_90);
      register(BlockModelRotation.X270_Y90, Direction.SOUTH, ROT_90);
      register(BlockModelRotation.X270_Y180, Direction.EAST, ROT_90);
      register(BlockModelRotation.X270_Y270, Direction.EAST, ROT_90);
      register(BlockModelRotation.X0_Y180, Direction.DOWN, ROT_180);
      register(BlockModelRotation.X0_Y180, Direction.UP, ROT_180);
      register(BlockModelRotation.X90_Y0, Direction.NORTH, ROT_180);
      register(BlockModelRotation.X90_Y0, Direction.UP, ROT_180);
      register(BlockModelRotation.X90_Y90, Direction.UP, ROT_180);
      register(BlockModelRotation.X90_Y180, Direction.SOUTH, ROT_180);
      register(BlockModelRotation.X90_Y180, Direction.UP, ROT_180);
      register(BlockModelRotation.X90_Y270, Direction.UP, ROT_180);
      register(BlockModelRotation.X180_Y0, Direction.EAST, ROT_180);
      register(BlockModelRotation.X180_Y0, Direction.NORTH, ROT_180);
      register(BlockModelRotation.X180_Y0, Direction.SOUTH, ROT_180);
      register(BlockModelRotation.X180_Y0, Direction.WEST, ROT_180);
      register(BlockModelRotation.X180_Y90, Direction.EAST, ROT_180);
      register(BlockModelRotation.X180_Y90, Direction.NORTH, ROT_180);
      register(BlockModelRotation.X180_Y90, Direction.SOUTH, ROT_180);
      register(BlockModelRotation.X180_Y90, Direction.WEST, ROT_180);
      register(BlockModelRotation.X180_Y180, Direction.DOWN, ROT_180);
      register(BlockModelRotation.X180_Y180, Direction.EAST, ROT_180);
      register(BlockModelRotation.X180_Y180, Direction.NORTH, ROT_180);
      register(BlockModelRotation.X180_Y180, Direction.SOUTH, ROT_180);
      register(BlockModelRotation.X180_Y180, Direction.UP, ROT_180);
      register(BlockModelRotation.X180_Y180, Direction.WEST, ROT_180);
      register(BlockModelRotation.X180_Y270, Direction.EAST, ROT_180);
      register(BlockModelRotation.X180_Y270, Direction.NORTH, ROT_180);
      register(BlockModelRotation.X180_Y270, Direction.SOUTH, ROT_180);
      register(BlockModelRotation.X180_Y270, Direction.WEST, ROT_180);
      register(BlockModelRotation.X270_Y0, Direction.DOWN, ROT_180);
      register(BlockModelRotation.X270_Y0, Direction.NORTH, ROT_180);
      register(BlockModelRotation.X270_Y90, Direction.DOWN, ROT_180);
      register(BlockModelRotation.X270_Y180, Direction.DOWN, ROT_180);
      register(BlockModelRotation.X270_Y180, Direction.SOUTH, ROT_180);
      register(BlockModelRotation.X270_Y270, Direction.DOWN, ROT_180);
      register(BlockModelRotation.X0_Y90, Direction.UP, ROT_270);
      register(BlockModelRotation.X0_Y270, Direction.DOWN, ROT_270);
      register(BlockModelRotation.X90_Y0, Direction.EAST, ROT_270);
      register(BlockModelRotation.X90_Y90, Direction.EAST, ROT_270);
      register(BlockModelRotation.X90_Y90, Direction.NORTH, ROT_270);
      register(BlockModelRotation.X90_Y90, Direction.SOUTH, ROT_270);
      register(BlockModelRotation.X90_Y180, Direction.EAST, ROT_270);
      register(BlockModelRotation.X90_Y270, Direction.EAST, ROT_270);
      register(BlockModelRotation.X270_Y0, Direction.WEST, ROT_270);
      register(BlockModelRotation.X180_Y90, Direction.DOWN, ROT_270);
      register(BlockModelRotation.X180_Y270, Direction.UP, ROT_270);
      register(BlockModelRotation.X270_Y90, Direction.WEST, ROT_270);
      register(BlockModelRotation.X270_Y180, Direction.WEST, ROT_270);
      register(BlockModelRotation.X270_Y270, Direction.NORTH, ROT_270);
      register(BlockModelRotation.X270_Y270, Direction.SOUTH, ROT_270);
      register(BlockModelRotation.X270_Y270, Direction.WEST, ROT_270);
   }

   @ClientJarOnly
   abstract static class Rotation {
      private Rotation() {
      }

      public BlockFaceUV recompute(BlockFaceUV blockFaceUV) {
         float var2 = blockFaceUV.getU(blockFaceUV.getReverseIndex(0));
         float var3 = blockFaceUV.getV(blockFaceUV.getReverseIndex(0));
         float var4 = blockFaceUV.getU(blockFaceUV.getReverseIndex(2));
         float var5 = blockFaceUV.getV(blockFaceUV.getReverseIndex(2));
         return this.apply(var2, var3, var4, var5);
      }

      abstract BlockFaceUV apply(float var1, float var2, float var3, float var4);
   }
}
