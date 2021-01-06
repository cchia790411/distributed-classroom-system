package remote;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClientUpdate extends Remote, Serializable {

    enum Action {KICKOUT, ASSIGNADMIN, KICKALL, JOINAPPROVAL};
    boolean updateUserList(String[] users) throws RemoteException;
    void terminateChat() throws RemoteException;
    int notifyManagerActions(String toClient, Action action) throws RemoteException;
    void setVisibility() throws RemoteException;

}
