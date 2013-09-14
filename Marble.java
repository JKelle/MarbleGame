import java.awt.Color;

public class Marble {
	private Color color;
	private int i;
	private int j;
	
	public Marble(Color c) {
		color = c;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int[] getLoc() {
		return new int[]{i,j};
	}
	public void setLoc(int i, int j) {
		this.i = i;
		this.j = j;
	}
}
