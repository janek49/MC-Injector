package com.mojang.realmsclient.gui.screens;

import com.fox2code.repacker.ClientJarOnly;

@ClientJarOnly
public class UploadResult {
   public final int statusCode;
   public final String errorMessage;

   public UploadResult(int statusCode, String errorMessage) {
      this.statusCode = statusCode;
      this.errorMessage = errorMessage;
   }

   @ClientJarOnly
   public static class Builder {
      private int statusCode = -1;
      private String errorMessage = null;

      public UploadResult.Builder withStatusCode(int statusCode) {
         this.statusCode = statusCode;
         return this;
      }

      public UploadResult.Builder withErrorMessage(String errorMessage) {
         this.errorMessage = errorMessage;
         return this;
      }

      public UploadResult build() {
         return new UploadResult(this.statusCode, this.errorMessage);
      }
   }
}
