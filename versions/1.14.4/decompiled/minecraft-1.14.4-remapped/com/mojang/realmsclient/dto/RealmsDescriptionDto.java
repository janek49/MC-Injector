package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.realmsclient.dto.ValueObject;

@ClientJarOnly
public class RealmsDescriptionDto extends ValueObject {
   public String name;
   public String description;

   public RealmsDescriptionDto(String name, String description) {
      this.name = name;
      this.description = description;
   }
}
