package termybird;

import java.util.ArrayList;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

public class PipeContainer {
	Screen screen;
	ArrayList<Pipe> pipes;
	int pipesPerTerm;
	double velocity;
	double distance;
	
	public PipeContainer(Screen screen) {
		this.screen = screen;
		this.pipes = new ArrayList<Pipe>();
		this.pipesPerTerm = 4;
		this.velocity = 15;
		this.distance = 0.0;
		
		this.addPipe();
		
	}
	
	public void move(long timeCurrent, long timePrevious) {
		// move pipes
		if (timePrevious != 0) {
			distance += calculateDistance(velocity, timeCurrent - timePrevious);
			if (distance > 1.0) {
				for (Pipe pipe : pipes) {	
						pipe.posX--;;
				}
				distance -= 1.0;
			}
		}
		removeOld();
		generateNew();
	}
	
	public double calculateDistance(double velocity, long time) {
		return (velocity * time)/1000;
	}
	
	public void removeOld() {
		Pipe pipe = pipes.get(0);
		if (pipe.posX + pipe.width < 0) {
			pipes.remove(0);
		}
	}
	
	public void generateNew() {
		Pipe lastPipe = pipes.get(pipes.size()-1);
		if (getScreenCols() - lastPipe.posX > getScreenCols() / pipesPerTerm) {
			addPipe();
		}
	}
	
	public void print() {
		// textgraphics objects keeps the state - colors, objects printed, separate from the terminal
		final TextGraphics textGraphics = screen.newTextGraphics();

		// setting of the TextGraphics object
		textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
		textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

		// printing string to position in the terminal window
		
		for (Pipe pipe : pipes) {
			for (int i=0; i<pipe.text.length(); i++) {
				// upper part of the pipe
				drawPipeLine(pipe.posX+i, 0, pipe.posX+i, pipe.topPartBoundaryY, pipe.text.charAt(i), textGraphics);
				
				//lower part of the pipe
				drawPipeLine(pipe.posX+i, pipe.bottomPartBoundaryY, pipe.posX+i, getScreenRows(), pipe.text.charAt(i), textGraphics);
			}
		}
	}
	
	public void drawPipeLine(int fromX, int fromY, int toX, int toY, char character, TextGraphics textGraphics) {
		textGraphics.drawLine(new TerminalPosition(fromX, fromY),
				  			  new TerminalPosition(toX, toY),
				  			  character);
	}
	
	public void addPipe() {
		// add new pipe at the end of the screen
		Pipe newPipe = new Pipe(screen, getScreenCols());
		pipes.add(newPipe);
	}
	
	public int getScreenCols() {
		return screen.getTerminalSize().getColumns();
	}
	
	public int getScreenRows() {
		return screen.getTerminalSize().getRows();
	}
}
