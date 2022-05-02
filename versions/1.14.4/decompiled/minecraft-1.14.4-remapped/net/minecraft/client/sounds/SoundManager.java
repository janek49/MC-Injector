package net.minecraft.client.sounds;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.client.resources.sounds.SoundEventRegistrationSerializer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class SoundManager extends SimplePreparableReloadListener {
   public static final Sound EMPTY_SOUND = new Sound("meta:missing_sound", 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 16);
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Component.class, new Component.Serializer()).registerTypeAdapter(SoundEventRegistration.class, new SoundEventRegistrationSerializer()).create();
   private static final ParameterizedType SOUND_EVENT_REGISTRATION_TYPE = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class, SoundEventRegistration.class};
      }

      public Type getRawType() {
         return Map.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };
   private final Map registry = Maps.newHashMap();
   private final SoundEngine soundEngine;

   public SoundManager(ResourceManager resourceManager, Options options) {
      this.soundEngine = new SoundEngine(this, options, resourceManager);
   }

   protected SoundManager.Preparations prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      SoundManager.Preparations soundManager$Preparations = new SoundManager.Preparations();
      profilerFiller.startTick();

      for(String var5 : resourceManager.getNamespaces()) {
         profilerFiller.push(var5);

         try {
            for(Resource var8 : resourceManager.getResources(new ResourceLocation(var5, "sounds.json"))) {
               profilerFiller.push(var8.getSourceName());

               try {
                  profilerFiller.push("parse");
                  Map<String, SoundEventRegistration> var9 = getEventFromJson(var8.getInputStream());
                  profilerFiller.popPush("register");

                  for(Entry<String, SoundEventRegistration> var11 : var9.entrySet()) {
                     soundManager$Preparations.handleRegistration(new ResourceLocation(var5, (String)var11.getKey()), (SoundEventRegistration)var11.getValue(), resourceManager);
                  }

                  profilerFiller.pop();
               } catch (RuntimeException var12) {
                  LOGGER.warn("Invalid sounds.json in resourcepack: \'{}\'", var8.getSourceName(), var12);
               }

               profilerFiller.pop();
            }
         } catch (IOException var13) {
            ;
         }

         profilerFiller.pop();
      }

      profilerFiller.endTick();
      return soundManager$Preparations;
   }

   protected void apply(SoundManager.Preparations soundManager$Preparations, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      soundManager$Preparations.apply(this.registry, this.soundEngine);

      for(ResourceLocation var5 : this.registry.keySet()) {
         WeighedSoundEvents var6 = (WeighedSoundEvents)this.registry.get(var5);
         if(var6.getSubtitle() instanceof TranslatableComponent) {
            String var7 = ((TranslatableComponent)var6.getSubtitle()).getKey();
            if(!I18n.exists(var7)) {
               LOGGER.debug("Missing subtitle {} for event: {}", var7, var5);
            }
         }
      }

      if(LOGGER.isDebugEnabled()) {
         for(ResourceLocation var5 : this.registry.keySet()) {
            if(!Registry.SOUND_EVENT.containsKey(var5)) {
               LOGGER.debug("Not having sound event for: {}", var5);
            }
         }
      }

      this.soundEngine.reload();
   }

   @Nullable
   protected static Map getEventFromJson(InputStream inputStream) {
      Map var1;
      try {
         var1 = (Map)GsonHelper.fromJson(GSON, (Reader)(new InputStreamReader(inputStream, StandardCharsets.UTF_8)), (Type)SOUND_EVENT_REGISTRATION_TYPE);
      } finally {
         IOUtils.closeQuietly(inputStream);
      }

      return var1;
   }

   private static boolean validateSoundResource(Sound sound, ResourceLocation resourceLocation, ResourceManager resourceManager) {
      ResourceLocation resourceLocation = sound.getPath();
      if(!resourceManager.hasResource(resourceLocation)) {
         LOGGER.warn("File {} does not exist, cannot add it to event {}", resourceLocation, resourceLocation);
         return false;
      } else {
         return true;
      }
   }

   @Nullable
   public WeighedSoundEvents getSoundEvent(ResourceLocation resourceLocation) {
      return (WeighedSoundEvents)this.registry.get(resourceLocation);
   }

   public Collection getAvailableSounds() {
      return this.registry.keySet();
   }

   public void play(SoundInstance soundInstance) {
      this.soundEngine.play(soundInstance);
   }

   public void playDelayed(SoundInstance soundInstance, int var2) {
      this.soundEngine.playDelayed(soundInstance, var2);
   }

   public void updateSource(Camera camera) {
      this.soundEngine.updateSource(camera);
   }

   public void pause() {
      this.soundEngine.pause();
   }

   public void stop() {
      this.soundEngine.stopAll();
   }

   public void destroy() {
      this.soundEngine.destroy();
   }

   public void tick(boolean b) {
      this.soundEngine.tick(b);
   }

   public void resume() {
      this.soundEngine.resume();
   }

   public void updateSourceVolume(SoundSource soundSource, float var2) {
      if(soundSource == SoundSource.MASTER && var2 <= 0.0F) {
         this.stop();
      }

      this.soundEngine.updateCategoryVolume(soundSource, var2);
   }

   public void stop(SoundInstance soundInstance) {
      this.soundEngine.stop(soundInstance);
   }

   public boolean isActive(SoundInstance soundInstance) {
      return this.soundEngine.isActive(soundInstance);
   }

   public void addListener(SoundEventListener soundEventListener) {
      this.soundEngine.addEventListener(soundEventListener);
   }

   public void removeListener(SoundEventListener soundEventListener) {
      this.soundEngine.removeEventListener(soundEventListener);
   }

   public void stop(@Nullable ResourceLocation resourceLocation, @Nullable SoundSource soundSource) {
      this.soundEngine.stop(resourceLocation, soundSource);
   }

   public String getDebugString() {
      return this.soundEngine.getDebugString();
   }

   // $FF: synthetic method
   protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }

   @ClientJarOnly
   public static class Preparations {
      private final Map registry = Maps.newHashMap();

      private void handleRegistration(ResourceLocation resourceLocation, SoundEventRegistration soundEventRegistration, ResourceManager resourceManager) {
         WeighedSoundEvents var4 = (WeighedSoundEvents)this.registry.get(resourceLocation);
         boolean var5 = var4 == null;
         if(var5 || soundEventRegistration.isReplace()) {
            if(!var5) {
               SoundManager.LOGGER.debug("Replaced sound event location {}", resourceLocation);
            }

            var4 = new WeighedSoundEvents(resourceLocation, soundEventRegistration.getSubtitle());
            this.registry.put(resourceLocation, var4);
         }

         for(final Sound var7 : soundEventRegistration.getSounds()) {
            final ResourceLocation var8 = var7.getLocation();
            Weighted<Sound> var9;
            switch(var7.getType()) {
            case FILE:
               if(!SoundManager.validateSoundResource(var7, resourceLocation, resourceManager)) {
                  continue;
               }

               var9 = var7;
               break;
            case SOUND_EVENT:
               var9 = new Weighted() {
                  public int getWeight() {
                     WeighedSoundEvents var1 = (WeighedSoundEvents)Preparations.this.registry.get(var8);
                     return var1 == null?0:var1.getWeight();
                  }

                  public Sound getSound() {
                     WeighedSoundEvents var1 = (WeighedSoundEvents)Preparations.this.registry.get(var8);
                     if(var1 == null) {
                        return SoundManager.EMPTY_SOUND;
                     } else {
                        Sound var2 = var1.getSound();
                        return new Sound(var2.getLocation().toString(), var2.getVolume() * var7.getVolume(), var2.getPitch() * var7.getPitch(), var7.getWeight(), Sound.Type.FILE, var2.shouldStream() || var7.shouldStream(), var2.shouldPreload(), var2.getAttenuationDistance());
                     }
                  }

                  public void preloadIfRequired(SoundEngine soundEngine) {
                     WeighedSoundEvents var2 = (WeighedSoundEvents)Preparations.this.registry.get(var8);
                     if(var2 != null) {
                        var2.preloadIfRequired(soundEngine);
                     }
                  }

                  // $FF: synthetic method
                  public Object getSound() {
                     return this.getSound();
                  }
               };
               break;
            default:
               throw new IllegalStateException("Unknown SoundEventRegistration type: " + var7.getType());
            }

            var4.addSound(var9);
         }

      }

      public void apply(Map map, SoundEngine soundEngine) {
         map.clear();

         for(Entry<ResourceLocation, WeighedSoundEvents> var4 : this.registry.entrySet()) {
            map.put(var4.getKey(), var4.getValue());
            ((WeighedSoundEvents)var4.getValue()).preloadIfRequired(soundEngine);
         }

      }
   }
}
