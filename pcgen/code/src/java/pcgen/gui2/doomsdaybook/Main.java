/*
 * main.java
 *
 * Created on April 25, 2003, 4:37 PM
 */
package pcgen.gui2.doomsdaybook;

import java.io.File;

/**
 *
 * @author  devon
 */
public class Main extends javax.swing.JFrame
{
	// Variables declaration - do not modify//GEN-BEGIN:variables
	private NameGenPanel nameGenPanel1;

	/** Creates new form main */
	public Main()
	{
		initComponents();
		initPrefs();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		new Main().setVisible(true);
	}

	/** Exit the Application 
	 * @param evt
	 */
	private void exitForm(java.awt.event.WindowEvent evt)
	{ //GEN-FIRST:event_exitForm
		nameGenPanel1.setExitPrefs();
		nameGenPanel1.namePrefs.putInt("WindowX", this.getX());
		nameGenPanel1.namePrefs.putInt("WindowY", this.getY());
		nameGenPanel1.namePrefs.putInt("WindowWidth", this.getSize().width);
		nameGenPanel1.namePrefs.putInt("WindowHeight", this.getSize().height);
		System.exit(0);
	}

	//GEN-LAST:event_exitForm

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents()
	{ //GEN-BEGIN:initComponents
		nameGenPanel1 =
				new pcgen.gui2.doomsdaybook.NameGenPanel(new File("./Data/"));
		setTitle("The Doomsday Book Name Generator");
		addWindowListener(new java.awt.event.WindowAdapter()
		{
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				exitForm(evt);
			}
		});
		getContentPane().add(nameGenPanel1, java.awt.BorderLayout.CENTER);
		pack();
	}

	//GEN-END:initComponents

	private void initPrefs()
	{
		int iWinX = nameGenPanel1.namePrefs.getInt("WindowX", 0);
		int iWinY = nameGenPanel1.namePrefs.getInt("WindowY", 0);
		this.setLocation(iWinX, iWinY);

		int iWinWidth = nameGenPanel1.namePrefs.getInt("WindowWidth", 700);
		int iWinHeight = nameGenPanel1.namePrefs.getInt("WindowHeight", 300);
		this.setSize(iWinWidth, iWinHeight);
		setTitle(getTitle() + " v"
			+ nameGenPanel1.namePrefs.getDouble("Version", 0) + "."
			+ nameGenPanel1.namePrefs.getDouble("SubVersion", 0));
	}

	// End of variables declaration//GEN-END:variables
}
