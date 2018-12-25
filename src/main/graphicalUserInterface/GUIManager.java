package main.graphicalUserInterface;


import main.graphicalUserInterface.GUIManager.BackAndFrontEndsActionSplicer;

import main.graphicalUserInterface.GameBoardManager.BoardButton;





import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import main.data.DataManager;
import main.data.XYPosition;

import main.mineSweeper.MineSweeper.MineSweeperGenerator;
 public class GUIManager extends JFrame 
 {
	  private static final long serialVersionUID = 1L;
	 
	  final  BoardMenuBar menuBar;
	  final GameBoardManager gUIGameBoard;
	  final GameInfoDisplayer gameInfoDisplayer;
	  final DataManager dataManager;
	  final IconManager   iconManager;
    
	  public GUIManager(final DataManager dataManager) throws IOException
	  {
		  
	   this.dataManager = dataManager;
	   
	   menuBar = new BoardMenuBar(this);
	   setJMenuBar(menuBar);
	    
	   gUIGameBoard     =   new   GameBoardManager(this,dataManager.getDimensionOfBoard()); //We must pass our reference since all the buttons needs ActionListener which is defined as a inner class
	   gameInfoDisplayer =  new   GameInfoDisplayer();
	   iconManager		 =  new   IconManager();
	   
	   setLayout(new BorderLayout());
	   add(gameInfoDisplayer.gameInfoFieldsContainer,BorderLayout.SOUTH);
	   add(gUIGameBoard,BorderLayout.CENTER);
	 
	   setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	   setTitle("MineSweeper v0.1 ");
	   setSize(500,500);
	   setIconImage(Toolkit.getDefaultToolkit().getImage("/media/Files/Movies,Pictures,Songs/Pictures/ScientistsPhotos"));
	   setResizable(false);
	   
	  
	   gameInfoDisplayer.setGameStatusText("Beware of Mines!!!");
	   gameInfoDisplayer.setNumberOfMinesUnflagged(dataManager.getTotalNumberOfMines());
	   gameInfoDisplayer.startTimer();
	   setVisible(true);
	  }
	
	  void removeFlag(XYPosition pos){
		gUIGameBoard.getBoardButtonAt(pos).setIcon(iconManager.removeIcon());		
		dataManager.setUnflagged(pos);
		gameInfoDisplayer.unflaggedMinesCounter.incrementNumberOfMinesUnflagged();
	  }
	
 	  void setFlag(XYPosition pos){
		gUIGameBoard.getBoardButtonAt(pos).setIcon(iconManager.flag);
		dataManager.setFlagged(pos);
		gameInfoDisplayer.unflaggedMinesCounter.decrementNumberOfMinesUnflagged();
	  }
		
      void setButtonUncovered(final XYPosition pos){
	    if(!(dataManager.isEmptySquare(pos) |  dataManager.isMine(pos) )){ //Set the Mine value visible if the box is not an empty square and it is not mine!
	    	 setMineValueVisible(pos,dataManager.getMineValue(pos));
	    }
	  }
   
      void setMineValueVisible(XYPosition pos,int mineValue){
		 gUIGameBoard.getBoardButtonAt(pos).setFont(new Font("Times new Roman",Font.BOLD,23)); 
	     gUIGameBoard.getBoardButtonAt(pos).setText(String.valueOf(mineValue));	 
	  }
	 
	  void setButtonDisabled(XYPosition pos){
		 gUIGameBoard.getBoardButtonAt(pos).setEnabled(false);
		 
		 gUIGameBoard.getBoardButtonAt(pos).removeActionListener(gUIGameBoard.getBoardButtonAt(pos).splicer);
		 gUIGameBoard.getBoardButtonAt(pos).removeMouseListener (gUIGameBoard.getBoardButtonAt(pos).splicer);
																					 	 
	  }
	  
	  class IconManager {
		  final Icon flag;
		  final Icon mine;
		  final Icon correctDecision;
		  final Icon wrongDecision;
		  final Icon blastedMine;
		  
		  IconManager() throws IOException
		  {
			  flag =  new ImageIcon(ImageIO.read(getClass().getResource("Flagged.jpg")));
			  mine = new ImageIcon(ImageIO.read(getClass().getResource("Mine.jpg")));
			  correctDecision = new ImageIcon(ImageIO.read(getClass().getResource("CorrectJudgement.jpg")));
			  wrongDecision = new ImageIcon(ImageIO.read(getClass().getResource("WrongJudgement.jpg")));
			  blastedMine = new ImageIcon(ImageIO.read(getClass().getResource("WrongJudgement.jpg")));
		 }		  
		   
		  Icon removeIcon(){
			  return null;
		  }	
		  
		  void assignIconsAtEndOfGame(){
			  XYPosition currentPos = new XYPosition(0,0);
			  for( currentPos.setXY(0,0) ; currentPos.x < gUIGameBoard.dimensionOfBoard; currentPos.x++)
		    		for(currentPos.y=0   ; currentPos.y < gUIGameBoard.dimensionOfBoard; currentPos.y++)
		    			if(dataManager.isFlagged(currentPos) ){
		    				if( dataManager.isMine(currentPos) )
		    					gUIGameBoard.getBoardButtonAt(currentPos).setIcon(correctDecision);
		    				else
		    					gUIGameBoard.getBoardButtonAt(currentPos).setIcon(wrongDecision);
		    					
		    			}
		    			else
		    				if( dataManager.isMine(currentPos) )
		    					gUIGameBoard.getBoardButtonAt(currentPos).setIcon(mine);
		    				else if(dataManager.isLost()){
		    					gUIGameBoard.getBoardButtonAt(dataManager.getMineBlastedPos()).setIcon(blastedMine);
		    				}
		   }
 
	  }//End of inner class Icon
	  
	  class BackAndFrontEndsActionSplicer extends MouseAdapter implements ActionListener  {		  
		   
		  	void uncoverTheVisibleButtons(){
				  XYPosition currentPos = new XYPosition(0,0);
				  for( currentPos.setXY(0,0) ; currentPos.x < gUIGameBoard.dimensionOfBoard; currentPos.x++)
			    		for(currentPos.y=0   ; currentPos.y < gUIGameBoard.dimensionOfBoard; currentPos.y++)
		    			if(dataManager.isVisible(currentPos)){
		    			  if(!dataManager.isMine(currentPos))// We are not disabling the mines because it make the icon picture dull.
		    				  setButtonDisabled(currentPos);
		    			  
		    			      setButtonUncovered(currentPos);	
		    			}
		    }		
   
			public void actionPerformed(ActionEvent event){
				XYPosition clickedButtonPos = ((BoardButton) event.getSource()).position;
				 
				dataManager.backEndClickActionTaker(clickedButtonPos);
						
				uncoverTheVisibleButtons();
				
				if(dataManager.isLost() | dataManager.isWon()){
					if(dataManager.isLost()){
						gameInfoDisplayer.setGameStatusText("You Lose!Try Again!!");
						gUIGameBoard.makeBoardNotResponding();
					}
					else
						gameInfoDisplayer.setGameStatusText("Brilliant!You Won!!");
				
				gameInfoDisplayer.stopTimer();
				iconManager.assignIconsAtEndOfGame();
				}
			}
		
			public void mouseClicked(MouseEvent event) {
			XYPosition clickedButtonPos = ((BoardButton) event.getSource()).position;
			      if(event.getButton() == MouseEvent.BUTTON3){ //If Right Mouse Button Is Clicked!!
			    	  if(dataManager.isFlagged(clickedButtonPos)){//if flagged!!!
			    		  removeFlag(clickedButtonPos);
			    		  gUIGameBoard.getBoardButtonAt(clickedButtonPos).addActionListener(gUIGameBoard.getBoardButtonAt(clickedButtonPos).splicer);
			    	  }
			    	  
			    	  else{//if already flagged!!!
			    		  setFlag(clickedButtonPos);
			    		  gUIGameBoard.getBoardButtonAt(clickedButtonPos).removeActionListener(gUIGameBoard.getBoardButtonAt(clickedButtonPos).splicer);
			          }
			      }	
			      if(dataManager.isWon()){
						gameInfoDisplayer.setGameStatusText("Brilliant!You Won!!");
					    gameInfoDisplayer.stopTimer();
					}
			}
			
	  }//End of inner class BackAndFrontEndsActionSplicer
}
 
 
 
