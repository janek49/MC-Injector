package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NbtUtils {
   private static final Logger LOGGER = LogManager.getLogger();

   @Nullable
   public static GameProfile readGameProfile(CompoundTag compoundTag) {
      String var1 = null;
      String var2 = null;
      if(compoundTag.contains("Name", 8)) {
         var1 = compoundTag.getString("Name");
      }

      if(compoundTag.contains("Id", 8)) {
         var2 = compoundTag.getString("Id");
      }

      try {
         UUID var3;
         try {
            var3 = UUID.fromString(var2);
         } catch (Throwable var12) {
            var3 = null;
         }

         GameProfile var4 = new GameProfile(var3, var1);
         if(compoundTag.contains("Properties", 10)) {
            CompoundTag var5 = compoundTag.getCompound("Properties");

            for(String var7 : var5.getAllKeys()) {
               ListTag var8 = var5.getList(var7, 10);

               for(int var9 = 0; var9 < var8.size(); ++var9) {
                  CompoundTag var10 = var8.getCompound(var9);
                  String var11 = var10.getString("Value");
                  if(var10.contains("Signature", 8)) {
                     var4.getProperties().put(var7, new com.mojang.authlib.properties.Property(var7, var11, var10.getString("Signature")));
                  } else {
                     var4.getProperties().put(var7, new com.mojang.authlib.properties.Property(var7, var11));
                  }
               }
            }
         }

         return var4;
      } catch (Throwable var13) {
         return null;
      }
   }

   public static CompoundTag writeGameProfile(CompoundTag var0, GameProfile gameProfile) {
      if(!StringUtil.isNullOrEmpty(gameProfile.getName())) {
         var0.putString("Name", gameProfile.getName());
      }

      if(gameProfile.getId() != null) {
         var0.putString("Id", gameProfile.getId().toString());
      }

      if(!gameProfile.getProperties().isEmpty()) {
         CompoundTag var2 = new CompoundTag();

         for(String var4 : gameProfile.getProperties().keySet()) {
            ListTag var5 = new ListTag();

            for(com.mojang.authlib.properties.Property var7 : gameProfile.getProperties().get(var4)) {
               CompoundTag var8 = new CompoundTag();
               var8.putString("Value", var7.getValue());
               if(var7.hasSignature()) {
                  var8.putString("Signature", var7.getSignature());
               }

               var5.add(var8);
            }

            var2.put(var4, var5);
         }

         var0.put("Properties", var2);
      }

      return var0;
   }

   @VisibleForTesting
   public static boolean compareNbt(@Nullable Tag var0, @Nullable Tag var1, boolean var2) {
      if(var0 == var1) {
         return true;
      } else if(var0 == null) {
         return true;
      } else if(var1 == null) {
         return false;
      } else if(!var0.getClass().equals(var1.getClass())) {
         return false;
      } else if(var0 instanceof CompoundTag) {
         CompoundTag var3 = (CompoundTag)var0;
         CompoundTag var4 = (CompoundTag)var1;

         for(String var6 : var3.getAllKeys()) {
            Tag var7 = var3.get(var6);
            if(!compareNbt(var7, var4.get(var6), var2)) {
               return false;
            }
         }

         return true;
      } else if(var0 instanceof ListTag && var2) {
         ListTag var3 = (ListTag)var0;
         ListTag var4 = (ListTag)var1;
         if(var3.isEmpty()) {
            return var4.isEmpty();
         } else {
            for(int var5 = 0; var5 < var3.size(); ++var5) {
               Tag var6 = var3.get(var5);
               boolean var7 = false;

               for(int var8 = 0; var8 < var4.size(); ++var8) {
                  if(compareNbt(var6, var4.get(var8), var2)) {
                     var7 = true;
                     break;
                  }
               }

               if(!var7) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return var0.equals(var1);
      }
   }

   public static CompoundTag createUUIDTag(UUID uUID) {
      CompoundTag compoundTag = new CompoundTag();
      compoundTag.putLong("M", uUID.getMostSignificantBits());
      compoundTag.putLong("L", uUID.getLeastSignificantBits());
      return compoundTag;
   }

   public static UUID loadUUIDTag(CompoundTag compoundTag) {
      return new UUID(compoundTag.getLong("M"), compoundTag.getLong("L"));
   }

   public static BlockPos readBlockPos(CompoundTag compoundTag) {
      return new BlockPos(compoundTag.getInt("X"), compoundTag.getInt("Y"), compoundTag.getInt("Z"));
   }

   public static CompoundTag writeBlockPos(BlockPos blockPos) {
      CompoundTag compoundTag = new CompoundTag();
      compoundTag.putInt("X", blockPos.getX());
      compoundTag.putInt("Y", blockPos.getY());
      compoundTag.putInt("Z", blockPos.getZ());
      return compoundTag;
   }

   public static BlockState readBlockState(CompoundTag compoundTag) {
      if(!compoundTag.contains("Name", 8)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         Block var1 = (Block)Registry.BLOCK.get(new ResourceLocation(compoundTag.getString("Name")));
         BlockState var2 = var1.defaultBlockState();
         if(compoundTag.contains("Properties", 10)) {
            CompoundTag var3 = compoundTag.getCompound("Properties");
            StateDefinition<Block, BlockState> var4 = var1.getStateDefinition();

            for(String var6 : var3.getAllKeys()) {
               Property<?> var7 = var4.getProperty(var6);
               if(var7 != null) {
                  var2 = (BlockState)setValueHelper(var2, var7, var6, var3, compoundTag);
               }
            }
         }

         return var2;
      }
   }

   private static StateHolder setValueHelper(StateHolder var0, Property property, String string, CompoundTag var3, CompoundTag var4) {
      Optional<T> var5 = property.getValue(var3.getString(string));
      if(var5.isPresent()) {
         return (StateHolder)var0.setValue(property, (Comparable)var5.get());
      } else {
         LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", string, var3.getString(string), var4.toString());
         return var0;
      }
   }

   public static CompoundTag writeBlockState(BlockState blockState) {
      CompoundTag compoundTag = new CompoundTag();
      compoundTag.putString("Name", Registry.BLOCK.getKey(blockState.getBlock()).toString());
      ImmutableMap<Property<?>, Comparable<?>> var2 = blockState.getValues();
      if(!var2.isEmpty()) {
         CompoundTag var3 = new CompoundTag();
         UnmodifiableIterator var4 = var2.entrySet().iterator();

         while(var4.hasNext()) {
            Entry<Property<?>, Comparable<?>> var5 = (Entry)var4.next();
            Property<?> var6 = (Property)var5.getKey();
            var3.putString(var6.getName(), getName(var6, (Comparable)var5.getValue()));
         }

         compoundTag.put("Properties", var3);
      }

      return compoundTag;
   }

   private static String getName(Property property, Comparable comparable) {
      return property.getName(comparable);
   }

   public static CompoundTag update(DataFixer dataFixer, DataFixTypes dataFixTypes, CompoundTag var2, int var3) {
      return update(dataFixer, dataFixTypes, var2, var3, SharedConstants.getCurrentVersion().getWorldVersion());
   }

   public static CompoundTag update(DataFixer dataFixer, DataFixTypes dataFixTypes, CompoundTag var2, int var3, int var4) {
      return (CompoundTag)dataFixer.update(dataFixTypes.getType(), new Dynamic(NbtOps.INSTANCE, var2), var3, var4).getValue();
   }
}
