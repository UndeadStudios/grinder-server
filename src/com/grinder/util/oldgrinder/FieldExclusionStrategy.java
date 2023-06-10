package com.grinder.util.oldgrinder;

import java.lang.annotation.Annotation;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class FieldExclusionStrategy implements ExclusionStrategy {
	
	private StreamType streamType;
	
	public static enum StreamType {
		INPUT, OUTPUT;
	}
	
	public FieldExclusionStrategy(StreamType streamType) {
		this.streamType = streamType;
	}
	
	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		Annotation annotation = f.getAnnotation(FilteredFieldIO.class);
		if (annotation != null) {
			FilteredFieldIO filteredField = (FilteredFieldIO) annotation;
			if (streamType == StreamType.INPUT) {
				return !filteredField.read();
			}
			if (streamType == StreamType.OUTPUT) {
				return !filteredField.write();
			}
		}
		return false;
	}
	
	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}
	
}
