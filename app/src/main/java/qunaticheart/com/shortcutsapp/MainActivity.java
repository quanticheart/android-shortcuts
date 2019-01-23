/*
 *
 *  * Copyright(c) Developed by John Alves at 2019/1/23 - BOOMMM!
 *  * .
 *
 */

package qunaticheart.com.shortcutsapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import qunaticheart.com.shortcut.AssincTask;
import qunaticheart.com.shortcut.ShortcutHelper;
import qunaticheart.com.shortcut.Utils;
import qunaticheart.com.shortcut.WebsiteUtils;

public class MainActivity extends AppCompatActivity {

    //==============================================================================================
    //
    // ** init Vars
    //
    //==============================================================================================

    private Context context;

    private static final String ID_ADD_WEBSITE = "add_website";
    private static final String ACTION_ADD_WEBSITE = "ADD_WEBSITE";

    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private ShortcutHelper mHelper;

    //==============================================================================================
    //
    // ** OnCreate
    //
    //==============================================================================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        context = getApplicationContext();

        initShortcutHelper();
        initVars();
        initActions();

        verifyAction();

    }

    private void initVars() {
        recyclerView = findViewById(R.id.list);
    }

    private void initActions() {
        adapter = new RecyclerAdapter(context, mHelper.getShortcuts());
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new RecyclerAdapter.OnclickListener() {
            @Override
            public void removeShortcut(ShortcutInfo shortcut) {
                Toast.makeText(context, shortcut.getId(), Toast.LENGTH_SHORT).show();
                mHelper.removeShortcut(shortcut);
                refreshList();
            }

            @Override
            public void disableOrEnableShortcut(ShortcutInfo shortcut) {
                if (shortcut.isEnabled()) {
                    mHelper.disableShortcut(shortcut);
                } else {
                    mHelper.enableShortcut(shortcut);
                }
                refreshList();
            }

            @Override
            public void addOnHomeScreen(ShortcutInfo shortcut, Bitmap icon) {
                mHelper.createHomescreenShortcut(shortcut, icon);
            }
        });

        findViewById(R.id.add).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addWebSite();
            }
        });

        new AssincTask().refreshShortcuts(/*force=*/ context, false);
    }

    //==============================================================================================
    //
    // ** create Shortcut Helpers
    //
    //==============================================================================================

    private void initShortcutHelper() {
        mHelper = new ShortcutHelper(context);
        mHelper.maybeRestoreAllDynamicShortcuts();
    }

    //==============================================================================================
    //
    // ** Verify Action Intent
    //
    //==============================================================================================

    private void verifyAction() {
        if (ACTION_ADD_WEBSITE.equals(getIntent().getAction())) {
            addWebSite();
        }
    }

    //==============================================================================================
    //
    // ** Activity LifeCycle
    //
    //==============================================================================================

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    //==============================================================================================
    //
    // ** Create Dialog for add Website Shortcut
    //
    //==============================================================================================

    private void addWebSite() {
        Utils.debugLog("addWebSite");

        mHelper.reportShortcutUsed(ID_ADD_WEBSITE);

        final EditText editUri = new EditText(this);
        editUri.setHint("http://www.android.com/");
        editUri.setInputType(EditorInfo.TYPE_TEXT_VARIATION_URI);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Add new website")
                .setMessage("Type URL of a website")
                .setView(editUri)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String url = editUri.getText().toString().trim();
                        if (url.length() > 0) {
                            addUriAsync(url);
                        }
                    }
                })
                .show();
    }

    //==============================================================================================
    //
    // ** AsyncTask for Create Shortcut
    //
    //==============================================================================================

    @SuppressLint("StaticFieldLeak")
    private void addUriAsync(final String uri) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                WebsiteUtils.addWebSiteShortcut(context, uri);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                refreshList();
            }
        }.execute();
    }

    private void refreshList() {
        adapter.refreshList(mHelper.getShortcuts());
    }

}
