package remote;

import GUI.StartScreen;

import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class EncryptDecrypt {
    public static String decryptString (SealedObject sealedObject, SecretKey key){

        String nonSealedString ="";
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,key);
            nonSealedString = (String)sealedObject.getObject(cipher);

        } catch (NoSuchAlgorithmException e) {
            System.out.println(java.time.LocalTime.now() + " " + "Shared key error");
//            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            System.out.println(java.time.LocalTime.now() + " " + "Shared key error");
//            e.printStackTrace();
        } catch (InvalidKeyException e) {
            System.out.println(java.time.LocalTime.now() + " " + "Shared key error");
//            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(java.time.LocalTime.now() + " " + "Shared key error");
//            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            System.out.println(java.time.LocalTime.now() + " " + "Shared key error");
//            e.printStackTrace();
        } catch (BadPaddingException e) {
            System.out.println(java.time.LocalTime.now() + " " + "Shared key error");
//            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println(java.time.LocalTime.now() + " " + "Shared key error");
//            e.printStackTrace();
        }
        return nonSealedString;

    }
    public static SealedObject encryptString (String string, SecretKey key){
        SealedObject sealedObject = null;

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,key);
            sealedObject = new SealedObject(string,cipher);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(java.time.LocalTime.now() + " " + "Shared key error");
//            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            System.out.println(java.time.LocalTime.now() + " " + "Shared key error");
//            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            System.out.println(java.time.LocalTime.now() + " " + "Shared key error");
//            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(java.time.LocalTime.now() + " " + "Shared key error");
//            e.printStackTrace();
        } catch (InvalidKeyException e) {
            System.out.println(java.time.LocalTime.now() + " " + "Shared key error");
//            e.printStackTrace();
        }
        return sealedObject;


    }
}
