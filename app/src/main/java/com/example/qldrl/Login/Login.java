package com.example.qldrl.Login;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.qldrl.General.Account;
import com.example.qldrl.Homes.MainHome_Edited;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText editNameCount, editPasswd;
    private TextView txtForgetPass, txtErrorPass, txtErrorNameAccount;
    private ImageView  imgTest, imgErroNameAccount, imgErroPass;
    private CheckBox chckLogin;
    private Button btnLogin;
    private ConstraintLayout layoutTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Tham chieu
        imgTest = findViewById(R.id.imgTEST);
        layoutTest = findViewById(R.id.layoutTest);
        editPasswd = findViewById(R.id.editPassCount);
        editNameCount = findViewById(R.id.editNameCount);
        btnLogin = findViewById(R.id.imgBtnLogin);
        chckLogin = findViewById(R.id.chckLogin);
//        imgErroPass = findViewById(R.id.imgErroPass);
//        imgErroNameAccount = findViewById(R.id.imgErroNameAccount);
//        txtErrorPass = findViewById(R.id.txtErrorPass);
//        txtErrorNameAccount = findViewById(R.id.txtErrorNameAcount);


        checkRememberMe();
        // Dang nhap
        logIn();
    }

    private void checkRememberMe() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);

        if (rememberMe) {
//            String username = sharedPreferences.getString("username", "");
//            String password = sharedPreferences.getString("password", "");
//            editNameCount.setText(username);
//            editPasswd.setText(password);
//            chckLogin.setChecked(true);
//            //checkLogin1(username, password);
            Account account = getAccount();
//            Toast.makeText(this, "this is acid" + account.getTkChucVu() , Toast.LENGTH_LONG).show();
//            Account account = DataLocalManager.getAccount();
            Intent intent = new Intent(this, MainHome_Edited.class);
            intent.putExtra("account", account);
            startActivity(intent);
            finish();
        }
    }

    private void checkLogin1(String tenTK, String matKhau) {
        CollectionReference taiKhoanRef = db.collection("taiKhoan");

        Query query = taiKhoanRef.whereEqualTo("TK_TenTaiKhoan", tenTK);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        String id = documentSnapshot.getString("TK_id");
                        String tenTaiKhoan = documentSnapshot.getString("TK_TenTaiKhoan");
                        String matKhauDB = documentSnapshot.getString("TK_MatKhau");
                        String hoTen = documentSnapshot.getString("TK_HoTen");
                        String chucVu = documentSnapshot.getString("TK_ChucVu");
                        String ngaySinh = documentSnapshot.getString("TK_NgaySinh");

                        if (matKhauDB.equals(matKhau)) {
                            saveLoginInfo(tenTaiKhoan, matKhauDB);
                            Account account = new Account(id, tenTaiKhoan, ngaySinh, matKhauDB, hoTen, chucVu);
                            updateToken(id);
                            Intent intent = new Intent(Login.this, MainHome_Edited.class);
                            intent.putExtra("account", account);
                            setAccount(account);
                            startActivity(intent);
                        } else {
                            editPasswd.setError("Mật khẩu không đúng!");
                        }
                    } else {
                            editNameCount.setError("Tên tài khoản không chính xác!");
                    }
                } else {
                    Toast.makeText(Login.this, "Lỗi đăng nhập", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void logIn() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenTK = editNameCount.getText().toString();
                String matKhau = editPasswd.getText().toString();
                checkLogin1(tenTK, matKhau);
            }
        });
    }


    private void saveLoginInfo(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("username", username);
        editor.putString("password", password);
        editor.putBoolean("rememberMe", chckLogin.isChecked());
        editor.apply();
    }

    public void setAccount(Account account) {
        Gson gson = new Gson();
        String strJsonAcoount = gson.toJson(account);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("Account", strJsonAcoount);
        editor.apply();
    }

    public Account getAccount()  {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        String strJsonAccount = sharedPreferences.getString("Account","");
        Gson gson = new Gson();
        Account accountL = gson.fromJson(strJsonAccount, Account.class);
        return  accountL;
    }

    private void updateToken(String id){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    Map<String, Object> newDvice = new HashMap<>();
                    newDvice.put("TK_id",id);
                    newDvice.put("TB_id", deviceId);
                    newDvice.put("TB_Token", token);

                    FirebaseFirestore.getInstance()
                            .collection("thietBi")
                            .document(id)
                            .set(newDvice);
                });
    }
}
