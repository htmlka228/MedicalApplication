package org.example.components;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
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
import org.example.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class DoctorEditor extends VerticalLayout implements KeyNotifier {
    private DoctorService doctorService;
    private Doctor doctor;

    private Dialog modalWindow = new Dialog();
    private Dialog validMessage = new Dialog();

    private TextField firstName = new TextField("First name","First name");
    private TextField lastName = new TextField("Last name", "Last name");
    private TextField patronymic = new TextField("Patronymic","Patronymic");
    private TextField specialization = new TextField("Specialization","Specialization");

    private Button save = new Button("Ok", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Cancel");

    private Button close = new Button("Close", VaadinIcon.CLOSE.create());

    private HorizontalLayout buttons = new HorizontalLayout(save, cancel);

    private Binder<Doctor> binder = new BeanValidationBinder<>(Doctor.class);
    private ChangeHandler changeHandler;

    public interface ChangeHandler{
        void onChange();
    }

    @Autowired
    public DoctorEditor(DoctorService doctorService){
        this.doctorService = doctorService;

        setWidthToTextFields();
        setPatternsForTextFields();
        configureModalWindows();

        setBinderFields();

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
        modalWindow.add(new VerticalLayout(lastName, firstName, patronymic, specialization, buttons));

        validMessage.add(new VerticalLayout(close, new H1("You entered an invalid or empty value")));
        close.getElement().getStyle().set("margin-left", "auto");

        close.addClickListener(e -> {
            validMessage.close();
        });
    }

    private void setWidthToTextFields(){
        firstName.setWidthFull();
        lastName.setWidthFull();
        patronymic.setWidthFull();
        specialization.setWidthFull();
    }

    private void setPatternsForTextFields(){
        firstName.setPattern("^[A-Za-zА-Яа-яЁё]+$");
        firstName.setPreventInvalidInput(true);
        lastName.setPattern("^[A-Za-zА-Яа-яЁё]+$");
        lastName.setPreventInvalidInput(true);
        patronymic.setPattern("^[A-Za-zА-Яа-яЁё]+$");
        patronymic.setPreventInvalidInput(true);
        specialization.setPattern("^[A-Za-zА-Яа-яЁё]+$");
        specialization.setPreventInvalidInput(true);
    }

    private void setBinderFields(){
        String emptyFieldMessage = "This field cannot be empty";

        binder.forField(lastName).asRequired(emptyFieldMessage).bind(
                Doctor::getLastName,
                Doctor::setLastName
        );
        binder.forField(firstName).asRequired(emptyFieldMessage).bind(
                Doctor::getFirstName,
                Doctor::setFirstName
        );
        binder.forField(patronymic).asRequired(emptyFieldMessage).bind(
                Doctor::getPatronymic,
                Doctor::setPatronymic
        );
        binder.forField(specialization).asRequired(emptyFieldMessage).bind(
                Doctor::getSpecialization,
                Doctor::setSpecialization
        );
    }

    private void save() {
        if(firstName.getValue().equals("") || lastName.getValue().equals("") || patronymic.getValue().equals("") || specialization.getValue().equals("")){
            validMessage.open();
        }
        else if(!(firstName.isInvalid() || lastName.isInvalid() || patronymic.isInvalid() || specialization.isInvalid())) {
            doctorService.updateDoctor(doctor);
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

    public void editDoctor(Doctor doctor){
        if(doctor == null){
            return;
        }

        if(doctor.getId() != null){
            this.doctor = doctorService.getDoctorById(doctor.getId()).orElse(doctor);
        }

        else {
            this.doctor = doctor;
        }

        binder.setBean(this.doctor);

        modalWindow.open();

        lastName.focus();
    }
}
