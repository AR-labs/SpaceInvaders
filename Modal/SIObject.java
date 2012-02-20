import java.awt.*;
import javax.swing.*;

public abstract class SIObject
{
	protected Rectangle rect; // absolute
	protected Rectangle collisionRect; // relative
	protected double direction; // relative, >0 == right/down, <0 == left/up 
	
	public double direction() { return this.direction; }
	public Rectangle rect() { return this.rect; }
	public Rectangle collision()
	{
		Rectangle r = new Rectangle();
		r.setSize(this.collisionRect.getSize());
		r.setLocation(this.rect.x + this.collisionRect.x, this.rect.y + this.collisionRect.y);
		return r;
	}

	public abstract void move(Object param);
	public abstract void draw(Graphics g, JComponent c);

	public double nextMaxX()
	{
		int maxx = this.rect.x;
		maxx += this.rect.width;
		maxx += this.rect.width * this.direction;
		return maxx;
	}
	public double nextMinX()
	{
		int minx = this.rect.x;
		minx += this.rect.width * this.direction;
		return minx;
	}

	public double nextMaxY()
	{
		int maxy = this.rect.y;
		maxy += this.rect.height;
		maxy += this.rect.height * this.direction;
		return maxy;
	}
	public double nextMinY()
	{
		int miny = this.rect.y;
		miny += this.rect.height * this.direction;
		return miny;
	}
	public boolean isOutOfVerticalBounds(int height)
	{
		return ((this.rect.y <= -this.rect.height) || (this.rect.y > height));
	}
}