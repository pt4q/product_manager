package com.example.application.modules.test_card.ui.test_card.main_view;

import com.example.application.modules.test_card.data.test_card.TestCardEntity;
import com.example.application.modules.test_card.ui.test_card.test_card_creator.TestCardCreatorView;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;

class AddNewTestCardToGridDiv extends Div {

    private Label testCardPartToAddLabel = new Label("add new test card");
    private Button addNewTestCardPartButton = new Button(testCardPartToAddLabel);

    private TestCardEntity testCardEntity;

    public AddNewTestCardToGridDiv() {
        initAddNewTestCardPartButton();
        add(addNewTestCardPartButton);
    }

    private void initAddNewTestCardPartButton() {
        this.addNewTestCardPartButton.addClickListener(e -> {
            this.testCardEntity = TestCardEntity
                    .builder()
                    .build();

            ComponentUtil.setData(UI.getCurrent(), TestCardEntity.class, testCardEntity);
            UI.getCurrent().navigate(TestCardCreatorView.ROUTE);
        });
    }
}