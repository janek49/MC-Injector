package net.minecraft.client.gui.font.providers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.font.GlyphProvider;
import javax.annotation.Nullable;
import net.minecraft.server.packs.resources.ResourceManager;

@ClientJarOnly
public interface GlyphProviderBuilder {
   @Nullable
   GlyphProvider create(ResourceManager var1);
}
