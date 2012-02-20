import java.awt.*;
import javax.swing.*;

public class Player extends SIObject
{
	private static final Image playerImage = SpaceInvader.imageForName("40.png");
	private static final Image hitImage = SpaceInvader.imageForName("41.png");
	private static final double directionConstant = .25;
	private int showHit = 0;
	
	public Player(int length, int width, int height)
	{
		super();
		this.rect = new Rectangle(width/2 - length, height -  length, length, length);
		this.direction = 0.0;
		this.collisionRect = new Rectangle(3, 12, 25, 14);
	}
	
	public void draw(Graphics g, JComponent c)
	{
		Image img = Player.playerImage;
		if(this.showHit != 0)
		{
			img = Player.hitImage;
			this.showHit -= 1;
		}
		g.drawImage(img, this.rect.x, this.rect.y, c);
	}
	
	public void setDirectionForMove(SpaceInvader.Move m)
	{
		double d;
		switch (m)
		{
			case LEFT: d = -directionConstant; break;
			case RIGHT: d = directionConstant; break;
			/*case NO:*/
			default: d = 0;
		}
		this.direction = d;
	}
	
	public void hit()
	{
		this.showHit = (int)(SpaceInvader.FPS/2);
	}
	
	public void move(Object width)
	{
		int x = (int)this.nextMinX();
		if (x < 0) x = 0;
		
		double max = this.nextMaxX();
		if (max > (Double)width) x = ((Double)width).intValue() - this.rect.width;
		
		this.rect.setLocation(x, this.rect.y);
	}
}