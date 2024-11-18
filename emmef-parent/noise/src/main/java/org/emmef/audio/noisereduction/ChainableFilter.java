package org.emmef.audio.noisereduction;

import org.emmef.audio.filter.Filter;


public interface ChainableFilter extends Filter {
	default Double getMetaData() {
		return null;
	};
}
