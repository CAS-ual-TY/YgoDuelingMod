package de.cas_ual_ty.ydm.cardbinder;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.cardinventory.UUIDCardsManager;

import java.io.File;
import java.util.UUID;

public class CardBinderCardsManager extends UUIDCardsManager
{
    public CardBinderCardsManager()
    {
        super();
    }
    
    @Override
    protected File getFile()
    {
        generateUUIDIfNull();
        return CardBinderCardsManager.getBinderFile(getUUID());
    }
    
    public static File getBinderFile(UUID uuid)
    {
        return new File(YDM.bindersFolder, uuid.toString() + ".json");
    }
}
