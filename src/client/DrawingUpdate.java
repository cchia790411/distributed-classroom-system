package client;

import GUI.StartScreen;
import remote.IDrawingUpdate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DrawingUpdate extends UnicastRemoteObject implements IDrawingUpdate, Serializable {

    private Client client;

    public DrawingUpdate(Client client) throws RemoteException {
        super();
        this.client = client;
    }

    public boolean notifyUserIsDrawing(String fromClient) throws RemoteException {
//        System.out.println("Adding name of user to the list of drawing users");
        DefaultListModel temp = client.getApplicationMain().getChatScreen().getAllUserModel();
        if(temp.contains(fromClient)){
//            System.out.println("Already in the list");
        }
        else {
            temp.addElement(fromClient);
        }

        return true;
    }

    public void notifyUserStoppedDrawing(String fromClient) throws RemoteException{
        DefaultListModel temp = client.getApplicationMain().getChatScreen().getAllUserModel();
        int elementIndex = -1;
        if(temp.contains(fromClient)){
            elementIndex = temp.indexOf(fromClient);
            temp.remove(elementIndex);
        }

    }

    @Override
    public boolean notifyTextDrawing(String fromClient, String text, Font font, Color color, Point startPoint) throws RemoteException {
        client.getApplicationMain().getPaintGUI().getDrawingArea().getG2().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        client.getApplicationMain().getPaintGUI().getDrawingArea().getG2().setFont(font);
        client.getApplicationMain().getPaintGUI().getDrawingArea().getG2().setColor(color);
        client.getApplicationMain().getPaintGUI().getDrawingArea().getG2().drawString(text, startPoint.x, startPoint.y);
        client.getApplicationMain().getPaintGUI().getDrawingArea().repaint();

        removeClientTimer removeClient = new removeClientTimer(fromClient, client);
        removeClient.start();
//
//        Timer timer = new Timer(1000, new ActionListener()
//        {
//            private int i = 0;
//            @Override
//            public void actionPerformed(ActionEvent e)
//            {
//                DefaultListModel temp = client.getApplicationMain().getChatScreen().getAllUserModel();
//                System.out.println("action performed" + Integer.toString(i));
//                int elementIndex = -1;
//                if(temp.contains(fromClient)){
//                    elementIndex = temp.indexOf(fromClient);
//                    temp.remove(elementIndex);
//                    System.out.println("removed from list");
//                }
//            }
//        });
//        timer.start();


        return true;
    }

    @Override
    public boolean notifyDrawing(String fromClient, Shape drawing, String mode, Color color, int strokeSize) throws RemoteException {
        Graphics2D g2 = client.getApplicationMain().getPaintGUI().getDrawingArea().getG2();
        switch (mode) {
            case "OVAL":
            case "RECTANGLE":
            case "CIRCLE":
            case "FREEHAND":
            case "LINE":
                g2.setColor(color);
                g2.setStroke(new BasicStroke(strokeSize));
                g2.draw(drawing);
                break;
            default:
//                System.out.println("Erased");
        }
        g2 = (Graphics2D) client.getApplicationMain().getPaintGUI().getDrawingArea().getImage().getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        client.getApplicationMain().getPaintGUI().getDrawingArea().repaint();

        DefaultListModel temp = client.getApplicationMain().getChatScreen().getAllUserModel();
        int elementIndex = -1;
        if(temp.contains(fromClient)){
            elementIndex = temp.indexOf(fromClient);
            temp.remove(elementIndex);
        }
        return true;
    }

    public boolean notifyDraggingDrawing(String fromClient, Shape drawing, String mode, Color color, int strokeSize) throws RemoteException {
        Graphics2D g2 = client.getApplicationMain().getPaintGUI().getDrawingArea().getG2();
        switch (mode) {
            case "FREEHAND":
                g2.setColor(color);
                g2.setStroke(new BasicStroke(strokeSize));
                g2.draw(drawing);
                break;

            case "ERASE":
                g2.setColor(Color.WHITE);
                g2.fill(drawing);
                g2.draw(drawing);
                break;

            default:
                break;
        }
        client.getApplicationMain().getPaintGUI().getDrawingArea().repaint();

        DefaultListModel temp = client.getApplicationMain().getChatScreen().getAllUserModel();
        if(temp.contains(fromClient)){
//            System.out.println("Already in the list");
        }
        else {
            temp.addElement(fromClient);
        }

        return true;
    }

    public boolean notifyCanvasClearance(String fromClient) throws RemoteException {
        client.getApplicationMain().getPaintGUI().getDrawingArea().clear();
        return true;
    }

    public boolean receiveImage(byte[] rawImage) throws RemoteException {
        try {
//            BufferedImage bufferedImage = client.getApplicationMain().getPaintGUI().getDrawingArea().getImage();
            BufferedImage bufferedImage = javax.imageio.ImageIO.read(new ByteArrayInputStream(rawImage));
            client.getApplicationMain().getPaintGUI().getDrawingArea().setImage(bufferedImage);
            client.getApplicationMain().getPaintGUI().getDrawingArea().setG2((Graphics2D) bufferedImage.getGraphics());
            client.getApplicationMain().getPaintGUI().getDrawingArea().getG2().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            client.getApplicationMain().getPaintGUI().getDrawingArea().repaint();

            // do whatever you wish with the image
        }
        catch (IOException err)
        {
            StartScreen.showErrorMessage("Error in receiving image");
            //err.printStackTrace();
        }
        return true;
    }
}

class removeClientTimer extends Thread {
    private String fromClient;
    private Client client;

    public removeClientTimer (String fromClient, Client client) {
        this.fromClient = fromClient;
        this.client = client;
    }

    @Override
    public void run()
    {
        try {
            DefaultListModel temp = client.getApplicationMain().getChatScreen().getAllUserModel();
            if(!temp.contains(fromClient)) temp.addElement(fromClient);

            sleep(1000);

            int elementIndex = -1;
            if(temp.contains(fromClient)){
                elementIndex = temp.indexOf(fromClient);
                temp.remove(elementIndex);
            }
        } catch (InterruptedException e)
        {
            StartScreen.showErrorMessage("Interrupted Exception");
            //e.printStackTrace();
        }
    }

}