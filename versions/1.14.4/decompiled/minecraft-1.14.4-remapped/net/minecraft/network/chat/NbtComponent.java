package net.minecraft.network.chat;

import com.google.common.base.Joiner;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.ContextAwareComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class NbtComponent extends BaseComponent implements ContextAwareComponent {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final boolean interpreting;
   protected final String nbtPathPattern;
   @Nullable
   protected final NbtPathArgument.NbtPath compiledNbtPath;

   @Nullable
   private static NbtPathArgument.NbtPath compileNbtPath(String string) {
      try {
         return (new NbtPathArgument()).parse(new StringReader(string));
      } catch (CommandSyntaxException var2) {
         return null;
      }
   }

   public NbtComponent(String string, boolean var2) {
      this(string, compileNbtPath(string), var2);
   }

   protected NbtComponent(String nbtPathPattern, @Nullable NbtPathArgument.NbtPath compiledNbtPath, boolean interpreting) {
      this.nbtPathPattern = nbtPathPattern;
      this.compiledNbtPath = compiledNbtPath;
      this.interpreting = interpreting;
   }

   protected abstract Stream getData(CommandSourceStack var1) throws CommandSyntaxException;

   public String getContents() {
      return "";
   }

   public String getNbtPath() {
      return this.nbtPathPattern;
   }

   public boolean isInterpreting() {
      return this.interpreting;
   }

   public Component resolve(@Nullable CommandSourceStack commandSourceStack, @Nullable Entity entity, int var3) throws CommandSyntaxException {
      if(commandSourceStack != null && this.compiledNbtPath != null) {
         Stream<String> var4 = this.getData(commandSourceStack).flatMap((compoundTag) -> {
            try {
               return this.compiledNbtPath.get(compoundTag).stream();
            } catch (CommandSyntaxException var3) {
               return Stream.empty();
            }
         }).map(Tag::getAsString);
         return (Component)(this.interpreting?(Component)var4.flatMap((string) -> {
            try {
               Component var4 = Component.Serializer.fromJson(string);
               return Stream.of(ComponentUtils.updateForEntity(commandSourceStack, var4, entity, var3));
            } catch (Exception var5) {
               LOGGER.warn("Failed to parse component: " + string, var5);
               return Stream.of(new Component[0]);
            }
         }).reduce((var0, var1) -> {
            return var0.append(", ").append(var1);
         }).orElse(new TextComponent("")):new TextComponent(Joiner.on(", ").join(var4.iterator())));
      } else {
         return new TextComponent("");
      }
   }

   public static class BlockNbtComponent extends NbtComponent {
      private final String posPattern;
      @Nullable
      private final Coordinates compiledPos;

      public BlockNbtComponent(String var1, boolean var2, String posPattern) {
         super(var1, var2);
         this.posPattern = posPattern;
         this.compiledPos = this.compilePos(this.posPattern);
      }

      @Nullable
      private Coordinates compilePos(String string) {
         try {
            return BlockPosArgument.blockPos().parse(new StringReader(string));
         } catch (CommandSyntaxException var3) {
            return null;
         }
      }

      private BlockNbtComponent(String var1, @Nullable NbtPathArgument.NbtPath nbtPathArgument$NbtPath, boolean var3, String posPattern, @Nullable Coordinates compiledPos) {
         super(var1, nbtPathArgument$NbtPath, var3);
         this.posPattern = posPattern;
         this.compiledPos = compiledPos;
      }

      @Nullable
      public String getPos() {
         return this.posPattern;
      }

      public Component copy() {
         return new NbtComponent.BlockNbtComponent(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.posPattern, this.compiledPos);
      }

      protected Stream getData(CommandSourceStack commandSourceStack) {
         if(this.compiledPos != null) {
            ServerLevel var2 = commandSourceStack.getLevel();
            BlockPos var3 = this.compiledPos.getBlockPos(commandSourceStack);
            if(var2.isLoaded(var3)) {
               BlockEntity var4 = var2.getBlockEntity(var3);
               if(var4 != null) {
                  return Stream.of(var4.save(new CompoundTag()));
               }
            }
         }

         return Stream.empty();
      }

      public boolean equals(Object object) {
         if(this == object) {
            return true;
         } else if(!(object instanceof NbtComponent.BlockNbtComponent)) {
            return false;
         } else {
            NbtComponent.BlockNbtComponent var2 = (NbtComponent.BlockNbtComponent)object;
            return Objects.equals(this.posPattern, var2.posPattern) && Objects.equals(this.nbtPathPattern, var2.nbtPathPattern) && super.equals(object);
         }
      }

      public String toString() {
         return "BlockPosArgument{pos=\'" + this.posPattern + '\'' + "path=\'" + this.nbtPathPattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
      }
   }

   public static class EntityNbtComponent extends NbtComponent {
      private final String selectorPattern;
      @Nullable
      private final EntitySelector compiledSelector;

      public EntityNbtComponent(String var1, boolean var2, String selectorPattern) {
         super(var1, var2);
         this.selectorPattern = selectorPattern;
         this.compiledSelector = compileSelector(selectorPattern);
      }

      @Nullable
      private static EntitySelector compileSelector(String string) {
         try {
            EntitySelectorParser var1 = new EntitySelectorParser(new StringReader(string));
            return var1.parse();
         } catch (CommandSyntaxException var2) {
            return null;
         }
      }

      private EntityNbtComponent(String var1, @Nullable NbtPathArgument.NbtPath nbtPathArgument$NbtPath, boolean var3, String selectorPattern, @Nullable EntitySelector compiledSelector) {
         super(var1, nbtPathArgument$NbtPath, var3);
         this.selectorPattern = selectorPattern;
         this.compiledSelector = compiledSelector;
      }

      public String getSelector() {
         return this.selectorPattern;
      }

      public Component copy() {
         return new NbtComponent.EntityNbtComponent(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.selectorPattern, this.compiledSelector);
      }

      protected Stream getData(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
         if(this.compiledSelector != null) {
            List<? extends Entity> var2 = this.compiledSelector.findEntities(commandSourceStack);
            return var2.stream().map(NbtPredicate::getEntityTagToCompare);
         } else {
            return Stream.empty();
         }
      }

      public boolean equals(Object object) {
         if(this == object) {
            return true;
         } else if(!(object instanceof NbtComponent.EntityNbtComponent)) {
            return false;
         } else {
            NbtComponent.EntityNbtComponent var2 = (NbtComponent.EntityNbtComponent)object;
            return Objects.equals(this.selectorPattern, var2.selectorPattern) && Objects.equals(this.nbtPathPattern, var2.nbtPathPattern) && super.equals(object);
         }
      }

      public String toString() {
         return "EntityNbtComponent{selector=\'" + this.selectorPattern + '\'' + "path=\'" + this.nbtPathPattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
      }
   }
}
