package server;

import remote.*;

import javax.crypto.*;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientController extends UnicastRemoteObject implements IClientController, Serializable
{
    private Server server;

    protected ClientController(Server server) throws RemoteException
    {
        this.server = server;
    }

    private SecretKey sharedSecretKey;

    @Override
    public int join(String username, IChatUpdate clientChat, IClientUpdate clientUpdate, IDrawingUpdate clientDrawing) throws RemoteException
    {
        // If there is no user in the list then let the user be the manager
        if(server.users.size() == 0) {
            User newUser = new User(username, clientChat, clientUpdate, clientDrawing, sharedSecretKey);
            server.users.add(newUser);
            newUser.setAdmin(true);
            broadcastUserList();
            clientUpdate.setVisibility();
            return 0;
        }
        // If there are users in the user list, then check if the username is used
        else if (getUserIndex(username) < 0 ) {
            // Ask the manager for the join approval
            int managerIndex = getUserIndex(getAdmin());
            User manager = server.users.get(managerIndex);
            IClientUpdate client;
            client = manager.getIClientUpdate();
            int answer = client.notifyManagerActions(username, IClientUpdate.Action.JOINAPPROVAL);

            // If the manager accepts the new user to enter, then add the user to the list
            if (answer == 0) {
                server.chatController.broadcastMessageUserLogin(username);
                User newUser = new User(username, clientChat, clientUpdate, clientDrawing, sharedSecretKey);
                server.users.add(newUser);
                broadcastUserList();
                return 0;
            }
            else {
                return 1;
            }
        }
        else {
            return 2;
        }

    }

    @Override
    public void quit(String username) throws RemoteException
    {
        int userIndex = getUserIndex(username);

        if( userIndex >= 0 )
        {
            server.users.remove(userIndex);
            if (!server.users.isEmpty()) {
                server.chatController.broadcastMessageUserLogout(username);
                broadcastUserList();
            }
        }
    }

    // for debuggins purposes
    private void printUserList()
    {
//        System.out.print("Currently connected users: ");
        for( User u : server.users )
        {
//            System.out.print(u.getUserName() + " ");
        }
        System.out.println();
    }

    @Override
    public boolean assignAdmin(String oldAdmin, String newAdmin) throws RemoteException
    {
        int oldAdminIndex = getUserIndex(oldAdmin);
        int newAdminIndex = getUserIndex(newAdmin);

        if( newAdminIndex >= 0 && server.users.get(oldAdminIndex).isAdmin())
        {
            server.users.get(oldAdminIndex).setAdmin(false);
            server.users.get(newAdminIndex).setAdmin(true);
            broadcastUserList();
            broadcastManagerMessage(newAdmin, Action.ASSIGNADMIN);

            return true;
        }

        return false;
    }

    @Override
    public boolean kickUser(String manager, String kickedUser) throws RemoteException
    {
        int userIndex = getUserIndex(kickedUser);

        int adminIndex = getUserIndex(manager);

        if ( adminIndex >= 0 && userIndex >= 0 && server.users.get(adminIndex).isAdmin() )
        {
            broadcastManagerMessage(kickedUser, Action.KICKOUT);
            server.users.remove(userIndex);
            broadcastUserList();
            return true;
        }

        return false;
    }

    public int getUserIndex(String username)
    {
        int index = -1;

        for( int i = 0; i < server.users.size(); i++ )
        {
            if( server.users.get(i).getUserName().equals(username) )
            {
                index = i;
                break;
            }
        }

        return index;
    }

    public void broadcastUserList() throws RemoteException
    {

        String[] connectedUsers = new String[server.users.size()];
        for( int i = 0; i<server.users.size(); i++ )
        {
            connectedUsers[i] = server.users.get(i).getUserName();
        }

        printUserList();

        for( User u : server.users )
        {
//            System.out.print(u.getUserName() + " being notified");
            u.getIClientUpdate().updateUserList(connectedUsers);
//            System.out.println("...DONE");
        }
    }

    @Override
    public String getAdmin() throws RemoteException
    {
        String adminName = "";
        for( User u : server.users )
        {
            if (u.isAdmin()) {
                adminName = u.getUserName();
            }
        }
        return adminName;
    }

    @Override
    public boolean broadcastManagerMessage(String toClient, Action action) throws RemoteException {
//        System.out.print("Broadcasting message to everyone...");
        IClientUpdate client;

        switch (action){
            case KICKOUT:
                for( User u : server.users )
                {
                    client = u.getIClientUpdate();
                    client.notifyManagerActions(toClient, remote.IClientUpdate.Action.KICKOUT);
                }

//                System.out.print("...DONE\n");
                System.out.println(java.time.LocalTime.now() + " " + toClient + " has been kicked out the room.");
                break;

            case ASSIGNADMIN:
                for( User u : server.users )
                {
                    client = u.getIClientUpdate();
                    client.notifyManagerActions(toClient, remote.IClientUpdate.Action.ASSIGNADMIN);
                    client.setVisibility();
                }

//                System.out.print("...DONE\n");
                System.out.println(java.time.LocalTime.now() + " " + toClient + " is the new manager.");
                break;
        }

        return true;
    }

    @Override
    public void kickAll (String manager) throws RemoteException {

        int adminIndex = getUserIndex(manager);
        IClientUpdate client;
        String toClient;
        String[] connectedUsers = {};

        if (server.users.get(adminIndex).isAdmin()) {
            for (User u : server.users) {
                client = u.getIClientUpdate();
                toClient = u.getUserName();
                client.updateUserList(connectedUsers);
                client.notifyManagerActions(toClient, remote.IClientUpdate.Action.KICKALL);
            }
            server.users.clear();
        } else {
//            System.out.println("You are not the manager");
        }
    }

    @Override
    public boolean checkPassword(SealedObject sealedPassword)
    {
        System.out.println("Sealed passwords" + sealedPassword);
        String password = EncryptDecrypt.decryptString(sealedPassword,this.sharedSecretKey);
        System.out.println("unsealed password"  + password);
        if( server.users.size() == 0 )
        {
            server.setPassword(password);

            return true;
        }
        else if( server.getPassword().equals(password) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public boolean setSharedKey(IEncryptionUpdate encryptionUpdate) throws RemoteException {
        try {
            sharedSecretKey = new MySharedKey(encryptionUpdate).getSharedSecretKey();
            return true;

        }catch (Exception e){
            return false;
        }

    }
}
