package pl.com.pt4q.product_manager.modules.environment.ui.master.detail;

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
import pl.com.pt4q.product_manager.modules.environment.data.bat.EnvBatteryEntity;
import pl.com.pt4q.product_manager.modules.environment.data.light_source.EnvLightSourceEntity;
import pl.com.pt4q.product_manager.modules.environment.data.master.EnvMasterEntity;
import pl.com.pt4q.product_manager.modules.environment.data.pack.EnvPackagingEntity;
import pl.com.pt4q.product_manager.modules.environment.data.weee.EnvWeeeEntity;
import pl.com.pt4q.product_manager.modules.environment.services.master.EnvMasterSaverService;
import pl.com.pt4q.product_manager.modules.environment.services.master.EnvMasterFinderService;
import pl.com.pt4q.product_manager.modules.environment.services.master.exceptions.EnvMasterAlreadyExistsException;
import pl.com.pt4q.product_manager.modules.environment.services.master.exceptions.EnvMasterNotFoundException;
import pl.com.pt4q.product_manager.modules.environment.services.weee.EnvWeeeFinderService;
import pl.com.pt4q.product_manager.modules.environment.ui.bat.EnvBatView;
import pl.com.pt4q.product_manager.modules.environment.ui.light_source.EnvLightSourceView;
import pl.com.pt4q.product_manager.modules.environment.ui.master.general.EnvMasterGeneralView;
import pl.com.pt4q.product_manager.modules.environment.ui.pack.EnvPackView;
import pl.com.pt4q.product_manager.modules.environment.ui.weee.EnvWeeeView;
import pl.com.pt4q.product_manager.modules.product.services.product.ProductFinderService;
import pl.com.pt4q.product_manager.modules.product.services.unit.UnitCrudService;
import pl.com.pt4q.product_manager.view_utils.SaveObjectAndBackButtonsDiv;
import pl.com.pt4q.product_manager.views.main.MainView;

import java.util.List;
import java.util.Map;

@Log4j2
@Route(value = EnvMasterDetailView.ROUTE, layout = MainView.class)
@PageTitle(EnvMasterDetailView.PAGE_TITLE)
public class EnvMasterDetailView extends Div implements HasUrlParameter<String> {

    public static final String PAGE_TITLE = EnvMasterGeneralView.PAGE_TITLE + " of product";
    public static final String ROUTE = EnvMasterGeneralView.ROUTE + "-detail";
    public static final String QUERY_PARAM_ID_NAME = "masterId";

    private SaveObjectAndBackButtonsDiv saveObjectAndBackButtonsDiv;
    private EnvMasterDetailEditorDiv masterDetailEditorDiv;
    private AddAdditionalEnvCardsButtonsDiv buttonsDiv;

    private ProductFinderService productFinderService;
    private UnitCrudService unitCrudService;

    private EnvWeeeFinderService weeeFinderService;
    private EnvMasterFinderService masterFinderService;
    private EnvMasterSaverService masterSaverService;

    private EnvMasterEntity envMasterEntity;
//    private EnvWeeeEntity envWeeeEntity;
//    private EnvLightSourceEntity envLightSourceEntity;
//    private EnvBatteryEntity envBatteryEntity;
//    private EnvPackagingEntity envPackagingEntity;

    @Autowired
    public EnvMasterDetailView(ProductFinderService productFinderService,
                               UnitCrudService unitCrudService,
                               EnvWeeeFinderService weeeFinderService,
                               EnvMasterFinderService masterFinderService,
                               EnvMasterSaverService masterSaverService) {

        this.productFinderService = productFinderService;
        this.unitCrudService = unitCrudService;
        this.weeeFinderService = weeeFinderService;
        this.masterFinderService = masterFinderService;
        this.masterSaverService = masterSaverService;

        initializeEntities();

        this.saveObjectAndBackButtonsDiv = new SaveObjectAndBackButtonsDiv("Save master card");
        this.masterDetailEditorDiv = new EnvMasterDetailEditorDiv(this.productFinderService, this.unitCrudService);
        this.buttonsDiv = new AddAdditionalEnvCardsButtonsDiv();

        initAddWeeButton();
        initAddLightSourceButton();
        initAddBatButton();
        initAddPackButton();

        initSaveButton();
        initBackButton();

        populateForm(this.envMasterEntity);

        VerticalLayout layout = new VerticalLayout();
        layout.add(this.saveObjectAndBackButtonsDiv, this.masterDetailEditorDiv, this.buttonsDiv);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSizeFull();

        setSizeFull();
        add(layout);
    }

    private void initializeEntities() {
        UI ui = UI.getCurrent();
        EnvMasterEntity masterEntity = ComponentUtil.getData(ui, EnvMasterEntity.class);
        this.envMasterEntity = masterEntity != null ? masterEntity : new EnvMasterEntity();
//        this.envWeeeEntity = ComponentUtil.getData(ui, EnvWeeeEntity.class);
//        this.envLightSourceEntity = ComponentUtil.getData(ui, EnvLightSourceEntity.class);
//        this.envBatteryEntity = ComponentUtil.getData(ui, EnvBatteryEntity.class);
//        this.envLightSourceEntity = ComponentUtil.getData(ui, EnvLightSourceEntity.class);
//        this.envPackagingEntity = ComponentUtil.getData(ui, EnvPackagingEntity.class);
    }

