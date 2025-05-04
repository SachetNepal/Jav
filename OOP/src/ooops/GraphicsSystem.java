package ooops;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import uk.ac.leedsbeckett.oop.LBUGraphics;

public class GraphicsSystem extends LBUGraphics {

    private ArrayList<String> commandParts;              // Stores parts of input command
    private int commandLength;                           // Length of the command
    private ArrayList<String> commandHistory = new ArrayList<>(); // Stores executed command history

    // All supported commands
    private final List<String> validCommands = Arrays.asList(
        "move", "reverse", "left", "right", "penup", "pendown", "pencolor", "penwidth",
        "square", "circle", "triangle", "equilateral", "reset", "clear",
        "blue", "red", "green", "white", "pen", "about", "name", "save", "load",
        "screenshot", "image", "help", "nepal"
    );

    // Constructor: Set up UI and menus
    public GraphicsSystem() {
        JFrame mainWindow = new JFrame("Graphics System");
        mainWindow.setLayout(new FlowLayout());
        mainWindow.add(this);
        mainWindow.setSize(850, 450);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setVisible(true);

        // File menu setup
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem saveLogItem = new JMenuItem("Save Command Log");
        saveLogItem.addActionListener(e -> saveCommandLog());
        fileMenu.add(saveLogItem);

        JMenuItem saveItem = new JMenuItem("Save Commands");
        saveItem.addActionListener(e -> saveCommands(commandHistory));
        fileMenu.add(saveItem);

        JMenuItem loadItem = new JMenuItem("Load Commands");
        loadItem.addActionListener(e -> loadCommands());
        fileMenu.add(loadItem);

        menuBar.add(fileMenu);
        mainWindow.setJMenuBar(menuBar);
        JTextArea commandArea = new JTextArea(10, 40); // height, width
        commandArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(commandArea);
        mainWindow.add(scrollPane);  // Add it to the frame

    }

    // ========================== Drawing Features ============================

    public void square(int side) {
        drawOn();
        for (int i = 0; i < 4; i++) {
            forward(side);
            left(90);
        }
    }

    public void EquilateralTriangle(int side) {
        drawOn();
        for (int i = 0; i < 3; i++) {
            forward(side);
            left(120);
        }
    }

    public void Triangle(int a, int b, int c) {
        double angleA = Math.toDegrees(Math.acos((b * b + c * c - a * a) / (2.0 * b * c)));
        double angleB = Math.toDegrees(Math.acos((a * a + c * c - b * b) / (2.0 * a * c)));
        double angleC = Math.toDegrees(Math.acos((a * a + b * b - c * c) / (2.0 * a * b)));

        drawOn();
        forward(a);
        left((int) (180 - angleC));
        forward(b);
        left((int) (180 - angleA));
        forward(c);
        left((int) (180 - angleB));
    }

    public void myName() {
        Graphics g = getGraphics();
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("OSKAR", 320, 390);
    }

    // ========================== Pen Settings ============================

    public void penwidth(int width) {
        setStroke(width);
    }

    public void pencolour(int r, int g, int b) {
        setPenColour(new Color(r, g, b));
    }
    public void appendCommand(String command) {
        System.out.println(">> " + command);  // Echo to console
      
    }


    // ========================== Image and Command File Handlers ============================

