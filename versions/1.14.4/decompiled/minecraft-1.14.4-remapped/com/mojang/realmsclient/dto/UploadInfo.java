package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class UploadInfo extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   @Expose
   private boolean worldClosed;
   @Expose
   private String token = "";
   @Expose
   private String uploadEndpoint = "";
   private int port;

   public static UploadInfo parse(String string) {
      UploadInfo uploadInfo = new UploadInfo();

      try {
         JsonParser var2 = new JsonParser();
         JsonObject var3 = var2.parse(string).getAsJsonObject();
         uploadInfo.worldClosed = JsonUtils.getBooleanOr("worldClosed", var3, false);
         uploadInfo.token = JsonUtils.getStringOr("token", var3, (String)null);
         uploadInfo.uploadEndpoint = JsonUtils.getStringOr("uploadEndpoint", var3, (String)null);
         uploadInfo.port = JsonUtils.getIntOr("port", var3, 8080);
      } catch (Exception var4) {
         LOGGER.error("Could not parse UploadInfo: " + var4.getMessage());
      }

      return uploadInfo;
   }

   public String getToken() {
      return this.token;
   }

   public String getUploadEndpoint() {
      return this.uploadEndpoint;
   }

   public boolean isWorldClosed() {
      return this.worldClosed;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public int getPort() {
      return this.port;
   }
}
