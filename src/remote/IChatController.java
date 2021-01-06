package remote;

import javax.crypto.SealedObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChatController extends Remote
{
    boolean broadcastMessage(String fromClient, SealedObject messageSealed) throws RemoteException;
    boolean broadcastMessageUserLogin(String fromClient) throws RemoteException;
    boolean broadcastMessageUserLogout(String fromClient) throws RemoteException;
    boolean sendPrivateMessage(String fromClient, String toClient, SealedObject messageSealed) throws RemoteException;
}
