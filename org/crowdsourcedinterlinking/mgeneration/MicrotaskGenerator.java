package org.crowdsourcedinterlinking.mgeneration;

import java.util.Set;

import org.crowdsourcedinterlinking.model.Alignment;
import org.crowdsourcedinterlinking.model.Microtask;
import org.crowdsourcedinterlinking.model.TypeOfMappingGoal;

public interface MicrotaskGenerator {

	public Set<Microtask> createMicrotasks(Alignment a);

}
