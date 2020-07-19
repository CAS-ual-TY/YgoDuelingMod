package de.cas_ual_ty.ydm.proxy;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.util.YDMResourcePackFinder;
import net.minecraft.client.Minecraft;

public class ClientProxy implements ISidedProxy
{
    @Override
    public void preInit()
    {
        Minecraft.getInstance().getResourcePackList().addPackFinder(new YDMResourcePackFinder(YDM.cardImagesFolder));
    }
}
