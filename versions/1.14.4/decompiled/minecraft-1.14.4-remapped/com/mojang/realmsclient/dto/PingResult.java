package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.ArrayList;
import java.util.List;

@ClientJarOnly
public class PingResult extends ValueObject {
   public List pingResults = new ArrayList();
   public List worldIds = new ArrayList();
}
