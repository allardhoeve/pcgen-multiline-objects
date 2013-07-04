/**
 * RaceInfoTab.java
 * Copyright James Dempsey, 2010
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 29/09/2010 7:16:42 PM
 *
 * $Id: RaceInfoTab.java 14578 2011-02-16 20:20:14Z cpmeister $
 */
package pcgen.gui2.tabs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.InfoFactory;
import pcgen.core.facade.RaceFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilterButton;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.models.ConcurrentDataView;
import pcgen.gui2.tabs.models.QualifiedTreeCellRenderer;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.SortMode;
import pcgen.gui2.util.SortingPriority;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.system.LanguageBundle;

/**
 * The Class <code>RaceInfoTab</code> is the component used in the Race tab.
 * <br/>
 * Last Editor: $Author: cpmeister $
 * Last Edited: $Date: 2011-02-16 12:20:14 -0800 (Wed, 16 Feb 2011) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 14578 $
 */
public class RaceInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

	private static final TabTitle title = new TabTitle(LanguageBundle.getString("in_races")); //$NON-NLS-1$
	private final FilteredTreeViewTable<Object, RaceFacade> raceTable;
	private final FilteredTreeViewTable<Object, RaceFacade> selectedTable;
	private final InfoPane infoPane;
	private final JButton selectRaceButton;
	private final JButton removeButton;
	private final FilterButton<Object, RaceFacade> qFilterButton;
	private final FilterButton<Object, RaceFacade> noRacialHdFilterButton;

	public RaceInfoTab()
	{
		super("Race");
		this.raceTable = new FilteredTreeViewTable<Object, RaceFacade>();
		this.selectedTable = new FilteredTreeViewTable<Object, RaceFacade>();
		this.infoPane = new InfoPane(LanguageBundle.getString("in_irRaceInfo")); //$NON-NLS-1$
		this.selectRaceButton = new JButton();
		this.removeButton = new JButton();
		this.qFilterButton = new FilterButton<Object, RaceFacade>("RaceQualified");
		this.noRacialHdFilterButton = new FilterButton<Object, RaceFacade>("RaceNoHD");
		initComponents();
	}

	private void initComponents()
	{
		FlippingSplitPane topPane = new FlippingSplitPane("RaceTop");
		setTopComponent(topPane);
		setOrientation(VERTICAL_SPLIT);

		JPanel availPanel = new JPanel(new BorderLayout());
		FilterBar<Object, RaceFacade> bar = new FilterBar<Object, RaceFacade>();
		bar.addDisplayableFilter(new SearchFilterPanel());
		noRacialHdFilterButton.setText(LanguageBundle.getString("in_irNoRacialHd")); //$NON-NLS-1$
		noRacialHdFilterButton.setToolTipText(LanguageBundle.getString("in_irNoRacialHdTip")); //$NON-NLS-1$
		bar.addDisplayableFilter(noRacialHdFilterButton);
		qFilterButton.setText(LanguageBundle.getString("in_igQualFilter")); //$NON-NLS-1$
		bar.addDisplayableFilter(qFilterButton);
		raceTable.setDisplayableFilter(bar);
		availPanel.add(bar, BorderLayout.NORTH);

		raceTable.setSortingPriority(Collections.singletonList(new SortingPriority(0, SortMode.ASCENDING)));
		raceTable.sortModel();
		availPanel.add(new JScrollPane(raceTable), BorderLayout.CENTER);

		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		selectRaceButton.setHorizontalTextPosition(SwingConstants.LEADING);

		box.add(selectRaceButton);
		box.add(Box.createHorizontalStrut(5));
		box.setBorder(new EmptyBorder(0, 0, 5, 0));
		availPanel.add(box, BorderLayout.SOUTH);

		topPane.setLeftComponent(availPanel);

		JPanel selPanel = new JPanel(new BorderLayout());
		FilterBar<Object, RaceFacade> filterBar = new FilterBar<Object, RaceFacade>();
		filterBar.addDisplayableFilter(new SearchFilterPanel());

		selectedTable.setDisplayableFilter(filterBar);
		selectedTable.setSortingPriority(Collections.singletonList(new SortingPriority(0, SortMode.ASCENDING)));
		selectedTable.sortModel();
		JScrollPane scrollPane = new JScrollPane(selectedTable);
		selPanel.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setPreferredSize(new Dimension(0, 0));

		box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(5));
		box.add(removeButton);
		box.add(Box.createHorizontalGlue());
		box.setBorder(new EmptyBorder(0, 0, 5, 0));
		selPanel.add(box, BorderLayout.SOUTH);

		topPane.setRightComponent(selPanel);
		topPane.setResizeWeight(.75);

		setBottomComponent(infoPane);
		setResizeWeight(.75);
	}

	private enum Models
	{

		AvailableViewModel,
		SelectedViewModel,
		AvailableDataModel,
		SelectedDataModel
	}

	@Override
	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put(SelectRaceAction.class, new SelectRaceAction(character));
		state.put(RemoveRaceAction.class, new RemoveRaceAction(character));
		RaceDataView availDataView = new RaceDataView(character, true);
		RaceDataView selDataView = new RaceDataView(character, false);
		state.put(Models.AvailableDataModel, availDataView);
		state.put(Models.SelectedDataModel, selDataView);
		state.put(Models.AvailableViewModel, new RaceTreeViewModel(character, true, availDataView));
		state.put(Models.SelectedViewModel, new RaceTreeViewModel(character, false, selDataView));
		state.put(InfoHandler.class, new InfoHandler(character));
		state.put(QualifiedTreeCellRenderer.class, new QualifiedTreeCellRenderer(character));
		state.put(QualifiedFilterHandler.class, new QualifiedFilterHandler(character));
		state.put(NoRacialHdFilterHandler.class, new NoRacialHdFilterHandler(character));
		return state;
	}

	@Override
	public void restoreModels(Hashtable<?, ?> state)
	{
		((QualifiedFilterHandler) state.get(QualifiedFilterHandler.class)).install();
		((NoRacialHdFilterHandler) state.get(NoRacialHdFilterHandler.class)).install();
		raceTable.setTreeViewModel((RaceTreeViewModel) state.get(Models.AvailableViewModel));
		selectedTable.setTreeViewModel((RaceTreeViewModel) state.get(Models.SelectedViewModel));
		((RaceDataView) state.get(Models.AvailableDataModel)).install();
		((RaceDataView) state.get(Models.SelectedDataModel)).install();
		((InfoHandler) state.get(InfoHandler.class)).install();
		((SelectRaceAction) state.get(SelectRaceAction.class)).install();
		((RemoveRaceAction) state.get(RemoveRaceAction.class)).install();
		
		raceTable.setTreeCellRenderer((QualifiedTreeCellRenderer) state.get(QualifiedTreeCellRenderer.class));
		selectedTable.setTreeCellRenderer((QualifiedTreeCellRenderer) state.get(QualifiedTreeCellRenderer.class));
		selectRaceButton.setAction((SelectRaceAction) state.get(SelectRaceAction.class));
		removeButton.setAction((RemoveRaceAction) state.get(RemoveRaceAction.class));
	}

	@Override
	public void storeModels(Hashtable<Object, Object> state)
	{
		((RaceDataView) state.get(Models.AvailableDataModel)).uninstall();
		((RaceDataView) state.get(Models.SelectedDataModel)).uninstall();
		((InfoHandler) state.get(InfoHandler.class)).uninstall();
		((SelectRaceAction) state.get(SelectRaceAction.class)).uninstall();
		((RemoveRaceAction) state.get(RemoveRaceAction.class)).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return title;
	}

	private class InfoHandler implements ListSelectionListener
	{

		private final CharacterFacade character;
		private String text;

		public InfoHandler(CharacterFacade character)
		{
			this.character = character;
			this.text = ""; //$NON-NLS-1$
		}

		public void install()
		{
			raceTable.getSelectionModel().addListSelectionListener(this);
			selectedTable.getSelectionModel().addListSelectionListener(this);
			infoPane.setText(text);
		}

		public void uninstall()
		{
			raceTable.getSelectionModel().removeListSelectionListener(this);
			selectedTable.getSelectionModel().removeListSelectionListener(this);
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				Object obj = null;
				if (e.getSource() == raceTable.getSelectionModel())
				{
					int selectedRow = raceTable.getSelectedRow();
					if (selectedRow != -1)
					{
						obj = raceTable.getModel().getValueAt(selectedRow, 0);
					}
				}
				else
				{
					int selectedRow = selectedTable.getSelectedRow();
					if (selectedRow != -1)
					{
						obj = selectedTable.getModel().getValueAt(selectedRow, 0);
					}
				}
				if (obj instanceof RaceFacade)
				{
					text = character.getInfoFactory().getHTMLInfo((RaceFacade) obj);
					infoPane.setText(text);
				}
			}
		}

	}

