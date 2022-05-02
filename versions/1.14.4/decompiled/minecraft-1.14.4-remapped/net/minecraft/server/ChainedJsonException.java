package net.minecraft.server;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class ChainedJsonException extends IOException {
   private final List entries = Lists.newArrayList();
   private final String message;

   public ChainedJsonException(String message) {
      this.entries.add(new ChainedJsonException.Entry());
      this.message = message;
   }

   public ChainedJsonException(String message, Throwable throwable) {
      super(throwable);
      this.entries.add(new ChainedJsonException.Entry());
      this.message = message;
   }

   public void prependJsonKey(String string) {
      ((ChainedJsonException.Entry)this.entries.get(0)).addJsonKey(string);
   }

   public void setFilenameAndFlush(String filenameAndFlush) {
      ((ChainedJsonException.Entry)this.entries.get(0)).filename = filenameAndFlush;
      this.entries.add(0, new ChainedJsonException.Entry());
   }

   public String getMessage() {
      return "Invalid " + this.entries.get(this.entries.size() - 1) + ": " + this.message;
   }

   public static ChainedJsonException forException(Exception exception) {
      if(exception instanceof ChainedJsonException) {
         return (ChainedJsonException)exception;
      } else {
         String var1 = exception.getMessage();
         if(exception instanceof FileNotFoundException) {
            var1 = "File not found";
         }

         return new ChainedJsonException(var1, exception);
      }
   }

   public static class Entry {
      @Nullable
      private String filename;
      private final List jsonKeys;

      private Entry() {
         this.jsonKeys = Lists.newArrayList();
      }

      private void addJsonKey(String string) {
         this.jsonKeys.add(0, string);
      }

      public String getJsonKeys() {
         return StringUtils.join(this.jsonKeys, "->");
      }

      public String toString() {
         return this.filename != null?(this.jsonKeys.isEmpty()?this.filename:this.filename + " " + this.getJsonKeys()):(this.jsonKeys.isEmpty()?"(Unknown file)":"(Unknown file) " + this.getJsonKeys());
      }
   }
}
