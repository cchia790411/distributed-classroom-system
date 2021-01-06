package server;

import remote.EncryptDecrypt;
import remote.IChatController;
import remote.IChatUpdate;

import javax.crypto.SealedObject;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatController extends UnicastRemoteObject implements IChatController, Serializable
{
    private Server server;

    public ChatController(Server server) throws RemoteException
    {
        this.server = server;
    }

    @Override
    public boolean broadcastMessage(String fromClient, SealedObject messageSealed) throws RemoteException
    {
//        System.out.print("Broadcasting message to everyone...");
        int fromClientIndex = server.clientController.getUserIndex(fromClient);
        String message = EncryptDecrypt.decryptString(messageSealed,server.users.get(fromClientIndex).getSharedSecretKey());

        for( User u : server.users )
        {   u.getSharedSecretKey();
            SealedObject messageSealedTo = EncryptDecrypt.encryptString(message,u.getSharedSecretKey());
            u.getIChatUpdate().notifyChat(fromClient, messageSealedTo, false);
        }

//        System.out.print("...DONE\n");

        return true;
    }

    @Override
    public boolean broadcastMessageUserLogin(String fromClient) throws RemoteException {
//        System.out.print("Broadcasting message to everyone...");

        IChatUpdate client;

        for( User u : server.users )
        {
            client = u.getIChatUpdate();
            client.notifyUserLogin(fromClient);
        }

//        System.out.print("...DONE\n");
        System.out.println(java.time.LocalTime.now() + " " + fromClient + " has joined the room.");

        return true;
    }

    @Override
    public boolean broadcastMessageUserLogout(String fromClient) throws RemoteException {
//        System.out.print("Broadcasting message to everyone...");

        IChatUpdate client;

        for( User u : server.users )
        {
            client = u.getIChatUpdate();
            client.notifyUserLogout(fromClient);
        }

//        System.out.print("...DONE\n");
        System.out.println(java.time.LocalTime.now() + " " + fromClient + " has left the room.");

        return true;
    }

    @Override
    public boolean sendPrivateMessage(String fromClient, String toClient, SealedObject messageSealed) throws RemoteException
    {   String message;
        int toClientIndex = server.clientController.getUserIndex(toClient);
        int fromClientIndex = server.clientController.getUserIndex(fromClient);

        if( toClientIndex >= 0 && fromClientIndex >= 0 )
        {
            message = EncryptDecrypt.decryptString(messageSealed,server.users.get(fromClientIndex).getSharedSecretKey());

            SealedObject messageSealedOutgoingFrom = EncryptDecrypt.encryptString(message,server.users.get(fromClientIndex).getSharedSecretKey());

            SealedObject messageSealedOutgoingTo = EncryptDecrypt.encryptString(message,server.users.get(toClientIndex).getSharedSecretKey());

            server.users.get(toClientIndex).getIChatUpdate().notifyChat(fromClient, messageSealedOutgoingTo, true);

            server.users.get(fromClientIndex).getIChatUpdate().notifyChat(fromClient, messageSealedOutgoingFrom, true);

            return true;
        }

        return false;
    }
}
