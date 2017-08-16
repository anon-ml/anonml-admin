package ml.anon.admin.rules;

import com.vaadin.navigator.View;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by mirco on 16.08.17.
 */
public class RulesView extends VerticalLayout implements View {

  public static final String ID = "RULESVIEW";

  public RulesView() {
    setSizeFull();
    Label label = new Label("Rules");
    addComponent(label);
  }


}
