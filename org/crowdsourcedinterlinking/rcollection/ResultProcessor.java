package org.crowdsourcedinterlinking.rcollection;

import org.crowdsourcedinterlinking.model.Alignment;

public interface ResultProcessor {

	public void serialiseSelectedAlignmentToAlignmentAPIFormat(
			Alignment crowdAlignment);

}
