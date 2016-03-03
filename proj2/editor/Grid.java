package editor;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.LinkedList;
import java.util.HashMap;


public class Grid {
    Pointer pointer;
    Line[] lines;
    String fontName;
    int fontSize;
    int lineCount;
    int topMargin;
    int bottomMargin;
    int leftMargin;
    int rightMargin;
    HashMap<Integer, String> typeStack;
    public class Pointer {
        private int xCoord;
        private int xPosition;
        private int yPosition;
        public Grid parent;

        public Pointer(int x, int y, Grid p) {
            xPosition = x;
            yPosition = y;
            parent = p;
            xCoord = getXCoord(x);
        }

        public int xCoord() { return xCoord; }
        public int xPosition() {
            return xPosition;
        }
        public int yPosition() {
            return yPosition;
        }
        public void setxPosition(int x) {
            xPosition = x;
            xCoord = getXCoord(x);
        }
        public void setxNotCoord(int x) {
            xPosition = x;
        }
        public void incxPosition(int newX) {
            xPosition = xPosition + 1;
            xCoord = xCoord + newX;
        }
        public void setyPosition(int y) {
            yPosition = y;
        }

        public void setXCoord() {
        }

        // input is an x cursor index i, output is appropriate xCoord to type a new char in that position.
        public int getXCoord(int i) {
            int returnX = parent.leftMargin();
            for (int j = 0; j < i; j++) {
                if (parent.lines()[yPosition()].contents.containsKey(j)) {
                    returnX = returnX + parent.lines[yPosition].getPixelsAtPosition(j);
                } else {
                    setxNotCoord(j);
                    j = i;
                }
            }
            return returnX;
        }

        // input is a pixel value i, output is the appropriate index for the cursor corresponding to
        // that pixel value
        public int getXPosition(int i) {
            int closesti = 0;
            int closestX = Math.abs(i - getXCoord(0));
            for (int j = 1; j <= parent.lines[yPosition].contents.size(); j++) {
                if (closestX > Math.abs(i - getXCoord(j))) {
                    closestX = Math.abs(i - getXCoord(j));
                    closesti = j;
                }
            }
            return closesti;
        }
    }

    public class Line {
        private int yPosition;
        private int yCoordinate;
        // contents contains a numerically ordered HashMap.
        // Keys are integers 0, 1, 2, ... n
        // Values are 2 element arrays of types {int, int, String}
        // where the int represents the pixel width of the char
        // stored in the string.
        private HashMap<Integer, Object[]> contents;
        public Grid parent;

        public Line(int y, Grid p) {
            parent = p;
            yPosition = y;
            double dCharacterHeight = new Text(0, 0, "A").getLayoutBounds().getHeight();
            int characterHeight = (int) Math.round(dCharacterHeight);
            yCoordinate = yPosition * characterHeight + parent.topMargin();
            contents = new HashMap<>();
        }

        public int getyCoordinate() {
            return yCoordinate;
        }

        public int getPixelsAtPosition(int i) {
            return (int) contents.get(i)[0];
        }

        public String getCharAtPosition(int i) {
            return (String) contents.get(i)[1];
        }

        public Text getTextAtPosition(int i) { return (Text) contents.get(i)[2]; }

        public Object[] getContent(int i) { return contents.get(i); }

        public void insertChar(int x, Object[] content) {
            Object[] previousContent = (Object[]) contents.get(x);
            contents.put(x, content);
            Type(x + 1, (String) previousContent[1]);
        }

        public Text Type(int i, String characterTyped) {
            double dCharacterWidth = new Text(0, 0, characterTyped).getLayoutBounds().getWidth();
            int characterWidth = (int) Math.round(dCharacterWidth);
            if (characterWidth + parent.pointer.xCoord() > parent.rightMargin()) {
                return parent.newLType(characterTyped);
            } else {
                Text displayText = new Text(parent.pointer.xCoord(), yCoordinate, characterTyped);
                Object[] content = new Object[] {characterWidth, characterTyped, displayText};
                boolean willRetype = false;
                Object[] oldContent = new Object[3];
                if (contents.containsKey(pointer.xPosition())) {
                    willRetype = true;
                    oldContent = contents.get(pointer.xPosition());
                }
                contents.put(pointer.xPosition(), content);
                pointer.incxPosition(characterWidth);
                if (willRetype) {
                    reType(i, (String) oldContent[1]);
                }
                return displayText;
            }
        }

