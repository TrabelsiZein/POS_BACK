package com.digithink.business_management.controller.inventory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.controller._BaseController;
import com.digithink.business_management.model.inventory.Location;
import com.digithink.business_management.service.inventory.LocationService;

@RestController
@RequestMapping("location")
public class LocationAPI extends _BaseController<Location, Long, LocationService> {

}
