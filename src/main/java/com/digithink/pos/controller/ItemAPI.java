package com.digithink.pos.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.Item;
import com.digithink.pos.service.ItemService;

@RestController
@RequestMapping("item")
public class ItemAPI extends _BaseController<Item, Long, ItemService> {

}

