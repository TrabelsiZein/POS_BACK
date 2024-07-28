package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.digithink.business_management.model.SeriesLine;
import com.digithink.business_management.service.SeriesLineService;

@RequestMapping("series_line")
public class SeriesLineAPI extends _BaseController<SeriesLine, Long, SeriesLineService> {

}
