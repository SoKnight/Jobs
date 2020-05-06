package ru.soknight.jobs.configuration;

import org.apache.commons.lang.WordUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobTypeEnum {

	MINER,
	WOODCUTTER;
	
	public String getDefaultName() {
		return WordUtils.capitalize(this.toString().toLowerCase());
	}
	
}
