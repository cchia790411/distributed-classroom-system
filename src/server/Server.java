package server;

import remote.IChatController;
import remote.IClientController;
import remote.IDrawingController;

import javax.naming.NamingEnumeration;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server
{
    protected ArrayList<User> users;
    protected ArrayList<User> waitingList;

    protected ClientController clientController;
    protected ChatController chatController;
    protected DrawingController drawingController;

    private String password;

    public Server() throws RemoteException
    {
        password = null;
        users = new ArrayList<User>();
        waitingList = new ArrayList<User>();
        clientController = new ClientController(this);
        chatController = new ChatController(this);
        drawingController = new DrawingController(this);
    }

    protected void setPassword(String password)
    {
        this.password = password;
    }

    protected String getPassword()
    {
        return password;
    }

    public static void main(String[] args)
    {
        try
        {
            Server server = new Server();

            server.run(args[0]);
        }
        catch( Exception e )
        {
            System.out.println(java.time.LocalTime.now() + " " + "Server launch failed.");
//            e.printStackTrace();
        }
    }

    public void run(String serverIP) throws RemoteException
    {
//        String serverIP = "";
//        try {
//            InetAddress inetAddress = InetAddress.getLocalHost();
//            serverIP = inetAddress.getHostAddress();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        System.setProperty("java.rmi.server.hostname", serverIP);

        LocateRegistry.createRegistry(1099);
        Registry registry = LocateRegistry.getRegistry();
//        if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new SecurityManager());
//        }
//        System.setProperty("java.security.policy", "file:/Users/haiho/OneDrive - The University of Melbourne/Distributed Systems (COMP90015)/Assignment 2/InfinityMonkeys-remaster/comp90015-dsass2-infinitymonkeys-remaster/src/server/server.policy");

        String clientControllerName = "ClientController";
        String chatControllerName = "ChatController";
        String drawingControllerName = "DrawingController";

        IClientController clientController = this.clientController;
        IChatController chatController = this.chatController;
        IDrawingController drawingController = this.drawingController;

        registry.rebind(clientControllerName, clientController);
        registry.rebind(chatControllerName, chatController);
        registry.rebind(drawingControllerName, drawingController);

        System.out.println(java.time.LocalTime.now() + " " + "Server is ready");

        printIP(serverIP);
    }

    private void printIP(String serverIP)
    {
        InetAddress inetAddress = null;

        try
        {
            if (serverIP.isEmpty()) {
                inetAddress = InetAddress.getLocalHost();
            } else inetAddress = InetAddress.getByName(serverIP);
        }
        catch (UnknownHostException e)
        {
            System.out.println(java.time.LocalTime.now() + " " + "Get IP failed.");
//            e.printStackTrace();
        }

        System.out.println(java.time.LocalTime.now() + " " + "IP Address:- " + inetAddress.getHostAddress());
        //System.out.println("Host Name:- " + inetAddress.getHostName());
    }
}
