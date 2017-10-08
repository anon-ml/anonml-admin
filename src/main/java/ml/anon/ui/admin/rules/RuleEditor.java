package ml.anon.ui.admin.rules;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import lombok.extern.slf4j.Slf4j;
import ml.anon.anonymization.model.Label;
import ml.anon.recognition.rulebased.api.model.Rule;
import ml.anon.recognition.rulebased.api.resource.RuleResource;

import org.springframework.context.annotation.Scope;
import org.vaadin.ui.NumberField;

/**
 * Created by mirco on 14.06.17.
 */
@org.springframework.stereotype.Component
@Scope("prototype")
@Slf4j
public class RuleEditor extends VerticalLayout {

  private final Grid<Rule> grid;

  private Rule rule = Rule.builder().build();

  private TextField name = new TextField("Name");

  private TextArea regExp = new TextArea("RegEx");

  private NumberField order = new NumberField("Gewicht");

  private CheckBox active = new CheckBox(("Aktiv"));

  private ComboBox<Label> label = new ComboBox<>("Label");

  private Button save = new Button("Änderungen Speichern", FontAwesome.SAVE);

  private Button delete = new Button("Löschen", FontAwesome.REMOVE);

  private Button create = new Button("Neue Regel anlegen", FontAwesome.PLUS_SQUARE);

  private Binder<Rule> binder = new Binder<>(Rule.class);

  private CssLayout actions = new CssLayout(save, delete);

  private RulesView rulesView;
  private RuleResource resource;

  public RuleEditor(RuleResource resource, Grid<Rule> grid, RulesView rulesView) {
    this.grid = grid;
    this.resource = resource;
    this.rulesView = rulesView;
    HorizontalLayout top = new HorizontalLayout();
    HorizontalLayout buttons = new HorizontalLayout();
    buttons.addComponents(save, create, delete);
    save.setEnabled(false);
    top.addComponents(name, order, label, active);
    addComponents(top, regExp, buttons);
    regExp.setSizeFull();
    active.setSizeUndefined();
    name.setSizeFull();
    order.setSizeUndefined();
    label.setItems(Label.values());
    top.setSizeFull();
    regExp.setRequiredIndicatorVisible(true);
    label.setRequiredIndicatorVisible(true);
    top.setComponentAlignment(active, Alignment.MIDDLE_CENTER);
    binder.forField(active).bind("active");
    binder.forField(regExp).bind("regExp");
    binder.forField(name).bind("name");
    binder.withValidator(r -> r.getLabel() != null && r.getRegExp() != null,
        "");
    label.addValueChangeListener(l -> rule.setLabel(l.getValue()));
    binder.forField(order).withConverter(NumberField.getConverter("...")).bind("weight");
    binder.setBean(rule);
    setSpacing(true);
    actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
    save.setStyleName(ValoTheme.BUTTON_PRIMARY);
    create.setStyleName(ValoTheme.BUTTON_FRIENDLY);
    delete.setStyleName(ValoTheme.BUTTON_DANGER, true);

    save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

    save.addClickListener(e -> {
      if (binder.validate().isOk()) {
        resource.update(rule.getId(), rule);
        onChange(resource, grid);
      } else {
        Notification.show("Label und RegEx müssen gesetzt sein.", Type.ERROR_MESSAGE);
      }
    });
    delete.addClickListener(e -> {
      resource.delete(rule.getId());
      onChange(resource, grid);
    });
    create.addClickListener(e -> {

      Rule created = resource.create(this.rule.copy());
      onChange(resource, grid);
      grid.select(created);
      rule = created;

    });

    this.components = components;
    this.resource = resource;
  }

  private void onChange(RuleResource resource, Grid<Rule> grid) {
    rulesView.refresh();
    rule = Rule.builder().build();
    binder.setBean(rule);

  }

  public final void changeBoundRule(Rule r) {

    rule = r;
    save.setEnabled(r.getId() != null);
    binder.setBean(rule);
    label.setSelectedItem(r.getLabel());
    save.focus();
    delete.setEnabled(!r.isCore());
    name.selectAll();
  }


}
