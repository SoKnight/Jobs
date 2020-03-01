package ru.soknight.jobs.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.soknight.jobs.enums.JobType;
import ru.soknight.jobs.exception.NotLoadedConfigException;
import ru.soknight.jobs.files.Config;
import ru.soknight.jobs.objects.JobConfig;

public class ExpUtils {

	public static final List<JobType> jobs = Arrays.asList(JobType.MINER, JobType.WOODCUTTER, JobType.HUNTER,
			JobType.FISHERMAN, JobType.FARMER);
	
	public static int getNeededExp(JobType jobType, int nextlevel) throws NotLoadedConfigException {
		JobConfig config = Config.getJobConfig(jobType);
		float multiplier = config.getExpMultiplier();
		float first = config.getFirstLevelExp();
		float result = first * multiplier;
		if(nextlevel > 2) for(int i = 2; i < nextlevel; i++) result *= multiplier;
		return (int) result;
	}
	
	public static List<String> fixListColors(List<String> raw) {
		List<String> output = new ArrayList<>();
		for(String s : raw)
			output.add(s.replace("&", "\u00A7"));
		return output;
	}
	
}
