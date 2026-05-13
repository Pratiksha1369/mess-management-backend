package com.pratiksha.messmanagement.controller;

import java.util.List;
import com.pratiksha.messmanagement.repository.DailyMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.pratiksha.messmanagement.entity.DailyMenu;
import com.pratiksha.messmanagement.enums.PlanType;
import com.pratiksha.messmanagement.service.DailyMenuService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/menu")
public class DailyMenuController {

    @Autowired
    private DailyMenuRepository dailyMenuRepository;
    
    @Autowired // 🟢 GALTI 1 THEEK KI: Yeh lagana bohot zaroori tha
    private DailyMenuService dailyMenuService;

    // POST API: Admin roz raat ko agle din ka (ya aaj subah) menu set karega
    @PostMapping("/add")
    public DailyMenu addMenu(@RequestBody DailyMenu menu) {
        return dailyMenuService.addMenu(menu);
    }

    // GET API: Dashboard ke liye aaj ka menu lana
    @GetMapping("/today")
    public DailyMenu getTodayMenu(@RequestParam PlanType planType) {
        return dailyMenuService.getTodayMenu(planType);
    }
    
    // 1. Saare menus dekhne ke liye
    @GetMapping("/all")
    public List<DailyMenu> getAllMenus() {
        // 🟢 GALTI 2 THEEK KI: Jab repository already hai, toh direct data nikal lo
        return dailyMenuRepository.findAll(); 
    }

    // 2. Galat menu delete karne ke liye
    @DeleteMapping("/{id}")
    public String deleteMenu(@PathVariable Long id) {
        dailyMenuRepository.deleteById(id);
        return "Menu deleted successfully";
    }
}