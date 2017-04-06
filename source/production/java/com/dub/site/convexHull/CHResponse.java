package com.dub.site.convexHull;

import java.util.List;

/** container object for AJAX response 
 * on initGraph request should return a component in a suitable form 
 **/
public class CHResponse {
	private Status status;
	private List<StepResult> results;
	

	public CHResponse() {
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<StepResult> getResults() {
		return results;
	}

	public void setResults(List<StepResult> results) {
		this.results = results;
	}







	public static enum Status {
		OK, ERROR, INIT
	}
}
