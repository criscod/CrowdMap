package org.crowdsourcedinterlinking.rcollection;

import java.util.Set;

import org.crowdsourcedinterlinking.model.Mapping;
import org.crowdsourcedinterlinking.model.Response;

import org.crowdsourcedinterlinking.mpublication.Service;

public interface ResultReader {

	public Set<Response> readResponsesZip(String microtaskId, Service s);

	// only ID needed
	public Set<Mapping> readResponsesOfMicrotask(String microtaskId,
			String type, Service service);

}
