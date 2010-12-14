import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;



public class MineQuestListener extends PluginListener {
    static private List<LivingEntity> entity_list;
    
    static private Quester questers[];
    
    static private List<LivingEntity> special_list;
	static private mysql_interface sql_server;
	public static void damageEntity(LivingEntity entity, int i) {
		int levelAdj = getAdjustment();
		
		if (levelAdj <= 0) {
			levelAdj = 1;
		}
		
		i /= levelAdj;
		
		entity.setHealth(entity.getHealth() - i);
	}
	public static int getAdjustment() {
		int i;
		int avgLevel = 0;
		for (i = 0; i < questers.length; i++) {
			avgLevel += questers[i].getLevel();
		}
		avgLevel /= questers.length;
		
		return (avgLevel / 10);
	}
	public static Quester getQuester(String name) {
		return lookupQuester(name);
	}
	static public void getQuesters() {
		int num, i;
		String names[];
		ResultSet results;
		try {
			results = sql_server.query("SELECT * FROM questers");
			
			results.last();
			num = results.getRow();
			results.first();
			
			questers = new Quester[num];
			names = new String[num];
			for (i = 0; i < num; i++) {
				names[i] = results.getString("name");
				results.next();
			}
			for (i = 0; i < num; i++) {
				questers[i] = new Quester(names[i], sql_server);
			}
		} catch (SQLException e) {
			System.out.println("Failed to get questers - things are not going to work");
			e.printStackTrace();
		}
	}

	public static List<LivingEntity> getSpecialList() {
    	return special_list;
    }
	
	private static Quester lookupQuester(String name) {
		int i;
		for (i = 0; i < questers.length; i++) {
			if (questers[i].getName().equals(name)) {
				return questers[i];
			}
		}
		System.out.println("No Quester Found");
		return null;
	}
	
	Random generator;
	
	@SuppressWarnings("unused")
	private Logger log;
	
	
	
	private PropertiesFile prop;
	
	private void deadSpecial(LivingEntity livingEntity) {
		// TODO: extra drops for special mobs
		return;
	}
	
	public String listSpellComps(String string) {
		
		if (string.equalsIgnoreCase("PowerStrike")) {
			return "Wooden Sword";
		} else if (string.equalsIgnoreCase("Dodge")) {
			return "5 Feathers";
		} else if (string.equalsIgnoreCase("Deathblow")) {
			return "2 Steel";
		} else if (string.equalsIgnoreCase("Sprint")) {
			return "Feather";
		} else if (string.equalsIgnoreCase("Fire Arrow")) {
			return "Coal";
		} else if (string.equalsIgnoreCase("Hail of Arrows")) {
			return "10 Arrows";
		} else if (string.equalsIgnoreCase("Repulsion")) {
			return "Torch + Cactus";
		} else if (string.equalsIgnoreCase("Fireball")) {
			return "Coal";
		} else if (string.equalsIgnoreCase("FireChain")) {
			return "5 Coal";
		} else if (string.equalsIgnoreCase("Wall of Fire")) {
			return "7 Dirt + 3 Coal";
		} else if (string.equalsIgnoreCase("Wall of Water")) {
			return "7 Dirt + 2 Water";
		} else if (string.equalsIgnoreCase("IceSphere")) {
			return "5 Snow";
		} else if (string.equalsIgnoreCase("Drain Life")) {
			return "Lightstone";
		} else if (string.equalsIgnoreCase("Fire Resistance")) {
			return "Netherstone";
		} else if (string.equalsIgnoreCase("Trap")) {
			return "6 Dirt + Shovel";
		} else if (string.equalsIgnoreCase("Heal")) {
			return "Water";
		} else if (string.equalsIgnoreCase("Heal Other")) {
			return "Water";
		} else if (string.equalsIgnoreCase("Heal Aura")) {
			return "2 Bread";
		} else if (string.equalsIgnoreCase("Damage Aura")) {
			return "Flint and Steel";
		}
		
		return "Unknown Ability";
	}
	
	public void onArmSwing(Player player) {
		lookupQuester(player.getName()).checkItemInHand(player);
		lookupQuester(player.getName()).checkItemInHandAbil(player);
	}
	
	public boolean onBlockDestroy(Player player, Block block) {
		return lookupQuester(player.getName()).destroyBlock(player, block);
	}
	
	@Override
	public boolean onBlockPlace(Player player, Block blockPlaced,
			Block blockClicked, Item itemInHand) {
		return lookupQuester(player.getName()).rightClick(player, blockClicked, itemInHand);
	}

	public void onBlockRightClicked(Player player, Block blockClicked, Item item) {
		lookupQuester(player.getName()).rightClick(player, blockClicked, item);
	}

