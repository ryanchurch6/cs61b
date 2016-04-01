package editor;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.control.ScrollBar;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.io.*;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.shape.Rectangle;

public class Editor extends Application {
    private static int window_width = 500;
    private static int window_height = 500;
    private static int font_size = 12;
    private static String font_name = "Verdana";
    private Group root;
    private Rectangle cursorRectangle = new Rectangle();
    Grid parent;
    String inputFilename;
    boolean isDebugTrue;

    private class mouseClickEventHandler implements EventHandler<MouseEvent> {
        public mouseClickEventHandler(){
        }
        @Override public void handle(MouseEvent mouseEvent) {
            double mousePressedX = mouseEvent.getX();
            double mousePressedY = mouseEvent.getY();
            parent.mouseClick(mousePressedX, mousePressedY);
            cursorRectangle.setX(parent.getPointerXCoord());
            cursorRectangle.setY(parent.getPointerYCoord());
        }
    }

    public void makeCursorBlink() {
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        CursorBlinkEventHandler cursorChange = new CursorBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    private class CursorBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] cursorColors = {Color.WHITE, Color.BLACK};
        CursorBlinkEventHandler() {
            changeColor();
        }
        private void changeColor() {
            cursorRectangle.setFill(cursorColors[currentColorIndex]);
            if (currentColorIndex == 0) {
                currentColorIndex = 1;
            } else {
                currentColorIndex = 0;
            }
        }
        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }

    private class KeyEventHandler implements EventHandler<KeyEvent> {
        private int fontSize = font_size;
        private String fontName = "Verdana";
        private int pointerXPos;
        private int pointerYPos;
        private Group root;
        public ScrollBar scrollBar;

        public void changeCursorHeight(int newHeight) {
            cursorRectangle.setHeight(newHeight);
        }

        KeyEventHandler(final Group root, int x, int y, ScrollBar scrollBar) {
            pointerXPos = x;
            pointerYPos = y;
            this.root = root;
            this.scrollBar = scrollBar;
        }

        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
                String characterTyped = keyEvent.getCharacter();
                if (characterTyped.equals("\r")) {
                    parent.newLine();
                } else if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8 && !keyEvent.isShortcutDown()) {
                    parent.Type(characterTyped);
                }
                cursorRectangle.setX(parent.getPointerXCoord());
                cursorRectangle.setY(parent.getPointerYCoord());
                keyEvent.consume();
            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.P && keyEvent.isShortcutDown()) {
                    Integer x = parent.pointer.getXCoord(parent.pointer.xPosition());
                    Integer y = parent.lines()[parent.pointer.yPosition()].getyCoordinate();
                    System.out.println(x.toString() + ", " + y.toString());
                } else if (code == KeyCode.L && keyEvent.isShortcutDown() && isDebugTrue) {
                    parent.printAll();
                } else if ((code == KeyCode.ADD || code == KeyCode.PLUS || code == KeyCode.EQUALS)
                        && keyEvent.isShortcutDown()) {
                    int newFont = parent.incrementFontSize();
                    changeCursorHeight(newFont);
                } else if ((code == KeyCode.SUBTRACT || code == KeyCode.MINUS) && keyEvent.isShortcutDown()) {
                    int newFont = parent.decrementFontSize();
                    changeCursorHeight(newFont);
                } else if (code == KeyCode.BACK_SPACE) {
                    Text deleted = parent.backSpace();
                    root.getChildren().remove(deleted);
                } else if (code == KeyCode.LEFT || code == KeyCode.RIGHT ||
                        code == KeyCode.UP || code == KeyCode.DOWN) {
                    parent.navigate(code);
                } else if (code == KeyCode.S && keyEvent.isShortcutDown()) {
                    writeToDocument(inputFilename);
                }
                cursorRectangle.setX(parent.getPointerXCoord());
                cursorRectangle.setY(parent.getPointerYCoord());
                keyEvent.consume();
            }
            scrollBar.setMax(parent.getScreenBound());
            // failed attempt at scrollbar "snapping"
