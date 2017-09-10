package ml.anon.admin.dashboard;

import com.google.common.collect.Iterables;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import lombok.extern.java.Log;
import ml.anon.admin.BaseView;
import ml.anon.documentmanagement.model.Document;
import ml.anon.documentmanagement.resource.DocumentResource;
import ml.anon.recognition.machinelearning.model.EvaluationData;
import ml.anon.recognition.machinelearning.resource.EvaluationDataResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MGridLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MPanel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by mirco on 16.08.17.
 */
@SpringComponent
@UIScope
@Log
public class DashboardView extends BaseView {

    public static final String ID = "";


    private DocumentResource documentResource;


    private MGrid<Document> docGrid = new MGrid<>(Document.class).withFullSize().withProperties("fileName");

    @Autowired
    public DashboardView(DocumentResource documentResource, EvaluationDataResource evaluationDataResource) {
        super();
        this.documentResource = documentResource;

        docGrid.addColumn(d -> DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(d.getCreated())).setCaption("Created");
        docGrid.addColumn(d -> DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(d.getLastModified())).setCaption("Updated");
        docGrid.addColumn(d -> d.getAnonymizations() == null ? '-' : d.getAnonymizations().size()).setCaption("Anonymizations");

        docGrid.setRows(getContent());
        addComponent(new MVerticalLayout().add(scoreOverview(evaluationDataResource.findById(null)))
                .add(docGrid.withCaption("Zuletzt bearbeitet")));


    }


    private List<Document> getContent() {
        try {
            List<Document> all = documentResource.findAll();
            Collections.sort(all, Comparator.comparing(Document::getLastModified));
            return all.subList(0, Math.min(10, all.size()));
        } catch (Exception e) {
            log.severe(e.getLocalizedMessage());
            return new ArrayList<>();
        }

    }


    private Component scoreOverview(EvaluationData data) {
        try {
            MGridLayout grid = new MGridLayout(2, 6);
            grid.with(label("Erstellte Anonymisierungen"), label(data.getTotalGenerated()));
            grid.with(label("Manuell berichtigt"), label(data.getTotalCorrected()));
            grid.with(label("Korrekt gefunden"), label(data.getTotalNumberOfCorrectFound()));

            grid.with(label("Precision insgesamt"), label(data.getOverallPrecision()));
            grid.with(label("Recall insgesamt"), label(data.getOverallRecall()));
            grid.with(label("F1 insgesamt"), label(data.getOverallFOne()));
            return new MPanel(grid.withMargin(true)).withCaption("Statistik");
        } catch (Exception e) {
            log.severe(e.getLocalizedMessage());
            return new MLabel(e.getLocalizedMessage());
        }


    }

    private MLabel label(Object object) {
        if (object instanceof Double) {
            return new MLabel(NumberFormat.getInstance().format(object));
        }
        return new MLabel(Objects.toString(object));
    }

}
