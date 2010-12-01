
public class Ability {
	private String name;
	@SuppressWarnings("unused")
	private boolean enabled;
	private int type;
	
	public Ability(String name, int type) {
		this.name = name;
		this.type = type;
		enabled = false;
	}
	
	public void parseAttack(Player player, Mob mob) {
		// TODO: write Ability.parseAttack(player, mob);
	}
	
	public void parseDefend(Player player, Mob mob) {
		// TODO: write Ability.parseDefend(player, mob);
	}
	
	public void enable() {
		// TODO: write Ability.enable();
	}
	
	public void disable() {
		// TODO: write Ability.disable();
	}
	
	public boolean isEnabled() {
		// TODO: write Ability.isEnabled();
		return false;
	}
	
	public boolean equals(String name) {
		return name.equals(this.name);
	}
	
	public String getName() {
		return name;
	}
	
	public int getType() {
		return type;
	}
	
	public void blockDestroy() {
		// TODO: write Ability.blockDestroy();
	}
}
