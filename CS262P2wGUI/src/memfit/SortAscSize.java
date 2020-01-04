package memfit;

import java.util.Comparator;

public class SortAscSize implements Comparator<Block> {
	@Override
	public int compare(Block left, Block right) {
		// TODO Auto-generated method stub
		return Integer.compare(left.size, right.size);
	}
	

}
