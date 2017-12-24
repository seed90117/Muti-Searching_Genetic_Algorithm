package IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import Values.Data;
import Values.Parameter;

public class LoadFile {

static BufferedReader br;
	
	private static String WINDOWS = "D:\\Algorithm_Data\\TSP Data\\tsp";
	private static String MAC = "//Users//kevin//Documents//OneDrive//Algorithm_Data//TSP Data//tsp";
	private Data data = Data.getInstance();

	public boolean loadfile(JFileChooser open, JPanel show, boolean isMac)
	{
		String tmp =null;
		FileReader fr = null;
		String loadPath = LoadFile.MAC;
		String fileName = "";
		if (isMac)
			loadPath = LoadFile.MAC;
		else
			loadPath = LoadFile.WINDOWS;
		
		//*****預設路徑*****//
		open.setCurrentDirectory(new File(loadPath));
		
		//*****設定Title*****//
		open.setDialogTitle("Choose dataset");
		
		//*****是否按下Load*****//
		if(open.showDialog(open, "Load") == JFileChooser.APPROVE_OPTION)
		{
			//*****取得路徑*****//
			File filepath = open.getSelectedFile();
			fileName = filepath.getName();
			
			//*****路徑轉為String*****//
			tmp = filepath.getPath().toString();
			
			//*****讀取檔案*****//
			try
			{
				fr = new FileReader(tmp);
			}
			catch (FileNotFoundException ex)
			{
				Logger.getLogger(LoadFile.class.getName()).log(Level.SEVERE, null , ex);
			}
			
			LoadFile.br = new BufferedReader(fr);
			String[] tmpArray = fileName.split("\\.");
			System.out.println(tmpArray[1]);
			if (tmpArray[1].equals("tsp"))
				getTspData(show);
			else
				getData(show);
		} 
		if (data.total > 0)
			return true;
		else
			return false;
	}
	
	private void getData(JPanel show){
		String tmp;
		String[] tmparray;
		int i =0;
		double sizeX=0;
		double sizeY=0;
		double addNumber = 0;
		Parameter parameter = Parameter.getInstance();
		
		try
		{
			//*****讀取一行*****//
			tmp = LoadFile.br.readLine();
			tmparray = tmp.split(" ");
			
			//*****資料集大小*****//
			this.data.total = Integer.parseInt(tmparray[0]);
			this.data.x = new double[data.total];
			this.data.y = new double[data.total];
			
			//*****讀取資料點*****//
			while((tmp = LoadFile.br.readLine()) != null)
			{
				tmparray = tmp.split(" ");
				this.data.x[i] = Double.valueOf(tmparray[0]);
				this.data.y[i] = Double.valueOf(tmparray[1]);
				
				if(this.data.x[i] > sizeX)
				{
					sizeX = this.data.x[i];
				}
				if(this.data.y[i] > sizeY)
				{
					sizeY = this.data.y[i];
				}
				if (this.data.x[i] < 0 || this.data.y[i] < 0) {
					if(this.data.x[i] < addNumber)
					{
						addNumber = this.data.x[i];
					}
					if(this.data.y[i] < addNumber)
					{
						addNumber = this.data.y[i];
					}
				}
				i++;
			}
			LoadFile.br.close();
			//*****取得XY軸比率*****//
			DrawPanel drawPanel = new DrawPanel();
			addNumber *= -1;
			parameter.setPointParameter(show.getWidth()/(sizeX+addNumber+1), 
									show.getHeight()/(sizeY+addNumber+1), addNumber);
			drawPanel.drawpanel(show);
			
		}catch(Exception ex){
			Logger.getLogger(LoadFile.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private void getTspData(JPanel show){
		String tmp;
		String[] tmparray;
		int i =0;
		double sizeX=0;
		double sizeY=0;
		double addNumber = 0;
		Parameter parameter = Parameter.getInstance();
		
		try
		{
			//*****讀取一行*****//
			tmp = br.readLine();
			tmparray = tmp.split("	");
			
			//*****資料集大小*****//
			data.total = Integer.parseInt(tmparray[0]);
			data.x = new double[data.total];
			data.y = new double[data.total];
			
			//*****讀取資料點*****//
			while((tmp = br.readLine()) != null)
			{
				tmparray = tmp.split("	");
				data.x[i] = Double.valueOf(tmparray[1]);
				data.y[i] = Double.valueOf(tmparray[2]);
				
				if(data.x[i] > sizeX)
				{
					sizeX = data.x[i];
				}
				if(data.y[i] > sizeY)
				{
					sizeY = data.y[i];
				}
				if (data.x[i] < 0 || data.y[i] < 0) {
					if(data.x[i] < addNumber)
					{
						addNumber = data.x[i];
					}
					if(data.y[i] < addNumber)
					{
						addNumber = data.y[i];
					}
				}
				i++;
			}
			
			//*****取得XY軸比率*****//
			DrawPanel drawPanel = new DrawPanel();
			addNumber *= -1;
			parameter.setPointParameter(show.getWidth()/(sizeX+addNumber+1), 
					show.getHeight()/(sizeY+addNumber+1), addNumber);
			drawPanel.drawpanel(show);
			
		}catch(Exception ex){
			Logger.getLogger(LoadFile.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
