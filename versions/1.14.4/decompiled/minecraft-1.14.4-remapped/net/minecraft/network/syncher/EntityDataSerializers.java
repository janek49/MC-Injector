package net.minecraft.network.syncher;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EntityDataSerializers {
   private static final CrudeIncrementalIntIdentityHashBiMap SERIALIZERS = new CrudeIncrementalIntIdentityHashBiMap(16);
   public static final EntityDataSerializer BYTE = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, Byte byte) {
         friendlyByteBuf.writeByte(byte.byteValue());
      }

      public Byte read(FriendlyByteBuf friendlyByteBuf) {
         return Byte.valueOf(friendlyByteBuf.readByte());
      }

      public Byte copy(Byte byte) {
         return byte;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer INT = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, Integer integer) {
         friendlyByteBuf.writeVarInt(integer.intValue());
      }

      public Integer read(FriendlyByteBuf friendlyByteBuf) {
         return Integer.valueOf(friendlyByteBuf.readVarInt());
      }

      public Integer copy(Integer integer) {
         return integer;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer FLOAT = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, Float float) {
         friendlyByteBuf.writeFloat(float.floatValue());
      }

      public Float read(FriendlyByteBuf friendlyByteBuf) {
         return Float.valueOf(friendlyByteBuf.readFloat());
      }

      public Float copy(Float float) {
         return float;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer STRING = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, String string) {
         friendlyByteBuf.writeUtf(string);
      }

      public String read(FriendlyByteBuf friendlyByteBuf) {
         return friendlyByteBuf.readUtf(32767);
      }

      public String copy(String string) {
         return string;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer COMPONENT = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, Component component) {
         friendlyByteBuf.writeComponent(component);
      }

      public Component read(FriendlyByteBuf friendlyByteBuf) {
         return friendlyByteBuf.readComponent();
      }

      public Component copy(Component component) {
         return component.deepCopy();
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer OPTIONAL_COMPONENT = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, Optional optional) {
         if(optional.isPresent()) {
            friendlyByteBuf.writeBoolean(true);
            friendlyByteBuf.writeComponent((Component)optional.get());
         } else {
            friendlyByteBuf.writeBoolean(false);
         }

      }

      public Optional read(FriendlyByteBuf friendlyByteBuf) {
         return friendlyByteBuf.readBoolean()?Optional.of(friendlyByteBuf.readComponent()):Optional.empty();
      }

      public Optional copy(Optional optional) {
         return optional.isPresent()?Optional.of(((Component)optional.get()).deepCopy()):Optional.empty();
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer ITEM_STACK = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, ItemStack itemStack) {
         friendlyByteBuf.writeItem(itemStack);
      }

      public ItemStack read(FriendlyByteBuf friendlyByteBuf) {
         return friendlyByteBuf.readItem();
      }

      public ItemStack copy(ItemStack itemStack) {
         return itemStack.copy();
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer BLOCK_STATE = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, Optional optional) {
         if(optional.isPresent()) {
            friendlyByteBuf.writeVarInt(Block.getId((BlockState)optional.get()));
         } else {
            friendlyByteBuf.writeVarInt(0);
         }

      }

      public Optional read(FriendlyByteBuf friendlyByteBuf) {
         int var2 = friendlyByteBuf.readVarInt();
         return var2 == 0?Optional.empty():Optional.of(Block.stateById(var2));
      }

      public Optional copy(Optional optional) {
         return optional;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer BOOLEAN = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, Boolean boolean) {
         friendlyByteBuf.writeBoolean(boolean.booleanValue());
      }

      public Boolean read(FriendlyByteBuf friendlyByteBuf) {
         return Boolean.valueOf(friendlyByteBuf.readBoolean());
      }

      public Boolean copy(Boolean boolean) {
         return boolean;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer PARTICLE = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, ParticleOptions particleOptions) {
         friendlyByteBuf.writeVarInt(Registry.PARTICLE_TYPE.getId(particleOptions.getType()));
         particleOptions.writeToNetwork(friendlyByteBuf);
      }

      public ParticleOptions read(FriendlyByteBuf friendlyByteBuf) {
         return this.readParticle(friendlyByteBuf, (ParticleType)Registry.PARTICLE_TYPE.byId(friendlyByteBuf.readVarInt()));
      }

      private ParticleOptions readParticle(FriendlyByteBuf friendlyByteBuf, ParticleType particleType) {
         return particleType.getDeserializer().fromNetwork(particleType, friendlyByteBuf);
      }

      public ParticleOptions copy(ParticleOptions particleOptions) {
         return particleOptions;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer ROTATIONS = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, Rotations rotations) {
         friendlyByteBuf.writeFloat(rotations.getX());
         friendlyByteBuf.writeFloat(rotations.getY());
         friendlyByteBuf.writeFloat(rotations.getZ());
      }

      public Rotations read(FriendlyByteBuf friendlyByteBuf) {
         return new Rotations(friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat());
      }

      public Rotations copy(Rotations rotations) {
         return rotations;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer BLOCK_POS = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, BlockPos blockPos) {
         friendlyByteBuf.writeBlockPos(blockPos);
      }

      public BlockPos read(FriendlyByteBuf friendlyByteBuf) {
         return friendlyByteBuf.readBlockPos();
      }

      public BlockPos copy(BlockPos blockPos) {
         return blockPos;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer OPTIONAL_BLOCK_POS = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, Optional optional) {
         friendlyByteBuf.writeBoolean(optional.isPresent());
         if(optional.isPresent()) {
            friendlyByteBuf.writeBlockPos((BlockPos)optional.get());
         }

      }

      public Optional read(FriendlyByteBuf friendlyByteBuf) {
         return !friendlyByteBuf.readBoolean()?Optional.empty():Optional.of(friendlyByteBuf.readBlockPos());
      }

      public Optional copy(Optional optional) {
         return optional;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer DIRECTION = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, Direction direction) {
         friendlyByteBuf.writeEnum(direction);
      }

      public Direction read(FriendlyByteBuf friendlyByteBuf) {
         return (Direction)friendlyByteBuf.readEnum(Direction.class);
      }

      public Direction copy(Direction direction) {
         return direction;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer OPTIONAL_UUID = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, Optional optional) {
         friendlyByteBuf.writeBoolean(optional.isPresent());
         if(optional.isPresent()) {
            friendlyByteBuf.writeUUID((UUID)optional.get());
         }

      }

      public Optional read(FriendlyByteBuf friendlyByteBuf) {
         return !friendlyByteBuf.readBoolean()?Optional.empty():Optional.of(friendlyByteBuf.readUUID());
      }

      public Optional copy(Optional optional) {
         return optional;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer COMPOUND_TAG = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, CompoundTag compoundTag) {
         friendlyByteBuf.writeNbt(compoundTag);
      }

      public CompoundTag read(FriendlyByteBuf friendlyByteBuf) {
         return friendlyByteBuf.readNbt();
      }

      public CompoundTag copy(CompoundTag compoundTag) {
         return compoundTag.copy();
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer VILLAGER_DATA = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, VillagerData villagerData) {
         friendlyByteBuf.writeVarInt(Registry.VILLAGER_TYPE.getId(villagerData.getType()));
         friendlyByteBuf.writeVarInt(Registry.VILLAGER_PROFESSION.getId(villagerData.getProfession()));
         friendlyByteBuf.writeVarInt(villagerData.getLevel());
      }

      public VillagerData read(FriendlyByteBuf friendlyByteBuf) {
         return new VillagerData((VillagerType)Registry.VILLAGER_TYPE.byId(friendlyByteBuf.readVarInt()), (VillagerProfession)Registry.VILLAGER_PROFESSION.byId(friendlyByteBuf.readVarInt()), friendlyByteBuf.readVarInt());
      }

      public VillagerData copy(VillagerData villagerData) {
         return villagerData;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer OPTIONAL_UNSIGNED_INT = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, OptionalInt optionalInt) {
         friendlyByteBuf.writeVarInt(optionalInt.orElse(-1) + 1);
      }

      public OptionalInt read(FriendlyByteBuf friendlyByteBuf) {
         int var2 = friendlyByteBuf.readVarInt();
         return var2 == 0?OptionalInt.empty():OptionalInt.of(var2 - 1);
      }

      public OptionalInt copy(OptionalInt optionalInt) {
         return optionalInt;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer POSE = new EntityDataSerializer() {
      public void write(FriendlyByteBuf friendlyByteBuf, Pose pose) {
         friendlyByteBuf.writeEnum(pose);
      }

      public Pose read(FriendlyByteBuf friendlyByteBuf) {
         return (Pose)friendlyByteBuf.readEnum(Pose.class);
      }

      public Pose copy(Pose pose) {
         return pose;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };

   public static void registerSerializer(EntityDataSerializer entityDataSerializer) {
      SERIALIZERS.add(entityDataSerializer);
   }

   @Nullable
   public static EntityDataSerializer getSerializer(int i) {
      return (EntityDataSerializer)SERIALIZERS.byId(i);
   }

   public static int getSerializedId(EntityDataSerializer entityDataSerializer) {
      return SERIALIZERS.getId(entityDataSerializer);
   }

   static {
      registerSerializer(BYTE);
      registerSerializer(INT);
      registerSerializer(FLOAT);
      registerSerializer(STRING);
      registerSerializer(COMPONENT);
      registerSerializer(OPTIONAL_COMPONENT);
      registerSerializer(ITEM_STACK);
      registerSerializer(BOOLEAN);
      registerSerializer(ROTATIONS);
      registerSerializer(BLOCK_POS);
      registerSerializer(OPTIONAL_BLOCK_POS);
      registerSerializer(DIRECTION);
      registerSerializer(OPTIONAL_UUID);
      registerSerializer(BLOCK_STATE);
      registerSerializer(COMPOUND_TAG);
      registerSerializer(PARTICLE);
      registerSerializer(VILLAGER_DATA);
      registerSerializer(OPTIONAL_UNSIGNED_INT);
      registerSerializer(POSE);
   }
}
