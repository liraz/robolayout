package org.lirazs.robolayout.example.view;

import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSObject;

import java.util.List;

/**
 * Created on 8/7/2015.
 */
public class CollectionViewItem extends NSObject {

    private String title;
    private String subTitle;
    private String itemDescription;

    public static CollectionViewItem createRandomItem() {
        CollectionViewItem item = new CollectionViewItem();
        item.setTitle("title " + Math.random() * 100);
        item.setTitle("subtitle " + Math.random() * 100);
        item.setTitle("description " + Math.random() * 100);

        return item;
    }

    public static List<CollectionViewItem> createRandomItems() {
        List<CollectionViewItem> items = new NSMutableArray<>();
        for (int i = 0; i < 1000; i++) {
            items.add(createRandomItem());
        }
        return items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
}
