package com.studioparametric.officeservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "option_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_value", nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private ItemOption option;
}
