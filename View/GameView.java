import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

public class GameView extends JComponent
{
	public MenuView menu;
	private String text;
	private int showCounter = 0;
	
	public GameView()
	{
		super();
		this.menu = new MenuView(new Dimension(320, 480));
		this.add(this.menu);
	}
	
	public void writeImportant(String str)
	{
		this.text = str;
		this.showCounter = SpaceInvader.FPS;
	}
	
	public void paint(Graphics g)
	{
		SIGame game = SpaceInvader.sharedInstance.game();
		g.clearRect(0, 0, this.getWidth(), this.getHeight());

		if (game.state == SIGame.RunState.START)
		{
			g.drawImage(SpaceInvader.startScreen, 0, 0, this);
			return;
		}
		
		g.drawImage(SpaceInvader.background, 0, 0, this);
		
		game.player().draw(g, this);
		if (game.playerBullet() != null) game.playerBullet().draw(g, this);
		if (game.ufo() != null) game.ufo().draw(g, this);
		for(Bullet b: game.invaderBullets()) b.draw(g, this);
		for (Invader inv: game.invaders()) inv.draw(g, this);
		
		if (this.showCounter > 0)
		{
			this.showCounter -= 1;
			
			if(this.showCounter > (SpaceInvader.FPS * .75)) g.setColor(Color.WHITE);
			else g.setColor(new Color(255, 255, 255, (int)(this.showCounter * 190/SpaceInvader.FPS)));
			g.setFont(SpaceInvader.font);
			
			Rectangle2D bounds = g.getFontMetrics().getStringBounds(this.text, g);
			g.drawString(
				this.text,
				((int)(this.getWidth()/2) - (int)(bounds.getWidth()/2)),
				((int)((this.getHeight()*.75) - bounds.getHeight()))
			);
		}
		super.paint(g);
	}
}