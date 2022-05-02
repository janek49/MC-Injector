package net.minecraft.server.packs.repository;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.Pack;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.PackCompatibility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnopenedPack implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final PackMetadataSection BROKEN_ASSETS_FALLBACK = new PackMetadataSection((new TranslatableComponent("resourcePack.broken_assets", new Object[0])).withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.ITALIC}), SharedConstants.getCurrentVersion().getPackVersion());
   private final String id;
   private final Supplier supplier;
   private final Component title;
   private final Component description;
   private final PackCompatibility compatibility;
   private final UnopenedPack.Position defaultPosition;
   private final boolean required;
   private final boolean fixedPosition;

   @Nullable
   public static UnopenedPack create(String string, boolean var1, Supplier supplier, UnopenedPack.UnopenedPackConstructor unopenedPack$UnopenedPackConstructor, UnopenedPack.Position unopenedPack$Position) {
      try {
         Pack var5 = (Pack)supplier.get();
         Throwable var6 = null;

         UnopenedPack var8;
         try {
            PackMetadataSection var7 = (PackMetadataSection)var5.getMetadataSection(PackMetadataSection.SERIALIZER);
            if(var1 && var7 == null) {
               LOGGER.error("Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!");
               var7 = BROKEN_ASSETS_FALLBACK;
            }

            if(var7 == null) {
               LOGGER.warn("Couldn\'t find pack meta for pack {}", string);
               return null;
            }

            var8 = unopenedPack$UnopenedPackConstructor.create(string, var1, supplier, var5, var7, unopenedPack$Position);
         } catch (Throwable var19) {
            var6 = var19;
            throw var19;
         } finally {
            if(var5 != null) {
               if(var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var18) {
                     var6.addSuppressed(var18);
                  }
               } else {
                  var5.close();
               }
            }

         }

         return var8;
      } catch (IOException var21) {
         LOGGER.warn("Couldn\'t get pack info for: {}", var21.toString());
         return null;
      }
   }

   public UnopenedPack(String id, boolean required, Supplier supplier, Component title, Component description, PackCompatibility compatibility, UnopenedPack.Position defaultPosition, boolean fixedPosition) {
      this.id = id;
      this.supplier = supplier;
      this.title = title;
      this.description = description;
      this.compatibility = compatibility;
      this.required = required;
      this.defaultPosition = defaultPosition;
      this.fixedPosition = fixedPosition;
   }

   public UnopenedPack(String string, boolean var2, Supplier supplier, Pack pack, PackMetadataSection packMetadataSection, UnopenedPack.Position unopenedPack$Position) {
      this(string, var2, supplier, new TextComponent(pack.getName()), packMetadataSection.getDescription(), PackCompatibility.forFormat(packMetadataSection.getPackFormat()), unopenedPack$Position, false);
   }

   public Component getTitle() {
      return this.title;
   }

   public Component getDescription() {
      return this.description;
   }

   public Component getChatLink(boolean b) {
      return ComponentUtils.wrapInSquareBrackets(new TextComponent(this.id)).withStyle((style) -> {
         style.setColor(b?ChatFormatting.GREEN:ChatFormatting.RED).setInsertion(StringArgumentType.escapeIfRequired(this.id)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new TextComponent("")).append(this.title).append("\n").append(this.description)));
      });
   }

   public PackCompatibility getCompatibility() {
      return this.compatibility;
   }

   public Pack open() {
      return (Pack)this.supplier.get();
   }

   public String getId() {
      return this.id;
   }

   public boolean isRequired() {
      return this.required;
   }

   public boolean isFixedPosition() {
      return this.fixedPosition;
   }

   public UnopenedPack.Position getDefaultPosition() {
      return this.defaultPosition;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof UnopenedPack)) {
         return false;
      } else {
         UnopenedPack var2 = (UnopenedPack)object;
         return this.id.equals(var2.id);
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public void close() {
   }

   public static enum Position {
      TOP,
      BOTTOM;

      public int insert(List list, Object object, Function function, boolean var4) {
         UnopenedPack.Position var5 = var4?this.opposite():this;
         if(var5 == BOTTOM) {
            int var6;
            for(var6 = 0; var6 < list.size(); ++var6) {
               P var7 = (UnopenedPack)function.apply(list.get(var6));
               if(!var7.isFixedPosition() || var7.getDefaultPosition() != this) {
                  break;
               }
            }

            list.add(var6, object);
            return var6;
         } else {
            int var6;
            for(var6 = list.size() - 1; var6 >= 0; --var6) {
               P var7 = (UnopenedPack)function.apply(list.get(var6));
               if(!var7.isFixedPosition() || var7.getDefaultPosition() != this) {
                  break;
               }
            }

            list.add(var6 + 1, object);
            return var6 + 1;
         }
      }

      public UnopenedPack.Position opposite() {
         return this == TOP?BOTTOM:TOP;
      }
   }

   @FunctionalInterface
   public interface UnopenedPackConstructor {
      @Nullable
      UnopenedPack create(String var1, boolean var2, Supplier var3, Pack var4, PackMetadataSection var5, UnopenedPack.Position var6);
   }
}