//	private static class RaceLabelHandler implements ReferenceListener<RaceFacade>
//	{
//
//		private JLabel label;
//		private ReferenceFacade<RaceFacade> ref;
//
//		public RaceLabelHandler(CharacterFacade character, JLabel label)
//		{
//			ref = character.getRaceRef();
//			this.label = label;
//		}
//
//		public void install()
//		{
//			if (ref.getReference() != null)
//			{
//				label.setText(ref.getReference().toString());
//			}
//			ref.addReferenceListener(this);
//		}
//
//		public void uninstall()
//		{
//			ref.removeReferenceListener(this);
//		}
//
//		@Override
//		public void referenceChanged(ReferenceEvent<RaceFacade> e)
//		{
//			label.setText(e.getNewReference().toString());
//		}
//
//	}
	private class SelectRaceAction extends AbstractAction
	{

		private final CharacterFacade character;

		public SelectRaceAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_irSelectRace")); //$NON-NLS-1$
			this.character = character;
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object obj = raceTable.getSelectedObject();
			if (obj instanceof RaceFacade)
			{
				character.setRace((RaceFacade) obj);
			}
		}

		public void install()
		{
			raceTable.addActionListener(this);
		}

		public void uninstall()
		{
			raceTable.removeActionListener(this);
		}

	}

	private class RemoveRaceAction extends AbstractAction
	{

		private final CharacterFacade character;

		public RemoveRaceAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_irUnselectRace")); //$NON-NLS-1$
			this.character = character;
			putValue(SMALL_ICON, Icons.Back16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.setRace(null);
		}

		public void install()
		{
			selectedTable.addActionListener(this);
		}

		public void uninstall()
		{
			selectedTable.removeActionListener(this);
		}

	}

	/**
	 * The Class <code>NoRacialHdFilterHandler</code> provides the filter backing the 
	 * No Racial HD filter button.
	 */
	private class NoRacialHdFilterHandler
	{

		private final Filter<Object, RaceFacade> noRacialHdFilter = new Filter<Object, RaceFacade>()
		{

			@Override
			public boolean accept(Object context, RaceFacade element)
			{
				return infoFactory.getNumMonsterClassLevels(element) == 0;
			}

		};
		private final InfoFactory infoFactory;

		public NoRacialHdFilterHandler(CharacterFacade character)
		{
			this.infoFactory = character.getInfoFactory();
		}

		public void install()
		{
			noRacialHdFilterButton.setFilter(noRacialHdFilter);
		}

	}

	private class QualifiedFilterHandler
	{

		private final Filter<Object, RaceFacade> qFilter = new Filter<Object, RaceFacade>()
		{

			@Override
			public boolean accept(Object context, RaceFacade element)
			{
				return character.isQualifiedFor(element);
			}

		};
		private final CharacterFacade character;

		public QualifiedFilterHandler(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			qFilterButton.setFilter(qFilter);
		}

	}

	private class RaceDataView extends ConcurrentDataView<RaceFacade>
	{

		private final List<DefaultDataViewColumn> columns;
		private final InfoFactory infoFactory;
		private final boolean isAvailModel;

		public RaceDataView(CharacterFacade character, boolean isAvailModel)
		{
			this.infoFactory = character.getInfoFactory();
			this.isAvailModel = isAvailModel;
			if (isAvailModel)
			{
				columns = Arrays.asList(new DefaultDataViewColumn("in_irTableStat", String.class, true), //$NON-NLS-1$
										new DefaultDataViewColumn("in_preReqs", String.class), //$NON-NLS-1$
										new DefaultDataViewColumn("in_size", String.class, true), //$NON-NLS-1$
										new DefaultDataViewColumn("in_movement", String.class, true), //$NON-NLS-1$
										new DefaultDataViewColumn("in_vision", String.class), //$NON-NLS-1$
										new DefaultDataViewColumn("in_favoredClass", String.class, true), //$NON-NLS-1$
										new DefaultDataViewColumn("in_lvlAdj", String.class, true), //$NON-NLS-1$
										new DefaultDataViewColumn("in_source", String.class, false)); //$NON-NLS-1$
			}
			else
			{
				columns = Arrays.asList(new DefaultDataViewColumn("in_irTableStat", String.class, false), //$NON-NLS-1$
										new DefaultDataViewColumn("in_preReqs", String.class, false), //$NON-NLS-1$
										new DefaultDataViewColumn("in_size", String.class, false), //$NON-NLS-1$
										new DefaultDataViewColumn("in_movement", String.class, false), //$NON-NLS-1$
										new DefaultDataViewColumn("in_vision", String.class, false), //$NON-NLS-1$
										new DefaultDataViewColumn("in_favoredClass", String.class, false), //$NON-NLS-1$
										new DefaultDataViewColumn("in_lvlAdj", String.class, false), //$NON-NLS-1$
										new DefaultDataViewColumn("in_source", String.class, false)); //$NON-NLS-1$
			}
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return columns;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPrefsKey()
		{
			return isAvailModel ? "RaceTreeAvail" : "RaceTreeSelected";  //$NON-NLS-1$//$NON-NLS-2$
		}

		@Override
		protected List<?> getDataList(RaceFacade obj)
		{
			return Arrays.asList(infoFactory.getStatAdjustments(obj),
								 infoFactory.getPreReqHTML(obj),
								 obj.getSize(),
								 infoFactory.getMovement(obj),
								 infoFactory.getVision(obj),
								 infoFactory.getFavoredClass(obj),
								 infoFactory.getLevelAdjustment(obj),
								 obj.getSource());
		}

		@Override
		protected void refreshTableData()
		{
			if (isAvailModel)
			{
				raceTable.refreshModelData();
			}
			else
			{
				selectedTable.refreshModelData();
			}
		}

	}

	private static class RaceTreeViewModel implements TreeViewModel<RaceFacade>
	{

		private static final DefaultListFacade<? extends TreeView<RaceFacade>> treeViews =
				new DefaultListFacade<TreeView<RaceFacade>>(Arrays.asList(RaceTreeView.values()));
		private final CharacterFacade character;
		private final boolean isAvailModel;
		private final DataView<RaceFacade> dataView;

		public RaceTreeViewModel(CharacterFacade character, boolean isAvailModel, DataView<RaceFacade> dataView)
		{
			this.character = character;
			this.isAvailModel = isAvailModel;
			this.dataView = dataView;
		}

		@Override
		public ListFacade<? extends TreeView<RaceFacade>> getTreeViews()
		{
			return treeViews;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		@Override
		public DataView<RaceFacade> getDataView()
		{
			return dataView;
		}

		@Override
		public ListFacade<RaceFacade> getDataModel()
		{
			if (isAvailModel)
			{
				return character.getDataSet().getRaces();
			}
			else
			{
				return character.getRaceAsList();
			}
		}

	}

	private enum RaceTreeView implements TreeView<RaceFacade>
	{

		NAME(LanguageBundle.getString("in_nameLabel")), //$NON-NLS-1$
		TYPE_NAME(LanguageBundle.getString("in_typeName")), //$NON-NLS-1$
		RACETYPE_NAME(LanguageBundle.getString("in_racetypeName")), //$NON-NLS-1$
		RACETYPE_RACE_SUBTYPE_NAME(
		LanguageBundle.getString("in_racetypeSubtypeName")), //$NON-NLS-1$
		SOURCE_NAME(LanguageBundle.getString("in_sourceName")); //$NON-NLS-1$
		private String name;

		private RaceTreeView(String name)
		{
			this.name = name;
		}

		@Override
		public String getViewName()
		{
			return name;
		}

		@Override
		public List<TreeViewPath<RaceFacade>> getPaths(RaceFacade pobj)
		{
			switch (this)
			{
				case NAME:
					return Collections.singletonList(new TreeViewPath<RaceFacade>(pobj));
				case TYPE_NAME:
					return Collections.singletonList(new TreeViewPath<RaceFacade>(pobj,
																				  pobj.getType()));
				case RACETYPE_RACE_SUBTYPE_NAME:
					List<String> subtypes = pobj.getRaceSubTypes();
					if (!subtypes.isEmpty())
					{
						List<TreeViewPath<RaceFacade>> paths =
								new ArrayList<TreeViewPath<RaceFacade>>();
						String raceType = pobj.getRaceType();
						for (String subtype : subtypes)
						{
							paths.add(new TreeViewPath<RaceFacade>(pobj,
																   raceType, subtype));
						}
						return paths;
					}
					// No subtypes, fall through to treat it as a type tree.
					return Collections.singletonList(new TreeViewPath<RaceFacade>(pobj,
																				  pobj.getRaceType()));
				case RACETYPE_NAME:
					return Collections.singletonList(new TreeViewPath<RaceFacade>(pobj,
																				  pobj.getRaceType()));
				case SOURCE_NAME:
					return Collections.singletonList(new TreeViewPath<RaceFacade>(pobj,
																				  pobj.getSourceForNodeDisplay()));
				default:
					throw new InternalError();
			}
		}

	}

}
