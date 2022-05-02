package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerAdvancements {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer()).registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).setPrettyPrinting().create();
   private static final TypeToken TYPE_TOKEN = new TypeToken() {
   };
   private final MinecraftServer server;
   private final File file;
   private final Map advancements = Maps.newLinkedHashMap();
   private final Set visible = Sets.newLinkedHashSet();
   private final Set visibilityChanged = Sets.newLinkedHashSet();
   private final Set progressChanged = Sets.newLinkedHashSet();
   private ServerPlayer player;
   @Nullable
   private Advancement lastSelectedTab;
   private boolean isFirstPacket = true;

   public PlayerAdvancements(MinecraftServer server, File file, ServerPlayer player) {
      this.server = server;
      this.file = file;
      this.player = player;
      this.load();
   }

   public void setPlayer(ServerPlayer player) {
      this.player = player;
   }

   public void stopListening() {
      for(CriterionTrigger<?> var2 : CriteriaTriggers.all()) {
         var2.removePlayerListeners(this);
      }

   }

   public void reload() {
      this.stopListening();
      this.advancements.clear();
      this.visible.clear();
      this.visibilityChanged.clear();
      this.progressChanged.clear();
      this.isFirstPacket = true;
      this.lastSelectedTab = null;
      this.load();
   }

   private void registerListeners() {
      for(Advancement var2 : this.server.getAdvancements().getAllAdvancements()) {
         this.registerListeners(var2);
      }

   }

   private void ensureAllVisible() {
      List<Advancement> var1 = Lists.newArrayList();

      for(Entry<Advancement, AdvancementProgress> var3 : this.advancements.entrySet()) {
         if(((AdvancementProgress)var3.getValue()).isDone()) {
            var1.add(var3.getKey());
            this.progressChanged.add(var3.getKey());
         }
      }

      for(Advancement var3 : var1) {
         this.ensureVisibility(var3);
      }

   }

   private void checkForAutomaticTriggers() {
      for(Advancement var2 : this.server.getAdvancements().getAllAdvancements()) {
         if(var2.getCriteria().isEmpty()) {
            this.award(var2, "");
            var2.getRewards().grant(this.player);
         }
      }

   }

   private void load() {
      if(this.file.isFile()) {
         try {
            JsonReader var1 = new JsonReader(new StringReader(Files.toString(this.file, StandardCharsets.UTF_8)));
            Throwable var2 = null;

            try {
               var1.setLenient(false);
               Dynamic<JsonElement> var3 = new Dynamic(JsonOps.INSTANCE, Streams.parse(var1));
               if(!var3.get("DataVersion").asNumber().isPresent()) {
                  var3 = var3.set("DataVersion", var3.createInt(1343));
               }

               var3 = this.server.getFixerUpper().update(DataFixTypes.ADVANCEMENTS.getType(), var3, var3.get("DataVersion").asInt(0), SharedConstants.getCurrentVersion().getWorldVersion());
               var3 = var3.remove("DataVersion");
               Map<ResourceLocation, AdvancementProgress> var4 = (Map)GSON.getAdapter(TYPE_TOKEN).fromJsonTree((JsonElement)var3.getValue());
               if(var4 == null) {
                  throw new JsonParseException("Found null for advancements");
               }

               Stream<Entry<ResourceLocation, AdvancementProgress>> var5 = var4.entrySet().stream().sorted(Comparator.comparing(Entry::getValue));

               for(Entry<ResourceLocation, AdvancementProgress> var7 : (List)var5.collect(Collectors.toList())) {
                  Advancement var8 = this.server.getAdvancements().getAdvancement((ResourceLocation)var7.getKey());
                  if(var8 == null) {
                     LOGGER.warn("Ignored advancement \'{}\' in progress file {} - it doesn\'t exist anymore?", var7.getKey(), this.file);
                  } else {
                     this.startProgress(var8, (AdvancementProgress)var7.getValue());
                  }
               }
            } catch (Throwable var18) {
               var2 = var18;
               throw var18;
            } finally {
               if(var1 != null) {
                  if(var2 != null) {
                     try {
                        var1.close();
                     } catch (Throwable var17) {
                        var2.addSuppressed(var17);
                     }
                  } else {
                     var1.close();
                  }
               }

            }
         } catch (JsonParseException var20) {
            LOGGER.error("Couldn\'t parse player advancements in {}", this.file, var20);
         } catch (IOException var21) {
            LOGGER.error("Couldn\'t access player advancements in {}", this.file, var21);
         }
      }

      this.checkForAutomaticTriggers();
      this.ensureAllVisible();
      this.registerListeners();
   }

   public void save() {
      Map<ResourceLocation, AdvancementProgress> var1 = Maps.newHashMap();

      for(Entry<Advancement, AdvancementProgress> var3 : this.advancements.entrySet()) {
         AdvancementProgress var4 = (AdvancementProgress)var3.getValue();
         if(var4.hasProgress()) {
            var1.put(((Advancement)var3.getKey()).getId(), var4);
         }
      }

      if(this.file.getParentFile() != null) {
         this.file.getParentFile().mkdirs();
      }

      JsonElement var2 = GSON.toJsonTree(var1);
      var2.getAsJsonObject().addProperty("DataVersion", Integer.valueOf(SharedConstants.getCurrentVersion().getWorldVersion()));

      try {
         OutputStream var3 = new FileOutputStream(this.file);
         Throwable var38 = null;

         try {
            Writer var5 = new OutputStreamWriter(var3, Charsets.UTF_8.newEncoder());
            Throwable var6 = null;

            try {
               GSON.toJson(var2, var5);
            } catch (Throwable var31) {
               var6 = var31;
               throw var31;
            } finally {
               if(var5 != null) {
                  if(var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var30) {
                        var6.addSuppressed(var30);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } catch (Throwable var33) {
            var38 = var33;
            throw var33;
         } finally {
            if(var3 != null) {
               if(var38 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var29) {
                     var38.addSuppressed(var29);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (IOException var35) {
         LOGGER.error("Couldn\'t save player advancements to {}", this.file, var35);
      }

   }

   public boolean award(Advancement advancement, String string) {
      boolean var3 = false;
      AdvancementProgress var4 = this.getOrStartProgress(advancement);
      boolean var5 = var4.isDone();
      if(var4.grantProgress(string)) {
         this.unregisterListeners(advancement);
         this.progressChanged.add(advancement);
         var3 = true;
         if(!var5 && var4.isDone()) {
            advancement.getRewards().grant(this.player);
            if(advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceChat() && this.player.level.getGameRules().getBoolean(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)) {
               this.server.getPlayerList().broadcastMessage(new TranslatableComponent("chat.type.advancement." + advancement.getDisplay().getFrame().getName(), new Object[]{this.player.getDisplayName(), advancement.getChatComponent()}));
            }
         }
      }

      if(var4.isDone()) {
         this.ensureVisibility(advancement);
      }

      return var3;
   }

   public boolean revoke(Advancement advancement, String string) {
      boolean var3 = false;
      AdvancementProgress var4 = this.getOrStartProgress(advancement);
      if(var4.revokeProgress(string)) {
         this.registerListeners(advancement);
         this.progressChanged.add(advancement);
         var3 = true;
      }

      if(!var4.hasProgress()) {
         this.ensureVisibility(advancement);
      }

      return var3;
   }

   private void registerListeners(Advancement advancement) {
      AdvancementProgress var2 = this.getOrStartProgress(advancement);
      if(!var2.isDone()) {
         for(Entry<String, Criterion> var4 : advancement.getCriteria().entrySet()) {
            CriterionProgress var5 = var2.getCriterion((String)var4.getKey());
            if(var5 != null && !var5.isDone()) {
               CriterionTriggerInstance var6 = ((Criterion)var4.getValue()).getTrigger();
               if(var6 != null) {
                  CriterionTrigger<CriterionTriggerInstance> var7 = CriteriaTriggers.getCriterion(var6.getCriterion());
                  if(var7 != null) {
                     var7.addPlayerListener(this, new CriterionTrigger.Listener(var6, advancement, (String)var4.getKey()));
                  }
               }
            }
         }

      }
   }

   private void unregisterListeners(Advancement advancement) {
      AdvancementProgress var2 = this.getOrStartProgress(advancement);

      for(Entry<String, Criterion> var4 : advancement.getCriteria().entrySet()) {
         CriterionProgress var5 = var2.getCriterion((String)var4.getKey());
         if(var5 != null && (var5.isDone() || var2.isDone())) {
            CriterionTriggerInstance var6 = ((Criterion)var4.getValue()).getTrigger();
            if(var6 != null) {
               CriterionTrigger<CriterionTriggerInstance> var7 = CriteriaTriggers.getCriterion(var6.getCriterion());
               if(var7 != null) {
                  var7.removePlayerListener(this, new CriterionTrigger.Listener(var6, advancement, (String)var4.getKey()));
               }
            }
         }
      }

   }

   public void flushDirty(ServerPlayer serverPlayer) {
      if(this.isFirstPacket || !this.visibilityChanged.isEmpty() || !this.progressChanged.isEmpty()) {
         Map<ResourceLocation, AdvancementProgress> var2 = Maps.newHashMap();
         Set<Advancement> var3 = Sets.newLinkedHashSet();
         Set<ResourceLocation> var4 = Sets.newLinkedHashSet();

         for(Advancement var6 : this.progressChanged) {
            if(this.visible.contains(var6)) {
               var2.put(var6.getId(), this.advancements.get(var6));
            }
         }

         for(Advancement var6 : this.visibilityChanged) {
            if(this.visible.contains(var6)) {
               var3.add(var6);
            } else {
               var4.add(var6.getId());
            }
         }

         if(this.isFirstPacket || !var2.isEmpty() || !var3.isEmpty() || !var4.isEmpty()) {
            serverPlayer.connection.send(new ClientboundUpdateAdvancementsPacket(this.isFirstPacket, var3, var4, var2));
            this.visibilityChanged.clear();
            this.progressChanged.clear();
         }
      }

      this.isFirstPacket = false;
   }

   public void setSelectedTab(@Nullable Advancement selectedTab) {
      Advancement advancement = this.lastSelectedTab;
      if(selectedTab != null && selectedTab.getParent() == null && selectedTab.getDisplay() != null) {
         this.lastSelectedTab = selectedTab;
      } else {
         this.lastSelectedTab = null;
      }

      if(advancement != this.lastSelectedTab) {
         this.player.connection.send(new ClientboundSelectAdvancementsTabPacket(this.lastSelectedTab == null?null:this.lastSelectedTab.getId()));
      }

   }

   public AdvancementProgress getOrStartProgress(Advancement advancement) {
      AdvancementProgress advancementProgress = (AdvancementProgress)this.advancements.get(advancement);
      if(advancementProgress == null) {
         advancementProgress = new AdvancementProgress();
         this.startProgress(advancement, advancementProgress);
      }

      return advancementProgress;
   }

   private void startProgress(Advancement advancement, AdvancementProgress advancementProgress) {
      advancementProgress.update(advancement.getCriteria(), advancement.getRequirements());
      this.advancements.put(advancement, advancementProgress);
   }

   private void ensureVisibility(Advancement advancement) {
      boolean var2 = this.shouldBeVisible(advancement);
      boolean var3 = this.visible.contains(advancement);
      if(var2 && !var3) {
         this.visible.add(advancement);
         this.visibilityChanged.add(advancement);
         if(this.advancements.containsKey(advancement)) {
            this.progressChanged.add(advancement);
         }
      } else if(!var2 && var3) {
         this.visible.remove(advancement);
         this.visibilityChanged.add(advancement);
      }

      if(var2 != var3 && advancement.getParent() != null) {
         this.ensureVisibility(advancement.getParent());
      }

      for(Advancement var5 : advancement.getChildren()) {
         this.ensureVisibility(var5);
      }

   }

   private boolean shouldBeVisible(Advancement advancement) {
      for(int var2 = 0; advancement != null && var2 <= 2; ++var2) {
         if(var2 == 0 && this.hasCompletedChildrenOrSelf(advancement)) {
            return true;
         }

         if(advancement.getDisplay() == null) {
            return false;
         }

         AdvancementProgress var3 = this.getOrStartProgress(advancement);
         if(var3.isDone()) {
            return true;
         }

         if(advancement.getDisplay().isHidden()) {
            return false;
         }

         advancement = advancement.getParent();
      }

      return false;
   }

   private boolean hasCompletedChildrenOrSelf(Advancement advancement) {
      AdvancementProgress var2 = this.getOrStartProgress(advancement);
      if(var2.isDone()) {
         return true;
      } else {
         for(Advancement var4 : advancement.getChildren()) {
            if(this.hasCompletedChildrenOrSelf(var4)) {
               return true;
            }
         }

         return false;
      }
   }
}