class GameBoardManager extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final BoardButton boardButton[][];
	final int dimensionOfBoard;
	
	class BoardButton extends JButton{ 
		
		private static final long serialVersionUID = 1L;
		final XYPosition position;		
		final BackAndFrontEndsActionSplicer splicer;
		 
	BoardButton(XYPosition pos,GUIManager gUIManager){
			 
		     position  = pos;
			 
			 splicer = gUIManager.new BackAndFrontEndsActionSplicer();
			 
			 addActionListener(splicer);
			 addMouseListener(splicer);
	} 
		

	 }//End of inner class BoardButton
	 
	GameBoardManager(GUIManager gUIManager,int dimensionOfBoard){
		 this.dimensionOfBoard = dimensionOfBoard;
		 setLayout(new GridLayout(dimensionOfBoard,dimensionOfBoard));
		 boardButton          = new BoardButton[dimensionOfBoard][dimensionOfBoard];
		 		 
		 for(int i = 0; i < dimensionOfBoard ; i++ )
		   for(int j = 0; j < dimensionOfBoard ; j++){
		  		boardButton[i][j] = new BoardButton(new XYPosition(i,j),gUIManager);  
		            add(boardButton[i][j]);
	     }
    }

	 public BoardButton getBoardButtonAt(XYPosition pos) {
		return boardButton[pos.x][pos.y];
	}
	 
	 void makeBoardNotResponding()
	 {
		 XYPosition currentPos = new XYPosition();
		  for( currentPos.setXY(0,0) ; currentPos.x < dimensionOfBoard; currentPos.x++)
	    		for(currentPos.y=0   ; currentPos.y < dimensionOfBoard; currentPos.y++){	   
				 getBoardButtonAt(currentPos).removeActionListener(getBoardButtonAt(currentPos).splicer);
				 getBoardButtonAt(currentPos).removeMouseListener(getBoardButtonAt(currentPos).splicer);
			 }
	 }

}//End of inner class GameBoardManager

