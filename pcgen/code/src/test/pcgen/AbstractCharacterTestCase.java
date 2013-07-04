/*
 * Created on 23-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import gmgen.pluginmgr.PluginLoader;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.GameModeFileLoader;
import pcgen.persistence.SourceFileLoader;
import pcgen.rules.context.ReferenceContext;

/**
 * This is an abstract TestClass designed to be able to create a PlayerCharacter
 * Object.
 *
 * @author frugal@purplewombat.co.uk
 */
@SuppressWarnings("nls")
abstract public class AbstractCharacterTestCase extends PCGenTestCase
{
	private PlayerCharacter character = null;
	protected PCStat str;
	protected PCStat cha;
	protected PCStat dex;
	protected PCStat wis;
	protected PCStat intel;
	protected PCAlignment lg;
	protected PCAlignment ln;
	protected PCAlignment le;
	protected PCAlignment ng;
	protected PCAlignment tn;
	protected PCAlignment ne;
	protected PCAlignment cg;
	protected PCAlignment cn;
	protected PCAlignment ce;
	protected SizeAdjustment colossal;
	protected SizeAdjustment gargantuan;
	protected SizeAdjustment huge;
	protected SizeAdjustment large;
	protected SizeAdjustment medium;
	protected SizeAdjustment small;
	protected SizeAdjustment tiny;
	protected SizeAdjustment diminutive;
	protected SizeAdjustment fine;

	/**
	 * Sets up the absolute minimum amount of data to create a PlayerCharacter
	 * Object.
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		Globals.setUseGUI(false);
		Globals.emptyLists();
		final GameMode gamemode = SettingsHandler.getGame();
		
		str = new PCStat();
		str.setName("Strength");
		str.put(StringKey.ABB, "STR");
		str.put(VariableKey.getConstant("LOADSCORE"),
				FormulaFactory.getFormulaFor("STRSCORE"));
		str.put(VariableKey.getConstant("OFFHANDLIGHTBONUS"),
				FormulaFactory.getFormulaFor(2));
		str.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		str.put(VariableKey.getConstant("MAXLEVELSTAT=" + str.getAbb()),
				FormulaFactory.getFormulaFor(str.getAbb() + "SCORE-10"));

		dex = new PCStat();
		dex.setName("Dexterity");
		dex.put(StringKey.ABB, "DEX");
		dex.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		dex.put(VariableKey.getConstant("MAXLEVELSTAT=" + dex.getAbb()),
				FormulaFactory.getFormulaFor(dex.getAbb() + "SCORE-10"));

		final PCStat con = new PCStat();
		con.setName("Constitution");
		con.put(StringKey.ABB, "CON");
		con.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		con.put(VariableKey.getConstant("MAXLEVELSTAT=" + con.getAbb()),
				FormulaFactory.getFormulaFor(con.getAbb() + "SCORE-10"));

		intel = new PCStat();
		intel.setName("Intelligence");
		intel.put(StringKey.ABB, "INT");
		intel.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		intel.put(VariableKey.getConstant("MAXLEVELSTAT=" + intel.getAbb()),
				FormulaFactory.getFormulaFor(intel.getAbb() + "SCORE-10"));

		wis = new PCStat();
		wis.setName("Wisdom");
		wis.put(StringKey.ABB, "WIS");
		wis.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		wis.put(VariableKey.getConstant("MAXLEVELSTAT=" + wis.getAbb()),
				FormulaFactory.getFormulaFor(wis.getAbb() + "SCORE-10"));

		cha = new PCStat();
		cha.setName("Charisma");
		cha.put(StringKey.ABB, "CHA");
		cha.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		cha.put(VariableKey.getConstant("MAXLEVELSTAT=" + cha.getAbb()),
				FormulaFactory.getFormulaFor(cha.getAbb() + "SCORE-10"));

		gamemode.setBonusFeatLevels("3|3");
		SettingsHandler.setGame("3.5");

		ReferenceContext ref = Globals.getContext().ref;
		lg = createAlignment("Lawful Good", "LG");
		ref.importObject(lg);
		ln = createAlignment("Lawful Neutral", "LN");
		ref.importObject(ln);
		le = createAlignment("Lawful Evil", "LE");
		ref.importObject(le);
		ng = createAlignment("Neutral Good", "NG");
		ref.importObject(ng);
		tn = createAlignment("True Neutral", "TN");
		ref.importObject(tn);
		ne = createAlignment("Neutral Evil", "NE");
		ref.importObject(ne);
		cg = createAlignment("Chaotic Good", "CG");
		ref.importObject(cg);
		cn = createAlignment("Chaotic Neutral", "CN");
		ref.importObject(cn);
		ce = createAlignment("Chaotic Evil", "CE");
		ref.importObject(ce);
		ref.importObject(createAlignment("None", "NONE"));
		ref.importObject(createAlignment("Deity's", "Deity"));

		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.SYSTEM_TOKENS);
		GameModeFileLoader.addDefaultWieldCategories(Globals.getContext());
		
		ref.importObject(str);
		ref.importObject(dex);
		ref.importObject(con);
		ref.importObject(intel);
		ref.importObject(wis);
		ref.importObject(cha);

		ref.constructCDOMObject(Language.class, "All Language For Test");

		fine = createSize("Fine");
		diminutive = createSize("Diminutive");
		tiny = createSize("Tiny");
		small = createSize("Small");
		medium = createSize("Medium");
		medium.put(ObjectKey.IS_DEFAULT_SIZE, true);
		large = createSize("Large");
		huge = createSize("Huge");
		gargantuan = createSize("Gargantuan");
		colossal = createSize("Colossal");

		for (PCStat stat : ref.getOrderSortedCDOMObjects(PCStat.class))
		{
			ref.registerAbbreviation(stat, stat.getAbb());
		}
		for (PCAlignment al : ref.getOrderSortedCDOMObjects(PCAlignment.class))
		{
			ref.registerAbbreviation(al, al.getAbb());
		}
		SourceFileLoader.createLangBonusObject(Globals.getContext());
		GameModeFileLoader.addDefaultUnitSet(SettingsHandler.getGame());
		SettingsHandler.getGame().selectDefaultUnitSet();
		ref.importObject(AbilityCategory.FEAT);
		additionalSetUp();

		character = new PlayerCharacter();
	}

	protected void additionalSetUp() throws Exception
	{
		//override to provide info
	}

	private SizeAdjustment createSize(String name)
	{
		final String abb  = name.substring(0, 1);

		final SizeAdjustment sa = new SizeAdjustment();

		sa.setName(name);
		sa.put(StringKey.ABB, abb);

		Globals.getContext().ref.importObject(sa);
		Globals.getContext().ref.registerAbbreviation(sa, sa.getAbbreviation());
		return sa;
	}

	/**
	 * Constructs a new <code>AbstractCharacterTestCase</code>.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public AbstractCharacterTestCase()
	{
		super();
	}

	/**
	 * Constructs a new <code>AbstractCharacterTestCase</code> with the given
	 * <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see PCGenTestCase#PCGenTestCase(String)
	 */
	public AbstractCharacterTestCase(final String name)
	{
		super(name);
	}

