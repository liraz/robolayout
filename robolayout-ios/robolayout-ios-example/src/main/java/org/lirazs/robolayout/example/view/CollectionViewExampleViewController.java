package org.lirazs.robolayout.example.view;

import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.*;

import java.util.List;

/**
 * Created on 8/7/2015.
 */
public class CollectionViewExampleViewController extends UICollectionViewController implements UICollectionViewDelegateFlowLayout {

    private List<CollectionViewItem> items;

    public CollectionViewExampleViewController(UICollectionViewLayout layout) {
        super(layout);

        items = CollectionViewItem.createRandomItems();
    }

    @Override
    public CGSize getItemSize(UICollectionView uiCollectionView, UICollectionViewLayout uiCollectionViewLayout, NSIndexPath nsIndexPath) {
        ExampleCollectionViewCell cell = new ExampleCollectionViewCell();
        cell.setItem(items.get(nsIndexPath.getItem()));

        CGSize size = uiCollectionView.getBounds().getSize();
        UICollectionViewFlowLayout flowLayout = (UICollectionViewFlowLayout) uiCollectionViewLayout;

        if(flowLayout.getScrollDirection() == UICollectionViewScrollDirection.Vertical) {
            size.setHeight(cell.getRequiredHeightForWidth(size.getWidth()));
        } else {
            size.setWidth(cell.getRequiredWidthForHeight(size.getHeight()));
        }

        return size;
    }

    @Override
    public UIEdgeInsets getSectionInset(UICollectionView uiCollectionView, UICollectionViewLayout uiCollectionViewLayout, long l) {
        return null;
    }

    @Override
    public double getSectionMinimumLineSpacing(UICollectionView uiCollectionView, UICollectionViewLayout uiCollectionViewLayout, long l) {
        return 0;
    }

    @Override
    public double getSectionMinimumInteritemSpacing(UICollectionView uiCollectionView, UICollectionViewLayout uiCollectionViewLayout, long l) {
        return 0;
    }

    @Override
    public CGSize getSectionHeaderReferenceSize(UICollectionView uiCollectionView, UICollectionViewLayout uiCollectionViewLayout, long l) {
        return null;
    }

    @Override
    public CGSize getSectionFooterReferenceSize(UICollectionView uiCollectionView, UICollectionViewLayout uiCollectionViewLayout, long l) {
        return null;
    }

    @Override
    public long getNumberOfSections(UICollectionView uiCollectionView) {
        return 1;
    }

    @Override
    public long getNumberOfItemsInSection(UICollectionView uiCollectionView, long l) {
        return items.size();
    }

    @Override
    public UICollectionViewCell getCellForItem(UICollectionView uiCollectionView, NSIndexPath nsIndexPath) {
        CollectionViewItem item = items.get(nsIndexPath.getItem());
        ExampleCollectionViewCell cell = (ExampleCollectionViewCell) uiCollectionView.dequeueReusableCell("cell", nsIndexPath);

        cell.setItem(item);

        return cell;
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        UICollectionView collectionView = getCollectionView();
        collectionView.setBackgroundColor(UIColor.lightGray());
        collectionView.registerReusableCellClass(ExampleCollectionViewCell.class, "cell");
    }

    @Override
    public void willAnimateRotation(UIInterfaceOrientation uiInterfaceOrientation, double v) {
        super.willAnimateRotation(uiInterfaceOrientation, v);

        getCollectionView().getCollectionViewLayout().invalidateLayout();
    }
}
