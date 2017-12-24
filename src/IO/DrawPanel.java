package IO;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import Values.Data;
import Values.Parameter;

public class DrawPanel {

	static Graphics g;
	private double sizeX;
	private double sizeY;
	private double addNumber;
	private Data data = Data.getInstance();
	
	public void drawpanel(JPanel show)
	{
		this.setParameter(show);
		//*****JPanel轉畫布*****//
		g = show.getGraphics();
				
		//*****設定顏色*****//
		g.setColor(Color.white);
								
		//*****清空畫布*****//
		g.fillRect(0, 0, show.getWidth(), show.getHeight());
				
		//*****設定顏色*****//
		g.setColor(Color.black);
				
		//*****畫點*****//
		for(int i = 0; i < data.total; i++)
		{
			int x = (int)((data.x[i]+addNumber)*sizeX);
			int y = (int)((data.y[i]+addNumber)*sizeY);
			g.fillRect(x, y, 3, 3);
		}
	}
	
	//*****畫線*****//
	public void drawline(int A, int B, JPanel show)
	{
		this.setParameter(show);
		int x1 = (int)((data.x[A]+addNumber)*sizeX);
		int y1 = (int)((data.y[A]+addNumber)*sizeY);
		int x2 = (int)((data.x[B]+addNumber)*sizeX);
		int y2 = (int)((data.y[B]+addNumber)*sizeY);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.RED);
		g2d.setStroke(new BasicStroke(1.0f));
	    g2d.drawLine(x1, y1, x2, y2);
	}
	
	private void setParameter(JPanel show) {
		Parameter parameter = Parameter.getInstance();
		this.sizeX = parameter.getSizeX();
		this.sizeY = parameter.getSizeY();
		this.addNumber = parameter.getAddNumber();
		DrawPanel.g = show.getGraphics();
		DrawPanel.g.setColor(Color.black);
	}
}
