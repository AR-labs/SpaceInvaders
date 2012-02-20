import java.awt.*;
import javax.swing.*;

public class Statistics
{
	private static final int lifeLength = 32;
	private static final Image lifeImage = SpaceInvader.imageForName("40.png");
		
	private int maxLives;
	private int lives;
	private int level;
	private int score;

	public int maxLives() { return this.maxLives; }
	public int currentLives() { return this.lives; }
	
	private StatsView view; // 320 x 32 px
	public  JComponent view() { return this.view; }
	
	public Statistics(int score, int lives, int level)
	{
		this.score = score;
		this.lives = lives;
		this.level = level;
		
		this.view = new StatsView(this);
	}
	
	public void reset(int score, int lives, int level)
	{
		this.score = score;
		this.lives = lives;
		this.level = level;
		this.view.repaint();
	}

	public int score()
	{
		return this.score;
	}
	public void addScore(int plus)
	{
		this.score += plus;
		this.view.repaint();
	}
	public void raiseLevel()
	{
		this.level += 1;
		this.view.repaint();
	}
	public int level()
	{
		return this.level;
	}
	public boolean removeLive()
	{
		--this.lives;
		this.view.repaint();
		return (this.lives <= 0);
	}
	
	class StatsView extends JComponent
	{
		private Statistics stats;
		
		public StatsView(Statistics s)
		{
			super();
			this.stats = s;
			this.setSize(320, Statistics.lifeLength);
		}
		
		public void repaint()
		{
			super.repaint(new Rectangle(new Point(), this.getSize()));
		}
		
		public void paint(Graphics g)
		{
			g.setColor(Color.black);
			Rectangle r = this.getBounds(new Rectangle());
			g.clearRect(r.x, r.y, r.width, r.height);
			g.fillRect(r.x, r.y, r.width, r.height);

			for (int i = 1; i <= this.stats.currentLives(); ++i)
			{
				g.drawImage(Statistics.lifeImage, r.width - (i * Statistics.lifeLength), 0, this);
			}
			
			g.setColor(Color.white);
			g.setFont(SpaceInvader.font);
			
			g.drawString(
				"LVL "
				+ this.stats.level()
				+ " | "
				+ this.stats.score()
				, 10
				, this.getHeight() - 4
			);
		}
	}
}