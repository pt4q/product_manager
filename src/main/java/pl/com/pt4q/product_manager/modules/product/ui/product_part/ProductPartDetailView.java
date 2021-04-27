package pl.com.pt4q.product_manager.modules.product.ui.product_part;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import pl.com.pt4q.product_manager.modules.product.data.product.ProductEntity;
import pl.com.pt4q.product_manager.modules.product.data.product_part.ProductPartEntity;
import pl.com.pt4q.product_manager.views.main.MainView;

import java.util.List;
import java.util.Map;

@Route(value = ProductPartDetailView.ROUTE, layout = MainView.class)
@PageTitle(ProductPartDetailView.PAGE_TITLE)
public class ProductPartDetailView extends VerticalLayout implements HasUrlParameter<String> {

    public static final String PAGE_TITLE = "Product part detail";
    public static final String ROUTE = "product-part-detail";
    public static final String QUERY_PARAM_ID_NAME = "productPartId";

    private PartFormDiv partFormDiv;
    private SaveProductPartOrBackButtonsDiv saveProductPartOrBackButtonsDiv;

    private ProductEntity product;
    private ProductPartEntity productPart;

    public ProductPartDetailView() {
        this.product = getProductFromContext();
        this.productPart = initEmptyProductPart();

        this.partFormDiv = new PartFormDiv(productPart);
        this.saveProductPartOrBackButtonsDiv = new SaveProductPartOrBackButtonsDiv(productPart);

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setHorizontalComponentAlignment(Alignment.CENTER);

        add(
                partFormDiv,
                saveProductPartOrBackButtonsDiv
        );
    }

    private ProductPartEntity initEmptyProductPart(){
        return new ProductPartEntity();
    }

    private ProductEntity getProductFromContext() {
        return product != null ? product : new ProductEntity();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String urlQuery) {
        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();

        if (parametersMap.containsKey(QUERY_PARAM_ID_NAME)) {
//            try {
            Long id = Long.valueOf(parametersMap.get(QUERY_PARAM_ID_NAME).get(0));
//                this.testCard = testCardFinder.findTestCardById(id);

//            } catch (TestCardNotFoundException e) {
//                Notification.show(e.getMessage());
//            }
        }
    }
}