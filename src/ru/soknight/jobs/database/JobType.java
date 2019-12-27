package ru.soknight.jobs.database;

import java.util.Arrays;
import java.util.List;

public enum JobType {

	JOBLESS("Безработный"),
	MINER("Шахтёр"), 
	WOODCUTTER("Лесоруб"), 
	HUNTER("Охотник"), 
	FISHERMAN("Рыбак"), 
	FARMER("Фермер");
	
	private String name;
	
	JobType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	private static List<String> names = Arrays.asList("MINER", "WOODCUTTER", "HUNTER", "FISHERMAN", "FARMER");
	
	public static boolean isExist(String name) {
		return names.contains(name.toUpperCase());
	}
	
}
