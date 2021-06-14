package pl.com.pt4q.product_manager.modules.product_parts.ui.product_part;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import pl.com.pt4q.product_manager.modules.product.data.product.ProductEntity;
import pl.com.pt4q.product_manager.modules.product_parts.data.product_part.ProductPartEntity;
import pl.com.pt4q.product_manager.modules.product_parts.services.product_part.ProductPartCreatorService;
import pl.com.pt4q.product_manager.modules.product_parts.services.product_part.ProductPartUpdaterService;
import pl.com.pt4q.product_manager.modules.product_parts.services.product_part_attribute.ProductPartAttributeFinderService;
import pl.com.pt4q.product_manager.modules.product_parts.services.product_part.ProductPartFinderService;
import pl.com.pt4q.product_manager.modules.product_parts.services.product_part.exceptions.ProductPartAlreadyExistsException;
import pl.com.pt4q.product_manager.modules.product_parts.services.product_part.exceptions.ProductPartAttributeNotFoundException;
import pl.com.pt4q.product_manager.modules.product_parts.services.product_part.exceptions.ProductPartNotFoundException;
import pl.com.pt4q.product_manager.modules.product.services.product_series.ProductSeriesCrudService;
import pl.com.pt4q.product_manager.modules.product.ui.product.detail.ProductDetailView;
import pl.com.pt4q.product_manager.modules.product.ui.product.general.ProductsGeneralView;
import pl.com.pt4q.product_manager.view_utils.SaveObjectAndBackButtonsDiv;
import pl.com.pt4q.product_manager.views.main.MainView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Route(value = ProductPartDetailView.ROUTE, layout = MainView.class)
@PageTitle(ProductPartDetailView.PAGE_TITLE)
public class ProductPartDetailView extends VerticalLayout implements HasUrlParameter<String> {

    public static final String PAGE_TITLE = "Product part detail";
    public static final String ROUTE = "product-part-detail";
    public static final String QUERY_PARAM_ID_NAME = "productPartId";

    private SaveObjectAndBackButtonsDiv saveProductPartOrBackButtonsDiv;
    private ProductPartFormDiv productPartFormDiv;
    private ProductPartAttributesGridDiv productPartAttributesGridDiv;

    private ProductSeriesCrudService productSeriesCrudService;
    private ProductPartCreatorService productPartCreatorService;
    private ProductPartUpdaterService productPartUpdaterService;
    private ProductPartFinderService productPartFinderService;
    private ProductPartAttributeFinderService productPartAttributeFinderService;

    private ProductPartEntity productPart;

    @Autowired
    public ProductPartDetailView(ProductSeriesCrudService productSeriesCrudService,
                                 ProductPartFinderService productPartFinderService,
                                 ProductPartCreatorService productPartCreatorService,
                                 ProductPartUpdaterService productPartUpdaterService,
                                 ProductPartAttributeFinderService productPartAttributeFinderService) {

        this.productSeriesCrudService = productSeriesCrudService;
        this.productPartFinderService = productPartFinderService;
        this.productPartCreatorService = productPartCreatorService;
        this.productPartUpdaterService = productPartUpdaterService;
        this.productPartAttributeFinderService = productPartAttributeFinderService;

        this.productPart = getProductPartFromContext();
//        ifPartIsNullThenRedirectToProductDetailView(productPart);

        this.saveProductPartOrBackButtonsDiv = new SaveObjectAndBackButtonsDiv("Save part");
        this.productPartFormDiv = new ProductPartFormDiv(this.productPart);
        this.productPartAttributesGridDiv = new ProductPartAttributesGridDiv(this.productPart, productSeriesCrudService, productPartCreatorService, productPartAttributeFinderService);

        initSaveButtonAction();
        initBackButtonAction();
        populateProductPartForm(productPart);
        refreshPartAttributesGrid(productPart);

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setHorizontalComponentAlignment(Alignment.CENTER);

        add(
                saveProductPartOrBackButtonsDiv,
                productPartFormDiv,
                productPartAttributesGridDiv
        );
    }

    private ProductPartEntity getProductPartFromContext() {
        return ComponentUtil.getData(UI.getCurrent(), ProductPartEntity.class);
    }

    //// TODO: 14.05.2021 Serialize and deserialize object using cookies -> view_utils -> ObjectToCookieManager.class
    private void ifPartIsNullThenRedirectToProductDetailView(ProductPartEntity productPart) {
        if (productPart == null)
            UI.getCurrent().navigate(ProductsGeneralView.ROUTE);
    }

    private void saveProductPartToContext(ProductPartEntity productPart) {
        ComponentUtil.setData(UI.getCurrent(), ProductPartEntity.class, productPart);
    }

    private void populateProductPartForm(ProductPartEntity part) {
        if (part != null)
            this.productPartFormDiv.populatePartForm(part);
    }

    private void refreshPartAttributesGrid(ProductPartEntity part) {
//        this.partAttributesDiv.refreshAttributesGrid(part);
        try {
            this.productPartAttributesGridDiv.refreshGrid(productPartAttributeFinderService.findAllProductPartsAttributesByProductPart(part));
        } catch (ProductPartAttributeNotFoundException e) {
            this.productPartAttributesGridDiv.refreshGrid(Collections.emptyList());
        }
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String urlQuery) {
        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();

        if (parametersMap.containsKey(QUERY_PARAM_ID_NAME)) {
            Long id = Long.valueOf(parametersMap.get(QUERY_PARAM_ID_NAME).get(0));
            try {
                this.productPart = productPartFinderService.findByIdOrThrowException(id);
                saveProductPartToContext(this.productPart);
                populateProductPartForm(this.productPart);
                refreshPartAttributesGrid(this.productPart);
            } catch (ProductPartNotFoundException e) {
//                Notification.show(e.getMessage());
            }
        }
    }

    private void initSaveButtonAction() {
        this.saveProductPartOrBackButtonsDiv.getSaveButton().addClickListener(buttonClickEvent -> {
            try {
                ProductPartEntity partFromForm = this.productPartFormDiv.getPartFromForm();
                this.productPart = productPartCreatorService.create(partFromForm);

                Notification.show(String.format("The part %s has been saved in the product %s", productPart.getPartModelOrPartName(), productPart.getProduct().getSku()));
            } catch (ProductPartAlreadyExistsException e) {
                try {
                    this.productPart = productPartUpdaterService.update(productPart);

                    Notification.show(String.format("The part %s has been updated in the product %s", productPart.getPartModelOrPartName(), productPart.getProduct().getSku()));
                } catch (ProductPartNotFoundException ex) {
                    Notification.show(String.format("Can't save part because: %s", ex.getMessage()));
                }
            }
        });
    }

    private void initBackButtonAction() {
        this.saveProductPartOrBackButtonsDiv.getBackButton().addClickListener(buttonClickEvent -> {
            UI ui = UI.getCurrent();
            if (productPart != null) {
                ProductEntity product = productPart.getProduct();
                ComponentUtil.setData(ui, ProductEntity.class, product);
            }
            ui.navigate(ProductDetailView.ROUTE);
        });
    }
}
