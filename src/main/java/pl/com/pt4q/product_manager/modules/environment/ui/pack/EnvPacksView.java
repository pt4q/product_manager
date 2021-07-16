package pl.com.pt4q.product_manager.modules.environment.ui.pack;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import pl.com.pt4q.product_manager.modules.environment.data.master.EnvMasterEntity;
import pl.com.pt4q.product_manager.modules.environment.data.pack.EnvPackagingEntity;
import pl.com.pt4q.product_manager.modules.environment.services.master.EnvMasterFinderService;
import pl.com.pt4q.product_manager.modules.environment.services.master.EnvMasterSaverService;
import pl.com.pt4q.product_manager.modules.environment.services.master.exceptions.EnvMasterNotFoundException;
import pl.com.pt4q.product_manager.modules.environment.services.pack.EnvPackSaverService;
import pl.com.pt4q.product_manager.modules.environment.services.weee.exceptions.EnvWeeeAlreadyExistsException;
import pl.com.pt4q.product_manager.modules.environment.ui.master.detail.EnvMasterDetailView;
import pl.com.pt4q.product_manager.modules.product.services.unit.UnitCrudService;
import pl.com.pt4q.product_manager.view_utils.SaveObjectAndBackButtonsDiv;
import pl.com.pt4q.product_manager.views.main.MainView;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
@Route(value = EnvPacksView.ROUTE, layout = MainView.class)
@PageTitle(EnvPacksView.PAGE_TITLE)
public class EnvPacksView extends Div implements HasUrlParameter<String> {

    public static final String PAGE_TITLE = "Product PACK card";
    public static final String ROUTE = EnvMasterDetailView.ROUTE + "-pack";
    public static final String QUERY_PARAM_ID_NAME = "productId";

    private SaveObjectAndBackButtonsDiv saveObjectAndBackButtonsDiv = new SaveObjectAndBackButtonsDiv("Save PACK card");
    private EnvPackEditorDiv packEditorDiv;

    private EnvPackSaverService envPackSaverService;
    private EnvMasterFinderService envMasterFinderService;
    private EnvMasterSaverService envMasterSaverService;
    private UnitCrudService unitCrudService;

    private EnvMasterEntity envMasterEntity;

    @Autowired
    public EnvPacksView(EnvPackSaverService envPackSaverService,
                        EnvMasterFinderService envMasterFinderService,
                        EnvMasterSaverService envMasterSaverService,
                        UnitCrudService unitCrudService) {

        this.envPackSaverService = envPackSaverService;
        this.envMasterFinderService = envMasterFinderService;
        this.envMasterSaverService = envMasterSaverService;
        this.unitCrudService = unitCrudService;

        this.envMasterEntity = getMasterEntityFromContext();
        if (this.envMasterEntity != null)
            this.envMasterEntity.setPacks(getPackFromMasterOrInitNew(this.envMasterEntity));

        this.packEditorDiv = new EnvPackEditorDiv(this.unitCrudService, this.envMasterEntity);

        initSaveButton();
        initBackButton();

//        if (this.envMasterEntity != null)
//            populatePackForm(this.envMasterEntity.getPackaging());

        VerticalLayout layout = new VerticalLayout();
        layout.add(this.saveObjectAndBackButtonsDiv, this.packEditorDiv);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSizeFull();

        setSizeFull();
        add(layout);
    }

    private Set<EnvPackagingEntity> getPackFromMasterOrInitNew(EnvMasterEntity masterEntity) {
        if (masterEntity != null)
            return envMasterEntity.getPacks() != null ? envMasterEntity.getPacks() : new HashSet<>();
        else
            return null;
    }

    private void populatePackForm(EnvPackagingEntity packagingEntity) {
        this.packEditorDiv.populateForm(packagingEntity);
    }

    private void initSaveButton() {
        this.saveObjectAndBackButtonsDiv.getSaveButton().addClickListener(buttonClickEvent -> {
            Binder<EnvPackagingEntity> formBinder = this.packEditorDiv.getPackEntityBinder();
            formBinder.validate().getBeanValidationErrors();

            if (formBinder.isValid()) {
                try {
                    this.envMasterEntity = envPackSaverService.createPackAndAddItToMaster(this.envMasterEntity, formBinder.getBean());
                    saveMasterToContext(this.envMasterEntity);
                    Notification.show(String.format("%s: PACK card has been created for %s", PAGE_TITLE, this.envMasterEntity.getProduct().getSku()));

                } catch (EnvWeeeAlreadyExistsException e) {
                    try {
//                        this.envMasterEntity.setPackaging(formBinder.getBean());
                        this.envMasterEntity = envMasterSaverService.update(this.envMasterEntity);

                    } catch (EnvMasterNotFoundException ex) {
                        String errMsg = ex.getMessage();
                        EnvPackagingEntity fromBinder = formBinder.getBean();
                        log.error(String.format("%s: %s for object %s", PAGE_TITLE, errMsg, fromBinder.toString()));
                        Notification.show(String.format("%s: Cannot update PACK card for %s", PAGE_TITLE, this.envMasterEntity.getProduct()));
                    }
                }
            }
        });
    }

    private void initBackButton() {
        this.saveObjectAndBackButtonsDiv.getBackButton().addClickListener(buttonClickEvent -> {
            Binder<EnvPackagingEntity> packagingEntityBinder = this.packEditorDiv.getPackEntityBinder();

            if (packagingEntityBinder.getBean() != null && packagingEntityBinder.getBean().getId() != null) {
//                this.envMasterEntity.setPackaging(packagingEntityBinder.getBean());
            }
            else {
                this.envMasterEntity.setPacks(null);
                showNotification(String.format("PACK card for %s product has not been saved", envMasterEntity.getProduct().getSku()));
            }

            saveMasterToContext(this.envMasterEntity);
            UI.getCurrent().navigate(EnvMasterDetailView.ROUTE);
        });
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();

        if (parametersMap.containsKey(QUERY_PARAM_ID_NAME)) {
            Long id = Long.valueOf(parametersMap.get(QUERY_PARAM_ID_NAME).get(0));
            try {
                this.envMasterEntity = envMasterFinderService.findByIdOrThrowException(id);
                saveMasterToContext(this.envMasterEntity);
//                populatePackForm(this.envMasterEntity.getPackaging());
            } catch (EnvMasterNotFoundException e) {
                log.warn(showNotification(e.getMessage()));
            }
        }
    }

    private String showNotification(String message) {
        String theWholeMessage = String.format("%s: %s", PAGE_TITLE, message);
        Notification.show(theWholeMessage);
        return theWholeMessage;
    }

    private void saveMasterToContext(EnvMasterEntity master) {
        ComponentUtil.setData(UI.getCurrent(), EnvMasterEntity.class, master);
    }

    private EnvMasterEntity getMasterEntityFromContext() {
        return ComponentUtil.getData(UI.getCurrent(), EnvMasterEntity.class);
    }
}