package main.mineSweeper;


import java.awt.EventQueue;
import java.io.IOException;

import main.data.DataManager;
import main.graphicalUserInterface.GUIManager;

 public class MineSweeper{
	  
	 public MineSweeper() throws IOException
	 {
		   DataManager dataManager   = new DataManager();
		   new GUIManager(dataManager);  
	 }
	 
	 public static void main(String args[]){
			EventQueue.invokeLater(new MineSweeperGenerator());	
	}
		
	public static class MineSweeperGenerator implements Runnable{
		
		public void run() {
			try
			{
				new MineSweeper();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}

		}
	}
}


		
	
	
	 

      

          
         


       