    private void saveMasterToContext(UI ui, EnvMasterEntity masterEntity) {
        ComponentUtil.setData(ui, EnvMasterEntity.class, masterEntity);
    }

    private void populateForm(EnvMasterEntity masterEntity) {
        this.masterDetailEditorDiv.populateForm(masterEntity);
    }

    private void initAddWeeButton() {
        if (this.envMasterEntity.getWeee() != null)
            this.buttonsDiv.getAddWeeButton().setText("Open WEEE");

        this.buttonsDiv.getAddWeeButton().addClickListener(buttonClickEvent -> {
            saveMasterToContextIfBinderIsValidAndRouteToEndpoint(EnvWeeeView.ROUTE);
        });
    }

    private void initAddLightSourceButton() {
        if (this.envMasterEntity.getLightSource() != null)
            this.buttonsDiv.getAddLightSourceButton().setText("Open LS");

        this.buttonsDiv.getAddLightSourceButton().addClickListener(buttonClickEvent -> {
            saveMasterToContextIfBinderIsValidAndRouteToEndpoint(EnvLightSourceView.ROUTE);
        });
    }

    private void initAddBatButton() {
        if (this.envMasterEntity.getBattery() != null)
            this.buttonsDiv.getAddBatButton().setText("Open BAT");

        this.buttonsDiv.getAddBatButton().addClickListener(buttonClickEvent -> {
            saveMasterToContextIfBinderIsValidAndRouteToEndpoint(EnvBatView.ROUTE);
        });
    }

    private void initAddPackButton() {
        if (this.envMasterEntity.getPackaging() != null)
            this.buttonsDiv.getAddPackButton().setText("Open PACK");

        this.buttonsDiv.getAddPackButton().addClickListener(buttonClickEvent -> {
            saveMasterToContextIfBinderIsValidAndRouteToEndpoint(EnvPackView.ROUTE);
        });
    }

    private void saveMasterToContextIfBinderIsValidAndRouteToEndpoint(String route) {
        this.masterDetailEditorDiv.getMasterBinder().validate();
        if (this.masterDetailEditorDiv.getMasterBinder().isValid()) {
            EnvMasterEntity masterEntityFromForm = this.masterDetailEditorDiv.getMasterBinder().getBean();
            UI ui = UI.getCurrent();
            ComponentUtil.setData(ui, EnvMasterEntity.class, masterEntityFromForm);
            ui.navigate(route);
        }
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();

        if (parametersMap.containsKey(QUERY_PARAM_ID_NAME)) {
            Long id = Long.valueOf(parametersMap.get(QUERY_PARAM_ID_NAME).get(0));
            try {
                this.envMasterEntity = masterFinderService.findByIdOrThrowException(id);
                saveMasterToContext(UI.getCurrent(), this.envMasterEntity);
                populateForm(this.envMasterEntity);
            } catch (EnvMasterNotFoundException e) {
                log.warn(showNotification(e.getMessage()));
            }
        }
    }

    private void initBackButton() {
        this.saveObjectAndBackButtonsDiv.getBackButton().addClickListener(buttonClickEvent -> {
            UI ui = UI.getCurrent();
            saveMasterToContext(ui, null);
            ui.navigate(EnvMasterGeneralView.ROUTE);
        });
    }

    private void initSaveButton() {
        this.saveObjectAndBackButtonsDiv.getSaveButton().addClickListener(buttonClickEvent -> {
            Binder<EnvMasterEntity> formBinder = this.masterDetailEditorDiv.getMasterBinder();
            formBinder.validate().getBeanValidationErrors();

            if (formBinder.isValid()) {
                try {
                    this.envMasterEntity = masterSaverService.create(formBinder.getBean());
                    Notification.show(String.format("%s: Master card has been created for %s", PAGE_TITLE, envMasterEntity.getProduct().getSku()));
                } catch (EnvMasterAlreadyExistsException e) {
                    try {
                        this.envMasterEntity = masterSaverService.update(formBinder.getBean());
                        Notification.show(String.format("%s: Master card has been updated for %s", PAGE_TITLE, envMasterEntity.getProduct().getSku()));
                    } catch (EnvMasterNotFoundException ex) {
                        String errMsg = ex.getMessage();
                        EnvMasterEntity fromBinder = formBinder.getBean();
                        log.error(String.format("%s: %s for object %s", PAGE_TITLE, errMsg, fromBinder.toString()));
                        Notification.show(String.format("%s: Cannot update master card for %s", PAGE_TITLE, fromBinder.getProduct()));
                    }
                }
            }
        });
    }

    private String showNotification(String message) {
        String theWholeMessage = String.format("%s: %s", PAGE_TITLE, message);
        Notification.show(theWholeMessage);
        return theWholeMessage;
    }
}
