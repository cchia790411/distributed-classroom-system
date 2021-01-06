package GUI;

import client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

public class ApplicationMain extends JPanel {
    private Client client;
    private ChatScreen chatScreen;
    private PaintGUI paintGUI;
    JFrame frame;

    public ChatScreen getChatScreen() { return chatScreen; }

    public PaintGUI getPaintGUI() { return paintGUI; }

    public JFrame getFrame() {
        return frame;
    }

    public int showManagerQuitMessage() {
        int answer = JOptionPane.showConfirmDialog(null,
                "Do you want to terminate the application for all the users?",
                "Close the application", JOptionPane.YES_NO_CANCEL_OPTION);
        return answer;
    }

    public int showNextManagerMessage() {
        int answer = JOptionPane.showConfirmDialog(null,
                "Before leaving, do you want to choose the next manager manually?",
                "Close the application", JOptionPane.YES_NO_CANCEL_OPTION);
        return answer;
    }

    public String showAssignManagerMessage(int numUsers) {
        String[] userOptions = new String[numUsers];
        for (int i = 0; i < numUsers; i++) {
            userOptions[i] = client.getChatScreen().getKickUserComboBox().getItemAt(i).toString();
        }
        String input = (String) JOptionPane.showInputDialog(null, "Choose the next manager",
                "Assign a new manager before leaving", JOptionPane.QUESTION_MESSAGE, null,
                userOptions, // Array of choices
                userOptions[0]); // Initial choice
        return input;
    }

    public ApplicationMain(Client client) {
        this.client = client;
        this.chatScreen = new ChatScreen(client);
        this.paintGUI = new PaintGUI(client);
    }

    public void createAndShowGUI() {
        frame = new JFrame("Application Main");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(paintGUI.getGlobal(), BorderLayout.WEST);
        content.add(chatScreen.panel2, BorderLayout.EAST);
        chatScreen.setUserName(client.getUserName());


//        try {
//            // Update canvas
//            ArrayList<Shape> shapeList = client.getDrawingController().getShapeList();
//            ArrayList<Color> colorList = client.getDrawingController().getColorList();
//            ArrayList<Integer> strokeSizeList = client.getDrawingController().getStrokeSizeList();
//
//            ArrayList<String> textList = client.getDrawingController().getTextList();
//            ArrayList<Font> fontList = client.getDrawingController().getFontList();
//            ArrayList<Point> textStartPointList = client.getDrawingController().getTextStartPointList();

//            Graphics2D g2 = paintGUI.getDrawingArea().getG2();
//            for (int i = 0; i < textList.size(); i++) {
//                g2.setFont(fontList.get(i));
//                g2.drawString(textList.get(i), textStartPointList.get(i).x, textStartPointList.get(i).y);
//                client.getApplicationMain().getPaintGUI().getDrawingArea().repaint();
//            }

//            for (int i = 0; i < shapeList.size(); i++) {
//                g2.setStroke(new BasicStroke(strokeSizeList.get(i)));
//                g2.setColor(colorList.get(i));
//                g2.draw(shapeList.get(i));
//                paintGUI.getDrawingArea().repaint();
//            }
//        }
//        catch (RemoteException e) {
//            e.printStackTrace();
//        }

        try {
            client.getDrawingController().getImage(client.getUserName());
        }
        catch (RemoteException err)
        {
            StartScreen.showErrorMessage("Connection with server lost");
            System.exit(0);
            //err.printStackTrace();
        }


        SwingUtilities.getRootPane(chatScreen.getSendButton()).setDefaultButton(chatScreen.getSendButton());
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent arg0)
            {
                closeWindow();
            }
        });

        frame.setSize(1200, 700);
        frame.setLocationRelativeTo( null );
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void exitApplication(){
        frame.setVisible(false);
        client.setVisibleStartScreen();
    }

    public void setVisible() {
        frame.setVisible(true);
        chatScreen.setUserName(client.getUserName());
    }

    public void closeWindow() {
        try {
            int numUsers = client.getChatScreen().getKickUserComboBox().getItemCount();
            if (client.getUserName().equals(client.getClientController().getAdmin()) && numUsers > 0) {
                int terminateAppAnswer = showManagerQuitMessage();
                // If the manager terminates the application
                if (terminateAppAnswer == 0) {
                    client.getClientController().kickAll(client.getUserName());
                    exitApplication();
                }
                else if (terminateAppAnswer == 1) {
                    int answer = showNextManagerMessage();
                    // If the manager wants to assign the next manager manually
                    if (answer == 0) {
                        String newManager = showAssignManagerMessage(numUsers);
                        client.getClientController().assignAdmin(client.getUserName(), newManager);
                        client.getClientController().quit(client.getUserName());
                        exitApplication();
                    }
                    // If the manager wants to assign the next manager by random choice
                    if (answer == 1) {
                        Random random = new Random();
                        int randomUserIndex = random.nextInt(numUsers);
                        String newManager = client.getChatScreen().getKickUserComboBox().getItemAt(randomUserIndex).toString();
                        client.getClientController().assignAdmin(client.getUserName(), newManager);
                        client.getClientController().quit(client.getUserName());
                        exitApplication();
                    }
                }
            }
            else {
                int reply = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to quit the session?",
                        "Shut down session", JOptionPane.YES_NO_OPTION);
                if (reply == 0) {
                    client.getClientController().quit(client.getUserName());
                    exitApplication();
                }
            }
        } catch (RemoteException e)
        {
            StartScreen.showErrorMessage("Connection with server lost");
            System.exit(0);
            //e.printStackTrace();
        }
    }

}
