package org.lirazs.robolayout.example.view;

import org.lirazs.robolayout.core.resource.ResourceManager;
import org.lirazs.robolayout.core.util.UIViewLayoutUtil;
import org.lirazs.robolayout.core.view.LayoutParamsSize;
import org.lirazs.robolayout.core.view.LayoutViewController;
import org.lirazs.robolayout.core.view.TableViewCell;
import org.lirazs.robolayout.core.widget.TextView;
import org.lirazs.robolayout.core.widget.layout.linear.LinearLayoutLayoutParams;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.foundation.*;
import org.robovm.apple.uikit.*;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.Method;

import java.util.List;

/**
 * Created on 8/8/2015.
 */
public class MainViewController extends UIViewController implements UITableViewDataSource, UITableViewDelegate {

    private UITableView tableView;
    private NSURL tableCellLayoutURL;

    private List<String> titles;
    private List<String> descriptions;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        tableCellLayoutURL = NSBundle.getMainBundle().findResourceURL("mainCell", "xml");

        tableView = new UITableView(getView().getBounds());
        tableView.setAutoresizingMask(UIViewAutoresizing.FlexibleWidth.set(UIViewAutoresizing.FlexibleHeight));
        tableView.setDataSource(this);
        tableView.setDelegate(this);
        getView().addSubview(tableView);

