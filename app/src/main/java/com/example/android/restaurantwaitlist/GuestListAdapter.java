package com.example.android.restaurantwaitlist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.restaurantwaitlist.data.WaitlistContract;

public class GuestListAdapter extends RecyclerView.Adapter <GuestListAdapter.GuestViewHolder> {

    // Holds on to the cursor to display the waitlist
    private Cursor mCursor;
    private Context mContext;

    public GuestListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public GuestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from( mContext );
        View view = inflater.inflate( R.layout.guest_list_item, parent, false );
        return new GuestViewHolder( view );
    }

    @Override
    public void onBindViewHolder(GuestViewHolder holder, int position) {
        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.moveToPosition( position ))
            return; // bail if returned null

        // Update the view holder with the information needed to display
        String name = mCursor.getString( mCursor.getColumnIndex( WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME ) );
        int partySize = mCursor.getInt( mCursor.getColumnIndex( WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE ) );

        long id = mCursor.getLong( mCursor.getColumnIndex( WaitlistContract.WaitlistEntry._ID ) );

        // Display the guest name
        holder.nameTextView.setText( name );
        // Display the party count
        holder.partySizeTextView.setText( String.valueOf( partySize ) );

        holder.itemView.setTag( id );
    }


    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    class GuestViewHolder extends RecyclerView.ViewHolder {

        // Will display the guest name
        TextView nameTextView;
        // Will display the party size number
        TextView partySizeTextView;

        public GuestViewHolder(View itemView) {
            super( itemView );
            nameTextView = itemView.findViewById( R.id.name_text_view );
            partySizeTextView = itemView.findViewById( R.id.party_size_text_view );
        }

    }
}