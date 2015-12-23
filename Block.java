import Audio.Audio;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.Timer;



public class Block implements ActionListener, MouseListener, KeyListener 
{

	public static Block cube;

	public final int WIDTH = 800;
			
	public final int HEIGHT = 800;

	public Provide provide;



	public ArrayList<Rectangle> columns;
	private Image block2; 
	public int ticks;
	public int yMove;
	public int score;
    private Audio music; 
    private Rectangle block; 
	public boolean lost; 
	public boolean begin;

	public Random random;

	public Block()
	{
		JFrame jframe = new JFrame();
		Timer timer = new Timer(20, this);

		provide = new Provide();
		random = new Random();

		jframe.add(provide);
		jframe.setTitle("Block");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(WIDTH, HEIGHT);
		jframe.addMouseListener(this);
		jframe.addKeyListener(this);
		jframe.setResizable(false);
		jframe.setVisible(true);
		
		
	block = new Rectangle(WIDTH, HEIGHT, 20, 20); 
		columns = new ArrayList<Rectangle>();

		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);

		timer.start();
	init();
	}
    private void loadImages() 
    {

        ImageIcon iid = new ImageIcon("smile.png");
        block2 = iid.getImage(); 
    }

	public void addColumn(boolean start)
	{
		int space = 300;
		int width = 150;
		int height = 50 + random.nextInt(200);

		if (start)
		{
			columns.add(new Rectangle(WIDTH + width + columns.size() * 200, HEIGHT - height - 120, width, height));
			columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));
		}
		else
		{
			columns.add(new Rectangle(columns.get(columns.size() - 1).x + 400, HEIGHT - height - 120, width, height));
			columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
		}
	}

	public void colorColumn(Graphics g, Rectangle column)
	{
		g.setColor(Color.cyan.darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}

	public void jump()
	{
		if (lost)
		{
			block = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
			columns.clear();
			yMove = 0;
			score = 0;

			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);

			lost = false;
		}

		if (!begin)
		{
			begin = true;
		}
		else if (!lost)
		{
			if (yMove > 0)
			{
				yMove = 0;
			}

			yMove -= 10;
		}
	}

	@Override
	public void actionPerformed (ActionEvent e)
	{
		int speed = 5;

		ticks++;

		if (begin)
		{
			for (int i = 0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);

				column.x -= speed;
			}

			if (ticks % 2 == 0 && yMove < 15)
			{
				yMove += 2;
			}

			for (int i = 0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);

				if (column.x + column.width < 0)
				{
					columns.remove(column);

					if (column.y == 0)
					{
						addColumn(false);
					}
				}
			}

			block.y += yMove;

			for (Rectangle column : columns)
			{
				if (column.y == 0 && block.x + block.width / 2 > column.x + column.width / 2 - 10 && block.x + block.width / 2 < column.x + column.width / 2 + 10)
				{
					score++;
				}

				if (column.intersects(block))
				{
					lost = true;

					if (block.x <= column.x)
					{
						block.x = column.x - block.width;

					}
					else
					{
						if (column.y != 0)
						{
							block.y = column.y - block.height;
						}
						else if (block.y < column.height)
						{
							block.y = column.height;
						}
					}
				}
			}

			if (block.y > HEIGHT - 120 || block.y < 0)
			{
				lost = true;
			}

			if (block.y + yMove >= HEIGHT - 120)
			{
				block.y = HEIGHT - 120 - block.height;
				lost = true;
			}
		}

		provide.repaint();
	}

	public void repaint(Graphics display)
	{
		display.setColor(Color.red);
		display.fillRect(0, 0, WIDTH, HEIGHT);

		display.setColor(Color.blue);
		display.fillRect(0, HEIGHT - 120, WIDTH, 120);

		display.setColor(Color.yellow);
		display.fillRect(0, HEIGHT - 120, WIDTH, 20);

		display.setColor(Color.blue);
		display.fillRect(block.x, block.y, block.width, block.height);

		for (Rectangle column : columns)
		{
			colorColumn(display, column);
		}

		display.setColor(Color.white);
		display.setFont(new Font("Arial", 1, 100));

		if (!begin)
		{
			display.drawString("Click to Begin!", 75, HEIGHT / 2 - 50);
		}

		if (lost)
		{
			display.drawString("Lost!", 100, HEIGHT / 2 - 50);
		}

		if (!lost && begin)
		{
			display.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
		}
		
	}
	
public void init()
{
	music = new Audio("/Music/BlockBackgroundMusic.wav");
	music.play();
}
	public static void main(String[] args)
	{
		cube = new Block();
	}

	@Override
	public void mouseClicked(MouseEvent j)
	{
		jump();
	}

	@Override
	public void keyReleased(KeyEvent k)
	{
		if (k.getKeyCode() == KeyEvent.VK_SPACE)
		{
			jump();
		}
	}

	@Override
	public void mousePressed(MouseEvent p)
	{
	}

	@Override
	public void mouseReleased(MouseEvent r)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void keyTyped(KeyEvent t)
	{

	}

	@Override
	public void keyPressed(KeyEvent p)
	{

	}

}
