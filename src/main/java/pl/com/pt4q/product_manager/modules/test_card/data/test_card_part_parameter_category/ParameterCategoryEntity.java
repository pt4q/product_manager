package pl.com.pt4q.product_manager.modules.test_card.data.test_card_part_parameter_category;

import pl.com.pt4q.product_manager.modules.test_card.data.test_card_part_parameter_category_parameter.ParameterEntity;
import pl.com.pt4q.product_manager.modules.test_card.data.test_card_part.TestCardPartEntity;
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
public class ParameterCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String parameterCategoryName;

    private Integer parameterCategoryPoints;

    @ManyToOne
    private TestCardPartEntity testCardPart;

    @OneToMany(mappedBy = "parameterCategory", fetch = FetchType.EAGER)
    private Set<ParameterEntity> parameters;
}