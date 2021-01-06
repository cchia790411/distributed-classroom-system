package remote;

import java.awt.*;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IDrawingUpdate extends Remote, Serializable {
    boolean notifyTextDrawing(String fromClient, String text, Font font, Color color, Point startPoint) throws RemoteException;
    boolean notifyDrawing(String fromClient, Shape drawing, String mode, Color color, int strokeSize) throws RemoteException;
    boolean notifyDraggingDrawing(String fromClient, Shape drawing, String mode, Color color, int strokeSize) throws RemoteException;
    boolean notifyCanvasClearance(String fromClient) throws RemoteException;
    boolean receiveImage(byte[] rawImage) throws RemoteException;
    boolean notifyUserIsDrawing(String fromClient) throws RemoteException;
    void notifyUserStoppedDrawing(String fromClient) throws RemoteException;
}
