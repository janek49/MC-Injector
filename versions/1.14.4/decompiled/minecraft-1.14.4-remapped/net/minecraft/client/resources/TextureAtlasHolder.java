package net.minecraft.client.resources;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TickableTextureObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

@ClientJarOnly
public abstract class TextureAtlasHolder extends SimplePreparableReloadListener implements AutoCloseable {
   private final TextureAtlas textureAtlas;

   public TextureAtlasHolder(TextureManager textureManager, ResourceLocation resourceLocation, String string) {
      this.textureAtlas = new TextureAtlas(string);
      textureManager.register((ResourceLocation)resourceLocation, (TickableTextureObject)this.textureAtlas);
   }

   protected abstract Iterable getResourcesToLoad();

   protected TextureAtlasSprite getSprite(ResourceLocation resourceLocation) {
      return this.textureAtlas.getSprite(resourceLocation);
   }

   protected TextureAtlas.Preparations prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      profilerFiller.startTick();
      profilerFiller.push("stitching");
      TextureAtlas.Preparations textureAtlas$Preparations = this.textureAtlas.prepareToStitch(resourceManager, this.getResourcesToLoad(), profilerFiller);
      profilerFiller.pop();
      profilerFiller.endTick();
      return textureAtlas$Preparations;
   }

   protected void apply(TextureAtlas.Preparations textureAtlas$Preparations, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      profilerFiller.startTick();
      profilerFiller.push("upload");
      this.textureAtlas.reload(textureAtlas$Preparations);
      profilerFiller.pop();
      profilerFiller.endTick();
   }

   public void close() {
      this.textureAtlas.clearTextureData();
   }

   // $FF: synthetic method
   protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }
}
