package memfit;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.Line2D;

/**                                                                                               
 *  Implements a graphical canvas that displays a bar of allocated memory                                 
 *                                                                                                
 *  @author  Eleanor Donaher                                                                     
 *  @version CSC 112, 18 April 2006                                                               
 */
class DrawMem extends JComponent {
    /** The Blocks */
    private ArrayList<Block> list;

    
    //pool size to tell what the dimensions must be scaled to
    private int poolSize;
    
    /** Constructor */
    public DrawMem() {
        list = new ArrayList<Block>();
        setMinimumSize(new Dimension(1000,200));
        setPreferredSize(new Dimension(1000,200));
    }

    /**                                                                                           
     *  Paints a bar showing allocated/unallocated space                                 
     *                                                                                            
     *  @param g The graphics object to draw with                                                 
     */
    
    public void paintComponent(Graphics g) {
    	//draw one large empty white rectangle to represent an empty pool
	    g.setColor(Color.WHITE);
        g.fillRect(10,0, 1000, 100);
        
        
        //set scale from poolSize (this is not perfect and will be off if there is a remainder)
        int scale=0;
        if(poolSize!=0){
        scale = 1000/poolSize;
        }
        //for each block in the list
        //if theres more than just pool
       
        for(Block block : list){

        	int blockSize = block.getSize()*scale;
        	int blockOffset = block.getOffset()*scale;
        	g.setColor(Color.GREEN);
        	g.fillRect(blockOffset, 0, blockSize, 100);
        	g.setColor(Color.BLACK);
        	g.drawRect(blockOffset, 0, blockSize, 100);
        	//if the block is not the pool
       	 if(!block.getName().equals("pool")){
        	g.setColor(Color.BLACK);
        	Font font = new Font("Verdana", Font.BOLD, 20);
        	g.setFont(font);
        	g.drawString(block.getName(), (int)(blockSize/2+blockOffset), 50 );
        }
        }
    }
    //manipulator for list
    public void setList(ArrayList<Block> list){
    	this.list = list;
    }
	//manipulator for poolSize
	public void setPoolSize(int poolSize){
		this.poolSize = poolSize;
	}
}

