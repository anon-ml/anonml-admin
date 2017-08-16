package ml.anon.admin;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import ml.anon.admin.dashboard.DashboardView;
import ml.anon.admin.rules.RulesView;

/**
 * Created by mirco on 16.08.17.
 */
@SpringUI(path = "/")
@Theme("valo")
@Slf4j
public class BaseUI extends UI {

  private Navigator navigator;

  @Override
  protected void init(VaadinRequest vaadinRequest) {
    getPage().setTitle("AnonML Admin");

    VerticalLayout base = new VerticalLayout();
    base.setSizeFull();
    base.setMargin(false);
    base.setSpacing(false);
    Panel container = new Panel();
    container.setSizeFull();

    base.addComponents(buildMenu(), container);
    base.setExpandRatio(container, 0.99f);

    navigator = new Navigator(this, container);
    navigator.addView(DashboardView.ID, new DashboardView());
    navigator.addView(RulesView.ID, new RulesView());
    setContent(base);

  }

  private HorizontalLayout buildMenu() {

    HorizontalLayout menu = new HorizontalLayout();
    Button dashboard = new Button("Dashboard");
    dashboard.addClickListener(e -> navigator.navigateTo(DashboardView.ID));

    Button rules = new Button("Rules");
    rules.addClickListener(e -> navigator.navigateTo(RulesView.ID));
    menu.addComponents(dashboard, rules);
    menu.setMargin(true);
    menu.setSpacing(true);
    return menu;
  }
}
