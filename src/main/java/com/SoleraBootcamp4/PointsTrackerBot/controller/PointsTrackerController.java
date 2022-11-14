package com.SoleraBootcamp4.PointsTrackerBot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.SoleraBootcamp4.PointsTrackerBot.service.PointsTrackerService;

@RestController
@RequestMapping("/api/v1.0")
public class PointsTrackerController {

    @Autowired
    PointsTrackerService service;

    @PostMapping("/PointsTracker")
    @ResponseStatus(HttpStatus.OK)
    public void getPayload(@RequestBody String payload){
        service.pullTeamData(payload);
    }

    
}
