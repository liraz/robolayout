package org.lirazs.robolayout.example.view;

import org.lirazs.robolayout.core.view.CollectionViewCell;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UILabel;

/**
 * Created on 8/9/2015.
 */
public class ExampleCollectionViewCell extends CollectionViewCell {

    private UILabel titleLabel;
    private UILabel subtitleLabel;
    private UILabel descriptionLabel;

    public ExampleCollectionViewCell() {
        this(null);
    }

    public ExampleCollectionViewCell(CGRect frame) {
        super("collectionViewCell.xml");

        titleLabel = (UILabel) findViewById("title");
        subtitleLabel = (UILabel) findViewById("subtitle");
        descriptionLabel = (UILabel) findViewById("description");
    }

    public void setItem(CollectionViewItem item) {
        titleLabel.setText(item.getTitle());
        subtitleLabel.setText(item.getSubTitle());
        descriptionLabel.setText(item.getItemDescription());
    }
}
