package net.minecraft.server.commands.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockDataAccessor implements DataAccessor {
   private static final SimpleCommandExceptionType ERROR_NOT_A_BLOCK_ENTITY = new SimpleCommandExceptionType(new TranslatableComponent("commands.data.block.invalid", new Object[0]));
   public static final Function PROVIDER = (string) -> {
      return new DataCommands.DataProvider() {
         public DataAccessor access(CommandContext commandContext) throws CommandSyntaxException {
            BlockPos var2 = BlockPosArgument.getLoadedBlockPos(commandContext, string + "Pos");
            BlockEntity var3 = ((CommandSourceStack)commandContext.getSource()).getLevel().getBlockEntity(var2);
            if(var3 == null) {
               throw BlockDataAccessor.ERROR_NOT_A_BLOCK_ENTITY.create();
            } else {
               return new BlockDataAccessor(var3, var2);
            }
         }

         public ArgumentBuilder wrap(ArgumentBuilder var1, Function function) {
            return var1.then(Commands.literal("block").then((ArgumentBuilder)function.apply(Commands.argument(string + "Pos", BlockPosArgument.blockPos()))));
         }
      };
   };
   private final BlockEntity entity;
   private final BlockPos pos;

   public BlockDataAccessor(BlockEntity entity, BlockPos pos) {
      this.entity = entity;
      this.pos = pos;
   }

   public void setData(CompoundTag data) {
      data.putInt("x", this.pos.getX());
      data.putInt("y", this.pos.getY());
      data.putInt("z", this.pos.getZ());
      this.entity.load(data);
      this.entity.setChanged();
      BlockState var2 = this.entity.getLevel().getBlockState(this.pos);
      this.entity.getLevel().sendBlockUpdated(this.pos, var2, var2, 3);
   }

   public CompoundTag getData() {
      return this.entity.save(new CompoundTag());
   }

   public Component getModifiedSuccess() {
      return new TranslatableComponent("commands.data.block.modified", new Object[]{Integer.valueOf(this.pos.getX()), Integer.valueOf(this.pos.getY()), Integer.valueOf(this.pos.getZ())});
   }

   public Component getPrintSuccess(Tag tag) {
      return new TranslatableComponent("commands.data.block.query", new Object[]{Integer.valueOf(this.pos.getX()), Integer.valueOf(this.pos.getY()), Integer.valueOf(this.pos.getZ()), tag.getPrettyDisplay()});
   }

   public Component getPrintSuccess(NbtPathArgument.NbtPath nbtPathArgument$NbtPath, double var2, int var4) {
      return new TranslatableComponent("commands.data.block.get", new Object[]{nbtPathArgument$NbtPath, Integer.valueOf(this.pos.getX()), Integer.valueOf(this.pos.getY()), Integer.valueOf(this.pos.getZ()), String.format(Locale.ROOT, "%.2f", new Object[]{Double.valueOf(var2)}), Integer.valueOf(var4)});
   }
}
