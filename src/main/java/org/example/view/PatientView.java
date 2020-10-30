package org.example.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.example.components.PatientEditor;
import org.example.entity.Doctor;
import org.example.entity.Patient;
import org.example.entity.Recipe;
import org.example.service.PatientService;
import org.example.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "Patient", layout = MainLayout.class)
public class PatientView extends VerticalLayout {
    private final PatientService patientService;
    private final RecipeService recipeService;
    private final PatientEditor editor;

    private Grid<Patient> grid;

    private Dialog deleteError = new Dialog(new H1("Error! You cannot remove a patient who has a recipes"));

    private final TextField filter = new TextField("", "Search patient");
    private final Button addNewBtn = new Button("Add new");
    private final Button changeBtn = new Button("Change");
    private final Button deleteBtn = new Button("Delete");
    private HorizontalLayout toolbar = new HorizontalLayout(filter, addNewBtn, changeBtn, deleteBtn);


    @Autowired
    public PatientView(PatientService patientService, RecipeService recipeService, PatientEditor editor){
        this.patientService = patientService;
        this.recipeService = recipeService;
        this.editor = editor;

        setSizeFull();
        configureGrid();

        add(toolbar, grid, editor);

        addButtonsListeners();

        editor.setChangeHandler(() ->{
            editor.setVisible(false);
            showPatient(filter.getValue());
        });

        showPatient("");
    }

    private void showPatient(String name){
        if(name.isEmpty()){
            grid.setItems(patientService.getPatients());
        }else{
            grid.setItems(patientService.getPatientsByName(name));
        }
    }

    private void configureGrid(){
        grid = new Grid<>(Patient.class);
        grid.setSizeFull();
        grid.setColumns("id", "lastName", "firstName", "patronymic", "phone");
    }

    private void addButtonsListeners(){
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> showPatient(e.getValue()));

        changeBtn.addClickListener(e -> {
            Patient patient = grid.asSingleSelect().getValue();
            editor.editPatient(patient);
        });

        deleteBtn.addClickListener(e -> {
            Patient patient = grid.asSingleSelect().getValue();

            if(!recipeService.getRecipesByPatient(patient).isEmpty()){
                deleteError.open();
            }
            else{
                patientService.deletePatient(patient);
                showPatient("");
            }
        });

        addNewBtn.addClickListener(e -> editor.editPatient(new Patient()));
        addNewBtn.addClickListener(e -> editor.getModalWindow().open());
    }
}