	public boolean onCommand(Player player, String[] split) {
		if (split[0].equals("/char")) {
			Quester quester = lookupQuester(player.getName());
			player.sendMessage("You are level " + quester.getLevel() + " with " + quester.getExp() + "/" + (400 * (quester.getLevel() + 1)) + " Exp");
			
			return true;
		} else if (split[0].equals("/minequest")) {
			player.sendMessage("Minequest Commands:");
			player.sendMessage("    /save - save progress of character");
			player.sendMessage("    /load - load progress - removing unsaved experience/levels");
			player.sendMessage("    /quest - enable minequest for your character (enabled by default)");
			player.sendMessage("    /noquest - disable minequest for your character");
			player.sendMessage("    /char - information about your character level");
			player.sendMessage("    /class <classname> - information about a specific class");
			player.sendMessage("    /health - display your health");
			player.sendMessage("    /abillist [classname] - display all abilities or for a specific class");
			player.sendMessage("    /enableabil <ability name> - enable an ability (enabled by default)");
			player.sendMessage("    /disableabil <ability name> - disable an ability");
			player.sendMessage("    /bind <ability name> <l or r> - bind an ability to current item");
			player.sendMessage("    /unbind - unbind current item from all abilities");
			player.sendMessage("    /spellcomp <ability name> - list the components required for an ability");
			return true;
		} else if (split[0].equals("/save")) {
			lookupQuester(player.getName()).save();
			return true;
		} else if (split[0].equals("/load")) {
			lookupQuester(player.getName()).update();
			return true;
		} else if (split[0].equals("/quest")) {
			lookupQuester(player.getName()).enable();
			return true;
		} else if (split[0].equals("/zombie")) {
			Mob mymob = new Mob("Zombie", new Location(player.getX() + 3, player.getY(), player.getZ()));
			mymob.spawn();
			return true;
		} else if (split[0].equals("/abillist")) {
			if (split.length < 2) {
				lookupQuester(player.getName()).listAbil(player);
			} else {
				lookupQuester(player.getName()).getClass(split[1]);
			}
			return true;
		} else if (split[0].equals("/unbind")) {
			lookupQuester(player.getName()).unBind(player.getItemInHand());
			return true;
		} else if (split[0].equals("/enableabil")) {
			if (split.length < 2) return false;
			String abil = split[1];
			int i;
			for (i = 2; i < split.length; i++) abil = abil + " " + split[i];
			lookupQuester(player.getName()).enableabil(abil);
			return true;
		} else if (split[0].equals("/disableabil")) {
			if (split.length < 2) return false;
			String abil = split[1];
			int i;
			for (i = 2; i < split.length; i++) abil = abil + " " + split[i];
			lookupQuester(player.getName()).disableabil(abil);
			return true;
		} else if (split[0].equals("/noquest")) {
			lookupQuester(player.getName()).disable();
			return true;
		} else if (split[0].equals("/bind")) {
			if (split.length < 3) {
				player.sendMessage("Usage: /bind <ability> <l or r>");
				return true;
			}
			String abil = split[1];
			int i;
			for (i = 2; i < split.length - 1; i++) abil = abil + " " + split[i];
			lookupQuester(player.getName()).bind(player, abil, split[split.length - 1]);
			return true;
		} else if (split[0].equals("/class")) {
			if (split.length < 2) {
				player.sendMessage("Usage: /class <class_name>");
				return true;
			}
			lookupQuester(player.getName()).getClass(split[1]).display(player);
			return true;
		} else if (split[0].equals("/health")) {
			player.sendMessage("Your health is " + lookupQuester(player.getName()).getHealth() + "/" + lookupQuester(player.getName()).getMaxHealth());
			return true;
		} else if (split[0].equals("/spellcomp")) {
			if (split.length < 2) {
				return false;
			}
			String abil = split[1];
			int i;
			for (i = 2; i < split.length; i++) abil = abil + " " + split[i];
			player.sendMessage(listSpellComps(abil));
			return true;
		} else if (split[0].equals("/entities")) {
			List<LivingEntity> entity_list = etc.getServer().getLivingEntityList();
			int i;
			int zombie = 0;
			int creeper = 0;
			int spider = 0;
			int skeleton = 0;
			for (i = 0; i < entity_list.size(); i++) {
				if (entity_list.get(i).getName().equals("Spider")) {
					spider++;
				} else if (entity_list.get(i).getName().equals("Skeleton")) {
					skeleton++;
				} else if (entity_list.get(i).getName().equals("Zombie")) {
					zombie++;
				} else if (entity_list.get(i).getName().equals("Creeper")) {
					creeper++;
				} else if (entity_list.get(i).isMob()) {
					player.sendMessage(entity_list.get(i).getName() + " is a mob");
				}
			}
			player.sendMessage("There are " + creeper + " Creepers " + zombie + " Zombies " + skeleton + " Skeletons and " + spider + " Spiders of " + entity_list.size());
			
			return true;
		}
		return false;
	}
	
	
	
