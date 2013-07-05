/**
 * EquipIconLstToken.java
 * Copyright James Dempsey, 2011
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
 * Created on 14/02/2011 5:50:36 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.net.URI;

import pcgen.core.GameMode;

/**
 * The Interface <code>EquipIconLstToken</code> defines a token
 * as being callable by the EquipIconLoader.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public interface EquipIconLstToken extends LstToken
{
	/**
	 * Parse the token
	 * @param gameMode
	 * @param value
	 * @return true if successful
	 */
	public abstract boolean parse(GameMode gameMode, String value, URI source);

}