package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;

@ClientJarOnly
public class ServerActivity extends ValueObject {
   public String profileUuid;
   public long joinTime;
   public long leaveTime;

   public static ServerActivity parse(JsonObject jsonObject) {
      ServerActivity serverActivity = new ServerActivity();

      try {
         serverActivity.profileUuid = JsonUtils.getStringOr("profileUuid", jsonObject, (String)null);
         serverActivity.joinTime = JsonUtils.getLongOr("joinTime", jsonObject, Long.MIN_VALUE);
         serverActivity.leaveTime = JsonUtils.getLongOr("leaveTime", jsonObject, Long.MIN_VALUE);
      } catch (Exception var3) {
         ;
      }

      return serverActivity;
   }
}
