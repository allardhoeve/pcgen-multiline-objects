/**
 * MockDataSetFacade.java
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
 * Created on 23/01/2011 7:56:33 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import java.util.List;

import pcgen.core.AbilityCategory;
import pcgen.core.GameMode;
import pcgen.core.facade.AbilityCategoryFacade;
import pcgen.core.facade.AbilityFacade;
import pcgen.core.facade.AlignmentFacade;
import pcgen.core.facade.BodyStructureFacade;
import pcgen.core.facade.CampaignFacade;
import pcgen.core.facade.ClassFacade;
import pcgen.core.facade.DataSetFacade;
import pcgen.core.facade.DeityFacade;
import pcgen.core.facade.DomainFacade;
import pcgen.core.facade.EquipmentFacade;
import pcgen.core.facade.GameModeFacade;
import pcgen.core.facade.GearBuySellFacade;
import pcgen.core.facade.KitFacade;
import pcgen.core.facade.RaceFacade;
import pcgen.core.facade.SkillFacade;
import pcgen.core.facade.StatFacade;
import pcgen.core.facade.TemplateFacade;
import pcgen.core.facade.generator.StatGenerationFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;

/**
 * The Class <code></code> is ...
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class MockDataSetFacade implements DataSetFacade
{

	private DefaultListFacade<BodyStructureFacade> equipmentLoc;
	private DefaultListFacade<AbilityCategoryFacade> abilityCat;
	private final GameMode game;
	private DefaultListFacade<RaceFacade> races;
	private DefaultListFacade<SkillFacade> skills;
	private DefaultListFacade<StatFacade> stats;
	private DefaultListFacade<GearBuySellFacade> gearBuySellSchemes;


	public MockDataSetFacade(GameMode gameMode)
	{
		this.game = gameMode;
		equipmentLoc = new DefaultListFacade<BodyStructureFacade>();
		abilityCat = new DefaultListFacade<AbilityCategoryFacade>();
		races = new DefaultListFacade<RaceFacade>();
		skills = new DefaultListFacade<SkillFacade>();
		stats  = new DefaultListFacade<StatFacade>();
		gearBuySellSchemes = new DefaultListFacade<GearBuySellFacade>();
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getAbilities(pcgen.core.facade.AbilityCategoryFacade)
	 */
    @Override
	public ListFacade<AbilityFacade> getAbilities(AbilityCategoryFacade category)
	{
		// TODO Auto-generated method stub
		return null;
	}

    @Override
	public List<AbilityFacade> getPrereqAbilities(AbilityFacade abilityFacade)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getAbilityCategories()
	 */
    @Override
	public ListFacade<AbilityCategoryFacade> getAbilityCategories()
	{
		return abilityCat;
	}

	/**
	 * Add an AbilityCategory to the list
	 * @param cat The AbilityCategory to be added.
	 */
	public void addAbilityCategory(AbilityCategory cat)
	{
		abilityCat.addElement(cat);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getAlignments()
	 */
    @Override
	public ListFacade<AlignmentFacade> getAlignments()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getCampaigns()
	 */
    @Override
	public ListFacade<CampaignFacade> getCampaigns()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getClasses()
	 */
    @Override
	public ListFacade<ClassFacade> getClasses()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getDeities()
	 */
    @Override
	public ListFacade<DeityFacade> getDeities()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getDomains()
	 */
	public ListFacade<DomainFacade> getDomains()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getEquipment()
	 */
    @Override
	public ListFacade<EquipmentFacade> getEquipment()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getEquipmentLocations()
	 */
    @Override
	public ListFacade<BodyStructureFacade> getEquipmentLocations()
	{
		return equipmentLoc;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getXPTableNames()
	 */
    @Override
	public ListFacade<String> getXPTableNames()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getCharacterTypes()
	 */
    @Override
	public ListFacade<String> getCharacterTypes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void addEquipmentLocation(BodyStructureFacade elf)
	{
		equipmentLoc.addElement(elf);
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getGameMode()
	 */
    @Override
	public GameModeFacade getGameMode()
	{
		return game;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getRaces()
	 */
    @Override
	public ListFacade<RaceFacade> getRaces()
	{
		return races;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getSkills()
	 */
    @Override
	public ListFacade<SkillFacade> getSkills()
	{
		return skills;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getSpeakLanguageSkill()
	 */
    @Override
	public SkillFacade getSpeakLanguageSkill()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getStatGenerators()
	 */
    @Override
	public ListFacade<StatGenerationFacade> getStatGenerators()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getStats()
	 */
    @Override
	public ListFacade<StatFacade> getStats()
	{
		return stats;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DataSetFacade#getTemplates()
	 */
    @Override
	public ListFacade<TemplateFacade> getTemplates()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListFacade<GearBuySellFacade> getGearBuySellSchemes()
	{
		return gearBuySellSchemes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListFacade<KitFacade> getKits()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refreshEquipment()
	{
		// TODO Auto-generated method stub
		
	}

}
