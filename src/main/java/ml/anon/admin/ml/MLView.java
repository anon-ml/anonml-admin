package ml.anon.admin.ml;

import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import java.time.LocalDateTime;
import javax.annotation.PostConstruct;
import lombok.extern.java.Log;
import ml.anon.admin.BaseView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

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
  Button imp = new Button("Trainingsdaten hinzufügen", FontAwesome.UPLOAD);

  public MLView(@Value("${machinelearning.service.url}") String mlUrl) {
    super();
    updateRetrainState("Training läuft seit", mlUrl);

    download.addClickListener(
        e -> log.info(restTemplate
            .exchange(mlUrl + "/ml/get/training/data/", HttpMethod.GET, null, String.class)
            .getBody()));

    retrain.addClickListener(
        e -> {
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

    layout.addComponents(download, imp, retrain, retrainState);
    addComponent(layout);
  }

  private void updateRetrainState(String msg, String url) {

    String body = restTemplate.getForEntity(url + "/ml/retrain/status", String.class)
        .getBody();
    if (body == null || "".equals(body)) {
      retrain.setEnabled(true);
      imp.setEnabled(true);
      download.setEnabled(true);
      retrainState.setValue("");
    } else {
      retrain.setEnabled(false);
      imp.setEnabled(false);
      download.setEnabled(false);
      retrainState.setValue(msg + " " + body);
    }
  }
}
