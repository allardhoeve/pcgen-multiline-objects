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
package pcgen.testsupport;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.rules.context.ReferenceContext;

/**
 * Support class for running Junit tests
 */
public class TestSupport
{

	/**
	 * Utility method for Unit tests to invoke private constructors
	 * 
	 * @param clazz The class we're gonig to invoke the constructor on
	 * @return An instance of the class
	 */
	public static Object invokePrivateConstructor(Class<?> clazz)
	{
		Constructor<?> constructor = null; 
		try
		{
			constructor = clazz.getDeclaredConstructor();
		}
		catch (NoSuchMethodException e)
		{
			System.err.println("Constructor for [" + clazz.getName() + "] does not exist");
		}
		
		constructor.setAccessible(true);
		Object instance = null;
		
		try
		{
			instance = constructor.newInstance();
		}
		catch (InvocationTargetException ite)
		{
			System.err.println("Instance creation failed with [" + ite.getCause() + "]");
		}
		catch (IllegalAccessException iae)
		{
			System.err.println("Instance creation failed due to access violation.");
		}
		catch (InstantiationException ie)
		{
			System.err.println("Instance creation failed with [" + ie.getCause() + "]");
		}
		
		return instance;
	}

	public static PCAlignment createAlignment(final String longName,
			final String shortName)
		{
			final PCAlignment align = new PCAlignment();
			align.setName(longName);
			align.put(StringKey.ABB, shortName);
			return align;
		}

	public static void createAllAlignments()
	{
		ReferenceContext ref = Globals.getContext().ref;
		ref.importObject(createAlignment("Lawful Good", "LG"));
		ref.importObject(createAlignment("Lawful Neutral", "LN"));
		ref.importObject(createAlignment("Lawful Evil", "LE"));
		ref.importObject(createAlignment("Neutral Good", "NG"));
		ref.importObject(createAlignment("True Neutral", "TN"));
		ref.importObject(createAlignment("Neutral Evil", "NE"));
		ref.importObject(createAlignment("Chaotic Good", "CG"));
		ref.importObject(createAlignment("Chaotic Neutral", "CN"));
		ref.importObject(createAlignment("Chaotic Evil", "CE"));
		ref.importObject(createAlignment("None", "NONE"));
		ref.importObject(createAlignment("Deity's", "Deity"));
		for (PCAlignment al : ref.getConstructedCDOMObjects(PCAlignment.class))
		{
			ref.registerAbbreviation(al, al.getAbb());
		}
	}	
}
