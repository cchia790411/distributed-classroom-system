package GUI;

import client.Client;
import remote.IDrawingController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;

public class DrawingArea extends JPanel implements MouseMotionListener, MouseListener, Serializable {

    /// enum for all different mode ///
    enum Mode { FREEHAND, LINE, CIRCLE, RECTANGLE, OVAL, ERASE, TEXT }

    /// Canvas size parameter ///
    private final static int AREA_WIDTH = 600;
    private final static int AREA_HEIGHT = 600;

    /// Shape to be drawn on the canvas ///
    private Client client;

    private Point startPoint;
    private Point previousPoint;
    private Point currentPoint;

    /// Default mode and color ///
    private Mode currentMode;
    private Color shapeColor;
    private Stroke lineStroke;
    private int strokeSize;
    private int eraserSize;
    private int textSize;
    private String textString;

    /// Create a empty canvas //
    private BufferedImage image;
    private Graphics2D g2;
    private Shape drawing;

    public DrawingArea(Client client) {
        this.client = client;
        setBackground(Color.WHITE); // Set Background color
        setDoubleBuffered(false);   // Non-buffered drawing
        image = new BufferedImage(AREA_WIDTH, AREA_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        shapeColor =  new Color(0, 0, 0);
        currentMode = Mode.FREEHAND;
        strokeSize = 3;
        lineStroke = new BasicStroke(strokeSize);
        eraserSize = 10;
        textSize = 60;
        textString = "Text here.";
        drawing = null;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public Shape getDrawing() {
        return drawing;
    }

    public void setDrawing(Shape drawing) {
        this.drawing = drawing;
    }

    public Graphics2D getG2() { return g2; }


    public void setG2(Graphics2D g2) { this.g2 = g2; }


    public BufferedImage getImage() { return image; }

    public void setImage(BufferedImage image) { this.image = image; }


    @Override
    public Dimension getPreferredSize()
    {
        return isPreferredSizeSet() ?
                super.getPreferredSize() : new Dimension(AREA_WIDTH, AREA_HEIGHT);
    }

/// Create a white image to clear previous drawing ///
    public void clear() {

        image = new BufferedImage(AREA_WIDTH, AREA_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D) image.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        repaint();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

/// Draw the whole image on JPanel ///
        if (image != null) {
                g.drawImage(image, 0, 0, null);
            }

/// Draw temporary shape ///
        if (drawing != null) {
            Graphics2D g2 = (Graphics2D) g;

/// Eraser has no border color ///
            Color borderColor = currentMode != Mode.ERASE ? shapeColor : Color.WHITE;
            g2.setColor(borderColor);
            g2.setStroke(lineStroke);
            g2.draw(drawing);
        }
    }

/// File manipulations (PNG only) ///

    public void saveAsPNGFile(File file) {
        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            StartScreen.showErrorMessage("Error in saving PNG");
            //e.printStackTrace();
        }
    }

    public void saveAsJPGFile(File file) {

        BufferedImage imageJPG = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        imageJPG.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
        try {
            ImageIO.write(imageJPG, "JPG", file);
        } catch (IOException e) {
            StartScreen.showErrorMessage("Error in saving JPG");
            //e.printStackTrace();
        }
    }

    public void openFile(File file) {
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            StartScreen.showErrorMessage("Error in opening file");
            //e.printStackTrace();
        }
        repaint();
    }

/// Drawing mode setters ///

    public void setModeFreehand() {
        currentMode = Mode.FREEHAND;
    }

    public void setModeLine() {
        currentMode = Mode.LINE;
    }

    public void setModeCircle() {
        currentMode = Mode.CIRCLE;
    }

    public void setModeRectangle() {
        currentMode = Mode.RECTANGLE;
    }

    public void setModeOval() {
        currentMode = Mode.OVAL;
    }

    public void setModeErase() {
        currentMode = Mode.ERASE;
    }

    public void setModeText(String string, int size) {
        currentMode = Mode.TEXT;
        textString = string;
        textSize = size;
    }

/// Drawing color setters ///

    public void setColor(Color color) {
        shapeColor = color;
    }

/// Drawing stroke setter ///

    public void setStroke(int size) {
        strokeSize = size;
        lineStroke = new BasicStroke(strokeSize);
    }

