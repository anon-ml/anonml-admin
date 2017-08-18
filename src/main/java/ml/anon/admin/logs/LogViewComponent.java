package ml.anon.admin.logs;

import com.cedarsoftware.util.io.JsonWriter;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONObject;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.extern.java.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.client.AsyncRestTemplate;

/**
 * Created by mirco on 18.08.17.
 */
@Log
public class LogViewComponent extends VerticalLayout {

  private final AsyncRestTemplate restTemplate = new AsyncRestTemplate();
  private String title;
  private TextArea metrics;
  private String url;

  public LogViewComponent(String title, String url) {
    this.title = title;
    this.url = url;
    Component buttons = buttons();
    metrics = logView();

    addComponents(buttons, metrics);
    setExpandRatio(metrics, 0.99f);
    metrics.setValue("Lade ...");
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

    }
  }


  private void metrics() {
    restTemplate.getForEntity(url + "/metrics", String.class).addCallback(s -> {
      metrics.setValue(JsonWriter.formatJson(s.getBody()));
      log.info("Loaded metrics");
    }, f -> {
      metrics.setValue("Fehler : " + f.getLocalizedMessage());
    });

  }

  private TextArea logView() {
    TextArea area = new TextArea(title + " Metriken");
    area.setSizeFull();

    return area;
  }


}
