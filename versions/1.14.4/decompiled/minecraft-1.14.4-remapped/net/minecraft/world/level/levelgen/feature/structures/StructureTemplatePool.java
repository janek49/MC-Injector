package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.GravityProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class StructureTemplatePool {
   public static final StructureTemplatePool EMPTY = new StructureTemplatePool(new ResourceLocation("empty"), new ResourceLocation("empty"), ImmutableList.of(), StructureTemplatePool.Projection.RIGID);
   public static final StructureTemplatePool INVALID = new StructureTemplatePool(new ResourceLocation("invalid"), new ResourceLocation("invalid"), ImmutableList.of(), StructureTemplatePool.Projection.RIGID);
   private final ResourceLocation name;
   private final ImmutableList rawTemplates;
   private final List templates;
   private final ResourceLocation fallback;
   private final StructureTemplatePool.Projection projection;
   private int maxSize = Integer.MIN_VALUE;

   public StructureTemplatePool(ResourceLocation name, ResourceLocation fallback, List list, StructureTemplatePool.Projection projection) {
      this.name = name;
      this.rawTemplates = ImmutableList.copyOf(list);
      this.templates = Lists.newArrayList();

      for(Pair<StructurePoolElement, Integer> var6 : list) {
         for(Integer var7 = Integer.valueOf(0); var7.intValue() < ((Integer)var6.getSecond()).intValue(); var7 = Integer.valueOf(var7.intValue() + 1)) {
            this.templates.add(((StructurePoolElement)var6.getFirst()).setProjection(projection));
         }
      }

      this.fallback = fallback;
      this.projection = projection;
   }

   public int getMaxSize(StructureManager structureManager) {
      if(this.maxSize == Integer.MIN_VALUE) {
         this.maxSize = this.templates.stream().mapToInt((structurePoolElement) -> {
            return structurePoolElement.getBoundingBox(structureManager, BlockPos.ZERO, Rotation.NONE).getYSpan();
         }).max().orElse(0);
      }

      return this.maxSize;
   }

   public ResourceLocation getFallback() {
      return this.fallback;
   }

   public StructurePoolElement getRandomTemplate(Random random) {
      return (StructurePoolElement)this.templates.get(random.nextInt(this.templates.size()));
   }

   public List getShuffledTemplates(Random random) {
      return ImmutableList.copyOf(ObjectArrays.shuffle(this.templates.toArray(new StructurePoolElement[0]), random));
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public int size() {
      return this.templates.size();
   }

   public static enum Projection {
      TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityProcessor(Heightmap.Types.WORLD_SURFACE_WG, -1))),
      RIGID("rigid", ImmutableList.of());

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(StructureTemplatePool.Projection::getName, (structureTemplatePool$Projection) -> {
         return structureTemplatePool$Projection;
      }));
      private final String name;
      private final ImmutableList processors;

      private Projection(String name, ImmutableList processors) {
         this.name = name;
         this.processors = processors;
      }

      public String getName() {
         return this.name;
      }

      public static StructureTemplatePool.Projection byName(String name) {
         return (StructureTemplatePool.Projection)BY_NAME.get(name);
      }

      public ImmutableList getProcessors() {
         return this.processors;
      }
   }
}
