package de.cas_ual_ty.ydm.task;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedList;

public class TaskQueue
{
    private static final LinkedList<Task> TASK_QUEUE = new LinkedList<>();
    private static final LinkedList<Task> TASKS_TO_ADD = new LinkedList<>();
    
    public static void addTask(Task t)
    {
        synchronized(TaskQueue.TASKS_TO_ADD)
        {
            TaskQueue.TASKS_TO_ADD.addLast(t);
        }
    }
    
    private static void removeTasksUnsafe()
    {
        Task t;
        Iterator<Task> it = TaskQueue.TASK_QUEUE.iterator();
        while(it.hasNext())
        {
            t = it.next();
            
            if(t.isCanceled())
            {
                it.remove();
                t.onCancel();
            }
        }
    }
    
    private static void shiftAddedTasksUnsafe()
    {
        synchronized(TaskQueue.TASKS_TO_ADD)
        {
            for(Task t : TaskQueue.TASKS_TO_ADD)
            {
                TaskQueue.TASK_QUEUE.add(t);
            }
            
            TaskQueue.TASKS_TO_ADD.clear();
        }
    }
    
    @Nullable
    public static Task pollTask()
    {
        synchronized(TaskQueue.TASK_QUEUE)
        {
            TaskQueue.shiftAddedTasksUnsafe();
            TaskQueue.removeTasksUnsafe();
            
            Task task = null;
            int priority = Integer.MIN_VALUE;
            
            for(Task t : TaskQueue.TASK_QUEUE)
            {
                if(!t.isCanceled() && t.isReady() && t.priority > priority)
                {
                    task = t;
                    priority = task.priority;
                }
            }
            
            if(task != null)
            {
                TaskQueue.TASK_QUEUE.remove(task);
            }
            
            return task;
        }
    }
}
