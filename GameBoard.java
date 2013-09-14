import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;

public class GameBoard {
	private Slot[][] slots = new Slot[7][7];
	
	public GameBoard() {
		for(int i = 0; i < 7; i++)
			for(int j = 0; j < 7; j++)
				if( (i > 1 && i < 5) || (j > 1 && j < 5) ) {
					slots[i][j] = new Slot(i, j, Color.lightGray);
					if( !(i == 3 && j == 3) )
						slots[i][j].putMarble( getInitMarble(i,j) );
				}
	}
	
	private Color getInitMarble(int i, int j) {
		if( (j > 1 && j < 5) && (i < 3) )	//top
			return Color.red;
		if( (j > 1 && j < 5) && (i > 3) )	//bottom
			return Color.black;
		if( (i > 1 && i < 5) && (j < 3) )	//left
			return Color.orange;
		if( (i > 1 && i < 5) && (j > 3) )	//right
			return Color.blue;
		return Color.lightGray;
	}
	
	public Slot getSlot(int i, int j) {
		return slots[i][j];
	}

}
