package com.digithink.business_management.controller.configuration;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.controller._BaseController;
import com.digithink.business_management.model.configuration.SeriesLine;
import com.digithink.business_management.service.configuration.SeriesLineService;

@RestController
@RequestMapping("series_line")
public class SeriesLineAPI extends _BaseController<SeriesLine, Long, SeriesLineService> {

}
