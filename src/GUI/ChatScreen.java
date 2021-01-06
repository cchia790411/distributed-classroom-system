package GUI;

import client.Client;
import remote.EncryptDecrypt;
import remote.IChatController;
import remote.IClientController;

import javax.crypto.SealedObject;
import javax.swing.*;
import java.awt.event.*;
import java.rmi.RemoteException;

public class ChatScreen {

    public JPanel panel2;

    private JButton sendButton;

    private JPanel othersPanel;
    private JComboBox sendMessageToComboBox;
    private JTextArea chatDisplayBox;
    private JComboBox kickUserComboBox;
    private JButton kickOutButton;
    private JButton promoteToManagerButton;
    private JTextField chatInputBox;
    private JLabel sendMessageToLabel;
    private JLabel managersNameLabel;
    private JLabel yourNameLabel;
    private JLabel yourNameDisplay;
    private JLabel managersNameDisplay;
    private JPanel myAreaPanel;
    private JPanel managersPanel;
    private JPanel chatPanel;
    private JButton exitThisRoomButton;
    private DefaultListModel defaultListModel;

    public DefaultListModel getAllUserModel() {
        return defaultListModel;
    }

    private JList allUsersList;
    private JFrame frame;

    public Client getClient() {
        return client;
    }

    public Client client;

    public ChatScreen(Client client)
    {
        this.client = client;
        yourNameDisplay.setText(client.getUserName());
        exitThisRoomButton.addActionListener(actionListener);
        sendButton.addActionListener(actionListener);
        kickOutButton.addActionListener(actionListener);
        promoteToManagerButton.addActionListener(actionListener);
        defaultListModel = new DefaultListModel();
        allUsersList.setModel(defaultListModel);

        allUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        allUsersList.setSelectedIndex(0);
        allUsersList.setVisibleRowCount(5);




    }

    public void addUserToList(){

    }

    public void removeUserFromList(){

    }

    public void setUserName(String userName)
    {
        this.yourNameDisplay.setText(userName);
    }

    public void setManagerName(String manager)
    {
        this.managersNameDisplay.setText(manager);
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public JTextArea getChatDisplayBox() {
        return chatDisplayBox;
    }

    public JComboBox getSendMessageToComboBox()
    {
        return sendMessageToComboBox;
    }

    public JComboBox getKickUserComboBox()
    {
        return kickUserComboBox;
    }

    public void setManagerToolsVisibility() {
        try {
            if (client.getClientController().getAdmin().equals(client.getUserName())){
                managersPanel.setEnabled(true);
                kickUserComboBox.setEnabled(true);
                kickOutButton.setEnabled(true);
                promoteToManagerButton.setEnabled(true);
                client.getApplicationMain().getPaintGUI().enableFileControl();
            }
            else {
                managersPanel.setEnabled(false);
                kickUserComboBox.setEnabled(false);
                kickOutButton.setEnabled(false);
                promoteToManagerButton.setEnabled(false);
                client.getApplicationMain().getPaintGUI().disableFileControl();
            }
        } catch (RemoteException e)
        {
            StartScreen.showErrorMessage("Connection with server lost");
            System.exit(0);
            //e.printStackTrace();
        }
    }

    public void exitChatScreen() {
        System.exit(0);
    }

    ActionListener actionListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == sendButton)
            {
                String message = chatInputBox.getText();
                chatInputBox.setText("");
                IChatController chatController = client.getChatController();
                try
                {
//                    System.out.println("Send button pressed");

                    String toUser = sendMessageToComboBox.getSelectedItem().toString();
                    SealedObject messageSealed = EncryptDecrypt.encryptString(message,client.getEncryptionUpdate().getSharedSecretKey());
                    if( toUser.equals("All") )
                    {
                        chatController.broadcastMessage(client.getUserName(), messageSealed);
                    }
                    else
                    {
                        chatController.sendPrivateMessage(client.getUserName(), toUser, messageSealed);
                    }
                }
                catch (RemoteException ex)
                {
                    StartScreen.showErrorMessage("Connection with server lost");
                    System.exit(0);
                    //ex.printStackTrace();
                }
            }
            else if (e.getSource() == exitThisRoomButton)
            {
//                System.out.println("Exit room button pressed by: " + client.getUserName());
                client.getApplicationMain().closeWindow();
            }
            else if (e.getSource() == kickOutButton)
            {
                IClientController clientController = client.getClientController();
                String toUser = kickUserComboBox.getSelectedItem().toString();

                try {
//                    System.out.println("Kick out button pressed");
                    clientController.kickUser(client.getUserName(), toUser);
                }
                catch (RemoteException ex) {
                    StartScreen.showErrorMessage("Connection with server lost");
                    System.exit(0);
                    //ex.printStackTrace();
                }

            }
            else if (e.getSource() == promoteToManagerButton)
            {

                IClientController clientController = client.getClientController();
                String toUser = kickUserComboBox.getSelectedItem().toString();

                try {
//                    System.out.println("Promote to manager button pressed");
                    clientController.assignAdmin(client.getUserName(), toUser);
                }
                catch (RemoteException ex) {
                    StartScreen.showErrorMessage("Connection with server lost");
                    System.exit(0);
                    //ex.printStackTrace();
                }

            }
        }

    };

}
