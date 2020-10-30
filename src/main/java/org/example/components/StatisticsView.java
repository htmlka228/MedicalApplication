package org.example.components;

import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.example.entity.Doctor;
import org.example.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;


@SpringComponent
@UIScope
public class StatisticsView implements KeyNotifier {
    private DoctorService doctorService;
    private Grid<Doctor> grid;
    private Dialog statistics = new Dialog();
    private Button close = new Button("Close", VaadinIcon.CLOSE.create());

    @Autowired
    public StatisticsView(DoctorService doctorService) {
        this.doctorService = doctorService;

        configureGrid();

        close.getElement().getStyle().set("margin-left", "auto");
        close.addClickListener(e -> {
           statistics.close();
        });

        showStatistics();
    }

    public Dialog getStatistics() {
        return statistics;
    }

    private void configureGrid(){
        grid =  new Grid<>(Doctor.class);
        grid.removeAllColumns();
        grid.addColumn(contact -> {
            String name = contact.getLastName() + " " + contact.getFirstName() + " " + contact.getPatronymic();
            return name;
        }).setHeader("Doctor");

        grid.addColumn(contact -> {
            int number = doctorService.getNumberRecipesByDoctor(contact);
            return number;
        }).setHeader("Count recipes");
    }

    private void showStatistics(){
        grid.setItems(doctorService.getDoctors());
        statistics.add(new VerticalLayout(close, grid));
        statistics.setWidth("calc(100vw - (48*var(--lumo-space-m)))");
    }
}
