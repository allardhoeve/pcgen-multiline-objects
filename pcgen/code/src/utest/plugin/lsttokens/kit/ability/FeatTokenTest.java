/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.kit.ability;

import org.junit.Test;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.kit.KitAbilities;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;

public class FeatTokenTest extends AbstractKitTokenTestCase<KitAbilities>
{

	static FeatToken token = new FeatToken();
	static CDOMSubLineLoader<KitAbilities> loader = new CDOMSubLineLoader<KitAbilities>(
			"SKILL", KitAbilities.class);

	@Override
	public Class<KitAbilities> getCDOMClass()
	{
		return KitAbilities.class;
	}

	@Override
	public CDOMSubLineLoader<KitAbilities> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<KitAbilities> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmptyCount() throws PersistenceLayerException
	{
		assertTrue(parse("Fireball"));
		assertConstructionError();
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		Ability ab = primaryContext.ref.constructCDOMObject(Ability.class,
				"Fireball");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref
				.constructCDOMObject(Ability.class, "Fireball");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		runRoundRobin("Fireball");
	}

	@Test
	public void testRoundRobinTwo() throws PersistenceLayerException
	{
		Ability ab = primaryContext.ref.constructCDOMObject(Ability.class,
				"Fireball");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref
				.constructCDOMObject(Ability.class, "Fireball");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(Ability.class, "English");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "English");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		runRoundRobin("English" + getJoinCharacter() + "Fireball");
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		assertFalse(parse("TestWP1" + getJoinCharacter()));
	}

	private char getJoinCharacter()
	{
		return '|';
	}

	@Test
	public void testInvalidChangeCatetory() throws PersistenceLayerException
	{
		assertFalse(parse("CATEGORY=FEAT|TestWP1"));
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		assertFalse(parse(getJoinCharacter() + "TestWP1"));
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		assertFalse(parse("TestWP2" + getJoinCharacter() + getJoinCharacter()
				+ "TestWP1"));
	}

	//TODO Doesn't test TYPE=
}