package memfit;

import java.util.Comparator;

public class SortByOffset implements Comparator<Block> {
	@Override
	public int compare(Block left, Block right) {
		// TODO Auto-generated method stub
		return Integer.compare(left.offset, right.offset);
	}
}
