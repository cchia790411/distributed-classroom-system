package remote;

import javax.crypto.SealedObject;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChatUpdate extends Remote, Serializable
{
    boolean notifyChat(String fromClient, SealedObject messageSealed, boolean isPrivate) throws RemoteException;
    boolean notifyUserLogin(String fromClient) throws RemoteException;
    boolean notifyUserLogout(String fromClient) throws RemoteException;
}
