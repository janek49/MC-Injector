package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ComplexItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapItem extends ComplexItem {
   public MapItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public static ItemStack create(Level level, int var1, int var2, byte var3, boolean var4, boolean var5) {
      ItemStack itemStack = new ItemStack(Items.FILLED_MAP);
      createAndStoreSavedData(itemStack, level, var1, var2, var3, var4, var5, level.dimension.getType());
      return itemStack;
   }

   @Nullable
   public static MapItemSavedData getSavedData(ItemStack itemStack, Level level) {
      return level.getMapData(makeKey(getMapId(itemStack)));
   }

   @Nullable
   public static MapItemSavedData getOrCreateSavedData(ItemStack itemStack, Level level) {
      MapItemSavedData mapItemSavedData = getSavedData(itemStack, level);
      if(mapItemSavedData == null && !level.isClientSide) {
         mapItemSavedData = createAndStoreSavedData(itemStack, level, level.getLevelData().getXSpawn(), level.getLevelData().getZSpawn(), 3, false, false, level.dimension.getType());
      }

      return mapItemSavedData;
   }

   public static int getMapId(ItemStack itemStack) {
      CompoundTag var1 = itemStack.getTag();
      return var1 != null && var1.contains("map", 99)?var1.getInt("map"):0;
   }

   private static MapItemSavedData createAndStoreSavedData(ItemStack itemStack, Level level, int var2, int var3, int var4, boolean var5, boolean var6, DimensionType dimensionType) {
      int var8 = level.getFreeMapId();
      MapItemSavedData var9 = new MapItemSavedData(makeKey(var8));
      var9.setProperties(var2, var3, var4, var5, var6, dimensionType);
      level.setMapData(var9);
      itemStack.getOrCreateTag().putInt("map", var8);
      return var9;
   }

   public static String makeKey(int i) {
      return "map_" + i;
   }

   public void update(Level level, Entity entity, MapItemSavedData mapItemSavedData) {
      if(level.dimension.getType() == mapItemSavedData.dimension && entity instanceof Player) {
         int var4 = 1 << mapItemSavedData.scale;
         int var5 = mapItemSavedData.x;
         int var6 = mapItemSavedData.z;
         int var7 = Mth.floor(entity.x - (double)var5) / var4 + 64;
         int var8 = Mth.floor(entity.z - (double)var6) / var4 + 64;
         int var9 = 128 / var4;
         if(level.dimension.isHasCeiling()) {
            var9 /= 2;
         }

         MapItemSavedData.HoldingPlayer var10 = mapItemSavedData.getHoldingPlayer((Player)entity);
         ++var10.step;
         boolean var11 = false;

         for(int var12 = var7 - var9 + 1; var12 < var7 + var9; ++var12) {
            if((var12 & 15) == (var10.step & 15) || var11) {
               var11 = false;
               double var13 = 0.0D;

               for(int var15 = var8 - var9 - 1; var15 < var8 + var9; ++var15) {
                  if(var12 >= 0 && var15 >= -1 && var12 < 128 && var15 < 128) {
                     int var16 = var12 - var7;
                     int var17 = var15 - var8;
                     boolean var18 = var16 * var16 + var17 * var17 > (var9 - 2) * (var9 - 2);
                     int var19 = (var5 / var4 + var12 - 64) * var4;
                     int var20 = (var6 / var4 + var15 - 64) * var4;
                     Multiset<MaterialColor> var21 = LinkedHashMultiset.create();
                     LevelChunk var22 = level.getChunkAt(new BlockPos(var19, 0, var20));
                     if(!var22.isEmpty()) {
                        ChunkPos var23 = var22.getPos();
                        int var24 = var19 & 15;
                        int var25 = var20 & 15;
                        int var26 = 0;
                        double var27 = 0.0D;
                        if(level.dimension.isHasCeiling()) {
                           int var29 = var19 + var20 * 231871;
                           var29 = var29 * var29 * 31287121 + var29 * 11;
                           if((var29 >> 20 & 1) == 0) {
                              var21.add(Blocks.DIRT.defaultBlockState().getMapColor(level, BlockPos.ZERO), 10);
                           } else {
                              var21.add(Blocks.STONE.defaultBlockState().getMapColor(level, BlockPos.ZERO), 100);
                           }

                           var27 = 100.0D;
                        } else {
                           BlockPos.MutableBlockPos var29 = new BlockPos.MutableBlockPos();
                           BlockPos.MutableBlockPos var30 = new BlockPos.MutableBlockPos();

                           for(int var31 = 0; var31 < var4; ++var31) {
                              for(int var32 = 0; var32 < var4; ++var32) {
                                 int var33 = var22.getHeight(Heightmap.Types.WORLD_SURFACE, var31 + var24, var32 + var25) + 1;
                                 BlockState var34;
                                 if(var33 <= 1) {
                                    var34 = Blocks.BEDROCK.defaultBlockState();
                                 } else {
                                    while(true) {
                                       --var33;
                                       var29.set(var23.getMinBlockX() + var31 + var24, var33, var23.getMinBlockZ() + var32 + var25);
                                       var34 = var22.getBlockState(var29);
                                       if(var34.getMapColor(level, var29) != MaterialColor.NONE || var33 <= 0) {
                                          break;
                                       }
                                    }

                                    if(var33 > 0 && !var34.getFluidState().isEmpty()) {
                                       int var35 = var33 - 1;
                                       var30.set((Vec3i)var29);

                                       while(true) {
                                          var30.setY(var35--);
                                          BlockState var36 = var22.getBlockState(var30);
                                          ++var26;
                                          if(var35 <= 0 || var36.getFluidState().isEmpty()) {
                                             break;
                                          }
                                       }

                                       var34 = this.getCorrectStateForFluidBlock(level, var34, var29);
                                    }
                                 }

                                 mapItemSavedData.checkBanners(level, var23.getMinBlockX() + var31 + var24, var23.getMinBlockZ() + var32 + var25);
                                 var27 += (double)var33 / (double)(var4 * var4);
                                 var21.add(var34.getMapColor(level, var29));
                              }
                           }
                        }

                        var26 = var26 / (var4 * var4);
                        double var29 = (var27 - var13) * 4.0D / (double)(var4 + 4) + ((double)(var12 + var15 & 1) - 0.5D) * 0.4D;
                        int var31 = 1;
                        if(var29 > 0.6D) {
                           var31 = 2;
                        }

                        if(var29 < -0.6D) {
                           var31 = 0;
                        }

                        MaterialColor var32 = (MaterialColor)Iterables.getFirst(Multisets.copyHighestCountFirst(var21), MaterialColor.NONE);
                        if(var32 == MaterialColor.WATER) {
                           var29 = (double)var26 * 0.1D + (double)(var12 + var15 & 1) * 0.2D;
                           var31 = 1;
                           if(var29 < 0.5D) {
                              var31 = 2;
                           }

                           if(var29 > 0.9D) {
                              var31 = 0;
                           }
                        }

                        var13 = var27;
                        if(var15 >= 0 && var16 * var16 + var17 * var17 < var9 * var9 && (!var18 || (var12 + var15 & 1) != 0)) {
                           byte var33 = mapItemSavedData.colors[var12 + var15 * 128];
                           byte var34 = (byte)(var32.id * 4 + var31);
                           if(var33 != var34) {
                              mapItemSavedData.colors[var12 + var15 * 128] = var34;
                              mapItemSavedData.setDirty(var12, var15);
                              var11 = true;
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private BlockState getCorrectStateForFluidBlock(Level level, BlockState var2, BlockPos blockPos) {
      FluidState var4 = var2.getFluidState();
      return !var4.isEmpty() && !var2.isFaceSturdy(level, blockPos, Direction.UP)?var4.createLegacyBlock():var2;
   }

   private static boolean isLand(Biome[] biomes, int var1, int var2, int var3) {
      return biomes[var2 * var1 + var3 * var1 * 128 * var1].getDepth() >= 0.0F;
   }

   public static void renderBiomePreviewMap(Level level, ItemStack itemStack) {
      MapItemSavedData var2 = getOrCreateSavedData(itemStack, level);
      if(var2 != null) {
         if(level.dimension.getType() == var2.dimension) {
            int var3 = 1 << var2.scale;
            int var4 = var2.x;
            int var5 = var2.z;
            Biome[] vars6 = level.getChunkSource().getGenerator().getBiomeSource().getBiomeBlock((var4 / var3 - 64) * var3, (var5 / var3 - 64) * var3, 128 * var3, 128 * var3, false);

            for(int var7 = 0; var7 < 128; ++var7) {
               for(int var8 = 0; var8 < 128; ++var8) {
                  if(var7 > 0 && var8 > 0 && var7 < 127 && var8 < 127) {
                     Biome var9 = vars6[var7 * var3 + var8 * var3 * 128 * var3];
                     int var10 = 8;
                     if(isLand(vars6, var3, var7 - 1, var8 - 1)) {
                        --var10;
                     }

                     if(isLand(vars6, var3, var7 - 1, var8 + 1)) {
                        --var10;
                     }

                     if(isLand(vars6, var3, var7 - 1, var8)) {
                        --var10;
                     }

                     if(isLand(vars6, var3, var7 + 1, var8 - 1)) {
                        --var10;
                     }

                     if(isLand(vars6, var3, var7 + 1, var8 + 1)) {
                        --var10;
                     }

                     if(isLand(vars6, var3, var7 + 1, var8)) {
                        --var10;
                     }

                     if(isLand(vars6, var3, var7, var8 - 1)) {
                        --var10;
                     }

                     if(isLand(vars6, var3, var7, var8 + 1)) {
                        --var10;
                     }

                     int var11 = 3;
                     MaterialColor var12 = MaterialColor.NONE;
                     if(var9.getDepth() < 0.0F) {
                        var12 = MaterialColor.COLOR_ORANGE;
                        if(var10 > 7 && var8 % 2 == 0) {
                           var11 = (var7 + (int)(Mth.sin((float)var8 + 0.0F) * 7.0F)) / 8 % 5;
                           if(var11 == 3) {
                              var11 = 1;
                           } else if(var11 == 4) {
                              var11 = 0;
                           }
                        } else if(var10 > 7) {
                           var12 = MaterialColor.NONE;
                        } else if(var10 > 5) {
                           var11 = 1;
                        } else if(var10 > 3) {
                           var11 = 0;
                        } else if(var10 > 1) {
                           var11 = 0;
                        }
                     } else if(var10 > 0) {
                        var12 = MaterialColor.COLOR_BROWN;
                        if(var10 > 3) {
                           var11 = 1;
                        } else {
                           var11 = 3;
                        }
                     }

                     if(var12 != MaterialColor.NONE) {
                        var2.colors[var7 + var8 * 128] = (byte)(var12.id * 4 + var11);
                        var2.setDirty(var7, var8);
                     }
                  }
               }
            }

         }
      }
   }

   public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int var4, boolean var5) {
      if(!level.isClientSide) {
         MapItemSavedData var6 = getOrCreateSavedData(itemStack, level);
         if(var6 != null) {
            if(entity instanceof Player) {
               Player var7 = (Player)entity;
               var6.tickCarriedBy(var7, itemStack);
            }

            if(!var6.locked && (var5 || entity instanceof Player && ((Player)entity).getOffhandItem() == itemStack)) {
               this.update(level, entity, var6);
            }

         }
      }
   }

   @Nullable
   public Packet getUpdatePacket(ItemStack itemStack, Level level, Player player) {
      return getOrCreateSavedData(itemStack, level).getUpdatePacket(itemStack, level, player);
   }

   public void onCraftedBy(ItemStack itemStack, Level level, Player player) {
      CompoundTag var4 = itemStack.getTag();
      if(var4 != null && var4.contains("map_scale_direction", 99)) {
         scaleMap(itemStack, level, var4.getInt("map_scale_direction"));
         var4.remove("map_scale_direction");
      }

   }

   protected static void scaleMap(ItemStack itemStack, Level level, int var2) {
      MapItemSavedData var3 = getOrCreateSavedData(itemStack, level);
      if(var3 != null) {
         createAndStoreSavedData(itemStack, level, var3.x, var3.z, Mth.clamp(var3.scale + var2, 0, 4), var3.trackingPosition, var3.unlimitedTracking, var3.dimension);
      }

   }

   @Nullable
   public static ItemStack lockMap(Level level, ItemStack var1) {
      MapItemSavedData var2 = getOrCreateSavedData(var1, level);
      if(var2 != null) {
         ItemStack var3 = var1.copy();
         MapItemSavedData var4 = createAndStoreSavedData(var3, level, 0, 0, var2.scale, var2.trackingPosition, var2.unlimitedTracking, var2.dimension);
         var4.lockData(var2);
         return var3;
      } else {
         return null;
      }
   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
      MapItemSavedData var5 = level == null?null:getOrCreateSavedData(itemStack, level);
      if(var5 != null && var5.locked) {
         list.add((new TranslatableComponent("filled_map.locked", new Object[]{Integer.valueOf(getMapId(itemStack))})).withStyle(ChatFormatting.GRAY));
      }

      if(tooltipFlag.isAdvanced()) {
         if(var5 != null) {
            list.add((new TranslatableComponent("filled_map.id", new Object[]{Integer.valueOf(getMapId(itemStack))})).withStyle(ChatFormatting.GRAY));
            list.add((new TranslatableComponent("filled_map.scale", new Object[]{Integer.valueOf(1 << var5.scale)})).withStyle(ChatFormatting.GRAY));
            list.add((new TranslatableComponent("filled_map.level", new Object[]{Byte.valueOf(var5.scale), Integer.valueOf(4)})).withStyle(ChatFormatting.GRAY));
         } else {
            list.add((new TranslatableComponent("filled_map.unknown", new Object[0])).withStyle(ChatFormatting.GRAY));
         }
      }

   }

   public static int getColor(ItemStack itemStack) {
      CompoundTag var1 = itemStack.getTagElement("display");
      if(var1 != null && var1.contains("MapColor", 99)) {
         int var2 = var1.getInt("MapColor");
         return -16777216 | var2 & 16777215;
      } else {
         return -12173266;
      }
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      BlockState var2 = useOnContext.getLevel().getBlockState(useOnContext.getClickedPos());
      if(var2.is(BlockTags.BANNERS)) {
         if(!useOnContext.level.isClientSide) {
            MapItemSavedData var3 = getOrCreateSavedData(useOnContext.getItemInHand(), useOnContext.getLevel());
            var3.toggleBanner(useOnContext.getLevel(), useOnContext.getClickedPos());
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.useOn(useOnContext);
      }
   }
}
