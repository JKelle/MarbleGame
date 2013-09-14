import java.awt.Color;

public class Slot {
	private boolean isOccupied;
	private Color c;
	private int i;
	private int j;
	
	public Slot(int i, int j, Color col) {
		this.i = i;
		this.j = j;
		c = col;
		isOccupied = false;
	}	
	
	public void putMarble(Color col) {
		if( isOccupied )
			throw new IllegalStateException("Attempt to put marble in isOccupied slot.");
		c = col;
		isOccupied = true;
	}
	
	public Color removeMarble() {
		Color toRemove = c;
		c = null;
		isOccupied = false;
		c = Color.lightGray;
		return toRemove;
	}
	
	public boolean isOccupied() {
		return isOccupied;
	}
	
	public Color getColor() {
		return c;
	}
	
	public int[] getLoc() {
		return new int[]{i,j};
	}
	
	public String toString() {
		return "("+ i +", "+ j +")";
	}
}
