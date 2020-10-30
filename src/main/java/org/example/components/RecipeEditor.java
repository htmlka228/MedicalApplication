package org.example.components;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.example.entity.Doctor;
import org.example.entity.Patient;
import org.example.entity.Priority;
import org.example.entity.Recipe;
import org.example.service.DoctorService;
import org.example.service.PatientService;
import org.example.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@SpringComponent
@UIScope
public class RecipeEditor extends VerticalLayout implements KeyNotifier {
    private DoctorService doctorService;
    private PatientService patientService;
    private RecipeService recipeService;

    private Doctor doctor;
    private Patient patient;
    private Recipe recipe;

    private Dialog modalWindow = new Dialog();
    private Dialog validMessage = new Dialog();

    private TextField description = new TextField("Description","Description");
    private DatePicker creationDate = new DatePicker("Creation date");
    private DatePicker validity = new DatePicker("Validity");
    private ComboBox<String> priority = new ComboBox<>("Priority");
    private ComboBox<Doctor> doctorField = new ComboBox<>("Doctor");
    private ComboBox<Patient> patientField = new ComboBox<>("Patient");

    private Button save = new Button("Ok", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Cancel");

    private Button close = new Button("Close", VaadinIcon.CLOSE.create());

    private HorizontalLayout buttons = new HorizontalLayout(save, cancel);

    private Binder<Recipe> binder = new BeanValidationBinder<>(Recipe.class);
    private ChangeHandler changeHandler;

    public interface ChangeHandler{
        void onChange();
    }

    @Autowired
    public RecipeEditor(DoctorService doctorService, PatientService patientService, RecipeService recipeService){
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.recipeService = recipeService;

        setWidthToTextFields();
        setItemsInComboBoxes();
        configureModalWindows();

        setBinderFields();
        setPatternsForTextFields();

        setSpacing(true);

        save.getElement().getThemeList().add("primary");

        addKeyPressListener(Key.ENTER, e -> save());

        save.addClickListener(e -> save());
        cancel.addClickListener(e -> cancel());

    }

    public void setChangeHandler(ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

    public Dialog getModalWindow() {
        return modalWindow;
    }

    private void configureModalWindows(){
        modalWindow.setWidth("500px");
        modalWindow.add(new VerticalLayout(description, creationDate, validity, priority, doctorField, patientField, buttons));


        validMessage.add(new VerticalLayout(close, new H1("You entered an invalid or empty value")));
        close.getElement().getStyle().set("margin-left", "auto");

        close.addClickListener(e -> {
            validMessage.close();
        });
    }

    private void setWidthToTextFields(){
        description.setWidthFull();
        creationDate.setWidthFull();
        validity.setWidthFull();
        priority.setWidthFull();
        doctorField.setWidthFull();
        patientField.setWidthFull();
    }

    private void setItemsInComboBoxes(){
        priority.setItems(Priority.NORMAL.name(), Priority.CITO.name(), Priority.STATIM.name());
        doctorField.setItems(doctorService.getDoctors());
        doctorField.setItemLabelGenerator(doctor -> (doctor.getLastName() + " " + doctor.getFirstName() + " " + doctor.getPatronymic()));
        patientField.setItems(patientService.getPatients());
        patientField.setItemLabelGenerator(patient -> (patient.getLastName() + " " + patient.getFirstName() + " " + patient.getPatronymic()));
    }

    private void setPatternsForTextFields() {

        description.setPreventInvalidInput(true);

    }

    private void setBinderFields(){
        String emptyFieldMessage = "This field cannot be empty";

        binder.forField(description).asRequired(emptyFieldMessage).bind(
                Recipe::getDescription,
                Recipe::setDescription
        );
        binder.forField(creationDate).asRequired(emptyFieldMessage).bind(
                Recipe::getCreationDate,
                Recipe::setCreationDate
        );
        binder.forField(validity).asRequired(emptyFieldMessage).bind(
                Recipe::getValidity,
                Recipe::setValidity
        );
        binder.forField(priority).asRequired(emptyFieldMessage).bind(
                Recipe::getPriority,
                Recipe::setPriority
        );
        binder.forField(doctorField).asRequired(emptyFieldMessage).bind(
                Recipe::getDoctor,
                Recipe::setDoctor
        );
        binder.forField(patientField).asRequired(emptyFieldMessage).bind(
                Recipe::getPatient,
                Recipe::setPatient
        );
    }

    private void save() {
        if((description.getValue().equals("") || creationDate.getValue() == null || validity.getValue() == null || priority.getValue().isEmpty() || doctorField.getValue() == null || patientField.getValue() == null) ||
                (description.isInvalid() || creationDate.isInvalid() || validity.isInvalid() || priority.isInvalid() || doctorField.isInvalid() || patientField.isInvalid())){

            validMessage.open();
        }
        else{
            recipeService.updateRecipe(recipe);
            changeHandler.onChange();
            modalWindow.close();
        }
    }

    private void cancel(){
        modalWindow.close();
    }

    public void editRecipe(Recipe recipe){
        setItemsInComboBoxes();
        if(recipe == null){
            return;
        }

        if(recipe.getId() != null){
            this.recipe = recipeService.getRecipeById(recipe.getId()).orElse(recipe);
        }

        else {
            this.recipe = recipe;
        }

        binder.setBean(this.recipe);

        Recipe recipe1 = binder.getBean();

        modalWindow.open();

        description.focus();
    }
}
