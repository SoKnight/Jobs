package ru.soknight.jobs.units;

import org.bukkit.inventory.ItemStack;

import ru.soknight.jobs.database.JobType;

public class SelectionTool {

	private ItemStack itemstack;
	private Operation operation;
	private JobType job;
	
	public SelectionTool(ItemStack itemstack, Operation operation, JobType job) {
		this.itemstack = itemstack;
		this.operation = operation;
		this.job = job;
	}
	
	public ItemStack getItemStack() {
		return itemstack;
	}

	public Operation getOperation() {
		return operation;
	}

	public JobType getJob() {
		return job;
	}

	public enum Operation { ADD, REMOVE; }
	
}
