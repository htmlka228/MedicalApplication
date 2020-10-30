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
import com.vaadin.flow.router.RouterLink;
import org.example.components.DoctorEditor;
import org.example.components.StatisticsView;
import org.example.entity.Doctor;
import org.example.service.DoctorService;
import org.example.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "", layout = MainLayout.class)
public class DoctorView extends VerticalLayout {
    private final DoctorService doctorService;
    private final DoctorEditor editor;
    private final StatisticsView statisticsView;

    private Grid<Doctor> grid;

    private Dialog deleteError = new Dialog(new H1("Error! You cannot remove a doctor who has written recipes"));

    private final TextField filter = new TextField("", "Search doctor");
    private final Button addNewBtn = new Button("Add new");
    private final Button changeBtn = new Button("Change");
    private final Button deleteBtn = new Button("Delete");
    private final Button showStatisticsBtn = new Button("Show statistics");
    private HorizontalLayout toolbar = new HorizontalLayout(filter, addNewBtn, changeBtn, deleteBtn, showStatisticsBtn);


    @Autowired
    public DoctorView(DoctorService doctorService, DoctorEditor editor, StatisticsView statisticsView){
        this.doctorService = doctorService;
        this.editor = editor;
        this.statisticsView = statisticsView;

        setSizeFull();
        configureGrid();

        add(toolbar, grid, editor);

        addButtonsListeners();

        editor.setChangeHandler(() ->{
            editor.setVisible(false);
            showDoctor(filter.getValue());
        });

        showDoctor("");
    }

    private void showDoctor(String name){
        if(name.isEmpty()){
            grid.setItems(doctorService.getDoctors());
        }else{
            grid.setItems(doctorService.getDoctorsByName(name));
        }
    }

    private void configureGrid(){
        grid = new Grid<>(Doctor.class);
        grid.setSizeFull();
        grid.setColumns("id", "lastName", "firstName", "patronymic", "specialization");
    }

    private void addButtonsListeners(){
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> showDoctor(e.getValue()));

        changeBtn.addClickListener(e -> {
            Doctor doctor = grid.asSingleSelect().getValue();
            editor.editDoctor(doctor);
        });

        deleteBtn.addClickListener(e -> {
            Doctor doctor = grid.asSingleSelect().getValue();
            if(!(doctorService.getNumberRecipesByDoctor(doctor) == 0)){
                deleteError.open();
            }
            else{
                doctorService.deleteDoctor(doctor);
                showDoctor("");
            }
        });


        addNewBtn.addClickListener(e -> editor.editDoctor(new Doctor()));
        addNewBtn.addClickListener(e -> editor.getModalWindow().open());

        showStatisticsBtn.addClickListener(e -> {
            statisticsView.getStatistics().open();
        });
    }

}
