import java.awt.*;
import javax.swing.*;
import java.lang.Math;

public class UFO extends SIObject
{
	private static final double directionConstant = 2.25;
	private static Image livingImage = SpaceInvader.imageForName("50.png");
	private static Image[] hitImages = {
		SpaceInvader.imageForName("51.png"),
		SpaceInvader.imageForName("52.png"),
		SpaceInvader.imageForName("53.png")
	};
	
	public enum State { LIVING, HIT, DEAD };
	
	public State state = State.LIVING;

	public UFO(int length, boolean reverse, int width, int frames)
	{
		super();
		this.rect = new Rectangle(reverse ? width-1 : -length+1, 0, length, length);
		this.direction = (reverse ? -directionConstant : directionConstant) / frames;
		this.collisionRect = new Rectangle(1, 10, 29, 16);
		Sounds.ufolow();
	}

	public void draw(Graphics g, JComponent c)
	{
		g.drawImage(this.image(), this.rect.x, this.rect.y, c);
	}

	public void move(Object reverseDir)
	{
		this.rect.setLocation((int)this.nextMinX(), this.rect.y);
	}
	
	private int showHitImage = 0;
	public boolean display()
	{
		return showHitImage < (SpaceInvader.FPS/2);
	}
	
	public Image image()
	{
		if (this.state == State.LIVING) return this.livingImage;
		if (this.state == State.HIT && this.display()) return this.hitImages[(int)(showHitImage++/(SpaceInvader.FPS/6))];
		else return null;
	}
	
	public int hit()
	{
		this.state = UFO.State.HIT;
		Sounds.ufohigh();
		//returns a number e [1..10]*10
		return (int)(Math.random()*10 + 1) * 10;
	}
}