package org.monk.MineQuest.Mob;

import org.bukkit.entity.LivingEntity;

public class HealthMob extends MQMob {
	protected int health;
	protected int max_health;

	public HealthMob(LivingEntity entity, int max_health) {
		super(entity);
		this.health = this.max_health = max_health;
	}

	public int defend(int damage, LivingEntity player) {
		int newHealth;

	    health -= damage;
	    
	    newHealth = 20 * health / max_health;
	    
	    if ((newHealth == 0) && (health > 0)) {
	    	newHealth++;
	    }
	    
	    if (health > max_health) {
	    	health = max_health;
	    }
        if (entity.getHealth() >= newHealth) {
        	return entity.getHealth() - newHealth;
        } else {
        	if (entity.getHealth() < 10) {
        		player.setHealth(health + 1);
        		return 1;
        	} else {
        		return 0;
        	}
        }
	}
	
	public void damage(int i) {
		health -= i;
		updateHealth();
	}

	public void updateHealth() {
		int newValue;
		
		newValue = 20 * health / max_health;
		
		if ((newValue == 0) && (health > 0)) {
			newValue++;
		}

		if (newValue < 0) {
			newValue = 0;
		}
		
		entity.setHealth(newValue);
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int i) {
		health = i;
		updateHealth();
	}
}
