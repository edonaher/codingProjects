package memfit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Simulation {
	//lists for used and free blocks
	private ArrayList<Block> freeList;
	private ArrayList<Block> usedList;
	//variable to store which block offset was selected last
	private int prevOffset;
	//variable for number of times we can't fit a block
	private int failedAlloc;
	//variable for which fit file designates
	private String fitAlg;

	//poolsize int for gui
	private int poolSize;
	//only set if there is an allocation error
	private boolean error;

	private String errorMsg;
	/** Constructor initializes data elements */
	public Simulation() {
		freeList = new ArrayList<Block>();
		usedList = new ArrayList<Block>();
		fitAlg = "";
		prevOffset=0;
		failedAlloc=0;
		error = false;
		poolSize=0;
	}

	//method to read file
	public static List<String> readFile(String fileName){
		List<String> fileLines = null;
		try {
			fileLines = Files.readAllLines(new File(fileName).toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileLines;
	}
	//method that reads through lines and branches to commands
	public void executeCommand(String userCmd){
		//for user's cmd
		//split line into individual words
		String[] words = userCmd.split(" ");
		String command = words[0];
		switch(command){
		case "pool":
			fitAlg = words[1];
			//pool command that will allocate space in free list
			pool(Integer.parseInt(words[2]));
			poolSize= Integer.parseInt(words[2]);
			break;
		case "alloc":
			//alloc takes name and amt space and creates block
			alloc(words[1], Integer.parseInt(words[2]));
			break;
		case "free":
			//free takes name and removes it from list
			free(words[1]);
			break;
		default:
			System.out.println(command + ": is not a valid command");
			break;
		}

	}
	//method for pool command
	public void pool(int amtSpace){
		//first block that will comprise entire freeList (to begin)
		Block poolBlock = new Block("pool", 0, amtSpace);
		//add it to freeList
		freeList.add(poolBlock);
	}

	//method to allocate new block
	public void alloc(String name, int size){
		//make sure error is reset to false every time it runs
		error=false;
		boolean foundBlock = false;
		//sort list correctly depending on fitAlg
		if(fitAlg.equals("best")){
			//call sortAscSize
			freeList.sort(new SortAscSize());
		}
		if(fitAlg.equals("worst")){
			//call DescBySize
			freeList.sort(new SortDescSize());
		}
		if(fitAlg.equals("random")){
			//call sortRandom
			Collections.shuffle(freeList);
		}
		if(fitAlg.equals("first")){
			//call sortbyOffset
			freeList.sort(new SortByOffset());
		}
		if(fitAlg.equals("next")){
			Block selected = null;
			//assume next fit works on list sorted by offset
			//call sortbyOffset
			freeList.sort(new SortByOffset());
			//find index of prev offset block
			int prevIndex=0;
			for(int i=0; i<freeList.size(); i++){
				if(freeList.get(i).offset>=prevOffset){
					prevIndex=i;
				}
			}
			//if there is only one thing in freeList
			if(freeList.size()==1){
				if(freeList.get(0).size>=size){
					selected = freeList.get(0);
					prevOffset = freeList.get(0).offset;
					//allocate found block
					blockSplit(name, selected, size);
					//return -- the rest of this method will not work for this fitAlg
					return;	
				}
			}
			else{
				//first loop goes from prevIndex to end of list
				for(int i=prevIndex; i<freeList.size(); i++){
					if(freeList.get(i).size>=size){
						selected = freeList.get(i);
						prevOffset = freeList.get(i).offset;
						//allocate found block
						blockSplit(name, selected, size);
						//return -- the rest of this method will not work for this fitAlg
						return;				
					}

				}
				//second loop will run from 0 to prev index
				for(int i=0; i<prevIndex;i++){
					if(freeList.get(i).size>=size){
						selected = freeList.get(i);
						prevOffset = freeList.get(i).offset;
						//allocate found block
						blockSplit(name, selected, size);
						//return -- the rest of this method will not work for this fitAlg
						return;				
					}
				}
			}//else next
			//error-block will not fit
			failedAlloc++;
			error=true;
			return;


		} // if next


		//go through free list then update used/free list to allocate space
		for(Block block: freeList){
			if(block.size>= size){
				blockSplit(name, block, size);
				foundBlock = true;
				break;
			}
		}
		//there is no block large enough to allocate the request
		if(!foundBlock){
			error=true;
			failedAlloc++;
		}

	}

	//method to split block
	void blockSplit(String name, Block block, int request_size){
		//make sure usedList is sorted by offset
		usedList.sort(new SortByOffset());

		Block alloc_Block = new Block(name, block.offset, request_size);
		//if the new block is the exact size of the free block
		if(block.size==request_size){
			//take block out of freelist
			freeList.remove(block);
		}
		else{
			//set old block's offset to offset+requestsize
			//set old blocks size to size-requestsize
			block.setOffset(block.offset+request_size);
			block.setSize(block.size-request_size);
		}
		//add new block to usedList
		usedList.add(alloc_Block);


	}
	//method to free block
	public void free(String name){

		//scroll through used list to find correct block
		boolean foundBlock = false;
		Block toFree=null;
		for(Block block: usedList){
			if(block.name.equals(name)){
				//break loop as we have found correct index
				toFree = block;
				foundBlock = true;
				break;
			}
		}
		if(!foundBlock){
			//should find block--if it gets through the list its wrong!
			failedAlloc++;
			error=true;
			return;
		}
		//remove block from usedList
		usedList.remove(toFree);
		freeList.add(toFree);
		//sort freeList by offset
		freeList.sort(new SortByOffset());
		//compact free list so adjacent new blocks are consolidated
		compactList(freeList);
	}

	//compacts the blocks in the list (freelist or usedList
	public void compactList(ArrayList<Block> list){
		for(int i=0; i<(list.size()-1); i++){
			//if the two blocks are adjacent
			if(list.get(i).is_adjacent(list.get(i+1))){
				//set the size of the first block to the size of the first and second blocks
				list.get(i).setSize(list.get(i).size + list.get(i+1).size);
				//take the second block out of the list
				list.remove(list.get(i+1));
				//change the iterator to account for there being one less block
				i--;
			}
		}
	}

	//accessor for freeList
	public ArrayList<Block> getFreeList(){
		return freeList;
	}
	//accessor for usedList
	public ArrayList<Block> getUsedList(){
		return usedList;
	}
	//accessor for error bool
	public boolean getError(){
		return error;
	}
	//accessor for poolSize
	public int getpoolSize(){
		return poolSize;
	}
	public int getFailedAlloc(){
		return failedAlloc;
	}
	public String getFreePercent(){
		double blockSize=0;
		for(Block block : freeList){
			blockSize+=block.size;
		}
		double p = (blockSize/(double)poolSize)*100;
		String percent = Double.toString(p);
		return (percent+"%");
	}//end getFreePercent
	
	public String getUsedPercent(){
		double blockSize=0;
		for(Block block : usedList){
			blockSize+=block.size;
		}
		double p = (blockSize/(double)poolSize)*100;
		String percent = Double.toString(p);
		return (percent+"%");
	}//end getUsedPercent
}

