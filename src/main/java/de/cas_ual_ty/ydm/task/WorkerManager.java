package de.cas_ual_ty.ydm.task;

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
    }
    
    private static void initWorker(int index)
    {
        WorkerManager.WORKERS[index] = new Worker(YDM.MOD_ID + " Worker Thread " + (index + 1), index, WorkerManager.sleepMillis);
        WorkerManager.WORKERS[index].start();
        YDM.log("Initialized Worker " + index);
    }
    
    public static void failedCallback(int index)
    {
        WorkerManager.initWorker(index);
    }
}
