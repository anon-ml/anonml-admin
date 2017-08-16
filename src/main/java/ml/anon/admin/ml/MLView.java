package ml.anon.admin.ml;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import ml.anon.admin.BaseView;

/**
 * Created by mirco on 16.08.17.
 */
@SpringComponent
@UIScope
public class MLView extends BaseView {

  public static final String ID = "MLVIEW";

  public MLView() {
    super();
    addComponent(new Label("ML"));
  }
}
