package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.Location;
import com.digithink.business_management.service.LocationService;

@RestController
@RequestMapping("location")
public class LocationAPI extends _BaseController<Location, Long, LocationService> {

}
