package net.minecraft.client.renderer.block.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.math.Vector3f;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

@ClientJarOnly
public class ItemModelGenerator {
   public static final List LAYERS = Lists.newArrayList(new String[]{"layer0", "layer1", "layer2", "layer3", "layer4"});

   public BlockModel generateBlockModel(Function function, BlockModel var2) {
      Map<String, String> var3 = Maps.newHashMap();
      List<BlockElement> var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < LAYERS.size(); ++var5) {
         String var6 = (String)LAYERS.get(var5);
         if(!var2.hasTexture(var6)) {
            break;
         }

         String var7 = var2.getTexture(var6);
         var3.put(var6, var7);
         TextureAtlasSprite var8 = (TextureAtlasSprite)function.apply(new ResourceLocation(var7));
         var4.addAll(this.processFrames(var5, var6, var8));
      }

      var3.put("particle", var2.hasTexture("particle")?var2.getTexture("particle"):(String)var3.get("layer0"));
      BlockModel var5 = new BlockModel((ResourceLocation)null, var4, var3, false, false, var2.getTransforms(), var2.getOverrides());
      var5.name = var2.name;
      return var5;
   }

   private List processFrames(int var1, String string, TextureAtlasSprite textureAtlasSprite) {
      Map<Direction, BlockElementFace> var4 = Maps.newHashMap();
      var4.put(Direction.SOUTH, new BlockElementFace((Direction)null, var1, string, new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
      var4.put(Direction.NORTH, new BlockElementFace((Direction)null, var1, string, new BlockFaceUV(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
      List<BlockElement> var5 = Lists.newArrayList();
      var5.add(new BlockElement(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), var4, (BlockElementRotation)null, true));
      var5.addAll(this.createSideElements(textureAtlasSprite, string, var1));
      return var5;
   }

   private List createSideElements(TextureAtlasSprite textureAtlasSprite, String string, int var3) {
      float var4 = (float)textureAtlasSprite.getWidth();
      float var5 = (float)textureAtlasSprite.getHeight();
      List<BlockElement> var6 = Lists.newArrayList();

      for(ItemModelGenerator.Span var8 : this.getSpans(textureAtlasSprite)) {
         float var9 = 0.0F;
         float var10 = 0.0F;
         float var11 = 0.0F;
         float var12 = 0.0F;
         float var13 = 0.0F;
         float var14 = 0.0F;
         float var15 = 0.0F;
         float var16 = 0.0F;
         float var17 = 16.0F / var4;
         float var18 = 16.0F / var5;
         float var19 = (float)var8.getMin();
         float var20 = (float)var8.getMax();
         float var21 = (float)var8.getAnchor();
         ItemModelGenerator.SpanFacing var22 = var8.getFacing();
         switch(var22) {
         case UP:
            var13 = var19;
            var9 = var19;
            var11 = var14 = var20 + 1.0F;
            var15 = var21;
            var10 = var21;
            var12 = var21;
            var16 = var21 + 1.0F;
            break;
         case DOWN:
            var15 = var21;
            var16 = var21 + 1.0F;
            var13 = var19;
            var9 = var19;
            var11 = var14 = var20 + 1.0F;
            var10 = var21 + 1.0F;
            var12 = var21 + 1.0F;
            break;
         case LEFT:
            var13 = var21;
            var9 = var21;
            var11 = var21;
            var14 = var21 + 1.0F;
            var16 = var19;
            var10 = var19;
            var12 = var15 = var20 + 1.0F;
            break;
         case RIGHT:
            var13 = var21;
            var14 = var21 + 1.0F;
            var9 = var21 + 1.0F;
            var11 = var21 + 1.0F;
            var16 = var19;
            var10 = var19;
            var12 = var15 = var20 + 1.0F;
         }

         var9 = var9 * var17;
         var11 = var11 * var17;
         var10 = var10 * var18;
         var12 = var12 * var18;
         var10 = 16.0F - var10;
         var12 = 16.0F - var12;
         var13 = var13 * var17;
         var14 = var14 * var17;
         var15 = var15 * var18;
         var16 = var16 * var18;
         Map<Direction, BlockElementFace> var23 = Maps.newHashMap();
         var23.put(var22.getDirection(), new BlockElementFace((Direction)null, var3, string, new BlockFaceUV(new float[]{var13, var15, var14, var16}, 0)));
         switch(var22) {
         case UP:
            var6.add(new BlockElement(new Vector3f(var9, var10, 7.5F), new Vector3f(var11, var10, 8.5F), var23, (BlockElementRotation)null, true));
            break;
         case DOWN:
            var6.add(new BlockElement(new Vector3f(var9, var12, 7.5F), new Vector3f(var11, var12, 8.5F), var23, (BlockElementRotation)null, true));
            break;
         case LEFT:
            var6.add(new BlockElement(new Vector3f(var9, var10, 7.5F), new Vector3f(var9, var12, 8.5F), var23, (BlockElementRotation)null, true));
            break;
         case RIGHT:
            var6.add(new BlockElement(new Vector3f(var11, var10, 7.5F), new Vector3f(var11, var12, 8.5F), var23, (BlockElementRotation)null, true));
         }
      }

      return var6;
   }

   private List getSpans(TextureAtlasSprite textureAtlasSprite) {
      int var2 = textureAtlasSprite.getWidth();
      int var3 = textureAtlasSprite.getHeight();
      List<ItemModelGenerator.Span> var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < textureAtlasSprite.getFrameCount(); ++var5) {
         for(int var6 = 0; var6 < var3; ++var6) {
            for(int var7 = 0; var7 < var2; ++var7) {
               boolean var8 = !this.isTransparent(textureAtlasSprite, var5, var7, var6, var2, var3);
               this.checkTransition(ItemModelGenerator.SpanFacing.UP, var4, textureAtlasSprite, var5, var7, var6, var2, var3, var8);
               this.checkTransition(ItemModelGenerator.SpanFacing.DOWN, var4, textureAtlasSprite, var5, var7, var6, var2, var3, var8);
               this.checkTransition(ItemModelGenerator.SpanFacing.LEFT, var4, textureAtlasSprite, var5, var7, var6, var2, var3, var8);
               this.checkTransition(ItemModelGenerator.SpanFacing.RIGHT, var4, textureAtlasSprite, var5, var7, var6, var2, var3, var8);
            }
         }
      }

      return var4;
   }

   private void checkTransition(ItemModelGenerator.SpanFacing itemModelGenerator$SpanFacing, List list, TextureAtlasSprite textureAtlasSprite, int var4, int var5, int var6, int var7, int var8, boolean var9) {
      boolean var10 = this.isTransparent(textureAtlasSprite, var4, var5 + itemModelGenerator$SpanFacing.getXOffset(), var6 + itemModelGenerator$SpanFacing.getYOffset(), var7, var8) && var9;
      if(var10) {
         this.createOrExpandSpan(list, itemModelGenerator$SpanFacing, var5, var6);
      }

   }

   private void createOrExpandSpan(List list, ItemModelGenerator.SpanFacing itemModelGenerator$SpanFacing, int var3, int var4) {
      ItemModelGenerator.Span var5 = null;

      for(ItemModelGenerator.Span var7 : list) {
         if(var7.getFacing() == itemModelGenerator$SpanFacing) {
            int var8 = itemModelGenerator$SpanFacing.isHorizontal()?var4:var3;
            if(var7.getAnchor() == var8) {
               var5 = var7;
               break;
            }
         }
      }

      int var6 = itemModelGenerator$SpanFacing.isHorizontal()?var4:var3;
      int var7 = itemModelGenerator$SpanFacing.isHorizontal()?var3:var4;
      if(var5 == null) {
         list.add(new ItemModelGenerator.Span(itemModelGenerator$SpanFacing, var7, var6));
      } else {
         var5.expand(var7);
      }

   }

   private boolean isTransparent(TextureAtlasSprite textureAtlasSprite, int var2, int var3, int var4, int var5, int var6) {
      return var3 >= 0 && var4 >= 0 && var3 < var5 && var4 < var6?textureAtlasSprite.isTransparent(var2, var3, var4):true;
   }

   @ClientJarOnly
   static class Span {
      private final ItemModelGenerator.SpanFacing facing;
      private int min;
      private int max;
      private final int anchor;

      public Span(ItemModelGenerator.SpanFacing facing, int min, int anchor) {
         this.facing = facing;
         this.min = min;
         this.max = min;
         this.anchor = anchor;
      }

      public void expand(int min) {
         if(min < this.min) {
            this.min = min;
         } else if(min > this.max) {
            this.max = min;
         }

      }

      public ItemModelGenerator.SpanFacing getFacing() {
         return this.facing;
      }

      public int getMin() {
         return this.min;
      }

      public int getMax() {
         return this.max;
      }

      public int getAnchor() {
         return this.anchor;
      }
   }

   @ClientJarOnly
   static enum SpanFacing {
      UP(Direction.UP, 0, -1),
      DOWN(Direction.DOWN, 0, 1),
      LEFT(Direction.EAST, -1, 0),
      RIGHT(Direction.WEST, 1, 0);

      private final Direction direction;
      private final int xOffset;
      private final int yOffset;

      private SpanFacing(Direction direction, int xOffset, int yOffset) {
         this.direction = direction;
         this.xOffset = xOffset;
         this.yOffset = yOffset;
      }

      public Direction getDirection() {
         return this.direction;
      }

      public int getXOffset() {
         return this.xOffset;
      }

      public int getYOffset() {
         return this.yOffset;
      }

      private boolean isHorizontal() {
         return this == DOWN || this == UP;
      }
   }
}
