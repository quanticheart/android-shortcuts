/*
 *
 *  * Copyright(c) Developed by John Alves at 2019/1/23 - BOOMMM!
 *  * .
 *
 */

package qunaticheart.com.shortcutsapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import qunaticheart.com.shortcut.AndroidUtil;
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

    private TextView msg;
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private Button button;
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
        initVars();
    }

    private void initVars() {
        msg = findViewById(R.id.text);
        if (!verifyApiLevel()) {
            msg.setText(getString(R.string.not_suported));
        } else {
            button = findViewById(R.id.add);
            recyclerView = findViewById(R.id.list);
            button.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

            initShortcutHelper();
            initActions();
            verifyAction();
        }

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
    // ** Create Actions
    //
    //==============================================================================================

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void initActions() {
        adapter = new RecyclerAdapter(context, mHelper.getShortcuts());
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new RecyclerAdapter.OnclickListener() {
            @Override
            public void removeShortcut(ShortcutInfo shortcut) {
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

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addWebSite();
            }
        });

        new AssincTask().refreshShortcuts(/*force=*/ context, false);
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
        if (verifyApiLevel())
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
        editUri.setInputType(EditorInfo.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);

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
        if (mHelper.getShortcuts().size() == 0) {
            msg.setText(getString(R.string.add_new_shortcuts_msg));
            msg.setVisibility(View.VISIBLE);
        } else {
            msg.setVisibility(View.GONE);
        }
        adapter.refreshList(mHelper.getShortcuts());
    }

    //==============================================================================================
    //
    // ** Verify Api Level
    //
    //==============================================================================================

    private boolean verifyApiLevel() {
        return AndroidUtil.verifyVesionMin(AndroidUtil.O);
    }

}
