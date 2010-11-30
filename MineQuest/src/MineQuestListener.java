import java.util.List;
import java.util.logging.Logger;


public class MineQuestListener extends PluginListener {
    private Logger log;
	
	
	@Override
	public boolean onDamage(BaseEntity attacker, BaseEntity defender) {
		int attack = 1;
		int i;
		String mobs = "";
		int mobid = 0;
		List<Mob> mob_list = etc.getServer().getMobList();
		Player player;
		if (attacker.getPlayer() != null) {
			player = attacker.getPlayer();
			if (defender.isMob()) {
				mobid = defender.getId();
			}
		} else {
			attack = 0;
			player = attacker.getPlayer();
			if (attacker.isMob()) {
				mobid = attacker.getId();
			}
		}
		
		if (attack == 1) {
			player.sendMessage("You attacked a:");
		} else {
			player.sendMessage("You defended against:");
		}
		for (i = 0; i < mob_list.size(); i++) {
			if (mob_list.get(i).getId() == mobid) {
				mobs = mob_list.get(i).getName();
			}
		}
		player.sendMessage("    " + mobs);
		
		return true;
	}


	public void setup() {
        log = Logger.getLogger("Minecraft");
	}

	
	private class Quester {
		private String name;
		private int level;
		private int exp;
		
		public Quester(String name, int level, int exp) {
			this.name = name;
			this.level = level;
			this.exp = exp;
		}
		
		public void kill(String name) {
			exp += expLookup(name);
		}
		
		public int expLookup(String name) {
			return 5;
		}
		
		public String getName() {
			return name;
		}
		
		public int getLevel() {
			return level;
		}
	}
}
