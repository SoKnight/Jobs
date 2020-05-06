package ru.soknight.jobs.command.validation;

import lombok.AllArgsConstructor;
import ru.soknight.jobs.configuration.Config;
import ru.soknight.jobs.configuration.JobConfiguration;
import ru.soknight.jobs.configuration.JobTypeEnum;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.CommandExecutionData;
import ru.soknight.lib.validation.ValidationResult;
import ru.soknight.lib.validation.validator.Validator;

@AllArgsConstructor
public class JobValidator implements Validator {

	private final Config config;
	private final Messages messages;
	
	@Override
	public ValidationResult validate(CommandExecutionData data) {
		if(!(data instanceof JobExecutionData)) return new ValidationResult(false);
		
		JobExecutionData jobdata = (JobExecutionData) data;
		
		String jobName = jobdata.getJobName();
		if(jobName == null) jobName = "";
		
		String message = messages.getFormatted("error.unknown-job", "%job%", jobName);
		ValidationResult failed = new ValidationResult(false, message);
		
		JobTypeEnum job = JobTypeEnum.valueOf(jobName.toUpperCase());
		if(job == null) return failed;
			
		JobConfiguration config = this.config.getJobConfig(job);
		if(config == null || !config.isInitialized()) {
			message = messages.getFormatted("error.job-is-not-init",
					"%file%", job.toString().toLowerCase() + ".yml");
			failed = new ValidationResult(false, message);
			return failed;
		}
		
		return new ValidationResult(true);
	}
	
}
