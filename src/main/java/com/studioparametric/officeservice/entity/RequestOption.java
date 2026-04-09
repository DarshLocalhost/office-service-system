package com.studioparametric.officeservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "request_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Request request;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(name = "selected_value", nullable = false)
    private String selectedValue;
}
