package remote;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * RMI Remote interface - must be shared between client and server.
 * All methods must throw RemoteException.
 * All parameters and return types must be either primitives or Serializable.
 *
 * Any object that is a remote object must implement this interface.
 * Only those methods specified in a "remote interface" are available remotely.
 */
public interface IDrawingController extends Remote {

    boolean broadcastText(String fromClient, String text, Font font, Color color, Point startPoint) throws RemoteException;
    boolean broadcastDrawing(String fromClient, Shape drawing, String mode, Color color, int strokeSize) throws RemoteException;
    boolean broadcastDraggingDrawing(String fromClient, Shape drawing, String mode, Color color, int strokeSize) throws RemoteException;
    boolean broadcastClearCanvas(String fromClient) throws RemoteException;
    boolean broadcastUpdateImage(String fromClient) throws RemoteException;
    boolean updateImage(byte[] rawImage) throws RemoteException;
    void broadcastDrawingUser (String fromClient) throws RemoteException;
    void getImage(String fromClient) throws RemoteException;
    void broadcastDrawingUserStopped (String fromClient) throws RemoteException;
}
