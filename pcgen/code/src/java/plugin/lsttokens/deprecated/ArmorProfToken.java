/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lsttokens.deprecated;

import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class ArmorProfToken implements CDOMSecondaryToken<CDOMObject>
{

    @Override
	public String getTokenName()
	{
		return "ARMORPROF";
	}

    @Override
	public String getParentToken()
	{
		return "CHOOSE";
	}

    @Override
	public ParseResult parseToken(LoadContext context, CDOMObject obj,
		String value)
	{
		Logging.deprecationPrint("CHOOSE:ARMORPROF has been deprecated, "
			+ "please use CHOOSE:ARMORPROFICIENCY|EQUIPMENT[x]", context);
		String newValue = processMagicalWords(context, obj, value);
		return context.processSubToken(obj, getParentToken(),
			"ARMORPROFICIENCY", newValue);
	}

	private String processMagicalWords(LoadContext context, CDOMObject obj, String value)
	{
		StringTokenizer st = new StringTokenizer(value, "|", true);
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		while (st.hasMoreTokens())
		{
			String tok = st.nextToken();

			if (first && StringUtils.isNumeric(tok))
			{
				Formula f = FormulaFactory.getFormulaFor(tok);
				context.obj.put(obj, FormulaKey.NUMCHOICES, f);
				context.obj.put(obj, FormulaKey.SELECT, f);
				if (st.hasMoreTokens())
				{
					// Consume the pipe that is no longer needed.
					st.nextToken();
				}
				first = false;
				continue;
			}

			if ("TYPE.".regionMatches(true, 0, tok, 0, 5)
				|| "TYPE=".regionMatches(true, 0, tok, 0, 5))
			{
				// No change
			}
			else if (!"|".equals(tok))
			{
				tok = "EQUIPMENT[" + tok + "]";
			}
			sb.append(tok);
			first = false;
		}
		return sb.toString();
	}

    @Override
	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		return null;
	}

    @Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
