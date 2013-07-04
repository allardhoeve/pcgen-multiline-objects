/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pcgen.base.util.ListSet;
import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.DataFacetChangeEvent;

/**
 * An AbstractSourcedListFacet is a DataFacet that contains information about
 * Objects that are contained in a PlayerCharacter when a PlayerCharacter may
 * have more than one of that type of Object (e.g. Language, PCTemplate) and the
 * source of that object should be tracked.
 * 
 * This class is designed to assume that each Object may only be contained one
 * time by the PlayerCharacter, even if received from multiple sources. The
 * Object will only trigger one DATA_ADDED event (when added by the first
 * source) and if removed by some sources, will only trigger one DATA_REMOVED
 * event (when it is removed by the last remaining source). Sources do not need
 * to be removed in the order in which they are added, and the first source to
 * be added does not possess special status with respect to triggering a
 * DATA_REMOVED event (it will only trigger removal if it was the last source
 * when removed)
 * 
 * The sources stored in this AbstractSourcedListFacet are stored as a List,
 * meaning the list of sources may contain the same source multiple times. If
 * so, each call to remove will only remove that source one time from the list
 * of sources.
 * 
 * null is a valid source.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public abstract class AbstractSourcedListFacet<T> extends AbstractDataFacet<T>
{
	/**
	 * Add the given object with the given source to the list of objects stored
	 * in this AbstractSourcedListFacet for the Player Character represented by
	 * the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            given item should be added
	 * @param obj
	 *            The object to be added to the list of objects stored in this
	 *            AbstractSourcedListFacet for the Player Character represented
	 *            by the given CharID
	 * @param source
	 *            The source for the given object
	 */
	public void add(CharID id, T obj, Object source)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException("Object to add may not be null");
		}
		Map<T, Set<Object>> map = getConstructingCachedMap(id);
		Set<Object> set = map.get(obj);
		boolean fireNew = (set == null);
		if (fireNew)
		{
			set = new WrappedMapSet<Object>(IdentityHashMap.class);
			map.put(obj, set);
		}
		set.add(source);
		if (fireNew)
		{
			fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
		}
	}

	/**
	 * Adds all of the objects with the given source in the given Collection to
	 * the list of objects stored in this AbstractSourcedListFacet for the
	 * Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            given items should be added
	 * @param c
	 *            The Collection of objects to be added to the list of objects
	 *            stored in this AbstractSourcedListFacet for the Player
	 *            Character represented by the given CharID
	 * @param source
	 *            The source for the given object
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
	public void addAll(CharID id, Collection<? extends T> c, Object source)
	{
		for (T obj : c)
		{
			add(id, obj, source);
		}
	}

	/**
	 * Removes the given source entry from the list of sources for the given
	 * object stored in this AbstractSourcedListFacet for the Player Character
	 * represented by the given CharID. If the given source was the only source
	 * for the given object, then the object is removed from the list of objects
	 * stored in this AbstractSourcedListFacet for the Player Character
	 * represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given item source should be removed
	 * @param obj
	 *            The object for which the source should be removed
	 * @param source
	 *            The source for the given object to be removed from the list of
	 *            sources.
	 */
	public boolean remove(CharID id, T obj, Object source)
	{
		Map<T, Set<Object>> componentMap = getCachedMap(id);
		return (componentMap != null)
				&& processRemoval(id, componentMap, obj, source);
	}

	/**
	 * Removes the given source entry from the list of sources for all of the
	 * objects in the given Collection for the Player Character represented by
	 * the given CharID. If the given source was the only source for any of the
	 * objects in the collection, then those objects are removed from the list
	 * of objects stored in this AbstractSourcedListFacet for the Player
	 * Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given items should be removed
	 * @param c
	 *            The Collection of objects to be removed from the list of
	 *            objects stored in this AbstractSourcedListFacet for the Player
	 *            Character represented by the given CharID
	 * @param source
	 *            The source for the objects in the given Collection to be
	 *            removed from the list of sources.
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
	public void removeAll(CharID id, Collection<T> c, Object source)
	{
		Map<T, Set<Object>> componentMap = getCachedMap(id);
		if (componentMap != null)
		{
			for (T obj : c)
			{
				processRemoval(id, componentMap, obj, source);
			}
		}
	}

	/**
	 * Removes all objects (and all sources for those objects) from the list of
	 * objects stored in this AbstractSourcedListFacet for the Player Character
	 * represented by the given CharID
	 * 
	 * This method is value-semantic in that ownership of the returned Map is
	 * transferred to the class calling this method. Since this is a remove all
	 * function, modification of the returned Map will not modify this
	 * AbstractSourcedListFacet and modification of this
	 * AbstractSourcedListFacet will not modify the returned Map. Modifications
	 * to the returned Map will also not modify any future or previous objects
	 * returned by this (or other) methods on AbstractSourcedListFacet. If you
	 * wish to modify the information stored in this AbstractSourcedListFacet,
	 * you must use the add*() and remove*() methods of
	 * AbstractSourcedListFacet.
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which all
	 *            items should be removed
	 * @return A non-null Set of objects removed from the list of objects stored
	 *         in this AbstractSourcedListFacet for the Player Character
	 *         represented by the given CharID
	 */
	public Map<T, Set<Object>> removeAll(CharID id)
	{
		Map<T, Set<Object>> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return Collections.emptyMap();
		}
		removeCache(id);
		for (T obj : componentMap.keySet())
		{
			fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_REMOVED);
		}
		return componentMap;
	}

	/**
	 * Returns a non-null copy of the Set of objects in this
	 * AbstractSourcedListFacet for the Player Character represented by the
	 * given CharID. This method returns an empty set if no objects are in this
	 * AbstractSourcedListFacet for the Player Character identified by the given
	 * CharID.
	 * 
	 * This method is value-semantic in that ownership of the returned Set is
	 * transferred to the class calling this method. Modification of the
	 * returned Set will not modify this AbstractSourcedListFacet and
	 * modification of this AbstractSourcedListFacet will not modify the
	 * returned Set. Modifications to the returned Set will also not modify any
	 * future or previous objects returned by this (or other) methods on
	 * AbstractSourcedListFacet. If you wish to modify the information stored in
	 * this AbstractSourcedListFacet, you must use the add*() and remove*()
	 * methods of AbstractSourcedListFacet.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            items in this AbstractSourcedListFacet should be returned.
	 * @return A non-null copy of the Set of objects in this
	 *         AbstractSourcedListFacet for the Player Character represented by
	 *         the given CharID
	 */
	public Set<T> getSet(CharID id)
	{
		Map<T, Set<Object>> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return Collections.emptySet();
		}
		return Collections
			.unmodifiableSet(new ListSet<T>(componentMap.keySet()));
	}

	/**
	 * Returns the count of items in this AbstractSourcedListFacet for the
	 * Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            count of items should be returned
	 * @return The count of items in this AbstractSourcedListFacet for the
	 *         Player Character represented by the given CharID
	 */
	public int getCount(CharID id)
	{
		Map<T, Set<Object>> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return 0;
		}
		return componentMap.size();
	}

	/**
	 * Returns true if this AbstractSourcedListFacet does not contain any items
	 * for the Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharId representing the PlayerCharacter to test if any
	 *            items are contained by this AbstractsSourcedListFacet
	 * @return true if this AbstractSourcedListFacet does not contain any items
	 *         for the Player Character represented by the given CharID; false
	 *         otherwise (if it does contain items for the Player Character)
	 */
	public boolean isEmpty(CharID id)
	{
		Map<T, Set<Object>> componentMap = getCachedMap(id);
		return componentMap == null || componentMap.isEmpty();
	}

	/**
	 * Returns true if this AbstractSourcedListFacet contains the given value in
	 * the list of items for the Player Character represented by the given
	 * CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character used for testing
	 * @param obj
	 *            The object to test if this AbstractSourcedListFacet contains
	 *            that item for the Player Character represented by the given
	 *            CharID
	 * @return true if this AbstractSourcedListFacet contains the given value
	 *         for the Player Character represented by the given CharID; false
	 *         otherwise
	 */
	public boolean contains(CharID id, T obj)
	{
		/*
		 * TODO obj == null? - log an error?
		 * 
		 * This should share behavior with AbstractListFacet
		 */
		Map<T, Set<Object>> componentMap = getCachedMap(id);
		return componentMap != null && componentMap.containsKey(obj);
	}

	/**
	 * Returns a Set of sources for this AbstractSourcedListFacet, the
	 * PlayerCharacter represented by the given CharID, and the given object.
	 * Will add the given object to the list of items for the PlayerCharacter
	 * represented by the given CharID and will return a new, empty Set if no
	 * information has been set in this AbstractSourcedListFacet for the given
	 * CharID and given object. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The Set object is owned by
	 * AbstractSourcedListFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractSourcedListFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @param obj
	 *            The object for which the Set of sources should be returned
	 * @return The Set of sources for the given object and Player Character
	 *         represented by the given CharID.
	 */
	private Set<Object> getConstructingCachedSetFor(CharID id, T obj)
	{
		Map<T, Set<Object>> map = getConstructingCachedMap(id);
		Set<Object> set = map.get(obj);
		if (set == null)
		{
			set = new WrappedMapSet<Object>(IdentityHashMap.class);
			map.put(obj, set);
		}
		return set;
	}

	/**
	 * Returns the type-safe Map for this AbstractSourcedListFacet and the given
	 * CharID. May return null if no information has been set in this
	 * AbstractSourcedListFacet for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The Map is owned by
	 * AbstractSourcedListFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractSourcedListFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @return The Map for the Player Character represented by the given CharID;
	 *         null if no information has been set in this
	 *         AbstractSourcedListFacet for the Player Character.
	 */
	protected Map<T, Set<Object>> getCachedMap(CharID id)
	{
		return (Map<T, Set<Object>>) getCache(id);
	}

	/**
	 * Returns the type-safe Map for this AbstractSourcedListFacet and the given
	 * CharID. Will return a new, empty Map if no information has been set in
	 * this AbstractSourcedListFacet for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The Map object is owned by
	 * AbstractSourcedListFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractSourcedListFacet.
	 * 
	 * @param id
	 *            The CharID for which the Map should be returned
	 * @return The Map for the Player Character represented by the given CharID.
	 */
	private Map<T, Set<Object>> getConstructingCachedMap(CharID id)
	{
		Map<T, Set<Object>> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			componentMap = getComponentMap();
			setCache(id, componentMap);
		}
		return componentMap;
	}

	/**
	 * Returns a new (empty) Map for this AbstractSourcedListFacet. Can be
	 * overridden by classes that extend AbstractSourcedListFacet if a Map other
	 * than an IdentityHashMap is desired for storing the information in the
	 * AbstractSourcedListFacet.
	 * 
	 * Note that this method SHOULD NOT be public. The Map object is owned by
	 * AbstractSourcedListFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractSourcedListFacet.
	 * 
	 * Note that this method should always be the only method used to construct
	 * a Map for this AbstractSourcedListFacet. It is actually preferred to use
	 * getConstructingCacheMap(CharID) in order to implicitly call this method.
	 * 
	 * @return A new (empty) Map for use in this AbstractSourcedListFacet.
	 */
	protected Map<T, Set<Object>> getComponentMap()
	{
		return new IdentityHashMap<T, Set<Object>>();
	}

	/**
	 * Copies the contents of the AbstractSourcedListFacet from one Player
	 * Character to another Player Character, based on the given CharIDs
	 * representing those Player Characters.
	 * 
	 * This is a method in AbstractSourcedListFacet in order to avoid exposing
	 * the mutable Map object to other classes. This should not be inlined, as
	 * the Map is internal information to AbstractSourcedListFacet and should
	 * not be exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the AbstractSourcedListFacet of
	 * one Player Character will only impact the Player Character where the
	 * AbstractSourcedListFacet was changed).
	 * 
	 * @param source
	 *            The CharID representing the Player Character from which the
	 *            information should be copied
	 * @param destination
	 *            The CharID representing the Player Character to which the
	 *            information should be copied
	 */
	@Override
	public void copyContents(CharID source, CharID destination)
	{
		Map<T, Set<Object>> sourceMap = getCachedMap(source);
		if (sourceMap != null)
		{
			for (Map.Entry<T, Set<Object>> me : sourceMap.entrySet())
			{
				T obj = me.getKey();
				Set<Object> sourceSet = me.getValue();
				Set<Object> targetSet = getConstructingCachedSetFor(
						destination, obj);
				targetSet.addAll(sourceSet);
			}
		}
	}

	/**
	 * This method implements removal of a source for an object contained by
	 * this AbstractSourcedListFacet. This implements the actual check that
	 * determines if the given source was the only source for the given object.
	 * If so, then that object is removed from the list of objects stored in
	 * this AbstractQualifiedListFacet for the Player Character represented by
	 * the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character which may have
	 *            the given item removed.
	 * @param componentMap
	 *            The (private) Map for this AbstractSourcedListFacet that will
	 *            as least have the given source removed from the list for the
	 *            given object.
	 * @param obj
	 *            The object which may be removed if the given source is the
	 *            only source for this object in the Player Character
	 *            represented by the given CharID
	 * @param source
	 *            The source for the given object to be removed from the list of
	 *            sources for that object
	 */
	private boolean processRemoval(CharID id, Map<T, Set<Object>> componentMap,
			T obj, Object source)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException("Object to remove may not be null");
		}
		Set<Object> set = componentMap.get(obj);
		if (set == null)
		{
			return false;
		}
		boolean returnVal = set.remove(source);
		if (set.isEmpty())
		{
			componentMap.remove(obj);
			fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_REMOVED);
			if (componentMap.isEmpty())
			{
				removeCache(id);
			}
		}
		return returnVal;
	}

	/**
	 * Removes all information for the given source from this
	 * AbstractSourcedListFacet for the PlayerCharacter represented by the given
	 * CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which items
	 *            from the given source will be removed
	 * @param source
	 *            The source for the objects to be removed from the list of
	 *            items stored for the Player Character identified by the given
	 *            CharID
	 */
	public void removeAll(CharID id, Object source)
	{
		Map<T, Set<Object>> componentMap = getCachedMap(id);
		if (componentMap != null)
		{
			List<T> removedKeys = new ArrayList<T>();
			for (Iterator<Map.Entry<T, Set<Object>>> it = componentMap
					.entrySet().iterator(); it.hasNext();)
			{
				Entry<T, Set<Object>> me = it.next();
				Set<Object> set = me.getValue();
				if (set.remove(source) && set.isEmpty())
				{
					T obj = me.getKey();
					it.remove();
					removedKeys.add(obj);
				}
			}
			if (componentMap.isEmpty())
			{
				removeCache(id);
			}
			for (T obj : removedKeys)
			{
				fireDataFacetChangeEvent(id, obj,
						DataFacetChangeEvent.DATA_REMOVED);
			}
		}
	}

	/**
	 * Returns a non-null copy of the Set of objects in this
	 * AbstractSourcedListFacet for the Player Character represented by the
	 * given CharID and the given source. This method returns an empty set if no
	 * objects are in this AbstractSourcedListFacet for the Player Character
	 * identified by the given CharID and source.
	 * 
	 * This method is value-semantic in that ownership of the returned List is
	 * transferred to the class calling this method. Modification of the
	 * returned List will not modify this AbstractSourcedListFacet and
	 * modification of this AbstractSourcedListFacet will not modify the
	 * returned List. Modifications to the returned List will also not modify
	 * any future or previous objects returned by this (or other) methods on
	 * AbstractSourcedListFacet. If you wish to modify the information stored in
	 * this AbstractSourcedListFacet, you must use the add*() and remove*()
	 * methods of AbstractSourcedListFacet.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            items in this AbstractSourcedListFacet should be returned.
	 * @param owner
	 *            The source object for which a copy of the List of objects in
	 *            this AbstractSourcedListFacet should be returned.
	 * @return A non-null Set of objects in this AbstractSourcedListFacet for
	 *         the Player Character represented by the given CharID
	 */
	public List<? extends T> getSet(CharID id, Object owner)
	{
		List<T> list = new ArrayList<T>();
		Map<T, Set<Object>> componentMap = getCachedMap(id);
		if (componentMap != null)
		{
			for (Iterator<Map.Entry<T, Set<Object>>> it = componentMap
					.entrySet().iterator(); it.hasNext();)
			{
				Entry<T, Set<Object>> me = it.next();
				Set<Object> set = me.getValue();
				if (set.contains(owner))
				{
					list.add(me.getKey());
				}
			}
		}
		return Collections.unmodifiableList(list);
	}

	/**
	 * Returns true if this AbstractSourcedListFacet contains any item from the
	 * given source in the list of items for the Player Character represented by
	 * the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character used for testing
	 * @param owner
	 *            The source object for which must have granted an object in
	 *            this AbstractSourcedListFacet for the Player Character
	 *            identified by the given CharID
	 * @return true if this AbstractSourcedListFacet contains any item from the
	 *         given source for the Player Character represented by the given
	 *         CharID; false otherwise
	 */
	public boolean containsFrom(CharID id, Object owner)
	{
		Map<T, Set<Object>> componentMap = getCachedMap(id);
		if (componentMap != null)
		{
			for (Iterator<Map.Entry<T, Set<Object>>> it = componentMap
					.entrySet().iterator(); it.hasNext();)
			{
				Entry<T, Set<Object>> me = it.next();
				Set<Object> set = me.getValue();
				if (set.contains(owner))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the count of items granted by the given source in this
	 * AbstractSourcedListFacet for the Player Character represented by the
	 * given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            count of items should be returned
	 * @param owner
	 *            The source object used to determine the count of objects in
	 *            this AbstractSourcedListFacet for the Player Character
	 *            identified by the given CharID
	 * @return The count of items granted by the given source in this
	 *         AbstractSourcedListFacet for the Player Character represented by
	 *         the given CharID
	 */
	public int getCountFrom(CharID id, CDOMObject owner)
	{
		Map<T, Set<Object>> componentMap = getCachedMap(id);
		int count = 0;
		if (componentMap != null)
		{
			for (Iterator<Map.Entry<T, Set<Object>>> it =
					componentMap.entrySet().iterator(); it.hasNext();)
			{
				Entry<T, Set<Object>> me = it.next();
				Set<Object> set = me.getValue();
				if (set.contains(owner))
				{
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Returns true if this AbstractSourcedListFacet contains the given value
	 * (granted by the given source) in the list of items for the Player
	 * Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character used for testing
	 * @param owner
	 *            The source object for which must have granted the object being
	 *            tested to see if it is contained by this
	 *            AbstractSourcedListFacet for the Player Character identified
	 *            by the given CharID
	 * @param obj
	 *            The object to test if this AbstractSourcedListFacet contains
	 *            that item for the Player Character represented by the given
	 *            CharID
	 * @return true if this AbstractSourcedListFacet contains the given value
	 *         (granted by the given source) for the Player Character
	 *         represented by the given CharID; false otherwise
	 */
	public boolean containsFrom(CharID id, T obj, CDOMObject owner)
	{
		Map<T, Set<Object>> componentMap = getCachedMap(id);
		if (componentMap != null)
		{
			Set<Object> sources = componentMap.get(obj);
			return sources != null && sources.contains(owner);
		}
		return false;
	}

}
