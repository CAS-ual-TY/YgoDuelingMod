package de.cas_ual_ty.ydm.task;

import java.util.function.Supplier;

public class Task implements Runnable
{
    private static final Supplier<Boolean> DEFAULT_IS_READY = () -> true;
    private static final Supplier<Boolean> DEFAULT_IS_CANCELED = () -> false;
    private static final Runnable DEFAULT_ON_CANCEL = () ->
    {};
    
    public final String name;
    public final int priority;
    public Runnable task;
    protected Supplier<Boolean> isReady;
    protected Supplier<Boolean> isCanceled;
    protected Runnable onCancel;
    
    public Task(String name, int priority, Runnable task)
    {
        this.name = name;
        this.priority = priority;
        this.task = task;
        this.isReady = Task.DEFAULT_IS_READY;
        this.isCanceled = Task.DEFAULT_IS_CANCELED;
        this.onCancel = Task.DEFAULT_ON_CANCEL;
    }
    
    public Task(TaskPriority priority, Runnable task)
    {
        this(priority.name, priority.priority, task);
    }
    
    public Task setDependency(Supplier<Boolean> isReady)
    {
        this.isReady = isReady;
        return this;
    }
    
    public Task setCancelable(Supplier<Boolean> isCanceled)
    {
        return this;
    }
    
    public Task setOnCancel(Runnable onCancel)
    {
        return this;
    }
    
    public boolean isReady()
    {
        return this.isReady.get();
    }
    
    public boolean isCanceled()
    {
        return this.isCanceled.get();
    }
    
    public void onCancel()
    {
        this.onCancel.run();
    }
    
    @Override
    public void run()
    {
        this.task.run();
    }
}
