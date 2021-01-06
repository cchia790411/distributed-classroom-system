package remote;

import javax.crypto.SealedObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClientController extends Remote
{
    enum Action {KICKOUT, ASSIGNADMIN};

    int join(String username, IChatUpdate clientChat, IClientUpdate clientUpdate, IDrawingUpdate clientDrawing) throws RemoteException;

    void quit(String username) throws RemoteException;

    boolean assignAdmin(String oldAdmin, String newAdmin) throws RemoteException;

    boolean kickUser(String username, String who) throws RemoteException;

    boolean broadcastManagerMessage(String toClient, Action action) throws RemoteException;

    void kickAll(String manager) throws RemoteException;

    String getAdmin() throws RemoteException;

    boolean checkPassword(SealedObject sealedPassword) throws RemoteException;

    boolean setSharedKey(IEncryptionUpdate encryptionUpdate) throws RemoteException;

}
