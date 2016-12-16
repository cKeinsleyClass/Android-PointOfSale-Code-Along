package edu.rosehulman.keinslc.pointofsale;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private Item currentItem;
    private TextView mNameTextView;
    private TextView mQuantityTextView;
    private TextView mDateTextView;
    private ArrayList<Item> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Wizardry
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mItems = new ArrayList<>();
        // Capturing the views
        mNameTextView = (TextView) findViewById(R.id.name_text);
        registerForContextMenu(mNameTextView);
        mQuantityTextView = (TextView) findViewById(R.id.quantity_text);
        mDateTextView = (TextView) findViewById(R.id.date_text);

        // Capturing the floating action button and assigning a listener to it
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                addEditItem(false);
            }
        });
    }

    // Detects when they long press and inflates a context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_context, menu);
    }

    // Detects when they click on a button in the context menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_context_edit:
                addEditItem(true);
                return true;
            case R.id.menu_context_remove:
                mItems.remove(currentItem);
                currentItem = new Item();
                showCurrentItem();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Shows the current item
     */
    private void showCurrentItem() {
        mNameTextView.setText(currentItem.getName());
        mDateTextView.setText(currentItem.getDeliveryDateString());
        mQuantityTextView.setText(currentItem.getQuantity() + "");
    }

    // For the App bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // For action bar clicks like our reset button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_reset) {
            final Item temp = currentItem;
            mItems.remove(currentItem);

            currentItem = new Item();
            showCurrentItem();
            View coordinator = findViewById(R.id.coordinator_layout);
            Snackbar snackbar = Snackbar.make(coordinator, "Item cleared", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentItem = temp;
                    mItems.add(currentItem);
                    showCurrentItem();
                }
            });
            snackbar.show();
            return true;
        }
        if (id == R.id.action_search) {
            showSearchDialog();
            return true;
        }
        if (id == R.id.action_clear_all) {
            clearAllItems();
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearAllItems() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.remove);
        builder.setMessage(R.string.confirmation_dialog_message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mItems.clear();
                currentItem = new Item();
                showCurrentItem();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.chose_item_title);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setItems(getNames(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentItem = mItems.get(which);
                showCurrentItem();
            }
        });

        builder.create().show();
    }

    private String[] getNames() {
        String[] names = new String[mItems.size()];
        for (int i = 0; i < mItems.size(); i++) {
            names[i] = mItems.get(i).getName();
        }
        return names;
    }

    /**
     * Called when the item is clicked
     */
    private void addEditItem(boolean isEditing) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_add, null, false);
        builder.setView(view);

        final EditText nameET = (EditText) view.findViewById(R.id.edit_name);
        final EditText quantityET = (EditText) view.findViewById(R.id.edit_quantity);
        // Makes a new calendar
        final GregorianCalendar calendar = new GregorianCalendar();
        // Make a view with that calendar
        final CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendar_view);
        // Need to use a separate listener for the calendar
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                calendar.set(year, month, day);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = nameET.getText().toString();
                int quantity = 1;
                if (quantityET.getText().equals("")) {
                    Integer.parseInt(quantityET.getText().toString());
                }
                currentItem = new Item(name, quantity, calendar);
                mItems.add(currentItem);
                showCurrentItem();
            }
        });
        if (isEditing) {
            mItems.remove(currentItem);
            nameET.setText(currentItem.getName());
            quantityET.setText(currentItem.getQuantity() + "");
            calendarView.setDate(currentItem.getDeliveryDateTime());
        }

        builder.create().show();
    }
}
