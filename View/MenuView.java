import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

public class MenuView extends JComponent
{
	private static final AbstractAction resume = new AbstractAction("RESUME")
	{
		public void actionPerformed(ActionEvent evt)
		{ SpaceInvader.sharedInstance.game().state = SIGame.RunState.RUNNING; }
	};
	private static final AbstractAction restart = new AbstractAction("RESTART")
	{
		public void actionPerformed(ActionEvent evt)
		{
			SpaceInvader.sharedInstance.game().state = SIGame.RunState.START;
			SpaceInvader.sharedInstance.resetStats();
		}
	};
	private static final AbstractAction quit = new AbstractAction("QUIT")
	{
		public void actionPerformed(ActionEvent evt) { System.exit(0); }
	};
	private static final AbstractAction back = new AbstractAction("BACK")
	{
		public void actionPerformed(ActionEvent evt)
		{
			SpaceInvader.sharedInstance.game().state = SIGame.RunState.RUNNING;
		}
	};
	
	private static final MVMenu defaultMenu = defaultMenu();
	private static final MVMenu quitMenu = quitMenu();
	private MVMenu currentMenu;

	public MenuView(Dimension size)
	{
		super();
		this.setSize(size);
		this.currentMenu = null;
	}
	
	public void repaint() { this.repaint(0, 0, this.getWidth(), this.getHeight()); }
	
	public void paint(Graphics g)
	{
		SIGame.RunState state = SpaceInvader.sharedInstance.game().state;
		if (	state == SIGame.RunState.RUNNING ||
			state == SIGame.RunState.GAMEOVER ||
			state == SIGame.RunState.START)
		{
			return;
		}
		
		g.setFont(SpaceInvader.font);
		g.setColor(new Color(0, 0, 0, 125));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if (state == SIGame.RunState.MENU)
		{
			this.currentMenu = MenuView.defaultMenu;
			this.drawMenuItems(g, this.currentMenu);
		}
		else if (state == SIGame.RunState.QUITMENU)
		{
			this.currentMenu = MenuView.quitMenu;
			this.drawMenuItems(g, this.currentMenu);
		}
		else this.currentMenu = null;
	}
	
	private void drawMenuItems(Graphics g, MVMenu menu)
	{
		int count = menu.actions.length;
		for (int i = 0; i < count; i++)
		{
			AbstractAction act = menu.actions[i];
			if (act == null) { System.out.println("action is null"); continue; }
			String name = (String)(act.getValue(AbstractAction.NAME));
			boolean selected = (i == menu.currentItemIndex);
			this.drawButton(g, name, selected, count, i);
		}
	}
	
	private void drawButton(Graphics g, String label, boolean selected, int c, int idx)
	{
		int x, y, H = this.getHeight(), W = this.getWidth(), h = 50, w = 200,  m = 50;
		y = (H / 2) - (((c * h) + ((c - 1) * m)) / 2) + (idx * h) + (idx * m);
		x = (W / 2) - (w / 2);
		
		g.setColor(Color.white);
		if (selected) g.fillRect(x, y, w, h);
		else g.drawRect(x, y, w, h);
		
		g.setColor(selected ? Color.black : Color.white);
		Rectangle bounds = g.getFontMetrics().getStringBounds(label, g).getBounds();
		x += (w / 2) - (bounds.width * 0.5);
		y += (h / 2) + (bounds.height * 0.35);
		g.drawString(label, x, y);
	}
	
	static class MVMenu
	{
		public AbstractAction[] actions;
		public int currentItemIndex = 0;
		
		public MVMenu(int itemCount)
		{
			this.actions = new AbstractAction[itemCount];
		}
		
		public void setActionForIndex(AbstractAction a, int idx)
		{
			if (idx >= 0 && idx < this.actions.length) this.actions[idx] = a;
		}
	}
	
	private static MVMenu defaultMenu()
	{
		MVMenu menu = new MVMenu(3);
		menu.setActionForIndex(MenuView.resume, 0);
		menu.setActionForIndex(MenuView.restart, 1);
		menu.setActionForIndex(MenuView.quit, 2);
		menu.currentItemIndex = 0;
		return menu;
	}
	
	private static MVMenu quitMenu()
	{
		MVMenu menu = new MVMenu(2);
		menu.setActionForIndex(MenuView.back, 0);
		menu.setActionForIndex(MenuView.quit, 1);
		return menu;
	}

	public void resetItemIndex()
	{
		if (this.currentMenu == null) return;
		this.currentMenu.currentItemIndex = 0;
	}
	public int increaseItemIndex()
	{
		if (this.currentMenu == null) return -1;
		int idx = this.currentMenu.currentItemIndex;
		idx = (idx + 1) % this.currentMenu.actions.length;
		this.currentMenu.currentItemIndex = idx;
		return idx;
	}
	public int decreaseItemIndex()
	{
		if (this.currentMenu == null) return -1;
		int idx = this.currentMenu.currentItemIndex;
		idx += (10 * this.currentMenu.actions.length) - 1; // ensure result > -1
		idx %= this.currentMenu.actions.length;
		this.currentMenu.currentItemIndex = idx;
		return idx;
	}
	
	public void performSelectedAction()
	{
		if (this.currentMenu == null) return;
		int idx = this.currentMenu.currentItemIndex;
		this.currentMenu.actions[idx].actionPerformed(null);
	}
	
	public void continueGame()
	{
		this.currentMenu.actions[0].actionPerformed(null);
	}
}