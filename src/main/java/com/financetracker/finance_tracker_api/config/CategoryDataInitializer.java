package com.financetracker.finance_tracker_api.config;

import com.financetracker.finance_tracker_api.entity.Category;
import com.financetracker.finance_tracker_api.entity.enums.CategoryType;
import com.financetracker.finance_tracker_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryDataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            log.info("Seeding default categories into database...");

            List<Category> defaultCategories = List.of(
                    Category.builder().name("Food").type(CategoryType.EXPENSE).icon("🍔").color("#FF2A85").build(),
                    Category.builder().name("Transport").type(CategoryType.EXPENSE).icon("🚗").color("#FFE600").build(),
                    Category.builder().name("Shopping").type(CategoryType.EXPENSE).icon("🛍️").color("#00E5FF").build(),
                    Category.builder().name("Entertainment").type(CategoryType.EXPENSE).icon("🎬").color("#A855F7").build(),
                    Category.builder().name("Bills").type(CategoryType.EXPENSE).icon("⚡").color("#EF4444").build(),
                    Category.builder().name("Salary").type(CategoryType.INCOME).icon("💼").color("#10B981").build(),
                    Category.builder().name("Freelance").type(CategoryType.INCOME).icon("💻").color("#3B82F6").build(),
                    Category.builder().name("Investments").type(CategoryType.INCOME).icon("📈").color("#8B5CF6").build()
            );

            categoryRepository.saveAll(defaultCategories);
            log.info("Default categories seeded successfully!");
        }
    }
}
