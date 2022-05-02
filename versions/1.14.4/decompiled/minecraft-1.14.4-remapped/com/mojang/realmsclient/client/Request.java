package com.mojang.realmsclient.client;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.realmsclient.client.RealmsClientConfig;
import com.mojang.realmsclient.exception.RealmsHttpException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

@ClientJarOnly
public abstract class Request {
   protected HttpURLConnection connection;
   private boolean connected;
   protected String url;

   public Request(String url, int var2, int var3) {
      try {
         this.url = url;
         Proxy var4 = RealmsClientConfig.getProxy();
         if(var4 != null) {
            this.connection = (HttpURLConnection)(new URL(url)).openConnection(var4);
         } else {
            this.connection = (HttpURLConnection)(new URL(url)).openConnection();
         }

         this.connection.setConnectTimeout(var2);
         this.connection.setReadTimeout(var3);
      } catch (MalformedURLException var5) {
         throw new RealmsHttpException(var5.getMessage(), var5);
      } catch (IOException var6) {
         throw new RealmsHttpException(var6.getMessage(), var6);
      }
   }

   public void cookie(String var1, String var2) {
      cookie(this.connection, var1, var2);
   }

   public static void cookie(HttpURLConnection httpURLConnection, String var1, String var2) {
      String var3 = httpURLConnection.getRequestProperty("Cookie");
      if(var3 == null) {
         httpURLConnection.setRequestProperty("Cookie", var1 + "=" + var2);
      } else {
         httpURLConnection.setRequestProperty("Cookie", var3 + ";" + var1 + "=" + var2);
      }

   }

   public int getRetryAfterHeader() {
      return getRetryAfterHeader(this.connection);
   }

   public static int getRetryAfterHeader(HttpURLConnection httpURLConnection) {
      String var1 = httpURLConnection.getHeaderField("Retry-After");

      try {
         return Integer.valueOf(var1).intValue();
      } catch (Exception var3) {
         return 5;
      }
   }

   public int responseCode() {
      try {
         this.connect();
         return this.connection.getResponseCode();
      } catch (Exception var2) {
         throw new RealmsHttpException(var2.getMessage(), var2);
      }
   }

   public String text() {
      try {
         this.connect();
         String string = null;
         if(this.responseCode() >= 400) {
            string = this.read(this.connection.getErrorStream());
         } else {
            string = this.read(this.connection.getInputStream());
         }

         this.dispose();
         return string;
      } catch (IOException var2) {
         throw new RealmsHttpException(var2.getMessage(), var2);
      }
   }

   private String read(InputStream inputStream) throws IOException {
      if(inputStream == null) {
         return "";
      } else {
         InputStreamReader var2 = new InputStreamReader(inputStream, "UTF-8");
         StringBuilder var3 = new StringBuilder();

         for(int var4 = var2.read(); var4 != -1; var4 = var2.read()) {
            var3.append((char)var4);
         }

         return var3.toString();
      }
   }

   private void dispose() {
      byte[] vars1 = new byte[1024];

      try {
         int var2 = 0;
         InputStream var3 = this.connection.getInputStream();

         while(var3.read(vars1) > 0) {
            ;
         }

         var3.close();
         return;
      } catch (Exception var10) {
         try {
            InputStream var3 = this.connection.getErrorStream();
            int var4 = 0;
            if(var3 != null) {
               while(var3.read(vars1) > 0) {
                  ;
               }

               var3.close();
               return;
            }
         } catch (IOException var9) {
            return;
         }
      } finally {
         if(this.connection != null) {
            this.connection.disconnect();
         }

      }

   }

   protected Request connect() {
      if(this.connected) {
         return this;
      } else {
         T request = this.doConnect();
         this.connected = true;
         return request;
      }
   }

   protected abstract Request doConnect();

   public static Request get(String string) {
      return new Request.Get(string, 5000, '\uea60');
   }

   public static Request get(String string, int var1, int var2) {
      return new Request.Get(string, var1, var2);
   }

   public static Request post(String var0, String var1) {
      return new Request.Post(var0, var1, 5000, '\uea60');
   }

   public static Request post(String var0, String var1, int var2, int var3) {
      return new Request.Post(var0, var1, var2, var3);
   }

   public static Request delete(String string) {
      return new Request.Delete(string, 5000, '\uea60');
   }

   public static Request put(String var0, String var1) {
      return new Request.Put(var0, var1, 5000, '\uea60');
   }

   public static Request put(String var0, String var1, int var2, int var3) {
      return new Request.Put(var0, var1, var2, var3);
   }

   public String getHeader(String string) {
      return getHeader(this.connection, string);
   }

   public static String getHeader(HttpURLConnection httpURLConnection, String var1) {
      try {
         return httpURLConnection.getHeaderField(var1);
      } catch (Exception var3) {
         return "";
      }
   }

   @ClientJarOnly
   public static class Delete extends Request {
      public Delete(String string, int var2, int var3) {
         super(string, var2, var3);
      }

      public Request.Delete doConnect() {
         try {
            this.connection.setDoOutput(true);
            this.connection.setRequestMethod("DELETE");
            this.connection.connect();
            return this;
         } catch (Exception var2) {
            throw new RealmsHttpException(var2.getMessage(), var2);
         }
      }

      // $FF: synthetic method
      public Request doConnect() {
         return this.doConnect();
      }
   }

   @ClientJarOnly
   public static class Get extends Request {
      public Get(String string, int var2, int var3) {
         super(string, var2, var3);
      }

      public Request.Get doConnect() {
         try {
            this.connection.setDoInput(true);
            this.connection.setDoOutput(true);
            this.connection.setUseCaches(false);
            this.connection.setRequestMethod("GET");
            return this;
         } catch (Exception var2) {
            throw new RealmsHttpException(var2.getMessage(), var2);
         }
      }

      // $FF: synthetic method
      public Request doConnect() {
         return this.doConnect();
      }
   }

   @ClientJarOnly
   public static class Post extends Request {
      private final String content;

      public Post(String var1, String content, int var3, int var4) {
         super(var1, var3, var4);
         this.content = content;
      }

      public Request.Post doConnect() {
         try {
            if(this.content != null) {
               this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }

            this.connection.setDoInput(true);
            this.connection.setDoOutput(true);
            this.connection.setUseCaches(false);
            this.connection.setRequestMethod("POST");
            OutputStream var1 = this.connection.getOutputStream();
            OutputStreamWriter var2 = new OutputStreamWriter(var1, "UTF-8");
            var2.write(this.content);
            var2.close();
            var1.flush();
            return this;
         } catch (Exception var3) {
            throw new RealmsHttpException(var3.getMessage(), var3);
         }
      }

      // $FF: synthetic method
      public Request doConnect() {
         return this.doConnect();
      }
   }

   @ClientJarOnly
   public static class Put extends Request {
      private final String content;

      public Put(String var1, String content, int var3, int var4) {
         super(var1, var3, var4);
         this.content = content;
      }

      public Request.Put doConnect() {
         try {
            if(this.content != null) {
               this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }

            this.connection.setDoOutput(true);
            this.connection.setDoInput(true);
            this.connection.setRequestMethod("PUT");
            OutputStream var1 = this.connection.getOutputStream();
            OutputStreamWriter var2 = new OutputStreamWriter(var1, "UTF-8");
            var2.write(this.content);
            var2.close();
            var1.flush();
            return this;
         } catch (Exception var3) {
            throw new RealmsHttpException(var3.getMessage(), var3);
         }
      }

      // $FF: synthetic method
      public Request doConnect() {
         return this.doConnect();
      }
   }
}
