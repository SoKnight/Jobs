package ru.soknight.jobs.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotLoadedConfigException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private final String jobType;

}