	public static PCAlignment createAlignment(final String longName,
		final String shortName)
	{
		final PCAlignment align = new PCAlignment();
		align.setName(longName);
		align.put(StringKey.ABB, shortName);
		return align;
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		character = null;
		super.tearDown();
	}

	/**
	 * @return Returns the character.
	 */
	public PlayerCharacter getCharacter()
	{
		return character;
	}

	/**
	 * @param aCharacter The character to set.
	 */
	public void setCharacter(final PlayerCharacter aCharacter)
	{
		this.character = aCharacter;
	}

	/**
	 * Set the value of the stat for the character.
	 *
	 * @param pc The Player Character
	 * @param statName The name of the stat to be set (eg DEX)
	 * @param value The value to be set (eg 18)
	 */
	public void setPCStat(final PlayerCharacter pc, final PCStat stat,
			final int value)
	{
		pc.setStat(stat,  value);
	}


	/**
	 * Checks if the character has the specified ability.
	 * 
	 * <p>
	 * If <tt>aCategory</tt> is <tt>null</tt> then all categories that have
	 * the same innate ability category will be checked.
	 * <p>
	 * If <tt>anAbilityType</tt> is <tt>ANY</tt> then all Natures will be
	 * checked for the ability.
	 * 
	 * @param aCategory
	 *            An <tt>AbilityCategory</tt> or <tt>null</tt>
	 * @param anAbilityType
	 *            A <tt>Nature</tt>.
	 * @param anAbility
	 *            The <tt>Ability</tt> to check for.
	 * 
	 * @return <tt>true</tt> if the character has the ability with the
	 *         criteria specified.
	 */
	public boolean hasAbility(PlayerCharacter pc, final AbilityCategory aCategory, final Nature anAbilityType, final Ability anAbility)
	{
		final List<AbilityCategory> categories;
		if (aCategory == null)
		{
			// A null category means we have to check all categories for
			// abilities of the same innate category as the passed in one.
			categories = new ArrayList<AbilityCategory>();
			final Collection<AbilityCategory> allCategories = SettingsHandler.getGame().getAllAbilityCategories();
			for (final AbilityCategory cat : allCategories)
			{
				if (cat.getParentCategory().equals(anAbility.getCDOMCategory()))
				{
					categories.add(cat);
				}
			}
		} else
		{
			categories = new ArrayList<AbilityCategory>();
			categories.add(aCategory);
		}

		final int start, end;
		if (anAbilityType == Nature.ANY)
		{
			start = 0;
			end = Nature.values().length - 2;
		} else
		{
			start = end = anAbilityType.ordinal();
		}
		for (int i = start; i <= end; i++)
		{
			final Nature nature = Nature.values()[i];
			boolean hasIt = false;
			for (final AbilityCategory cat : categories)
			{
				switch (nature)
				{
				case NORMAL:
					hasIt = pc.hasRealAbility(cat, anAbility);
					break;
				case AUTOMATIC:
					hasIt = pc.hasAutomaticAbility(cat, anAbility);
					break;
				case VIRTUAL:
					hasIt = pc.hasVirtualAbility(cat, anAbility);
					break;
				}
				if (hasIt)
				{
					return true;
				}
			}
		}
		return false;
	}

}
