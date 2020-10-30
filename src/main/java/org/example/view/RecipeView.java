package org.example.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.example.components.RecipeEditor;
import org.example.entity.Patient;
import org.example.entity.Priority;
import org.example.entity.Recipe;
import org.example.service.DoctorService;
import org.example.service.PatientService;
import org.example.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "Recipe", layout = MainLayout.class)
public class RecipeView extends VerticalLayout {
    private final RecipeService recipeService;

    private final PatientService patientService;

    private final DoctorService doctorService;

    private final RecipeEditor editor;

    private Grid<Recipe> grid;

    private final Button addNewBtn = new Button("Add new");
    private final Button changeBtn = new Button("Change");
    private final Button deleteBtn = new Button("Delete");

    private final H1 filterHeader = new H1("Filter");
    private final TextField filterPatient = new TextField("", "Patient");
    private final ComboBox<String> filterPriority = new ComboBox<>("", Priority.NORMAL.name(), Priority.CITO.name(), Priority.STATIM.name());
    private final TextField filterDescription = new TextField("", "Description");
    private final Button apply = new Button("Apply");

    private HorizontalLayout toolbar = new HorizontalLayout(addNewBtn, changeBtn, deleteBtn);
    private HorizontalLayout filterBar = new HorizontalLayout( filterHeader, filterPatient, filterPriority, filterDescription, apply);


    @Autowired
    public RecipeView(RecipeService recipeService, DoctorService doctorService, PatientService patientService, RecipeEditor editor){
        this.recipeService = recipeService;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.editor = editor;

        setSizeFull();

        configureComboBoxes();

        setStylingFilter();

        filterHeader.addClassName("filterHeader");

        configureGrid();

        add(toolbar, filterBar, grid, editor);

        addButtonsListeners();

        editor.setChangeHandler(() ->{
            editor.setVisible(false);
            showRecipe("", "", "");
        });

        showRecipe("", "", "");
    }

    private void showRecipe(String namePatient, String priority, String description){
        if(priority == null) {priority = "";}

            List<Recipe> recipes = recipeService.getRecipes();
            String finalPriority = priority;
            grid.setItems(recipes.stream().filter(e -> e.getDescription().contains(description)).filter(e -> {
                String name = e.getPatient().getLastName() + " " + e.getPatient().getFirstName() + " " + e.getPatient().getPatronymic();
                return name.contains(namePatient);
            }).filter(e -> e.getPriority().contains(finalPriority)).collect(Collectors.toList()));
    }

    private void configureGrid(){
        grid = new Grid<>(Recipe.class);
        grid.setSizeFull();
        grid.removeColumnByKey("patient");
        grid.removeColumnByKey("doctor");
        grid.setColumns("id", "description", "creationDate", "validity", "priority");

        grid.addColumn(contact ->{
            String name = contact.getDoctor().getLastName() + " " + contact.getDoctor().getFirstName() + " " + contact.getDoctor().getPatronymic();
            return name;
        }).setHeader("Doctor");

        grid.addColumn(contact -> {
            String name = contact.getPatient().getLastName() + " " + contact.getPatient().getFirstName() + " " + contact.getPatient().getPatronymic();
            return name;
        }).setHeader("Patient");
    }

    private void configureComboBoxes(){
        filterPriority.setPlaceholder("Priority");
    }

    private void setStylingFilter(){
        filterHeader.getElement().getStyle().set("margin", "0");
        filterHeader.getElement().getStyle().set("padding", "0");
        filterHeader.getElement().getStyle().set("padding-left", "15px");
        filterHeader.getElement().getStyle().set("font-size", "33px");
    }

    private void addButtonsListeners(){
        deleteBtn.addClickListener(e ->{
            Recipe recipe = grid.asSingleSelect().getValue();
            recipeService.deleteRecipe(recipe);
            if(filterPatient.getValue() == null){
                showRecipe("", filterPriority.getValue(), filterDescription.getValue());
            }
            else{
                showRecipe(filterPatient.getValue(), filterPriority.getValue(), filterDescription.getValue());
            }
        });

        changeBtn.addClickListener(e -> {
            Recipe recipe = grid.asSingleSelect().getValue();
            editor.editRecipe(recipe);
        });

        apply.addClickListener(e -> {
            if(filterPatient.getValue() == null){
                showRecipe("", filterPriority.getValue(), filterDescription.getValue());
            }
            else{
                showRecipe(filterPatient.getValue(), filterPriority.getValue(), filterDescription.getValue());
            }
        });

        addNewBtn.addClickListener(e -> editor.editRecipe(new Recipe()));
        addNewBtn.addClickListener(e -> editor.getModalWindow().open());
    }
}
