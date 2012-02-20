import java.awt.*;
import javax.swing.*;
import java.lang.Math;

public class Invader extends SIObject
{
	public enum Type { A, B, C };
	public enum State { UP, DOWN, HIT, DEAD };

	public Invader.State state = State.UP;
	private final Invader.Type type;
	
	private static final double directionConstant = 0.375;
	private static final int spawnHeight = 32;
	
		private static Image hitImage = SpaceInvader.imageForName("00.png");
		private static Image[] upImages = {
			SpaceInvader.imageForName("10.png"),
			SpaceInvader.imageForName("20.png"),
			SpaceInvader.imageForName("30.png")
		};
		private static Image[] downImages = {
			SpaceInvader.imageForName("11.png"),
			SpaceInvader.imageForName("21.png"),
			SpaceInvader.imageForName("31.png")
		};
	

	public Invader(int length, int x, int y, Invader.Type t)
	{
		super();
		this.type = t;
		
		this.rect = new Rectangle(x*length, y*length + Invader.spawnHeight, length, length);
		this.direction = Invader.directionConstant;
		this.collisionRect = collisionRectForType(t);
	}

	public Invader.Type type() { return this.type; }
	public boolean isAlive() { return (this.state == State.UP || this.state == State.DOWN); }

	public void draw(Graphics g, JComponent c)
	{
		g.drawImage(this.image(), this.rect.x, this.rect.y, c);
	}

	public void move(Object changeDir)
	{
		if ((Boolean)changeDir)
		{
			int movement = (int)(this.rect.height * Math.abs(this.direction));
			this.rect.setLocation(this.rect.x, this.rect.y + movement);
			this.direction = -this.direction;
		}
		else
		{
			this.rect.setLocation((int)this.nextMinX(), this.rect.y);
		}
		if (this.state == State.DOWN) this.state = State.UP;
		else if (this.state == State.UP) this.state = State.DOWN;
	}
	
	//hit: updates invader, return points
	public int hit()
	{
		this.state = State.HIT;
		Sounds.invaderkilled();
		switch (this.type)
		{
			case A:	return 30;
			case B: return 20;
			/*case C:*/
			default: return 10;
		}
	}
	
	public static Rectangle collisionRectForType(Type t)
	{
		switch (t)
		{
			case A: return new Rectangle(6, 6, 19, 19);
			case B: return new Rectangle(5, 9, 22, 16);
			case C: return new Rectangle(6, 6, 20, 15);
			default: return null;
		}
	}
	
	public Image image()
	{
		if (this.state == State.DEAD) return null;
		if (this.state == State.HIT) return Invader.hitImage;
		
		int idx;
		switch (this.type)
		{
			case A:	idx = 0; break;
			case B:	idx = 1; break;
			default: case C: idx = 2; break;
		}
		
		if (this.state == State.UP) return upImages[idx];
		else return downImages[idx];
	}
}