package com.salon.VelvetBeauty.config;

import com.salon.VelvetBeauty.model.*;
import com.salon.VelvetBeauty.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Initializes realistic sample data into .txt files if they are empty upon startup.
 * Guarantees out-of-the-box demonstration of all OOP features.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final ServiceRepository serviceRepository;
    private final StylistRepository stylistRepository;
    private final AppointmentRepository appointmentRepository;
    private final ReviewRepository reviewRepository;

    public DataInitializer(UserRepository userRepository, 
                           AdminRepository adminRepository, 
                           ServiceRepository serviceRepository, 
                           StylistRepository stylistRepository, 
                           AppointmentRepository appointmentRepository, 
                           ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.serviceRepository = serviceRepository;
        this.stylistRepository = stylistRepository;
        this.appointmentRepository = appointmentRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void run(String... args) {
        seedAdmins();
        seedUsers();
        seedServices();
        seedStylists();
        seedAppointments();
        seedReviews();
    }

    private void seedAdmins() {
        if (adminRepository.findAll().isEmpty()) {
            adminRepository.save(new Admin(
                    "ADM-001", "admin", "admin123", "Giselle Dupont", 
                    "owner@velvetbeauty.com", "+1 555-0101", "2026-01-10 09:00", 
                    "SALON_OWNER", "Executive Suite"
            ));
            adminRepository.save(new Admin(
                    "ADM-002", "reception", "recep123", "Chloe Bennett", 
                    "frontdesk@velvetbeauty.com", "+1 555-0102", "2026-02-15 08:30", 
                    "RECEPTIONIST", "Front Desk & Concierge"
            ));

            adminRepository.logActivity(new AdminActivityLog(
                    "LOG-001", "admin", "SYSTEM_INIT", "Velvet Beauty administrative system initialized", 
                    "2026-01-10 09:01:00"
            ));
        }
    }

    private void seedUsers() {
        if (userRepository.findAll().isEmpty()) {
            // Regular Customers (5% discount)
            userRepository.save(new RegularCustomer(
                    "CUST-001", "sophia", "sophia123", "Sophia Miller", 
                    "sophia@example.com", "+1 555-0201", "2026-03-01 10:00", 35
            ));
            userRepository.save(new RegularCustomer(
                    "CUST-002", "emma", "emma123", "Emma Watson", 
                    "emma@example.com", "+1 555-0202", "2026-03-12 11:15", 15
            ));

            // Premium VIP Customer (20% discount + free cancellation)
            userRepository.save(new PremiumCustomer(
                    "CUST-003", "victoria", "vip123", "Victoria Sterling", 
                    "victoria@luxury.com", "+1 555-0203", "2026-02-20 14:20", 320, "DIAMOND"
            ));
        }
    }

    private void seedServices() {
        if (serviceRepository.findAll().isEmpty()) {
            // Hair Services
            serviceRepository.save(new HairService(
                    "SRV-001", "Signature Balayage & Blowout", 43500.0, 90, 
                    "Hand-painted luminous dimension with luxury gloss toner and styling blowout.",
                    "https://images.unsplash.com/photo-1560869713-7d0a29430803?auto=format&fit=crop&w=600&q=80",
                    true, "Long"
            ));
            serviceRepository.save(new HairService(
                    "SRV-002", "Botanical Keratin Smoothing", 28500.0, 60, 
                    "Organic amino-acid infusion eliminating frizz while protecting hair integrity.",
                    "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=600&q=80",
                    true, "Medium"
            ));

            // Skin Services
            serviceRepository.save(new SkinService(
                    "SRV-003", "24K Gold Cellular Glow Facial", 39000.0, 75, 
                    "Regenerative ultrasonic therapy enriched with pure 24K gold foil and stem cells.",
                    "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?auto=format&fit=crop&w=600&q=80",
                    true, "All Types"
            ));
            serviceRepository.save(new SkinService(
                    "SRV-004", "Deep Pore Clarifying Hydro-Peel", 25500.0, 50, 
                    "Vacuum vortex extraction infused with salicylic acid and tea tree soothing mist.",
                    "https://images.unsplash.com/photo-1512290900672-1f413344b584?auto=format&fit=crop&w=600&q=80",
                    true, "Oily & Acne-Prone"
            ));

            // Nail Services
            serviceRepository.save(new NailService(
                    "SRV-005", "Velvet Luxe Gel Manicure", 16500.0, 45, 
                    "Russian cuticle prep, non-toxic Japanese gel overlay, and shea butter hot towel massage.",
                    "https://images.unsplash.com/photo-1604654894610-df63bc536371?auto=format&fit=crop&w=600&q=80",
                    true, "Gel"
            ));
            serviceRepository.save(new NailService(
                    "SRV-006", "Aromatherapy Spa Pedicure", 21000.0, 60, 
                    "Himalayan pink salt soak, eucalyptus mask, volcanic stone reflexology massage.",
                    "https://images.unsplash.com/photo-1519014816548-bf5fe059798b?auto=format&fit=crop&w=600&q=80",
                    true, "Classic"
            ));
        }
    }

    private void seedStylists() {
        if (stylistRepository.findAll().isEmpty()) {
            // Full-time stylists
            stylistRepository.save(new FullTimeStylist(
                    "STY-001", "Elena Rostova", "Master Colorist & Editorial Hair", 
                    "Monday, Tuesday, Wednesday, Friday, Saturday", "09:00 - 18:00", 
                    "+1 555-0301", "elena@velvetbeauty.com", 
                    "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=500&q=80", 
                    "12 years experience in Paris and Milan fashion week runway hair design.", 
                    4.9, true, 1260000.0, 0.08, "Executive Medical, Paid Retreat, Product Stipend"
            ));
            stylistRepository.save(new FullTimeStylist(
                    "STY-002", "Camille Laurent", "Senior Dermal Esthetician", 
                    "Tuesday, Wednesday, Thursday, Friday, Saturday", "10:00 - 19:00", 
                    "+1 555-0302", "camille@velvetbeauty.com", 
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=500&q=80", 
                    "Board-certified skin therapist specializing in non-invasive glass skin rituals.", 
                    5.0, true, 1170000.0, 0.08, "Full Medical, Dental & Continuing Education Grant"
            ));

            // Part-time stylists
            stylistRepository.save(new PartTimeStylist(
                    "STY-03", "Mateo Silva", "Haute Nail Sculptor & Artistry", 
                    "Thursday, Friday, Saturday, Sunday", "11:00 - 18:00", 
                    "+1 555-0303", "mateo@velvetbeauty.com", 
                    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=500&q=80", 
                    "Renowned celebrity nail artist with mastery over sculpted acrylics and chrome finishes.", 
                    4.8, true, 9600.0, 24, 0.20
            ));
            stylistRepository.save(new PartTimeStylist(
                    "STY-004", "Aria Montgomery", "Bridal Glow & Skin Rejuvenation", 
                    "Friday, Saturday, Sunday", "09:00 - 17:00", 
                    "+1 555-0304", "aria@velvetbeauty.com", 
                    "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=500&q=80", 
                    "Specializes in camera-ready bridal aesthetics and delicate organic skincare treatments.", 
                    4.9, true, 9000.0, 18, 0.25
            ));
        }
    }

    private void seedAppointments() {
        if (appointmentRepository.findAll().isEmpty()) {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
            String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);

            // Completed appointment for Victoria (used for verified review)
            appointmentRepository.save(new Appointment(
                    "APT-101", "CUST-003", "victoria", "Victoria Sterling", "victoria@luxury.com", 
                    "+1 555-0203", "PREMIUM", "SRV-003", "24K Gold Cellular Glow Facial", "SKIN", 
                    "24K Gold Luxury Radiance (+ Rs. 13500.00)", "STY-002", "Camille Laurent", 
                    yesterday, "10:30 AM", "COMPLETED", 52500.0, 10500.0, 42000.0, 0.0, "", 
                    "Customer requested extra cooling globes.", "2026-10-01 10:00"
            ));

            // Completed appointment for Sophia
            appointmentRepository.save(new Appointment(
                    "APT-102", "CUST-001", "sophia", "Sophia Miller", "sophia@example.com", 
                    "+1 555-0201", "REGULAR", "SRV-001", "Signature Balayage & Blowout", "HAIR", 
                    "Long (+ Rs. 6000.00)", "STY-001", "Elena Rostova", 
                    yesterday, "01:30 PM", "COMPLETED", 49500.0, 2475.0, 47025.0, 0.0, "", 
                    "Wants subtle warm honey ribbons.", "2026-10-02 12:30"
            ));

            // Upcoming appointment for Victoria
            appointmentRepository.save(new Appointment(
                    "APT-103", "CUST-003", "victoria", "Victoria Sterling", "victoria@luxury.com", 
                    "+1 555-0203", "PREMIUM", "SRV-005", "Velvet Luxe Gel Manicure", "NAIL", 
                    "Long-Wear Gel Polish (+ Rs. 3600.00)", "STY-03", "Mateo Silva", 
                    tomorrow, "03:00 PM", "BOOKED", 20100.0, 4020.0, 16080.0, 0.0, "", 
                    "Nude pink with pearlescent glaze.", "2026-10-05 09:15"
            ));

            // Upcoming appointment for Sophia
            appointmentRepository.save(new Appointment(
                    "APT-104", "CUST-001", "sophia", "Sophia Miller", "sophia@example.com", 
                    "+1 555-0201", "REGULAR", "SRV-004", "Deep Pore Clarifying Hydro-Peel", "SKIN", 
                    "Standard Hydrating (Included)", "STY-002", "Camille Laurent", 
                    today, "04:30 PM", "BOOKED", 25500.0, 1275.0, 24225.0, 0.0, "", 
                    "First time hydro-peel experience.", "2026-10-06 14:00"
            ));
        }
    }

    private void seedReviews() {
        if (reviewRepository.findAll().isEmpty()) {
            // Verified Review from Victoria linked to APT-101
            reviewRepository.save(new VerifiedReview(
                    "REV-001", "CUST-003", "Victoria Sterling", "SRV-003", 
                    "24K Gold Cellular Glow Facial", "STY-002", "Camille Laurent", 
                    5, "The 24K Gold facial with Camille is unmatched! My skin felt plump, hydrated and radiant for days. The VIP salon suite made me feel like royalty.", 
                    "APPROVED", "2026-10-06 18:30", "APT-101"
            ));

            // Verified Review from Sophia linked to APT-102
            reviewRepository.save(new VerifiedReview(
                    "REV-002", "CUST-001", "Sophia Miller", "SRV-001", 
                    "Signature Balayage & Blowout", "STY-001", "Elena Rostova", 
                    5, "Elena is an absolute magician with color. The balayage transition was completely seamless and natural. Love the atmosphere here!", 
                    "APPROVED", "2026-10-06 19:10", "APT-102"
            ));

            // Public community review
            reviewRepository.save(new PublicReview(
                    "REV-003", "CUST-002", "Emma Watson", "SRV-005", 
                    "Velvet Luxe Gel Manicure", "STY-03", "Mateo Silva", 
                    5, "Best manicure in town. Mateo is extremely precise with cuticles and the nail shape was flawless.", 
                    "APPROVED", "2026-10-05 16:45"
            ));
        }
    }
}
