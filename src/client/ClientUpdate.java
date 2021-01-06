package client;

import remote.IClientUpdate;

import javax.swing.*;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientUpdate extends UnicastRemoteObject implements IClientUpdate, Serializable {

    private Client client;

    public ClientUpdate(Client client) throws RemoteException
    {
        super();
        this.client = client;
    }

    public boolean notifyClient(String fromClient, String newUsername) throws RemoteException
    {
        client.getApplicationMain().getChatScreen().getSendMessageToComboBox().addItem(newUsername);

        return true;
    }

    // for debuggins purposes
    private void printUserList(String[] users)
    {
//        System.out.print("Currently connected users: ");
        for( String s : users )
        {
//            System.out.print(s + " ");
        }
//        System.out.println();
    }

    @Override
    public boolean updateUserList(String[] users) throws RemoteException
    {
        printUserList(users);

        JComboBox userBox = client.getApplicationMain().getChatScreen().getSendMessageToComboBox();
        JComboBox kickUserBox = client.getChatScreen().getKickUserComboBox();

        userBox.removeAllItems();
        kickUserBox.removeAllItems();

        userBox.addItem("All");

        for( String s : users )
        {
            if( !s.equals(client.getUserName()) )
            {
                userBox.addItem(s);
            }

            if( !s.equals(client.getClientController().getAdmin()) )
            {
                kickUserBox.addItem(s);
            }
            else {
                client.getApplicationMain().getChatScreen().setManagerName(s);
            }

        }

        return true;
    }

    @Override
    public void terminateChat() throws RemoteException {
        client.getChatScreen().exitChatScreen();
    }

    @Override
    public void setVisibility() {
        client.getChatScreen().setManagerToolsVisibility();
    }

    @Override
    public int notifyManagerActions(String toClient, Action action) throws RemoteException {
        int answer = -1;
        switch (action) {
            case KICKOUT:
                if (client.getUserName().equals(toClient)) {
                    client.getStartScreen().setKickedOut(true);
                    client.getApplicationMain().exitApplication();
                    client.getChatScreen().getChatDisplayBox().append(" You were kicked out by the manager.\n");
                }
                else {
                    client.getChatScreen().getChatDisplayBox().append(toClient + " has been kicked out by the manager.\n");
                }
                break;
            case ASSIGNADMIN:
                if (client.getUserName().equals(toClient)) {
                    JOptionPane.showMessageDialog(null,
                            "Congratulations! the manager promoted you, now you are the new manager.",
                            "Promoted",
                            JOptionPane.INFORMATION_MESSAGE);
                    client.getChatScreen().getChatDisplayBox().append("You are the new manager.\n");
                }
                else {
                    client.getChatScreen().getChatDisplayBox().append(toClient + " is the new manager.\n");
                }
                break;
            case KICKALL:
                client.getStartScreen().setAppTerminated(true);
                client.getApplicationMain().exitApplication();
                break;
            case JOINAPPROVAL:
               if (client.getUserName().equals(client.getClientController().getAdmin())) {
                   answer = JOptionPane.showConfirmDialog(null,
                           "The user: " + toClient + " wants to join to your whiteboard, do you want to accept?",
                           "Join request", JOptionPane.YES_NO_OPTION);
               }
               break;
        }
        return answer;
    }

}
