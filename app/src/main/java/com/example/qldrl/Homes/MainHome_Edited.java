package com.example.qldrl.Homes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.qldrl.Account.account_main;
import com.example.qldrl.Class.ListClass;
import com.example.qldrl.Conduct.ConductInformation;
import com.example.qldrl.Conduct.ListConduct;
import com.example.qldrl.General.Account;
import com.example.qldrl.Mistake.Mistake_Board;
import com.example.qldrl.R;
import com.example.qldrl.Report.Report;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainHome_Edited extends AppCompatActivity {
   public static BottomNavigationView bottomNavigationView;
    private Account account;
    private CardView cardReport, cardUpdate, cardClass, cardPoint;
    private TextView txtNameAcced, txtPositioned;
    private ImageView notification;
    public static String gv = "giáo viên";
    public static String bcs = "ban cán sự";
    public static String hs = "học sinh";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_edited);

        bottomNavigationView = findViewById(R.id.botNavi);
        txtPositioned = findViewById(R.id.txtPostioned);
        txtNameAcced = findViewById(R.id.txtNameAcced);
        cardClass = findViewById(R.id.cardClass);
        cardPoint = findViewById(R.id.cardPoint);
        cardReport = findViewById(R.id.cardReport);
        cardUpdate = findViewById(R.id.cardUpdate);
        notification = findViewById(R.id.notification);



        getIntentData();
        // Chuyen qua activity MainHome
        intentActivity();

        if(account.getTkChucVu().toLowerCase().equals("học sinh")) {
            cardReport.setVisibility(View.GONE);
            cardUpdate.setVisibility(View.GONE);
            cardClass.setVisibility(View.GONE);
        } else if (account.getTkChucVu().toLowerCase().equals("ban cán sự")) {
            cardReport.setVisibility(View.GONE);
            cardClass.setVisibility(View.GONE);
        }


            cardReport.setOnClickListener(v -> {
            Intent intent = new Intent(this, Report.class);
            intent.putExtra("account", account);
            startActivity(intent);
        });

        cardPoint.setOnClickListener(v -> {
            if(account.getTkChucVu().toLowerCase().trim().equals(gv)){
                Intent intent = new Intent(MainHome_Edited.this, ListConduct.class);
                intent.putExtra("account", account);
                startActivity(intent);
            } else if (account.getTkChucVu().toLowerCase().trim().equals(bcs)
                    || account.getTkChucVu().toLowerCase().trim().equals(hs)) {
                Intent intent = new Intent(MainHome_Edited.this, ConductInformation.class);
                intent.putExtra("account", account);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainHome_Edited.this, ListConduct.class);
                intent.putExtra("account", account);

                startActivity(intent);
            }

        });

        cardClass.setOnClickListener(v -> {
            if(account.getTkChucVu().toLowerCase().trim().equals(gv)){
                Intent intent = new Intent(MainHome_Edited.this, ListClass.class);
                intent.putExtra("account", account);
                startActivity(intent);
            } else if (account.getTkChucVu().toLowerCase().trim().equals(bcs)
                    || account.getTkChucVu().toLowerCase().trim().equals(hs)) {
                Toast.makeText(this, "Tài khoản không có quyền truy cập lớp học", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainHome_Edited.this, ListClass.class);
                intent.putExtra("account", account);

                startActivity(intent);
            }
        });




        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if(id == R.id.action_home) {

            }else if(id == R.id.action_help) {

            }else {
                Intent intent1 = new Intent(MainHome_Edited.this, account_main.class);
                intent1.putExtra("account", account);
                startActivity(intent1);
            }
            return true;
        });

//        notification.setOnClickListener(v -> {
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            db.collection("thietBi").whereEqualTo("TK_id",account.getTkID()).get()
//                    .addOnSuccessListener(queryDocumentSnapshots -> {
//                        for( DocumentSnapshot doc : queryDocumentSnapshots){
//                            String token = doc.getString("TB_Token");
//                            Notification notification = new Notification(token, "Một vi phạm đã được cập nhật","!!!!", MainHome_Edited.this);
//                            notification.sendNotification();
//                        }
//                    });
//        });

    }

    private void intentActivity() {
        cardUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(account.getTkChucVu().toLowerCase().trim().equals(gv) || account.getTkChucVu().toLowerCase().trim().equals(bcs)){
                    Intent intent = new Intent(MainHome_Edited.this, Mistake_Board.class);
                    intent.putExtra("account", account);
                    startActivity(intent);
                } else if (account.getTkChucVu().toLowerCase().trim().equals(hs)) {
                    Toast.makeText(MainHome_Edited.this, "Tài khoản không có quyền truy cập lớp học", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainHome_Edited.this, Mistake_Board.class);
                    intent.putExtra("account", account);
                    startActivity(intent);
                }


//
//                Intent intent1 = new Intent(MainHome.this, Mistake_Board.class);
//                intent1.putExtra("account", account);
//                startActivity(intent1);
            }
        });
    }

    private void getIntentData() {

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");

        txtNameAcced.setText(account.getTkHoTen());
        txtPositioned.setText(account.getTkChucVu());
    }


}