package de.cas_ual_ty.ydm.proxy;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.util.YDMResourcePackFinder;
import de.cas_ual_ty.ydm.util.YDMUtil;
import net.minecraft.client.Minecraft;

public class ClientProxy implements ISidedProxy
{
    @Override
    public void setup()
    {
        Minecraft.getInstance().getResourcePackList().addPackFinder(new YDMResourcePackFinder(YDM.cardImagesFolder, YDMUtil.PNG_FILTER));
    }
}
