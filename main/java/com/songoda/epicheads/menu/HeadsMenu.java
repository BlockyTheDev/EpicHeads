package com.songoda.epicheads.menu;

import com.songoda.epicheads.cache.CacheHead;
import com.songoda.epicheads.menu.ui.Bounds;
import com.songoda.epicheads.menu.ui.MenuResponse;
import com.songoda.epicheads.menu.ui.element.Element;
import com.songoda.epicheads.menu.ui.element.PagedBox;
import com.songoda.epicheads.menu.ui.item.Button;
import com.songoda.epicheads.menu.ui.item.Item;
import com.songoda.epicheads.util.Checks;
import com.songoda.epicheads.util.SafeCall;
import com.songoda.epicheads.util.Stringify;
import com.songoda.epicheads.volatilecode.Items;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class HeadsMenu extends Element {

    public static final Item defaultHead = Items.createSkull().name("&7%name%").lore("&eCost: &6%cost%");

    public static final Template defaultTemplate = new Template(PagedBox.defaultTemplate, defaultHead);

    private Template template;

    private final Function<CacheHead, MenuResponse> onSelect;

    private final List<CacheHead> heads = new ArrayList<>();
    private final PagedBox pagedBox;

    public HeadsMenu(Bounds bounds, Function<CacheHead, MenuResponse> onSelect) {
        super(bounds);

        Checks.ensureNonNull(onSelect, "onSelect");
        Checks.ensureTrue(bounds.height >= 3, "bounds must have a height of at least 3");

        this.onSelect = SafeCall.nonNullFunction(onSelect, "onHeadSelect");
        this.pagedBox = new PagedBox(bounds);

        setTemplate(defaultTemplate, PagedBox.defaultLeftControl, PagedBox.defaultRightControl);
    }

    public void setItems(Collection<CacheHead> heads) {
        this.heads.clear();
        this.heads.addAll(heads);

        updateItems();
    }

    @Override
    public Button[] getItems() {
        return pagedBox.getItems();
    }

    private void updateItems() {
        Button[] items = new Button[heads.size()];

        for(int index = 0; index < heads.size(); ++index) {
            CacheHead head = heads.get(index);

            items[index] = template.constructHead(this, head);
        }

        pagedBox.setItems(items);
    }

    public void setTemplate(Template template, Button leftControl, Button rightControl) {
        Checks.ensureNonNull(template, "template");

        this.template = template;
        this.template.init(this, leftControl, rightControl);
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("template", template)
                .entry("onSelect", onSelect)
                .entry("pagedBox", pagedBox)
                .entry("heads", heads).toString();
    }

    public static final class Template {

        private final PagedBox.Template pagedBoxTemplate;
        private final Item headItem;

        public Template(PagedBox.Template pagedBoxTemplate, Item headItem) {
            Checks.ensureNonNull(pagedBoxTemplate, "pagedBoxTemplate");
            Checks.ensureNonNull(headItem, "headItem");

            this.pagedBoxTemplate = pagedBoxTemplate;
            this.headItem = headItem;
        }

        private void init(HeadsMenu menu, Button leftControl, Button rightControl) {
            menu.pagedBox.setTemplate(pagedBoxTemplate, leftControl, rightControl);
        }

        public Button constructHead(HeadsMenu menu, CacheHead head) {
            ItemStack item = headItem.build(head.getPlaceholders(null));

            item = head.addTexture(item);

            return new Button(item, () -> menu.onSelect.apply(head));
        }

        @Override
        public String toString() {
            return Stringify.builder()
                    .entry("pagedBoxTemplate", pagedBoxTemplate)
                    .entry("headItem", headItem).toString();
        }

    }

}
