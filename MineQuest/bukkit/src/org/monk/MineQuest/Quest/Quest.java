package org.monk.MineQuest.Quest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.AreaEvent;
import org.monk.MineQuest.Event.ArrowEvent;
import org.monk.MineQuest.Event.BlockCDEvent;
import org.monk.MineQuest.Event.BlockDCEvent;
import org.monk.MineQuest.Event.BlockEvent;
import org.monk.MineQuest.Event.EntitySpawnerCompleteEvent;
import org.monk.MineQuest.Event.EntitySpawnerCompleteNMEvent;
import org.monk.MineQuest.Event.EntitySpawnerEvent;
import org.monk.MineQuest.Event.EntitySpawnerNoMove;
import org.monk.MineQuest.Event.Event;
import org.monk.MineQuest.Event.ExperienceAdd;
import org.monk.MineQuest.Event.HealthEntitySpawn;
import org.monk.MineQuest.Event.LockWorldTime;
import org.monk.MineQuest.Event.MessageEvent;
import org.monk.MineQuest.Event.NormalEvent;
import org.monk.MineQuest.Event.PartyHealthEvent;
import org.monk.MineQuest.Event.QuestEvent;
import org.monk.MineQuest.Event.SingleAreaEvent;
import org.monk.MineQuest.Quester.Quester;

public class Quest {
	private Quester questers[];
	private List<QuestTask> tasks;
	private List<Event> events;
	private Location spawn;
	private Party party;
	private World world;
	private Location[] exceptions;
	private int[] triggers;
	
