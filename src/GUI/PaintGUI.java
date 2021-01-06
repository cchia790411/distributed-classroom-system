package GUI;

import client.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

public class PaintGUI extends JPanel {

    Client client;
    DrawingArea drawingArea;

    String[] shapes = {"Freehand", "Line", "Circle", "Rectangle", "Oval", "Eraser", "Text"};
    String[] strokes = {"Thin", "Medium", "Thick"};
    String[] eraserSizes = {"Small", "Medium", "Large"};
    JFrame frame;
    JButton clearBtn, newBtn, openBtn, saveBtn, saveAsBtn;
    JButton freehandBtn, lineBtn, circleBtn, rectBtn, ovalBtn, eraserBtn, textBtn;
    JButton colorPaletteBtn, setFontBtn;
    JTextField textInput, textSize;
    JComboBox strokeOptions;
    JComboBox eraserSizeOptions;

    String filePath = "";
    Color currentColor = Color.BLACK;

    JPanel global = new JPanel();
    JPanel toolbox = new JPanel();
    JPanel toolbox1 = new JPanel();
    JPanel toolbox2 = new JPanel();
    JPanel fileControl = new JPanel();
    JFileChooser fileChooser= new JFileChooser();

    /// GUI setup ///
    public PaintGUI(Client client) {

        this.client = client;

/// Main drawing area ///
        drawingArea = new DrawingArea(client);
//        drawingArea.setPreferredSize(new Dimension(600, 620));

/// Set up main frame and container ///
        global.setLayout(new BorderLayout());
        toolbox.setLayout(new BorderLayout());

        /// Set up button icons ///
        try {
            BufferedImage paletteIcon = ImageIO.read(getClass().getResource("/palette.png"));
            BufferedImage freehandIcon = ImageIO.read(getClass().getResource("/freehand.png"));
            BufferedImage lineIcon = ImageIO.read(getClass().getResource("/line.png"));
            BufferedImage circleIcon = ImageIO.read(getClass().getResource("/circle.png"));
            BufferedImage rectIcon = ImageIO.read(getClass().getResource("/rectangle.png"));
            BufferedImage ovalIcon = ImageIO.read(getClass().getResource("/oval.png"));
            BufferedImage eraserIcon = ImageIO.read(getClass().getResource("/eraser.png"));
            BufferedImage textIcon = ImageIO.read(getClass().getResource("/text.png"));

            colorPaletteBtn = new JButton(new ImageIcon(paletteIcon));
            colorPaletteBtn.addActionListener(actionListener);
            freehandBtn = new JButton(new ImageIcon(freehandIcon));
            freehandBtn.addActionListener(actionListener);
            lineBtn = new JButton(new ImageIcon(lineIcon));
            lineBtn.addActionListener(actionListener);
            circleBtn = new JButton(new ImageIcon(circleIcon));
            circleBtn.addActionListener(actionListener);
            rectBtn = new JButton(new ImageIcon(rectIcon));
            rectBtn.addActionListener(actionListener);
            ovalBtn = new JButton(new ImageIcon(ovalIcon));
            ovalBtn.addActionListener(actionListener);
            eraserBtn = new JButton(new ImageIcon(eraserIcon));
            eraserBtn.addActionListener(actionListener);
            textBtn = new JButton(new ImageIcon(textIcon));
            textBtn.addActionListener(actionListener);
        } catch (Exception e) {
            StartScreen.showErrorMessage("Error setting up canvas buttons");
            //e.printStackTrace();
        }

        strokeOptions = new JComboBox(strokes);
        strokeOptions.setSelectedItem("Small");
        strokeOptions.addActionListener(actionListener);
        eraserSizeOptions = new JComboBox(eraserSizes);
        eraserSizeOptions.setSelectedItem("Small");
        eraserSizeOptions.addActionListener(actionListener);

        //        setFontBtn = new JButton("Font");
//        setFontBtn.addActionListener(actionListener);
        textInput = new JTextField("Text here.");
        textInput.setColumns(13);
        textInput.addFocusListener(focusListener);
//        textInput.setVisible(false);
        textSize = new JTextField("12");
        textSize.setColumns(2);
        textSize.addFocusListener(focusListener);
//        textSize.setVisible(false);
        setTextDetail();

/// Set up elements ///
        clearBtn = new JButton("Clear");
        clearBtn.addActionListener(actionListener);
        newBtn = new JButton("New");
        newBtn.addActionListener(actionListener);
        openBtn = new JButton("Open");
        openBtn.addActionListener(actionListener);
        saveBtn = new JButton("Save");
        saveBtn.addActionListener(actionListener);
        saveAsBtn = new JButton("Save As");
        saveAsBtn.addActionListener(actionListener);

/// Toolbox panel ///
        toolbox1.add(colorPaletteBtn);
        toolbox1.add(freehandBtn);
        toolbox1.add(lineBtn);
        toolbox1.add(circleBtn);
        toolbox1.add(rectBtn);
        toolbox1.add(ovalBtn);
        toolbox1.add(strokeOptions);
        toolbox2.add(textBtn);
        toolbox2.add(textInput);
//        toolbox2.add(setFontBtn);
        toolbox2.add(textSize);
        toolbox2.add(eraserBtn);
        toolbox2.add(eraserSizeOptions);

/// File control panel ///
        fileControl.add(newBtn);
        fileControl.add(openBtn);
        fileControl.add(saveBtn);
        fileControl.add(saveAsBtn);
        fileControl.add(clearBtn);

/// Layout ///
        toolbox.add(toolbox1, BorderLayout.NORTH);
        toolbox.add(toolbox2, BorderLayout.SOUTH);
        global.add(fileControl, BorderLayout.NORTH);
        global.add(drawingArea);
        global.add(toolbox, BorderLayout.SOUTH);
    }

