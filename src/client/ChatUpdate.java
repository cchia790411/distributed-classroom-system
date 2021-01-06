package client;

import remote.EncryptDecrypt;
import remote.IChatUpdate;

import javax.crypto.SealedObject;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatUpdate extends UnicastRemoteObject implements IChatUpdate, Serializable
{
    private Client client;

    public ChatUpdate(Client client) throws RemoteException
    {
        super();
        this.client = client;
    }

    @Override
    public boolean notifyChat(String fromClient, SealedObject messageSealed, boolean isPrivate) throws RemoteException
    {   String message;
        message = EncryptDecrypt.decryptString(messageSealed,client.getEncryptionUpdate().getSharedSecretKey());
        String outputString;

        if( isPrivate )
        {
            outputString = "PRIVATE (from " + fromClient + "): " + message + "\n";
        }
        else
        {
            outputString = fromClient + ": " + message + "\n";
        }

        client.getApplicationMain().getChatScreen().getChatDisplayBox().append(outputString);

        //client.setReceivedMessage(message);
//        System.out.println(fromClient + ": " + message);
        return true;
    }

    @Override
    public boolean notifyUserLogin(String fromClient) throws RemoteException {
        client.getApplicationMain().getChatScreen().getChatDisplayBox().append(fromClient + " has joined the room.\n");
        return true;
    }

    @Override
    public boolean notifyUserLogout(String fromClient) throws RemoteException {
        client.getApplicationMain().getChatScreen().getChatDisplayBox().append(fromClient + " has left the room.\n");
        return true;
    }

}
