package editor;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.LinkedList;

public class Editor extends Application {
    private static int window_width = 500;
    private static int window_height = 500;
    private static int font_size = 12;
    private static String font_name = "Verdana";
    Grid parent = new Grid(font_size, font_name, window_width, window_height);

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
				if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
					displayText = parent.Type(characterTyped);
					displayText.setTextOrigin(VPos.TOP);
					displayText.setFont(Font.font(parent.fontName(), parent.fontSize()));
					displayText.toFront();
					root.getChildren().add(displayText);
				}
				keyEvent.consume();
			}
		}
    }

    @Override
    public void start(Stage primaryStage) {
		Group root = new Group();
		Scene scene = new Scene(root, window_width, window_height, Color.WHITE);
		EventHandler<KeyEvent> keyEventHandler = new KeyEventHandler(root, parent.leftMargin(), parent.topMargin());
		scene.setOnKeyTyped(keyEventHandler);
		scene.setOnKeyPressed(keyEventHandler);
		primaryStage.setTitle("Editor");
		primaryStage.setScene(scene);
		primaryStage.show();
    }

    public static void main(String[] args) {
		launch(args);
    }

    public static class Grid {
	Pointer pointer;
	Line[] lines;
	String fontName;
	int fontSize;
	int lineCount;
	int topMargin;
	int bottomMargin;
	int leftMargin;
	int rightMargin;
	public class Pointer {
	    private int xPosition;
	    private int yPosition;
	    public Grid parent;

	    public Pointer(int x, int y, Grid p) {
		xPosition = x;
		yPosition = y;
		parent = p;
	    }

	    public int getX() {
		return xPosition;
	    }
	    public int getY() {
		return yPosition;
	    }
		public void setxPosition(int x) {
			xPosition = x;
		}
		public void setyPosition(int y) {
			yPosition = y;
		}
	}

	public class Line {
	    private int yPosition;
	    private int yCoordinate;
	    private int lastXCoord;
	    private LinkedList<String> contents;
	    public Grid parent;

	    public Line(int y, Grid p) {
            parent = p;
            yPosition = y;
            yCoordinate = yPosition * parent.fontSize() + parent.topMargin();
            lastXCoord = parent.leftMargin();
            contents = new LinkedList<String>();
	    }

	    public Text Type(int i, String characterTyped) {
			double dCharacterWidth = new Text(0, 0, characterTyped).getLayoutBounds().getWidth();
            int characterWidth = (int) Math.ceil(dCharacterWidth);
            if (characterWidth + this.lastXCoord > parent.rightMargin()) {
                return parent.newLType(characterTyped);
            } else {
                Text displayText = new Text(lastXCoord, yCoordinate, characterTyped);
                contents.add(i, characterTyped);
                lastXCoord = lastXCoord + characterWidth;
                return displayText;
            }
	    }
	}



	public Grid(int fontSize, String fontName, int windowWidth, int Windowheight) {
	    rightMargin = windowWidth - 5;
	    leftMargin = 5;
	    bottomMargin = 0;
	    topMargin = 0;
	    lineCount = 32;
	    this.fontSize = fontSize;
	    this.fontName = fontName;
	    lines = new Line[lineCount];
        for (int i = 0; i <= lines.length - 1; i++) {
            lines[i] = new Line(i, this);
        }
	    pointer = new Pointer(0, 0, this);
	}

	public Text Type(String characterTyped) {
		Text returnText = lines[pointer.getY()].Type(pointer.getX(), characterTyped);
		pointer.setxPosition(pointer.getX() + 1);
		return returnText;
	}

        public Text newLType(String characterTyped) {
            if (pointer.getY() == lines.length - 1) {
                resizeLines();
            }
            pointer.setxPosition(0);
            pointer.setyPosition(pointer.getY() + 1);
            Text returnText = lines[pointer.getY()].Type(pointer.getX(), characterTyped);
            pointer.setxPosition(pointer.getX() + 1);
            return returnText;
        }

	public void resizeLines() {
	    Line[] oldLines = lines();
	    Line[] newLines = new Line[oldLines.length * 2];
		System.arraycopy(oldLines, 0, newLines, 0, oldLines.length);
	}

	public int rightMargin() {
	    return rightMargin;
	}
	public int leftMargin() {
	    return leftMargin;
	}
	public int bottomMargin() {
	    return bottomMargin;
	}
	public int topMargin() {
	    return topMargin;
	}
	public String fontName() {
	    return fontName;
	}
	public int fontSize() {
	    return fontSize;
	}
	public Line[] lines() {
	    return lines;
	}
    }
}
