package de.cas_ual_ty.ydm.cardinventory;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.task.Task;
import de.cas_ual_ty.ydm.task.TaskPriority;
import de.cas_ual_ty.ydm.task.TaskQueue;
import de.cas_ual_ty.ydm.util.DNCList;
import de.cas_ual_ty.ydm.util.YdmIOUtil;
import net.minecraft.nbt.CompoundTag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class JsonCardsManager
{
    public static List<JsonCardsManager> LOADED_MANAGERS = new LinkedList<>();
    
    protected volatile boolean isIdle;
    protected boolean loaded; // false = last safed, true = last loaded
    
    protected DNCList<CardHolder, CardHolderStack> stackList;
    
    protected List<CardHolder> cardsList;
    
    public JsonCardsManager()
    {
        isIdle = true;
        loaded = false;
        stackList = new DNCList<>((w) -> w.getKey(), CardHolderStack::compareCardHolders);
        cardsList = new ArrayList<>(0);
    }
    
    protected abstract File getFile();
    
    public List<CardHolder> getList()
    {
        if(isInIdleState())
        {
            return cardsList;
        }
        else
        {
            return ImmutableList.of();
        }
    }
    
    public List<CardHolder> forceGetList()
    {
        return cardsList;
    }
    
    public void load(Runnable callback)
    {
        if(YDM.commonConfig.logBinderIO.get())
        {
            YDM.log("Trying to load card inventory from file: " + getFile().getAbsolutePath());
        }
        
        if(isSafed())
        {
            if(YDM.commonConfig.logBinderIO.get())
            {
                YDM.log("File will load!");
            }
            
            setWorking();
            loaded = true;
            
            synchronized(JsonCardsManager.LOADED_MANAGERS)
            {
                JsonCardsManager.LOADED_MANAGERS.add(this);
            }
            
            Task t = new Task(TaskPriority.BINDER_LOAD, () ->
            {
                loadRunnable().run();
                if(YDM.commonConfig.logBinderIO.get())
                {
                    YDM.log("Done loading from file: " + getFile().getAbsolutePath());
                }
                setIdle();
                callback.run();
            });
            
            TaskQueue.addTask(t);
        }
    }
    
    public Runnable loadRunnable()
    {
        return () ->
        {
            JsonArray array = loadFromFile();
            JsonObject j;
            CardHolderStack stack;
            int index;
            
            int total = 0;
            
            for(JsonElement e : array)
            {
                j = e.getAsJsonObject();
                
                stack = new CardHolderStack(j);
                index = stackList.getIndexOfSameKey(stack);
                
                if(index == -1)
                {
                    stackList.add(stack);
                }
                else
                {
                    stackList.getByIndex(index).merge(stack);
                }
                
                total += stack.getCount();
            }
            
            cardsList = new ArrayList<>(total);
            
            for(CardHolderStack value : stackList)
            {
                for(index = 0; index < value.getCount(); ++index)
                {
                    cardsList.add(value.getKey());
                }
            }
            
            stackList.clear();
        };
    }
    
    public void safe(Runnable callback)
    {
        if(YDM.commonConfig.logBinderIO.get())
        {
            YDM.log("Trying to save card inventory to file: " + getFile().getAbsolutePath());
        }
        
        if(isLoaded())
        {
            if(YDM.commonConfig.logBinderIO.get())
            {
                YDM.log("File will safe!");
            }
            
            setWorking();
            loaded = false;
            
            synchronized(JsonCardsManager.LOADED_MANAGERS)
            {
                JsonCardsManager.LOADED_MANAGERS.remove(this);
            }
            
            Task t = new Task(TaskPriority.BINDER_SAVE, () ->
            {
                safeRunnable().run();
                if(YDM.commonConfig.logBinderIO.get())
                {
                    YDM.log("Done saving to file: " + getFile().getAbsolutePath());
                }
                setIdle();
                callback.run();
            });
            
            TaskQueue.addTask(t);
        }
    }
    
    public Runnable safeRunnable()
    {
        return () ->
        {
            CardHolderStack stack;
            int index;
            
            for(CardHolder cardHolder : cardsList)
            {
                stack = new CardHolderStack(cardHolder);
                index = stackList.getIndexOfSameKey(stack);
                
                if(index == -1)
                {
                    stackList.addKeepSorted(stack);
                }
                else
                {
                    stackList.getByIndex(index).merge(stack);
                }
            }
            
            JsonArray array = new JsonArray();
            JsonObject j;
            
            for(CardHolderStack value : stackList)
            {
                j = new JsonObject();
                value.writeToJson(j);
                array.add(j);
            }
            
            saveToFile(array);
            stackList.clear();
        };
    }
    
    public boolean isLoaded()
    {
        return isInIdleState() && loaded;
    }
    
    public boolean isSafed()
    {
        return isInIdleState() && !loaded;
    }
    
    public boolean isInIdleState()
    {
        return isIdle;
    }
    
    public void setIdle()
    {
        isIdle = true;
    }
    
    public void setWorking()
    {
        isIdle = false;
    }
    
    protected JsonArray loadFromFile()
    {
        File file = getFile();
        
        if(!file.exists())
        {
            JsonArray a = new JsonArray();
            
            try
            {
                YdmIOUtil.writeJson(file, a);
            }
            catch(IOException e)
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
            catch(JsonIOException | JsonSyntaxException | IOException e)
            {
                e.printStackTrace();
                return new JsonArray();
            }
        }
    }
    
    protected void saveToFile(JsonArray json)
    {
        File file = getFile();
        
        try
        {
            YdmIOUtil.writeJson(file, json);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public abstract void readFromNBT(CompoundTag nbt);
    
    public abstract void writeToNBT(CompoundTag nbt);
}
