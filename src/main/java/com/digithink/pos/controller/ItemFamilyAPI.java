package com.digithink.pos.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.ItemFamily;
import com.digithink.pos.service.ItemFamilyService;

@RestController
@RequestMapping("item-family")
public class ItemFamilyAPI extends _BaseController<ItemFamily, Long, ItemFamilyService> {

}


