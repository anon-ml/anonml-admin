package ml.anon.ui.admin.logs;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.TabSheet;

import javax.annotation.PostConstruct;

import lombok.extern.java.Log;
import ml.anon.ui.common.BaseView;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by mirco on 16.08.17.
 */

@SpringComponent
@UIScope
@Log
public class LogsView extends BaseView {

    public static final String ID = "LOGVIEW";
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
