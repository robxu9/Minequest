package org.monksanctum.MineQuest.Quest.Instance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Logger;

import net.minecraft.server.CompressedStreamTools;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IChunkLoader;
import net.minecraft.server.IDataManager;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.PlayerFileData;
import net.minecraft.server.RegionFileCache;
import net.minecraft.server.WorldData;
import net.minecraft.server.WorldProvider;
import net.minecraft.server.WorldProviderHell;

/**
 * The complete idea for this system came from tehbeard.
 * 
 * @author jmonk
 *
 */
public class NewServerNBTManager implements PlayerFileData, IDataManager {

	private File instance;
	private String name;
	public NewServerNBTManager(File file, int instance, String s, boolean flag) {
        b = new File(file, s);
        b.mkdirs();
        c = new File(b, "players");
        d = new File(b, "data");
        d.mkdirs();
        if(flag)
        {
            c.mkdirs();
        }
        f();
		this.instance = new File(file, s + instance);
		this.name = s + instance;
	}

    public void e()
    {
        RegionFileCache.a();
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

    private void f()
    {
    }

    protected File a()
    {
        return b;
    }

    public WorldData c()
    {
        File file = new File(b, "level.dat");
        if(file.exists())
        {
            try
            {
                NBTTagCompound nbttagcompound = CompressedStreamTools.a(new FileInputStream(file));
                NBTTagCompound nbttagcompound2 = nbttagcompound.k("Data");
            	WorldData ret = new WorldData(nbttagcompound2);
            	
            	ret.name = name;
            	
            	return ret;
            }
            catch(Exception exception)
            {
                exception.printStackTrace();
            }
        }
        file = new File(b, "level.dat_old");
        if(file.exists())
        {
            try
            {
                NBTTagCompound nbttagcompound1 = CompressedStreamTools.a(new FileInputStream(file));
                NBTTagCompound nbttagcompound3 = nbttagcompound1.k("Data");
            	WorldData ret = new WorldData(nbttagcompound3);
            	
            	ret.name = name;
            	
            	return ret;
            }
            catch(Exception exception1)
            {
                exception1.printStackTrace();
            }
        }
        return null;
    }

    public void a(WorldData worlddata, @SuppressWarnings("rawtypes") List list)
    {
        worlddata.a(19132);
        NBTTagCompound nbttagcompound = worlddata.a(list);
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        nbttagcompound1.a("Data", nbttagcompound);
        try
        {
            File file = new File(b, "level.dat_new");
            File file1 = new File(b, "level.dat_old");
            File file2 = new File(b, "level.dat");
            CompressedStreamTools.a(nbttagcompound1, new FileOutputStream(file));
            if(file1.exists())
            {
                file1.delete();
            }
            file2.renameTo(file1);
            if(file2.exists())
            {
                file2.delete();
            }
            file.renameTo(file2);
            if(file.exists())
            {
                file.delete();
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public void a(WorldData worlddata)
    {
        NBTTagCompound nbttagcompound = worlddata.a();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        nbttagcompound1.a("Data", nbttagcompound);
        try
        {
            File file = new File(b, "level.dat_new");
            File file1 = new File(b, "level.dat_old");
            File file2 = new File(b, "level.dat");
            CompressedStreamTools.a(nbttagcompound1, new FileOutputStream(file));
            if(file1.exists())
            {
                file1.delete();
            }
            file2.renameTo(file1);
            if(file2.exists())
            {
                file2.delete();
            }
            file.renameTo(file2);
            if(file.exists())
            {
                file.delete();
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public void a(EntityHuman entityhuman)
    {
        try
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            entityhuman.d(nbttagcompound);
            File file = new File(c, "_tmp_.dat");
            File file1 = new File(c, (new StringBuilder()).append(entityhuman.name).append(".dat").toString());
            CompressedStreamTools.a(nbttagcompound, new FileOutputStream(file));
            if(file1.exists())
            {
                file1.delete();
            }
            file.renameTo(file1);
        }
        catch(Exception exception)
        {
            a.warning((new StringBuilder()).append("Failed to save player data for ").append(entityhuman.name).toString());
        }
    }

    public void b(EntityHuman entityhuman)
    {
        NBTTagCompound nbttagcompound = a(entityhuman.name);
        if(nbttagcompound != null)
        {
            entityhuman.e(nbttagcompound);
        }
    }

    public NBTTagCompound a(String s)
    {
        try
        {
            File file = new File(c, (new StringBuilder()).append(s).append(".dat").toString());
            if(file.exists())
            {
                return CompressedStreamTools.a(new FileInputStream(file));
            }
        }
        catch(Exception exception)
        {
            a.warning((new StringBuilder()).append("Failed to load player data for ").append(s).toString());
        }
        return null;
    }

    public PlayerFileData d()
    {
        return this;
    }

    public File b(String s)
    {
        return new File(d, (new StringBuilder()).append(s).append(".dat").toString());
    }


    private static final Logger a = Logger.getLogger("Minecraft");
    private final File b;
    private final File c;
    private final File d;

}
