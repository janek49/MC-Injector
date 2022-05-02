package net.minecraft.client.resources.model;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;

@ClientJarOnly
public interface UnbakedModel {
   Collection getDependencies();

   Collection getTextures(Function var1, Set var2);

   @Nullable
   BakedModel bake(ModelBakery var1, Function var2, ModelState var3);
}
