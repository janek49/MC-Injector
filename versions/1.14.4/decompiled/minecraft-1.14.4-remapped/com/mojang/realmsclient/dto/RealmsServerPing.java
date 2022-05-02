package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.realmsclient.dto.ValueObject;

@ClientJarOnly
public class RealmsServerPing extends ValueObject {
   public volatile String nrOfPlayers = "0";
   public volatile String playerList = "";
}
