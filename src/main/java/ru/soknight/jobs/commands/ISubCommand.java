package ru.soknight.jobs.commands;

import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;

public interface ISubCommand {
    
    void execute() throws NotLoadedConfigException;

    boolean hasPermission();
    
    boolean hasPermission(String permission);
    
    boolean isPlayerRequired();
    
    boolean isPlayerExist(String name);
    
    boolean isJobExist(String job);
    
    boolean workOnJob(String name, JobType job);
    
    boolean argIsInteger(String arg);
    
    boolean isCorrectUsage();
	
}
