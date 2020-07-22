package de.cas_ual_ty.ydm.cardinventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.util.DNCList;
import de.cas_ual_ty.ydm.util.YdmIOUtil;
import net.minecraft.nbt.CompoundNBT;

public abstract class JsonCardInventoryManager
{
    private volatile boolean isIdle;
    
    protected DNCList<CardHolder, CardHolderStack> stackList;
    
    protected List<CardHolder> cardsList;
    
    public JsonCardInventoryManager()
    {
        this.isIdle = true;
        this.stackList = new DNCList<>((w) -> w.getKey(), CardHolderStack::compareCardHolders);
        this.cardsList = new ArrayList<>(0);
    }
    
    protected abstract File getFile();
    
    public List<CardHolder> getList()
    {
        if(this.isInIdleState())
        {
            return this.cardsList;
        }
        else
        {
            return ImmutableList.of();
        }
    }
    
    public void load(Runnable callback)
    {
        if(this.isInIdleState())
        {
            this.isIdle = false;
            
            Thread t = new Thread(() ->
            {
                
                JsonArray array = this.loadFromFile();
                JsonObject j;
                CardHolderStack stack;
                int index;
                
                int total = 0;
                
                for(JsonElement e : array)
                {
                    j = e.getAsJsonObject();
                    
                    stack = new CardHolderStack(j);
                    index = this.stackList.getIndexOf(stack);
                    
                    if(index == -1)
                    {
                        this.stackList.add(stack);
                    }
                    else
                    {
                        this.stackList.getByIndex(index).merge(stack);
                    }
                    
                    total += stack.getCount();
                }
                
                this.cardsList = new ArrayList<>(total);
                
                for(CardHolderStack value : this.stackList)
                {
                    for(index = 0; index < value.getCount(); ++index)
                    {
                        this.cardsList.add(value.getKey());
                    }
                }
                
                this.isIdle = true;
                callback.run();
                
            });
            
            t.start();
        }
    }
    
    public void safe(Runnable callback)
    {
        if(this.isInIdleState())
        {
            this.isIdle = false;
            
            Thread t = new Thread(() ->
            {
                
                this.stackList.clear();
                
                this.stackList.ensureExtraCapacity(this.getList().size());
                
                CardHolderStack stack;
                int index;
                
                for(CardHolder cardHolder : this.cardsList)
                {
                    stack = new CardHolderStack(cardHolder);
                    index = this.stackList.getIndexOf(stack);
                    
                    if(index == -1)
                    {
                        this.stackList.add(stack);
                    }
                    else
                    {
                        this.stackList.getByIndex(index).merge(stack);
                    }
                }
                
                JsonArray array = new JsonArray();
                JsonObject j;
                
                for(CardHolderStack value : this.stackList)
                {
                    j = new JsonObject();
                    value.writeToJson(j);
                    array.add(j);
                }
                
                this.saveToFile(array);
                
                this.isIdle = true;
                callback.run();
                
            });
            
            t.start();
        }
    }
    
    public boolean isLoaded()
    {
        return false;
    }
    
    public boolean isInIdleState()
    {
        return this.isIdle;
    }
    
    protected JsonArray loadFromFile()
    {
        File file = this.getFile();
        
        if(!file.exists())
        {
            return new JsonArray();
        }
        else
        {
            try
            {
                return YdmIOUtil.parseJsonFile(file).getAsJsonArray();
            }
            catch (JsonIOException | JsonSyntaxException | FileNotFoundException e)
            {
                e.printStackTrace();
                return new JsonArray();
            }
        }
    }
    
    protected void saveToFile(JsonArray json)
    {
        File file = this.getFile();
        
        try
        {
            YdmIOUtil.writeJson(file, json);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public abstract void readFromNBT(CompoundNBT nbt);
    
    public abstract void writeToNBT(CompoundNBT nbt);
}
