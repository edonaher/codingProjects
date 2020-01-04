package memfit;

import java.util.Comparator;

public class SortDescSize implements Comparator<Block> {
	@Override
	public int compare(Block left, Block right) {
		// TODO Auto-generated method stub
		//tricks it into sorting it backwards
		return Integer.compare(right.size, left.size);
	}
	
}
