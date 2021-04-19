package com.example.application.modules.test_card.data.test_card;

import com.example.application.modules.product.data.product_category.ProductCategoryEntity;
import com.example.application.modules.test_card.data.test_card_part.TestCardPartEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class TestCardEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String testCardName;
    private Integer points = 100;

    private LocalDateTime creationTime;
    private LocalDateTime modificationTime;

    @OneToOne
    private ProductCategoryEntity productCategory;

    @OneToMany(mappedBy = "testCard", fetch = FetchType.EAGER)
    private Set<TestCardPartEntity> testCardParts;

}