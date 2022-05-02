package net.minecraft.network.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ContextAwareComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectorComponent extends BaseComponent implements ContextAwareComponent {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String pattern;
   @Nullable
   private final EntitySelector selector;

   public SelectorComponent(String pattern) {
      this.pattern = pattern;
      EntitySelector var2 = null;

      try {
         EntitySelectorParser var3 = new EntitySelectorParser(new StringReader(pattern));
         var2 = var3.parse();
      } catch (CommandSyntaxException var4) {
         LOGGER.warn("Invalid selector component: {}", pattern, var4.getMessage());
      }

      this.selector = var2;
   }

   public String getPattern() {
      return this.pattern;
   }

   public Component resolve(@Nullable CommandSourceStack commandSourceStack, @Nullable Entity entity, int var3) throws CommandSyntaxException {
      return (Component)(commandSourceStack != null && this.selector != null?EntitySelector.joinNames(this.selector.findEntities(commandSourceStack)):new TextComponent(""));
   }

   public String getContents() {
      return this.pattern;
   }

   public SelectorComponent copy() {
      return new SelectorComponent(this.pattern);
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof SelectorComponent)) {
         return false;
      } else {
         SelectorComponent var2 = (SelectorComponent)object;
         return this.pattern.equals(var2.pattern) && super.equals(object);
      }
   }

   public String toString() {
      return "SelectorComponent{pattern=\'" + this.pattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   // $FF: synthetic method
   public Component copy() {
      return this.copy();
   }
}
