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

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.util.DNCList;
import de.cas_ual_ty.ydm.util.YdmIOUtil;
import net.minecraft.nbt.CompoundNBT;

public abstract class JsonCardInventoryManager
{
    protected volatile boolean isIdle;
    protected boolean loaded; // false = last safed, true = last loaded
    
    protected DNCList<CardHolder, CardHolderStack> stackList;
    
    protected List<CardHolder> cardsList;
    
    public JsonCardInventoryManager()
    {
        this.isIdle = true;
        this.loaded = false;
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
        YDM.log("Trying to load card inventory from file: " + this.getFile().getAbsolutePath());
        
        if(this.isSafed())
        {
            YDM.log("File will load!");
            
            this.isIdle = false;
            this.loaded = true;
            
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
                    index = this.stackList.getIndexOfSameKey(stack);
                    
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
                
                this.stackList.clear();
                
                YDM.log("Done loading from file: " + this.getFile().getAbsolutePath());
                
                this.isIdle = true;
                callback.run();
            });
            
            t.start();
        }
    }
    
    public void safe(Runnable callback)
    {
        YDM.log("Trying to save card inventory to file: " + this.getFile().getAbsolutePath());
        
        if(this.isLoaded())
        {
            YDM.log("File will safe!");
            
            this.isIdle = false;
            this.loaded = false;
            
            Thread t = new Thread(() ->
            {
                //                this.stackList.ensureExtraCapacity(this.getList().size());
                
                CardHolderStack stack;
                int index;
                
                for(CardHolder cardHolder : this.cardsList)
                {
                    stack = new CardHolderStack(cardHolder);
                    index = this.stackList.getIndexOfSameKey(stack);
                    
                    if(index == -1)
                    {
                        this.stackList.addKeepSorted(stack);
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
                this.stackList.clear();
                
                YDM.log("Done saving to file: " + this.getFile().getAbsolutePath());
                
                this.isIdle = true;
                callback.run();
            });
            
            t.start();
        }
    }
    
    public boolean isLoaded()
    {
        return this.isInIdleState() && this.loaded;
    }
    
    public boolean isSafed()
    {
        return this.isInIdleState() && !this.loaded;
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
            JsonArray a = new JsonArray();
            
            try
            {
                YdmIOUtil.writeJson(file, a);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
            return a;
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
