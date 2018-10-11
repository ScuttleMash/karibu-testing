package com.github.karibu.testing;

import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import static com.github.karibu.testing.LocatorJ.*;
import static com.github.vok.karibudsl.NavigatorKt.*;

/**
 * Not really a test, doesn't run any test methods. It just fails compilation if the LocatorJ API
 * is hard to use from Java. This is an API test.
 * @author mavi
 */
public class LocatorJTest {
    public static class MyUI extends UI {
        @Override
        protected void init(VaadinRequest request) {
        }
    }
    public LocatorJTest() {
        MockVaadin.setup(MyUI::new);

        _get(Label.class);
        _get(Label.class, spec -> spec.withCaption("Name:").withId("foo"));
        _get(new Button(), TextField.class);
        _get(new VerticalLayout(), TextField.class, spec -> spec.withCaption("Name:").withId("foo"));

        _find(Label.class);
        _find(Label.class, spec -> spec.withCaption("Name:").withId("foo"));
        _find(new Button(), TextField.class);
        _find(new VerticalLayout(), TextField.class, spec -> spec.withCaption("Name:").withId("foo"));

        _assertNone(Label.class);
        _assertNone(Label.class, spec -> spec.withCaption("Name:").withId("foo"));
        _assertNone(new Button(), TextField.class);
        _assertNone(new VerticalLayout(), TextField.class, spec -> spec.withCaption("Name:").withId("foo"));

        _click(new Button());
        _setValue(new TextField(), "John");

        navigateToView(MyJavaView.class);
        autoDiscoverViews("com.myproject");

        final Grid<Person> grid = _get(Grid.class);

        final AddNewPersonForm form = new AddNewPersonForm();
        _setValue(_get(form, TextField.class, spec -> spec.withCaption("Name:")), "John Doe");
        _click(_get(form, Button.class, spec -> spec.withCaption("Create")));

        final TabSheet ts = _get(TabSheet.class);
        final Component first = ts.getTab(0).getComponent();
        _setValue(_get(first, TextField.class, spec -> spec.withCaption("Age")), "45");

        GridKt._get(new Grid<>(), 0);
        GridKt._size(new Grid<>());
        GridKt._clickRenderer(new Grid<>(), 0, "col");
        GridKt._getFormattedRow(new Grid<>(), 5);
        GridKt.expectRows(new Grid<>(), 0);
        GridKt.expectRow(new Grid<>(), 0, "John Doe", "25");
    }

    public static class MyJavaView implements View {}

    public static class Person {}

    public static class AddNewPersonForm extends VerticalLayout {}
}
