package com.mojang.realmsclient.util;

import com.fox2code.repacker.ClientJarOnly;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@ClientJarOnly
public class TextRenderingUtils {
   static List lineBreak(String string) {
      return Arrays.asList(string.split("\\n"));
   }

   public static List decompose(String string, TextRenderingUtils.LineSegment... textRenderingUtils$LineSegments) {
      return decompose(string, Arrays.asList(textRenderingUtils$LineSegments));
   }

   private static List decompose(String string, List var1) {
      List<String> var2 = lineBreak(string);
      return insertLinks(var2, var1);
   }

   private static List insertLinks(List var0, List var1) {
      int var2 = 0;
      ArrayList<TextRenderingUtils.Line> var3 = new ArrayList();

      for(String var5 : var0) {
         List<TextRenderingUtils.LineSegment> var6 = new ArrayList();

         for(String var9 : split(var5, "%link")) {
            if(var9.equals("%link")) {
               var6.add(var1.get(var2++));
            } else {
               var6.add(TextRenderingUtils.LineSegment.text(var9));
            }
         }

         var3.add(new TextRenderingUtils.Line(var6));
      }

      return var3;
   }

   public static List split(String var0, String var1) {
      if(var1.isEmpty()) {
         throw new IllegalArgumentException("Delimiter cannot be the empty string");
      } else {
         List<String> list = new ArrayList();

         int var3;
         int var4;
         for(var3 = 0; (var4 = var0.indexOf(var1, var3)) != -1; var3 = var4 + var1.length()) {
            if(var4 > var3) {
               list.add(var0.substring(var3, var4));
            }

            list.add(var1);
         }

         if(var3 < var0.length()) {
            list.add(var0.substring(var3));
         }

         return list;
      }
   }

   @ClientJarOnly
   public static class Line {
      public final List segments;

      Line(List segments) {
         this.segments = segments;
      }

      public String toString() {
         return "Line{segments=" + this.segments + '}';
      }

      public boolean equals(Object object) {
         if(this == object) {
            return true;
         } else if(object != null && this.getClass() == object.getClass()) {
            TextRenderingUtils.Line var2 = (TextRenderingUtils.Line)object;
            return Objects.equals(this.segments, var2.segments);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.segments});
      }
   }

   @ClientJarOnly
   public static class LineSegment {
      final String fullText;
      final String linkTitle;
      final String linkUrl;

      private LineSegment(String fullText) {
         this.fullText = fullText;
         this.linkTitle = null;
         this.linkUrl = null;
      }

      private LineSegment(String fullText, String linkTitle, String linkUrl) {
         this.fullText = fullText;
         this.linkTitle = linkTitle;
         this.linkUrl = linkUrl;
      }

      public boolean equals(Object object) {
         if(this == object) {
            return true;
         } else if(object != null && this.getClass() == object.getClass()) {
            TextRenderingUtils.LineSegment var2 = (TextRenderingUtils.LineSegment)object;
            return Objects.equals(this.fullText, var2.fullText) && Objects.equals(this.linkTitle, var2.linkTitle) && Objects.equals(this.linkUrl, var2.linkUrl);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.fullText, this.linkTitle, this.linkUrl});
      }

      public String toString() {
         return "Segment{fullText=\'" + this.fullText + '\'' + ", linkTitle=\'" + this.linkTitle + '\'' + ", linkUrl=\'" + this.linkUrl + '\'' + '}';
      }

      public String renderedText() {
         return this.isLink()?this.linkTitle:this.fullText;
      }

      public boolean isLink() {
         return this.linkTitle != null;
      }

      public String getLinkUrl() {
         if(!this.isLink()) {
            throw new IllegalStateException("Not a link: " + this);
         } else {
            return this.linkUrl;
         }
      }

      public static TextRenderingUtils.LineSegment link(String var0, String var1) {
         return new TextRenderingUtils.LineSegment((String)null, var0, var1);
      }

      static TextRenderingUtils.LineSegment text(String string) {
         return new TextRenderingUtils.LineSegment(string);
      }
   }
}
