/*
 * RagePanel.java
 *
 * Created on February 9, 2004, 2:01 PM
 */

package plugin.charactersheet.gui;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JCheckBox;

import pcgen.core.PlayerCharacter;
import pcgen.gui2.util.FontManipulation;

/**
 * Confirmed no memory Leaks Dec 10, 2004
 * @author  ddjone3
 */
public class RagePane extends javax.swing.JPanel
{
	private PlayerCharacter pc;
	private List<JCheckBox> checkList = new ArrayList<JCheckBox>();
	private static final String rageText1 =
			"The Barbarian temporarily gains +4 to Strength, +4 to Constitution, and a +2 morale bonus on Will saves, but suffers a -2 penalty to AC.";
	private static final String rageText2 =
			"The Barbarian temporarily gains +6 to Strength, +6 to Constitution, and a +3 morale bonus on Will saves, but suffers a -2 penalty to AC.";
	private static final String rageText3 =
			"The Barbarian temporarily gains +8 to Strength, +8 to Constitution, and a +4 morale bonus on Will saves, but suffers a -2 penalty to AC.";
	private static final String rageTextLength =
			"A fit of rage lasts for a number of rounds equal to 3 + the character's (newly improved) Constitution modifier. ";
	private static final String rageTextFatigue =
			"At the end of the rage, the barbarian is fatigued (-2 to Strength, -2 to Dexterity, can't charge or run) for the duration of that encounter";
	private static final String rageTextFrequency =
			"The barbarian can only fly into a rage once per encounter, and only a certain number of times per day (determined by level). Entering a rage takes no time itself, but the barbarian can only do it during his action.";
	private static final String BARBARIAN_RAGE = "BARBARIAN RAGE";
	private static final String PER_DAY = "Uses Per Day";
	private static final String RAGE_TIMES = "RageTimes";
	private static final String GREATER_RAGE = "GreaterRage";
	private static final String TIRELESS_RAGE = "TirelessRage";
	private static final String MIGHTY_RAGE = "MightyRage";
	private static final String SPACE = " ";
	private static final String PROPERTY_RAGEPANE = "cs.RagePane";
	private Properties pcProperties;
	private boolean updateProperties = false;

	/** Creates new form RagePanel */
	public RagePane()
	{
		initComponents();
		setLocalColor();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents()
	{//GEN-BEGIN:initComponents
		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jPanel3 = new javax.swing.JPanel();
		jPanel2 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		checkPanel = new javax.swing.JPanel();
		rageText = new javax.swing.JTextArea();

		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

		jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER,
			1, 0));

		FontManipulation.size140(jLabel1);
		jLabel1.setText(BARBARIAN_RAGE);
		jPanel1.add(jLabel1);

		add(jPanel1);

		jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3,
			javax.swing.BoxLayout.X_AXIS));

		jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER,
			1, 0));

		jLabel2.setText(PER_DAY);
		jPanel2.add(jLabel2);

		jPanel3.add(jPanel2);

		checkPanel.setLayout(new java.awt.FlowLayout(
			java.awt.FlowLayout.CENTER, 0, 1));

		jPanel3.add(checkPanel);

		add(jPanel3);

		rageText.setLineWrap(true);
		rageText.setWrapStyleWord(true);
		add(rageText);

	}//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel checkPanel;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JTextArea rageText;

	// End of variables declaration//GEN-END:variables

	public void setColor()
	{
		setLocalColor();
		refresh();
	}

	public void setLocalColor()
	{
		jPanel1.setBackground(CharacterPanel.header);
		jPanel1.setBorder(new javax.swing.border.LineBorder(
			CharacterPanel.border));
		jPanel3.setBackground(CharacterPanel.header);
		jPanel2.setBackground(CharacterPanel.header);
		jPanel2.setBorder(new javax.swing.border.LineBorder(
			CharacterPanel.header));
		checkPanel.setBackground(CharacterPanel.white);
		checkPanel.setBorder(new javax.swing.border.LineBorder(
			CharacterPanel.header));
		rageText.setBorder(new javax.swing.border.LineBorder(
			CharacterPanel.header));
	}

	public void setPc(PlayerCharacter pc, Properties pcProperties)
	{
		this.pc = pc;
		this.pcProperties = pcProperties;
	}

	public void refresh()
	{
		int numDay = pc.getVariable(RAGE_TIMES, true).intValue();
		if (numDay > 0)
		{
			setVisible(true);

			int greaterRage = pc.getVariable(GREATER_RAGE, true).intValue();
			int tirelessRage = pc.getVariable(TIRELESS_RAGE, true).intValue();
			int mightyRage = pc.getVariable(MIGHTY_RAGE, true).intValue();

			if (mightyRage == 1)
			{
				rageText
					.setText(rageText3 + rageTextLength + rageTextFrequency);
			}
			else if (tirelessRage == 1)
			{
				rageText
					.setText(rageText2 + rageTextLength + rageTextFrequency);
			}
			else if (greaterRage == 1)
			{
				rageText.setText(rageText2 + rageTextLength + rageTextFatigue
					+ rageTextFrequency);
			}
			else
			{
				rageText.setText(rageText1 + rageTextLength + rageTextFatigue
					+ rageTextFrequency);
			}
			addCheckBoxes(numDay);
		}
		else
		{
			setVisible(false);
		}
		updatePane();
	}

	private void addCheckBoxes(int numDay)
	{
		if (checkList.size() != numDay)
		{
			checkList.clear();
			for (int i = 0; i < numDay; i++)
			{
				if (i % 5 == 0 && i != 0)
				{
					javax.swing.JLabel bufLabel = new javax.swing.JLabel();
					bufLabel.setText(SPACE);
					checkPanel.add(bufLabel);
				}
				JCheckBox checkBox = new JCheckBox();
				checkBox.setBackground(CharacterPanel.white);
				checkBox.setBorder(null);
				checkList.add(checkBox);
				checkPanel.add(checkBox);
				checkBox.addActionListener(new java.awt.event.ActionListener()
				{
                    @Override
					public void actionPerformed(java.awt.event.ActionEvent evt)
					{
						pc.setDirty(true);
						updateProperties();
					}
				});
			}
		}
	}

	public void updateProperties()
	{
		if (updateProperties)
		{
			int counter = 0;
			for (JCheckBox checkBox : checkList)
			{
				if (checkBox.isSelected())
				{
					counter++;
				}
			}
			pcProperties.put(PROPERTY_RAGEPANE, Integer.valueOf(counter));
		}
	}

	public void updatePane()
	{
		try
		{
			int counter =
					Integer.parseInt((String) pcProperties
						.get(PROPERTY_RAGEPANE));
			for (JCheckBox checkBox : checkList)
			{
				if (counter > 0)
				{
					checkBox.setSelected(true);
					counter--;
				}
				else
				{
					checkBox.setSelected(false);
				}
			}
			updateProperties = true;
		}
		catch (NumberFormatException e)
		{
			//Do nothing
		}
		catch (ClassCastException ce)
		{
			//Do Nothing
		}
	}

	public void destruct()
	{
		//Put any code here that is needed to prevent memory leaks when this panel is destroyed
	}
}
