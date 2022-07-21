package de.cas_ual_ty.ydm.task;

public enum TaskPriority
{
    IMG_DOWNLOAD("img download", 2), IMG_ADJUSTMENT("img adjustment", 2), BINDER_SAVE("binder save", 3), BINDER_LOAD("binder load", 3);
    
    public final String name;
    public final int priority;
    
    TaskPriority(String name, int priority)
    {
        this.name = name;
        this.priority = priority;
    }
}
