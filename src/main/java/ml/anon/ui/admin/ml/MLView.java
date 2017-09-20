package ml.anon.ui.admin.ml;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import ml.anon.ui.common.BaseView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MPanel;
import org.vaadin.viritin.layouts.MVerticalLayout;
import server.droporchoose.UploadComponent;

/**
 * Created by mirco on 16.08.17.
 */
@SpringComponent
@UIScope
@Log
public class MLView extends BaseView {


    public static final String ID = "MLVIEW";

    private final RestTemplate restTemplate = new RestTemplate();
    private final AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
    private Label retrainState = new Label();
    private Button retrain = new Button("Training starten", FontAwesome.GRADUATION_CAP);
    Button download = new Button("Trainingsdaten exportieren", FontAwesome.DOWNLOAD);
    UploadComponent upload;
    private String url;

    public MLView(@Value("${machinelearning.service.url}") String mlUrl) {
        super();
        url = mlUrl;
        initUpload();
        initDownloadButton(download, mlUrl + "/ml/get/training/data/");
        updateRetrainState("Training läuft seit", mlUrl);
        retrain.addClickListener(
                e -> {
                    toggleButtons(false);
                    updateRetrainState("Training gestartet ", mlUrl);
                    asyncRestTemplate.exchange(mlUrl + "/ml/retrain/", HttpMethod.GET, null, Boolean.class)
                            .addCallback(
                                    new ListenableFutureCallback<ResponseEntity<Boolean>>() {
                                        @Override
                                        public void onFailure(Throwable throwable) {
                                            updateRetrainState(throwable.getLocalizedMessage(), mlUrl);
                                        }

                                        @Override
                                        public void onSuccess(ResponseEntity<Boolean> booleanResponseEntity) {
                                            if (booleanResponseEntity.getBody()) {
                                                updateRetrainState("Training erfolgreich beendet: ", mlUrl);
                                            } else {
                                                updateRetrainState("Training fehlgeschlagen: ", mlUrl);
                                            }
                                        }
                                    });
                });

        VerticalLayout layout = new MVerticalLayout(
                new MPanel(new MHorizontalLayout(download,
                        new MHorizontalLayout(retrain, retrainState).withSpacing(true)).withSpacing(true)
                        .withMargin(true))
                        .withFullWidth(),
                new MPanel(
                        new MHorizontalLayout(upload).withSpacing(true)
                                .withMargin(true)).withCaption("Trainingsdaten hinzufügen")
                        .withFullWidth());
        addComponent(layout);
    }

    private void updateRetrainState(String msg, String url) {
        try {
            String body = restTemplate.getForEntity(url + "/ml/retrain/status", String.class)
                    .getBody();
            if (body == null || "".equals(body)) {
                toggleButtons(true);
                retrainState.setValue("");
            } else {
                toggleButtons(false);
                retrainState.setValue(msg + " " + body);
            }
        } catch (Exception e) {
            log.info("ML Service not available");
            log.severe(e.getLocalizedMessage());
            toggleButtons(false);
            retrainState.setValue(e.getLocalizedMessage());
        }

    }


    private void toggleButtons(boolean enabled) {
        retrain.setEnabled(enabled);
        upload.setEnabled(enabled);
        download.setEnabled(enabled);
    }

    private void initUpload() {
        upload = new UploadComponent(this::uploadReceived);
        upload.setFailedCallback(
                (fileName, file) -> Notification.show("Upload failed: " + fileName, Type.ERROR_MESSAGE));
        upload.setWidth(300, Unit.PIXELS);
        upload.setHeight(100, Unit.PIXELS);

        upload.setDescription("");


    }

    @SneakyThrows
    private void uploadReceived(String fileName, Path file) {
        String str = Files.lines(file).collect(Collectors.joining("\n"));
        ConfirmDialog.show(UI.getCurrent(), "", "Trainingsdaten anhängen oder\n existierende überschreiben?", "Anhängen", "Überschreiben", (e) -> {
            boolean append = !e.isConfirmed();
            e.close();
            Notification.show("Trainingsdaten werden importiert ...", Type.TRAY_NOTIFICATION);
            Boolean body = restTemplate
                    .exchange(url + "/ml/post/training/data/" + append + "/", HttpMethod.POST, new HttpEntity<>(str),
                            Boolean.class).getBody();
            if (body) {
                Notification
                        .show("Trainingsdaten erfolgreich hinzugefügt: " + fileName, Type.TRAY_NOTIFICATION);
            } else {
                Notification
                        .show("Fehler beim Hinzufügen: " + fileName, Type.ERROR_MESSAGE);
            }


        });

    }

    private void initDownloadButton(Button button, String url) {
        try {
            StreamSource streamSource = new StreamSource() {

                @Override
                public InputStream getStream() {
                    try {
                        return new URL(url).openStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
            FileDownloader fileDownloader = new FileDownloader(
                    new StreamResource(streamSource, "training_data_" + LocalDateTime.now().format(
                            DateTimeFormatter.ISO_DATE_TIME) + ".txt"));
            fileDownloader.extend(button);

        } catch (Exception e) {
            log.severe(e.getLocalizedMessage());
        }
    }

}
