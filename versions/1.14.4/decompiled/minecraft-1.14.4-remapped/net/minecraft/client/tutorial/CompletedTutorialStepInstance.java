package net.minecraft.client.tutorial;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;

@ClientJarOnly
public class CompletedTutorialStepInstance implements TutorialStepInstance {
   private final Tutorial tutorial;

   public CompletedTutorialStepInstance(Tutorial tutorial) {
      this.tutorial = tutorial;
   }
}
