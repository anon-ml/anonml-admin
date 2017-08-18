package ml.anon.admin.logs;

import com.fasterxml.jackson.databind.ser.Serializers.Base;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import lombok.extern.java.Log;
import ml.anon.admin.BaseView;
import org.springframework.web.client.RestTemplate;

/**
 * Created by mirco on 16.08.17.
 */
@SpringComponent
@UIScope
@Log
public class LogsView extends BaseView {

  private RestTemplate restTemplate = new RestTemplate();
  public static final String ID = "LOGSVIEW";

  public LogsView() {
    super();
    TabSheet tabs = new TabSheet();
    tabs.addTab(new LogViewComponent("Document", "http://localhost:9901"), "Document Management",
        VaadinIcons.FILE);
    tabs.addTab(new LogViewComponent("Rules", "http://localhost:9902"), "Rulebased Recogition",
        VaadinIcons.BAN);

    tabs.addTab(new LogViewComponent("Machine Learning", "http://localhost:9903"),
        "Machine Learning",
        VaadinIcons.AUTOMATION);

    tabs.addTab(new LogViewComponent("Frontend", "http://localhost:9904"), "Frontend",
        VaadinIcons.DESKTOP);

    tabs.setSizeFull();
    addComponent(tabs);
  }
}
