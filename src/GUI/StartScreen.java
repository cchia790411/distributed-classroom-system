package GUI;

import client.Client;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class StartScreen {

    private JTextPane information;
    private JPanel mainPanel;
    private JTextField usernameField;
    private JButton joinButton;

    private JTextField textField2;
    private boolean kickedOut;
    private boolean appTerminated;

    private JTextField serverField;
    private JTextField textField3;
    private JPasswordField passwordField;
    private JPanel logInPanel;
    private JPanel joinButtonPanel;
    private JPanel titlePanel;
    private JPanel waitingPanel;
    private ButtonModel joinButtonModel = joinButton.getModel();

    JFrame frame;

    private Client client;

    public StartScreen(Client client)
    {
        this.client = client;
    }

    ChangeListener changeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (joinButtonModel.isPressed()){
                waitingPanel.setVisible(true);
                logInPanel.setVisible(false);
            }
            else {
                waitingPanel.setVisible(false);
                logInPanel.setVisible(true);
            }
        }
    };

    ActionListener actionListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == joinButton)
            {
                String serverAddress = serverField.getText();
                String userName = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (userName.length() <= 15) {
                    int connectionStatus = client.connect(userName, serverAddress, password);

                    if( connectionStatus == 0 )
                    {
                        frame.setVisible(false);
                        frame.dispose();
                        client.startApplication();
                    }
                    else if( connectionStatus == 1)
                    {
                        showErrorMessage("The manager rejected your join request");
                    }
                    else if( connectionStatus == 2)
                    {
                        showErrorMessage("Duplicate username: Please enter a different username");
                    }
                    else if( connectionStatus == 3 )
                    {
                        showErrorMessage("Cannot connect to server: Please check the server address");
                    }
                    else if( connectionStatus == 4 )
                    {
                        showErrorMessage("Incorrect Password");
                    }
                    else
                    {
                        showErrorMessage("Unknown Connection Status");
                    }
                }
                else {
                    showErrorMessage("Username must be less or equal to 15 characters");
                }
            }
        }

    };


    public static void showErrorMessage(String message)
    {
        JOptionPane.showMessageDialog(null,
            message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void go()
    {
        joinButton.addActionListener(actionListener);
        joinButtonModel.addChangeListener(changeListener);
        frame = new JFrame("StartScreen");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.getRootPane().setDefaultButton(joinButton);
        joinButton.requestFocus();

        //frame.getContentPane().add(new JPanelWithBackground("sample.jpeg"));
        frame.setVisible(true);

        frame.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                if (isAppTerminated()){
                    showKickAllMessage();
                    setAppTerminated(false);
                }
                if (isKickedOut()){
                    showKickOutMessage();
                    setKickedOut(false);
                }
            }
        });
    }

    public void setVisible(){
        frame.setVisible(true);
    }

    public void setKickedOut(boolean kickedOut) {
        this.kickedOut = kickedOut;
    }

    public void setAppTerminated(boolean appTerminated) {
        this.appTerminated = appTerminated;
    }

    public boolean isKickedOut() {
        return kickedOut;
    }

    public boolean isAppTerminated() {
        return appTerminated;
    }

    public void showKickAllMessage() {
        JOptionPane.showMessageDialog(null,
                "The manager terminated the application", "Application terminated",
                JOptionPane.ERROR_MESSAGE);
    }

    public void showKickOutMessage() {
        JOptionPane.showMessageDialog(null,
                "The manager kicked you out of the whiteboard", "Kicked out",
                JOptionPane.ERROR_MESSAGE);
    }

//    public class JPanelWithBackground extends JPanel
//    {
//
//        private Image backgroundImage;
//
//        // Some code to initialize the background image.
//        // Here, we use the constructor to load the image. This
//        // can vary depending on the use case of the panel.
//        public JPanelWithBackground(String fileName) throws IOException
//        {
//            backgroundImage = ImageIO.read(new File(fileName));
//        }
//
//        public void paintComponent(Graphics g) {
//            super.paintComponent(g);
//
//            // Draw the background image.
//            g.drawImage(backgroundImage, 0, 0, this);
//        }
//    }

}
