/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.navigation.widget;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Node;
import org.uberfire.mvp.Command;

public abstract class BaseNavWidgetView<T> extends Composite implements NavWidgetView<T> {

    protected Node navWidget = null;
    protected Element selectedItem = null;
    protected Map<String,Element> itemMap = new HashMap<>();

    @Override
    public void clearItems() {
        DOMUtil.removeAllChildren(navWidget);
    }

    @Override
    public void addGroupItem(String id, String name, String description, IsWidget widget) {
        Element el = widget.asWidget().getElement();
        navWidget.appendChild((Node) el);
    }

    @Override
    public void addItem(String id, String name, String description, Command onItemSelected) {
        AnchorElement anchor = Document.get().createAnchorElement();
        anchor.setInnerText(name);
        if (description != null && !description.equals(name)) {
            anchor.setTitle(description);
        }

        LIElement li = Document.get().createLIElement();
        li.getStyle().setCursor(Style.Cursor.POINTER);
        li.appendChild(anchor);
        navWidget.appendChild((Node) li);
        itemMap.put(id, li);

        Event.sinkEvents(anchor, Event.ONCLICK);
        Event.setEventListener(anchor, event -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                onItemSelected.execute();
            }
        });
    }

    @Override
    public void setSelectedItem(String id) {
        Element el = itemMap.get(id);
        if (el != null) {
            clearSelectedItem();
            selectedItem = el;
            el.setClassName("active");
        }
    }

    @Override
    public void clearSelectedItem() {
        if (selectedItem != null) {
            selectedItem.setClassName("");
        }
    }

    @Override
    public void errorNavItemNotFound(String navItemId) {
        addItem(navItemId, NavigationConstants.INSTANCE.navItemNotFound(navItemId), null, () -> {});
    }

    @Override
    public void errorNavItemsEmpty() {
        addItem("error", NavigationConstants.INSTANCE.navItemsEmpty(), null, () -> {});
    }
}
