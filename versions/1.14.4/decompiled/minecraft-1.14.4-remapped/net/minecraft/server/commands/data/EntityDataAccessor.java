package net.minecraft.server.commands.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class EntityDataAccessor implements DataAccessor {
   private static final SimpleCommandExceptionType ERROR_NO_PLAYERS = new SimpleCommandExceptionType(new TranslatableComponent("commands.data.entity.invalid", new Object[0]));
   public static final Function PROVIDER = (string) -> {
      return new DataCommands.DataProvider() {
         public DataAccessor access(CommandContext commandContext) throws CommandSyntaxException {
            return new EntityDataAccessor(EntityArgument.getEntity(commandContext, string));
         }

         public ArgumentBuilder wrap(ArgumentBuilder var1, Function function) {
            return var1.then(Commands.literal("entity").then((ArgumentBuilder)function.apply(Commands.argument(string, EntityArgument.entity()))));
         }
      };
   };
   private final Entity entity;

   public EntityDataAccessor(Entity entity) {
      this.entity = entity;
   }

   public void setData(CompoundTag data) throws CommandSyntaxException {
      if(this.entity instanceof Player) {
         throw ERROR_NO_PLAYERS.create();
      } else {
         UUID var2 = this.entity.getUUID();
         this.entity.load(data);
         this.entity.setUUID(var2);
      }
   }

   public CompoundTag getData() {
      return NbtPredicate.getEntityTagToCompare(this.entity);
   }

   public Component getModifiedSuccess() {
      return new TranslatableComponent("commands.data.entity.modified", new Object[]{this.entity.getDisplayName()});
   }

   public Component getPrintSuccess(Tag tag) {
      return new TranslatableComponent("commands.data.entity.query", new Object[]{this.entity.getDisplayName(), tag.getPrettyDisplay()});
   }

   public Component getPrintSuccess(NbtPathArgument.NbtPath nbtPathArgument$NbtPath, double var2, int var4) {
      return new TranslatableComponent("commands.data.entity.get", new Object[]{nbtPathArgument$NbtPath, this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", new Object[]{Double.valueOf(var2)}), Integer.valueOf(var4)});
   }
}
