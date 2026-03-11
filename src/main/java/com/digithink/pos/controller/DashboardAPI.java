package com.digithink.pos.controller;

import com.digithink.pos.dto.DashboardTodayDTO;
import com.digithink.pos.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin/dashboard")
public class DashboardAPI {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/today")
    public ResponseEntity<DashboardTodayDTO> getTodayStats() {
        return ResponseEntity.ok(dashboardService.getTodayStats());
    }
}
