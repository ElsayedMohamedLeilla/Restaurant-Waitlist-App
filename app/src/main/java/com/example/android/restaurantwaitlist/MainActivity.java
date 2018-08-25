package com.example.android.restaurantwaitlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.android.restaurantwaitlist.data.WaitlistContract;
import com.example.android.restaurantwaitlist.data.WaitlistDbHelper;


public class MainActivity extends AppCompatActivity {

    private GuestListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private EditText mNewGuestNameEditText;
    private EditText mNewPartySizeEditText;
    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );


        RecyclerView waitlistRecyclerView;

        // Set local attributes to corresponding views
        waitlistRecyclerView = this.findViewById( R.id.all_guests_list_view );
        mNewGuestNameEditText = this.findViewById( R.id.person_name_edit_text );
        mNewPartySizeEditText = this.findViewById( R.id.party_count_edit_text );

        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        waitlistRecyclerView.setLayoutManager( new LinearLayoutManager( this ) );


        // Create a DB helper (this will create the DB if run for the first time)
        WaitlistDbHelper dbHelper = new WaitlistDbHelper( this );

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        mDb = dbHelper.getWritableDatabase();

        // Get all guest info from the database and save in a cursor
        Cursor cursor = getAllGuests();

        // Create an adapter for that cursor to display the data
        mAdapter = new GuestListAdapter( this, cursor );

        // Link the adapter to the RecyclerView
        waitlistRecyclerView.setAdapter( mAdapter );


        new ItemTouchHelper( new ItemTouchHelper.SimpleCallback( 0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, we only care about swiping
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                removeGuest( id );
                mAdapter.swapCursor( getAllGuests() );
            }


        } ).attachToRecyclerView( waitlistRecyclerView );

    }


    public void addToWaitlist(View view) {
        if (mNewGuestNameEditText.getText().length() == 0 ||
                mNewPartySizeEditText.getText().length() == 0) {
            return;
        }
        //default party size to 1
        int partySize = 1;
        try {
            //mNewPartyCountEditText inputType="number", so this should always work
            partySize = Integer.parseInt( mNewPartySizeEditText.getText().toString() );
        } catch (NumberFormatException ex) {
            Log.e( LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage() );
        }

        // Add guest info to mDb
        addNewGuest( mNewGuestNameEditText.getText().toString(), partySize );

        // Update the cursor in the adapter to trigger UI to display the new list
        mAdapter.swapCursor( getAllGuests() );

        //clear UI text fields
        mNewPartySizeEditText.clearFocus();
        mNewGuestNameEditText.getText().clear();
        mNewPartySizeEditText.getText().clear();
    }


    private Cursor getAllGuests() {
        return mDb.query(
                WaitlistContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP
        );
    }


    private long addNewGuest(String name, int partySize) {
        ContentValues cv = new ContentValues();
        cv.put( WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name );
        cv.put( WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, partySize );
        return mDb.insert( WaitlistContract.WaitlistEntry.TABLE_NAME, null, cv );
    }


    private boolean removeGuest(long id) {
        return mDb.delete( WaitlistContract.WaitlistEntry.TABLE_NAME, WaitlistContract.WaitlistEntry._ID + "=" + id, null ) > 0;
    }

}