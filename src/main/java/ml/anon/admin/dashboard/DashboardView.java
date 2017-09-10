package ml.anon.admin.dashboard;

import com.google.common.collect.Iterables;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import ml.anon.admin.BaseView;
import ml.anon.documentmanagement.model.Document;
import ml.anon.documentmanagement.resource.DocumentResource;
import ml.anon.recognition.machinelearning.resource.EvaluationDataResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by mirco on 16.08.17.
 */
@SpringComponent
@UIScope
public class DashboardView extends BaseView {

    public static final String ID = "";


    private DocumentResource documentResource;

    private EvaluationDataResource evaluationDataResource;

    private MGrid<Document> docGrid = new MGrid<>(Document.class).withFullSize().withProperties("fileName");

    @Autowired
    public DashboardView(DocumentResource documentResource, EvaluationDataResource evaluationDataResource) {
        super();
        this.documentResource = documentResource;

        docGrid.addColumn(d -> DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(d.getCreated())).setCaption("Created");
        docGrid.addColumn(d -> DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(d.getLastModified())).setCaption("Updated");
        docGrid.addColumn(d -> d.getAnonymizations() == null ? '-' : d.getAnonymizations().size()).setCaption("Anonymizations");

        docGrid.setRows(getContent());
        addComponent(new MVerticalLayout().add(new MButton(FontAwesome.TRASH, e -> {
            if (docGrid.getSelectedItems().size() == 1) {
                ConfirmDialog.show(getUI(), "Confirm delete", a -> {
                    documentResource.delete(Iterables.getFirst(docGrid, null).getId());
                });
            }


        })).add(docGrid, 0.99f));

    }


    private List<Document> getContent() {
        List<Document> all = documentResource.findAll();
        Collections.sort(all, Comparator.comparing(Document::getCreated));
        return all.subList(0, Math.min(10, all.size()));
    }


}
