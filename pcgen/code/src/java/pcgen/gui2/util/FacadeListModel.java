/*
 * FacadeListModel.java
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
 * Created on May 29, 2010, 7:01:01 PM
 */
package pcgen.gui2.util;

import javax.swing.AbstractListModel;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.util.DelegatingListFacade;
import pcgen.core.facade.util.ListFacade;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FacadeListModel<E> extends AbstractListModel implements ListListener<E>
{

	private DelegatingListFacade<E> delegate;

	public FacadeListModel()
	{
		this(null);
	}

	public FacadeListModel(ListFacade<E> list)
	{
		this.delegate = new DelegatingListFacade<E>();
		delegate.addListListener(this);
		setListFacade(list);
	}

	public void setListFacade(ListFacade<E> list)
	{
		delegate.setDelegate(list);
	}

	@Override
	public int getSize()
	{
		return delegate.getSize();
	}

	@Override
	public Object getElementAt(int index)
	{
		return delegate.getElementAt(index);
	}

	@Override
	public void elementAdded(ListEvent<E> e)
	{
		fireIntervalAdded(this, e.getIndex(), e.getIndex());
	}

	@Override
	public void elementRemoved(ListEvent<E> e)
	{
		fireIntervalRemoved(this, e.getIndex(), e.getIndex());
	}

	@Override
	public void elementsChanged(ListEvent<E> e)
	{
		fireContentsChanged(this, 0, delegate.getSize() - 1);
	}

	@Override
	public void elementModified(ListEvent<E> e)
	{
		fireContentsChanged(this, e.getIndex(), e.getIndex());
	}

}