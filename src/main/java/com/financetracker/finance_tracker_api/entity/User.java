package com.financetracker.finance_tracker_api.entity;

import com.financetracker.finance_tracker_api.entity.enums.UserRole;
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
@Table(
        name="users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
})
public class User extends BaseEntity {
    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private Long telegramChatId;

    @Builder.Default
    private String currency = "INR";

    @Builder.Default
    private String timezone = "Asia/Kolkata";

    @Builder.Default
    private Boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private List<Income> incomes = new ArrayList<>();
}
