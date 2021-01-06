package server;

import remote.IEncryptionUpdate;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

public class MySharedKey {
    private byte[] encryptedSharedSecretKey;
    private IEncryptionUpdate encryptionUpdate;

    public SecretKey getSharedSecretKey() {
        return sharedSecretKey;
    }

    private SecretKey sharedSecretKey;


    public MySharedKey (IEncryptionUpdate encryptionUpdate) throws RemoteException {
        this.encryptionUpdate = encryptionUpdate;
        this.sharedSecretKey = new SecretKeySpec(new byte[16], "AES");
        System.out.println(sharedSecretKey);
        this.encryptedSharedSecretKey = wrapKey(encryptionUpdate.getPub());
//        System.out.println("Shared key on server:" + sharedSecretKey);
        encryptionUpdate.setSharedKey(encryptedSharedSecretKey);
    }

    private byte[] wrapKey(PublicKey clientPubKey){

        try {
            Cipher c = Cipher.getInstance("RSA", "SunJCE");
            c.init(Cipher.WRAP_MODE, clientPubKey);
            byte[] result2 = c.wrap(sharedSecretKey);
            return result2;
        } catch (Exception e) {
            System.out.println(java.time.LocalTime.now() + " " + "Error in wrapping the key.");
//            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }
}
