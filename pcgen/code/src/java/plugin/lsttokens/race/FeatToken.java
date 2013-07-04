/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.race;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.helper.AbilityTargetSelector;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.Race;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with FEAT Token
 */
public class FeatToken extends AbstractTokenWithSeparator<Race> implements
		CDOMPrimaryToken<Race>, DeferredToken<Race>
{
	public static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		Race race, String value)
	{
		context.getObjectContext().removeList(race, ListKey.FEAT_TOKEN_LIST);

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		boolean first = true;

		ReferenceManufacturer<Ability> rm = context.ref.getManufacturer(
				ABILITY_CLASS, AbilityCategory.FEAT);

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				if (!first)
				{
					return new ParseResult.Fail("  Non-sensical " + getTokenName()
							+ ": .CLEAR was not the first list item: " + value, context);
				}
			}
			else
			{
				CDOMReference<Ability> ability = TokenUtilities.getTypeOrPrimitive(rm, token);
				if (ability == null)
				{
					return ParseResult.INTERNAL_ERROR;
				}
				ability.setRequiresTarget(true);
				context.getObjectContext().addToList(race,
						ListKey.FEAT_TOKEN_LIST, ability);
			}
			first = false;
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Race race)
	{
		Changes<CDOMReference<Ability>> changes = context.getObjectContext()
				.getListChanges(race, ListKey.FEAT_TOKEN_LIST);
		Collection<CDOMReference<Ability>> added = changes.getAdded();
		Collection<CDOMReference<Ability>> removedItems = changes.getRemoved();
		String returnVal = null;
		if (changes.includesGlobalClear())
		{
			if (removedItems != null && !removedItems.isEmpty())
			{
				context.addWriteMessage("Non-sensical relationship in "
						+ getTokenName()
						+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			returnVal = Constants.LST_DOT_CLEAR;
		}
		else if (removedItems != null && !removedItems.isEmpty())
		{
			context.addWriteMessage(getTokenName() + " does not support "
					+ Constants.LST_DOT_CLEAR_DOT);
			return null;
		}
		if (added != null && !added.isEmpty())
		{
			returnVal = ReferenceUtilities.joinLstFormat(added, Constants.PIPE);
		}
		if (returnVal == null)
		{
			return null;
		}
		return new String[] { returnVal };
	}

	@Override
	public Class<Race> getTokenClass()
	{
		return Race.class;
	}

	@Override
	public Class<Race> getDeferredTokenClass()
	{
		return Race.class;
	}

	/*
	 * Explanation of: Why is this a deferred token?
	 * 
	 * The OVERWRITE behavior of this token changes behavior in such a way that
	 * the processing below needs to be done *after* processing. It makes the
	 * OVERWRITE and .CLEAR processing significantly easier, in that it only has
	 * to worry about one list, and not clearing in two locations. It's also an
	 * issue because the ListContext list used below is a shared list with other
	 * tokens, so the overwrite needs to be isolated to this token, so it needs
	 * to be separate.
	 * 
	 * As a result of this, the unparse gets a bit counter-intuitive in that no
	 * special processing on %LIST needs to be done, so this may look
	 * inconsistent to other Ability/Feat based tokens
	 * 
	 * -thpr Dec 15, 2012
	 */
	@Override
	public boolean process(LoadContext context, Race obj)
	{
		for (CDOMReference<Ability> ability : obj
				.getSafeListFor(ListKey.FEAT_TOKEN_LIST))
		{
			String token = ability.getLSTformat(false);
			boolean loadList = true;
			List<String> choices = null;
			if (token.indexOf('(') != -1)
			{
				choices = new ArrayList<String>();
				AbilityUtilities.getUndecoratedName(token, choices);
				if (choices.size() == 1)
				{
					if (Constants.LST_PERCENT_LIST.equals(choices.get(0))
							&& (ability instanceof CDOMSingleRef))
					{
						CDOMSingleRef<Ability> ref = (CDOMSingleRef<Ability>) ability;
						AbilityTargetSelector ats = new AbilityTargetSelector(
								getTokenName(), AbilityCategory.FEAT, ref,
								Nature.AUTOMATIC);
						context.obj.addToList(obj, ListKey.CHOOSE_ACTOR, ats);
						loadList = false;
					}
				}
			}
			if (loadList)
			{
				AssociatedPrereqObject assoc = context.getListContext()
						.addToList(getTokenName(), obj, Ability.FEATLIST,
								ability);
				assoc.setAssociation(AssociationKey.NATURE, Nature.AUTOMATIC);
				assoc.setAssociation(AssociationKey.CATEGORY,
						AbilityCategory.FEAT);
				if (choices != null)
				{
					assoc.setAssociation(AssociationKey.ASSOC_CHOICES, choices);
				}
			}
		}
		return true;
	}

}
