package com.financetracker.finance_tracker_api.entity;

import com.financetracker.finance_tracker_api.entity.enums.CategoryType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="categories")
public class Category extends BaseEntity {
    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type;

    @Column(length = 10)
    private String icon;

    @Column(length = 20)
    private String color;

    @OneToMany(
            mappedBy = "category",
            fetch = FetchType.LAZY
    )
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(
            mappedBy = "category",
            fetch = FetchType.LAZY
    )
    private List<Income> incomes = new ArrayList<>();
}
