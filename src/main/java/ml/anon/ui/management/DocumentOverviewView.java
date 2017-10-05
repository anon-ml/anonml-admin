package ml.anon.ui.management;

import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.DataProviderListener;
import com.vaadin.data.provider.Query;
import com.vaadin.event.UIEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.Registration;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import ml.anon.documentmanagement.model.Document;
import ml.anon.documentmanagement.resource.DocumentResource;
import ml.anon.ui.common.BaseView;
import org.apache.poi.ss.formula.functions.T;
import org.glassfish.jersey.jaxb.internal.DocumentProvider;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.addons.ToastPosition;
import org.vaadin.addons.Toastr;
import org.vaadin.dialogs.ConfirmDialog;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@SpringComponent
@UIScope
@Log
public class DocumentOverviewView extends BaseView implements UIEvents.PollListener {


    public static final String ID = "";

    @Value("${documentmanagement.service.url}")
    private String baseUrl;

    @Value("${frontend.service.url}")
    private String frontEndUrl;

    @Resource
    private DocumentResource documentResource;

    private Grid<Document> grid;

    private UploadComponent bulkUpload;


    @PostConstruct
    private void init() {

        grid = new Grid<Document>(Document.class);
        grid.setColumns("fileName", "version");
        grid.addComponentColumn(d -> new MLabel(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(d.getCreated()))).setCaption("Erstellt");
        grid.addComponentColumn(d -> new MLabel(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(d.getLastModified()))).setCaption("Letzte Änderung");

        grid.addComponentColumn((d) -> new MHorizontalLayout(new MButton(FontAwesome.PENCIL, (e) -> {
            getUI().getPage().open(frontEndUrl + "/document/" + d.getId(), "", false);
        }).withStyleName(ValoTheme.BUTTON_BORDERLESS), initDownloadButton(d.fileNameAs("zip"), baseUrl + "/document/" + d.getId() + "/export", FontAwesome.DOWNLOAD),
                initDownloadButton(d.getFileName(), baseUrl + "/document/" + d.getId() + "/original", FontAwesome.FILE_TEXT_O), new MButton(FontAwesome.TRASH, (e) -> {
            ConfirmDialog.show(UI.getCurrent(), (ee) -> {
                documentResource.delete(d.getId());
                grid.setItems(documentResource.findAll(-1));
            });


        }).withStyleName(ValoTheme.BUTTON_BORDERLESS))).setCaption("");
        try {
            grid.setItems(documentResource.findAll(-1));
        } catch (Exception e) {
            log.severe(e.getLocalizedMessage());
        }


        grid.setSizeFull();

        addComponent(new MVerticalLayout().add(new MLabel("Dokumentenmanagement")
                .withStyleName(ValoTheme.LABEL_H2), 0.05f).add(buildUpload(documentResource), 0.1f).add(grid, 0.85f).withFullSize());

    }


    private UploadComponent buildUpload(DocumentResource documentResource) {

        bulkUpload = new UploadComponent((String a, Path b) -> {
            try {
                Document doc = documentResource.importDocument(a, Files.readAllBytes(b));
                grid.setItems(documentResource.findAll(-1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        bulkUpload.setFailedCallback((a, b) -> Notification.show(a + "\n" + b, Notification.Type.ERROR_MESSAGE));
        bulkUpload.setStartedCallback((a) ->
                Notification.show("", "Import für " + a + " gestartet", Notification.Type.TRAY_NOTIFICATION));
        bulkUpload.setSizeFull();
        return bulkUpload;
    }

    private MButton initDownloadButton(String fileName, String url, FontAwesome icon) {
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
                    new StreamResource(streamSource, fileName));
            fileDownloader.extend(button);

        } catch (Exception e) {
            log.severe(e.getLocalizedMessage());
        }
        return button;
    }

    @Override
    public void poll(UIEvents.PollEvent event) {
        if (grid != null && documentResource != null) {
            grid.setItems(documentResource.findAll(-1));
        }
    }
}
