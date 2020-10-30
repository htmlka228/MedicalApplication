package org.example.components;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.example.entity.Doctor;
import org.example.entity.Patient;
import org.example.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;


@SpringComponent
@UIScope
public class PatientEditor extends VerticalLayout implements KeyNotifier {
    private PatientService patientService;
    private Patient patient;

    private Dialog modalWindow = new Dialog();
    private Dialog validMessage = new Dialog();

    private TextField firstName = new TextField("First name","First name");
    private TextField lastName = new TextField("Last name", "Last name");
    private TextField patronymic = new TextField("Patronymic","Patronymic");
    private TextField phone = new TextField("Phone (format +7xxxxxxxxxx)","Phone");

    private Button save = new Button("Ok", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Cancel");

    private Button close = new Button("Close", VaadinIcon.CLOSE.create());

    private HorizontalLayout buttons = new HorizontalLayout(save, cancel);

    private Binder<Patient> binder = new BeanValidationBinder<>(Patient.class);
    private PatientEditor.ChangeHandler changeHandler;

    public interface ChangeHandler{
        void onChange();
    }

    @Autowired
    public PatientEditor(PatientService patientService){
        this.patientService = patientService;

        setWidthToTextFields();
        setPatternsForTextFields();
        configureModalWindows();

        setBinderFields();

        setSpacing(true);

        save.getElement().getThemeList().add("primary");

        addKeyPressListener(Key.ENTER, e -> save());

        save.addClickListener(e -> save());
        cancel.addClickListener(e -> cancel());


        setVisible(false);
    }

    public void setChangeHandler(PatientEditor.ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

    public Dialog getModalWindow() {
        return modalWindow;
    }

    private void configureModalWindows(){
        modalWindow.setWidth("500px");
        modalWindow.add(new VerticalLayout(lastName, firstName, patronymic, phone, buttons));

        validMessage.add(new VerticalLayout(close, new H1("You entered an invalid or empty value")));
        close.getElement().getStyle().set("margin-left", "auto");

        close.addClickListener(e -> {
            validMessage.close();
        });
    }

    private void setPatternsForTextFields(){
        firstName.setPattern("^[A-Za-zА-Яа-яЁё]+$");
        firstName.setPreventInvalidInput(true);
        lastName.setPattern("^[A-Za-zА-Яа-яЁё]+$");
        lastName.setPreventInvalidInput(true);
        patronymic.setPattern("^[A-Za-zА-Яа-яЁё]+$");
        patronymic.setPreventInvalidInput(true);
        phone.setPattern("\\+7\\s?[\\(]{0,1}9[0-9]{2}[\\)]{0,1}\\s?\\d{3}[-]{0,1}\\d{2}[-]{0,1}\\d{2}");
    }

    private void setWidthToTextFields(){
        firstName.setWidthFull();
        lastName.setWidthFull();
        patronymic.setWidthFull();
        phone.setWidthFull();
    }

    private void setBinderFields(){
        String emptyFieldMessage = "This field cannot be empty";

        binder.forField(lastName).asRequired(emptyFieldMessage).bind(
                Patient::getLastName,
                Patient::setLastName
        );
        binder.forField(firstName).asRequired(emptyFieldMessage).bind(
                Patient::getFirstName,
                Patient::setFirstName
        );
        binder.forField(patronymic).asRequired(emptyFieldMessage).bind(
                Patient::getPatronymic,
                Patient::setPatronymic
        );
        binder.forField(phone).withValidator(new RegexpValidator("Invalid value","\\+7\\s?[\\(]{0,1}[0-9]{3}[\\)]{0,1}\\s?\\d{3}[-]{0,1}\\d{2}[-]{0,1}\\d{2}")).asRequired(emptyFieldMessage).bind(
                Patient::getPhone,
                Patient::setPhone
        );
    }

    private void save() {
        if(firstName.getValue().equals("") || lastName.getValue().equals("") || patronymic.getValue().equals("") || phone.getValue().equals("")){
            validMessage.open();
        }
        else if(!(firstName.isInvalid() || lastName.isInvalid() || patronymic.isInvalid() || phone.isInvalid())) {
            patientService.updatePatient(patient);
            changeHandler.onChange();
            modalWindow.close();
        }
        else{
            validMessage.open();
        }
    }

    private void cancel(){
        modalWindow.close();
    }

    public void editPatient(Patient patient){
        if(patient == null){
            setVisible(false);
            return;
        }

        if(patient.getId() != null){
            this.patient = patientService.getPatientById(patient.getId()).orElse(patient);
        }

        else {
            this.patient = patient;
        }

        binder.setBean(this.patient);

        modalWindow.open();

        setVisible(true);

        lastName.focus();
    }
}
