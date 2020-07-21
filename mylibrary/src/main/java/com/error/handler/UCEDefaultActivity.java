/*
 *
 *  * Copyright © 2018 Rohit Sahebrao Surwase.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.error.handler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public final class UCEDefaultActivity extends AppCompatActivity {
    private File txtFile;
    private String strCurrentErrorLog;
    Toolbar toolbar;

    @SuppressLint("PrivateResource")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_error_activity);
        if (getSupportActionBar() == null) {
            toolbar = findViewById(R.id.toolbar);
            //     toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
           /* toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });*/
            toolbar.setVisibility(View.VISIBLE);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);


        findViewById(R.id.button_close_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UCEHandler.closeApplication(UCEDefaultActivity.this);

            }
        });

        findViewById(R.id.button_restart_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UCEHandler.RestartApplication(UCEDefaultActivity.this);

                // SendError();
            }
        });
        findViewById(R.id.button_copy_error_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyErrorToClipboard();
            }
        });
        findViewById(R.id.button_share_error_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareErrorLog();
            }
        });
        findViewById(R.id.button_save_error_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveErrorLogToFile(true);
            }
        });
        findViewById(R.id.button_email_error_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailErrorLog();
            }
        });
        findViewById(R.id.button_view_error_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(UCEDefaultActivity.this)
                        .setTitle(getString(R.string.Error_Log))
                        .setMessage(getAllErrorDetailsFromIntent(UCEDefaultActivity.this, getIntent()))
                        .setPositiveButton(getString(R.string.Copy_Log_Close),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        copyErrorToClipboard();
                                        dialog.dismiss();
                                    }
                                })
                        .setNeutralButton(getString(R.string.Close),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                        .show();
                TextView textView = dialog.findViewById(android.R.id.message);
                if (textView != null) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        UCEHandler.RestartApplication(UCEDefaultActivity.this);
        return super.onOptionsItemSelected(item);
    }
    public String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    private String getVersionName(Context context) {
        try {
            return( context.getPackageManager().getPackageInfo(context.getPackageName(), 0)).versionName;
        } catch (Exception e) {
            return getString(R.string.Unknown);
        }
    }

    private String getActivityLogFromIntent(Intent intent) {
        return intent.getStringExtra(UCEHandler.eXTRA_ACTIVITY_LOG);
    }

    private String getError_Page(Intent intent) {
        return intent.getStringExtra(UCEHandler.eXTRA_Error_Page);
    }

    private String getErrorMessage(Intent intent) {
        return intent.getStringExtra(UCEHandler.eXTRA_ErrorMessage);
    }

    private String getStackTrace(Intent intent) {
        return intent.getStringExtra(UCEHandler.eXTRA_StackTrace);
    }

    private String getStackTraceFromIntent(Intent intent) {
        return intent.getStringExtra(UCEHandler.eXTRA_STACK_TRACE);
    }

    private void emailErrorLog() {
        saveErrorLogToFile(false);
        String errorLog = getAllErrorDetailsFromIntent(UCEDefaultActivity.this, getIntent());
        String[] emailAddressArray = UCEHandler.commaSeparatedEmailAddresses.trim().split("\\s*,\\s*");
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddressArray);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getApplicationName(UCEDefaultActivity.this) + " Application Crash Error Log");
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_welcome_note) + errorLog);
        if (txtFile.exists()) {
            Uri filePath = Uri.fromFile(txtFile);
            emailIntent.putExtra(Intent.EXTRA_STREAM, filePath);
        }
        startActivity(Intent.createChooser(emailIntent, getString(R.string.Email_Error_Log)));
    }

    private void saveErrorLogToFile(boolean isShowToast) {
        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isSDPresent && isExternalStorageWritable()) {
            Date currentDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String strCurrentDate = dateFormat.format(currentDate);
            strCurrentDate = strCurrentDate.replace(" ", "_");
            String errorLogFileName = getApplicationName(UCEDefaultActivity.this) + "_Error-Log_" + strCurrentDate;
            String errorLog = getAllErrorDetailsFromIntent(UCEDefaultActivity.this, getIntent());
            String fullPath = Environment.getExternalStorageDirectory() + "/AppErrorLogs_UCEH/";
            FileOutputStream outputStream;
            try {
                File file = new File(fullPath);
                file.mkdir();
                txtFile = new File(fullPath + errorLogFileName + ".txt");
                txtFile.createNewFile();
                outputStream = new FileOutputStream(txtFile);
                outputStream.write(errorLog.getBytes());
                outputStream.close();
                if (txtFile.exists() && isShowToast) {
                    Toast.makeText(this, getString(R.string.File_Saved_Successfully), Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                if (isShowToast) {
                    Toast.makeText(this, getString(R.string.Storage_Permission_Not_Found), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private void shareErrorLog() {
        String errorLog = getAllErrorDetailsFromIntent(UCEDefaultActivity.this, getIntent());
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.Application_Crash_Error_Log));
        share.putExtra(Intent.EXTRA_TEXT, errorLog);
        startActivity(Intent.createChooser(share, getString(R.string.Share_Error_Log)));
    }

    private void copyErrorToClipboard() {
        String errorInformation = getAllErrorDetailsFromIntent(UCEDefaultActivity.this, getIntent());
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText(getString(R.string.View_Error_Log), errorInformation);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(UCEDefaultActivity.this, getString(R.string.Error_Log_Copied), Toast.LENGTH_SHORT).show();
        }
    }

    private String getAllErrorDetailsFromIntent(Context context, Intent intent) {
        if (TextUtils.isEmpty(strCurrentErrorLog)) {
            String LINE_SEPARATOR = "\n";
            StringBuilder errorReport = new StringBuilder();
            errorReport.append("\n***** DEVICE INFO \n");
            errorReport.append("Brand: ");
            errorReport.append(Build.BRAND);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Device: ");
            errorReport.append(Build.DEVICE);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Model: ");
            errorReport.append(Build.MODEL);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Manufacturer: ");
            errorReport.append(Build.MANUFACTURER);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Product: ");
            errorReport.append(Build.PRODUCT);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("SDK: ");
            errorReport.append(Build.VERSION.SDK_INT);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Release: ");
            errorReport.append(Build.VERSION.RELEASE);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("\n***** APP INFO \n");
            String versionName = getVersionName(context);
            errorReport.append("Version: ");
            errorReport.append(versionName);
            errorReport.append(LINE_SEPARATOR);
            Date currentDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String firstInstallTime = getFirstInstallTimeAsString(context, dateFormat);
            if (!TextUtils.isEmpty(firstInstallTime)) {
                errorReport.append("Installed On: ");
                errorReport.append(firstInstallTime);
                errorReport.append(LINE_SEPARATOR);
            }
            String lastUpdateTime = getLastUpdateTimeAsString(context, dateFormat);
            if (!TextUtils.isEmpty(lastUpdateTime)) {
                errorReport.append("Updated On: ");
                errorReport.append(lastUpdateTime);
                errorReport.append(LINE_SEPARATOR);
            }
            errorReport.append("Current Date: ");
            errorReport.append(dateFormat.format(currentDate));
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("\n***** ERROR LOG \n");
            errorReport.append(getStackTraceFromIntent(intent));
            errorReport.append(LINE_SEPARATOR);
            String activityLog = getActivityLogFromIntent(intent);
            errorReport.append(LINE_SEPARATOR);
            if (activityLog != null) {
                errorReport.append("\n***** USER ACTIVITIES \n");
                errorReport.append("User Activities: ");
                errorReport.append(activityLog);
                errorReport.append(LINE_SEPARATOR);
            }
            errorReport.append("\n***** END OF LOG *****\n");
            strCurrentErrorLog = errorReport.toString();
            return strCurrentErrorLog;
        } else {
            return strCurrentErrorLog;
        }
    }

    private String getFirstInstallTimeAsString(Context context, DateFormat dateFormat) {
        long firstInstallTime;
        try {
            firstInstallTime = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .firstInstallTime;
            return dateFormat.format(new Date(firstInstallTime));
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private String getLastUpdateTimeAsString(Context context, DateFormat dateFormat) {
        long lastUpdateTime;
        try {
            lastUpdateTime = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .lastUpdateTime;
            return dateFormat.format(new Date(lastUpdateTime));
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void SendError() {
        String Error_Product = "OfferSwiper";
        String Error_Customer = "Android";
        String Error_Page = getError_Page(getIntent());
        String Error_Message = getErrorMessage(getIntent());
        String Error_Details = getStackTrace(getIntent());
        String Error_note = getAllErrorDetailsFromIntent(UCEDefaultActivity.this, getIntent());
        Retrofit retrofit = RetrofitClientInstanceWithLink.getRetrofitInstance(UCEHandler.Link);
        ErrorHandler_Client errorHandler_client = retrofit.create(ErrorHandler_Client.class);
        Call<JsonElement> call = errorHandler_client.SendError(Error_Product,
                Error_Customer,
                Error_Page,
                Error_Message,
                Error_Details,
                Error_note);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {


            }
        });

    }
}
