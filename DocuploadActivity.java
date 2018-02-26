package com.addventure.loanadda;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.addventure.loanadda.Multipart.MultipartRequest;
import com.addventure.loanadda.Multipart.NetworkOperationHelper;
import com.addventure.loanadda.Utility.AppConfig;
import com.addventure.loanadda.Utility.Network;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class DocuploadActivity extends BaseActivity implements View.OnClickListener {
    private static final int TIMEOUT_CONNECTION = 10000;
    private static final int TIMEOUT_SOCKET = 10000;

    Toolbar toolbar;
    TextView titleTv;
    ImageView pancarduploadIv,aadharcarduploadIv,ministmntuploadIv;
    Button pancardBtn,aadharcardBtn,ministmntBtn,submitBtn;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;
    private int MY_REQUEST_CODE=123;
    public boolean isPancardClick=false;
    public boolean isAadharcardClick=false;
    public boolean isMinistatementClick=false;
    String galleryPicturePath;
    String contactStr;
    SharedPreferences loginSp;
    public static final String loginPrefrence = "loginSp";
    String imageString;
    String galleryPicturePathPan="/storage/emulated/0/DCIM/Camera/IMG_20171216_205835.jpg",
            galleryPicturePathaadhar="/storage/emulated/0/DCIM/Camera/IMG_20171216_205835.jpg",
            galleryPicturePathBank="/storage/emulated/0/DCIM/Camera/IMG_20171216_205835.jpg";
    String ministmtStr="https://www.perfios.com/KuberaVault/insights/iiflDigital/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getStatusbarcolor();
        setContentView(R.layout.activity_docupload);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        titleTv = (TextView)findViewById(R.id.title);
        pancarduploadIv = (ImageView) findViewById(R.id.pancardupload);
        aadharcarduploadIv = (ImageView) findViewById(R.id.aadharcardupload);
        ministmntuploadIv = (ImageView) findViewById(R.id.ministmntupload);
        aadharcardBtn = (Button) findViewById(R.id.aadharcardBtn);
        ministmntBtn = (Button) findViewById(R.id.ministmntBtn);
        pancardBtn = (Button) findViewById(R.id.pancardBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        titleTv.setText(getResources().getString(R.string.docuploadd));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });

        loginSp = getSharedPreferences(loginPrefrence, MODE_PRIVATE);
        contactStr = loginSp.getString("contactNo", "");

        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);

        pancardBtn.setOnClickListener(this);
        aadharcardBtn.setOnClickListener(this);
        ministmntBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pancardBtn:
                isPancardClick=true;
                getPancardupload();
                //getUploadImageString();

                break;
            case R.id.aadharcardBtn:
                isAadharcardClick=true;
                getAadharcard();

                break;
            case R.id.ministmntBtn:
                isMinistatementClick=true;
                getMinistatement();

                String DownloadUrl = AppConfig.BaseUrll+"perfios_report.xls";
                String fileName = "perfios_report.xls";

                if (Network.isNetworkCheck(getApplicationContext())) {
                    DownloadDatabase(DownloadUrl,fileName);
                }
                else {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.internet_check),Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.submitBtn:
                getUploadDocument();
                break;
        }

    }


    private void getMinistmt() {
        Uri uri = Uri.parse(ministmtStr); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void getMinistatement() {

        if (ActivityCompat.checkSelfPermission(DocuploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(DocuploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(DocuploadActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(DocuploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(DocuploadActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(DocuploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
            editor.commit();


        } else {
            //You already have the permission, just go ahead.
            //selectImage();
            Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }

    }

    private void getAadharcard() {

        if (ActivityCompat.checkSelfPermission(DocuploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(DocuploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(DocuploadActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(DocuploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(DocuploadActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(DocuploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
            editor.commit();


        } else {
            //You already have the permission, just go ahead.
            //selectImage();
            Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }

    }

    private void getPancardupload() {

        if (ActivityCompat.checkSelfPermission(DocuploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(DocuploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(DocuploadActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(DocuploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(DocuploadActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(DocuploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
            editor.commit();


        } else {
            //You already have the permission, just go ahead.
            //selectImage();
            Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                //selectImage();
                Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(DocuploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(DocuploadActivity.this);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                            ActivityCompat.requestPermissions(DocuploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(DocuploadActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
               //selectImage();
                Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        }
    }

    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(DocuploadActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    pancarduploadIv.setImageBitmap(bitmap);

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {

                /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                Log.d("imageString",imageString);*/


                if (isPancardClick==true) {
                    Uri selectedImage = data.getData();
                    String[] filePath = { MediaStore.Images.Media.DATA };
                    Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    galleryPicturePathPan = c.getString(columnIndex);
                    c.close();
                    Bitmap thumbnail = (BitmapFactory.decodeFile(galleryPicturePathPan));
                    Log.d("galleryPicturePathPan", galleryPicturePathPan);
                    pancarduploadIv.setImageBitmap(thumbnail);
                    isPancardClick=false;
                }
                else if (isAadharcardClick==true) {
                    Uri selectedImage = data.getData();
                    String[] filePath = { MediaStore.Images.Media.DATA };
                    Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    galleryPicturePathaadhar = c.getString(columnIndex);
                    c.close();
                    Bitmap thumbnail = (BitmapFactory.decodeFile(galleryPicturePathaadhar));
                    Log.d("galleryPictureaadhar", galleryPicturePathaadhar);
                    aadharcarduploadIv.setImageBitmap(thumbnail);
                    isAadharcardClick=false;
                }
                else if (isMinistatementClick==true) {
                    Uri selectedImage = data.getData();
                    String[] filePath = { MediaStore.Images.Media.DATA };
                    Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    galleryPicturePathBank = c.getString(columnIndex);
                    c.close();
                    Bitmap thumbnail = (BitmapFactory.decodeFile(galleryPicturePathBank));
                    Log.d("galleryPicturePathBank", galleryPicturePathBank);
                    ministmntuploadIv.setImageBitmap(thumbnail);
                    isMinistatementClick=false;
                }
            }
        }
    }

    public void getUploadDocument() {
        showProgressDialog();
        String panimg_url= AppConfig.BaseUrl+"fileupload?contact_no="+contactStr;
        MultipartRequest req = null;
        HashMap<String, String> map = new HashMap<>();
        Log.d("contact_no",contactStr);
        Log.d("panimg_url",panimg_url);

        //map.put("contact_no",contactStr);
        map.put("pan",galleryPicturePathPan);
        map.put("aadhar_card",galleryPicturePathaadhar);
        map.put("bank_stattement",galleryPicturePathBank);
        map.put("Accept","application/json");
        map.put("Content-Type","application/json");
       /* Log.d("galleryPicturePath",galleryPicturePathPan);
        Log.d("galleryPictureadhar",galleryPicturePathaadhar);
        Log.d("galleryPicturePathBank",galleryPicturePathBank);*/

        try {
            req = new MultipartRequest(panimg_url, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    Log.d("DocError", error.toString());
                    Toast.makeText(getApplicationContext(),""+error.toString(),Toast.LENGTH_LONG).show();
                    return;
                }
            }, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();
                    Log.d("docrespose",response);

                    Toast.makeText(getApplicationContext(),"Document Uploaded",Toast.LENGTH_LONG).show();
                    Log.d("panimguploadRes", response);
                    Log.d("contact_no",contactStr);
                   /* try {
                        JSONObject object = new JSONObject(response);
                        int succ = object.getInt("success");
                        if (succ == 1) {
                            Intent i = new Intent(DocuploadActivity.this, DocuploadActivity.class);
                            startActivity(i);
                            finish();
                        } else if (succ == 0) {
                            Toast.makeText(getApplicationContext(), object.getString("error_msg"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/

                }
            }, map);

            req.addImageData("pan", new File(galleryPicturePathPan));
            req.addImageData("aadhar_card", new File(galleryPicturePathaadhar));
            req.addImageData("bank_stattement", new File(galleryPicturePathBank));
            NetworkOperationHelper.getInstance(DocuploadActivity.this).addToRequestQueue(req);
            //Log.d("imagepath",imgDecodableString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void DownloadDatabase(String DownloadUrl, String fileName) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/");
            if (dir.exists() == false) {
                dir.mkdirs();
            }

            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                //your codes here

                URL url = new URL(DownloadUrl);
                File file = new File(dir, fileName);

                long startTime = System.currentTimeMillis();
                Log.d("DownloadManager", "download url:" + url);
                Log.d("DownloadManager", "download file name:" + fileName);

                URLConnection uconn = url.openConnection();
                uconn.setReadTimeout(TIMEOUT_CONNECTION);
                uconn.setConnectTimeout(TIMEOUT_SOCKET);

                InputStream is = uconn.getInputStream();
                BufferedInputStream bufferinstream = new BufferedInputStream(is);

                ByteArrayBuffer baf = new ByteArrayBuffer(5000);
                int current = 0;
                while ((current = bufferinstream.read()) != -1) {
                    baf.append((byte) current);
                }

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baf.toByteArray());
                fos.flush();
                fos.close();
                Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + "sec");
                int dotindex = fileName.lastIndexOf('.');

                if (dotindex >= 0) {
                    fileName = fileName.substring(0, dotindex);
                    Log.d("fileName",fileName);
                    createNotification();
                }
            }
        }

        catch(IOException e) {
                Log.d("DownloadManager", "Error:" + e);
            }
    }

    public void createNotification() {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, DocuploadActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Download")
                .setContentText("Perfios File Download!").setSmallIcon(R.drawable.appicon)
                .setContentIntent(pIntent).build();
         NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

    }


    public void getUploadImageString() {

        String panimg_url= AppConfig.BaseUrl+"documentupload";
        Toast.makeText(getApplicationContext(),panimg_url,Toast.LENGTH_SHORT).show();

        StringRequest request = new StringRequest(Request.Method.POST, panimg_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                Log.v("DocResponse",response);
                System.out.print(response);

            }

        } , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),volleyError.toString(),Toast.LENGTH_SHORT).show();
                Log.v("Docerror",volleyError.toString());
            }

        }) {
            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("contact_no",contactStr);
                parameters.put("pan",imageString);
                return parameters;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(DocuploadActivity.this);
        rQueue.add(request);
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}

