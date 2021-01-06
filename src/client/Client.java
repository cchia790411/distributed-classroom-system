package client;

import GUI.ApplicationMain;
import GUI.ChatScreen;
import GUI.PaintGUI;
import GUI.StartScreen;
import remote.EncryptDecrypt;
import remote.IChatController;
import remote.IClientController;
import remote.IDrawingController;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Client
{
    private final String DEFAULT_USERNAME = "Anonymous";
    private final String DEFAULT_SERVER_ADDRESS = "localhost";

    public EncryptionUpdate getEncryptionUpdate() {
        return encryptionUpdate;
    }

    private final EncryptionUpdate encryptionUpdate;

    private String defaultUserName;
    private String userName;
    private String serverAddress;

    private Registry registryServer;
    private IChatController chatController;
    private IClientController clientController;
    private IDrawingController drawingController;

    private ClientUpdate clientUpdate;
    private ChatUpdate chatUpdate;
    private DrawingUpdate drawingUpdate;
    private ApplicationMain applicationMain;
    private StartScreen startScreen;


    public String getUserName()
    {
        return this.userName;
    }

    public String getDefaultUserName()
    {
        return this.defaultUserName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getServerAddress()
    {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress)
    {
        this.serverAddress = serverAddress;
    }


    public ApplicationMain getApplicationMain() { return applicationMain; }

    public ChatScreen getChatScreen() {
        return getApplicationMain().getChatScreen();
    }

    public IChatController getChatController()
    {
        return chatController;
    }

    public IClientController getClientController()
    {
        return clientController;
    }

    public IDrawingController getDrawingController() { return drawingController; }

    public StartScreen getStartScreen() {
        return startScreen;
    }


    public Client() throws RemoteException, NoSuchProviderException, NoSuchAlgorithmException
    {
        this.defaultUserName = DEFAULT_USERNAME;
        this.clientUpdate = new ClientUpdate(this);
        this.chatUpdate = new ChatUpdate(this);
        this.drawingUpdate = new DrawingUpdate(this);
        this.startScreen = new StartScreen(this);
        this.applicationMain = new ApplicationMain(this);
        this.encryptionUpdate = new EncryptionUpdate();
    }

    public static void main(String[] args)
    {
        try
        {
            Client client = new Client();
            client.showStartScreen();
        }
        catch (Exception e)
        {
            StartScreen.showErrorMessage("Error starting up client");
            System.exit(0);
        }
    }


    public void showStartScreen()
    {
        startScreen.go();
    }

    public void setVisibleStartScreen(){
        startScreen.setVisible();
    }

    public void startApplication()
    {
        applicationMain.createAndShowGUI();
        applicationMain.getChatScreen().setManagerToolsVisibility();
    }

    public void setVisibleApplication(){
        applicationMain.setVisible();
    }

    // return = 1 -> connected successfully
    // return = 2 -> duplicate username
    // return = 3 -> error in locating the server
    // return = 4 -> incorrect password entered
    // return = 5 -> re-connected successfully
    // return = 6 -> duplicate username when re-connecting
    public int connect(String userName, String serverAddress, String password)
    {
        try
        {
            if (userName.trim().isEmpty()) {
                userName = getDefaultUserName();
            }
            if (serverAddress.trim().isEmpty()) {
                serverAddress = DEFAULT_SERVER_ADDRESS;
            }
            // New connection
            setUserName(userName);
            setServerAddress(serverAddress);
            System.out.println(java.time.LocalTime.now() + " " + "Server address:" + serverAddress);

            registryServer = LocateRegistry.getRegistry(serverAddress);
            chatController = (IChatController) registryServer.lookup("ChatController");
            clientController = (IClientController) registryServer.lookup("ClientController");
            drawingController = (IDrawingController) registryServer.lookup("DrawingController");

            System.out.println(java.time.LocalTime.now() + " " + "User name:" + getUserName());
            System.out.println(java.time.LocalTime.now() + " " + "Password: " + password);

            if (clientController.setSharedKey(this.encryptionUpdate)) {
                SealedObject sealedPassword = EncryptDecrypt.encryptString(password, this.encryptionUpdate.getSharedSecretKey());

                if (clientController.checkPassword(sealedPassword)) {
                    int joinStatus = clientController.join(getUserName(), this.chatUpdate, this.clientUpdate, this.drawingUpdate);
                    return joinStatus;
                }
                else
                {
                    return 4;
                }
            }
            else
            {
                return 4;
            }

        }
        catch (Exception e)
        {
            return 3;
        }
    }

    public void clearChat() {
        getApplicationMain().getChatScreen().getChatDisplayBox().setText("");
    }

    public void clearDrawingArea() {
        getApplicationMain().getPaintGUI().getDrawingArea().clear();
    }

}