package termybird;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class World {
	Terminal terminal;
	Screen screen;
	Bird bird;
	PipeContainer pipes;
	
	public World(Terminal terminal) throws IOException {
		this.terminal = terminal;
		screen = new TerminalScreen(terminal);
		screen.startScreen();
		initWorld();
	}
	
	public static void main(String[] args) {
		DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
		Terminal terminal = null;
		try {
			terminal = defaultTerminalFactory.createTerminal();
			World world = new World(terminal);
			world.loop();
		} catch(IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			if(terminal != null) {
				try {
					terminal.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		} 
	}
	
	public void initWorld() throws IOException {
		screen.setCursorPosition(null);
		screen.clear();

		this.bird = new Bird(screen);
		this.pipes = new PipeContainer(screen);
	
		
	}
	
	public void loop() throws IOException, InterruptedException {
		// time
		long timeCurrent = 0;
		long timePrevious = 0;
		
		// game control
		boolean gameRuns = false;
		boolean missionFailed = false;
		

		// simple game loop
		while (true) {
			
			// in case the terminal size changes
			screen.doResizeIfNecessary();
			
			// save current time
			timeCurrent = System.currentTimeMillis();
			
			// clear the screen
			screen.clear();
			
			KeyStroke keyStroke = screen.pollInput();
			
			if (!gameRuns && !missionFailed) {
				printMessage(screen, "Press SPACE to begin");
				screen.refresh();
			}

		    // if space was pressed, jump; if escape was pressed, exit;
		    if(gameRuns && keyStroke != null && keyStroke.getCharacter() != null && keyStroke.getCharacter().charValue() == ' ') {
		    	bird.jump();
		    } else if (keyStroke != null && (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF)) {
		        break;
		        // endGame();
		    } else if (!gameRuns && keyStroke != null && keyStroke.getCharacter() != null && keyStroke.getCharacter().charValue() == ' ') {
		    	initWorld();
		    	timePrevious = timeCurrent;
		    	gameRuns = true;
		    	missionFailed = false;
		    }
		    
		    if (gameRuns) {
			
			    // print the bird
				bird.print();
				
				// calculate the falling distance and change position
				bird.fall(timeCurrent, timePrevious);
				
				// print pipes
				pipes.print();
				
				// calculate pipes movement
				pipes.move(timeCurrent, timePrevious);				
				
				screen.refresh();
				timePrevious = timeCurrent;
				
				if (isCollision(bird, pipes, screen)) {
					initWorld();
					printMessage(screen, "Mission failed, press SPACE to continue");
					screen.refresh();
					gameRuns = false;
					missionFailed = true;
				}
		    }
		    
		    // 1000 millis / 17 = approx. 60FPS
			Thread.sleep(17);
		}
	}


	
	public static boolean isCollision(Bird bird, PipeContainer pipes, Screen screen) {
		// detect top/down collision
		if (bird.posY < 0 || bird.posY > screen.getTerminalSize().getRows()) {
			return true;
		}
		
		// detect collision with pipes
		for (Pipe pipe : pipes.pipes) {
			if (bird.posX + bird.text.length() > pipe.posX && 
				bird.posX + bird.text.length() < pipe.posX+pipe.text.length()-1 &&
				(bird.posY < pipe.topPartBoundaryY || bird.posY > pipe.bottomPartBoundaryY)) {
				return true;
			}
		}		
		return false;
	}
	

	public static double calculateDistance(double velocity, long time) {
		return (velocity * time)/1000;
	}
	
	
	public static void printMessage(Screen screen, String message) {
		final TextGraphics textGraphics = screen.newTextGraphics();
		textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
		textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
		textGraphics.putString((screen.getTerminalSize().getColumns()/2)-(message.length()/2), screen.getTerminalSize().getRows()/2, message, SGR.BOLD);
	}
}


