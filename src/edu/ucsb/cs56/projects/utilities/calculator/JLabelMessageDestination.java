package edu.ucsb.cs56.projects.utilities.calculator;
import javax.swing.JLabel;
/** 
    An interface to represent a JLabel to send messages to
 */
public class JLabelMessageDestination extends JLabel  {


    /**
       Replaces the current String on the JLabel 
       @param msg String to replace the current String
     */

	public void append(String msg){
		super.setText(msg);

	}



}