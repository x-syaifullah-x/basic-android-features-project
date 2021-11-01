package com.example.androidlabs;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {
    private MessageAdapter messageAdapter;
    private List<Message> messages;
    private EditText newMessageView;
    private String newMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages);
        ListView listView = findViewById(R.id.theListView);
        listView.setAdapter(messageAdapter);

        boolean isTablet = findViewById(R.id.fragmentLocation) != null;

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DetailsFragment.ARG_IS_LARGE, isTablet);
            bundle.putLong(DetailsFragment.ARG_ID, messages.get(position).getId());
            bundle.putString(DetailsFragment.ARG_TEXT, messages.get(position).getText());
            bundle.putBoolean(DetailsFragment.ARG_IS_SEND, messages.get(position).isSend());

            if (isTablet) {
                Fragment fragment = new DetailsFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, fragment)
                        .commit();
            } else {
                Intent intent = new Intent(this, EmptyActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener((arg0, arg1, position, arg3) -> {
            deleteMessage(position);
            return true;
        });
        newMessageView = (EditText) findViewById(R.id.EditText);
        findViewById(R.id.send_button).setOnClickListener(view -> {
            View focusView = ChatRoomActivity.this.getCurrentFocus();
            if (newMessageView != null) {
                newMessage = newMessageView.getText().toString();
                if (!newMessage.isEmpty()) {
                    saveMessage(true, newMessage);
                    newMessageView.setText("");
                    if (focusView != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
        findViewById(R.id.receive_button).setOnClickListener(view -> {
            View focusView = ChatRoomActivity.this.getCurrentFocus();
            if (newMessageView != null) {
                newMessage = newMessageView.getText().toString();
                if (!newMessage.isEmpty()) {
                    saveMessage(false, newMessage);
                    newMessageView.setText("");
                    if (focusView != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
        updateInterface();
    }

    public void purgeMessage(int position) {
        try (SQLiteDatabase database = new DatabaseHelper(this).getWritableDatabase()) {
            database.delete(DatabaseSchema.Messages.NAME, DatabaseSchema.Messages.Columns.ID + " LIKE ?", new String[]{messages.get(position).getId() + ""});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentLocation);
            if (fragment != null)
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }
        updateInterface();
    }

    public void deleteMessage(int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to delete this row at position " + " with id " + messages.get(position).getId());
        alertDialogBuilder.setPositiveButton("Yes", (arg0, arg1) -> purgeMessage(position));
        alertDialogBuilder.setNegativeButton("No", null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void updateInterface() {
        try (SQLiteDatabase database = new DatabaseHelper(this).getReadableDatabase()) {
            Cursor cursor = database.query(DatabaseSchema.Messages.NAME, new String[]{DatabaseSchema.Messages.Columns.ID, DatabaseSchema.Messages.Columns.IS_SEND, DatabaseSchema.Messages.Columns.TEXT}, null, null, null, null, DatabaseSchema.Messages.Columns.ID);
            if (cursor.getCount() >= 0) {
                cursor.moveToFirst();
                messages.clear();
                while (!cursor.isAfterLast()) {
                    @SuppressLint("Range") Long id = cursor.getLong(cursor.getColumnIndex(DatabaseSchema.Messages.Columns.ID));
                    @SuppressLint("Range") String text = cursor.getString(cursor.getColumnIndex(DatabaseSchema.Messages.Columns.TEXT));
                    @SuppressLint("Range") boolean isSend = cursor.getInt(cursor.getColumnIndex(DatabaseSchema.Messages.Columns.IS_SEND)) != 0;
                    messages.add(new Message(id, isSend, text));
                    cursor.moveToNext();
                }
            }
            printCursor(cursor, database.getVersion());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        messageAdapter.notifyDataSetChanged();
    }

    public void saveMessage(boolean isSend, String text) {
        if (text != null && !text.isEmpty()) {
            try (SQLiteDatabase database = new DatabaseHelper(this).getWritableDatabase()) {
                ContentValues values = new ContentValues();
                values.put(DatabaseSchema.Messages.Columns.IS_SEND, isSend);
                values.put(DatabaseSchema.Messages.Columns.TEXT, text);
                database.insert(DatabaseSchema.Messages.NAME, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }

            updateInterface();
        }
    }

    public void printCursor(Cursor c, int version) {
        Log.d(getPackageName(), "Number of columns in cursor = " + c.getColumnCount());
        Log.d(getPackageName(), "Names of columns in cursor = " + c.getColumnNames()[0] + ", " + c.getColumnNames()[1] + ", " + c.getColumnNames()[2]);
        Log.d(getPackageName(), "Number of results in cursor = " + c.getCount());
        Log.d(getPackageName(), "Rows in cursor = ");
        if (c.getCount() > 0 && c.moveToFirst()) {
            while (!c.isAfterLast()) {
                @SuppressLint("Range") Long id = c.getLong(c.getColumnIndex(DatabaseSchema.Messages.Columns.ID));
                @SuppressLint("Range") String text = c.getString(c.getColumnIndex(DatabaseSchema.Messages.Columns.TEXT));
                @SuppressLint("Range") boolean isSend = c.getInt(c.getColumnIndex(DatabaseSchema.Messages.Columns.IS_SEND)) != 0;
                Log.d(getPackageName(), id + " " + text + " " + isSend);
                c.moveToNext();
            }
        }
        Log.d(getPackageName(), "Database version = " + version);
    }
}
