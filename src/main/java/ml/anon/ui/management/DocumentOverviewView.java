package ml.anon.ui.management;

import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.DataProviderListener;
import com.vaadin.data.provider.Query;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.Registration;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.ValoTheme;
import lombok.extern.java.Log;
import ml.anon.documentmanagement.model.Document;
import ml.anon.documentmanagement.resource.DocumentResource;
import ml.anon.ui.common.BaseView;
import org.glassfish.jersey.jaxb.internal.DocumentProvider;
import org.vaadin.viritin.LazyList;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.v7.fields.MTable;
import server.droporchoose.UploadComponent;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ws.rs.POST;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@SpringComponent
@UIScope
@Log
public class DocumentOverviewView extends BaseView {


    public static final String ID = "";

    @Resource
    private DocumentResource documentResource;

    private Grid<Document> grid;

    private UploadComponent bulkUpload;


    @PostConstruct
    private void init() {
        grid = new Grid<Document>(Document.class);
        grid.setColumns("fileName");
        grid.addComponentColumn(d -> new MLabel(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(d.getCreated()))).setCaption("Erstellt");
        grid.addComponentColumn(d -> new MLabel(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(d.getLastModified()))).setCaption("Letzte Änderung");

        grid.addComponentColumn((d) -> new MHorizontalLayout(new MButton(FontAwesome.PENCIL, (e) -> {
            getUI().getPage().open("http://localhost:9000/document/" + d.getId(), "", false);
        }).withStyleName(ValoTheme.BUTTON_BORDERLESS), initDownloadButton(d, "http://localhost:9001/document/" + d.getId() + "/export", FontAwesome.DOWNLOAD),
                initDownloadButton(d, "", FontAwesome.FILE_TEXT_O))).setCaption("");
        grid.setItems(documentResource.findAll(-1));

        grid.setSizeFull();

        addComponent(new MVerticalLayout().add(buildUpload(documentResource)).add(grid, 0.8f).withFullSize());
    }

    private UploadComponent buildUpload(DocumentResource documentResource) {

        bulkUpload = new UploadComponent((a, b) -> System.out.println(a + " | " + b));
        return bulkUpload;
    }

    private MButton initDownloadButton(Document doc, String url, FontAwesome icon) {
        MButton button = new MButton(icon).withStyleName(ValoTheme.BUTTON_BORDERLESS);
        try {
            StreamResource.StreamSource streamSource = (StreamResource.StreamSource) () -> {
                try {
                    return new URL(url).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            };
            FileDownloader fileDownloader = new FileDownloader(
                    new StreamResource(streamSource, doc.getFileName() + ".zip"));
            fileDownloader.extend(button);

        } catch (Exception e) {
            log.severe(e.getLocalizedMessage());
        }
        return button;
    }

}
