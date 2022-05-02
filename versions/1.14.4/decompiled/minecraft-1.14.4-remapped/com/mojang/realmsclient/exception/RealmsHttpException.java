package com.mojang.realmsclient.exception;

import com.fox2code.repacker.ClientJarOnly;

@ClientJarOnly
public class RealmsHttpException extends RuntimeException {
   public RealmsHttpException(String string, Exception exception) {
      super(string, exception);
   }
}
