package main.data;

public class XYPosition{
	public int x;
	public int y;
	
	public XYPosition(int x, int y) {
		setXY(x,y);
	}
	
	@Override
	public String toString(){
	    	return "\n (xVal,yVal) :  ("+x+","+y+")"; 
	}
	
	public XYPosition() {
		x = -1;
		y = -1;
	}


	public XYPosition(XYPosition userCurrentClickPos) {
		setXY(userCurrentClickPos);
	}

	private void setXY(XYPosition pos) {
	       x = pos.x;
	       y = pos.y;	
		}

	
	public void setXY(int x,int y){
		this.x = x;
		this.y = y;
	}
	
	
	int getX(){
		return x;  
	}
	
	int getY(){
		return y;  
	}	
}