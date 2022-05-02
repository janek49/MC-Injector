package net.minecraft.server;

import java.io.OutputStream;
import net.minecraft.server.LoggedPrintStream;

public class DebugLoggedPrintStream extends LoggedPrintStream {
   public DebugLoggedPrintStream(String string, OutputStream outputStream) {
      super(string, outputStream);
   }

   protected void logLine(String string) {
      StackTraceElement[] vars2 = Thread.currentThread().getStackTrace();
      StackTraceElement var3 = vars2[Math.min(3, vars2.length)];
      LOGGER.info("[{}]@.({}:{}): {}", this.name, var3.getFileName(), Integer.valueOf(var3.getLineNumber()), string);
   }
}
