package plugin.doomsdaybook;

import gmgen.GMGenSystemView;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.StateChangedMessage;
import gmgen.pluginmgr.messages.TabAddMessage;
import gmgen.pluginmgr.messages.ToolMenuItemAddMessage;
import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.gui2.doomsdaybook.NameGenPanel;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * 
 */
public class RandomNamePlugin extends GMBPlugin
{
	/** Log name */
	public static final String LOG_NAME = "Random_Name_Generator";

	/** The plugin menu item in the tools menu. */
	private JMenuItem nameToolsItem = new JMenuItem();

	/** The user interface that this class will be using. */
	private NameGenPanel theView;

	/** The English name of the plugin. */
	private static final String NAME = "Random Names"; //$NON-NLS-1$
	/** Key of plugin tab. */
	private static final String IN_NAME = "in_plugin_randomname_name"; //$NON-NLS-1$
	/** Mnemonic in menu for {@link #IN_NAME} */
	private static final String IN_NAME_MN = "in_mn_plugin_randomname_name"; //$NON-NLS-1$

	/** The version number of the plugin. */
	private String version = "01.00.99.01.00";

	/**
	 * Constructor
	 */
	public RandomNamePlugin()
	{
		// Do Nothing
	}

    @Override
	public FileFilter[] getFileTypes()
	{
		return null;
	}

	/**
	 * Starts the plugin, registering itself with the <code>TabAddMessage</code>.
	 */
    @Override
	public void start()
	{
		theView = new NameGenPanel(new File(getDataDir()));
		GMBus.send(new TabAddMessage(this, getLocalizedName(), getView(), getPluginSystem()));
		initMenus();
	}

    @Override
	public String getPluginSystem()
	{
		return SettingsHandler.getGMGenOption(LOG_NAME + ".System",
			Constants.SYSTEM_GMGEN);
	}

    @Override
	public int getPluginLoadOrder()
	{
		return SettingsHandler.getGMGenOption(LOG_NAME + ".LoadOrder", 80);
	}

	/**
	 * Accessor for name
	 * @return name
	 */
    @Override
	public String getName()
	{
		return NAME;
	}
	
	private String getLocalizedName()
	{
		return LanguageBundle.getString(IN_NAME);
	}

	/**
	 * Accessor for version
	 * @return version
	 */
    @Override
	public String getVersion()
	{
		return version;
	}

	/**
	 * Gets the view that this class is using.
	 * @return the view.
	 */
	public Component getView()
	{
		return theView;
	}

	/**
	 * listens to messages from the GMGen system, and handles them as needed
	 * @param message the source of the event from the system
	 * @see GMBPlugin#handleMessage(GMBMessage)
	 */
    @Override
	public void handleMessage(GMBMessage message)
	{
		if (message instanceof StateChangedMessage)
		{
			if (isActive())
			{
				nameToolsItem.setEnabled(false);
			}
			else
			{
				nameToolsItem.setEnabled(true);
			}
		}
	}

	/**
	 * Returns true if this plugin is active
	 * @return true if this plugin is active
	 */
	public boolean isActive()
	{
		JTabbedPane tp = Utility.getTabbedPaneFor(theView);
		return tp != null && JOptionPane.getFrameForComponent(tp).isFocused()
			&& tp.getSelectedComponent().equals(theView);
	}

	/**
	 * Initialise the menus
	 */
	public void initMenus()
	{
		nameToolsItem.setMnemonic(LanguageBundle.getMnemonic(IN_NAME_MN));
		nameToolsItem.setText(getLocalizedName());
		nameToolsItem.addActionListener(new ActionListener()
		{
            @Override
			public void actionPerformed(ActionEvent evt)
			{
				toolMenuItem(evt);
			}
		});
		GMBus.send(new ToolMenuItemAddMessage(this, nameToolsItem));
	}

	/**
	 * Set the tool menu item
	 * @param evt
	 */
	public void toolMenuItem(ActionEvent evt)
	{
		JTabbedPane tp = GMGenSystemView.getTabPane();

		for (int i = 0; i < tp.getTabCount(); i++)
		{
			if (tp.getComponentAt(i) instanceof NameGenPanel)
			{
				tp.setSelectedIndex(i);
			}
		}
	}
}
