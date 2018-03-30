package org.atomicworkshop;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

//FIXME: (Steven) Look up how to do version constraints again.
@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class ConductorMod
{
    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        logger.info("I AM ALIVE!");
    }
}
