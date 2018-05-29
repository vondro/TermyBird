package termybird;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

public class Bird {
	Screen screen;
	int posY;
	int posX;
	String text;
	double acceleration;
	double innitialVelocity;
	double previousVelocity;
	double velocity;
	double velocityUp;
	double distance;
	
	public Bird(Screen screen) {
		this.screen = screen;
		this.posX = 5;
		this.posY = screen.getTerminalSize().getRows() / 2;
		this.text = "bird";
		this.acceleration = 10.0;
		this.innitialVelocity = 5.0;
		this.previousVelocity = 0.0;
		this.velocity = innitialVelocity;
		this.velocityUp = -10.0;
		this.distance = 0.0;
	}
	
	public double calculateDistance(double velocity, long time) {
		return (velocity * time)/1000;
	}
	
	public void jump() {
		velocity = velocityUp;
	}
	
	public void fall(long timeCurrent, long timePrevious) {
		if (timePrevious != 0) {
			previousVelocity = velocity;
			velocity += acceleration / 60;
			distance += calculateDistance(velocity, timeCurrent - timePrevious);
			
			if (distance > 1.0 && velocity > 0) {
				posY++;
				distance -= 1.0;
			} else if (distance < -1.0 && velocity < 0) {
				posY--;
				distance += 1.0;
			} else if (velocity > 0 && previousVelocity < 0) {
				// when the velocity increases above 0
				distance = 0.75;
			}
		}
	}
	
	public void print() {
		
		// textgraphics objects keeps the state - colors, objects printed, separate from the terminal
		final TextGraphics textGraphics = screen.newTextGraphics();

		// setting of the TextGraphics object
		textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
		textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

		// printing string to position in the terminal window
		textGraphics.putString(posX, posY, text, SGR.BOLD);
	}

}
