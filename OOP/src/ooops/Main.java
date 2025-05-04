package ooops;





import java.awt.FlowLayout;

import javax.swing.JFrame;


import uk.ac.leedsbeckett.oop.LBUGraphics;


public class Main extends LBUGraphics {
	


	public Main()
	{
		JFrame MainFrame = new JFrame();            //create a frame to display the turtle panel on
		MainFrame.setLayout(new FlowLayout());     //not strictly necessary
		MainFrame.add(this);                       //"this" is this object that extends turtle graphics so we are adding a turtle graphics panel to the frame
		MainFrame.setSize(850,450);                //set the frame to a size we can see
		MainFrame.setVisible(true);   
		about();//now display it

		//call the LBUGraphics about method to display version information.
	}
	public static void main(String[] args) {
		new Main();
	}
	@Override
	public void processCommand(String arg0) {
		
	}




}


