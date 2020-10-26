package de.cas_ual_ty.ydm.duel;

import de.cas_ual_ty.ydm.duelmanager.DuelManager;

public class ServerDuelManagerProvider implements IDuelManagerProvider
{
    protected DuelManager duelManager;
    
    public ServerDuelManagerProvider(DuelManager duelManager)
    {
        this.duelManager = duelManager;
    }
    
    @Override
    public DuelManager getDuelManager()
    {
        return this.duelManager;
    }
}
