package net.minecraft.network.chat;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClickEvent {
   private final ClickEvent.Action action;
   private final String value;

   public ClickEvent(ClickEvent.Action action, String value) {
      this.action = action;
      this.value = value;
   }

   public ClickEvent.Action getAction() {
      return this.action;
   }

   public String getValue() {
      return this.value;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object != null && this.getClass() == object.getClass()) {
         ClickEvent var2 = (ClickEvent)object;
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
      return "ClickEvent{action=" + this.action + ", value=\'" + this.value + '\'' + '}';
   }

   public int hashCode() {
      int var1 = this.action.hashCode();
      var1 = 31 * var1 + (this.value != null?this.value.hashCode():0);
      return var1;
   }

   public static enum Action {
      OPEN_URL("open_url", true),
      OPEN_FILE("open_file", false),
      RUN_COMMAND("run_command", true),
      SUGGEST_COMMAND("suggest_command", true),
      CHANGE_PAGE("change_page", true);

      private static final Map LOOKUP = (Map)Arrays.stream(values()).collect(Collectors.toMap(ClickEvent.Action::getName, (clickEvent$Action) -> {
         return clickEvent$Action;
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

      public static ClickEvent.Action getByName(String name) {
         return (ClickEvent.Action)LOOKUP.get(name);
      }
   }
}
