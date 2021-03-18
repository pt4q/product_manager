package com.example.application.views.test_card.creator;

import com.example.application.data.entity.product_category.ProductCategoryEntity;
import com.example.application.data.entity.test_card_associated.parameter_category.ParameterCategoryEntity;
import com.example.application.data.entity.test_card_associated.test_card.TestCardEntity;
import com.example.application.data.service.test_card_associated.test_card.TestCardFinder;
import com.example.application.data.service.test_card_associated.test_card.TestCardForProductCategoryCreator;
import com.example.application.data.service.test_card_associated.test_card.exceptions.TestCardNotFoundException;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Route(value = TestCardCreatorView.ROUTE, layout = MainView.class)
@PageTitle(TestCardCreatorView.PAGE_TITLE)
public class TestCardCreatorView extends VerticalLayout implements HasUrlParameter<String> {

    public static final String PAGE_TITLE = "Test card creator";
    public static final String ROUTE = "testcard-creator";
    public static final String QUERY_PARAM_ID_NAME = "testCardId";

    private TestCardForProductCategoryCreator testCardForProductCategoryCreator;
    private TestCardFinder testCardFinder;

    private ProductCategoryEntity productCategoryEntity;
    private TestCardEntity testCard;
    private LinkedHashSet<ParameterCategoryEntity> testCardParamCategories;

    private Div testCardInfoDiv;
    private Div testCardCategoriesDiv;

    public TestCardCreatorView(TestCardFinder testCardFinder,
                               TestCardForProductCategoryCreator testCardForProductCategoryCreator) {
        setId(ROUTE);

        this.testCardFinder = testCardFinder;
        this.testCardForProductCategoryCreator = testCardForProductCategoryCreator;
        this.productCategoryEntity = loadProductFromContext();
        this.testCard = initEmptyTestCardForProduct();
        this.testCardInfoDiv = new TestCardInfoDiv(this.testCard);
        this.testCardCategoriesDiv = new TestCardParamCategoriesFactoryDiv(this.testCardParamCategories).create();

        add(testCardInfoDiv, testCardCategoriesDiv);
        setAlignItems(Alignment.CENTER);
        setWidth("80%");
    }

    private TestCardEntity initEmptyTestCardForProduct() {
        return TestCardEntity.builder()
                .productCategory(productCategoryEntity != null ? productCategoryEntity : null)
                .build();
    }

    private ProductCategoryEntity loadProductFromContext() {
        return ComponentUtil.getData(UI.getCurrent(), ProductCategoryEntity.class);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String urlQuery) {
        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();

        if (parametersMap.containsKey(QUERY_PARAM_ID_NAME)) {
            try {
                Long id = Long.valueOf(parametersMap.get(QUERY_PARAM_ID_NAME).get(0));
                this.testCard = testCardFinder.findTestCardById(id);

            } catch (TestCardNotFoundException e) {
                Notification.show(e.getMessage());
            }
        }
    }
}
