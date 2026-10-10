package com.salon.VelvetBeauty.service;

import com.salon.VelvetBeauty.model.FullTimeStylist;
import com.salon.VelvetBeauty.model.PartTimeStylist;
import com.salon.VelvetBeauty.model.Stylist;
import com.salon.VelvetBeauty.repository.StylistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service managing Salon Stylists and Staff Members.
 * Demonstrates: Polymorphic delegation to FullTimeStylist and PartTimeStylist.
 */
@Service
public class StylistService {

    private final StylistRepository stylistRepository;

    public StylistService(StylistRepository stylistRepository) {
        this.stylistRepository = stylistRepository;
    }

    public List<Stylist> getAllStylists() {
        return stylistRepository.findAll();
    }

    public List<Stylist> getActiveStylists() {
        return stylistRepository.findAll().stream().filter(Stylist::isActive).toList();
    }

    public Optional<Stylist> getStylistById(String id) {
        return stylistRepository.findById(id);
    }

    public List<Stylist> searchStylists(String query) {
        return stylistRepository.search(query);
    }

    public Stylist createStylist(String name, String specialty, String workingDays, 
                                 String workingHours, String phone, String email, 
                                 String imageUrl, String bio, String type, 
                                 double rateOrSalary, double commissionOrBonus, String extraParam) {
        String id = "STY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Stylist stylist;

        if ("PART_TIME".equalsIgnoreCase(type)) {
            int weeklyHours = 20;
            try {
                if (extraParam != null && !extraParam.isEmpty()) {
                    weeklyHours = Integer.parseInt(extraParam);
                }
            } catch (Exception ignored) {}
            stylist = new PartTimeStylist(id, name, specialty, workingDays, workingHours, phone, email, 
                    imageUrl, bio, 5.0, true, rateOrSalary, weeklyHours, commissionOrBonus);
        } else {
            String benefits = (extraParam != null && !extraParam.isEmpty()) ? extraParam : "Medical & Dental Insurance";
            stylist = new FullTimeStylist(id, name, specialty, workingDays, workingHours, phone, email, 
                    imageUrl, bio, 5.0, true, rateOrSalary, commissionOrBonus, benefits);
        }

        stylistRepository.save(stylist);
        return stylist;
    }

    public boolean updateStylist(String id, String name, String specialty, String workingDays, 
                                 String workingHours, String phone, String email, 
                                 String bio, String imageUrl, boolean active) {
        Optional<Stylist> existingOpt = stylistRepository.findById(id);
        if (existingOpt.isEmpty()) return false;

        Stylist s = existingOpt.get();
        s.setName(name);
        s.setSpecialty(specialty);
        s.setWorkingDays(workingDays);
        s.setWorkingHours(workingHours);
        s.setPhone(phone);
        s.setEmail(email);
        s.setBio(bio);
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            s.setImageUrl(imageUrl);
        }
        s.setActive(active);

        stylistRepository.save(s);
        return true;
    }

    public boolean deleteStylist(String id) {
        return stylistRepository.deleteById(id);
    }
}
