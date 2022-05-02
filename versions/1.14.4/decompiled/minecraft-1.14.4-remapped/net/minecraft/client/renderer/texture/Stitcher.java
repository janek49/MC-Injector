package net.minecraft.client.renderer.texture;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.renderer.texture.StitcherException;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;

@ClientJarOnly
public class Stitcher {
   private static final Comparator HOLDER_COMPARATOR = Comparator.comparing((stitcher$Holder) -> {
      return Integer.valueOf(-stitcher$Holder.height);
   }).thenComparing((stitcher$Holder) -> {
      return Integer.valueOf(-stitcher$Holder.width);
   }).thenComparing((stitcher$Holder) -> {
      return stitcher$Holder.sprite.getName();
   });
   private final int mipLevel;
   private final Set texturesToBeStitched = Sets.newHashSetWithExpectedSize(256);
   private final List storage = Lists.newArrayListWithCapacity(256);
   private int storageX;
   private int storageY;
   private final int maxWidth;
   private final int maxHeight;

   public Stitcher(int maxWidth, int maxHeight, int mipLevel) {
      this.mipLevel = mipLevel;
      this.maxWidth = maxWidth;
      this.maxHeight = maxHeight;
   }

   public int getWidth() {
      return this.storageX;
   }

   public int getHeight() {
      return this.storageY;
   }

   public void registerSprite(TextureAtlasSprite textureAtlasSprite) {
      Stitcher.Holder var2 = new Stitcher.Holder(textureAtlasSprite, this.mipLevel);
      this.texturesToBeStitched.add(var2);
   }

   public void stitch() {
      List<Stitcher.Holder> var1 = Lists.newArrayList(this.texturesToBeStitched);
      var1.sort(HOLDER_COMPARATOR);

      for(Stitcher.Holder var3 : var1) {
         if(!this.addToStorage(var3)) {
            throw new StitcherException(var3.sprite, (Collection)var1.stream().map((stitcher$Holder) -> {
               return stitcher$Holder.sprite;
            }).collect(ImmutableList.toImmutableList()));
         }
      }

      this.storageX = Mth.smallestEncompassingPowerOfTwo(this.storageX);
      this.storageY = Mth.smallestEncompassingPowerOfTwo(this.storageY);
   }

   public List gatherSprites() {
      List<TextureAtlasSprite> list = Lists.newArrayList();

      for(Stitcher.Region var3 : this.storage) {
         var3.walk((stitcher$Region) -> {
            Stitcher.Holder var3 = stitcher$Region.getHolder();
            TextureAtlasSprite var4 = var3.sprite;
            var4.init(this.storageX, this.storageY, stitcher$Region.getX(), stitcher$Region.getY());
            list.add(var4);
         });
      }

      return list;
   }

   private static int smallestFittingMinTexel(int var0, int var1) {
      return (var0 >> var1) + ((var0 & (1 << var1) - 1) == 0?0:1) << var1;
   }

   private boolean addToStorage(Stitcher.Holder stitcher$Holder) {
      for(Stitcher.Region var3 : this.storage) {
         if(var3.add(stitcher$Holder)) {
            return true;
         }
      }

      return this.expand(stitcher$Holder);
   }

   private boolean expand(Stitcher.Holder stitcher$Holder) {
      int var3 = Mth.smallestEncompassingPowerOfTwo(this.storageX);
      int var4 = Mth.smallestEncompassingPowerOfTwo(this.storageY);
      int var5 = Mth.smallestEncompassingPowerOfTwo(this.storageX + stitcher$Holder.width);
      int var6 = Mth.smallestEncompassingPowerOfTwo(this.storageY + stitcher$Holder.height);
      boolean var7 = var5 <= this.maxWidth;
      boolean var8 = var6 <= this.maxHeight;
      if(!var7 && !var8) {
         return false;
      } else {
         boolean var9 = var7 && var3 != var5;
         boolean var10 = var8 && var4 != var6;
         boolean var2;
         if(var9 ^ var10) {
            var2 = var9;
         } else {
            var2 = var7 && var3 <= var4;
         }

         Stitcher.Region var11;
         if(var2) {
            if(this.storageY == 0) {
               this.storageY = stitcher$Holder.height;
            }

            var11 = new Stitcher.Region(this.storageX, 0, stitcher$Holder.width, this.storageY);
            this.storageX += stitcher$Holder.width;
         } else {
            var11 = new Stitcher.Region(0, this.storageY, this.storageX, stitcher$Holder.height);
            this.storageY += stitcher$Holder.height;
         }

         var11.add(stitcher$Holder);
         this.storage.add(var11);
         return true;
      }
   }

   @ClientJarOnly
   static class Holder {
      public final TextureAtlasSprite sprite;
      public final int width;
      public final int height;

      public Holder(TextureAtlasSprite sprite, int var2) {
         this.sprite = sprite;
         this.width = Stitcher.smallestFittingMinTexel(sprite.getWidth(), var2);
         this.height = Stitcher.smallestFittingMinTexel(sprite.getHeight(), var2);
      }

      public String toString() {
         return "Holder{width=" + this.width + ", height=" + this.height + '}';
      }
   }

   @ClientJarOnly
   public static class Region {
      private final int originX;
      private final int originY;
      private final int width;
      private final int height;
      private List subSlots;
      private Stitcher.Holder holder;

      public Region(int originX, int originY, int width, int height) {
         this.originX = originX;
         this.originY = originY;
         this.width = width;
         this.height = height;
      }

      public Stitcher.Holder getHolder() {
         return this.holder;
      }

      public int getX() {
         return this.originX;
      }

      public int getY() {
         return this.originY;
      }

      public boolean add(Stitcher.Holder holder) {
         if(this.holder != null) {
            return false;
         } else {
            int var2 = holder.width;
            int var3 = holder.height;
            if(var2 <= this.width && var3 <= this.height) {
               if(var2 == this.width && var3 == this.height) {
                  this.holder = holder;
                  return true;
               } else {
                  if(this.subSlots == null) {
                     this.subSlots = Lists.newArrayListWithCapacity(1);
                     this.subSlots.add(new Stitcher.Region(this.originX, this.originY, var2, var3));
                     int var4 = this.width - var2;
                     int var5 = this.height - var3;
                     if(var5 > 0 && var4 > 0) {
                        int var6 = Math.max(this.height, var4);
                        int var7 = Math.max(this.width, var5);
                        if(var6 >= var7) {
                           this.subSlots.add(new Stitcher.Region(this.originX, this.originY + var3, var2, var5));
                           this.subSlots.add(new Stitcher.Region(this.originX + var2, this.originY, var4, this.height));
                        } else {
                           this.subSlots.add(new Stitcher.Region(this.originX + var2, this.originY, var4, var3));
                           this.subSlots.add(new Stitcher.Region(this.originX, this.originY + var3, this.width, var5));
                        }
                     } else if(var4 == 0) {
                        this.subSlots.add(new Stitcher.Region(this.originX, this.originY + var3, var2, var5));
                     } else if(var5 == 0) {
                        this.subSlots.add(new Stitcher.Region(this.originX + var2, this.originY, var4, var3));
                     }
                  }

                  for(Stitcher.Region var5 : this.subSlots) {
                     if(var5.add(holder)) {
                        return true;
                     }
                  }

                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public void walk(Consumer consumer) {
         if(this.holder != null) {
            consumer.accept(this);
         } else if(this.subSlots != null) {
            for(Stitcher.Region var3 : this.subSlots) {
               var3.walk(consumer);
            }
         }

      }

      public String toString() {
         return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + '}';
      }
   }
}
