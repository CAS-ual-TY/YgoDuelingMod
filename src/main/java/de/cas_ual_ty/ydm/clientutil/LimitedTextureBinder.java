package de.cas_ual_ty.ydm.clientutil;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Iterator;
import java.util.LinkedList;

public class LimitedTextureBinder
{
    private final Minecraft mc;
    public final int size;
    private final LinkedList<ResourceLocation> list;
    
    public LimitedTextureBinder(Minecraft minecraft, int size)
    {
        mc = minecraft;
        this.size = size;
        list = new LinkedList<>();
    }
    
    public void bind(ResourceLocation rl)
    {
        // if the list isnt full, just add the new rl
        if(list.size() >= size)
        {
            // list is already full
            // if its already in, remove it (itll be re-added in front)
            // else remove the last one
            
            Iterator<ResourceLocation> it = list.iterator();
            boolean found = false;
            
            while(it.hasNext())
            {
                if(rl.equals(it.next()))
                {
                    // its already in, remove it
                    it.remove();
                    found = true;
                    break;
                }
            }
            
            // wasnt in, remove the last one
            if(!found)
            {
                // size must be >= 1, so this can only be called if the list size is >= size >= 1
                unbindTexture(list.removeLast());
            }
        }
        
        // (re-)add it in front
        list.addFirst(rl);
        bindTexture(rl);
    }
    
    private void bindTexture(ResourceLocation rl)
    {
        mc.getTextureManager().bind(rl);
    }
    
    private void unbindTexture(ResourceLocation rl)
    {
        mc.getTextureManager().release(rl);
    }
}
