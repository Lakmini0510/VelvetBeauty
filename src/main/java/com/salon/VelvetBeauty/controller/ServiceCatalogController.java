package com.salon.VelvetBeauty.controller;

import com.salon.VelvetBeauty.model.SalonService;
import com.salon.VelvetBeauty.service.SalonCatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller for public catalog exploration and service searching.
 */
@Controller
public class ServiceCatalogController {

    private final SalonCatalogService catalogService;

    public ServiceCatalogController(SalonCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/services")
    public String listServices(@RequestParam(required = false) String category,
                               @RequestParam(required = false) String query,
                               Model model) {
        List<SalonService> services = catalogService.searchServices(query, category);

        model.addAttribute("services", services);
        model.addAttribute("selectedCategory", category != null ? category : "ALL");
        model.addAttribute("searchQuery", query != null ? query : "");
        return "services";
    }
}