        public void reType(int i, String characterTyped) {
            int allToRetype = contents.size() - i; // length from index to last item on the line
            for (int j = allToRetype; j >= 1; j--) {
                double dCharacterWidth = new Text(0, 0, characterTyped).getLayoutBounds().getWidth();
                int characterWidth = (int) Math.ceil(dCharacterWidth);
                if (characterWidth + parent.pointer.xCoord() > parent.rightMargin()) {
//                    return parent.newLReType(characterTyped);
                } else {
                    Text displayText = new Text(parent.pointer.xCoord(), yCoordinate, characterTyped);
                    Object[] content = new Object[] {characterWidth, characterTyped, displayText};
                }
            }
        }

        public Text backSpace() {
            Object[] deleted = lines()[pointer.yPosition()].contents.remove(pointer.xPosition() - 1);
            if (pointer.xPosition() == 0) {
                if (pointer.yPosition() == 0) {
                    return null;
                } else {
                    pointer.setyPosition(pointer.yPosition() - 1);
                    pointer.setxPosition(lines()[pointer.yPosition()].contents.size() - 1);
                }
            } else {
                pointer.setxPosition(pointer.xPosition() - 1);
            }
            return (Text) deleted[2];
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
        typeStack = new HashMap<>();
    }

    public Text backSpace() {
        return lines[pointer.yPosition()].backSpace();
    }

//    public void toTypeStack(int i, String characterTyped) {
//        typeStack.put();
//    }

    public void navigate(KeyCode code) {
        if (code == KeyCode.LEFT) {
            if (pointer.xPosition() == 0) {
                if (pointer.yPosition() == 0) {
                    return;
                } else {
                    pointer.setyPosition(pointer.yPosition() - 1);
                    pointer.setxPosition(lines()[pointer.yPosition()].contents.size());
                }
            } else {
                pointer.setxPosition(pointer.xPosition() - 1);
            }
        }
        if (code == KeyCode.RIGHT) {
            if (pointer.xPosition() == lines[pointer.yPosition()].contents.size()) {
                pointer.setyPosition(pointer.yPosition() + 1);
                pointer.setxPosition(0);
            } else {
                pointer.setxPosition(pointer.xPosition() + 1);
            }
        }
        if (code == KeyCode.UP) {
            if (pointer.yPosition() == 0) {
                return;
            } else {
                pointer.setyPosition(pointer.yPosition() - 1);
                pointer.setxPosition(pointer.xPosition());
            }
        }
        if (code == KeyCode.DOWN) {
            pointer.setyPosition(pointer.yPosition() + 1);
            pointer.setxPosition(pointer.xPosition());
        }
    }

    public int getPointerXCoord() {
        return pointer.getXCoord();
    }

    public int getPointerYCoord() {
        return lines[pointer.yPosition()].getyCoordinate();
    }

    public Text getText(int line, int space) {
        return (Text) lines[line].contents.get(space)[2];
    }

    public int getLineSize(int line) {
        return lines[line].contents.size();
    }

    public Text Type(String characterTyped) {
        Text returnText = lines[pointer.yPosition()].Type(pointer.xPosition(), characterTyped);
        return returnText;
    }

    public Text newLType(String characterTyped) {
        if (pointer.yPosition() == lines.length - 1) {
            resizeLines();
        }
        pointer.setyPosition(pointer.yPosition() + 1);
        pointer.setxPosition(0);
        Text returnText = lines[pointer.yPosition()].Type(pointer.xPosition(), characterTyped);
        return returnText;
    }

    public void mouseClick(double x, double y) {
        int yCoordinate = (int) y / fontSize();
        int xCoordinate = pointer.getXPosition((int) Math.round(x));
        pointer.setxPosition(xCoordinate);
        pointer.setyPosition(yCoordinate);
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