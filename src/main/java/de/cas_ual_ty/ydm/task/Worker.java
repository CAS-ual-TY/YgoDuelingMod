package de.cas_ual_ty.ydm.task;

import java.util.concurrent.TimeUnit;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.client.Minecraft;

public class Worker extends Thread
{
    public final int index;
    public final long sleepMillis;
    
    public Worker(String name, int index, long sleepMillis)
    {
        super(name);
        this.index = index;
        this.sleepMillis = sleepMillis;
    }
    
    @Override
    public void run()
    {
        Task t;
        
        while(Minecraft.getInstance().isRunning())
        {
            t = TaskQueue.pollTask();
            
            if(t != null)
            {
                try
                {
                    t.run();
                }
                catch (Exception e)
                {
                    YDM.log("Task failed!");
                    e.printStackTrace();
                }
            }
            else
            {
                try
                {
                    TimeUnit.MILLISECONDS.sleep(this.sleepMillis);
                }
                catch (InterruptedException e)
                {
                    YDM.log("Worker failed!");
                    e.printStackTrace();
                    WorkerManager.failedCallback(this.index);
                    return;
                }
            }
        }
    }
}
