/*
 * CRToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on December 15, 2003, 12:21 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.exporttokens;

import org.apache.commons.lang.math.Fraction;

import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

/**
 * Deal with CR Token
 */
public class CRToken extends AbstractExportToken
{
	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "CR";
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, CharacterDisplay display,
		ExportHandler eh)
	{
		String retString = "";
		Float cr = display.calcCR();
		
		String crAsString = Float.toString(cr);
		String decimalPlaceValue =
				crAsString.substring(crAsString.length() - 2);
		
		if (cr > 0 && cr < 1)
		{
			// If the CR is a fractional CR then we convert to a 1/x format
			Fraction fraction = Fraction.getFraction(cr);// new Fraction(CR);
			int denominator = fraction.getDenominator();
			int numerator = fraction.getNumerator();
			retString = numerator + "/" + denominator;
		}
		else if (cr >= 1 || cr == 0)
		{
			int newCr = -99;
			if (decimalPlaceValue.equals(".0"))
			{
				newCr = (int) cr.floatValue();
			}
		
			if (newCr > -99)
			{
				retString = retString + newCr;
			}
			else
			{
				retString = retString + cr;
			}
		}
		else if (cr.isNaN())
		{
			retString = "0";
		}
		return retString;
	}
}
