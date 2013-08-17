package mineSweeper;


import java.awt.EventQueue;

import data.DataManager;
import graphicalUserInterface.GUIManager;

 public class MineSweeper{
	  
	 public MineSweeper(){
		   DataManager dataManager   = new DataManager();
		   new GUIManager(dataManager);  
	 }
	 
		public static void main(String args[]){
			EventQueue.invokeLater(new MineSweeperGenerator());	
	}
		
	public static class MineSweeperGenerator implements Runnable{
		
		public void run() {
                  new MineSweeper();
                  
		}
	}
}


		
	
	
	 

      

          
         


       
