package com.example.contacts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.contacts.databinding.ActivityAddContactBinding;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Pattern;


public class AddContactActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private Contact contact;

    private int GALLERY = 1;
    private int CAM_REQUEST = 2;

    //Parameters
    private Bitmap bitmap;
    private String imgPath = "";
    private int id = 0;

    ActivityAddContactBinding activityAddContactBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddContactBinding = ActivityAddContactBinding.inflate(getLayoutInflater());
        setContentView(activityAddContactBinding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        contact = new Contact();

        if (Constants.EDIT_CLICKED) {
            activityAddContactBinding.btnAddEdit.setText(R.string.add);
            activityAddContactBinding.btnAddEdit.setOnClickListener(view -> {
                addClick();
            });

        } else {
            activityAddContactBinding.btnAddEdit.setText(R.string.update);
            editClick();

            activityAddContactBinding.btnAddEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contact.setName(activityAddContactBinding.etName.getText().toString());
                    contact.setEmail(activityAddContactBinding.etEmail.getText().toString());
                    contact.setNumber(activityAddContactBinding.etNumber.getText().toString());
                    contact.setImage(imgPath);
                    contact.setId(id);

                    databaseHelper.updateContact(contact);

                    Intent edit = new Intent(AddContactActivity.this, ContactListActivity.class);
                    startActivity(edit);
                }
            });
        }

        activityAddContactBinding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileClick();
            }
        });
    }

    //AddFunction
    public void addClick() {
        String name = activityAddContactBinding.etName.getText().toString();
        String email = activityAddContactBinding.etEmail.getText().toString();
        String number = activityAddContactBinding.etNumber.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter the name", Toast.LENGTH_SHORT).show();
        } else if (number.isEmpty()) {
            Toast.makeText(this, "Please enter the number", Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty()) {
            Toast.makeText(this, "Please enter the email", Toast.LENGTH_SHORT).show();
        } else {
            if (!databaseHelper.checkContact(number)) {
                contact.setName(name);
                contact.setEmail(email);
                contact.setNumber(number);
                contact.setImage(this.imgPath);
                databaseHelper.addUser(contact);

                Intent intent = new Intent(this, ContactListActivity.class);
                startActivity(intent);

                showEmptyInput();

                Toast.makeText(this, R.string.success_message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.failure_message, Toast.LENGTH_SHORT).show();
            }
        }


    }

    //EditFunction
    private void editClick() {

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String number = intent.getStringExtra("number");
        String image = intent.getStringExtra("image");
        id = intent.getIntExtra("id", 0);

        activityAddContactBinding.etName.setText(name);
        activityAddContactBinding.etEmail.setText(email);
        activityAddContactBinding.etNumber.setText(number);

        File file = new File(image);
        Picasso.get().load(file).placeholder(R.drawable.ph).into(activityAddContactBinding.ivProfile);
    }


    private void showEmptyInput() {
        activityAddContactBinding.etName.setText("");
        activityAddContactBinding.etEmail.setText("");
        activityAddContactBinding.etNumber.setText("");
    }

    public void profileClick() {
        showPictureDialog();
    }

    public void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getResources().getString(R.string.txt_dialog_action));
        String[] pictureDialogItems = {
                getResources().getString(R.string.txt_dialog_photo),
                getResources().getString(R.string.txt_dialog_capture)};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), 1);
                                break;
                            case 1:
                                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent1, CAM_REQUEST);
                                break;
                        }
                    }
                });

        pictureDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    activityAddContactBinding.ivProfile.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAM_REQUEST) {
            bitmap = (Bitmap) data.getExtras().get("data");
            activityAddContactBinding.ivProfile.setImageBitmap(bitmap);
        }
        saveToInternalStorage(bitmap);

    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        // Create imageDir
        long mills = new Date().getTime();
        File mypath = new File(directory, mills + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.imgPath = String.valueOf(mypath);

        return directory.getAbsolutePath();
    }

}