    public void setEraserSize(int size) {
        eraserSize = size;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        startPoint = e.getPoint();

/// Instantiate object based on current mode ///
        switch (currentMode) {

            case TEXT:
                g2.setColor(shapeColor);
                g2.setFont(new Font("TimesRoman", Font.PLAIN, textSize));
                g2.drawString(textString, startPoint.x, startPoint.y);
                try {
                    client.getDrawingController().broadcastText(client.getUserName(), textString, g2.getFont(), shapeColor, startPoint);
                } catch (RemoteException ex) {
                    StartScreen.showErrorMessage("Connection with server lost");
                    System.exit(0);
                    //ex.printStackTrace();
                }
                repaint();
                break;
            default:
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        g2 = (Graphics2D) image.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        startPoint = previousPoint = e.getPoint();

/// Instantiate object based on current mode ///
        switch (currentMode) {

            case FREEHAND:
            case LINE:

                drawing = new Line2D.Double();
                break;

            case ERASE:
            case CIRCLE:
            case OVAL:

                drawing = new Ellipse2D.Double();
                break;

            case RECTANGLE:

                drawing = new Rectangle2D.Double();
                break;

            case TEXT:

//                g2.setFont(new Font("TimesRoman", Font.PLAIN, textSize));
//                g2.drawString(textString, startPoint.x, startPoint.y);
//                try {
//                    client.getDrawingController().broadcastText(client.getUserName(), textString, g2.getFont(), startPoint);
//                } catch (RemoteException ex) {
//                    ex.printStackTrace();
//                }
//                repaint();
//                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        IDrawingController drawingController = client.getDrawingController();
        switch (currentMode) {
            case FREEHAND:
                if (startPoint.equals(previousPoint)) {
                    ((Line2D) drawing).setLine(startPoint, startPoint);
                    g2.setColor(shapeColor);
                    g2.setStroke(lineStroke);
                    g2.draw(drawing);
                }
                break;
            case OVAL:
            case RECTANGLE:
            case CIRCLE:
            case LINE:
                g2.setColor(shapeColor);
                // g2.fill(drawing);    /// Uncomment the line to fill the shapes with color ///
                g2.setStroke(lineStroke);
                g2.draw(drawing);
                break;
            case TEXT:
            case ERASE:
            default:
                break;
        }
        g2.setColor(shapeColor);

        /// This repaint is needed if we want to fill the drawing shape with color
        repaint();

        try {
            drawingController.broadcastDrawing(client.getUserName(), drawing, currentMode.toString(), shapeColor, strokeSize);

//            if (currentMode == Mode.TEXT){
//                notifyUsingTimer();
//            }
//            else {
//                drawingController.broadcastDrawingUserStopped(client.getUserName());
//            }


        } catch (RemoteException ex)
        {
            StartScreen.showErrorMessage("Connection with server lost");
            System.exit(0);
            //ex.printStackTrace();
        }

        drawing = null;
    }


//    private void notifyUsingTimer() throws RemoteException {
//
//        Timer timer = new Timer(4000, new ActionListener()
//        {
//
//            @Override
//            public void actionPerformed(ActionEvent e)
//            {
//                try {
//                    client.getDrawingController().broadcastDrawingUserStopped(client.getUserName());
//                } catch (RemoteException ex) {
//                    ex.printStackTrace();
//                }
//
//            }
//
//        });
//        try {
//            client.getDrawingController().broadcastDrawingUser(client.getUserName());
//        } catch (RemoteException ex) {
//            ex.printStackTrace();
//        }
//        timer.start();
//    }


    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        IDrawingController drawingController = client.getDrawingController();

        currentPoint = e.getPoint();
        int x = Math.min(startPoint.x, e.getX());
        int y = Math.min(startPoint.y, e.getY());
        int width = Math.abs(startPoint.x - e.getX());
        int height = Math.abs(startPoint.y - e.getY());

        switch (currentMode) {

/// Freehand drawing is continuously drawing line from current point to previous point ///
            case FREEHAND:

                ((Line2D) drawing).setLine(currentPoint, previousPoint);
                g2.setColor(shapeColor);
                g2.setStroke(lineStroke);
                g2.draw(drawing);
                previousPoint = currentPoint;
                break;

/// Single line ///
            case LINE:

                ((Line2D) drawing).setLine(startPoint, currentPoint);
                break;

/// Eraser is continuously drawing "small white circle" from current point to previous point ///
            case ERASE:

                ((Ellipse2D) drawing).setFrame((currentPoint.getX() - (eraserSize / 2)), (currentPoint.getY() - (eraserSize / 2)), eraserSize, eraserSize);
                g2.setColor(Color.WHITE);
                g2.fill(drawing);
                g2.draw(drawing);
                break;

/// Single circle (How to draw more intuitively?)///
            case CIRCLE:

                double radius = Math.sqrt(width * width + height * height);
//                        ((Ellipse2D) drawing).setFrame(x, y, radius, radius);
                ((Ellipse2D) drawing).setFrame(startPoint.getX() - radius, startPoint.getY() - radius, 2 * radius, 2 * radius);
                break;

/// Single oval ///
            case OVAL:

                ((Ellipse2D) drawing).setFrame(x, y, width, height);
                break;

/// Single rectangle ///
            case RECTANGLE:

                ((Rectangle2D) drawing).setFrame(x, y, width, height);
                break;

            case TEXT:
                break;
        }
        g2.setStroke(lineStroke);
        repaint();
        try {
            if (currentMode != Mode.TEXT){
                drawingController.broadcastDraggingDrawing(client.getUserName(), drawing, currentMode.toString(), shapeColor, strokeSize);

//                drawingController.broadcastDrawingUser(client.getUserName());

            }

        } catch (RemoteException ex) {
            StartScreen.showErrorMessage("Connection with server lost");
            System.exit(0);
            //ex.printStackTrace();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}