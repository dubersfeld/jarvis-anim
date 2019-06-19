package com.dub.spring.controller;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dub.spring.convexHull.CHResponse;
import com.dub.spring.convexHull.ConvexHullServices;
import com.dub.spring.convexHull.JSONSnapshot;
import com.dub.spring.convexHull.Point;
import com.dub.spring.convexHull.PointDist;
import com.dub.spring.convexHull.PointsInitRequest;
import com.dub.spring.convexHull.SearchRequest;
import com.dub.spring.convexHull.StepResult;


@Controller
public class ConvexHullController {
	
	// using a service layer
	@Autowired
	private ConvexHullServices chServices;
	
	@RequestMapping(value="/initPoints")
	@ResponseBody
	public CHResponse initPoints(@RequestBody PointsInitRequest message, 
											HttpServletRequest request) 
	{	
		int N = message.getPoints().length;
		System.out.println("controller: " + N);
		
		Point[] points = message.getPoints();
		
		PointDist pointDist = new PointDist(points);
		
		pointDist.init();
		
		// add pointDist to session context
		HttpSession session = request.getSession();
		
		if (session.getAttribute("points") != null) {
			session.removeAttribute("points");
		}
		
		session.setAttribute("points", pointDist);
		
		
		CHResponse chResponse = new CHResponse();
		chResponse.setStatus(CHResponse.Status.INIT);
		
		System.out.println("controller: initPoints");
		pointDist.displayCH();
		
		return chResponse;
	}// initPoints
	
	
	@RequestMapping(value="jarvisMarch")
	@ResponseBody
	public CHResponse jarvisMarch(@RequestBody SearchRequest message, 
														HttpServletRequest request) 
	{	
		// retrieve points from session context
		HttpSession session = request.getSession();
		PointDist pointDist = (PointDist)session.getAttribute("points");
		
		CHResponse chResponse = new CHResponse();
		
		chResponse.setStatus(CHResponse.Status.OK);
		
		List<StepResult> results = new ArrayList<>();
		
		while (!pointDist.isFinished()) {
			pointDist.marchStep();
			JSONSnapshot snapshot = chServices.PointsToJSON(pointDist);
			
			StepResult result = new StepResult();
			
			result.setSnapshot(snapshot);
			
			if (pointDist.isFinished()) {
				result.setStatus(StepResult.Status.FINISHED);
			} else if (pointDist.isNewVertexFound()) { 
				result.setStatus(StepResult.Status.REDRAW);
			} else {
				result.setStatus(StepResult.Status.STEP);
			}
			
			results.add(result);
						
		}// while
		
		chResponse.setResults(results);
		
		
		return chResponse;

	}
	

}