//            if (parent.getPointerYCoord() >= window_height) {
//                scrollBar.setValue(parent.getPointerYCoord() - window_height);
//            }
        }
    }

    public void readToDocument(char input) {
        String characterTyped = Character.toString(input);
        if (characterTyped.equals("\r")) {
            parent.newLine();
        } else {
            parent.TypeButDontRender(characterTyped);
        }
        cursorRectangle.setX(parent.getPointerXCoord());
        cursorRectangle.setY(parent.getPointerYCoord());
    }

    public void writeToDocument(String inputFilename) {
        try {
            File inputFile = new File(inputFilename);
            FileWriter writer = new FileWriter(inputFilename);
            for (int i = 0; i <= parent.lines.length - 1; i++) {
                int xCoordSoFar = parent.leftMargin();
                for (int j = 0; j <= parent.getLineSize(i) - 1; j++) {
                    Text displayText = parent.getText(i, j);
                    if (displayText != null) {
                        String displayString = displayText.getText();
                        if (displayString.equals("\r\n") || displayString.equals("\r")) {
                            writer.write('\n');
                        } else {
                            char displayChar = displayString.toCharArray()[0];
                            writer.write(displayChar);
                        }
                    }
                }
            }

            System.out.println("Successfully saved file " + inputFilename);

            // Close the reader and writer.
            writer.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found! Exception was: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when saving; exception was: " + ioException);
        }
    }

    @Override
    public void start(Stage primaryStage) {

        // initialize roots
        root = new Group();
        Group textRoot = new Group();
        root.getChildren().add(textRoot);

        // Make a vertical scroll bar on the right side of the screen.
        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        // Set the height of the scroll bar so that it fills the whole window.
        scrollBar.setPrefHeight(window_height);
        // set the height and width of the scrollbar
        scrollBar.setMin(0);
        scrollBar.setMax(0);
        double usableScreenWidth = window_width - scrollBar.getLayoutBounds().getWidth();
        scrollBar.setLayoutX(usableScreenWidth);
        textRoot.setLayoutY(0);

        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                // newValue describes the value of the new position of the scroll bar. The numerical
                // value of the position is based on the position of the scroll bar, and on the min
                // and max we set above. For example, if the scroll bar is exactly in the middle of
                // the scroll area, the position will be:
                //      scroll minimum + (scroll maximum - scroll minimum) / 2
                // Here, we can directly use the value of the scroll bar to set the height of Josh,
                // because of how we set the minimum and maximum above.
                textRoot.setLayoutY(-1 * newValue.doubleValue());
            }
    });

        int parentScreenWidth = (int) Math.round(usableScreenWidth);
        //initialize the grid
        parent = new Grid(font_size, font_name, parentScreenWidth, window_height, textRoot);
        //initialize the scene
		Scene scene = new Scene(root, window_width, window_height, Color.WHITE);
        // initialize various handlers including the cursor rectangle and the keyeventhandler
        EventHandler<KeyEvent> keyEventHandler = new KeyEventHandler(textRoot, parent.leftMargin(),
                parent.topMargin(), scrollBar);
        EventHandler<MouseEvent> mouseEventHandler = new mouseClickEventHandler();
        cursorRectangle = new Rectangle(5,0,1,font_size);
		textRoot.setOnMouseClicked(mouseEventHandler);
		scene.setOnKeyTyped(keyEventHandler);
		scene.setOnKeyPressed(keyEventHandler);
        textRoot.getChildren().add(cursorRectangle);
        makeCursorBlink();
        root.getChildren().add(scrollBar);
		primaryStage.setTitle("Editor");
		primaryStage.setScene(scene);
		primaryStage.show();

        // load file
        List arguments = getParameters().getUnnamed();
        if (arguments.contains("debug")) {
            isDebugTrue = true;
        }
        if (arguments.isEmpty()) {
            System.out.println("Expected usage: Editor <source filename>");
            System.exit(1);
        }
        inputFilename = (String) arguments.get(0);

        try {
            File inputFile = new File(inputFilename);
            if (!inputFile.exists()) {
                System.out.println("Unable to open file because file with name " + inputFilename +
                        " does not exist.");
                return;
            }
            FileReader reader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(reader);

            int intRead = -1;
            while ((intRead = bufferedReader.read()) != -1) {
                char charRead = (char) intRead;
                readToDocument(charRead);
            }
            parent.renderAll();
            scrollBar.setMax(parent.getScreenBound());

            System.out.println("Successfully opened file " + inputFilename);

            // Close the reader and writer.
            bufferedReader.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found! Exception was: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when opening; exception was: " + ioException);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