        titles = ResourceManager.getCurrent().getStringArray("@array/values.main_menu_titles");
        descriptions = ResourceManager.getCurrent().getStringArray("@array/values.main_menu_descriptions");
    }

    @Method(selector = "didPressToggleButton")
    public void didPressToggleButton(UIButton button) {
        TextView textView = (TextView) UIViewLayoutUtil.findViewById(button.getSuperview(), "toggleText");

        LinearLayoutLayoutParams lp = (LinearLayoutLayoutParams) UIViewLayoutUtil.getLayoutParams(textView);
        if(lp.getHeight() == LayoutParamsSize.WrapContent.getValue()) {
            lp.setHeight(44);
        } else {
            lp.setHeight(LayoutParamsSize.WrapContent.getValue());
        }

        UIViewLayoutUtil.setLayoutParams(textView, lp);

        UIView.animate(0.5, new Runnable() {
            @Override
            public void run() {
                getNavigationController().getTopViewController().getView().layoutIfNeeded();
            }
        });
    }

    //#pragma mark - UITableViewDataSource

    @Override
    public long getNumberOfRowsInSection(UITableView uiTableView, long l) {
        return Math.min(titles.size(), descriptions.size());
    }

    @Override
    public UITableViewCell getCellForRow(UITableView uiTableView, NSIndexPath nsIndexPath) {
        String cellIdentifier = "Cell";
        TableViewCell cell = (TableViewCell) tableView.dequeueReusableCell(cellIdentifier);
        if(cell == null) {
            cell = new TableViewCell(tableCellLayoutURL, cellIdentifier);
            cell.setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);
        }

        setupCell(cell, nsIndexPath.getRow());
        return cell;
    }

    private void setupCell(TableViewCell cell, int row) {
        UILabel titleLabel = (UILabel) cell.getLayoutBridge().findViewById("title");
        UILabel descriptionLabel = (UILabel) cell.getLayoutBridge().findViewById("description");

        titleLabel.setText(titles.get(row));
        descriptionLabel.setText(descriptions.get(row));
    }

    @Override
    public long getNumberOfSections(UITableView uiTableView) {
        return 1;
    }

    @Override
    public String getTitleForHeader(UITableView uiTableView, long l) {
        return null;
    }

    @Override
    public String getTitleForFooter(UITableView uiTableView, long l) {
        return null;
    }

    @Override
    public boolean canEditRow(UITableView uiTableView, NSIndexPath nsIndexPath) {
        return false;
    }

    @Override
    public boolean canMoveRow(UITableView uiTableView, NSIndexPath nsIndexPath) {
        return false;
    }

    @Override
    public List<String> getSectionIndexTitles(UITableView uiTableView) {
        return null;
    }

    @Override
    public long getSectionForSectionIndexTitle(UITableView uiTableView, String s, long l) {
        return 0;
    }

    @Override
    public void commitEditingStyleForRow(UITableView uiTableView, UITableViewCellEditingStyle uiTableViewCellEditingStyle, NSIndexPath nsIndexPath) {

    }

    @Override
    public void moveRow(UITableView uiTableView, NSIndexPath nsIndexPath, NSIndexPath nsIndexPath1) {

    }

    //#pragma mark - UITableViewDataSource END


    //#pragma mark - UITableViewDelegate

    @Override
    public void performActionForRow(UITableView uiTableView, Selector selector, NSIndexPath nsIndexPath, NSObject nsObject) {

    }

    @Override
    public void willDisplayCell(UITableView uiTableView, UITableViewCell uiTableViewCell, NSIndexPath nsIndexPath) {

    }

    @Override
    public void willDisplayHeaderView(UITableView uiTableView, UIView uiView, long l) {

    }

    @Override
    public void willDisplayFooterView(UITableView uiTableView, UIView uiView, long l) {

    }

    @Override
    public void didEndDisplayingCell(UITableView uiTableView, UITableViewCell uiTableViewCell, NSIndexPath nsIndexPath) {

    }

    @Override
    public void didEndDisplayingHeaderView(UITableView uiTableView, UIView uiView, long l) {

    }

    @Override
    public void didEndDisplayingFooterView(UITableView uiTableView, UIView uiView, long l) {

    }

    @Override
    public double getHeightForRow(UITableView uiTableView, NSIndexPath nsIndexPath) {
        String cellIdentifier = "Cell";

        TableViewCell cell = (TableViewCell) tableView.dequeueReusableCell(cellIdentifier);
        if(cell == null) {
            cell = new TableViewCell(tableCellLayoutURL, cellIdentifier);
            cell.setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);
        }

        setupCell(cell, nsIndexPath.getRow());

        return cell.getRequiredHeightInView(tableView);
    }

    @Override
    public double getHeightForHeader(UITableView uiTableView, long l) {
        return 0;
    }

    @Override
    public double getHeightForFooter(UITableView uiTableView, long l) {
        return 0;
    }

    @Override
    public double getEstimatedHeightForRow(UITableView uiTableView, NSIndexPath nsIndexPath) {
        return 114;
    }

    @Override
    public double getEstimatedHeightForHeader(UITableView uiTableView, long l) {
        return 0;
    }

    @Override
    public double getEstimatedHeightForFooter(UITableView uiTableView, long l) {
        return 0;
    }

    @Override
    public UIView getViewForHeader(UITableView uiTableView, long l) {
        return null;
    }

    @Override
    public UIView getViewForFooter(UITableView uiTableView, long l) {
        return null;
    }

    @Override
    public void accessoryButtonTapped(UITableView uiTableView, NSIndexPath nsIndexPath) {

    }

    @Override
    public boolean shouldHighlightRow(UITableView uiTableView, NSIndexPath nsIndexPath) {
        return false;
    }

    @Override
    public void didHighlightRow(UITableView uiTableView, NSIndexPath nsIndexPath) {

    }

    @Override
    public void didUnhighlightRow(UITableView uiTableView, NSIndexPath nsIndexPath) {

    }

    @Override
    public NSIndexPath willSelectRow(UITableView uiTableView, NSIndexPath nsIndexPath) {
        return null;
    }

    @Override
    public NSIndexPath willDeselectRow(UITableView uiTableView, NSIndexPath nsIndexPath) {
        return null;
    }

    @Override
    public void didSelectRow(UITableView uiTableView, NSIndexPath nsIndexPath) {
        UIViewController vc = null;

        switch (nsIndexPath.getRow()) {
            case 0:
                vc = new FormularViewController("formular");
                break;
            case 1:
                vc = new LayoutAnimationsViewController("animations");
                break;
            case 2:
                vc = new LayoutViewController("animations");
                UIButton toggleButton = (UIButton) UIViewLayoutUtil.findViewById(vc.getView(), "toggleButton");
                toggleButton.getTitleLabel().setNumberOfLines(0);
                toggleButton.addTarget(this, Selector.register("didPressToggleButton"), UIControlEvents.TouchUpInside);
                break;
            case 4:
                vc = new LayoutViewController("includeContainer");
                break;
            case 5:
                UICollectionViewFlowLayout layout = new UICollectionViewFlowLayout();
                layout.setScrollDirection(UICollectionViewScrollDirection.Vertical);

                vc = new CollectionViewExampleViewController(layout);
                break;
            default:
                break;
        }

        if(vc != null) {
            getNavigationController().pushViewController(vc, true);
        }
    }

    @Override
    public void didDeselectRow(UITableView uiTableView, NSIndexPath nsIndexPath) {

    }

    @Override
    public UITableViewCellEditingStyle getEditingStyleForRow(UITableView uiTableView, NSIndexPath nsIndexPath) {
        return null;
    }

    @Override
    public String getTitleForDeleteConfirmationButton(UITableView uiTableView, NSIndexPath nsIndexPath) {
        return null;
    }

    @Override
    public NSArray<UITableViewRowAction> getEditActionsForRow(UITableView uiTableView, NSIndexPath nsIndexPath) {
        return null;
    }

    @Override
    public boolean shouldIndentWhileEditingRow(UITableView uiTableView, NSIndexPath nsIndexPath) {
        return false;
    }

    @Override
    public void willBeginEditingRow(UITableView uiTableView, NSIndexPath nsIndexPath) {

    }

    @Override
    public void didEndEditingRow(UITableView uiTableView, NSIndexPath nsIndexPath) {

    }

    @Override
    public NSIndexPath getTargetForMove(UITableView uiTableView, NSIndexPath nsIndexPath, NSIndexPath nsIndexPath1) {
        return null;
    }

    @Override
    public long getIndentationLevelForRow(UITableView uiTableView, NSIndexPath nsIndexPath) {
        return 0;
    }

    @Override
    public boolean shouldShowMenuForRow(UITableView uiTableView, NSIndexPath nsIndexPath) {
        return false;
    }

    @Override
    public boolean canPerformAction(UITableView uiTableView, Selector selector, NSIndexPath nsIndexPath, NSObject nsObject) {
        return false;
    }

    @Override
    public void didScroll(UIScrollView uiScrollView) {

    }

    @Override
    public void didZoom(UIScrollView uiScrollView) {

    }

    @Override
    public void willBeginDragging(UIScrollView uiScrollView) {

    }

    @Override
    public void willEndDragging(UIScrollView uiScrollView, CGPoint cgPoint, CGPoint cgPoint1) {

    }

    @Override
    public void didEndDragging(UIScrollView uiScrollView, boolean b) {

    }

    @Override
    public void willBeginDecelerating(UIScrollView uiScrollView) {

    }

    @Override
    public void didEndDecelerating(UIScrollView uiScrollView) {

    }

    @Override
    public void didEndScrollingAnimation(UIScrollView uiScrollView) {

    }

    @Override
    public UIView getViewForZooming(UIScrollView uiScrollView) {
        return null;
    }

    @Override
    public void willBeginZooming(UIScrollView uiScrollView, UIView uiView) {

    }

    @Override
    public void didEndZooming(UIScrollView uiScrollView, UIView uiView, double v) {

    }

    @Override
    public boolean shouldScrollToTop(UIScrollView uiScrollView) {
        return false;
    }

    @Override
    public void didScrollToTop(UIScrollView uiScrollView) {

    }

    //#pragma mark - UITableViewDelegate END
}
