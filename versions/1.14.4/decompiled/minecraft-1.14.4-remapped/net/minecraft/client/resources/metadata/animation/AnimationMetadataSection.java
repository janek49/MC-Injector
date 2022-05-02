package net.minecraft.client.resources.metadata.animation;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSectionSerializer;

@ClientJarOnly
public class AnimationMetadataSection {
   public static final AnimationMetadataSectionSerializer SERIALIZER = new AnimationMetadataSectionSerializer();
   private final List frames;
   private final int frameWidth;
   private final int frameHeight;
   private final int defaultFrameTime;
   private final boolean interpolatedFrames;

   public AnimationMetadataSection(List frames, int frameWidth, int frameHeight, int defaultFrameTime, boolean interpolatedFrames) {
      this.frames = frames;
      this.frameWidth = frameWidth;
      this.frameHeight = frameHeight;
      this.defaultFrameTime = defaultFrameTime;
      this.interpolatedFrames = interpolatedFrames;
   }

   public int getFrameHeight() {
      return this.frameHeight;
   }

   public int getFrameWidth() {
      return this.frameWidth;
   }

   public int getFrameCount() {
      return this.frames.size();
   }

   public int getDefaultFrameTime() {
      return this.defaultFrameTime;
   }

   public boolean isInterpolatedFrames() {
      return this.interpolatedFrames;
   }

   private AnimationFrame getFrame(int i) {
      return (AnimationFrame)this.frames.get(i);
   }

   public int getFrameTime(int i) {
      AnimationFrame var2 = this.getFrame(i);
      return var2.isTimeUnknown()?this.defaultFrameTime:var2.getTime();
   }

   public int getFrameIndex(int i) {
      return ((AnimationFrame)this.frames.get(i)).getIndex();
   }

   public Set getUniqueFrameIndices() {
      Set<Integer> set = Sets.newHashSet();

      for(AnimationFrame var3 : this.frames) {
         set.add(Integer.valueOf(var3.getIndex()));
      }

      return set;
   }
}
