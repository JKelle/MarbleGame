import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import javax.swing.JPanel;

public class GamePlayer implements Runnable
{
	private GameBoard gb;
	private final int slotWidth = 55;
	private final int paddingLeft = 58;
	private final int paddingTop = 60;
	private ArrayList<Rectangle> rects = new ArrayList<Rectangle>(33);
	private Image woodenBoard;
	private Slot from;
	private Slot to;
	private int numMarbles;
	private Rectangle restart;

	//edit init------------------------------------------------------------------
	private void init()	{
		restart();
		woodenBoard = getImageResource("woodenBoard.jpg");
		restart = new Rectangle(400, 70, 100, 30);
	}

	public void restart() {
		gb = new GameBoard();
		for(int i = 0; i < 7; i++)
			for(int j = 0; j < 7; j++)
				if(gb.getSlot(i, j) != null)
					rects.add( new Rectangle(i*80, j*80, slotWidth, slotWidth) );
		numMarbles = 32;
	}

	public Image getImageResource(String name) {

		String url = ""+getClass().getResource(name);
		if (url.equals("null")||url==null) {
			System.out.println("DEBUG: image resource name: " + name);
			System.out.println("DEBUG: image resource url: " + url);
		}

		Image tbr = null;

		try {

			tbr = Toolkit.getDefaultToolkit().getImage(getClass().getResource(name));

			long startTime = System.currentTimeMillis();
			while (tbr.getWidth(canvas)<1 && System.currentTimeMillis() < startTime + 5000) {}

		} catch (Exception e) {
			System.out.println("Exception thrown for image " + name + ": ");
			e.printStackTrace();
		}

		return tbr;
	}

	//edit update----------------------------------------------------------------
	protected void update(int deltaTime) {
	}
	//edit render----------------------------------------------------------------
	protected void render(Graphics2D g)	{
		drawGameBoard(g);

		g.setColor(Color.white);
		g.fill(restart);
		g.setColor(Color.black);
		g.drawRect(restart.x+2, restart.y+2, restart.width-5, restart.height-5);
		g.setFont( new Font("SANS_SERIF", Font.BOLD, 25) );
		g.drawString("Restart", restart.x+5, restart.y+restart.height-6);

		if( numMarbles == 1 ) {
			g.setColor(Color.white);
			g.setFont(new Font("SANS_SERIF", Font.PLAIN, 40));
			g.drawString("You Win!", 10, 60);
		}
	}

	private void drawGameBoard(Graphics2D g) {
		g.drawImage(woodenBoard, 0, -5, 80*7, 80*7, canvas);
		for(int i = 0; i < 7; i++)
			for(int j = 0; j < 7; j++) {
				Slot s = gb.getSlot(i, j);
				if( s != null && s.isOccupied() ) {
					if( s == from ) {
						g.setColor(Color.green);
						g.fillOval(paddingLeft+j*(slotWidth+10)-5, paddingTop+i*(slotWidth+10)-5, slotWidth+10, slotWidth+10);
					}
					g.setColor(s.getColor());
					g.fillOval(paddingLeft+j*(slotWidth+10), paddingTop+i*(slotWidth+10), slotWidth, slotWidth);
				}
			}
	}

	private void swap() {
		int fi = from.getLoc()[0];
		int fj = from.getLoc()[1];
		int ti = to.getLoc()[0];
		int tj = to.getLoc()[1];

		if( fi == ti )
			gb.getSlot(fi, (fj+tj)/2).removeMarble();
		else
			gb.getSlot((fi+ti)/2, fj).removeMarble();
		to.putMarble(from.getColor());
		from.removeMarble();
		numMarbles--;
		from = null;
		to = null;
	}

	private boolean isValidSlot(int i, int j) {
		if( i < 7 && j < 7 && ((i > 1 && i < 5) || (j > 1 && j < 5)) )
			return true;
		return false;
	}

	private boolean isValidTo(int i, int j) {
		int fi = from.getLoc()[0];
		int fj = from.getLoc()[1];

		if( (i == fi && ( j == fj+2 || j == fj-2 ) && gb.getSlot(i, (fj+j)/2).isOccupied()) ||
			(j == fj && ( i == fi+2 || i == fi-2 ) && gb.getSlot((fi+i)/2, j).isOccupied()) )
			return true;
		return false;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	JFrame frame;
	Canvas canvas;
	BufferStrategy bufferStrategy;
	MouseControl mouse;

	public static final int WIDTH = 1000;
	public static final int HEIGHT = 600;

	public GamePlayer()
	{
		frame = new JFrame("Marble Game");

		JPanel panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(80*7, 80*7));
		panel.setLayout(null);

		canvas = new Canvas();
		canvas.setBounds(0, 0, 80*7, 80*7);
		canvas.setIgnoreRepaint(true);

		panel.add(canvas);

		mouse = new MouseControl();
		canvas.addMouseListener(mouse);
		canvas.addMouseMotionListener(mouse);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);

		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();

		canvas.requestFocus();
	}

	private class MouseControl extends MouseAdapter	{
		public void mouseClicked(MouseEvent e) {
			if(restart.contains(e.getPoint())) {
				restart();
				return;
			}

			int i = (e.getY()-paddingTop)/(slotWidth+10);
			int j = (e.getX()-paddingLeft)/(slotWidth+10);

			if( !isValidSlot(i, j) )
				return;

			Slot s = gb.getSlot(i, j);
			if( s.isOccupied() )
				from = s;
			else if( from != null  && isValidTo(i, j) ) {
				to = s;
				swap();
			}
		}
		public void mouseDragged(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	}

	long desiredFPS = 60;
	long desiredDeltaLoop = (1000*1000*1000)/desiredFPS;
	boolean running = true;

	public void run()
	{
		long beginLoopTime;
		long endLoopTime;
		long currentUpdateTime = System.nanoTime();
		long lastUpdateTime;
		long deltaLoop;
		int deltaTime;

		init();

		while(running)
		{
			beginLoopTime = System.nanoTime();

			render();

			lastUpdateTime = currentUpdateTime;
			currentUpdateTime = System.nanoTime();
			deltaTime = (int) ((currentUpdateTime - lastUpdateTime)/(1000*1000));
			update(deltaTime);

			endLoopTime = System.nanoTime();
			deltaLoop = endLoopTime - beginLoopTime;

			if(deltaLoop <= desiredDeltaLoop)
			{
				try
				{
					Thread.sleep((desiredDeltaLoop - deltaLoop)/(1000*1000));
				} catch(InterruptedException e) { /* Do nothing */ }
			}
		}
	}

	private void render()
	{
		Graphics2D g = (Graphics2D)bufferStrategy.getDrawGraphics();
		g.clearRect(0, 0, WIDTH, HEIGHT);
		render(g);
		g.dispose();
		bufferStrategy.show();
	}

	public static void main(String[] args)
	{
		GamePlayer ex = new GamePlayer();
		new Thread(ex).start();
	}
}