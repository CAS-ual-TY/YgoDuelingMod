package de.cas_ual_ty.ydm.duel.network;

import de.cas_ual_ty.ydm.duel.DuelManager;

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
