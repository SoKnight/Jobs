package ru.soknight.jobs.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import ru.soknight.jobs.database.WorkspaceBlock;

public class ListUtils {
	
	public static List<Material> getBlocktypesOnPage(List<Material> list, int size, int page) {
		List<Material> empty = new ArrayList<>();
		if(list.isEmpty()) return empty;
		
		int start = size * (page - 1), end = size * page - 1;
		
		if(start >= list.size()) return empty;
		if(end >= list.size()) end = list.size();
		
		List<Material> onpage = list.subList(start, end);
		return onpage;
	}
	
	public static List<WorkspaceBlock> getBlocksOnPage(List<WorkspaceBlock> list, int size, int page) {
		List<WorkspaceBlock> empty = new ArrayList<>();
		if(list.isEmpty()) return empty;
		
		int start = size * (page - 1), end = size * page - 1;
		
		if(start >= list.size()) return empty;
		if(end >= list.size()) end = list.size();
		
		List<WorkspaceBlock> onpage = list.subList(start, end);
		return onpage;
	}
	
}
