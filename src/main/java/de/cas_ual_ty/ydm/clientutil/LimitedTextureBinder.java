package de.cas_ual_ty.ydm.clientutil;

import java.util.Iterator;
import java.util.LinkedList;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class LimitedTextureBinder
{
    private final Minecraft mc;
    public final int size;
    private final LinkedList<ResourceLocation> list;
    
    public LimitedTextureBinder(Minecraft minecraft, int size)
    {
        this.mc = minecraft;
        this.size = size;
        this.list = new LinkedList<>();
    }
    
    public void bind(ResourceLocation rl)
    {
        // if the list isnt full, just add the new rl
        if(this.list.size() >= this.size)
        {
            // list is already full
            // if its already in, remove it (itll be re-added in front)
            // else remove the last one
            
            Iterator<ResourceLocation> it = this.list.iterator();
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
                this.unbindTexture(this.list.removeLast());
            }
        }
        
        // (re-)add it in front
        this.list.addFirst(rl);
        this.bindTexture(rl);
    }
    
    private void bindTexture(ResourceLocation rl)
    {
        this.mc.getTextureManager().bindTexture(rl);
    }
    
    private void unbindTexture(ResourceLocation rl)
    {
        this.mc.getTextureManager().deleteTexture(rl);
    }
}
