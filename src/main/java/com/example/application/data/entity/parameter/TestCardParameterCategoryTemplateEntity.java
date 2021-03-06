package com.example.application.data.entity.parameter;

import com.example.application.data.entity.test_card.TestCardEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class TestCardParameterCategoryTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String parameterCategoryName;

    private Integer parameterCategoryPoints;

    @ManyToOne
    private TestCardEntity testCard;

    @OneToMany(mappedBy = "parameterCategory")
    private Set<TestCardParameterTemplateEntity> parameters;
}
