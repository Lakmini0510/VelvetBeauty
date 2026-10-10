package com.salon.VelvetBeauty.controller;

import com.salon.VelvetBeauty.model.Admin;
import com.salon.VelvetBeauty.model.Customer;
import com.salon.VelvetBeauty.model.PremiumCustomer;
import com.salon.VelvetBeauty.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Controller advice populating common user authentication attributes into all Thymeleaf views.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentUser")
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }

    @ModelAttribute("isAdmin")
    public boolean getIsAdmin(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return user instanceof Admin;
    }

    @ModelAttribute("isCustomer")
    public boolean getIsCustomer(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return user instanceof Customer;
    }

    @ModelAttribute("isPremium")
    public boolean getIsPremium(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return user instanceof PremiumCustomer;
    }
}
