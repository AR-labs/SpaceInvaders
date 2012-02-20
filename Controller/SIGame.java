import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.lang.Math;

public class SIGame extends Thread
{
	public enum RunState { START, QUITMENU, MENU, RUNNING, GAMEOVER };
	public RunState state = RunState.START;

	private int fps = SpaceInvader.FPS;
	private Dimension size;
	
	private Player player;
	private LinkedList<Invader> invaders;
	private LinkedList<Bullet> invaderBullets = new LinkedList<Bullet>();
	private Bullet playerBullet;
	private UFO ufo;
	
	private int invaderDelayCounter;

	public double gridWidth() { return this.size.getWidth(); }
	public Player player() { return this.player; }
	public Bullet playerBullet() { return this.playerBullet; }
	public UFO ufo(){ return this.ufo; }
	public LinkedList<Invader> invaders() { return this.invaders; }
	public LinkedList<Bullet> invaderBullets(){ return this.invaderBullets; }
	
	public SIGame(int width, int height, int fps)
	{
		this.size = new Dimension(width, height);
		this.fps = fps;
		this.startGame(width, height);
		Sounds.start();
	}
	
	public void startGame(int width, int height)
	{
		this.player = new Player(32, width, height);
		this.initLevel();
	}
	
	public void run()
	{
		SpaceInvader controller = SpaceInvader.sharedInstance;
		while (true)
		{
			long startTime = System.currentTimeMillis();
			if (this.state == RunState.RUNNING)
			{
				boolean needsRepaint = false;
	
				SpaceInvader.Move m = controller.playerMove();
				this.player.setDirectionForMove(m);
				
				if (m != SpaceInvader.Move.NO &&
					this.player.nextMaxX() <= this.gridWidth() ||
					this.player.nextMinX() >= 0)
				{
					this.player.move(this.gridWidth());
					needsRepaint = true;
				}
				
				if (this.playerBullet != null)
				{
					this.playerBullet.move(null);
					if (this.playerBullet.isOutOfVerticalBounds(this.size.height))
					{
						this.playerBullet = null;
					}
					else needsRepaint = true;
				}
				else if (controller.spacePressed)
				{
					this.playerBullet = new Bullet(this.player.collision(), true);
					needsRepaint = true;
				}
				
				for (Bullet b: this.invaderBullets)
				{
					b.move(null);
					if(b.collision().intersects(this.player.collision()))
					{
						this.invaderBullets.remove(b);
						this.player.hit();
						if(controller.stats().removeLive())
						{
							this.gameOver();
							return;
						}
						Sounds.explosion();
						break;
					}
				}
				
				// @params: level, invader count
				if (this.ufo != null)
				{
					this.updateUFO(); 
					needsRepaint = true;
				}
				else if (this.invaders.size() >= 12 && this.invaders.size() <= 16 && Math.random() < .1/this.fps)
				{
					this.ufo = new UFO(32, Math.random()>.5, this.size.width, this.fps);
				}
					
				if(this.invaderDelayCounter > Math.cbrt(this.fps*Math.sqrt(this.invaders.size())*8)/(Math.sqrt(controller.stats().level())))
				{
					this.updateInvaders();
					needsRepaint = true;
				}
				else this.invaderDelayCounter += 1;
				
				if (this.playerBullet != null){
					Rectangle r = this.playerBullet.collision();
					for (Invader inv: this.invaders)
					{
						if (r.intersects(inv.collision()) && inv.isAlive())
						{
							controller.stats().addScore(inv.hit());
							this.playerBullet = null;
							if(this.invaders.size() == 1) this.finishedLevel();
							break;
						}
					}
					if (this.playerBullet != null && 
						this.ufo != null &&
						r.intersects(this.ufo.collision())
					) {
						controller.stats().addScore(this.ufo.hit());
						this.playerBullet = null;
					}
				}
				
				if(needsRepaint) controller.repaint();
			}
			
			try 
			{
				long delta = (1000 - (System.currentTimeMillis() - startTime));
				Thread.sleep(((delta < 0) ? 0 : delta) / this.fps);
			} 
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void finishedLevel()
	{
		SpaceInvader.sharedInstance.stats().raiseLevel();
		Sounds.success();
		this.initLevel();
	}
	
	private void updateInvaders(){
		this.invaderDelayCounter = 0;
		boolean changeDir = false;
		
		Object[] vaders = this.invaders.toArray();
		Statistics stats = SpaceInvader.sharedInstance.stats();
		int shouldShoot = (int)(Math.random() * (50/Math.cbrt(stats.level())));
		
		for (int i = 0; i < vaders.length; ++i)
		{
				Invader inv = (Invader)vaders[i];
				
				if (inv.state == Invader.State.HIT)
				{
					this.invaders.remove(inv);
					inv.state = Invader.State.DEAD;
					continue;
				}
				
				if (inv.nextMaxX() > this.gridWidth() ||
					inv.nextMinX() < 0)
				{
					changeDir = true;
					break;
				}
				
				if (inv.isOutOfVerticalBounds(
					this.size.height 
					- this.player.rect().height
					- inv.rect().height
				)){
					this.gameOver();
					return;
				}
				
				if(shouldShoot == i)
				{
					this.invaderBullets.add(new Bullet(inv.collision(), false));
				}
		}
		
		if(this.invaders.size() == 0) this.finishedLevel();

		// move invaders in direction
		for (Invader inv: this.invaders) inv.move(changeDir);
		Sounds.invader();
	}
	
	private void updateUFO()
	{
		if(	!this.ufo.display() ||
			ufo.nextMinX() > this.gridWidth() ||
			ufo.nextMaxX() < 0)
		{
			this.ufo = null;
		}
		else ufo.move(null);
	}
	
	private void gameOver()
	{
		int score = SpaceInvader.sharedInstance.stats().score();
		String state;
		
		// wtf? Eine Auflistung des Highscores wäre eigentlich angesagt.
		if(score == Integer.MAX_VALUE) state = "CHEATER";
		else if(score > 1e6) state = "V";
		else if(score > 1e5) state = "Superman";
		else if(score > 5e4) state = "Van Helsing";
		else if(score > 1e4) state = "Batman";
		else if(score > 8e3) state = "Iron Man";
		else if(score > 5e3) state = "Wolverine";
		else if(score > 3e3) state = "Captain America";
		else if(score > 2e3) state = "Hulk";
		else if(score > 1e3) state = "Robin";
		else if(score == 0)  state = "and RTFM!";
		else state = "no_ob";
		
		this.writeImportant("GAME OVER, " + state);
		SpaceInvader.sharedInstance.repaint();
		Sounds.failure();
		this.state = RunState.GAMEOVER;
	}

	private void initLevel()
	{
		this.writeImportant("Level " + SpaceInvader.sharedInstance.stats().level());
		this.invaderBullets = new LinkedList<Bullet>();
		this.playerBullet = null;
		this.ufo = null;
		this.invaders = this.createInvader(32);
	}
	
	private LinkedList<Invader> createInvader(int length)
	{
		LinkedList<Invader> l = new LinkedList<Invader>();
		
		for (int y = 0; y < 4; ++y)
		{
			Invader.Type t;
			if (y == 0) t = Invader.Type.A;
			else if (y == 1) t = Invader.Type.B;
			else t = Invader.Type.C;
			
			for (int x = 0; x < 7; ++x)
			{
				l.add(new Invader(length, x, y, t));
			}
		}
		
		return l;
	}
	
	private void writeImportant(String text)
	{
		SpaceInvader.sharedInstance.writeImportant(text);
	}
	
}