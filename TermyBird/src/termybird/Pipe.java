package termybird;

import java.util.Random;

import com.googlecode.lanterna.screen.Screen;

public class Pipe {
	Screen screen;
	String text;
	int holeSize;
	int holePos;
	int width;
	int posX;
	int topPartBoundaryY;
	int bottomPartBoundaryY;
	
	Random rand;
	
	public Pipe(Screen screen, int posX) {
		this.rand = new Random();
		
		this.screen = screen;
		this.text = "pipe";
		this.holeSize = 5;
		this.holePos = calculateHolePosition();
		this.width = this.text.length();
		this.posX = posX;
		this.topPartBoundaryY = this.holePos - 1;
		this.bottomPartBoundaryY = this.holePos + this.holeSize - 1;
	}
	
	public int calculateHolePosition( ) {
		// random hole position
		int rows = screen.getTerminalSize().getRows();
		
		// hole's "budget" is half of the number of rows
		int num = rand.nextInt((rows - holeSize) / 2);
		
		// hole can start or end at 1/4 or rows from the top or bottom
		return rows / 4 + num;
	}
}
