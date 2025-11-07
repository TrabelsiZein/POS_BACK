package com.digithink.pos.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.Location;
import com.digithink.pos.service.LocationService;

@RestController
@RequestMapping("location")
public class LocationAPI extends _BaseController<Location, Long, LocationService> {

}

