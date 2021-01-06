package server;

import client.Client;
import remote.IChatUpdate;
import remote.IDrawingController;
import remote.IDrawingUpdate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DrawingController extends UnicastRemoteObject implements IDrawingController {

    private final static int AREA_WIDTH = 600;
    private final static int AREA_HEIGHT = 600;

    private Server server;
    private BufferedImage bufferedImage;
    private Graphics2D g2;


    protected DrawingController(Server server) throws RemoteException {
        this.server = server;

        this.bufferedImage = new BufferedImage(AREA_WIDTH, AREA_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D) bufferedImage.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    }

    @Override
    public boolean broadcastText(String fromClient, String text, Font font, Color color, Point startPoint) throws RemoteException {
//        System.out.print("Broadcasting drawing to everyone...");

        g2 = (Graphics2D) bufferedImage.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(font);
        g2.setPaint(color);
        g2.drawString(text, startPoint.x, startPoint.y);
        g2 = (Graphics2D) bufferedImage.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        IDrawingUpdate client;

        for( User u : server.users )
        {
            if (!u.getUserName().equals(fromClient)) {
                client = u.getIDrawingUpdate();
                client.notifyTextDrawing(fromClient, text, font, color, startPoint);
            }
        }

//        System.out.print("...DONE\n");

        return true;
    }


    @Override
    public boolean broadcastDrawing(String fromClient, Shape drawing, String mode, Color color, int strokeSize) throws RemoteException {
//        System.out.print("Broadcasting drawing to everyone...");

        g2 = (Graphics2D) bufferedImage.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        switch (mode) {
            case "OVAL":
            case "RECTANGLE":
            case "CIRCLE":
            case "FREEHAND":
            case "LINE":
                g2.setColor(color);
                // g2.fill(drawing);    /// Uncomment the line to fill the shapes with color ///
                g2.setStroke(new BasicStroke(strokeSize));
                g2.draw(drawing);
                break;
            case "TEXT":
            case "ERASE":
            default:
                break;
        }

        g2 = (Graphics2D) bufferedImage.getGraphics();
        g2.setColor(color);

        IDrawingUpdate client;

        for( User u : server.users )
        {
            if (!u.getUserName().equals(fromClient)) {
                client = u.getIDrawingUpdate();
                client.notifyDrawing(fromClient, drawing, mode, color, strokeSize);
            }
        }

//        System.out.print("...DONE\n");

        return true;
    }

    public boolean broadcastDraggingDrawing(String fromClient, Shape drawing, String mode, Color color, int strokeSize) throws RemoteException {
//        System.out.print("Broadcasting dragging drawing to everyone...");

        g2 = (Graphics2D) bufferedImage.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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

        g2 = (Graphics2D) bufferedImage.getGraphics();
        g2.setColor(color);

        IDrawingUpdate client;

        for( User u : server.users )
        {
            if (!u.getUserName().equals(fromClient)) {
                client = u.getIDrawingUpdate();
                client.notifyDraggingDrawing(fromClient, drawing, mode, color, strokeSize);
            }
        }

//        System.out.print("...DONE\n");

        return true;
    }

    public boolean broadcastClearCanvas(String fromClient) throws RemoteException {
//        System.out.print("Broadcasting canvas clearance to everyone...");

        IDrawingUpdate client;

        bufferedImage = new BufferedImage(AREA_WIDTH, AREA_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D) bufferedImage.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for( User u : server.users )
        {
            if (!u.getUserName().equals(fromClient)) {
                client = u.getIDrawingUpdate();
                client.notifyCanvasClearance(fromClient);
            }
        }

//        System.out.print("...DONE\n");

        return true;
    }


    public void broadcastDrawingUser (String fromClient) throws RemoteException {
//        System.out.println("Current client is drawing: " + fromClient);
        IDrawingUpdate client;
        for (User u: server.users){
            client = u.getIDrawingUpdate();
            client.notifyUserIsDrawing(fromClient);
        }

    }

    public void broadcastDrawingUserStopped (String fromClient) throws RemoteException{
//        System.out.println("Current user stopped drawing" + fromClient);
        IDrawingUpdate client;
        for (User u: server.users){
            client = u.getIDrawingUpdate();
            client.notifyUserStoppedDrawing(fromClient);
        }

    }



    public boolean broadcastUpdateImage(String fromClient) throws RemoteException {
//        System.out.print("Broadcasting new image loading to everyone...");

        try {

            // do whatever you wish with the image
            IDrawingUpdate client;

            for( User u : server.users )
            {
                if (!u.getUserName().equals(fromClient)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    client = u.getIDrawingUpdate();
                    try {
                        javax.imageio.ImageIO.write(bufferedImage, "png", baos);
                        baos.flush();
                        client.receiveImage(baos.toByteArray());
                    }
                    catch (IOException err) {
                        System.out.println(java.time.LocalTime.now() + " " + "Error in broadcasting the latest canvas.");
//                        err.printStackTrace();
                    }
                    baos.close();
                }
            }
        }
        catch (IOException err) {
            System.out.println(java.time.LocalTime.now() + " " + "Error in broadcasting the latest canvas.");
//            err.printStackTrace();
        }

//        System.out.print("...DONE\n");

        return true;
    }

    public boolean updateImage(byte[] rawImage) throws RemoteException {
        try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(rawImage));
            g2 = (Graphics2D) bufferedImage.getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(bufferedImage, null, 0, 0);
        }
        catch (IOException err) {
            System.out.println(java.time.LocalTime.now() + " " + "Error in updating canvas.");
//            err.printStackTrace();
        }
        return true;
    }

    public void getImage(String fromClient) throws RemoteException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        IDrawingUpdate client;

        try {
            javax.imageio.ImageIO.write(bufferedImage, "png", baos);
            baos.flush();
            baos.close();
        }
        catch (IOException err) {
            System.out.println(java.time.LocalTime.now() + " " + "Error in getting canvas.");
//            err.printStackTrace();
        }

        for( User u : server.users )
        {
            if (u.getUserName().equals(fromClient)) {
                client = u.getIDrawingUpdate();
                client.receiveImage(baos.toByteArray());
            }
        }
    }
}
