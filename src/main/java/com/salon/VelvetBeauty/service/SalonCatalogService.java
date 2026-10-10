package com.salon.VelvetBeauty.service;

import com.salon.VelvetBeauty.model.HairService;
import com.salon.VelvetBeauty.model.NailService;
import com.salon.VelvetBeauty.model.SalonService;
import com.salon.VelvetBeauty.model.SkinService;
import com.salon.VelvetBeauty.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service managing salon service catalog offerings.
 * Demonstrates: Polymorphic delegation to HairService, SkinService, and NailService.
 */
@Service
public class SalonCatalogService {

    private final ServiceRepository serviceRepository;

    public SalonCatalogService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<SalonService> getAllServices() {
        return serviceRepository.findAll();
    }

    public List<SalonService> getServicesByCategory(String category) {
        return serviceRepository.findByCategory(category);
    }

    public List<SalonService> searchServices(String query, String category) {
        return serviceRepository.search(query, category);
    }

    public Optional<SalonService> getServiceById(String id) {
        return serviceRepository.findById(id);
    }

    /**
     * Creates and saves a new service offering polymorphically based on category.
     */
    public SalonService createService(String name, String category, double basePrice, 
                                     int durationMinutes, String description, String imageUrl, 
                                     String customProp) {
        String id = "SRV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        SalonService service;

        String catUpper = category != null ? category.trim().toUpperCase() : "HAIR";
        switch (catUpper) {
            case "HAIR":
                service = new HairService(id, name, basePrice, durationMinutes, description, 
                        imageUrl, true, customProp != null ? customProp : "Medium");
                break;
            case "SKIN":
                service = new SkinService(id, name, basePrice, durationMinutes, description, 
                        imageUrl, true, customProp != null ? customProp : "All Types");
                break;
            case "NAIL":
                service = new NailService(id, name, basePrice, durationMinutes, description, 
                        imageUrl, true, customProp != null ? customProp : "Classic");
                break;
            default:
                service = new HairService(id, name, basePrice, durationMinutes, description, 
                        imageUrl, true, "Medium");
                break;
        }

        serviceRepository.save(service);
        return service;
    }

    /**
     * Updates an existing service.
     */
    public boolean updateService(String id, String name, double basePrice, int durationMinutes, 
                                 String description, String imageUrl, boolean active, String customProp) {
        Optional<SalonService> existingOpt = serviceRepository.findById(id);
        if (existingOpt.isEmpty()) return false;

        SalonService service = existingOpt.get();
        service.setName(name);
        service.setBasePrice(basePrice);
        service.setDurationMinutes(durationMinutes);
        service.setDescription(description);
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            service.setImageUrl(imageUrl);
        }
        service.setActive(active);

        if (service instanceof HairService && customProp != null) {
            ((HairService) service).setDefaultHairLength(customProp);
        } else if (service instanceof SkinService && customProp != null) {
            ((SkinService) service).setRecommendedSkinType(customProp);
        } else if (service instanceof NailService && customProp != null) {
            ((NailService) service).setDefaultFinish(customProp);
        }

        serviceRepository.save(service);
        return true;
    }

    public boolean deleteService(String id) {
        return serviceRepository.deleteById(id);
    }
}
