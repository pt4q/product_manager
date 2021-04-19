package com.example.application.modules.test_card.data.test_card_part_parameter_category_parameter.parameter_types;

import com.example.application.modules.test_card.data.test_card_part_parameter_category_parameter.ParameterEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class BooleanParameterTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "booleanParameter")
    private ParameterEntity parameter;

    private Boolean requiredValue;

    private Integer points;
}