package memfit;

public class Block {
	//I’d want a block class with at least these fields and methods.
	String name;
	int offset;
	int size;
	
    /** Constructor initializes data elements */
    public Block(String name, int offset, int size) {
        this.name = name;
        this.offset = offset;
        this.size = size;
    }
    public void setName(String newName) {
        this.name = newName;
      }
    public void setOffset(int newOffset) {
        this.offset = newOffset;
      }
    public void setSize(int newSize) {
        this.size = newSize;
      }
	public String toString(){
		String Block = ("Block Name, Offset, Size: "+ name + " " + offset + " " + size);
		return Block;
	}
	//Accessor for name
	public String getName() {
		return name;
	}
	//Accessor for size
	public int getSize() {
		return size;
	}
	//Accessor for offset
	public int getOffset() {
		return offset;
	}
	public boolean is_adjacent(Block other) { /* #2 on worksheet*/
		boolean adj = false;
		if(offset==(other.offset+other.size)){
			adj=true;
		}
		return adj;
	}
}
