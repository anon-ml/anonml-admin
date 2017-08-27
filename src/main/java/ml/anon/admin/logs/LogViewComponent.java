package ml.anon.admin.logs;

import com.cedarsoftware.util.io.JsonWriter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by mirco on 18.08.17.
 */
@Log
public class LogViewComponent extends VerticalLayout {

  private final RestTemplate restTemplate = new RestTemplate();
  private String title;
  private TextArea metrics;
  private String url;
  private Label health = new Label();

  public LogViewComponent(String title, String url) {
    this.title = title;
    this.url = url;
    Component buttons = buttons();
    metrics = logView();
    addComponents(buttons, metrics);
    setExpandRatio(metrics, 0.99f);
    metrics.setValue("");
    metrics();
    setSizeFull();
  }


  private Component buttons() {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setSizeUndefined();
    Button save = new Button("Logfile", FontAwesome.SAVE);
    layout.addComponent(save);

    logfile(save);

    Button refresh = new Button("Metriken", FontAwesome.REFRESH);
    refresh.addClickListener(e -> {
      metrics();
    });
    layout.addComponent(refresh);
    layout.addComponent(health);

    return layout;
  }


  private void logfile(Button button) {

    try {
      StreamSource streamSource = new StreamSource() {

        @Override
        public InputStream getStream() {
          try {
            return new URL(url + "/logfile").openStream();
          } catch (IOException e) {
            e.printStackTrace();
            return null;
          }
        }
      };
      FileDownloader fileDownloader = new FileDownloader(
          new StreamResource(streamSource, title + "_log.txt".toLowerCase()));
      fileDownloader.extend(button);
    } catch (Exception e) {
      log.severe(e.getLocalizedMessage());
    }
  }


  private void metrics() {
    try {
      ResponseEntity<String> m = restTemplate.getForEntity(url + "/metrics", String.class);
      metrics.setValue(JsonWriter.formatJson(m.getBody()));

      Health h = restTemplate.getForEntity(url + "/health", Health.class).getBody();

      health.setValue(
          (h.isUp() ? VaadinIcons.ARROW_UP.getHtml() : VaadinIcons.QUESTION.getHtml()) + " " + h
              .getStatus());
      health.setContentMode(ContentMode.HTML);


    } catch (Exception e) {
      log.severe(title + " not available: " + e.getLocalizedMessage());
      health.setValue((VaadinIcons.BOMB.getHtml() + " DOWN"));
      health.setContentMode(ContentMode.HTML);
    }


  }

  private TextArea logView() {
    TextArea area = new TextArea(title + " Metriken");
    area.setSizeFull();

    return area;
  }


}