class BoardMenuBar extends JMenuBar
{
	private static final long serialVersionUID = 1L;
	
	JMenu gameMenu;
	JMenu helpMenu;
	MenuItemsActionListener menuItemsActionListener;
	
	BoardMenuBar(JFrame gUIManagerFrame)
	{
		
		menuItemsActionListener = new MenuItemsActionListener(gUIManagerFrame);
		gameMenu = new JMenu("Game");			
    		JMenuItem game_new = new JMenuItem("New");
    		    game_new.addActionListener(menuItemsActionListener);
    			gameMenu.add(game_new);
    		
    	 add(gameMenu);
		
         
		
		helpMenu = new JMenu("Help");
			JMenuItem help_howToPlay = new JMenuItem("HowToPlay");
				help_howToPlay.addActionListener(menuItemsActionListener);
				helpMenu.add(help_howToPlay);
				
			JMenuItem help_about = new JMenuItem("About MineSweeper v0.1");
				help_about.addActionListener(menuItemsActionListener);
				helpMenu.add(help_about);
		add(helpMenu);
		    	 
	    //setBounds(0, 0, getSize().width, 10);
		setVisible(true);	
	}
	
	static class MenuItemsActionListener implements ActionListener{

		
		JFrame gUIManagerFrame;
		
		
		public MenuItemsActionListener(JFrame gUIManagerFrame) {
               this.gUIManagerFrame = gUIManagerFrame;
        }

		public void actionPerformed(ActionEvent event){
			if((event.getActionCommand()).equals("New")){
				new Thread(new MineSweeperGenerator()).start();
			}	
			else{			
					class InfoDisplayer extends JDialog{
					JLabel info;
					private static final long serialVersionUID = 1L;
					BorderLayout infoBorderLayout = new BorderLayout();
					
					public InfoDisplayer(JFrame ownerFrame){
						super(ownerFrame,true); //Always modal is set to be true....
						info = new JLabel();
						setFocusable(false);						
						setDefaultCloseOperation(DISPOSE_ON_CLOSE);
						setLayout(infoBorderLayout);
						add(info,BorderLayout.CENTER);					
					}
					
					void setInfo(JLabel  info){
						 remove(info);
						 this.info = info;
						 add(info,BorderLayout.CENTER);
					}
					
					public void setTitle(String  title){
						   super.setTitle(title);	
						}									
					}
					
				if ((event.getActionCommand()).equals("HowToPlay")){
				InfoDisplayer instructionsDisplay = new InfoDisplayer(gUIManagerFrame);
				instructionsDisplay.setTitle("INSTRUCTIONS");
				instructionsDisplay.setInfo(new JLabel("<html><h1><U><B>INSTRUCTIONS:</B></U></h1> Its just Toy Game but it requires little effort of your mind. <B>The number of Squares and number of mines are user defined.</B> Trace all Mines and Flag it by right clicking on it! " +
						"LeftClicking a square,uncovers it! A Square can be : <h3> <B> Empty Square </B> </h3> or <h3><B> Non-Empty Square</B> </h3> or <B> <h3> Mine </B> </h3> If it a Mine, then you lose. If it an Empty Square then you are lucky enough, since it will uncover all the adjacent empty squares which inturn uncover its ajacents in a recursive manner." +
						"If It is a Non-Empty Square, then that value gives the information about the number of mines sorrounding it! To unflag a square do a right click on it again. All the Best! Suggestions are most welcome!" +
						" <B> Send your comments to <I> muthamilnathan@gmail.com </I>  or <I> rktserviceproviders@gmail.com </I></B> </html>"));
				instructionsDisplay.setSize(300,500);
				instructionsDisplay.setResizable(false);
				instructionsDisplay.setVisible(true);
		
				}	
				else{// if about is clicked!!
				InfoDisplayer aboutDisplay = new InfoDisplayer(gUIManagerFrame);
				aboutDisplay.setTitle("MineSweeper v0.1");
				aboutDisplay.setInfo(new JLabel("<html><h1><B><I><U>DEVELOPERS :</U></I> <h2> 1. MUTHU GANAPATHY NATHAN . P , Valliammai Engineering College</h2>  <h2> 2. AJANTH . P, Valliammai Engineering College </h2> </B></h1></html>"));
				aboutDisplay.setSize(400,200);
				aboutDisplay.setResizable(false);
				aboutDisplay.setVisible(true);
				
				
			}
		}			
			
			 
		 }
	 }
}