    private void saveCommands(ArrayList<String> history) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Command File");
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.write(chooser.getSelectedFile().toPath(), history);
            } catch (IOException e) {
                showError("Could not save file.");
            }
        }
    }

    private void loadCommands() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load Command File");
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (Scanner scan = new Scanner(chooser.getSelectedFile())) {
                while (scan.hasNextLine()) {
                    processCommand(scan.nextLine());
                }
            } catch (FileNotFoundException e) {
                showError("Could not load file.");
            }
        }
    }

    private void saveAuto() {
        try {
            File dir = new File("command_logs");
            if (!dir.exists()) dir.mkdirs();
            String fileName = "commands_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt";
            File file = new File(dir, fileName);
            Files.write(file.toPath(), commandHistory);
            System.out.println("Auto-saved to " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Auto-save failed.");
        }
    }

    public void saveCommandLog() {
        if (commandHistory.isEmpty()) {
            showInfo("No commands to save.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Command Log (.txt)");

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.write(chooser.getSelectedFile().toPath(), commandHistory);
                showInfo("Command log saved.");
            } catch (IOException e) {
                showError("Error saving log!");
            }
        }
    }

    public void saveCanvasImageWithFormat() {
        Object[] options = {"PNG", "JPG"};
        int choice = JOptionPane.showOptionDialog(null, "Choose image format:", "Save Drawing",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == JOptionPane.CLOSED_OPTION) return;

        String format = (choice == 0) ? "png" : "jpg";
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Drawing");

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            if (!path.toLowerCase().endsWith("." + format)) path += "." + format;

            try {
                BufferedImage image = getBufferedImage();
                ImageIO.write(image, format, new File(path));
                showInfo("Drawing saved as " + format.toUpperCase());
            } catch (IOException e) {
                showError("Failed to save drawing!");
            }
        }
    }

    public void IMG() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Image");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath() + "/picture.png";
            try {
                Thread.sleep(120);
                Robot robot = new Robot();
                Rectangle capture = new Rectangle(0, 0, 900, 900);
                BufferedImage img = robot.createScreenCapture(capture);
                ImageIO.write(img, "png", new File(path));
                System.out.println("Screenshot saved");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void IMGLoad() throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open Image");

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            BufferedImage img = ImageIO.read(file);
            setBufferedImage(img);
            Graphics g = getGraphics();
            g.drawImage(img, 0, 0, this);
        }
    }

    // ========================== Command Parsing ============================

    public void processCommand(String command) {
        commandParts = new ArrayList<>(List.of(command.split(" ")));
        commandLength = commandParts.size();
        evaluate(command);               // Run it
        appendCommand(command);         // Show in console (or text area)
    }

 // This method processes and executes user commands from the input field
    public void evaluate(String val) {
        try {
            String cmd = commandParts.get(0); // Get the main command keyword (e.g., "move")

            // Check if the entered command is supported
            if (!validCommands.contains(cmd)) {
                showError("Invalid command entered.");
                return;
            }

            switch (cmd) {

                // Movement-related commands
                case "move":
                case "reverse":
                    if (commandLength < 2) {
                        showError("Please enter a distance.");
                        return;
                    }

                    int distance = Integer.parseInt(commandParts.get(1));
                    if (distance < 0) {
                        showError("Distance cannot be negative.");
                        return;
                    }

                    // Determine direction based on command
                    int directionMultiplier = cmd.equals("reverse") ? -1 : 1;

                    // Calculate new coordinates based on direction
                    double radians = Math.toRadians(getDirection());
                    int futureX = xPos + (int) (distance * Math.sin(radians)) * directionMultiplier;
                    int futureY = yPos + (int) (distance * Math.cos(radians)) * directionMultiplier;

                    // Ensure movement does not go outside the drawing canvas
                    if (futureX < 0 || futureX > getWidth() || futureY < 0 || futureY > getHeight()) {
                        showError("Move would go out of bounds!");
                        return;
                    }

                    forward(distance * directionMultiplier);
                    commandHistory.add(val);
                    saveAuto();
                    break;

                // Rotation commands
                case "left":
                case "right":
                    // Default to 90 degrees if no value is provided
                    int angle = (commandLength == 1) ? 90 : Integer.parseInt(commandParts.get(1));
                    if (angle < 0) {
                        showError("Angle cannot be negative.");
                        return;
                    }

                    if (cmd.equals("left")) left(angle);
                    else right(angle);

                    commandHistory.add(val);
                    saveAuto();
                    break;

                // Shape drawing commands (single-parameter shapes)
                case "square":
                    drawShapeWithOneParam(val, commandParts.get(1), this::square);
                    break;

                case "circle":
                    drawShapeWithOneParam(val, commandParts.get(1), this::circle);
                    break;

                case "equilateral":
                    drawShapeWithOneParam(val, commandParts.get(1), this::EquilateralTriangle);
                    break;

                // Triangle requires 3 side lengths
                case "triangle":
                    if (commandLength < 4) {
                        showError("Enter 3 sides for triangle.");
                        return;
                    }
                    int a = Integer.parseInt(commandParts.get(1));
                    int b = Integer.parseInt(commandParts.get(2));
                    int c = Integer.parseInt(commandParts.get(3));
                    Triangle(a, b, c);
                    commandHistory.add(val);
                    saveAuto();
                    break;

                // Pen up/down controls
                case "penup":
                    drawOff();
                    logCommand(val);
                    break;

                case "pendown":
                    drawOn();
                    logCommand(val);
                    break;

                // Pen width setting
                case "penwidth":
                    drawShapeWithOneParam(val, commandParts.get(1), this::penwidth);
                    break;

                // Predefined pen colors
                case "blue":
                    setPenColour(Color.blue);
                    logCommand(val);
                    break;

                case "red":
                    setPenColour(Color.red);
                    logCommand(val);
                    break;

                case "green":
                    setPenColour(Color.green);
                    logCommand(val);
                    break;

                case "white":
                    setPenColour(Color.white);
                    logCommand(val);
                    break;

                // Custom RGB pen color
                case "pencolor":
                case "pen":  // allow both "pencolor" and "pen"
                    if (commandLength < 4) {
                        showError("Enter 3 RGB values (0–255).");
                        return;
                    }

                    try {
                        int r = Integer.parseInt(commandParts.get(1));
                        int g = Integer.parseInt(commandParts.get(2));
                        int b1 = Integer.parseInt(commandParts.get(3));

                        if (r < 0 || r > 255 || g < 0 || g > 255 || b1 < 0 || b1 > 255) {
                            showError("RGB values must be between 0 and 255.");
                            return;
                        }

                        pencolour(r, g, b1);
                        logCommand(val);
                    } catch (NumberFormatException e) {
                        showError("RGB values must be valid numbers.");
                    }
                    break;


                // Save commands and canvas image
                case "save":
                    Object[] options = {"Save Commands (.txt)", "Save Canvas Image (.png/.jpg)"};
                    int choice = JOptionPane.showOptionDialog(
                        null,
                        "What would you like to save?",
                        "Save Options",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                    );

                    if (choice == 0) {
                        // Save command history
                        saveCommands(commandHistory);
                        System.out.println("Commands saved to .txt file.");
                    } else if (choice == 1) {
                        // Save canvas image
                        saveCanvasImageWithFormat();
                        System.out.println("Canvas saved as image.");
                    } else {
                        System.out.println("Save operation cancelled.");
                    }
                    logCommand(val);
                    break;


                case "load":
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Select Command File or Image");

                    int userSelection = fileChooser.showOpenDialog(null);
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        String fileName = selectedFile.getName().toLowerCase();

                        try {
                            if (fileName.endsWith(".txt")) {
                                // Load and execute commands from text file
                                Scanner scan = new Scanner(selectedFile);
                                while (scan.hasNextLine()) {
                                    String line = scan.nextLine();
                                    processCommand(line);
                                }
                                scan.close();
                                showInfo("Command file loaded successfully.");
                                System.out.println("Loaded commands from " + fileName);
                            } else if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                                // Load image to canvas
                                BufferedImage img = ImageIO.read(selectedFile);
                                this.setBufferedImage(img);
                                Graphics g = this.getGraphics();
                                g.drawImage(img, 0, 0, this);
                                showInfo("Image loaded successfully.");
                                System.out.println("Loaded image from " + fileName);
                            } else {
                                showError("Unsupported file type. Please select .txt or .png/.jpg image.");
                            }

                            logCommand(val);
                        } catch (IOException e) {
                            showError("Failed to load file: " + e.getMessage());
                        }
                    }
                    break;
                // Take a screenshot of the drawing area
                case "screenshot":
                    IMG();
                    logCommand(val);
                    break;

                // Load an external image onto canvas
                case "image":
                    try {
                        IMGLoad();
                        logCommand(val);
                    } catch (IOException e) {
                        e.printStackTrace();
                        showError("Image loading failed.");
                    }
                    break;

                // Reset turtle to initial state
                case "reset":
                    reset();
                    saveAuto();
                    break;

                // Clear canvas with optional save confirmation
                case "clear":
                    handleClear(val);
                    break;

                // Display user-defined name
                case "name":
                    myName();
                    logCommand(val);
                    break;

                // Display info about the system
                case "about":
                    about();
                    logCommand(val);
                    break;

                // Show help/instruction menu
                case "help":
                    Help();
                    logCommand(val);
                    break;
            }
        } catch (NumberFormatException e) {
            showError("Invalid number format in parameters.");
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
        }
    }
 // Logs a command and saves it automatically to the history
    private void logCommand(String val) {
        commandHistory.add(val);  // Add to history list
        saveAuto();               // Auto-save to file
    }

    // Executes drawing functions that take one int parameter (like square, circle, etc.)
    private void drawShapeWithOneParam(String val, String param, java.util.function.IntConsumer shapeFunction) {
        try {
            int size = Integer.parseInt(param);  // Parse the shape size
            if (size <= 0) {
                showError("Parameter must be positive.");
                return;
            }

            shapeFunction.accept(size);  // Call the actual drawing function
            logCommand(val);             // Log command after execution
        } catch (NumberFormatException e) {
            showError("Invalid number format."); // Handle wrong input (e.g., "abc")
        }
    }

    // Handles the "clear" command with a prompt to save changes
    private void handleClear(String val) {
        if (commandHistory.isEmpty()) {
            clear();           // If no commands in history, just clear directly
            logCommand(val);
            return;
        }

        // Ask user if they want to save before clearing
        int choice = JOptionPane.showConfirmDialog(
            null,
            "Save changes before clearing?",
            "Confirm",
            JOptionPane.YES_NO_CANCEL_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            saveCommands(commandHistory);  // Save command history
            clear();
            logCommand(val);
        } else if (choice == JOptionPane.NO_OPTION) {
            clear();
            logCommand(val);
        }
        // If CANCEL, do nothing
    }

    // ========================== Utility Methods ============================

    private void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    

    // ========================== About and Help ============================

    @Override
    public void about() {
        super.about();
        getGraphicsContext().drawString("Oskar Nepal", 200, 150);
    }

    public void Help() {
        JTextArea textArea = new JTextArea(
        		"ABOUT\n"+
        		        "------\n"+
        		        "about: Display the turtle dance moving round oop and the name of the user\n\n"+

        		        "PEN COMMANDS\n"+
        		        "---------\n"+
        		        "penwidth: sets the texture of pen color to more thickness\n"+
        		        "penup: lifts the pen so movement doesn’t draw\n" +
        		        "pendown: puts the pen down so movement draws a line\n" +
        		        "blue: Make the pen color blue\n"+
        		        "green: Makes the pen color green\n"+
        		        "red: Makes the pen color red\n"+
        		        "white: Makes the pen color white\n"+

        		        "SCREEN COMMANDS\n"+
        		        "---------------\n"+
        		        "clear: Clears the whole screen\n"+
        		        "reset: Moves the turtle back to the starting position, facing downward, without clearing the drawing\n"+
        		        "save: Provides options to save commands or save image\n"+
        		        "load: Provides options to load commands or load image\n"+

        		        "DRAWINGS\n"+
        		        "--------\n"+
        		        "circle angle: Draws a circle with the radius entered by the user\n"+
        		        "rectangle BREADTH HEIGHT: Draws a rectangle\n"+
        		        "square side: Draws a square with equal sides\n"+
        		        "equilateral 1POINT: Draws an equilateral triangle\n"+
        		        "triangle 3POINTS: Draws a triangle with three given points\n\n"+

        		        "LINE COMMANDS\n"+
        		        "-------------\n"+
        		        "move UNITS: Moves the turtle forward by given units\n"+
        		        "reverse UNITS: Moves the turtle backward by given units\n"+
        		        "left DEGREES: Turns the turtle to the left by given degrees\n"+
        		        "right DEGREES: Turns the turtle to the right by given degrees\n"+

        		        "HELP\n"+
        		        "----\n"+
        		        "help: Displays this help menu!"

        );
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(null, scrollPane, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    // ========================== Entry Point ============================

    public static void main(String[] args) {
        new GraphicsSystem();
    }
}
