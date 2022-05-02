package net.minecraft.network.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ContextAwareComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class ScoreComponent extends BaseComponent implements ContextAwareComponent {
   private final String name;
   @Nullable
   private final EntitySelector selector;
   private final String objective;
   private String value = "";

   public ScoreComponent(String name, String objective) {
      this.name = name;
      this.objective = objective;
      EntitySelector var3 = null;

      try {
         EntitySelectorParser var4 = new EntitySelectorParser(new StringReader(name));
         var3 = var4.parse();
      } catch (CommandSyntaxException var5) {
         ;
      }

      this.selector = var3;
   }

   public String getName() {
      return this.name;
   }

   public String getObjective() {
      return this.objective;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public String getContents() {
      return this.value;
   }

   private void resolve(CommandSourceStack commandSourceStack) {
      MinecraftServer var2 = commandSourceStack.getServer();
      if(var2 != null && var2.isInitialized() && StringUtil.isNullOrEmpty(this.value)) {
         Scoreboard var3 = var2.getScoreboard();
         Objective var4 = var3.getObjective(this.objective);
         if(var3.hasPlayerScore(this.name, var4)) {
            Score var5 = var3.getOrCreatePlayerScore(this.name, var4);
            this.setValue(String.format("%d", new Object[]{Integer.valueOf(var5.getScore())}));
         } else {
            this.value = "";
         }
      }

   }

   public ScoreComponent copy() {
      ScoreComponent scoreComponent = new ScoreComponent(this.name, this.objective);
      scoreComponent.setValue(this.value);
      return scoreComponent;
   }

   public Component resolve(@Nullable CommandSourceStack commandSourceStack, @Nullable Entity entity, int var3) throws CommandSyntaxException {
      if(commandSourceStack == null) {
         return this.copy();
      } else {
         String var4;
         if(this.selector != null) {
            List<? extends Entity> var5 = this.selector.findEntities(commandSourceStack);
            if(var5.isEmpty()) {
               var4 = this.name;
            } else {
               if(var5.size() != 1) {
                  throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
               }

               var4 = ((Entity)var5.get(0)).getScoreboardName();
            }
         } else {
            var4 = this.name;
         }

         String var5 = entity != null && var4.equals("*")?entity.getScoreboardName():var4;
         ScoreComponent var6 = new ScoreComponent(var5, this.objective);
         var6.setValue(this.value);
         var6.resolve(commandSourceStack);
         return var6;
      }
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof ScoreComponent)) {
         return false;
      } else {
         ScoreComponent var2 = (ScoreComponent)object;
         return this.name.equals(var2.name) && this.objective.equals(var2.objective) && super.equals(object);
      }
   }

   public String toString() {
      return "ScoreComponent{name=\'" + this.name + '\'' + "objective=\'" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   // $FF: synthetic method
   public Component copy() {
      return this.copy();
   }
}
