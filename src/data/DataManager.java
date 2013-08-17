package data;

import java.util.ArrayList;
import java.util.Random;




class Square  
{
	
	int mineValue;
	boolean isVisible;
	boolean isMine;
	boolean isEmptySquare;
	boolean isFlagged;
	boolean isFirstUncoveredSquare;
	final XYPosition position;
	XYPosition  adjacentPositions[];
   
			
	public Square(XYPosition position) {
		this.position = position;		
		isVisible = false;
		mineValue = 0; 
	  	isMine = false;
	  	isFirstUncoveredSquare = false; 
	}

	boolean isAdjacentTo(Square targetSquare){
		
		for(XYPosition currentAdjacentPosition : adjacentPositions)
			 if( (targetSquare.position).equals(currentAdjacentPosition) ) //Is the target squares position is in our Adjacency list....
	            	return true; 
		
		return false;		
	}
	
	boolean isAdjacentTo(XYPosition targetPos){
		
		for(XYPosition currentAdjacentPosition : adjacentPositions)
			 if( (currentAdjacentPosition).equals(targetPos) )
	            	return true; 
		
		return false;		
	}

}//End of inner class Square


class Board 
{
	final int dimOfBoard;
    final int totalNumberOfMinesInTheBoard;
    int numberOfMinesCorrectlyFlagged;
    
    
    
    private Square square[][];
   
	
	
	Board(int dimOfBoard,int totalNumberOfMinesInTheBoard)
	{
		this.dimOfBoard = dimOfBoard;
		this.totalNumberOfMinesInTheBoard = totalNumberOfMinesInTheBoard;
	    numberOfMinesCorrectlyFlagged = 0; 
   
	    squaresCreator();
	    validAdjacentPositionsListsGenerator();
	}
	
		
	private int getNumberOfMinesInAdjacentSquares(Square currSqu)
	{
		int totalNumberOfMinesInAdjacentSquares = 0;
		
		for(XYPosition currentAdjacentPosition : currSqu.adjacentPositions)
			if( getSquareAt(currentAdjacentPosition).isMine ) //Is the adjacent Square is a Mine....
				totalNumberOfMinesInAdjacentSquares += 1;
		
		return totalNumberOfMinesInAdjacentSquares;
	}
	
			
	private boolean isThisPositionExistsWithinBounds(XYPosition pos) {
		if( (pos.x >= 0 && pos.y >=0) && (pos.x < dimOfBoard  &&  pos.y < dimOfBoard))
			return true;
		
		else
			return false;
    }
    
    private void squaresCreator()
    {
    	square = new Square[dimOfBoard][dimOfBoard];
    	for(int i = 0 ; i < dimOfBoard ; i++ )
    		for(int j = 0 ; j < dimOfBoard ; j++ )
	    	  square[i][j] = new Square(new XYPosition(i,j));
    }
    
    
    
   	
	private void initialMinePlacer()
	{
		class RandXYPairForMine{
			
			private XYPosition randomPos;
			
			public RandXYPairForMine(){
				randomPos = new XYPosition();				
			}
			
			XYPosition nextRandXY()
			{
				Random random = new Random();
			
				randomPos.setXY(random.nextInt(dimOfBoard),random.nextInt(dimOfBoard));
				//Logger logger = Logger.getLogger("RandXy");
				//logger.info(randomPos.toString());
				if(isValidPositionToBeMine(randomPos))
					return randomPos;
				else
					return nextRandXY();
			}

			
			/**
			 * 1st case : A Position is invalid to be a mine if it has firstClickPosition as one of its adjacents. Since firstClick must be always an empty square
			 * such that it can spread its visibility!  
			 * 2nd case : If a square is already selected as mine then for the 2nd time the same square cannot be selected!
			 * In other words a square cannot be selected as a validMine if it is already a mine.
			 * */
			
			private boolean isValidPositionToBeMine(XYPosition pos) {
				if(getSquareAt(pos).isMine | isAnyAdjacentAFirstClickedSquare(pos) )
					return false;
			  else 	
				 return true;
			}

			private boolean isAnyAdjacentAFirstClickedSquare(XYPosition pos) {
				
				for(XYPosition currentAdjacentPosition : getSquareAt(pos).adjacentPositions)
					if(getSquareAt(currentAdjacentPosition).isFirstUncoveredSquare)
						return true; 
				
				   return false;
			}	
		}//End of class RandXYPair.
	
		RandXYPairForMine randomXYPair = new RandXYPairForMine();
		
		for(int i=0;i<totalNumberOfMinesInTheBoard;i++)
		{
		    XYPosition pos = randomXYPair.nextRandXY();
			getSquareAt(pos).isMine = true;
			getSquareAt(pos).isEmptySquare = false;			
		}
	}

	private void mineValueAssigner()
	{
		for(int i=0;i<dimOfBoard;i++)
			for(int j=0;j<dimOfBoard;j++)
			{
				 if(!square[i][j].isMine)
				 {
					 square[i][j].mineValue = getNumberOfMinesInAdjacentSquares(square[i][j]);
					 if(square[i][j].mineValue != 0)
						 square[i][j].isEmptySquare = false;
					 else	 /* If a square has a mine value then its adjacents have mine, in transitive it is not an empty square. */
						 square[i][j].isEmptySquare = true;
						 
				 }
				 else
					 square[i][j].mineValue = -1;
					 
			}	
	
	}
	
	
		
	private void  validAdjacentPositionsListsGenerator(){
		for(int i=0;i<dimOfBoard;i++)
			for(int j=0;j<dimOfBoard;j++)
				getValidAdjacentPositionsList(square[i][j].position);
	}
	
