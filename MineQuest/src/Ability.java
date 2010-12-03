
public class Ability {
	private String name;
	@SuppressWarnings("unused")
	private boolean enabled;
	private int bindl;
	private int bindr;
	private SkillClass myclass;
	
	public Ability(String name, SkillClass myclass) {
		this.name = name;
		enabled = false;
		this.myclass = myclass;
	}
	
	public void bindl(Player player, Item item) {
		bindl = item.getItemId();
		player.sendMessage(name + " is now bound to Left " + item.getItemId());
	}
	
	public void bindr(Player player, Item item) {
		bindr = item.getItemId();
		player.sendMessage(name + " is now bound to Right " + item.getItemId());
	}
	
	public void blockDestroy() {
		// TODO: write Ability.blockDestroy();
	}
	
	public void disable() {
		// TODO: write Ability.disable();
	}
	
	public void enable() {
		// TODO: write Ability.enable();
	}
	
	public boolean equals(String name) {
		return name.equals(this.name);
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isBound(int id) {
		return (bindl == id) || (bindr == id);
	}
	
	public boolean isDefending(Player player, BaseEntity mob) {
		return false;
	}
	
	public boolean isEnabled() {
		// TODO: write Ability.isEnabled();
		return false;
	}
	
	public void parseAttack(Player player, LivingEntity defend, Quester quester) {
		useAbility(player, new Block(1, (int)defend.getX(), (int)defend.getY(), (int)defend.getZ()), quester, 1);
	}
	
	public int parseDefend(Player player, BaseEntity mob, int amount) {
		// TODO: write Ability.parseDefend(player, mob);
		return 0;
	}
	
	public void parseLeftClick(Player player, Block block, Quester quester) {
		useAbility(player, block, quester, 1);
	}
	
	public void parseRightClick(Player player, Block block, Quester quester) {
		useAbility(player, block, quester, 0);
	}
	
	public void unbind(Player player) {
		bindl = 0;
		bindr = 0;
		player.sendMessage(name + " is now unbound");
	}
	
	public void useAbility(Player player, Block block, Quester quester, int l) {
		if (name.equals("Fireball")) {
			if (player.getItemInHand() == ((l == 1)?bindl:bindr)) {
				if (quester.canCast(50)) {
					Block nblock = new Block(51);
					nblock.setX(block.getX());
					nblock.setY(block.getY());
					nblock.setZ(block.getZ());
					nblock.update();
					player.sendMessage("Created block at " + nblock.getX() + " " + nblock.getY() + " " + nblock.getZ());
					player.sendMessage("Created block at " + block.getX() + " " + block.getY() + " " + block.getZ());
					player.sendMessage("Casting Fireball 1");
					myclass.expAdd(15, quester);
				}
			}
		}
	}
}
