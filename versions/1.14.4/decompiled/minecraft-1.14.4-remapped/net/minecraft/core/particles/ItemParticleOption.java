package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class ItemParticleOption implements ParticleOptions {
   public static final ParticleOptions.Deserializer DESERIALIZER = new ParticleOptions.Deserializer() {
      public ItemParticleOption fromCommand(ParticleType particleType, StringReader stringReader) throws CommandSyntaxException {
         stringReader.expect(' ');
         ItemParser var3 = (new ItemParser(stringReader, false)).parse();
         ItemStack var4 = (new ItemInput(var3.getItem(), var3.getNbt())).createItemStack(1, false);
         return new ItemParticleOption(particleType, var4);
      }

      public ItemParticleOption fromNetwork(ParticleType particleType, FriendlyByteBuf friendlyByteBuf) {
         return new ItemParticleOption(particleType, friendlyByteBuf.readItem());
      }

      // $FF: synthetic method
      public ParticleOptions fromNetwork(ParticleType var1, FriendlyByteBuf var2) {
         return this.fromNetwork(var1, var2);
      }

      // $FF: synthetic method
      public ParticleOptions fromCommand(ParticleType var1, StringReader var2) throws CommandSyntaxException {
         return this.fromCommand(var1, var2);
      }
   };
   private final ParticleType type;
   private final ItemStack itemStack;

   public ItemParticleOption(ParticleType type, ItemStack itemStack) {
      this.type = type;
      this.itemStack = itemStack;
   }

   public void writeToNetwork(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.writeItem(this.itemStack);
   }

   public String writeToString() {
      return Registry.PARTICLE_TYPE.getKey(this.getType()) + " " + (new ItemInput(this.itemStack.getItem(), this.itemStack.getTag())).serialize();
   }

   public ParticleType getType() {
      return this.type;
   }

   public ItemStack getItem() {
      return this.itemStack;
   }
}
