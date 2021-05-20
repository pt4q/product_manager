package pl.com.pt4q.product_manager.modules.product.data.product_part;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.com.pt4q.product_manager.modules.product.data.product_series.ProductSeriesEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class ProductPartAttributeEntity {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    private ProductPartEntity part;

    private String attributeName;
    private String attributeValue;
    @ManyToOne(fetch = FetchType.EAGER)
    private ProductSeriesEntity productSeries;
    private LocalDate validFromDate;

    @OneToOne
    private ProductPartAttributeEntity previousAttribute;
    private boolean actualAttribute;
}
