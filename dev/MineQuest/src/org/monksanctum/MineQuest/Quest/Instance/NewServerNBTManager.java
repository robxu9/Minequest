package org.monksanctum.MineQuest.Quest.Instance;

import java.io.File;

import org.monksanctum.MineQuest.MineQuest;

import net.minecraft.server.IChunkLoader;
import net.minecraft.server.ServerNBTManager;
import net.minecraft.server.WorldData;
import net.minecraft.server.WorldProvider;
import net.minecraft.server.WorldProviderHell;

/**
 * The complete idea for this system came from tehbeard.
 * 
 * @author jmonk
 *
 */
public class NewServerNBTManager extends ServerNBTManager {

	private File instance;
	private String name;

	public NewServerNBTManager(File arg0, int instance, String arg1, boolean arg2) {
		super(arg0, arg1, arg2);
		this.instance = new File(arg0, arg1 + instance);
		this.name = arg1 + instance;
	}

    public IChunkLoader a(WorldProvider worldprovider)
    {
        File file = a();
        if(worldprovider instanceof WorldProviderHell)
        {
            File file1 = new File(file, "DIM-1");
            File file1_i = new File(instance, "DIM-1");
            file1.mkdirs();
            return new NewChunkRegionLoader(file1_i, file1);
        } else
        {
            return new NewChunkRegionLoader(instance, file);
        }
    }
    
    @Override
    public void b() {
    }
    
    @Override
    public WorldData c() {
    	WorldData ret = super.c();
    	
    	ret.name = name;
    	
    	return ret;
    }

}