    public JPanel getGlobal() {
        return global;
    }

    public DrawingArea getDrawingArea() { return drawingArea; }

    ActionListener actionListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {

/// Clear button ///
            if (e.getSource() == clearBtn) {
                int reply = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to clear the canvas?",
                        "Canvas clearance", JOptionPane.YES_NO_OPTION);

                if( reply == JOptionPane.YES_OPTION )
                {
                    try
                    {
                        client.getDrawingController().broadcastClearCanvas(client.getUserName());
                    }
                    catch (RemoteException err)
                    {
                        JOptionPane.showMessageDialog(null,
                                "Error in clearing the canvas", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    drawingArea.clear();
                }

/// Create new canvas ///
            } else if (e.getSource() == newBtn) {
                File file;

                int returnVal = JOptionPane.showConfirmDialog(new JFrame(), "Save your current whiteboard?", "Save or Discard", JOptionPane.YES_NO_CANCEL_OPTION);
                if (returnVal == JOptionPane.YES_OPTION) {

                    if (filePath.isEmpty()) {
                        file = chooseSaveFile();

                    } else {
                        file = new File(filePath);

                    }

                    drawingArea.saveAsPNGFile(file);

                    try
                    {
                        client.getDrawingController().broadcastClearCanvas(client.getUserName());
                    }
                    catch (RemoteException err)
                    {
                        JOptionPane.showMessageDialog(null,
                                "Error in clearing the canvas", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    drawingArea.clear();

                } else if (returnVal == JOptionPane.NO_OPTION) {

                    try
                    {
                        client.getDrawingController().broadcastClearCanvas(client.getUserName());
                    }
                    catch (RemoteException err)
                    {
                        JOptionPane.showMessageDialog(null,
                                "Error in clearing the canvas", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    drawingArea.clear();

                }

// Open file (PNG only) ///
            } else if (e.getSource() == openBtn) {
                int returnVal = fileChooser.showOpenDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    drawingArea.openFile(file);

                    try
                    {
                        client.getDrawingController().broadcastClearCanvas(client.getUserName());
                    }
                    catch (RemoteException err)
                    {
                        StartScreen.showErrorMessage("Connection with server lost");
                        System.exit(0);
                        //err.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        javax.imageio.ImageIO.write(ImageIO.read(file), "png", baos);
                        baos.flush();
                        baos.close();
                    }
                    catch (IOException err) {
                        StartScreen.showErrorMessage("Error writing file");
                        //err.printStackTrace();
                    }

                    try {
                        if (client.getDrawingController().updateImage(baos.toByteArray())) {
                            client.getDrawingController().broadcastUpdateImage(client.getUserName());
                        }
                    } catch (RemoteException ex) {
                        StartScreen.showErrorMessage("Connection with server lost");
                        System.exit(0);
                        //ex.printStackTrace();
                    }
                }



/// Save under project directory without filename (PNG file with default filename) ///
            } else if (e.getSource() == saveBtn) {

                File file;

                if (filePath.isEmpty()) {
                    file = chooseSaveFile();
                } else {
                    file = new File(filePath);

                }
                if (file != null) {
                    if (filePath.endsWith(".jpg")) {
                        drawingArea.saveAsJPGFile(file);
                    } else if (filePath.endsWith(".png")) {
                        drawingArea.saveAsPNGFile(file);
                    }
                }

/// Save with other filename (PNG only) ///
            } else if (e.getSource() == saveAsBtn) {

                File file = chooseSaveFile();

                if (file != null) {
                    if (filePath.endsWith(".jpg")) {
                        drawingArea.saveAsJPGFile(file);
                    } else if (filePath.endsWith(".png")) {
                        drawingArea.saveAsPNGFile(file);
                    }
                }

/// Choose drawing color ///
            } else if (e.getSource() == colorPaletteBtn) {
                drawingArea.setColor(JColorChooser.showDialog(null, "Choose a Color", currentColor));

/// Choose drawing tool ///
            } else if (e.getSource() == freehandBtn) {
                drawingArea.setModeFreehand();

            } else if (e.getSource() == lineBtn) {
                drawingArea.setModeLine();

            } else if (e.getSource() == circleBtn) {
                drawingArea.setModeCircle();

            } else if (e.getSource() == rectBtn) {
                drawingArea.setModeRectangle();

            } else if (e.getSource() == ovalBtn) {
                drawingArea.setModeOval();

            } else if (e.getSource() == eraserBtn) {
                drawingArea.setModeErase();

            } else if (e.getSource() == textBtn) {
    //                textInput.setVisible(true);
    //                textSize.setVisible(true);
                setTextDetail();
            } else if (e.getSource() == strokeOptions) {
                String strokeChosen = (String) strokeOptions.getSelectedItem();

                switch (strokeChosen) {
                    case "Thin":
                        drawingArea.setStroke(3);
                        break;
                    case "Medium":
                        drawingArea.setStroke(6);
                        break;
                    case "Thick":
                        drawingArea.setStroke(10);
                        break;
                }

            } else if (e.getSource() == eraserSizeOptions) {
                String eraserSizeChosen = (String) eraserSizeOptions.getSelectedItem();

                switch (eraserSizeChosen) {
                    case "Small":
                        drawingArea.setEraserSize(10);
                        break;
                    case "Medium":
                        drawingArea.setEraserSize(20);
                        break;
                    case "Large":
                        drawingArea.setEraserSize(30);
                        break;
                }
            }
        }
    };

    FocusListener focusListener = new FocusListener() {
        @Override
        public void focusGained(FocusEvent e) {
            setTextDetail();
        }

        @Override
        public void focusLost(FocusEvent e) {
            setTextDetail();
        }
    };

    private File chooseSaveFile() {

        fileChooser = new JFileChooser();
        FileFilter png = new FileNameExtensionFilter("PNG format", "png");
        FileFilter jpg = new FileNameExtensionFilter("JPG format", "jpg");
        fileChooser.addChoosableFileFilter(png);
        fileChooser.addChoosableFileFilter(jpg);
        fileChooser.setFileFilter(png);
        int returnVal = fileChooser.showSaveDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            filePath = file.getAbsolutePath();

            if (fileChooser.getFileFilter().getDescription().equals("JPG format")) {
                if (!filePath.toLowerCase().endsWith(".jpg")) {
                    filePath = filePath + ".jpg";
                    file = new File(file + ".jpg");
                }
                return file;
            } else {
                if (!filePath.toLowerCase().endsWith(".png")) {
                    filePath = filePath + ".png";
                    file = new File(file + ".png");
                }
                return file;
            }

        } else {
            return null;
        }
    }


    private void setTextDetail() {
        String textString = textInput.getText();
        int size = 12;  // default
        if (!textSize.getText().isEmpty()) {
            size = Integer.parseInt(textSize.getText());
        }
        else {
            textSize.setText(Integer.toString(size));
        }

        drawingArea.setModeText(textString, size);
    }

    public void disableFileControl() {
        newBtn.setEnabled(false);
        openBtn.setEnabled(false);
        saveBtn.setEnabled(false);
        saveAsBtn.setEnabled(false);
        clearBtn.setEnabled(false);
    }

    public void enableFileControl() {
        newBtn.setEnabled(true);
        openBtn.setEnabled(true);
        saveBtn.setEnabled(true);
        saveAsBtn.setEnabled(true);
        clearBtn.setEnabled(true);
    }

}