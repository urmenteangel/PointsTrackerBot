package com.SoleraBootcamp4.PointsTrackerBot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.SoleraBootcamp4.PointsTrackerBot.service.PointsTrackerBot;
import com.SoleraBootcamp4.PointsTrackerBot.service.PointsTrackerService;

@RestController
@RequestMapping("/PointsTracker")
public class PointsTrackerController {

    @Autowired
    PointsTrackerService service;

    @Autowired
    PointsTrackerBot botService;

    @PostMapping("/github_payload")
    @ResponseStatus(HttpStatus.OK)
    public void getGitHubPayload(@RequestBody String payload){
        service.pullTeamData(payload);
    }
    @PostMapping("/telegram_payload")
    @ResponseStatus(HttpStatus.OK)
    public void getTelegramPayload(@RequestBody String payload){
        service.payloadToTelegramMessage(payload);
    }

    
}