	public Quest(String filename, Party party) {
		this.questers = party.getQuesterArray();
		this.party = party;
		tasks = new ArrayList<QuestTask>();
		events = new ArrayList<Event>();
		exceptions = new Location[0];
		triggers = new int[0];

		try {
			BufferedReader bis = new BufferedReader(new FileReader(filename + ".quest"));
			
			String line = "";
			int number = 0;
			world = questers[0].getPlayer().getWorld();
			spawn = null;
			try {
				while ((line = bis.readLine()) != null) {
					number++;
					String split[] = line.split(":");
					if (split[0].equals("Event")) {
						createEvent(split);
					} else if (split[0].equals("Task")) {
						createTask(split, false);
					} else if (split[0].equals("RepeatingTask")) {
						createTask(split, true);
					} else if (split[0].equals("World")) {
						World world = null;
						if (MineQuest.getSServer().getWorld(split[1]) == null) {
							if ((split.length == 2) || (split[2].equals("NORMAL"))) {
								world = MineQuest.getSServer().createWorld(split[1], Environment.NORMAL);
							} else {
								world = MineQuest.getSServer().createWorld(split[1], Environment.NETHER);
							}
						}
						
						teleport(party.getQuesterArray(), world);
					} else if (split[0].equals("LoadWorld")) {
						deleteDir(new File(split[1]));
						copyDirectory(new File(split[2]), new File(split[1]));
						world = null;
						if (MineQuest.getSServer().getWorld(split[1]) == null) {
							if ((split.length == 3) || (split[3].equals("NORMAL"))) {
								world = MineQuest.getSServer().createWorld(split[1], Environment.NORMAL);
							} else {
								world = MineQuest.getSServer().createWorld(split[1], Environment.NETHER);
							}
						}
					} else if (split[0].equals("Spawn")) {
						MineQuest.log("Loaded Spawn");
						double x = Double.parseDouble(split[1]);
						double y = Double.parseDouble(split[2]);
						double z = Double.parseDouble(split[3]);
						this.spawn = new Location(world, x, y, z);
					}
				}
			} catch (Exception e) {
				MineQuest.log("Unable to load Quest Problem on Line " + number);
				MineQuest.log("  " + line);
				try {
					issueNextEvents(-1);
				} catch (Exception e1) {
					MineQuest.log("Unable to unload events properly");
				}
				return;
			}
			if (spawn == null) {
				MineQuest.log("No Spawn Found");
				spawn = world.getSpawnLocation();
			}
			
			for (QuestTask task : tasks) {
				MineQuest.log("Task: " + task.getId());
				for (Event event : task.getEvents()) {
					MineQuest.log(event.getName());
				}
			}
			
			for (Quester quester : party.getQuesters()) {
				quester.setQuest(this, world);
			}
			
			MineQuest.getEventParser().addEvent(new QuestEvent(this, 100, 0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void removeQuester(Quester quester) {
		party.remQuester(quester);
		
		quester.clearQuest();
	}
	
	public Location getSpawn() {
		return spawn;
	}
	
	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    // The directory is now empty so delete it
	    return dir.delete();
	}
	
	// If targetLocation does not exist, it will be created.
    public void copyDirectory(File sourceLocation , File targetLocation)
    throws IOException {
        
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            
            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }
	
	private void teleport(Quester[] questers, World world) {
		if (world == null) {
			MineQuest.log("Null World!!");
			return;
		}
		for (Quester quester : questers) {
			Location location = quester.getPlayer().getLocation();
			location.setWorld(world);
			quester.getPlayer().teleportTo(location);
		}
	}

	public void createTask(String line[], boolean repeating) throws Exception {
		int id = Integer.parseInt(line[1]);
		Event[] events;
		if (line.length == 3) {
			events = new Event[line[2].split(",").length];
			int i = 0;
			
			for (String event : line[2].split(",")) {
				if (getEvent(Integer.parseInt(event)) == null) {
					MineQuest.log("Problem Getting Event: " + event);
					throw new Exception();
				}
				events[i++] = getEvent(Integer.parseInt(event));
			}
		} else {
			events = new Event[1];
			
			events[0] = new NormalEvent(0);
		}
		
		if (repeating) {
			tasks.add(new RepeatingQuestTask(events, id));
		} else {
			tasks.add(new QuestTask(events, id));
		}
	}
	
	public void createEvent(String line[]) throws Exception {
		int id = Integer.parseInt(line[1]);
		String type = line[2];
		Event new_event;
		LivingEntity entities[] = new LivingEntity[questers.length];
		int i = 0;
		for (Quester quester : questers) {
			entities[i++] = quester.getPlayer();
		}
		
		if (type.equals("AreaEvent")) {
			int delay = Integer.parseInt(line[3]);
			int index = Integer.parseInt(line[4]);
			
			Location loc = new Location(world, Integer.parseInt(line[5]), Integer.parseInt(line[6]), Integer.parseInt(line[7]));
			int radius = Integer.parseInt(line[8]);
			new_event = new AreaEvent(this, delay, index, party, loc, radius);
		} else if (type.equals("SingleAreaEvent")) {
			int delay = Integer.parseInt(line[3]);
			int index = Integer.parseInt(line[4]);
			
			Location loc = new Location(world, Integer.parseInt(line[5]), Integer.parseInt(line[6]), Integer.parseInt(line[7]));
			int radius = Integer.parseInt(line[8]);
			new_event = new SingleAreaEvent(this, delay, index, party, loc, radius);
		} else if (type.equals("MessageEvent")) {
			int delay = Integer.parseInt(line[3]);

			new_event = new MessageEvent(delay, party, line[4]);
		} else if (type.equals("BlockEvent")) {
			int delay = Integer.parseInt(line[3]);

			Block block = world.getBlockAt(Integer.parseInt(line[4]), Integer.parseInt(line[5]), Integer.parseInt(line[6]));
			
			int mat = Integer.parseInt(line[7]);
			
			new_event = new BlockEvent(delay, block, Material.getMaterial(mat));
		} else if (type.equals("QuestEvent")) {
			int delay = Integer.parseInt(line[3]);
			int index = Integer.parseInt(line[4]);
			
			new_event = new QuestEvent(this, delay, index);
		} else if (type.equals("EntitySpawnerEvent")) {
			int delay = Integer.parseInt(line[3]);
			String creature = line[7];
			Location location = new Location(world, Integer.parseInt(line[4]), Integer.parseInt(line[5]), Integer.parseInt(line[6]));
			boolean superm;
			if (line[8].equals("f")) {
				superm = false;
			} else {
				superm = true;
			}
			new_event = new EntitySpawnerEvent(delay, location, CreatureType.fromName(creature), superm);
		} else if (type.equals("EntitySpawnerNoMove")) {
			int delay = Integer.parseInt(line[3]);
			String creature = line[7];
			Location location = new Location(world, Integer.parseInt(line[4]), Integer.parseInt(line[5]), Integer.parseInt(line[6]));
			boolean superm;
			if (line[8].equals("f")) {
				superm = false;
			} else {
				superm = true;
			}
			new_event = new EntitySpawnerNoMove(delay, location, CreatureType.fromName(creature), superm);			
		} else if (type.equals("EntitySpawnerCompleteNMEvent")) {
			long delay = Integer.parseInt(line[3]);
			int index = Integer.parseInt(line[4]);
			i = 0;
			EntitySpawnerEvent[] eventss = new EntitySpawnerEvent[line[5].split(",").length];
			for (String s : line[5].split(",")) {
				if (getEvent(Integer.parseInt(s)) == null) {
					MineQuest.log("Cannot Find Event: " + Integer.parseInt(s));
					throw new Exception();
				}
				eventss[i++] = (EntitySpawnerEvent)getEvent(Integer.parseInt(s));
			}

			new_event = new EntitySpawnerCompleteNMEvent(this, delay, index, eventss);
		} else if (type.equals("EntitySpawnerCompleteEvent")) {
			int delay = Integer.parseInt(line[3]);
			i = 0;
			EntitySpawnerEvent[] eventss = new EntitySpawnerEvent[line[4].split(",").length];
			for (String s : line[5].split(",")) {
				if (getEvent(Integer.parseInt(s)) == null) {
					MineQuest.log("Cannot Find Event: " + Integer.parseInt(s));
					throw new Exception();
				}
				eventss[i++] = (EntitySpawnerEvent)getEvent(Integer.parseInt(s));
			}

			new_event = new EntitySpawnerCompleteEvent(delay, eventss);
		} else if (type.equals("ExperienceAdd")) {
			long delay = Integer.parseInt(line[3]);
			int exp = Integer.parseInt(line[5]);
			int class_exp = Integer.parseInt(line[6]);
			if (!line[4].equals("all")) {
				MineQuest.log("Warning: Options other than all are not supported for ExperienceAdd");
			}
			
			new_event = new ExperienceAdd(delay, party, exp, class_exp);
		} else if (type.equals("LockWorldTime")) {
			long delay = Integer.parseInt(line[3]);
			long time = Integer.parseInt(line[5]);
			long time_2 = Integer.parseInt(line[6]);
			
			new_event = new LockWorldTime(delay, world, time, time_2);
		} else if (type.equals("BlockCDEvent")) {
			long delay = Integer.parseInt(line[3]);
			long second_delay = Integer.parseInt(line[4]);
			Location location = new Location(world,
					Integer.parseInt(line[5]), Integer.parseInt(line[6]), Integer.parseInt(line[7]));
			Block block = world.getBlockAt(location);
			int idd = Integer.parseInt(line[8]);
			
			new_event = new BlockCDEvent(delay, second_delay, block, Material.getMaterial(idd));
		} else if (type.equals("BlockDCEvent")) {
			long delay = Integer.parseInt(line[3]);
			long second_delay = Integer.parseInt(line[4]);
			Location location = new Location(world,
					Integer.parseInt(line[5]), Integer.parseInt(line[6]), Integer.parseInt(line[7]));
			Block block = world.getBlockAt(location);
			int idd = Integer.parseInt(line[8]);
			
			new_event = new BlockDCEvent(delay, second_delay, block, Material.getMaterial(idd));
		} else if (type.equals("ArrowEvent")) {
			long delay = Integer.parseInt(line[3]);
			Location start = new Location(world,
					Double.parseDouble(line[4]),
					Double.parseDouble(line[5]),
					Double.parseDouble(line[6])
					);
			Vector vector = new Vector(
					Double.parseDouble(line[7]),
					Double.parseDouble(line[8]),
					Double.parseDouble(line[9])
					);
			
			new_event = new ArrowEvent(delay, start, vector);
		} else if (type.equals("CanEdit")) {
			Location new_loc = new Location(world,
					Integer.parseInt(line[3]),
					Integer.parseInt(line[4]),
					Integer.parseInt(line[5])
					);
			int next_task = Integer.parseInt(line[6]);
			
			Location new_locs[] = new Location[exceptions.length + 1];
			int new_tasks[] = new int[triggers.length + 1];
			for (i = 0; i < exceptions.length; i++) {
				new_locs[i] = exceptions[i];
				new_tasks[i] = triggers[i];
			}
			new_locs[i] = new_loc;
			new_tasks[i] = next_task;
			
			exceptions = new_locs;
			triggers = new_tasks;

//			MineQuest.log("Added CanEdit");
			return;
		} else if (type.equals("PartyHealthEvent")) {
			long delay = Integer.parseInt(line[3]);
			double percent = Double.parseDouble(line[4]);
			
			new_event = new PartyHealthEvent(delay, party, percent);
		} else if (type.equals("HealthEntitySpawn")) {
			long delay;
			int task;
			Location location;
			int health;
			CreatureType creature;
			try {
				creature = CreatureType.fromName(line[8]);
				delay = Integer.parseInt(line[3]);
				task = Integer.parseInt(line[4]);
				location = new Location(world,
						Double.parseDouble(line[5]),
						Double.parseDouble(line[6]),
						Double.parseDouble(line[7])
						);
				health = Integer.parseInt(line[9]);
			} catch (Exception e2) {
				MineQuest.log("Problem getting HealthEntitySpawner Parameters");
				throw new Exception();
			}
			boolean stay = true;
			if (line[8].equals("f")) {
				stay = false;
			}
			 
			new_event = new HealthEntitySpawn(this, delay, task, location, creature, health, stay);
		} else {
			MineQuest.log("Unknown Event Type: " + type);
			throw new Exception();
		}
		
		
//		MineQuest.log("Added " + events.get(events.size() - 1).getName());
		new_event.setId(id);
		events.add(new_event);
	}
	
	private Event getEvent(int id) {
		for (Event event : events) {
			if (event.getId() == id) {
				return event;
			}
		}
		
		return null;
	}

	public void issueNextEvents(int index) {
		if (index == -1) {
			for (Quester quester : questers) {
				quester.clearQuest();
			}
			
			for (QuestTask task : tasks) {
				task.clearEvents();
			}
			return;
		} else if (index <= -2) {
			return;
		}
		
		for (QuestTask task : tasks) {
			if (task.getId() == index) {
				task.issueEvents();
			}
		}
	}

	public boolean canEdit(Quester quester, Block block) {
		int i;
		for (i = 0; i < exceptions.length; i++) {
			if (quester.isDebug()) {
				quester.sendMessage("Checking Trigger " + triggers[i]);
			}
			if (equals(block.getLocation(), exceptions[i])) {
				if (triggers[i] >= -1) {
					issueNextEvents(triggers[i]);
				}
				return false;
			}
		}

		quester.sendMessage("A Mystical Force is keeping you from Modifying the world!");
		
		return true;
	}

	private boolean equals(Location location, Location location2) {
		if (((int)location.getX()) != ((int)location2.getX())) {
			return false;
		}
		if (((int)location.getY()) != ((int)location2.getY())) {
			return false;
		}
		if (((int)location.getZ()) != ((int)location2.getZ())) {
			return false;
		}
		
		return true;
	}
}
