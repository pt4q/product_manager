package com.example.application.data.entity.product_associated.product_part;

import com.example.application.data.entity.product_associated.product.ProductEntity;
import com.example.application.data.entity.product_associated.product_manufacturer.ProductManufacturerEntity;
import com.example.application.data.entity.product_associated.product_series.ProductSeriesEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class ProductPartEntity {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    private ProductEntity product;
    @ManyToOne
    private ProductSeriesEntity productSeries;
    @ManyToOne
    private ProductManufacturerEntity productManufacturer;

    private String partName;
    private String partModel;
    private String partDescription;
   
    private LocalDateTime validFromTime;
    private Boolean currentPart;

    private LocalDateTime modificationTime;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] partPicture;
}
