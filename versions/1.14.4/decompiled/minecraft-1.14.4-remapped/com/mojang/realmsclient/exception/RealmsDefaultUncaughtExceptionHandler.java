package com.mojang.realmsclient.exception;

import com.fox2code.repacker.ClientJarOnly;
import java.lang.Thread.UncaughtExceptionHandler;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsDefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {
   private final Logger logger;

   public RealmsDefaultUncaughtExceptionHandler(Logger logger) {
      this.logger = logger;
   }

   public void uncaughtException(Thread thread, Throwable throwable) {
      this.logger.error("Caught previously unhandled exception :");
      this.logger.error(throwable);
   }
}
