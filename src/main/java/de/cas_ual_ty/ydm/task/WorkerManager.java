package de.cas_ual_ty.ydm.task;

import java.util.concurrent.TimeUnit;

import de.cas_ual_ty.ydm.YDM;

public class WorkerManager
{
    private static Worker[] WORKERS;
    private static long sleepMillis = 100; //TODO configurable sleep
    
    public static void init()
    {
        YDM.log("Worker init");
        
        WorkerManager.WORKERS = new Worker[4]; //TODO configurable size
        
        for(int i = 0; i < WorkerManager.WORKERS.length; ++i)
        {
            WorkerManager.initWorker(i);
        }
        
        Thread shutdownListener = new Thread(() ->
        {
            int everythingDone = 0;
            
            for(;;)
            {
                everythingDone++;
                
                for(Worker w : WorkerManager.WORKERS)
                {
                    if(w.isAlive() && w.isWorking)
                    {
                        everythingDone = 0;
                        break;
                    }
                }
                
                // we do this check 3 times
                // on 3rd time, we finish
                if(everythingDone >= 3)
                {
                    break;
                }
                else
                {
                    try
                    {
                        TimeUnit.MILLISECONDS.sleep(WorkerManager.sleepMillis * 2);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
            }
            
            YDM.forceTaskStop = true;
            
            for(Worker w : WorkerManager.WORKERS)
            {
                if(w.isAlive())
                {
                    try
                    {
                        w.join();
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
            }
        },
            YDM.MOD_ID_UP + " shutdown hook");
        
        Runtime.getRuntime().addShutdownHook(shutdownListener);
    }
    
    private static void initWorker(int index)
    {
        WorkerManager.WORKERS[index] = new Worker(YDM.MOD_ID_UP + " Worker Thread " + (index + 1), index, WorkerManager.sleepMillis);
        WorkerManager.WORKERS[index].start();
        YDM.log("Initialized Worker " + index);
    }
    
    public static void failedCallback(int index)
    {
        WorkerManager.initWorker(index);
    }
}
