package editor;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
    Group root;
    int characterHeight;
    int screenBound;
    int windowHeight;
    public class Pointer {
        // pointer monitor's its own x position AND coordinate
        private int xCoord;
        private int xPosition;
        // pointer knows what line it is on, but that line is responsible for the y coordinate
        private int yPosition;
        // pointer has access to its parent: sometimes it needs help to get something done.
        // parent is intentionally public
        public Grid parent;

        public Pointer(int x, int y, Grid p) {
            xPosition = x;
            yPosition = y;
            parent = p;
            xCoord = getXCoord(x);
        }

        // getters
        public int xCoord() { return xCoord; }
        public int xPosition() {
            return xPosition;
        }
        public int yPosition() {
            return yPosition;
        }

        // set xPosition, then  set xCoord based on the new xPosition
        public void setxPosition(int x) {
            xPosition = x;
            xCoord = getXCoord(x);
        }
        // set xPosition, but for some reason do not update xCoord: primarily used to correct inconsistencies
        public void setxNotCoord(int x) {
            xPosition = x;
        }
        // increase xPosition by one, using a width that was passed into this function so
        // we can avoid a costly call to getXCoord
        public void incxPosition(int newX) {
            xPosition = xPosition + 1;
            xCoord = xCoord + newX;
        }

        public void setxCoord(int x) { xCoord = x; }
        // set yPosition
        public void setyPosition(int y) {
            yPosition = y;
        }

        // input is an x cursor index i, output is appropriate xCoord to type a new char in that position.
        public int getXCoord(int i) {
            int returnX = parent.leftMargin();
            for (int j = 0; j < i; j++) {
                if (parent.lines()[yPosition()].contents.containsKey(j)) {
                    returnX = returnX + parent.lines[yPosition].getPixelsAtPosition(j);
                }
            }
            return returnX;
        }

        // input is a pixel value i, output is the appropriate index for the cursor corresponding to
        // that pixel value
        public int getXPosition(int i) {
            int closesti = parent.leftMargin();
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
        private HashMap<Integer, Text> contents;
        public Grid parent;
        private Text last;

        // line constructor
        public Line(int y, Grid p) {
            parent = p;
            yPosition = y;
            Text dCharacterHeight = new Text(10, 10, "a");
            dCharacterHeight.setFont(Font.font(parent.fontName(), parent.fontSize()));
            double dubCharacterHeight = dCharacterHeight.getLayoutBounds().getHeight();
            int characterHeight = (int) Math.round(dubCharacterHeight);
            yCoordinate = yPosition * characterHeight + parent.topMargin();
            contents = new HashMap<>();
            last = null;
        }

        public int getyCoordinate() {
            return yCoordinate;
        }

        public int yPosition() {
            return yPosition;
        }

        // get the integral rounded width of the text object at index i in the current line
        public int getPixelsAtPosition(int i) {
            Text textObj = getTextAtPosition(i);
            if (textObj == null) {
                return 0;
            } else {
                textObj.setFont(Font.font(parent.fontName(), parent.fontSize()));
                double dubCharacterWidth = textObj.getLayoutBounds().getWidth();
                return (int) Math.round(dubCharacterWidth);
            }
        }

        // get the string contained within the text object at index i in the current line
        public String getCharAtPosition(int i) {
            Text textObj = getTextAtPosition(i);
            if (textObj == null) {
                return "";
            } else {
                return textObj.getText();
            }
        }

        // get the actual text object at index i in the current line
        public Text getTextAtPosition(int i) {
            if (contents.containsKey(i)) {
                return contents.get(i);
            } else {
                return null;
            }
        }

        // failed attempt at word wrap
//        public void lineWrap(Line y) {
//            int lastSpace = 0;
//            for (Integer key : y.contents.keySet()) {
//                if (y.contents.containsKey(key)) {
//                    if (y.contents.get(key).getText().equals(" ")) {
//                        lastSpace = key;
//                    }
//                }
//            }
//            if (lastSpace == 0) {
//                return;
//            } else {
//                for (int i = lastSpace + 1; i <= y.contents.size() - 1; i++) {
//                    Text toMove = lines()[y.yPosition()].contents.remove(i);
//                    if (lines()[y.yPosition() + 1].contents.containsKey(0)) {
//                        lines()[y.yPosition() + 1].keyShift();
//                    }
//                    lines()[y.yPosition() + 1].contents.put(0, toMove);
//                }
//            }
//        }

        // shift ALL keys by +1... probably faulty, only used by lineWrap
        public void keyShift() {
            for (int i = contents.size() - 1; i >= 0; i--) {
                contents.put(i + 1, contents.get(i));
            }
        }

        // the primary Type function that the root uses to add new text objects to the scene
        public void Type(String characterTyped) {
            // get character width
            Text dCharacterWidth = new Text(10, 10, characterTyped);
            dCharacterWidth.setFont(Font.font(parent.fontName(), parent.fontSize()));
            double dubCharacterWidth = dCharacterWidth.getLayoutBounds().getWidth();
            int characterWidth = (int) Math.round(dubCharacterWidth);
            // initialize our text object
            Text displayText = new Text(parent.pointer.xCoord(), yCoordinate, characterTyped);
            // case where the pointer is placing the last char in the current line
            if (pointer.xCoord() + characterWidth > rightMargin()) {
                Text newLineChar = new Text("\r\n");
                lines()[pointer.yPosition()].contents.put(pointer.xPosition(), newLineChar);
                pointer.setyPosition(yPosition() + 1);
                if (pointer.yPosition() == parent.lines().length - 1) {
                    parent.resizeLines();
                }
                pointer.setxPosition(0);
                Type(characterTyped);
                return;
            }
            boolean willInsert = false;
            if (parent.lines()[pointer.yPosition()].contents.containsKey(pointer.xPosition())) {
                willInsert = true;
            } else {
                last = displayText;
            }
            Text oldText = parent.lines()[pointer.yPosition()].contents.put(pointer.xPosition(), displayText);
            pointer.incxPosition(characterWidth);
            if (willInsert) {
                insert(yPosition(), oldText, pointer.xPosition(),
                        characterWidth, pointer.xCoord());
            }
            displayText.setTextOrigin(VPos.TOP);
            displayText.setFont(Font.font(fontName(), fontSize()));
            displayText.toFront();
            root.getChildren().add(displayText);
       //     lineWrap(this);
            renderAll();
        }

        // only used to open files
        public void TypeButDontRender(String characterTyped) {
            // get character width
            Text dCharacterWidth = new Text(10, 10, characterTyped);
            dCharacterWidth.setFont(Font.font(parent.fontName(), parent.fontSize()));
            double dubCharacterWidth = dCharacterWidth.getLayoutBounds().getWidth();
            int characterWidth = (int) Math.round(dubCharacterWidth);
            // initialize our text object
            Text displayText = new Text(parent.pointer.xCoord(), yCoordinate, characterTyped);
            if (! parent.lines()[pointer.yPosition()].contents.containsKey(pointer.xPosition())) {
                last = displayText;
            }
            // case where the pointer is placing the last char in the current line
            if (pointer.xCoord() + characterWidth > rightMargin()) {
                Text newLineChar = new Text("\r\n");
                lines()[pointer.yPosition()].contents.put(pointer.xPosition(), newLineChar);
                pointer.setyPosition(yPosition() + 1);
                if (pointer.yPosition() == parent.lines().length - 1) {
                    parent.resizeLines();
                }
                pointer.setxPosition(0);
                TypeButDontRender(characterTyped);
                return;
            }
            parent.lines()[pointer.yPosition()].contents.put(pointer.xPosition(), displayText);
            pointer.incxPosition(characterWidth);
            displayText.setTextOrigin(VPos.TOP);
            displayText.setFont(Font.font(fontName(), fontSize()));
            displayText.toFront();
            root.getChildren().add(displayText);
        //    lineWrap(this);
        }

        public void insert(int lineY, Text newContent, int xIndex, int shiftWidth, int startingCoord) {
                for (int j = xIndex; j <= lines()[lineY].contents.size(); j++) {
                    if (startingCoord + shiftWidth > parent.rightMargin()) {
                        contents.remove(j);
                        last = contents.get(j - 1);
                        insert(lineY + 1, newContent, 0, shiftWidth, parent.leftMargin());
                        return;
                    }
                    Text textToShift = newContent;
                    double doubleXVal = textToShift.getX();
                    int intXVal = (int) Math.round(doubleXVal);
                    textToShift.setX(intXVal + shiftWidth);
                    Text oldText = parent.lines()[lineY].contents.put(xIndex, newContent);
                    if (oldText != null) {
                        newContent = oldText;
                        xIndex = xIndex + 1;
                        startingCoord = startingCoord + shiftWidth;
                    } else {
                        last = contents.get(j);
                    }
                }
        }

        public Text backSpace() {
            Text deleted = new Text();
            if (pointer.xPosition() == 0) {
                if (pointer.yPosition() == 0) {
                    return null;
                } else {
                    pointer.setyPosition(pointer.yPosition() - 1);
                    if (lines()[pointer.yPosition()].contents.isEmpty()) {
                        pointer.setxPosition(0);
                    } else {
                        pointer.setxPosition(lines()[pointer.yPosition()].contents.size() - 1);
                    }
                }
            } else {
                deleted = lines()[pointer.yPosition()].contents.remove(pointer.xPosition() - 1);
                pointer.setxPosition(pointer.xPosition() - 1);
            }
            return deleted;
        }
    }

    public Grid(int fontSize, String fontName, int windowWidth, int Windowheight, Group root) {
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
        this.root = root;
        characterHeight = getFontHeight();
        screenBound = 0;
        windowHeight = Windowheight;
    }

    public Text backSpace() {
        return lines[pointer.yPosition()].backSpace();
    }

    public void renderAll() {
        if (pointer.yPosition() == lines.length - 1) {
            resizeLines();
        }
        int lastYcoordinate = 0;
        for (int i = 0; i <= lines.length - 1; i++) {
            lines()[i].yCoordinate = lines()[i].yPosition() * getFontHeight();
            int xCoordSoFar = leftMargin();
            for (int j = 0; j <= getLineSize(i) - 1; j++) {
                Text displayText = getText(i, j);
                if (displayText != null) {
                    displayText.setTextOrigin(VPos.TOP);
                    displayText.setFont(Font.font(fontName(), fontSize()));
                    displayText.toFront();
                    displayText.setX(xCoordSoFar);
                    displayText.setY(lines()[i].getyCoordinate());
                    xCoordSoFar = xCoordSoFar + lines()[i].getPixelsAtPosition(j);
                    if (lastYcoordinate <= lines()[i].getyCoordinate()) {
                        lastYcoordinate = lines()[i].getyCoordinate();
                    }

                }
            }
        }
        if (lastYcoordinate + characterHeight >= windowHeight - characterHeight) {
            screenBound = lastYcoordinate - windowHeight + characterHeight;
        }
    }

    public int getScreenBound() {
        return screenBound;
    }

    public int getFontHeight() {
        Text dCharacterHeight = new Text(10, 10, "a");
        dCharacterHeight.setFont(Font.font(fontName(), fontSize()));
        double dubCharacterHeight = dCharacterHeight.getLayoutBounds().getHeight();
        return (int) Math.round(dubCharacterHeight);
    }

    public void printAll() {
        for (int i = 0; i <= lines.length - 1; i++) {
            String lineSoFar = "line" + i + ": (";
            for (int j = 0; j <= getLineSize(i) - 1; j++) {
                Text displayText = getText(i, j);
                if (displayText != null) {
                    lineSoFar = lineSoFar + displayText.getText();
                }
            }
            lineSoFar = lineSoFar + ") last:" + lines()[i].last;
            System.out.println(lineSoFar);
        }
        System.out.println(getScreenBound());
        System.out.println(lines());
    }

    public void navigate(KeyCode code) {
        if (code == KeyCode.LEFT) {
            if (pointer.xPosition() == 0) {
                if (pointer.yPosition() == 0) {
                    return;
                } else {
                    pointer.setyPosition(pointer.yPosition() - 1);
                    pointer.setxPosition(lines()[pointer.yPosition()].contents.size()   );
                    String space = lines()[pointer.yPosition()].getCharAtPosition(pointer.xPosition());
                    if (space.equals("\n") || space.equals("\r\n") || space.equals("\r")) {
                        pointer.setxPosition(pointer.xPosition() - 1);
                    }
                }
            } else {
                pointer.setxPosition(pointer.xPosition() - 1);
            }
        }
        if (code == KeyCode.RIGHT) {
            if (pointer.xPosition() == lines[pointer.yPosition()].contents.size() - 1) {
                if (! lines()[pointer.yPosition() + 1].contents.isEmpty()) {
                    pointer.setyPosition(pointer.yPosition() + 1);
                    pointer.setxPosition(0);
                } else {
                    return;
                }
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
            if (lines()[pointer.yPosition()].contents.containsKey(0)) {
                pointer.setyPosition(pointer.yPosition() + 1);
                pointer.setxPosition(pointer.xPosition());
            }
        }
    }

    public void newLine() {
        Text newLineChar = new Text("\r\n");
        if (pointer.yPosition() == lines.length - 1) {
            resizeLines();
        }
        for (int i = lines().length - 1; i - 1 != pointer.yPosition(); i--) {
            if ((i == lines().length - 1) && !lines()[i].contents.isEmpty()) {
                resizeLines();
            }
            lines()[i].contents = (HashMap) lines()[i - 1].contents.clone();
            lines()[i - 1].contents.clear();
        }
        int j = 0;
        int s = lines()[pointer.yPosition()].contents.size() - 1;
        for (int i = pointer.xPosition(); i <= s; i++) {
            Text content = lines()[pointer.yPosition()].contents.get(i);
            lines()[pointer.yPosition()].contents.remove(i);
            lines()[pointer.yPosition() + 1].contents.put(j, content);
            j++;
        }
        lines()[pointer.yPosition()].contents.put(pointer.xPosition(), newLineChar);
        lines()[pointer.yPosition()].last = newLineChar;
        pointer.setyPosition(pointer.yPosition() + 1);
        if (pointer.yPosition() == lines.length - 1) {
            resizeLines();
        }
        pointer.setxPosition(0);
        renderAll();
    }

    public int incrementFontSize() {
        fontSize = fontSize + 4;
        characterHeight = getFontHeight();
        renderAll();
        return characterHeight;
    }

    public int decrementFontSize() {
        fontSize = fontSize - 4;
        characterHeight = getFontHeight();
        renderAll();
        return characterHeight;
    }



    public int getPointerXCoord() {
        return pointer.getXCoord(pointer.xPosition());
    }

    public int getPointerYCoord() {
        return lines[pointer.yPosition()].getyCoordinate();
    }

    public Text getText(int line, int space) {
        return lines[line].getTextAtPosition(space);
    }

    public int getLineSize(int line) {
        return lines[line].contents.size();
    }

    public void Type(String characterTyped) {
        lines[pointer.yPosition()].Type(characterTyped);
    }

    public void TypeButDontRender(String characterTyped) {
        lines[pointer.yPosition()].TypeButDontRender(characterTyped);
    }

    public void mouseClick(double x, double yPosition) {
        double dubYCoordinate = Math.floor(yPosition / characterHeight) + topMargin();
        int yCoordinate = (int) Math.round(dubYCoordinate);
        pointer.setyPosition(yCoordinate);
        Line currLine = lines()[yCoordinate];
        int xInput = (int) Math.round(x);
        int xCoordSoFar = leftMargin();
        int closestX = Math.abs(xInput - xCoordSoFar);
        int indexForClosestX = 0;
        for (int i = 0; i < currLine.contents.size(); i++ ) {
            xCoordSoFar = xCoordSoFar + currLine.getPixelsAtPosition(i);
            if (Math.abs(xCoordSoFar - xInput) < closestX) {
                closestX = Math.abs(xCoordSoFar - xInput);
                indexForClosestX = i;
            }
        }
        pointer.setxPosition(indexForClosestX);
    }

    public void resizeLines() {
        Line[] newLines = new Line[lineCount * 2];
        System.arraycopy(lines, 0, newLines, 0, lineCount);
        lines = newLines;
        for (int i = lineCount; i <= lines.length - 1; i++) {
            lines[i] = new Line(i, this);
        }
        lineCount = lines.length;
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