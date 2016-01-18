package bendavid.is.intershopping;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.robotium.solo.Solo;

import bendavid.is.intershopping.activities.CreateSListActivity;
import bendavid.is.intershopping.activities.CreateSupermarketActivity;
import bendavid.is.intershopping.activities.InterShoppingActivity;

public class InterShoppingTest extends ActivityInstrumentationTestCase2<InterShoppingActivity> {

    private Solo solo;

    public InterShoppingTest() {
        super(InterShoppingActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void test1AddSupermarket() throws Exception {
        solo.unlockScreen();
        // Change tab
        solo.drag(450, 0, 400, 400, 1);
        // Click on the floating action button
        solo.clickOnView(solo.getView(R.id.fab));
        solo.assertCurrentActivity("Expected CreateSupermarketActivity",
                CreateSupermarketActivity.class);
        // Enter data
        EditText name = (EditText) solo.getView(R.id.newname);
        EditText address = (EditText) solo.getView(R.id.newaddress);
        EditText notes = (EditText) solo.getView(R.id.newnote);
        solo.enterText(name, "MINI");
        solo.enterText(address, "Koszykowa 75, Warszawa");
        solo.enterText(notes, "Tel. 22 621 93 12");
        // Click on done icon
        solo.clickOnView(solo.getView(R.id.action_menu_done));
        solo.assertCurrentActivity("Expected InterShoppingActivity",
                InterShoppingActivity.class);
        // Check it has been added
        assertTrue("No supermarket created", solo.searchText(
                "MINI"));
    }

    public void test2AddShoppingList() throws Exception {
        solo.unlockScreen();
        // Click on the floating action button
        solo.clickOnView(solo.getView(R.id.fab));
        solo.assertCurrentActivity("Expected CreateSListActivity",
                CreateSListActivity.class);
        // Enter data
        TextView date = (TextView) solo.getView(R.id.datefield);
        EditText item = (EditText) solo.getView(R.id.itemfield);
        Button addButton = (Button) solo.getView(R.id.addbtn);
        for (int i = 0; i < 5; i++) {
            solo.clearEditText(item);
            solo.enterText(item, "Item " + i );
            solo.clickOnView(addButton);
        }
        // Click on done icon
        solo.clickOnView(solo.getView(R.id.action_menu_done));
        solo.assertCurrentActivity("Expected InterShoppingActivity",
                InterShoppingActivity.class);
        // Check it has been added
        assertTrue("No supermarket created", solo.searchText(
                String.valueOf(date.getText())));
    }
}
