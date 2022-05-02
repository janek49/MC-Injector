package net.minecraft.server.level.progress;

import net.minecraft.server.level.progress.ChunkProgressListener;

public interface ChunkProgressListenerFactory {
   ChunkProgressListener create(int var1);
}
