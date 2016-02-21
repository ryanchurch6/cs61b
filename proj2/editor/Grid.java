/** the idea is to implement a grid consisting of lines and spaces. Each space
    can conceivably hold one character of the font in question.

    ***********THE EASY PARTS***********
    Each line consists of a number of spaces. All of the lines together make up
    all available window space. Each space can hold one Text object.

    Cursor position occupies one grid space which is the "active for editing"
    space. 


    ***********THE HARD PARTS***********
    When font is selected, the grid must adjust all x and y positioning feedback    for the Text objects.
    

 */

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


public class Grid {

    // to be clear, the grid is responsible for all issues of x,y coordinates
    // within the window size scheme. the pointer keeps track of integer x,y
    // coords within the grid. GRID = PIXELS, POINTER = GRID
    public class Pointer {
	private int xPos;
	private int yPos;
	private int[] position;
	private Grid parent;

	public Pointer(int x, int y, Grid p) {
	    xPos = x;
	    yPos = y;
	    position = new int[] {x, y};
	    parent = p;
	}
	public int[] getPointer() {
	    System.out.print("(");
	    System.out.print(position[0]);
	    System.out.print(",");
	    System.out.print(position[1]);
	    System.out.println(")");
	    return position;
	}
	private int getPointerX() {
	    return xPos;
	}
	private int getPointerY() {
	    return yPos;
	}
	private void setX(int newX) {
	    xPos = newX;
	    position[0] = newX;
	}
	private void setY(int newY) {
	    yPos = newY;
	    position[1] = newY;
	}
	private void setXY(int newX, int newY) {
	    this.setX(newX);
	    this.setY(newY);
	}
	private void Type(String letter) {
	    if (xPos == (parent.getLine(yPos).contents.size() - 1)) {
		parent.getLine(yPos).Type(letter);
		xPos = xPos + 1;
	    }
	    else {
	    parent.getLine(yPos).Type(letter, xPos);
	    xPos = xPos + 1;
	    }
	}
    }

    // the lines are the worker bees. they each are responsible for
    // maintaining a list of the text objects contained within them.
    // the lines are the many arms of the grid that handle the actual
    // X coordinates of the multiple text objects that they contain.
    // each line has a unique yPos and yCoord corresponding to that
    // line's height. LINES ARE NOT RESPONSIBLE FOR DECODING KEYBOARD
    // INPUT!!!! they take chars, and display them as Text objects.
    public class Line {
	private LinkedList contents;
	private int yPos;
	private int yCoord;
	private int xPos;
	private int xCoord;
	private int fontWidth;
	private int lineLength;
	

	public Line(int row) {
	    contents = new LinkedList();
	    //NOTE: yPos corresponds to this Line's index within the
	    //the lines LinkedList in the grid. This should allow any
	    //line to delegate work to the line(s) above and below it,
	    //as appropriate!
	    yPos = row;
	    yCoord = row*Grid.this.fontHeight;
	    xPos = 0;
	    fontWidth = Grid.this.fontWidth;
	    xCoord = fontWidth/2;
	    lineLength = Grid.this.spaceCount;
	}

	public void printItem(int x) {
	    System.out.print(contents.get(x));
	}

	public void printAll() {
	    for (int i = 0; i <= contents.size() - 1; i++) {
		printItem(i);
	    }
	}

	// This method should ONLY EVER be called by the pointer!
	private void Type(String input, int x) {
	    //Text displayText = new Text(xCoord, yCoord, input);
	    //contents.add(displayText);
	    contents.add(x, input);
	    xCoord = xCoord + fontWidth;
	    xPos += 1;
	}

	private void Type(String input) {
	    contents.add(input);
	    xCoord = xCoord + fontWidth;
	    xPos += 1;
	}
    }
    
    // the pointer keeps track of the x,y position of the current space
    // that is occupied at all times.
    Pointer pointer;

    // keeps track of font dimensions for placing items within the grid.
    int fontHeight;
    int fontWidth;
    
    // number of lines as decided by font size and window size.
    // number of spaces per line as decided by font size and window size.
    int lineCount;
    int spaceCount;

    // the actual lines making up the text file; all that we need this
    // thing to be is a list of LinkedLists. Do not overthink this.
    LinkedList lines;

    public Grid(int fontSize, int windowWidth, int windowHeight) {
	lineCount = 0;
	spaceCount = windowWidth/fontSize;
	fontHeight = fontSize;
	fontWidth = fontSize;
	lines = new LinkedList();
	pointer = new Pointer(0, 0, this);
    }


    // this method is what occurs when the user types a newline key,
    // such as return/enter
    /*
    public void newLineAfterPointer() {
	int pointerX = pointer.getPointerX;
	int pointerY = pointer.getPointerY;
        if (lines.get(pointerY+1) == null) {
	    Grid.this.newLine();
	}
    }
    */

    // simply adds a new line to the end of lines
    public void newLine() {
	Line addition = new Line(lineCount);
	pointer.setXY(0,lineCount);
	lineCount = lineCount + 1;
	lines.add(addition);
    }

    public Line getLine(int y) {
	Line l = (Line) lines.get(y);
	return l;
    }

    public void printAll() {
	for (int i = 0; i <= lines.size() - 1; i++) {
	    getLine(i).printAll();
	    System.out.println("");
	}
    }

    public static void main(String[] args) {
	Grid grid1 = new Grid(20,500,500);
	grid1.newLine();
	grid1.pointer.Type("R");
	grid1.pointer.Type("y");
	grid1.pointer.Type("a");
	grid1.pointer.Type("n");
	grid1.newLine();
	grid1.lines.get(1);
	System.out.println(grid1.pointer.getPointer());
	grid1.pointer.Type("a");
	grid1.pointer.Type("b");
	grid1.pointer.Type(" ");
	grid1.pointer.Type("c");
	grid1.newLine();
	grid1.pointer.Type("w");
	grid1.pointer.Type("u");
	grid1.pointer.Type("t");
	grid1.printAll();
    }
}
