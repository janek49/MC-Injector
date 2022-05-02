package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.realmsclient.dto.ValueObject;

@ClientJarOnly
public class RealmsWorldResetDto extends ValueObject {
   private final String seed;
   private final long worldTemplateId;
   private final int levelType;
   private final boolean generateStructures;

   public RealmsWorldResetDto(String seed, long worldTemplateId, int levelType, boolean generateStructures) {
      this.seed = seed;
      this.worldTemplateId = worldTemplateId;
      this.levelType = levelType;
      this.generateStructures = generateStructures;
   }
}
