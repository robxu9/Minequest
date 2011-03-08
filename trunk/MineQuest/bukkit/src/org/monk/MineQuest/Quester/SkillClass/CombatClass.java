package org.monk.MineQuest.Quester.SkillClass;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Ability.Ability;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.Combat.PeaceMage;
import org.monk.MineQuest.Quester.SkillClass.Combat.WarMage;

public class CombatClass extends SkillClass implements DefendingClass {

	public CombatClass(Quester quester, String type) {
		super(quester, type);
	}
	
	public CombatClass() {
		// Shell
	}

	/**
	 * Called whenever an attack is made by a Quester. Checks for
	 * bound abilities and if none are found sets the damage of the
	 * event as required.
	 * 
	 * @param defend Entity that is defending the attack
	 * @param event Event created for this attack
	 * @return boolean true
	 */
	public boolean attack(LivingEntity defend, EntityDamageByEntityEvent event) {
		for (Ability abil : ability_list) {
			if (abil.isBound(quester.getPlayer().getItemInHand())) {
				if (abil.parseAttack(quester, defend)) {
					if ((this instanceof WarMage) || (this instanceof PeaceMage)) {
						return true;
					}
				}
			}
		}

		event.setDamage(getDamage(defend));

		expAdd(getExpMob(defend) + MineQuest.getAdjustment());
		
		return true;
	}
	
	/**
	 * Gets the amount of damage that this class would do
	 * to a specific entity.
	 * 
	 * @param defend Defending Entity
	 * @return Damage to be dealt
	 */
	protected int getDamage(LivingEntity defend) {
		int damage = 2;
		damage += (quester.getLevel() / 10);
		damage += (level / 5);
		
		if (generator.nextDouble() < getCritChance()) {
			damage *= 2;
			quester.sendMessage("Critical Hit!");
		}
		if (!isClassItem(quester.getPlayer().getItemInHand())) {
			damage /= 2;
		}
		
		if (MineQuest.getMob(defend) != null) {
			damage = MineQuest.getMob(defend).defend(damage, quester.getPlayer());
		} else {
			MineQuest.log("No Mob " + defend.getEntityId());
		}
		
		return damage;
	}

	/**
	 * Gets the Critical Hit chance for this specific
	 * class.
	 * 
	 * @return Critical Hit Chance
	 */
	private double getCritChance() {
		if ((getAbility("Deathblow") != null) && (getAbility("Deathblow").isEnabled())) {
			return 0.1;
		}
		
		return 0.05;
	}
	
	/**
	 * Called whenever the Quester that has this class is
	 * being attacked.
	 * 
	 * @param entity Entity that is attacker
	 * @param amount Amount of damage being dealt
	 * @return The amount of damage negated by this class
	 */
	public int defend(LivingEntity entity, int amount) {
		int i;
		int armor[] = getClassArmorIds();
		boolean flag = true;
		int sum = 0;
		
		if (armor == null) return 0;
		
		for (i = 0; i < armor.length; i++) {
			if (isWearing(armor[i])) {
				if (generator.nextDouble() < (.05 * i)) {
					sum++;
				}
			} else {
				flag = false;
			}
		}
		if (flag) {
			if (generator.nextDouble() < .4) {
				sum++;
			}
			
			if (generator.nextDouble() < armorBlockChance(armor)) {
				return amount;
			}
		}
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isDefending()) {
				return sum + ability_list[i].parseDefend(quester, entity, amount - sum);
			}
		}
		
		return sum;
	}

	/**
	 * Gets the chance for an armor set to block an
	 * attack.
	 * 
	 * @param armor Set of armor to check.
	 * @return Chance of armor blocking attack.
	 */
	private double armorBlockChance(int[] armor) {
		switch (armor[0]) {
		case 302:
			return .25;
		case 310:
			return .20;
		case 306:
			return .15;
		case 314:
			return .10;
		case 298: 
			return .05;
		}
		
		return 0;
	}
	
	@Override
	public void levelUp() {
		super.levelUp();
		int add_health;
		int size;
		
		size = getSize();
		
		add_health = generator.nextInt(size) + 1;
		
		quester.addHealth(add_health);

		if (type.equals("Warrior")) {
			switch (level) {
			case 3:
				addAbility("PowerStrike");
				break;
			case 1:
				addAbility("Dodge");
				break;
			case 12:
				addAbility("Deathblow");
				break;
			case 5:
				addAbility("Sprint");
				break;
			default:
				break;
			}
		} else if (type.equals("Archer")) {
			switch (level) {
			case 1:
				addAbility("Dodge");
				break;
			case 3:
				addAbility("Sprint");
				break;
			case 5:
				addAbility("Hail of Arrows");
				break;
			case 7:
				addAbility("Fire Arrow");
				break;
			case 10:
				addAbility("Repulsion");
				break;
			default:
				break;
			}
		} else if (type.equals("WarMage")) {
			switch (level) {
			case 2:
				addAbility("Trap");
				break;
			case 3:
				addAbility("Drain Life");
				break;
			case 5:
				addAbility("Wall of Fire");
				break;
			case 7:
				addAbility("IceSphere");
				break;
			case 10:
				addAbility("FireChain");
				break;
			default:
				break;
			}
		} else if (type.equals("PeaceMage")) {
			switch (level) {
			case 2:
				addAbility("Trape");
				break;
			case 3:
				addAbility("Wall of Water");
				break;
			case 7:
				addAbility("Heal Aura");
				break;
			case 8:
				addAbility("Damage Aura");
				break;
			default:
				break;
			}
		}
	}

	private int getSize() {
		//TODO: deal with this
		if (type.equals("Warrior")) {
			return 10;
		} else if (type.equals("Archer") || type.equals("PeaceMage")) {
			return 6;
		} else {
			return 4;
		}
	}

}
