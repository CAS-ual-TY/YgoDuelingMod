package de.cas_ual_ty.ydm.duelmanager;

public class DuelRenderer
{
    public final DuelRenderingProvider provider;
    public final DuelManager manager;
    
    public DuelRenderer(DuelRenderingProvider provider, DuelManager manager)
    {
        this.provider = provider;
        this.manager = manager;
    }
    
    // must be called every render tick
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        
    }
}
