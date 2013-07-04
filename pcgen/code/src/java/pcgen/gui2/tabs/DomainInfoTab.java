/*
 * DomainInfoTab.java
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Aug 8, 2010, 4:29:55 PM
 */
package pcgen.gui2.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.DeityFacade;
import pcgen.core.facade.DomainFacade;
import pcgen.core.facade.InfoFacade;
import pcgen.core.facade.InfoFactory;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.util.ListFacades;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.filter.DisplayableFilter;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilterButton;
import pcgen.gui2.filter.FilterHandler;
import pcgen.gui2.filter.FilteredListFacadeTableModel;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.models.QualifiedTreeCellRenderer;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.tools.PrefTableColumnModel;
import pcgen.gui2.util.JDynamicTable;
import pcgen.gui2.util.table.DynamicTableColumnModel;
import pcgen.gui2.util.table.TableUtils;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.system.LanguageBundle;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DomainInfoTab extends FlippingSplitPane implements CharacterInfoTab, TodoHandler
{

	private final FilteredTreeViewTable<Object, DeityFacade> deityTable;
	private final JDynamicTable domainTable;
	private final JTable domainRowHeaderTable;
	private final JLabel selectedDeity;
	private final JButton selectDeity;
	private final JLabel selectedDomain;
	private final InfoPane deityInfo;
	private final InfoPane domainInfo;
	private DisplayableFilter<CharacterFacade, DomainFacade> domainFilter;
	private static final Object COLUMN_ID = new Object();
	private final FilterButton<Object, DomainFacade> qFilterButton;

	public DomainInfoTab()
	{
		super("Domain");
		this.deityTable = new FilteredTreeViewTable<Object, DeityFacade>();
		this.domainTable = new JDynamicTable();
		this.domainRowHeaderTable = TableUtils.createDefaultTable();
		this.selectedDeity = new JLabel();
		this.selectDeity = new JButton();
		this.selectedDomain = new JLabel();
		this.deityInfo = new InfoPane("in_deityInfo"); //$NON-NLS-1$
		this.domainInfo = new InfoPane("in_domainInfo"); //$NON-NLS-1$
		this.qFilterButton = new FilterButton<Object, DomainFacade>("DomainQualified");
		initComponents();
	}

	private void initComponents()
	{
		setOrientation(VERTICAL_SPLIT);

		JPanel panel = new JPanel(new BorderLayout());
		FilterBar<Object, DeityFacade> bar = new FilterBar<Object, DeityFacade>();
		bar.addDisplayableFilter(new SearchFilterPanel());
		deityTable.setDisplayableFilter(bar);
		panel.add(bar, BorderLayout.NORTH);

		ListSelectionModel selectionModel = deityTable.getSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel.add(new JScrollPane(deityTable), BorderLayout.CENTER);

		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel(LanguageBundle.getString("in_domDeityLabel"))); //$NON-NLS-1$
		box.add(Box.createHorizontalStrut(5));
		box.add(selectedDeity);
		box.add(Box.createHorizontalStrut(5));
		box.add(selectDeity);
		box.add(Box.createHorizontalGlue());
		panel.add(box, BorderLayout.SOUTH);

		FlippingSplitPane splitPane = new FlippingSplitPane("DomainTop");
		splitPane.setLeftComponent(panel);

		panel = new JPanel(new BorderLayout());
		FilterBar<CharacterFacade, DomainFacade> dbar = new FilterBar<CharacterFacade, DomainFacade>();
		dbar.addDisplayableFilter(new SearchFilterPanel());
		qFilterButton.setText(LanguageBundle.getString("in_igQualFilter")); //$NON-NLS-1$
		dbar.addDisplayableFilter(qFilterButton);
		domainFilter = dbar;
		panel.add(dbar, BorderLayout.NORTH);

		selectionModel = domainTable.getSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		domainTable.setAutoCreateColumnsFromModel(false);
		domainTable.setColumnModel(createDomainColumnModel());
		JScrollPane scrollPane = TableUtils.createCheckBoxSelectionPane(domainTable, domainRowHeaderTable);
		panel.add(scrollPane, BorderLayout.CENTER);

		box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel(LanguageBundle.getString("in_domRemainDomLabel"))); //$NON-NLS-1$
		box.add(Box.createHorizontalStrut(5));
		box.add(selectedDomain);
		box.add(Box.createHorizontalGlue());

		panel.add(box, BorderLayout.SOUTH);

		splitPane.setRightComponent(panel);
		setTopComponent(splitPane);
		splitPane = new FlippingSplitPane("DomainBottom");
		splitPane.setLeftComponent(deityInfo);
		splitPane.setRightComponent(domainInfo);
		setBottomComponent(splitPane);
		setResizeWeight(.65);
	}

	public DynamicTableColumnModel createDomainColumnModel()
	{
		PrefTableColumnModel model = new PrefTableColumnModel("DomainList", 1);
		TableColumn column = new TableColumn(0);
		column.setHeaderValue(LanguageBundle.getString("in_domains")); //$NON-NLS-1$
		model.addColumn(column, true, 150);
		column = new TableColumn(1);
		column.setHeaderValue(LanguageBundle.getString("in_source")); //$NON-NLS-1$
		model.addColumn(column, true, 150);
		return model;
	}

	@Override
	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put(DeityTreeViewModel.class, new DeityTreeViewModel(character));
		state.put(DomainTableHandler.class, new DomainTableHandler(character));
		state.put(SelectDeityAction.class, new SelectDeityAction(character));
		state.put(DeityLabelHandler.class, new DeityLabelHandler(character, selectedDeity));
		state.put(DomainLabelHandler.class, new DomainLabelHandler(character, selectedDomain));
		state.put(DeityInfoHandler.class, new DeityInfoHandler(character));
		state.put(DomainInfoHandler.class, new DomainInfoHandler(character));
		state.put(DomainRenderer.class, new DomainRenderer(character));
		state.put(QualifiedTreeCellRenderer.class, new QualifiedTreeCellRenderer(character));
		state.put(QualifiedFilterHandler.class, new QualifiedFilterHandler(character));
		return state;
	}

	@Override
	public void restoreModels(Hashtable<?, ?> state)
	{
		((DomainLabelHandler) state.get(DomainLabelHandler.class)).install();
		((DeityLabelHandler) state.get(DeityLabelHandler.class)).install();
		((DomainTableHandler) state.get(DomainTableHandler.class)).install();
		((DomainInfoHandler) state.get(DomainInfoHandler.class)).install();
		((DeityInfoHandler) state.get(DeityInfoHandler.class)).install();
		((DomainRenderer) state.get(DomainRenderer.class)).install();
		((SelectDeityAction) state.get(SelectDeityAction.class)).install();
		((QualifiedFilterHandler) state.get(QualifiedFilterHandler.class)).install();

		deityTable.setTreeViewModel((DeityTreeViewModel) state.get(DeityTreeViewModel.class));
		deityTable.setTreeCellRenderer((QualifiedTreeCellRenderer) state.get(QualifiedTreeCellRenderer.class));
		selectDeity.setAction((SelectDeityAction) state.get(SelectDeityAction.class));
	}

	@Override
	public void storeModels(Hashtable<Object, Object> state)
	{
		((DomainLabelHandler) state.get(DomainLabelHandler.class)).uninstall();
		((DeityLabelHandler) state.get(DeityLabelHandler.class)).uninstall();
		((DomainTableHandler) state.get(DomainTableHandler.class)).uninstall();
		((DomainInfoHandler) state.get(DomainInfoHandler.class)).uninstall();
		((DeityInfoHandler) state.get(DeityInfoHandler.class)).uninstall();
		((SelectDeityAction) state.get(SelectDeityAction.class)).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return new TabTitle("in_domains"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void adviseTodo(String fieldName)
	{
		if ("Domains".equals(fieldName)) //$NON-NLS-1$
		{
			if (domainTable.getRowCount() > 0)
			{
				domainTable.requestFocusInWindow();
				domainTable.getSelectionModel().setSelectionInterval(0, 0);
				deityTable.getSelectionModel().clearSelection();
			}
			else if (deityTable.getRowCount() > 0)
			{
				deityTable.requestFocusInWindow();
				deityTable.getSelectionModel().setSelectionInterval(0, 0);
			}
		}
	}

	private class DomainRenderer extends DefaultTableCellRenderer
	{

		private CharacterFacade character;

		public DomainRenderer(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			domainTable.setDefaultRenderer(Object.class, this);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value instanceof DomainFacade && !character.isQualifiedFor((DomainFacade) value))
			{
				setForeground(UIPropertyContext.getNotQualifiedColor());
			}
			else if (!isSelected)
			{
				setForeground(UIPropertyContext.getQualifiedColor());
			}
			if (value instanceof InfoFacade && ((InfoFacade) value).isNamePI())
			{
				setFont(getFont().deriveFont(Font.BOLD + Font.ITALIC));
			}
			else
			{
				setFont(getFont().deriveFont(Font.PLAIN));
			}
			return this;
		}

	}

	private class DeityInfoHandler implements ListSelectionListener
	{

		private CharacterFacade character;
		private String text;

		public DeityInfoHandler(CharacterFacade character)
		{
			this.character = character;
			this.text = ""; //$NON-NLS-1$
		}

		public void install()
		{
			deityTable.getSelectionModel().addListSelectionListener(this);
			deityInfo.setText(text);
		}

		public void uninstall()
		{
			deityTable.getSelectionModel().removeListSelectionListener(this);
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				int selectedRow = deityTable.getSelectedRow();
				if (selectedRow != -1)
				{
					Object obj = deityTable.getModel().getValueAt(selectedRow, 0);
					if (obj instanceof DeityFacade)
					{
						text = character.getInfoFactory().getHTMLInfo((DeityFacade) obj);
						deityInfo.setText(text);
					}
				}
			}
		}

	}

	private class DomainInfoHandler implements ListSelectionListener
	{

		private CharacterFacade character;
		private String text;

		public DomainInfoHandler(CharacterFacade character)
		{
			this.character = character;
			this.text = ""; //$NON-NLS-1$
		}

		public void install()
		{
			domainTable.getSelectionModel().addListSelectionListener(this);
			domainInfo.setText(text);
		}

		public void uninstall()
		{
			domainTable.getSelectionModel().removeListSelectionListener(this);
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				int selectedRow = domainTable.getSelectedRow();
				DomainFacade domain = null;
				if (selectedRow != -1)
				{
					domain = (DomainFacade) domainTable.getModel().getValueAt(selectedRow, 0);
				}
				if (domain != null)
				{
					text = character.getInfoFactory().getHTMLInfo(domain);
					domainInfo.setText(text);
				}
			}
		}

	}

	private class SelectDeityAction extends AbstractAction
	{

		private CharacterFacade character;

		public SelectDeityAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_select")); //$NON-NLS-1$
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int selectedRow = deityTable.getSelectedRow();
			if (selectedRow != -1)
			{
				Object rowObj = deityTable.getModel().getValueAt(selectedRow, 0);
				if (rowObj instanceof DeityFacade)
				{
					DeityFacade deity = (DeityFacade) rowObj;
					character.setDeity(deity);
				}
			}
		}

		public void install()
		{
			deityTable.addActionListener(this);
		}

		public void uninstall()
		{
			deityTable.removeActionListener(this);
		}

	}

	private class QualifiedFilterHandler
	{

		private final Filter<Object, DomainFacade> qFilter = new Filter<Object, DomainFacade>()
		{
			@Override
			public boolean accept(Object context, DomainFacade element)
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

	private class DomainTableHandler implements FilterHandler
	{

		private DomainTableModel tableModel;

		public DomainTableHandler(CharacterFacade character)
		{
			tableModel = new DomainTableModel(character);
		}

		public void install()
		{
			domainFilter.setFilterHandler(this);
			tableModel.setFilter(domainFilter);
			domainTable.setModel(tableModel);
			domainRowHeaderTable.setModel(tableModel);
		}

		public void uninstall()
		{
			tableModel.setFilter(null);
		}

		@Override
		public void refilter()
		{
			tableModel.refilter();
		}

		@Override
		public void scrollToTop()
		{
			// do nothing
		}

		@Override
		public void setSearchEnabled(boolean enable)
		{
		}

	}

	private static class DomainLabelHandler implements ReferenceListener<Integer>
	{

		private JLabel label;
		private ReferenceFacade<Integer> ref;

		public DomainLabelHandler(CharacterFacade character, JLabel label)
		{
			ref = character.getRemainingDomainSelectionsRef();
			this.label = label;
		}

		public void install()
		{
			if (ref.getReference() != null)
			{
				label.setText(ref.getReference().toString());
			}
			ref.addReferenceListener(this);
		}

		public void uninstall()
		{
			ref.removeReferenceListener(this);
		}

		@Override
		public void referenceChanged(ReferenceEvent<Integer> e)
		{
			label.setText(e.getNewReference().toString());
		}

	}

	private static class DeityLabelHandler implements ReferenceListener<DeityFacade>
	{

		private JLabel label;
		private ReferenceFacade<DeityFacade> ref;

		public DeityLabelHandler(CharacterFacade character, JLabel label)
		{
			ref = character.getDeityRef();
			this.label = label;
		}

		public void install()
		{
			label.setFont(label.getFont().deriveFont(Font.PLAIN));
			if (ref.getReference() != null)
			{
				label.setText(ref.getReference().toString());
				if (ref.getReference().isNamePI())
				{
					label.setFont(label.getFont().deriveFont(Font.BOLD + Font.ITALIC));
				}
			}
			else
			{
				label.setText(""); //$NON-NLS-1$
			}
			ref.addReferenceListener(this);
		}

		public void uninstall()
		{
			ref.removeReferenceListener(this);
		}

		@Override
		public void referenceChanged(ReferenceEvent<DeityFacade> e)
		{
			label.setText(e.getNewReference().toString());
		}

	}

	private static class DomainTableModel extends FilteredListFacadeTableModel<DomainFacade>
	{

		private final ListListener<DomainFacade> listListener = new ListListener<DomainFacade>()
		{
			@Override
			public void elementAdded(ListEvent<DomainFacade> e)
			{
				int index = ListFacades.wrap(sortedList).indexOf(e.getElement());
				DomainTableModel.this.fireTableCellUpdated(index, -1);
			}

			@Override
			public void elementRemoved(ListEvent<DomainFacade> e)
			{
				int index = ListFacades.wrap(sortedList).indexOf(e.getElement());
				DomainTableModel.this.fireTableCellUpdated(index, -1);
			}

			@Override
			public void elementsChanged(ListEvent<DomainFacade> e)
			{
				DomainTableModel.this.fireTableRowsUpdated(0, sortedList.getSize() - 1);
			}

			@Override
			public void elementModified(ListEvent<DomainFacade> e)
			{
			}

		};

		public DomainTableModel(CharacterFacade character)
		{
			super(character);
			setDelegate(character.getAvailableDomains());
			character.getDomains().addListListener(listListener);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			if (columnIndex == -1)
			{
				return Boolean.class;
			}
			return super.getColumnClass(columnIndex);
		}

		@Override
		protected Object getValueAt(DomainFacade element, int column)
		{
			switch (column)
			{
				case -1:
					return character.getDomains().containsElement(element);
				case 0:
					return element;
				case 1:
					return element.getSource();
				default:
					return null;
			}
		}

		@Override
		public int getColumnCount()
		{
			return 2;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			if (columnIndex >= 0)
			{
				return false;
			}
			if (character.getRemainingDomainSelectionsRef().getReference() > 0)
			{
				return true;
			}
			DomainFacade domain = sortedList.getElementAt(rowIndex);
			return character.getDomains().containsElement(domain);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			DomainFacade domain = sortedList.getElementAt(rowIndex);
			Boolean bool = (Boolean) aValue;
			if (bool)
			{
				character.addDomain(domain);
			}
			else
			{
				character.removeDomain(domain);
			}
		}

	}

	private static class DeityTreeViewModel implements TreeViewModel<DeityFacade>, DataView<DeityFacade>
	{

		private static final ListFacade<TreeView<DeityFacade>> views =
				new DefaultListFacade<TreeView<DeityFacade>>(Arrays.asList(DeityTreeView.values()));
		private final List<DefaultDataViewColumn> columns = Arrays.asList(new DefaultDataViewColumn("in_alignLabel", Object.class), //$NON-NLS-1$
																		  new DefaultDataViewColumn("in_domains", String.class), //$NON-NLS-1$
																		  new DefaultDataViewColumn("in_pantheon", String.class), //$NON-NLS-1$
																		  new DefaultDataViewColumn("in_sourceLabel", String.class)); //$NON-NLS-1$
		private final CharacterFacade character;
		private InfoFactory infoFactory;

		public DeityTreeViewModel(CharacterFacade character)
		{
			this.character = character;
			this.infoFactory = character.getInfoFactory();
		}

		@Override
		public ListFacade<? extends TreeView<DeityFacade>> getTreeViews()
		{
			return views;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		@Override
		public DataView<DeityFacade> getDataView()
		{
			return this;
		}

		@Override
		public ListFacade<DeityFacade> getDataModel()
		{
			return character.getDataSet().getDeities();
		}

		@Override
		public List<?> getData(DeityFacade obj)
		{
			return Arrays.asList(obj.getAlignment(),
								 infoFactory.getDomains(obj), infoFactory.getPantheons(obj),
								 obj.getSource());
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
			return "DeityTree";  //$NON-NLS-1$
		}

	}

	private enum DeityTreeView implements TreeView<DeityFacade>
	{

		NAME("in_deity"), //$NON-NLS-1$
		ALIGNMENT_NAME("in_alignmentDeity"), //$NON-NLS-1$
		DOMAIN_NAME("in_domainDeity"), //$NON-NLS-1$
		PANTHEON_NAME("in_pantheonDeity"), //$NON-NLS-1$
		SOURCE_NAME("in_sourceDeity"); //$NON-NLS-1$
		private String name;

		private DeityTreeView(String name)
		{
			this.name = LanguageBundle.getString(name);
		}

		@Override
		public String getViewName()
		{
			return name;
		}

		@Override
		public List<TreeViewPath<DeityFacade>> getPaths(DeityFacade pobj)
		{
			List<TreeViewPath<DeityFacade>> paths = new ArrayList<TreeViewPath<DeityFacade>>();
			switch (this)
			{
				case NAME:
					return Collections.singletonList(new TreeViewPath<DeityFacade>(pobj));
				case DOMAIN_NAME:
					for (String domain : pobj.getDomainNames())
					{
						paths.add(new TreeViewPath<DeityFacade>(pobj, domain));
					}
					return paths;
				case ALIGNMENT_NAME:
					return Collections.singletonList(new TreeViewPath<DeityFacade>(pobj,
																				   pobj.getAlignment()));
				case PANTHEON_NAME:
					for (String pantheon : pobj.getPantheons())
					{
						paths.add(new TreeViewPath<DeityFacade>(pobj, pantheon));
					}
					return paths;
				case SOURCE_NAME:
					return Collections.singletonList(new TreeViewPath<DeityFacade>(pobj,
																				   pobj.getSourceForNodeDisplay()));
				default:
					throw new InternalError();
			}


		}

	}

}
