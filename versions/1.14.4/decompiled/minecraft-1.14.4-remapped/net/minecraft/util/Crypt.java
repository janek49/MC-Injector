package net.minecraft.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Crypt {
   private static final Logger LOGGER = LogManager.getLogger();

   public static SecretKey generateSecretKey() {
      try {
         KeyGenerator var0 = KeyGenerator.getInstance("AES");
         var0.init(128);
         return var0.generateKey();
      } catch (NoSuchAlgorithmException var1) {
         throw new Error(var1);
      }
   }

   public static KeyPair generateKeyPair() {
      try {
         KeyPairGenerator var0 = KeyPairGenerator.getInstance("RSA");
         var0.initialize(1024);
         return var0.generateKeyPair();
      } catch (NoSuchAlgorithmException var1) {
         var1.printStackTrace();
         LOGGER.error("Key pair generation failed!");
         return null;
      }
   }

   public static byte[] digestData(String string, PublicKey publicKey, SecretKey secretKey) {
      try {
         return digestData("SHA-1", new byte[][]{string.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded()});
      } catch (UnsupportedEncodingException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   private static byte[] digestData(String string, byte[]... bytes) {
      try {
         MessageDigest var2 = MessageDigest.getInstance(string);

         for(byte[] vars6 : bytes) {
            var2.update(vars6);
         }

         return var2.digest();
      } catch (NoSuchAlgorithmException var7) {
         var7.printStackTrace();
         return null;
      }
   }

   public static PublicKey byteToPublicKey(byte[] bytes) {
      try {
         EncodedKeySpec var1 = new X509EncodedKeySpec(bytes);
         KeyFactory var2 = KeyFactory.getInstance("RSA");
         return var2.generatePublic(var1);
      } catch (NoSuchAlgorithmException var3) {
         ;
      } catch (InvalidKeySpecException var4) {
         ;
      }

      LOGGER.error("Public key reconstitute failed!");
      return null;
   }

   public static SecretKey decryptByteToSecretKey(PrivateKey privateKey, byte[] bytes) {
      return new SecretKeySpec(decryptUsingKey(privateKey, bytes), "AES");
   }

   public static byte[] encryptUsingKey(Key key, byte[] vars1) {
      return cipherData(1, key, vars1);
   }

   public static byte[] decryptUsingKey(Key key, byte[] vars1) {
      return cipherData(2, key, vars1);
   }

   private static byte[] cipherData(int var0, Key key, byte[] vars2) {
      try {
         return setupCipher(var0, key.getAlgorithm(), key).doFinal(vars2);
      } catch (IllegalBlockSizeException var4) {
         var4.printStackTrace();
      } catch (BadPaddingException var5) {
         var5.printStackTrace();
      }

      LOGGER.error("Cipher data failed!");
      return null;
   }

   private static Cipher setupCipher(int var0, String string, Key key) {
      try {
         Cipher cipher = Cipher.getInstance(string);
         cipher.init(var0, key);
         return cipher;
      } catch (InvalidKeyException var4) {
         var4.printStackTrace();
      } catch (NoSuchAlgorithmException var5) {
         var5.printStackTrace();
      } catch (NoSuchPaddingException var6) {
         var6.printStackTrace();
      }

      LOGGER.error("Cipher creation failed!");
      return null;
   }

   public static Cipher getCipher(int var0, Key key) {
      try {
         Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
         cipher.init(var0, key, new IvParameterSpec(key.getEncoded()));
         return cipher;
      } catch (GeneralSecurityException var3) {
         throw new RuntimeException(var3);
      }
   }
}