class GameInfoDisplayer{
	 
	private static final long serialVersionUID = 1L;

	private StatusDisplayer statusDisplayer;
	private TimeController timeController;
	UnflaggedMinesCounter unflaggedMinesCounter;
	
	JComponent gameInfoFieldsContainer = new JPanel();
	
	GameInfoDisplayer(){
		gameInfoFieldsContainer.setLayout(new GridLayout(2,2));
	
		statusDisplayer       =  new StatusDisplayer();
		timeController        =  new TimeController();
		unflaggedMinesCounter =  new UnflaggedMinesCounter();
		
		gameInfoFieldsContainer.add(new JTextField("Have A Nice Day!!!!!"));
		
	}
	
	public void setNumberOfMinesUnflagged(int totalNumberOfMines) {
		unflaggedMinesCounter.setNumberOfMinesUnflagged(totalNumberOfMines);	
	}

	void setGameStatusText(String s){
		statusDisplayer.displayStatus(s);
	}
	
	void startTimer(){
		timeController.start();	
	}
	
	void stopTimer(){
		timeController.stop();
	}
 
	private class TimeController{
    	private TimeUpdatorAndPrinter timeUpdatorAndPrinter;
    	Timer timer;
    	 
    	private JTextField displayField;
    	
    	TimeController(){
    		timeUpdatorAndPrinter = new TimeUpdatorAndPrinter();
    		timer = new Timer(1000,timeUpdatorAndPrinter);
    		gameInfoFieldsContainer.add(displayField);
     	}
    	
    	public void start() {              
    	   timer.start();
		}
    	
    	public void stop() {              
     	   timer.stop();
 		}
    	
    	private class TimeUpdatorAndPrinter implements ActionListener {
    		int timeCounter;  		
    		TimeUpdatorAndPrinter(){
    			timeCounter  = 0;
    			displayField =  new JTextField();
    			displayField.setFocusable(false);		
    			displayField.setText("Time: 0 Secs.");
    		}
    		 public void actionPerformed(ActionEvent event){
    			timeCounter += 1;
    			displayField.setText("Time: " + String.valueOf(timeCounter) + " Secs.");
    		}	
    		 
    		
    	}
    }
    
    private class StatusDisplayer{
    	JTextField displayField;
		StatusDisplayer(){
			displayField = new JTextField();
			displayField.setFocusable(false);			
			gameInfoFieldsContainer.add(displayField);
		}
		void displayStatus(String s){
			displayField.setText(s);
		}
	}	
    	
     class UnflaggedMinesCounter{
    		
    		int numberOfMinesUnflagged;
    		JTextField displayField;
    		
    		UnflaggedMinesCounter(){
    			 displayField = new JTextField();
    			 displayField.setFocusable(false);
    			 gameInfoFieldsContainer.add(displayField);
    		}
    		
    		void setNumberOfMinesUnflagged(int n){
    			numberOfMinesUnflagged = n;
    			displayNumberOfMinesUnflagged();
    		}

    		void  incrementNumberOfMinesUnflagged(){
    			numberOfMinesUnflagged += 1;
    			displayNumberOfMinesUnflagged();
    		}
    		void  decrementNumberOfMinesUnflagged(){
    			numberOfMinesUnflagged -= 1;
    			displayNumberOfMinesUnflagged();
    		}
    		
    		int getNumberOfMinesUnflagged(){
    			return numberOfMinesUnflagged;
    		}
    		
    		void displayNumberOfMinesUnflagged(){
    			displayField.setText("Mines Left : " + numberOfMinesUnflagged);
    		}
    	
    		
    	}//End of class UnflaggedMinesCounter
    	
    }//End of class
    
    
    

