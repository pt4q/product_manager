package pl.com.pt4q.product_manager.modules.product.services.product;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.pt4q.product_manager.modules.product.data.product.ProductEntity;
import pl.com.pt4q.product_manager.modules.product.services.product.exceptions.ProductAlreadyExistsException;
import pl.com.pt4q.product_manager.modules.product.services.product.exceptions.ProductNotFoundException;
import pl.com.pt4q.product_manager.modules.product.services.product.exceptions.ProductValidatorException;

@Log4j2
@Service
public class AddNewProductService {

    @Autowired
    private ProductCrudFinder productCrudFinder;
    @Autowired
    private ProductCrudSaver productCrudSaver;

    public ProductEntity add(ProductEntity product) throws ProductAlreadyExistsException, ProductValidatorException {
        new ProductValidator(product)
                .validate();

        try {
            product = findProduct(product.getId());
            throw new ProductAlreadyExistsException(String.format("Product already exists on id:%d", product.getId()));
        } catch (ProductNotFoundException e) {
            return productCrudSaver.save(product);
        }
    }

    private ProductEntity findProduct(Long id) throws ProductNotFoundException {
        return productCrudFinder.findByIdOrThrowException(id);
    }
}
