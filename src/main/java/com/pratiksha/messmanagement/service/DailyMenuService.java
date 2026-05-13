package com.pratiksha.messmanagement.service;

import java.time.LocalDate;
import java.util.Optional; // 🟢 Naya Import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pratiksha.messmanagement.entity.DailyMenu;
import com.pratiksha.messmanagement.enums.PlanType;
import com.pratiksha.messmanagement.repository.DailyMenuRepository;

@Service
public class DailyMenuService {

    @Autowired
    private DailyMenuRepository dailyMenuRepository;

    // Admin naya menu add karega
    public DailyMenu addMenu(DailyMenu menu) {
        // Option: Tum chaho toh save karne se pehle menu ki date force kar sakti ho
        if (menu.getDate() == null) {
            menu.setDate(LocalDate.now());
        }
        return dailyMenuRepository.save(menu);
    }

    // Bache apne dashboard pe aaj ka menu dekhenge
    public DailyMenu getTodayMenu(PlanType planType) {
        LocalDate today = LocalDate.now();
        
        Optional<DailyMenu> optionalMenu = dailyMenuRepository.findByDateAndPlanType(today, planType);
        
        // 🟢 NAYA LOGIC: Agar menu mil gaya toh wahi bhejo, warna ek blank (empty) menu bhejo
        // Isse React kabhi error state me nahi jayega!
        if (optionalMenu.isPresent()) {
            return optionalMenu.get();
        } else {
            // Return empty object instead of throwing exception
            DailyMenu emptyMenu = new DailyMenu();
            emptyMenu.setPlanType(planType);
            emptyMenu.setDate(today);
            emptyMenu.setLunch("");
            emptyMenu.setDinner("");
            return emptyMenu;
        }
    }
}