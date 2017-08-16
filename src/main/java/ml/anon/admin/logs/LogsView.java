package ml.anon.admin.logs;

import com.fasterxml.jackson.databind.ser.Serializers.Base;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import ml.anon.admin.BaseView;

/**
 * Created by mirco on 16.08.17.
 */
@SpringComponent
@UIScope
public class LogsView extends BaseView {

  public static final String ID = "LOGSVIEW";

  public LogsView() {
    super();
    Label label = new Label("Logfiles");
    addComponent(label);
  }
}
