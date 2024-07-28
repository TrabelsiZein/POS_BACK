package com.digithink.business_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.digithink.business_management.model.SeriesHeader;
import com.digithink.business_management.service.SeriesHeaderService;

@RequestMapping("series_header")
public class SeriesHeaderAPI extends _BaseController<SeriesHeader, Long, SeriesHeaderService> {

}
