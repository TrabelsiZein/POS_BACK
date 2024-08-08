package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model.configuration.SeriesHeader;
import com.digithink.business_management.service.SeriesHeaderService;

@RestController
@RequestMapping("series_header")
public class SeriesHeaderAPI extends _BaseController<SeriesHeader, Long, SeriesHeaderService> {

}