	public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker,
			BaseEntity defender, int amount) {
		int attack = 1;
		int defend = 0;
		

		if (((type == PluginLoader.DamageType.FIRE) || (type == PluginLoader.DamageType.FIRE_TICK)) && defender.isPlayer()) {
			Player player = defender.getPlayer();
			lookupQuester(player.getName()).parseFire(type, amount);
		} else if (type == PluginLoader.DamageType.CREEPER_EXPLOSION) {
			Player player = defender.getPlayer();
			lookupQuester(player.getName()).parseExplosion(attacker, player, amount);
		}
		if (type != PluginLoader.DamageType.ENTITY) {
			return false;
		}
		
		if ((attacker.getPlayer() == null) && (defender.getPlayer() != null)) {
			attack = 0;
			defend = 1;
		}
		
		if (attack == 1) {
			Player player = attacker.getPlayer();
			return lookupQuester(player.getName()).attack(player, defender, amount);
		}

		if (defend == 1) {
			Player player = defender.getPlayer();
			lookupQuester(player.getName()).defend(player, attacker, amount);
			return false;
		}
		
		return false;
	}
	
	public void onDisconnect(Player player) {
		lookupQuester(player.getName()).save();
		player.setHealth(20);
	}

	public boolean onEquipmentChange(Player player) {
		lookupQuester(player.getName()).checkEquip(player);
		return super.onEquipmentChange(player);
	}


	
	public boolean onHealthChange(Player player, int oldValue, int newValue) {
		if (lookupQuester(player.getName()).isEnabled()) {
			lookupQuester(player.getName()).healthChange(player, oldValue, newValue);
			return false;
		}
		return false;
	}

	public void onLogin(Player player) {
		Quester new_questers[];
		int i;
		
		
		if (lookupQuester(player.getName()) == null) {
			
			new_questers = new Quester[questers.length + 1];
			for (i = 0; i < questers.length; i++) {
				new_questers[i] = questers[i];
			}
			new_questers[questers.length] = new Quester(player.getName(), 0, sql_server);
			questers = new_questers;
		} else {
			lookupQuester(player.getName()).update();
		}
	}

	public void onPlayerMove(Player player, Location from, Location to) {
		List<LivingEntity> list = etc.getServer().getLivingEntityList();
		List<LivingEntity> remove_list = new ArrayList<LivingEntity>();
		int i;
		
		for (i = 0; i < list.size(); i++) {
			if (!listContains(entity_list, list.get(i))) {
				entity_list.add(list.get(i));
				if (generator.nextDouble() < (getAdjustment() / 300.0)) {
					special_list.add(list.get(i));
				}
			}
		}
		
		for (i = 0; i < special_list.size(); i++) {
			LivingEntity mob = special_list.get(i);
			Block nblock = etc.getServer().getBlockAt((int)mob.getX(), Ability.getNearestY((int)mob.getX(), (int)mob.getY() - 5, (int)mob.getZ()) - 1, (int)mob.getZ());
			if (etc.getServer().getBlockAt(nblock.getX(), nblock.getY() + 1, nblock.getZ()).getType() == 78) {
				nblock = etc.getServer().getBlockAt(nblock.getX(), nblock.getY() + 1, nblock.getZ());
				nblock.setType(0);
				nblock.update();
				nblock = etc.getServer().getBlockAt(nblock.getX(), nblock.getY() - 1, nblock.getZ());
			}
			if ((nblock.getType() != 9) && (nblock.getType() != 8) && (nblock.getType() != 10)) { // can't walk across liquid
				nblock.setType(3);
				nblock.update();
			}
			if (special_list.get(i).getHealth() < 0) {
				remove_list.add(special_list.get(i));
			}
		}
		for (i = 0; i < remove_list.size(); i++) {
			deadSpecial(remove_list.get(i));
			special_list.remove(remove_list.get(i));
		}
	}

	private boolean listContains(List<LivingEntity> entityList,
			LivingEntity livingEntity) {
		int i;
		
		for (i = 0; i < entityList.size(); i++) {
			if (entityList.get(i).getId() == livingEntity.getId()) {
				return true;
			}
		}
		
		return false;
	}
	public void setup() {
		String url, port, db, user, pass;
		
        log = Logger.getLogger("Minecraft");

		prop = new PropertiesFile("minequest.properties");
		url = prop.getString("url", "localhost");
		port = prop.getString("port", "3306");
		db = prop.getString("db", "cubonomy");
		user = prop.getString("user", "root");
		pass = prop.getString("pass", "root");
		sql_server = new mysql_interface();
		sql_server.setup(url, port, db, user, pass);
		
		getQuesters();
		generator = new Random();
		entity_list = new ArrayList<LivingEntity>();
		special_list = new ArrayList<LivingEntity>();
	}
}
