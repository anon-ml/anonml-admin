package ml.anon.admin.ml;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import ml.anon.admin.BaseView;
import org.apache.commons.io.output.WriterOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.vaadin.easyuploads.UploadField;
import org.vaadin.viritin.layouts.MHorizontalLayout;
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
  private Button retrain = new Button("Training starten", FontAwesome.MAGIC);
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

    HorizontalLayout layout = new HorizontalLayout();
    layout.addComponents(download, upload, retrain, retrainState);
    addComponent(layout);
  }

  private void updateRetrainState(String msg, String url) {

    String body = restTemplate.getForEntity(url + "/ml/retrain/status", String.class)
        .getBody();
    if (body == null || "".equals(body)) {
      toggleButtons(true);
      retrainState.setValue("");
    } else {
      toggleButtons(false);
      retrainState.setValue(msg + " " + body);
    }
  }

  private void initUpload() {
    upload = new UploadComponent(this::uploadReceived);

    upload.setFailedCallback(this::uploadFailed);
    upload.setWidth(300, Unit.PIXELS);
    upload.setHeight(200, Unit.PIXELS);
    upload.setCaption("Trainingsdaten hinzufügen");
    upload.setDescription("");

  }

  @SneakyThrows
  private void uploadReceived(String fileName, Path file) {
    Notification
        .show("Trainingsdaten erfolgreich hinzugefügt: " + fileName, Type.TRAY_NOTIFICATION);
    String str = Files.lines(file).collect(Collectors.joining("\n"));
    Boolean body = restTemplate
        .exchange(url + "/ml/post/training/data/", HttpMethod.POST, new HttpEntity<>(str),
            Boolean.class).getBody();
    if (body) {
      Notification
          .show("Trainingsdaten erfolgreich hinzugefügt: " + fileName, Type.TRAY_NOTIFICATION);
    } else {
      Notification
          .show("Fehler beim Hinzufügen: " + fileName, Type.ERROR_MESSAGE);
    }
  }

  private void uploadFailed(String fileName, Path file) {
    Notification.show("Upload failed: " + fileName, Type.ERROR_MESSAGE);
  }

  private void toggleButtons(boolean enabled) {
    retrain.setEnabled(enabled);
    upload.setEnabled(enabled);
    download.setEnabled(enabled);
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
