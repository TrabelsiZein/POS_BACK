package com.digithink.vacation_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.vacation_app.model.VacationRequest;
import com.digithink.vacation_app.service.VacationRequestService;

@RestController
@RequestMapping("vacation_request")
public class VacationRequestAPI extends _BaseController<VacationRequest, Long, VacationRequestService> {

}
