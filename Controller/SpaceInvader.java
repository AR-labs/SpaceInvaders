import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class SpaceInvader
{
	public static SpaceInvader sharedInstance;
	public static Image background;
	public static Image startScreen;
	public static Font font;
	public static final int FPS = 30;
	
	private int startLevel = 1;
	private String basePath;
	public String basePath() { return this.basePath; }
	
	private JFrame window;
	private GameView gameView;
	private UserInput inputHandler;
	
	public boolean leftPressed = false;
	public boolean rightPressed = false;
	public boolean spacePressed = false;
	
	private Statistics stats;
	public Statistics stats() { return this.stats; }
	
	private SIGame game;
	public SIGame game() { return this.game; }
	
	public static void main(String[] args)
	{
		String usage = "Usage: java SpaceInvader [-l level] -p pathToImageFolder";
		
		if (args.length < 2 || (args.length % 2) != 0)
		{
			System.out.println(usage);
			return;
		}
		
		int lvl = 1;
		String path = null;
		
		for (int i = 0; i < args.length; ++i)
		{
			if (args[i].equals("-l"))
			{
				lvl = Integer.parseInt(args[i+1]);
			}
			else if (args[i].equals("-p"))
			{
				path = args[i+1];
			}
		}
		if (path == null)
		{
			System.out.println(usage);
			return;
		}
		
		new SpaceInvader(path, lvl);
	}
	
	public SpaceInvader(String path, int level)
	{
		this.basePath = path;
		this.startLevel = level;
		SpaceInvader.sharedInstance = this;

		SpaceInvader.startScreen =  SpaceInvader.imageForName("Title.png");
		SpaceInvader.background = SpaceInvader.imageForName("Background.png");
		SpaceInvader.font = SpaceInvader.loadFont();

		int width = 320, height = 480;

		this.stats = new Statistics(0, 3, this.startLevel);
		this.gameView = new GameView();
		this.game = new SIGame(width, height, 30); // width x height in px

		this.window = new JFrame("SpaceInvaders");
		this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.window.setLocation(100, 100);
		this.window.setFocusable(true);
		
		this.inputHandler = new UserInput(this);
		this.window.addKeyListener(this.inputHandler);
		
		JComponent st = this.stats.view();
		this.window.setContentPane(new WrapView(st, this.gameView));

		Container c = this.window.getContentPane();
		this.window.setSize(c.getWidth(), c.getHeight() + 22);
		this.window.setResizable(false);

		try  { this.game.start(); }
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		this.window.setVisible(true);
	}
	
	public void resetStats()
	{
		this.stats.reset(0, 3, this.startLevel);
		this.game.startGame(this.gameView.getWidth(), this.gameView.getHeight());
		this.repaint();
	}

	public static Image imageForName(String name)
	{
		String path = SpaceInvader.sharedInstance.basePath + File.separator;
		path += "Images" + File.separator;
		return Toolkit.getDefaultToolkit().getImage(path + name);
	}
	
	public static InputStream streamForName(String name) throws java.io.FileNotFoundException
	{
		return new FileInputStream(SpaceInvader.sharedInstance.basePath + File.separator + name);
	}
	
	public static AudioInputStream audioStreamForName(String name)
	{
		AudioInputStream stream = null;
		try
		{
			String path = SpaceInvader.sharedInstance.basePath;
			path += File.separator + "Sounds";
			path += File.separator + name;
			File soundFile = new File(path);
			stream = AudioSystem.getAudioInputStream(soundFile);
		}
		catch (Exception e) { e.printStackTrace(); }
		return  stream;
	}
	
	public void repaint()
	{
		this.gameView.repaint(new Rectangle(new Point(), this.gameView.getSize()));
	}
	
	private static Font loadFont()
	{
		Font f = null;
		try
		{
			InputStream stream = SpaceInvader.streamForName("YanoneKaffeesatz-Bold.ttf");
			f = Font.createFont(Font.TRUETYPE_FONT, stream);
			if (f != null)
			{
				f = f.deriveFont(28.0f);
			}
			else
			{
				f = new Font("Georgia", Font.PLAIN, 28);
			}
		}
		catch (Exception e) { e.printStackTrace(); }
		return f;
	}

	
	public void writeImportant(String text)
	{
		// For messages as scores (at the position of the hit)
		// or general messages as "GAME OVER"
		this.gameView.writeImportant(text);
	}

	public enum Move { LEFT, NO, RIGHT };
	public Move playerMove()
	{
		if (leftPressed && !rightPressed) return Move.LEFT;
		if (rightPressed && !leftPressed) return Move.RIGHT;
		return Move.NO;
	}
	
	public void increaseCurrentMenuSelectionIndex()
	{
		this.gameView.menu.increaseItemIndex();
		this.gameView.menu.repaint();
	}
	public void decreaseCurrentMenuSelectionIndex()
	{
		this.gameView.menu.decreaseItemIndex();
		this.gameView.menu.repaint();
	}
	public void performCurrentMenuItemAction()
	{
		this.gameView.menu.performSelectedAction();
		this.gameView.menu.resetItemIndex();
		this.gameView.menu.repaint();
	}
	public void continueGame()
	{
		this.gameView.menu.continueGame();
		this.gameView.menu.resetItemIndex();
	}
	
	class WrapView extends JComponent
	{
		private JComponent stats;
		private JComponent game;
		
		public WrapView(JComponent statistics, JComponent gameView)
		{
			this.setSize(320, 512);
			this.stats = statistics;
			this.game = gameView;
			
			this.add(statistics);
			this.add(gameView);
			
			this.validate();
		}
		
		public void validate()
		{
			if (this.stats != null) this.stats.setBounds(0, 0, 320, 32);
			if (this.game != null) this.game.setBounds(0, 32, 320, 480);
		}
	}
	
	class UserInput implements KeyListener
	{
		private SpaceInvader invader;
		
		public UserInput(SpaceInvader si) { this.invader = si; }
	
		public void keyPressed(KeyEvent key)
		{
			switch(key.getKeyCode())
			{
				case KeyEvent.VK_LEFT:
				{
					invader.leftPressed = true;
					break;
				}
				case KeyEvent.VK_RIGHT:{
					invader.rightPressed = true;
					break;
				}
				case KeyEvent.VK_SPACE:
				{
					invader.spacePressed = true;
					break;
				}
				default: break;
			}
		}
		
		public void keyReleased(KeyEvent key)
		{
			if (this.invader.game().state == SIGame.RunState.QUITMENU ||
				this.invader.game().state == SIGame.RunState.MENU)
			{
				switch(key.getKeyCode())
				{
					case KeyEvent.VK_LEFT:
					case KeyEvent.VK_UP:
					{
						this.invader.decreaseCurrentMenuSelectionIndex();
						break;
					}
					case KeyEvent.VK_RIGHT:
					case KeyEvent.VK_DOWN:
					{
						this.invader.increaseCurrentMenuSelectionIndex();
						break;
					}
					case KeyEvent.VK_SPACE:
					case KeyEvent.VK_ENTER:
					{
						this.invader.performCurrentMenuItemAction();
						return;
					}
					case KeyEvent.VK_ESCAPE:
					{
						this.invader.continueGame();
						break;
					}
					default: break;
				}
			}
			else switch(key.getKeyCode())
			{
				case KeyEvent.VK_LEFT:
				{
					invader.leftPressed = false;
					break;
				}
				case KeyEvent.VK_RIGHT:
				{
					invader.rightPressed = false;
					break;
				}
				case KeyEvent.VK_SPACE:
				{
					invader.spacePressed = false;
					break;
				}
				case KeyEvent.VK_ENTER:
				{
					if (this.invader.game().state == SIGame.RunState.START)
					{
						this.invader.game().state = SIGame.RunState.RUNNING;
					}
					else if (this.invader.game().state == SIGame.RunState.GAMEOVER)
					{
						this.invader.resetStats();
						this.invader.game().state = SIGame.RunState.START;
					}
					this.invader.repaint();
					break;
				}
				case KeyEvent.VK_ESCAPE:
				{
					if (this.invader.game().state == SIGame.RunState.RUNNING)
					{
						this.invader.game().state = SIGame.RunState.MENU;
					}
					else
					{
						this.invader.game().state = SIGame.RunState.QUITMENU;
					}
					this.invader.repaint();
					break;
				}
				default: break;
			}
		}
		public void keyTyped(KeyEvent key) {}
	}
}