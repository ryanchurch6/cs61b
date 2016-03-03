package editor;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.LinkedList;
import java.util.HashMap;





public class Editor extends Application {
    private static int window_width = 500;
    private static int window_height = 500;
    private static int font_size = 12;
    private static String font_name = "Verdana";
	private Group root;
    private final Rectangle cursorIndicator = new Rectangle(0,0);
    cursorIndicator.setX(parent.getPointerXCoord());
    cursorIndicator.setY(parent.getPointerYCoord());
    cursorIndicator.setHeight(characterHeight);
    cursorIndicator.setWidth(1);
    Grid parent = new Grid(font_size, font_name, window_width, window_height);

    public void renderAll() {
        for (int i = 0; i <= parent.lines.length - 1; i++) {
            for (int j = 0; j <= parent.getLineSize(i) - 1; j++) {
                Text displayText = parent.getText(i, j);
				root.getChildren().remove(displayText);
                displayText.setTextOrigin(VPos.TOP);
                displayText.setFont(Font.font(parent.fontName(), parent.fontSize()));
                displayText.toFront();
                root.getChildren().add(displayText);
            }
        }
    }

    private class mouseClickEventHandler implements EventHandler<MouseEvent> {

        public mouseClickEventHandler(){
        }

        @Override public void handle(MouseEvent mouseEvent) {
            double mousePressedX = mouseEvent.getX();
            double mousePressedY = mouseEvent.getY();
            parent.mouseClick(mousePressedX, mousePressedY);
        }
    }



    private class CursorBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] cursorColors = {Color.WHITE, Color.BLACK};
        public Rectangle cursorIndicator = new Rectangle(0, 0);
        double dCharacterHeight = new Text(0, 0, "A").getLayoutBounds().getHeight();
        double characterHeight = Math.round(dCharacterHeight);
        cursorIndicator.setX(parent.getPointerXCoord());
        cursorIndicator.setY(parent.getPointerYCoord());
        cursorIndicator.setHeight(characterHeight);
        cursorIndicator.setWidth(1);

        CursorBlinkEventHandler() {
            // Set the color to be the first color in the list.
            changeColor();
        }

        private void changeColor() {
            cursorIndicator.setFill(cursorColors[currentColorIndex]);
            currentColorIndex = (currentColorIndex + 1) % cursorColors.length;
        }

        public void makeCursorBlink() {
            // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
            // every 1 second.
            final Timeline timeline = new Timeline();
            // The rectangle should continue blinking forever.
            timeline.setCycleCount(Timeline.INDEFINITE);
            CursorBlinkEventHandler cursorChange = new CursorBlinkEventHandler();
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
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

	KeyEventHandler(final Group root, int x, int y) {
	    pointerXPos = x;
	    pointerYPos = y;
	    this.root = root;
	}

		@Override
		public void handle(KeyEvent keyEvent) {
			if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
				Text displayText;
				String characterTyped = keyEvent.getCharacter();
				if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8 && !keyEvent.isShortcutDown()) {
					displayText = parent.Type(characterTyped);
					displayText.setTextOrigin(VPos.TOP);
					displayText.setFont(Font.font(parent.fontName(), parent.fontSize()));
					displayText.toFront();
					root.getChildren().add(displayText);
				}
				keyEvent.consume();
			} else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                // Arrow keys should be processed using the KEY_PRESSED event, because KEY_PRESSED
                // events have a code that we can check (KEY_TYPED events don't have an associated
                // KeyCode).
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.P && keyEvent.isShortcutDown()) {
                    Integer x = parent.pointer.getXCoord(parent.pointer.xPosition());
                    Integer y = parent.lines()[parent.pointer.yPosition()].getyCoordinate();
                    System.out.println(x.toString() + ", " + y.toString());
                } else if (code == KeyCode.BACK_SPACE) {
                    Text deleted = parent.backSpace();
                    root.getChildren().remove(deleted);
                } else if (code == KeyCode.LEFT || code == KeyCode.RIGHT ||
                        code == KeyCode.UP || code == KeyCode.DOWN) {
                    parent.navigate(code);
                }
            }
		}
    }

    @Override
    public void start(Stage primaryStage) {
		root = new Group();
		Scene scene = new Scene(root, window_width, window_height, Color.WHITE);
		EventHandler<KeyEvent> keyEventHandler = new KeyEventHandler(root, parent.leftMargin(), parent.topMargin());
		scene.setOnMouseClicked(new mouseClickEventHandler());
		scene.setOnKeyTyped(keyEventHandler);
		scene.setOnKeyPressed(keyEventHandler);
        root.getChildren().add(cursorIndicator);
        makeCursorBlink();
		primaryStage.setTitle("Editor");
		primaryStage.setScene(scene);
		primaryStage.show();
    }

    public static void main(String[] args) {
		launch(args);
    }
}
