package net.minecraft.world.scores;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class Objective {
   private final Scoreboard scoreboard;
   private final String name;
   private final ObjectiveCriteria criteria;
   private Component displayName;
   private ObjectiveCriteria.RenderType renderType;

   public Objective(Scoreboard scoreboard, String name, ObjectiveCriteria criteria, Component displayName, ObjectiveCriteria.RenderType renderType) {
      this.scoreboard = scoreboard;
      this.name = name;
      this.criteria = criteria;
      this.displayName = displayName;
      this.renderType = renderType;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public String getName() {
      return this.name;
   }

   public ObjectiveCriteria getCriteria() {
      return this.criteria;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public Component getFormattedDisplayName() {
      return ComponentUtils.wrapInSquareBrackets(this.displayName.deepCopy().withStyle((style) -> {
         style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(this.getName())));
      }));
   }

   public void setDisplayName(Component displayName) {
      this.displayName = displayName;
      this.scoreboard.onObjectiveChanged(this);
   }

   public ObjectiveCriteria.RenderType getRenderType() {
      return this.renderType;
   }

   public void setRenderType(ObjectiveCriteria.RenderType renderType) {
      this.renderType = renderType;
      this.scoreboard.onObjectiveChanged(this);
   }
}
