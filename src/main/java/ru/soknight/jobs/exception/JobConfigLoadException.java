package ru.soknight.jobs.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.soknight.jobs.configuration.JobTypeEnum;

@Getter
@RequiredArgsConstructor
public class JobConfigLoadException extends Exception {

	private static final long serialVersionUID = 1L;

	private final JobTypeEnum jobType;
	private final String message;
	
}
