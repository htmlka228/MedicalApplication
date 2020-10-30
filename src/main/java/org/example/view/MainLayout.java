package org.example.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.HighlightCondition;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.example.components.DoctorEditor;
import org.example.entity.Doctor;
import org.example.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;


public class MainLayout extends AppLayout {

    public MainLayout(){
        createHeader();
        createDrawer();
    }



    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle());
        header.addClassName("header");
        header.setWidth("100%");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink doctorLink = new RouterLink("Doctors", DoctorView.class);
        RouterLink patientLink = new RouterLink("Patient", PatientView.class);
        RouterLink recipeLink = new RouterLink("Recipe", RecipeView.class);
        doctorLink.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(
                doctorLink,
                patientLink,
                recipeLink
        ));
    }
}