	private void getValidAdjacentPositionsList(XYPosition pos)
	 {
			int[] adjacentTracer = {-1,0,1};  // Here -1 is used for making left movement and 1 is used for making right movement.	
			
			ArrayList<XYPosition>  adjacentPositionsArrayList = new ArrayList<XYPosition>();
			
			for(int i=0;i<adjacentTracer.length;i++)
				  for(int j=0;j<adjacentTracer.length;j++){	  
					  XYPosition currentAdjacentPos = new XYPosition(pos.x + adjacentTracer[i],pos.y + adjacentTracer[j]);
					  
					  if(isThisPositionExistsWithinBounds(currentAdjacentPos)){
						  adjacentPositionsArrayList.add(currentAdjacentPos);
					  }
				  }
			  getSquareAt(pos).adjacentPositions = new XYPosition[adjacentPositionsArrayList.size()];
			  adjacentPositionsArrayList.toArray(getSquareAt(pos).adjacentPositions);
		}
			
	 public void mineAndMineValueInitializer(){
		   initialMinePlacer();
		   mineValueAssigner();
	 }

	Square getSquareAt(XYPosition xyPos) {
		return square[xyPos.x][xyPos.y];
	}
    
}

public class DataManager extends Board
{
	  private boolean isLost;
	  private boolean isWon;
	  private XYPosition mineBlastedPos;
      private boolean isThisFirstClickInBoard;
	  
	  
	  public DataManager(){ 
    	  super(10,10);   	     	   	    
  		  isLost = false;
  		  isWon  = false;
  		  isThisFirstClickInBoard = true;
  		  
      }
	  
	  private boolean checkWonStatus(){
		  XYPosition currentPos = new XYPosition(); 
		  for( currentPos.setXY(0,0) ; currentPos.x < dimOfBoard; currentPos.x++)
	    	for(currentPos.y = 0 ; currentPos.y < dimOfBoard; currentPos.y++)	  	                  
			  if( getSquareAt(currentPos).isFlagged ^ getSquareAt(currentPos).isMine ) // When one button is flagged, then it must be a mine or vice versa. If the converse is true then the OP was not won.
			     return false;
				      	      
				  
		return true;
	  }
	  
	  public int getTotalNumberOfMines(){
			return totalNumberOfMinesInTheBoard;
	  }
	  
	  public void  backEndClickActionTaker(XYPosition userCurrentClickPos)
	  {
		   if(isThisFirstClickInBoard){
			   getSquareAt(userCurrentClickPos).isFirstUncoveredSquare = true;
			   super.mineAndMineValueInitializer();
			   isThisFirstClickInBoard = false;
		   }
		   
		   Square s = getSquareAt(userCurrentClickPos);
		  
		    s.isVisible = true;
		    
		    if(s.isMine)
		    {
		    	//Make some special smliey to act in a special manner   	
		       	setMineBlastedPos(userCurrentClickPos);
		       	mineBlasted();
		    }
		    
		    if(s.isEmptySquare)
		    	spreadVisibility(s);
		    
		    isWon = checkWonStatus();
		    
	  }
	  
	    private void setMineBlastedPos(XYPosition userCurrentClickPos) { 
		mineBlastedPos = new XYPosition(userCurrentClickPos);
  	    }
	    
	    public XYPosition getMineBlastedPos(){
	    	return mineBlastedPos;
	    }

		public boolean isLost(){
	    	return isLost;
	    }

	    public boolean isWon(){
	    	return isWon;
	    
	    }
	 
	  private void spreadVisibility(Square s)
	  {
		  if(!s.isFlagged){ //Don't spread the visibility if it is flagged!!
		      s.isVisible = true;
		      if(s.isEmptySquare){  //If it is an empty square....
		    	  for(XYPosition currentAdjacentPosition : s.adjacentPositions )				 
					 if(!getSquareAt(currentAdjacentPosition).isVisible)
							spreadVisibility(getSquareAt(currentAdjacentPosition));	     //spread the visibility to their adjacent squares.....
 				  }
		      }

		  }
	   
	  private void mineBlasted(){
	 	  makeAllMinesVisible();
		  isLost = true;
	  }
	  
	  private void makeAllMinesVisible(){
		  XYPosition currentPos = new XYPosition(); 
		  for( currentPos.setXY(0,0) ; currentPos.x < dimOfBoard; currentPos.x++)
	    	for(currentPos.y = 0 ; currentPos.y < dimOfBoard; currentPos.y++)	  	                  
			  if(getSquareAt(currentPos).isMine)
					  getSquareAt(currentPos).isVisible = true;
	  }

     public int getDimensionOfBoard() {		
		return dimOfBoard;
	}

	public void setFlagged(XYPosition pos) {
		getSquareAt(pos).isFlagged = true;
	}

	public boolean isEmptySquare(XYPosition pos) {
		return getSquareAt(pos).isEmptySquare;		
	}

	public void setUnflagged(XYPosition pos) {
		getSquareAt(pos).isFlagged = false;		
	}

	public boolean isMine(XYPosition pos) {		
		return getSquareAt(pos).isMine;
	}

	public int getMineValue(XYPosition pos) {
		return getSquareAt(pos).mineValue;
	}

	public boolean isFlagged(XYPosition pos) {
		return getSquareAt(pos).isFlagged;
	}

	public boolean isVisible(XYPosition pos) {
		return getSquareAt(pos).isVisible;
	}
	
	  
}

