package net.minecraft.world.level.saveddata.maps;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

public class MapBanner {
   private final BlockPos pos;
   private final DyeColor color;
   @Nullable
   private final Component name;

   public MapBanner(BlockPos pos, DyeColor color, @Nullable Component name) {
      this.pos = pos;
      this.color = color;
      this.name = name;
   }

   public static MapBanner load(CompoundTag compoundTag) {
      BlockPos var1 = NbtUtils.readBlockPos(compoundTag.getCompound("Pos"));
      DyeColor var2 = DyeColor.byName(compoundTag.getString("Color"), DyeColor.WHITE);
      Component var3 = compoundTag.contains("Name")?Component.Serializer.fromJson(compoundTag.getString("Name")):null;
      return new MapBanner(var1, var2, var3);
   }

   @Nullable
   public static MapBanner fromWorld(BlockGetter blockGetter, BlockPos blockPos) {
      BlockEntity var2 = blockGetter.getBlockEntity(blockPos);
      if(var2 instanceof BannerBlockEntity) {
         BannerBlockEntity var3 = (BannerBlockEntity)var2;
         DyeColor var4 = var3.getBaseColor(() -> {
            return blockGetter.getBlockState(blockPos);
         });
         Component var5 = var3.hasCustomName()?var3.getCustomName():null;
         return new MapBanner(blockPos, var4, var5);
      } else {
         return null;
      }
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public MapDecoration.Type getDecoration() {
      switch(this.color) {
      case WHITE:
         return MapDecoration.Type.BANNER_WHITE;
      case ORANGE:
         return MapDecoration.Type.BANNER_ORANGE;
      case MAGENTA:
         return MapDecoration.Type.BANNER_MAGENTA;
      case LIGHT_BLUE:
         return MapDecoration.Type.BANNER_LIGHT_BLUE;
      case YELLOW:
         return MapDecoration.Type.BANNER_YELLOW;
      case LIME:
         return MapDecoration.Type.BANNER_LIME;
      case PINK:
         return MapDecoration.Type.BANNER_PINK;
      case GRAY:
         return MapDecoration.Type.BANNER_GRAY;
      case LIGHT_GRAY:
         return MapDecoration.Type.BANNER_LIGHT_GRAY;
      case CYAN:
         return MapDecoration.Type.BANNER_CYAN;
      case PURPLE:
         return MapDecoration.Type.BANNER_PURPLE;
      case BLUE:
         return MapDecoration.Type.BANNER_BLUE;
      case BROWN:
         return MapDecoration.Type.BANNER_BROWN;
      case GREEN:
         return MapDecoration.Type.BANNER_GREEN;
      case RED:
         return MapDecoration.Type.BANNER_RED;
      case BLACK:
      default:
         return MapDecoration.Type.BANNER_BLACK;
      }
   }

   @Nullable
   public Component getName() {
      return this.name;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object != null && this.getClass() == object.getClass()) {
         MapBanner var2 = (MapBanner)object;
         return Objects.equals(this.pos, var2.pos) && this.color == var2.color && Objects.equals(this.name, var2.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.pos, this.color, this.name});
   }

   public CompoundTag save() {
      CompoundTag compoundTag = new CompoundTag();
      compoundTag.put("Pos", NbtUtils.writeBlockPos(this.pos));
      compoundTag.putString("Color", this.color.getName());
      if(this.name != null) {
         compoundTag.putString("Name", Component.Serializer.toJson(this.name));
      }

      return compoundTag;
   }

   public String getId() {
      return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
   }
}
