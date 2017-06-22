package technica.com.greencity.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;

import technica.com.greencity.R;
import technica.com.greencity.Utils;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Amanpreet Singh on 4/23/2017.
 */
public class TreeRequestForm_old extends Fragment implements CompoundButton.OnCheckedChangeListener {
    CheckBox sameAsAbove;
    TextView youAreAt;
    Button uploadPic;
    private int REQUEST_CAMERA = 1;
    private int SELECT_FILE = 2;
    EditText edt_city, name, email;
    ProgressDialog dialog = null;
    SharedPreferences sharedPreference;
    SharedPreferences.Editor editor;
    ImageView profilePic;
    private String TAG = "TreeRequestForm";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.tree_request, container, false);
        sameAsAbove = (CheckBox) rootView.findViewById(R.id.sameAsAbove);
        youAreAt = (TextView) rootView.findViewById(R.id.youAreAt);
        sameAsAbove.setOnCheckedChangeListener(this);
        profilePic = (ImageView) rootView.findViewById(R.id.profilePic);
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreference.edit();
        uploadPic = (Button) rootView.findViewById(R.id.uploadPic);
        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTreeLocation();
            }
        });
        return rootView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }

    public void uploadTreeLocation() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profilePic.setImageBitmap(getCroppedBitmap(thumbnail, 100));
                Bitmap bgArray[] = {thumbnail};
              /*  TaskImage task=new TaskImage();
                task.execute(bgArray);*/
                upNow(destination.getPath());
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();

                File myFile = new File(getRealPathFromURI(selectedImageUri));
                //  File myFile = new File(selectedImageUri.toString());

                upNow(myFile.getAbsolutePath());
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(getActivity(), selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                Bitmap bgArray[] = {bm};
              /*  TaskImage task=new TaskImage();
                task.execute(bgArray);*/
                profilePic.setImageBitmap(getCroppedBitmap(bm, 100));
            }
        }
    }

    String paths;

    public void upNow(String path) {
        this.paths = path;
        dialog = ProgressDialog.show(getActivity(), "", "Uploading file...", true);
        new Thread(new Runnable() {
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        //  tv.setText("uploading started.....");
                    }
                });
                int response = uploadFile(paths);
                Log.e(TAG, "run: " + response);
            }
        }).start();
    }

    int serverResponseCode = 0;

    public int uploadFile(String sourceFileUri) {
        String upLoadServerUri = Utils.IMAGE_UPLOAD_URL;
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        //this.UserImageFile=sourceFile;
        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File Does not exist");
            return 0;
        }
        try { // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);
            conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");


            String tempName = "";
            if (fileName.endsWith(".png"))
                tempName = "placeImageUploadBy" + sharedPreference.getString(Utils.EMAIL, null) + "_.png";
            else if (fileName.endsWith(".jpg"))
                tempName = "placeImageUploadBy" + sharedPreference.getString(Utils.EMAIL, null) + "_.jpg";
            else if (fileName.endsWith(".jpeg"))
                tempName = "placeImageUploadBy" + sharedPreference.getString(Utils.EMAIL, null) + "_.jpeg";

            else if (fileName.endsWith(".bmp"))
                tempName = "placeImageUploadBy" + sharedPreference.getString(Utils.EMAIL, null) + "_.bmp";


            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", tempName);
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);


            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + tempName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
            if (serverResponseCode == 200) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {

                        // tv.setText("File Upload Completed.");
                        Toast.makeText(getActivity(), "File Upload Complete.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            createDirectoryOnSD(sourceFile);
            //close the streams //
            fileInputStream.close();

            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            dialog.dismiss();
            ex.printStackTrace();
            Toast.makeText(getActivity(), "MalformedURLException", Toast.LENGTH_SHORT).show();
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            dialog.dismiss();
            e.printStackTrace();
            Toast.makeText(getActivity(), "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Uploads", "Exception : " + e.getMessage(), e);
        }
        dialog.dismiss();
        return serverResponseCode;
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f,
                sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);


        return output;
    }

    public void createDirectoryOnSD(File sourcePath) {
        String folder_main = "uploads";
        Log.d(TAG, "  source path: " + sourcePath);

        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }
        String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folder_main + "/test.png";
        File destination = new File(destinationPath);
        Log.d(TAG, sourcePath + "  && " + destination);
//        Toast.makeText(getActivity(), "srouce: "+sourcePath, Toast.LENGTH_LONG).show();
//        Toast.makeText(getActivity(), "des: "+destinationPath, Toast.LENGTH_LONG).show();
        try {
            // FileUtils.copyFile(sourcePath, destination);
            copyFile(sourcePath, destination);

            Log.d(TAG, " in file utils ");

            //    Toast.makeText(getActivity(), "created okk", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, " i o exception after FIleUtils ");

            //  Toast.makeText(getActivity(), "exception:  "+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    public void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }
}
