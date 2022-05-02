package com.mojang.realmsclient.exception;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.realmsclient.client.RealmsError;
import net.minecraft.realms.RealmsScreen;

@ClientJarOnly
public class RealmsServiceException extends Exception {
   public final int httpResultCode;
   public final String httpResponseContent;
   public final int errorCode;
   public final String errorMsg;

   public RealmsServiceException(int httpResultCode, String httpResponseContent, RealmsError realmsError) {
      super(httpResponseContent);
      this.httpResultCode = httpResultCode;
      this.httpResponseContent = httpResponseContent;
      this.errorCode = realmsError.getErrorCode();
      this.errorMsg = realmsError.getErrorMessage();
   }

   public RealmsServiceException(int httpResultCode, String httpResponseContent, int errorCode, String errorMsg) {
      super(httpResponseContent);
      this.httpResultCode = httpResultCode;
      this.httpResponseContent = httpResponseContent;
      this.errorCode = errorCode;
      this.errorMsg = errorMsg;
   }

   public String toString() {
      if(this.errorCode == -1) {
         return "Realms (" + this.httpResultCode + ") " + this.httpResponseContent;
      } else {
         String string = "mco.errorMessage." + this.errorCode;
         String var2 = RealmsScreen.getLocalizedString(string);
         return (var2.equals(string)?this.errorMsg:var2) + " - " + this.errorCode;
      }
   }
}
