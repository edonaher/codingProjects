package memfit;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

/**                                                                                               
 *  Implements a GUI for inputting points.                                                        
 *                                                                                                
 *  @author  Nicholas R. Howe, Elizabeth Muirhead                                                 
 *  @version December 2017                                                                   
 */
public class MemfitGUI {

	//drawmem class draws the bars for used and freeList
	private DrawMem usedList;
	private DrawMem freeList;

	//simulation that will be running
	private Simulation s;

	//these will need to be updated as the program runs
	private JLabel errorMsg;
	private JTextField userInstr;

	//variables for statistics
	private JLabel usedPercent;
	private JLabel freePercent;
	private JLabel failedAllocs;

	//check if pool has been called before
	private boolean poolCalled;
	/**                                                                                           
	 *  Schedules a job for the event-dispatching thread                                          
	 *  creating and showing this application's GUI.                                              
	 */
	public static void main(String[] args) {
		final MemfitGUI GUI = new MemfitGUI();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GUI.createAndShowGUI();
			}
		});
	}

	/** Sets up the GUI window */
	public void createAndShowGUI() {
		// Make sure we have nice window decorations.                                             
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.                                                          
		JFrame frame = new JFrame("Memory Allocation GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add components                                                                         
		createComponents(frame);

		// Display the window.                                                                    
		frame.pack();
		frame.setVisible(true);
	}
	/** Puts content in the GUI window */
	public void createComponents(JFrame frame) {
		// graph display                                                                          
		Container pane = frame.getContentPane();
		pane.setLayout(new FlowLayout());
		//panel to hold p2, freelistlabel, freelist, usedlistlabel, usedlist
		JPanel panel1 = new JPanel();
		//panel to hold instruction label & instruction
		JPanel panel2 = new JPanel();
		//panel to hold error label and error
		JPanel panel3 = new JPanel();
		//panel to hold statistics
		JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayout(3,3));
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		JLabel instrLabel = new JLabel("Instruction");
		userInstr = new JTextField(30);
		//begins empty
		errorMsg = new JLabel("");
		JLabel errorLabel = new JLabel("Error: ");
		JLabel freeLabel = new JLabel("FreeList");
		JLabel usedLabel = new JLabel("UsedList");
		JLabel freePercentLabel = new JLabel("Percent Free: ");
		JLabel usedPercentLabel = new JLabel("Percent Used: ");
		JLabel failedAllocLabel = new JLabel("Failed Allocs: ");

		//start states for allocations
		freePercent = new JLabel("100%");
		usedPercent = new JLabel("0%");
		failedAllocs = new JLabel("0");

		//bars for free and usedList
		freeList = new DrawMem();
		usedList = new DrawMem();

		//add to panel 2
		panel2.add(instrLabel);
		panel2.add(userInstr);

		//add to panel 3
		panel3.add(errorLabel);
		panel3.add(errorMsg);

		//add to panel 4
		panel4.add(freePercentLabel);
		panel4.add(freePercent);
		panel4.add(usedPercentLabel);
		panel4.add(usedPercent);
		panel4.add(failedAllocLabel);
		panel4.add(failedAllocs);

		panel1.add(panel2);
		panel1.add(freeLabel);
		panel1.add(freeList);
		panel1.add(usedLabel);
		panel1.add(usedList);
		panel1.add(panel3);
		panel1.add(panel4);

		//add to pane
		pane.add(panel1);
		//listeners
		userInstr.addActionListener(new userInstrListener());

		//simulation to conceptually track the data
		s = new Simulation();
	}//end create components

	/** Listener for userInstr button */
	private class userInstrListener implements ActionListener {
		/** Event handler for userInstr button */
		public void actionPerformed(ActionEvent e) {
			boolean error = false;
			String cmd = userInstr.getText();
			cmd.toLowerCase();
			//reset error msg to blank
			errorMsg.setText("");
			boolean invalidCmd = checkValidCmd(cmd);
			//if the cmd is invalid
			if(invalidCmd){
				//print error msg
				errorMsg.setText("ERROR: " + cmd + " is not a valid command");
				//break out of the loop
				return;
			}

			//clear text area again
			userInstr.setText("");
			//execute the user's command in the graph
			s.executeCommand(cmd);

			//update statistics
			failedAllocs.setText(Integer.toString((s.getFailedAlloc())));
			usedPercent.setText(s.getUsedPercent());
			freePercent.setText(s.getFreePercent());
			//if error is true, print error msg in errorlabel
			error = s.getError();
			if(error){
				//print error msg
				errorMsg.setText("ERROR: BLOCK TOO LARGE TO ALLOCATE/THIS IS NOT A VALID BLOCK");
			}		
			//now handle the painting of the blocks

			//set poolSize so we know how to scale the blocks
			int poolSize = s.getpoolSize();
			usedList.setPoolSize(poolSize);
			freeList.setPoolSize(poolSize);

			//update used and freeLists in GUI
			usedList.setList(s.getUsedList());
			freeList.setList(s.getFreeList());

			//finally repaint
			usedList.repaint();
			freeList.repaint();
		}
	}
	public boolean checkValidCmd(String cmd){
		//split cmd into separate words
		String[] words = cmd.split(" ");
		if(words[0].equals("pool")){
			//if pool has been called before
			if(poolCalled){
				//this is not allowed again!
				return true;
			}
			poolCalled=true;
			return false;
		}
		//if cmd is alloc
		if(words[0].equals("alloc")){
			//there are not the right number of args
			if(words.length!=3){
				return true;
			}
		}
		//if cmd is free
		else if(words[0].equals("free")){
			//there are not the right number of args
			if(words.length!=2){
				return true;
			}
		}
		//the cmd is not valid
		else{
			return true;
		}
		return false;
	}
}
