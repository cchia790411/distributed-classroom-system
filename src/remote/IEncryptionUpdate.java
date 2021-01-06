package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;

public interface IEncryptionUpdate extends Remote {
    public PublicKey getPub() throws RemoteException;
    public void setSharedKey(byte[] encryptedSharedSecretKey) throws RemoteException;
}
