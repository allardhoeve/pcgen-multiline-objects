/**
 * DescriptionInfoTab.java
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
 * $Id: DescriptionInfoTab.java 13208 2010-09-29 12:59:43Z jdempsey $
 */
package pcgen.gui2.tabs;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.NoteFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.tabs.bio.BiographyInfoPane;
import pcgen.gui2.tabs.bio.CampaignHistoryInfoPane;
import pcgen.gui2.tabs.bio.NoteInfoPane;
import pcgen.gui2.tabs.bio.PortraitInfoPane;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.system.LanguageBundle;

/**
 * The Class <code>DescriptionInfoTab</code> is a placeholder for the yet to
 * be implemented description tab.
 *
 * <br/>
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2010-09-29 05:59:43 -0700 (Wed, 29 Sep 2010) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 13208 $
 */
@SuppressWarnings("serial")
public class DescriptionInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

	private final TabTitle tabTitle = new TabTitle("in_descrip"); //$NON-NLS-1$
	private final PortraitInfoPane portraitPane;
	private final BiographyInfoPane bioPane;
	private final CampaignHistoryInfoPane histPane;
	private final JList pageList;
	private final JButton addButton;
	private final JPanel pagePanel;

	/**
	 * Create a new instance of DescriptionInfoTab
	 */
	public DescriptionInfoTab()
	{
		super("Desc"); //$NON-NLS-1$
		this.portraitPane = new PortraitInfoPane();
		this.bioPane = new BiographyInfoPane();
		this.histPane = new CampaignHistoryInfoPane();
		this.pageList = new JList();
		this.addButton = new JButton();
		this.pagePanel = new JPanel();
		initComponents();
	}

	private void initComponents()
	{
		addButton.setAlignmentX((float) 0.5);

		Box box = Box.createVerticalBox();
		box.add(new JScrollPane(pageList));
		box.add(Box.createVerticalStrut(5));
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createRigidArea(new Dimension(8, 0)));
			hbox.add(addButton);
			hbox.add(Box.createRigidArea(new Dimension(8, 0)));
			box.add(hbox);
		}
		box.add(Box.createVerticalStrut(4));
		setLeftComponent(box);

		CardLayout pages = new CardLayout();

		pagePanel.setLayout(pages);
		addPage(bioPane);
		addPage(portraitPane);
		addPage(histPane);
		setRightComponent(pagePanel);
		setResizeWeight(0);
	}

	private <T extends Component & CharacterInfoTab> void addPage(T page)
	{
		pagePanel.add(page, page.getTabTitle().getValue(TabTitle.TITLE));
	}

	/**
	 * @param noteInfoPane
	 */
	private <T extends Component & CharacterInfoTab> void removePage(T page)
	{
		pagePanel.remove(page);
	}

	@Override
	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		DefaultListModel listModel = new DefaultListModel();
		List<NoteInfoPane> notePaneList = new ArrayList<NoteInfoPane>();

		PageItem firstPage = new PageItem(character, LanguageBundle.getString("in_descBiography"), bioPane); //$NON-NLS-1$
		listModel.addElement(firstPage);
		listModel.addElement(new PageItem(character, LanguageBundle.getString("in_portrait"), portraitPane)); //$NON-NLS-1$
		listModel.addElement(new PageItem(character, LanguageBundle.getString("in_descCampHist"), histPane)); //$NON-NLS-1$
		
		state.put(ListModel.class, listModel);
		state.put(List.class, notePaneList);
		state.put(NoteListHandler.class, new NoteListHandler(character, listModel, notePaneList));

		ListSelectionModel model = new DefaultListSelectionModel();
		model.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		model.setSelectionInterval(0, 0);
		state.put(ListSelectionModel.class, model);
		state.put(PageHandler.class, new PageHandler(model, firstPage));
		state.put(AddAction.class, new AddAction(character));
		
		return state;
	}

	@Override
	public void restoreModels(Hashtable<?, ?> state)
	{
		pageList.setModel((ListModel) state.get(ListModel.class));
		pageList.setSelectionModel((ListSelectionModel) state.get(ListSelectionModel.class));
		((NoteListHandler) state.get(NoteListHandler.class)).install();
		((PageHandler) state.get(PageHandler.class)).install();
		addButton.setAction(((AddAction) state.get(AddAction.class)));
	}

	@Override
	public void storeModels(Hashtable<Object, Object> state)
	{
		pageList.setSelectionModel(new DefaultListSelectionModel());
		((PageHandler) state.get(PageHandler.class)).uninstall();
		((NoteListHandler) state.get(NoteListHandler.class)).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	private class NoteListHandler implements ListListener<NoteFacade>
	{
		private static final int NUM_NON_NOTE_NODES = 3;
		private ListFacade<NoteFacade> notes;
		private final DefaultListModel listModel;
		private final List<NoteInfoPane> notePaneList;
		private final CharacterFacade character;

		public NoteListHandler(CharacterFacade character, DefaultListModel listModel, List<NoteInfoPane> notePaneList)
		{
			this.character = character;
			this.listModel = listModel;
			this.notePaneList = notePaneList;
			this.notes = character.getDescriptionFacade().getNotes();
			
			for (NoteFacade note : notes)
			{
				createNotePane(note, character, listModel, notePaneList, -1);
			}
			
		}
		
		private NoteInfoPane createNotePane(NoteFacade note, CharacterFacade character,
			DefaultListModel listModel, List<NoteInfoPane> notePaneList, int pos)
		{
			NoteInfoPane notePane = new NoteInfoPane(note);
			PageItem pageItem = new PageItem(character, note, notePane);
			if (pos >= 0 && pos < notePaneList.size())
			{
				// List model also has the portrait etc tabs, so we have to skip over those.
				listModel.insertElementAt(pageItem, pos+NUM_NON_NOTE_NODES);
				notePaneList.add(pos, notePane);
			}
			else
			{
				listModel.addElement(pageItem);
				notePaneList.add(notePane);
			}
			return notePane;
		}

		public void install()
		{
			notes.addListListener(this);
			
			for (NoteInfoPane noteInfoPane : notePaneList)
			{
				addPage(noteInfoPane);
			}
		}

		public void uninstall()
		{
			notes.removeListListener(this);

			for (NoteInfoPane noteInfoPane : notePaneList)
			{
				removePage(noteInfoPane);
			}
		}


		/**
		 * {@inheritDoc}
		 */
		@Override
		public void elementAdded(ListEvent<NoteFacade> e)
		{
			NoteFacade note = e.getElement();
			NoteInfoPane notePane =
					createNotePane(note, character, listModel, notePaneList,
						e.getIndex());
			addPage(notePane);
		}


		/**
		 * {@inheritDoc}
		 */
		@Override
		public void elementRemoved(ListEvent<NoteFacade> e)
		{
			NoteFacade note = e.getElement();
			if (note == null)
			{
				return;
			}
			
			removeNote(note);
			// Select the next node
			int index = e.getIndex()+NUM_NON_NOTE_NODES;
			if (index >= pageList.getModel().getSize())
			{
				index = pageList.getModel().getSize()-1;
			}
			pageList.setSelectedIndex(index);
		}

		private void removeNote(NoteFacade note)
		{
			for (Iterator<NoteInfoPane> iterator = notePaneList.iterator(); iterator.hasNext();)
			{
				NoteInfoPane pane = iterator.next();
				if (pane.getNote().equals(note))
				{
					iterator.remove();
					break;
				}
			}
			for (int i = 0; i < listModel.getSize(); i++)
			{
				PageItem item = (PageItem) listModel.elementAt(i);
				if (note == item.note)
				{
					listModel.removeElement(item);
					break;
				}
				
			}
		}


		/**
		 * {@inheritDoc}
		 */
		@Override
		public void elementsChanged(ListEvent<NoteFacade> e)
		{
			for (NoteInfoPane pane : notePaneList)
			{
				listModel.removeElement(pane);
			}
			notePaneList.clear();
			for (NoteFacade note : notes)
			{
				createNotePane(note, character, listModel, notePaneList, -1);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void elementModified(ListEvent<NoteFacade> e)
		{
			NoteFacade note = e.getElement();
			if (note == null)
			{
				return;
			}
			
			int noteIndex = e.getIndex();
			listModel.set(noteIndex, listModel.getElementAt(noteIndex));
		}
	}
	
	private static class PageItem
	{

		private NoteFacade note;
		private String name;
		private String id;
		private CharacterInfoTab page;
		private Hashtable<Object, Object> data;

		/**
		 * Create a new instance of PageItem to represent a Note.
		 * @param character The character being displayed.
		 * @param note The note being represented.
		 * @param page The page to display the note.
		 */
		public PageItem(CharacterFacade character, NoteFacade note, CharacterInfoTab page)
		{
			this.note = note;
			this.name = "";  //$NON-NLS-1$
			this.id = (String) page.getTabTitle().getValue(TabTitle.TITLE);
			this.page = page;
			this.data = page.createModels(character);
		}

		/**
		 * Create a new instance of PageItem to represent a pre-defined panel.
		 * @param character The character being displayed.
		 * @param name The name of the page.
		 * @param page The pre-defined page..
		 */
		public PageItem(CharacterFacade character, String name, CharacterInfoTab page)
		{
			this.note = null;
			this.name = name; 
			this.id = (String) page.getTabTitle().getValue(TabTitle.TITLE);
			this.page = page;
			this.data = page.createModels(character);
		}

		@Override
		public String toString()
		{
			return note == null ? name : note.getName();
		}

		public void storeModels()
		{
			page.storeModels(data);
		}

		public void restoreModels()
		{
			page.restoreModels(data);
		}

	}

	private class PageHandler implements ListSelectionListener
	{

		private final ListSelectionModel selectionModel;
		private PageItem currentPage;

		public PageHandler(ListSelectionModel selectionModel, PageItem currentPage)
		{
			this.selectionModel = selectionModel;
			this.currentPage = currentPage;
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getValueIsAdjusting())
			{
				return;
			}
			PageItem item = (PageItem) pageList.getSelectedValue();
			if (item == null)
			{
				return;
			}
			currentPage.storeModels();
			currentPage = item;
			currentPage.restoreModels();
			CardLayout pages = (CardLayout) pagePanel.getLayout();
			pages.show(pagePanel, currentPage.id);
		}

		public void install()
		{
			selectionModel.addListSelectionListener(this);
			currentPage.restoreModels();
			CardLayout pages = (CardLayout) pagePanel.getLayout();
			pages.show(pagePanel, currentPage.id);
		}

		public void uninstall()
		{
			selectionModel.removeListSelectionListener(this);
			currentPage.storeModels();
		}

	}

	/**
	 * The Class <code>AddAction</code> acts on a user pressing the Add Note 
	 * button.
	 */
	private class AddAction extends AbstractAction
	{

		private CharacterFacade character;

		public AddAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_descAddPage")); //$NON-NLS-1$
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.getDescriptionFacade().addNewNote();
		}

	}

}
