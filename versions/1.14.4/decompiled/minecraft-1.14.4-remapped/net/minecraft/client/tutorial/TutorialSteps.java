package net.minecraft.client.tutorial;

import com.fox2code.repacker.ClientJarOnly;
import java.util.function.Function;
import net.minecraft.client.tutorial.CompletedTutorialStepInstance;
import net.minecraft.client.tutorial.CraftPlanksTutorialStep;
import net.minecraft.client.tutorial.FindTreeTutorialStepInstance;
import net.minecraft.client.tutorial.MovementTutorialStepInstance;
import net.minecraft.client.tutorial.OpenInventoryTutorialStep;
import net.minecraft.client.tutorial.PunchTreeTutorialStepInstance;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;

@ClientJarOnly
public enum TutorialSteps {
   MOVEMENT("movement", MovementTutorialStepInstance::<init>),
   FIND_TREE("find_tree", FindTreeTutorialStepInstance::<init>),
   PUNCH_TREE("punch_tree", PunchTreeTutorialStepInstance::<init>),
   OPEN_INVENTORY("open_inventory", OpenInventoryTutorialStep::<init>),
   CRAFT_PLANKS("craft_planks", CraftPlanksTutorialStep::<init>),
   NONE("none", CompletedTutorialStepInstance::<init>);

   private final String name;
   private final Function constructor;

   private TutorialSteps(String name, Function constructor) {
      this.name = name;
      this.constructor = constructor;
   }

   public TutorialStepInstance create(Tutorial tutorial) {
      return (TutorialStepInstance)this.constructor.apply(tutorial);
   }

   public String getName() {
      return this.name;
   }

   public static TutorialSteps getByName(String name) {
      for(TutorialSteps var4 : values()) {
         if(var4.name.equals(name)) {
            return var4;
         }
      }

      return NONE;
   }
}
