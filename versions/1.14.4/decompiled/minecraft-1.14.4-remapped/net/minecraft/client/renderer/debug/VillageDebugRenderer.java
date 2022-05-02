package net.minecraft.client.renderer.debug;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.PathfindingRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.DebugVillagerNameGenerator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class VillageDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final Map pois = Maps.newHashMap();
   private final Set villageSections = Sets.newHashSet();
   private final Map brainDumpsPerEntity = Maps.newHashMap();
   private UUID lastLookedAtUuid;

   public VillageDebugRenderer(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void clear() {
      this.pois.clear();
      this.villageSections.clear();
      this.brainDumpsPerEntity.clear();
      this.lastLookedAtUuid = null;
   }

   public void addPoi(VillageDebugRenderer.PoiInfo villageDebugRenderer$PoiInfo) {
      this.pois.put(villageDebugRenderer$PoiInfo.pos, villageDebugRenderer$PoiInfo);
   }

   public void removePoi(BlockPos blockPos) {
      this.pois.remove(blockPos);
   }

   public void setFreeTicketCount(BlockPos blockPos, int var2) {
      VillageDebugRenderer.PoiInfo var3 = (VillageDebugRenderer.PoiInfo)this.pois.get(blockPos);
      if(var3 == null) {
         LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: " + blockPos);
      } else {
         var3.freeTicketCount = var2;
      }
   }

   public void setVillageSection(SectionPos villageSection) {
      this.villageSections.add(villageSection);
   }

   public void setNotVillageSection(SectionPos notVillageSection) {
      this.villageSections.remove(notVillageSection);
   }

   public void addOrUpdateBrainDump(VillageDebugRenderer.BrainDump villageDebugRenderer$BrainDump) {
      this.brainDumpsPerEntity.put(villageDebugRenderer$BrainDump.uuid, villageDebugRenderer$BrainDump);
   }

   public void render(long l) {
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture();
      this.doRender();
      GlStateManager.enableTexture();
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
      if(!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }

   }

   private void doRender() {
      BlockPos var1 = this.getCamera().getBlockPosition();
      this.villageSections.forEach((sectionPos) -> {
         if(var1.closerThan(sectionPos.center(), 60.0D)) {
            highlightVillageSection(sectionPos);
         }

      });
      this.brainDumpsPerEntity.values().forEach((villageDebugRenderer$BrainDump) -> {
         if(this.isPlayerCloseEnoughToMob(villageDebugRenderer$BrainDump)) {
            this.renderVillagerInfo(villageDebugRenderer$BrainDump);
         }

      });

      for(BlockPos var3 : this.pois.keySet()) {
         if(var1.closerThan(var3, 30.0D)) {
            highlightPoi(var3);
         }
      }

      this.pois.values().forEach((villageDebugRenderer$PoiInfo) -> {
         if(var1.closerThan(villageDebugRenderer$PoiInfo.pos, 30.0D)) {
            this.renderPoiInfo(villageDebugRenderer$PoiInfo);
         }

      });
      this.getGhostPois().forEach((var2, list) -> {
         if(var1.closerThan(var2, 30.0D)) {
            this.renderGhostPoi(var2, list);
         }

      });
   }

   private static void highlightVillageSection(SectionPos sectionPos) {
      float var1 = 1.0F;
      BlockPos var2 = sectionPos.center();
      BlockPos var3 = var2.offset(-1.0D, -1.0D, -1.0D);
      BlockPos var4 = var2.offset(1.0D, 1.0D, 1.0D);
      DebugRenderer.renderFilledBox(var3, var4, 0.2F, 1.0F, 0.2F, 0.15F);
   }

   private static void highlightPoi(BlockPos blockPos) {
      float var1 = 0.05F;
      DebugRenderer.renderFilledBox(blockPos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void renderGhostPoi(BlockPos blockPos, List list) {
      float var3 = 0.05F;
      DebugRenderer.renderFilledBox(blockPos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      renderTextOverPos("" + list, blockPos, 0, -256);
      renderTextOverPos("Ghost POI", blockPos, 1, -65536);
   }

   private void renderPoiInfo(VillageDebugRenderer.PoiInfo villageDebugRenderer$PoiInfo) {
      int var2 = 0;
      if(this.getTicketHolderNames(villageDebugRenderer$PoiInfo).size() < 4) {
         renderTextOverPoi("" + this.getTicketHolderNames(villageDebugRenderer$PoiInfo), villageDebugRenderer$PoiInfo, var2, -256);
      } else {
         renderTextOverPoi("" + this.getTicketHolderNames(villageDebugRenderer$PoiInfo).size() + " ticket holders", villageDebugRenderer$PoiInfo, var2, -256);
      }

      ++var2;
      renderTextOverPoi("Free tickets: " + villageDebugRenderer$PoiInfo.freeTicketCount, villageDebugRenderer$PoiInfo, var2, -256);
      ++var2;
      renderTextOverPoi(villageDebugRenderer$PoiInfo.type, villageDebugRenderer$PoiInfo, var2, -1);
   }

   private void renderPath(VillageDebugRenderer.BrainDump villageDebugRenderer$BrainDump) {
      if(villageDebugRenderer$BrainDump.path != null) {
         PathfindingRenderer.renderPath(this.getCamera(), villageDebugRenderer$BrainDump.path, 0.5F, false, false);
      }

   }

   private void renderVillagerInfo(VillageDebugRenderer.BrainDump villageDebugRenderer$BrainDump) {
      boolean var2 = this.isVillagerSelected(villageDebugRenderer$BrainDump);
      int var3 = 0;
      renderTextOverMob(villageDebugRenderer$BrainDump.pos, var3, villageDebugRenderer$BrainDump.name, -1, 0.03F);
      ++var3;
      if(var2) {
         renderTextOverMob(villageDebugRenderer$BrainDump.pos, var3, villageDebugRenderer$BrainDump.profession + " " + villageDebugRenderer$BrainDump.xp + "xp", -1, 0.02F);
         ++var3;
      }

      if(var2 && !villageDebugRenderer$BrainDump.inventory.equals("")) {
         renderTextOverMob(villageDebugRenderer$BrainDump.pos, var3, villageDebugRenderer$BrainDump.inventory, -98404, 0.02F);
         ++var3;
      }

      if(var2) {
         for(String var5 : villageDebugRenderer$BrainDump.behaviors) {
            renderTextOverMob(villageDebugRenderer$BrainDump.pos, var3, var5, -16711681, 0.02F);
            ++var3;
         }
      }

      if(var2) {
         for(String var5 : villageDebugRenderer$BrainDump.activities) {
            renderTextOverMob(villageDebugRenderer$BrainDump.pos, var3, var5, -16711936, 0.02F);
            ++var3;
         }
      }

      if(villageDebugRenderer$BrainDump.wantsGolem) {
         renderTextOverMob(villageDebugRenderer$BrainDump.pos, var3, "Wants Golem", -23296, 0.02F);
         ++var3;
      }

      if(var2) {
         for(String var5 : villageDebugRenderer$BrainDump.gossips) {
            if(var5.startsWith(villageDebugRenderer$BrainDump.name)) {
               renderTextOverMob(villageDebugRenderer$BrainDump.pos, var3, var5, -1, 0.02F);
            } else {
               renderTextOverMob(villageDebugRenderer$BrainDump.pos, var3, var5, -23296, 0.02F);
            }

            ++var3;
         }
      }

      if(var2) {
         for(String var5 : Lists.reverse(villageDebugRenderer$BrainDump.memories)) {
            renderTextOverMob(villageDebugRenderer$BrainDump.pos, var3, var5, -3355444, 0.02F);
            ++var3;
         }
      }

      if(var2) {
         this.renderPath(villageDebugRenderer$BrainDump);
      }

   }

   private static void renderTextOverPoi(String string, VillageDebugRenderer.PoiInfo villageDebugRenderer$PoiInfo, int var2, int var3) {
      BlockPos var4 = villageDebugRenderer$PoiInfo.pos;
      renderTextOverPos(string, var4, var2, var3);
   }

   private static void renderTextOverPos(String string, BlockPos blockPos, int var2, int var3) {
      double var4 = 1.3D;
      double var6 = 0.2D;
      double var8 = (double)blockPos.getX() + 0.5D;
      double var10 = (double)blockPos.getY() + 1.3D + (double)var2 * 0.2D;
      double var12 = (double)blockPos.getZ() + 0.5D;
      DebugRenderer.renderFloatingText(string, var8, var10, var12, var3, 0.02F, true, 0.0F, true);
   }

   private static void renderTextOverMob(Position position, int var1, String string, int var3, float var4) {
      double var5 = 2.4D;
      double var7 = 0.25D;
      BlockPos var9 = new BlockPos(position);
      double var10 = (double)var9.getX() + 0.5D;
      double var12 = position.y() + 2.4D + (double)var1 * 0.25D;
      double var14 = (double)var9.getZ() + 0.5D;
      float var16 = 0.5F;
      DebugRenderer.renderFloatingText(string, var10, var12, var14, var3, var4, false, 0.5F, true);
   }

   private Camera getCamera() {
      return this.minecraft.gameRenderer.getMainCamera();
   }

   private Set getTicketHolderNames(VillageDebugRenderer.PoiInfo villageDebugRenderer$PoiInfo) {
      return (Set)this.getTicketHolders(villageDebugRenderer$PoiInfo.pos).stream().map(DebugVillagerNameGenerator::getVillagerName).collect(Collectors.toSet());
   }

   private boolean isVillagerSelected(VillageDebugRenderer.BrainDump villageDebugRenderer$BrainDump) {
      return Objects.equals(this.lastLookedAtUuid, villageDebugRenderer$BrainDump.uuid);
   }

   private boolean isPlayerCloseEnoughToMob(VillageDebugRenderer.BrainDump villageDebugRenderer$BrainDump) {
      Player var2 = this.minecraft.player;
      BlockPos var3 = new BlockPos(var2.x, villageDebugRenderer$BrainDump.pos.y(), var2.z);
      BlockPos var4 = new BlockPos(villageDebugRenderer$BrainDump.pos);
      return var3.closerThan(var4, 30.0D);
   }

   private Collection getTicketHolders(BlockPos blockPos) {
      return (Collection)this.brainDumpsPerEntity.values().stream().filter((villageDebugRenderer$BrainDump) -> {
         return villageDebugRenderer$BrainDump.hasPoi(blockPos);
      }).map(VillageDebugRenderer.BrainDump::getUuid).collect(Collectors.toSet());
   }

   private Map getGhostPois() {
      Map<BlockPos, List<String>> map = Maps.newHashMap();

      for(VillageDebugRenderer.BrainDump var3 : this.brainDumpsPerEntity.values()) {
         for(BlockPos var5 : var3.pois) {
            if(!this.pois.containsKey(var5)) {
               List<String> var6 = (List)map.get(var5);
               if(var6 == null) {
                  var6 = Lists.newArrayList();
                  map.put(var5, var6);
               }

               var6.add(var3.name);
            }
         }
      }

      return map;
   }

   private void updateLastLookedAtUuid() {
      DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent((entity) -> {
         this.lastLookedAtUuid = entity.getUUID();
      });
   }

   @ClientJarOnly
   public static class BrainDump {
      public final UUID uuid;
      public final int id;
      public final String name;
      public final String profession;
      public final int xp;
      public final Position pos;
      public final String inventory;
      public final Path path;
      public final boolean wantsGolem;
      public final List activities = Lists.newArrayList();
      public final List behaviors = Lists.newArrayList();
      public final List memories = Lists.newArrayList();
      public final List gossips = Lists.newArrayList();
      public final Set pois = Sets.newHashSet();

      public BrainDump(UUID uuid, int id, String name, String profession, int xp, Position pos, String inventory, @Nullable Path path, boolean wantsGolem) {
         this.uuid = uuid;
         this.id = id;
         this.name = name;
         this.profession = profession;
         this.xp = xp;
         this.pos = pos;
         this.inventory = inventory;
         this.path = path;
         this.wantsGolem = wantsGolem;
      }

      private boolean hasPoi(BlockPos blockPos) {
         Stream var10000 = this.pois.stream();
         blockPos.getClass();
         return var10000.anyMatch(blockPos::equals);
      }

      public UUID getUuid() {
         return this.uuid;
      }
   }

   @ClientJarOnly
   public static class PoiInfo {
      public final BlockPos pos;
      public String type;
      public int freeTicketCount;

      public PoiInfo(BlockPos pos, String type, int freeTicketCount) {
         this.pos = pos;
         this.type = type;
         this.freeTicketCount = freeTicketCount;
      }
   }
}
