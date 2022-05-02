package net.minecraft.network.chat;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;

public class HoverEvent {
   private final HoverEvent.Action action;
   private final Component value;

   public HoverEvent(HoverEvent.Action action, Component value) {
      this.action = action;
      this.value = value;
   }

   public HoverEvent.Action getAction() {
      return this.action;
   }

   public Component getValue() {
      return this.value;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object != null && this.getClass() == object.getClass()) {
         HoverEvent var2 = (HoverEvent)object;
         if(this.action != var2.action) {
            return false;
         } else {
            if(this.value != null) {
               if(!this.value.equals(var2.value)) {
                  return false;
               }
            } else if(var2.value != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public String toString() {
      return "HoverEvent{action=" + this.action + ", value=\'" + this.value + '\'' + '}';
   }

   public int hashCode() {
      int var1 = this.action.hashCode();
      var1 = 31 * var1 + (this.value != null?this.value.hashCode():0);
      return var1;
   }

   public static enum Action {
      SHOW_TEXT("show_text", true),
      SHOW_ITEM("show_item", true),
      SHOW_ENTITY("show_entity", true);

      private static final Map LOOKUP = (Map)Arrays.stream(values()).collect(Collectors.toMap(HoverEvent.Action::getName, (hoverEvent$Action) -> {
         return hoverEvent$Action;
      }));
      private final boolean allowFromServer;
      private final String name;

      private Action(String name, boolean allowFromServer) {
         this.name = name;
         this.allowFromServer = allowFromServer;
      }

      public boolean isAllowedFromServer() {
         return this.allowFromServer;
      }

      public String getName() {
         return this.name;
      }

      public static HoverEvent.Action getByName(String name) {
         return (HoverEvent.Action)LOOKUP.get(name);
      }
   }
}
