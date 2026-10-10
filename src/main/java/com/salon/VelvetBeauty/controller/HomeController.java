package com.salon.VelvetBeauty.controller;

import com.salon.VelvetBeauty.model.Review;
import com.salon.VelvetBeauty.model.SalonService;
import com.salon.VelvetBeauty.model.Stylist;
import com.salon.VelvetBeauty.service.ReviewService;
import com.salon.VelvetBeauty.service.SalonCatalogService;
import com.salon.VelvetBeauty.service.StylistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controller for the public home landing page.
 */
@Controller
public class HomeController {

    private final SalonCatalogService catalogService;
    private final StylistService stylistService;
    private final ReviewService reviewService;

    public HomeController(SalonCatalogService catalogService, 
                          StylistService stylistService, 
                          ReviewService reviewService) {
        this.catalogService = catalogService;
        this.stylistService = stylistService;
        this.reviewService = reviewService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<SalonService> featuredServices = catalogService.getAllServices().stream().limit(6).toList();
        List<Stylist> stylists = stylistService.getActiveStylists().stream().limit(4).toList();
        List<Review> approvedReviews = reviewService.getApprovedReviews().stream().limit(3).toList();

        model.addAttribute("featuredServices", featuredServices);
        model.addAttribute("stylists", stylists);
        model.addAttribute("reviews", approvedReviews);
        model.addAttribute("averageRating", reviewService.getAverageRating());
        return "index";
    }
}
