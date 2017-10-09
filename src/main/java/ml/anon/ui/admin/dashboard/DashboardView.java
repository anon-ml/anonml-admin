package ml.anon.ui.admin.dashboard;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import lombok.extern.java.Log;
import ml.anon.ui.common.BaseView;
import ml.anon.documentmanagement.model.Document;
import ml.anon.documentmanagement.resource.DocumentResource;
import ml.anon.recognition.machinelearning.model.EvaluationData;
import ml.anon.recognition.machinelearning.resource.EvaluationDataResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MGridLayout;
import org.vaadin.viritin.layouts.MPanel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.text.DateFormat;
import java.text.NumberFormat;
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
        addComponent(new MVerticalLayout().add(scoreOverview(evaluationDataResource))
                .add(docGrid.withCaption("Last edited")));


    }


    private List<Document> getContent() {
        try {
            List<Document> all = documentResource.findAll(0);
            Collections.sort(all, Comparator.comparing(Document::getLastModified));
            return all.subList(0, Math.min(10, all.size()));
        } catch (Exception e) {
            log.severe(e.getLocalizedMessage());
            return new ArrayList<>();
        }

    }


    private Component scoreOverview(EvaluationDataResource res) {
        try {
            EvaluationData data = res.findById(null);
            MGridLayout grid = new MGridLayout(2, 6);
            grid.with(label("Generated anonymizations"), label(data.getGenerated()));
            grid.with(label("Manually corrected"), label(data.getCorrected()));
            grid.with(label("Correct found"), label(data.getCorrectFound()));

            grid.with(label("Total Precision"), label(data.getPrecision()));
            grid.with(label("Total Recall"), label(data.getRecall()));
            grid.with(label("Total F1"), label(data.getFOne()));
            return new MPanel(grid.withMargin(true)).withCaption("Statistics");
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
