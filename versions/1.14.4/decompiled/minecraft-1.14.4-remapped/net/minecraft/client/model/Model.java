package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.client.model.geom.ModelPart;

@ClientJarOnly
public class Model {
   public final List cubes = Lists.newArrayList();
   public int texWidth = 64;
   public int texHeight = 32;

   public ModelPart getRandomModelPart(Random random) {
      return (ModelPart)this.cubes.get(random.nextInt(this.cubes.size()));
   }
}
