package com.salon.VelvetBeauty.controller;

import com.salon.VelvetBeauty.model.Stylist;
import com.salon.VelvetBeauty.service.StylistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller for public stylist directory and schedule viewing.
 */
@Controller
public class StylistCatalogController {

    private final StylistService stylistService;

    public StylistCatalogController(StylistService stylistService) {
        this.stylistService = stylistService;
    }

    @GetMapping("/stylists")
    public String listStylists(@RequestParam(required = false) String query, Model model) {
        List<Stylist> stylists = stylistService.searchStylists(query);
        model.addAttribute("stylists", stylists);
        model.addAttribute("searchQuery", query != null ? query : "");
        return "stylists";
    }
}
