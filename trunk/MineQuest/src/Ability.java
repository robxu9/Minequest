
public class Ability {
	private String name;
	@SuppressWarnings("unused")
	private boolean enabled;
	private int bindl;
	private int bindr;
	
	public Ability(String name) {
		this.name = name;
		enabled = false;
	}
	
	public void parseAttack(Player player, LivingEntity defend, Quester quester) {
		if (name.equals("Fireball")) {
			if (player.getItemInHand() == bindr) {
				if (quester.canCast(50)) {
					etc.getServer().setBlockAt(1, (int)defend.getX(), (int)defend.getY(), (int)defend.getZ());
					player.sendMessage("Casting Fireball");
				}
			}
		}
	}
	
	public void parseDefend(Player player, Mob mob) {
		// TODO: write Ability.parseDefend(player, mob);
	}
	
	public void parseLeftClick(Player player, Block block, Quester quester) {
		if (name.equals("Fireball")) {
			if (player.getItemInHand() == bindl) {
				if (quester.canCast(50)) {
					Block nblock = new Block(1, block.getX() + 1, block.getY() + 1, block.getZ() + 1);
					nblock.update();
					player.sendMessage("Casting Fireball 1");
				}
			}
		}
	}
	
	public void parseRightClick(Player player, Block block, Quester quester) {
		if (name.equals("Fireball")) {
			if (player.getItemInHand() == bindr) {
				if (quester.canCast(50)) {
					etc.getServer().setBlockAt(1, block.getX(), block.getY() + 1, block.getZ());
					player.sendMessage("Casting Fireball");
				}
			}
		}
	}
	
	public boolean isBound(int id) {
		return (bindl == id) || (bindr == id);
	}
	
	public void bindl(Player player, Item item) {
		bindl = item.getItemId();
		player.sendMessage(name + " is now bound to Left " + item.getItemId());
	}
	
	public void bindr(Player player, Item item) {
		bindr = item.getItemId();
		player.sendMessage(name + " is now bound to Right " + item.getItemId());
	}
	
	public void unbind(Player player) {
		bindl = 0;
		bindr = 0;
		player.sendMessage(name + " is now unbound");
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
	
	public void blockDestroy() {
		// TODO: write Ability.blockDestroy();
	}
}
