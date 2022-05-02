package net.minecraft.client.renderer.chunk;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.List;
import net.minecraft.client.renderer.chunk.VisibilitySet;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.block.entity.BlockEntity;

@ClientJarOnly
public class CompiledChunk {
   public static final CompiledChunk UNCOMPILED = new CompiledChunk() {
      protected void setChanged(BlockLayer changed) {
         throw new UnsupportedOperationException();
      }

      public void layerIsPresent(BlockLayer blockLayer) {
         throw new UnsupportedOperationException();
      }

      public boolean facesCanSeeEachother(Direction var1, Direction var2) {
         return false;
      }
   };
   private final boolean[] hasBlocks = new boolean[BlockLayer.values().length];
   private final boolean[] hasLayer = new boolean[BlockLayer.values().length];
   private boolean isCompletelyEmpty = true;
   private final List renderableBlockEntities = Lists.newArrayList();
   private VisibilitySet visibilitySet = new VisibilitySet();
   private BufferBuilder.State transparencyState;

   public boolean hasNoRenderableLayers() {
      return this.isCompletelyEmpty;
   }

   protected void setChanged(BlockLayer changed) {
      this.isCompletelyEmpty = false;
      this.hasBlocks[changed.ordinal()] = true;
   }

   public boolean isEmpty(BlockLayer blockLayer) {
      return !this.hasBlocks[blockLayer.ordinal()];
   }

   public void layerIsPresent(BlockLayer blockLayer) {
      this.hasLayer[blockLayer.ordinal()] = true;
   }

   public boolean hasLayer(BlockLayer blockLayer) {
      return this.hasLayer[blockLayer.ordinal()];
   }

   public List getRenderableBlockEntities() {
      return this.renderableBlockEntities;
   }

   public void addRenderableBlockEntity(BlockEntity blockEntity) {
      this.renderableBlockEntities.add(blockEntity);
   }

   public boolean facesCanSeeEachother(Direction var1, Direction var2) {
      return this.visibilitySet.visibilityBetween(var1, var2);
   }

   public void setVisibilitySet(VisibilitySet visibilitySet) {
      this.visibilitySet = visibilitySet;
   }

   public BufferBuilder.State getTransparencyState() {
      return this.transparencyState;
   }

   public void setTransparencyState(BufferBuilder.State transparencyState) {
      this.transparencyState = transparencyState;
   }
}
