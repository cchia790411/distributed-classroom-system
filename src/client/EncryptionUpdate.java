package client;

import GUI.StartScreen;
import remote.IDrawingUpdate;
import remote.IEncryptionUpdate;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;

public class EncryptionUpdate extends UnicastRemoteObject implements IEncryptionUpdate, Serializable {

    private PrivateKey priv;
    private PublicKey pub;

    public SecretKey getSharedSecretKey() {
        return sharedSecretKey;
    }

    private SecretKey sharedSecretKey;
    private Cipher myCipher;

    public PublicKey getPub() {
        return pub;
    }

    public void setSharedKey(byte[] encryptedSharedSecretKey){
        try {
            Cipher cipher = Cipher.getInstance("RSA", "SunJCE");
            cipher.init(Cipher.DECRYPT_MODE, priv);
            sharedSecretKey = new SecretKeySpec(cipher.doFinal(encryptedSharedSecretKey), "AES");
//            System.out.println("Client holds this shared key: "+sharedSecretKey);


        } catch (NoSuchAlgorithmException e) {
            StartScreen.showErrorMessage("Shared key error");
            //e.printStackTrace();
        } catch (NoSuchProviderException e) {
            StartScreen.showErrorMessage("Shared key error");
            //e.printStackTrace();
        } catch (NoSuchPaddingException | InvalidKeyException e) {
            StartScreen.showErrorMessage("Shared key error");
            //e.printStackTrace();
        } catch (BadPaddingException e) {
            StartScreen.showErrorMessage("Shared key error");
            //e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            StartScreen.showErrorMessage("Shared key error");
            //e.printStackTrace();
        }


    }

    EncryptionUpdate() throws RemoteException {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "SunJSSE");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            this.priv = pair.getPrivate();
            this.pub = pair.getPublic();

        }
        catch (Exception e){
            StartScreen.showErrorMessage("Encryption Update error");
            //e.printStackTrace();
        }
    }
}
