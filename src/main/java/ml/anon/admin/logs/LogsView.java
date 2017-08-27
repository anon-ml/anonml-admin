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
import javax.annotation.PostConstruct;
import lombok.extern.java.Log;
import ml.anon.admin.BaseView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by mirco on 16.08.17.
 */

@SpringComponent
@UIScope
@Log
public class LogsView extends BaseView {

  public static final String ID = "LOGSVIEW";
  @Value("${web.actuator.url}")
  private String webUrl;

  @Value("${documentmanagement.actuator.url}")
  private String documentManagementUrl;

  @Value("${rulebased.actuator.url}")
  private String rulebasedUrl;

  @Value("${machinelearning.actuator.url}")
  private String machinelearningUrl;

  @PostConstruct
  public void init() {
    TabSheet tabs = new TabSheet();

    tabs.addTab(new LogViewComponent("Frontend", webUrl), "Frontend",
        VaadinIcons.DESKTOP);

    tabs.addTab(new LogViewComponent("Document", documentManagementUrl), "Document Management",
        VaadinIcons.FILE);
    tabs.addTab(new LogViewComponent("Rules", rulebasedUrl), "Rulebased Recogition",
        VaadinIcons.BAN);

    tabs.addTab(new LogViewComponent("Machine Learning", machinelearningUrl),
        "Machine Learning",
        VaadinIcons.AUTOMATION);

    tabs.setSizeFull();
    addComponent(tabs);
  }

}
