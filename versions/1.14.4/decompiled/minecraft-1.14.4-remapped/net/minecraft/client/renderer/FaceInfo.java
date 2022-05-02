package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.core.Direction;

@ClientJarOnly
public enum FaceInfo {
   DOWN(new FaceInfo.VertexInfo[]{new FaceInfo.VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MAX_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MIN_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MIN_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MAX_Z)}),
   UP(new FaceInfo.VertexInfo[]{new FaceInfo.VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MIN_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MAX_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MAX_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MIN_Z)}),
   NORTH(new FaceInfo.VertexInfo[]{new FaceInfo.VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MIN_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MIN_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MIN_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MIN_Z)}),
   SOUTH(new FaceInfo.VertexInfo[]{new FaceInfo.VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MAX_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MAX_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MAX_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MAX_Z)}),
   WEST(new FaceInfo.VertexInfo[]{new FaceInfo.VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MIN_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MIN_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MAX_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MAX_Z)}),
   EAST(new FaceInfo.VertexInfo[]{new FaceInfo.VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MAX_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MAX_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MIN_Z), new FaceInfo.VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MIN_Z)});

   private static final FaceInfo[] BY_FACING = (FaceInfo[])Util.make(new FaceInfo[6], (faceInfos) -> {
      faceInfos[FaceInfo.Constants.MIN_Y] = DOWN;
      faceInfos[FaceInfo.Constants.MAX_Y] = UP;
      faceInfos[FaceInfo.Constants.MIN_Z] = NORTH;
      faceInfos[FaceInfo.Constants.MAX_Z] = SOUTH;
      faceInfos[FaceInfo.Constants.MIN_X] = WEST;
      faceInfos[FaceInfo.Constants.MAX_X] = EAST;
   });
   private final FaceInfo.VertexInfo[] infos;

   public static FaceInfo fromFacing(Direction facing) {
      return BY_FACING[facing.get3DDataValue()];
   }

   private FaceInfo(FaceInfo.VertexInfo... infos) {
      this.infos = infos;
   }

   public FaceInfo.VertexInfo getVertexInfo(int i) {
      return this.infos[i];
   }

   @ClientJarOnly
   public static final class Constants {
      public static final int MAX_Z = Direction.SOUTH.get3DDataValue();
      public static final int MAX_Y = Direction.UP.get3DDataValue();
      public static final int MAX_X = Direction.EAST.get3DDataValue();
      public static final int MIN_Z = Direction.NORTH.get3DDataValue();
      public static final int MIN_Y = Direction.DOWN.get3DDataValue();
      public static final int MIN_X = Direction.WEST.get3DDataValue();
   }

   @ClientJarOnly
   public static class VertexInfo {
      public final int xFace;
      public final int yFace;
      public final int zFace;

      private VertexInfo(int xFace, int yFace, int zFace) {
         this.xFace = xFace;
         this.yFace = yFace;
         this.zFace = zFace;
      }
   }
}
