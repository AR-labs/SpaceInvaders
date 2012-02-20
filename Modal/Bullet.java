import java.awt.*;
import javax.swing.*;

public class Bullet extends SIObject
{
	private static final double directionConstant = 1.5;
	private static Image shot = SpaceInvader.imageForName("shot.png");

	public Bullet(Rectangle source, boolean upwards)
	{
		super();
		
		if (upwards) Sounds.shoot(); // just player
		
		int x = source.x + (source.width / 2) - 1;
		int y = source.y + (upwards ? -4 : source.height);
		
		this.rect = new Rectangle(x, y, 2, 4);
		this.direction = upwards ? -3*directionConstant : directionConstant;
		this.collisionRect = new Rectangle(new Point(), this.rect.getSize());
	}

	public void move(Object param)
	{
		this.rect.setLocation(this.rect.x, (int)nextMinY());
	};
	public void draw(Graphics g, JComponent c)
	{
		g.drawImage(Bullet.shot, this.rect.x, this.rect.y, c);
	};
}