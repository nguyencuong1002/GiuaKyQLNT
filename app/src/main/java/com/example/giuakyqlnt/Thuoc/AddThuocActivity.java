package com.example.giuakyqlnt.Thuoc;
import static com.example.giuakyqlnt.Thuoc.ActivityThuoc.MATHUOC_FIELD;
import static com.example.giuakyqlnt.Thuoc.ActivityThuoc.TENTHUOC_FIELD;
import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.giuakyqlnt.ChiTietBanLe.ActivityChiTietBanLe;
import com.example.giuakyqlnt.NhaThuoc.ActivityNhaThuoc;
import com.example.giuakyqlnt.NhaThuoc.AddNhaThuocActivity;
import com.example.giuakyqlnt.R;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class AddThuocActivity extends AppCompatActivity {
    ActivityThuoc Thuoc;
    EditText txtMATHUOC, txtTENTHUOC, txtDVT, txtDONGIA;
    Button btnAdd, btnCancel, btnAddImg;
    ImageView ivBack, imgTHUOC;
    boolean isAllFieldsChecked = false;
    final int REQUEST_CODE_GALLERY = 999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_thuoc);
        mapping();
        setEvent();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void add(View view){
        String MATHUOC = txtMATHUOC.getText().toString();
        String TENTHUOC = txtTENTHUOC.getText().toString();
        String DVT = txtDVT.getText().toString();
        //check data
        isAllFieldsChecked = CheckAllFields();
        if (isAllFieldsChecked){
            //Add dữ liệu
            ContentValues values = new ContentValues();
            values.put(MATHUOC_FIELD, MATHUOC);
            values.put(ActivityThuoc.TENTHUOC_FIELD, TENTHUOC);
            values.put(ActivityThuoc.DVT_FIELD, DVT);
            Float DONGIA = Float.parseFloat(txtDONGIA.getText().toString());
            values.put(ActivityThuoc.DONGIA_FIELD, DONGIA);
            byte[] IMGTHUOC = imageViewToByte(imgTHUOC);
            values.put(ActivityThuoc.IMGTHUOC_FIELD, IMGTHUOC);
            ActivityThuoc.myDatabase.insert(ActivityThuoc.TABLE_NAME, null,values);
            startActivity(new Intent(AddThuocActivity.this, ActivityThuoc.class));
            //gửi mail thông báo
            String host = "smtp.gmail.com";
            final String username = "sytruong61@gmail.com";
            final String password = "mywghdzgzuznmnbv";
            String messageToSend = "Có một loại thuốc vừa được thêm mới. Mã thuốc: " + MATHUOC + ", tên thuốc: " + TENTHUOC ;
            Properties props = new Properties();
            props.setProperty("mail.transport.protocol", "smtp");
            props.setProperty("mail.host", host);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.quitwait", "false");
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });
            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("sytruong18112000@gmail.com"));
                message.setSubject("Thông Báo");
                message.setText(messageToSend);
                Transport.send(message);
                Toast.makeText(getApplicationContext (), "Email send successfully", Toast. LENGTH_LONG). show();
            }catch (MessagingException e){
                Toast.makeText(getApplicationContext (), e.getMessage().toString(), Toast. LENGTH_LONG). show();
            }
        }
    }

    public void addImg(View view){
       ActivityCompat.requestPermissions(
                AddThuocActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLERY
        );
    }
    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgTHUOC.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private boolean CheckAllFields() {
        if (txtMATHUOC.length() == 0) {
            txtMATHUOC.setError("Vui lòng không để trống!");
            return false;
        } else if (!txtMATHUOC.getText().toString().matches("[a-zA-Z0-9]+")) {
            txtMATHUOC.setError("Vui lòng chỉ nhập kí tự chữ hoặc số!");
            return false;
        }else if(Thuoc.myDatabase.checkExistID(Thuoc.TABLE_NAME, MATHUOC_FIELD, txtMATHUOC.getText().toString())){
            txtMATHUOC.setError("Mã nhà thuốc đã tồn tại!");
            return false;
        }
        if (txtTENTHUOC.length() == 0) {
            txtTENTHUOC.setError("Vui lòng không để trống!");
            return false;
        }
        if (txtDVT.length() == 0) {
            txtDVT.setError("Vui lòng không để trống!");
            return false;
        }
        if (txtDONGIA.length() == 0) {
            txtDONGIA.setError("Vui lòng không để trống!");
            return false;
        }
        if(imgTHUOC.getDrawable()==null) {
            Toast.makeText(getApplicationContext(), "Vui lòng chọn ảnh của Thuốc", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void cancle(View view){
        startActivity(new Intent(AddThuocActivity.this, ActivityThuoc.class));
    }

    public void setEvent(){
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddThuocActivity.this, ActivityThuoc.class));
            }
        });
    }

    private void mapping(){
        txtMATHUOC = findViewById(R.id.txtMATHUOC);
        txtTENTHUOC = findViewById(R.id.txtTENTHUOC);
        txtDVT = findViewById(R.id.txtDVT);
        txtDONGIA = findViewById(R.id.txtDONGIA);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);
        ivBack = findViewById(R.id.ivBack);
        imgTHUOC = findViewById(R.id.imgTHUOC);
        btnAddImg = findViewById(R.id.btnAddImg);
    }
}
