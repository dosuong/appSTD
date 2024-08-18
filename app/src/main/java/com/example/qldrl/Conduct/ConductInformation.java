package com.example.qldrl.Conduct;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.Account;
import com.example.qldrl.General.AdapterCategory;
import com.example.qldrl.General.Category;
import com.example.qldrl.Homes.MainHome_Edited;
import com.example.qldrl.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ConductInformation extends AppCompatActivity {
    private String idStudent, idMistake, idAccount, idMistakeType, semester ,timeMistake; //Luot Vi Pham
    private List<ConductInformation> conductInformationList = new ArrayList<>();;
    private Account account;
    TextView txtTrainingPointStudent;
    TextView txtConductStudent;
    TextView txtSemester;
    RecyclerView recyclerView;
    public ConductInformation() {
    }

    public ConductInformation(String idStudent, String idMistake, String idAccount, String idMistakeType, String semester, String timeMistake) {
        this.idStudent = idStudent;
        this.idMistake = idMistake;
        this.idAccount = idAccount;
        this.idMistakeType = idMistakeType;
        this.timeMistake = timeMistake;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conduct_information);

        Intent intent = getIntent();// Ban giam hiệu
        String StudentName = intent.getStringExtra("StudentName");
        String StudentId = intent.getStringExtra("StudentId");
        String StudentTrainningPoint = intent.getStringExtra("StudentTrainningPoint");
        String StudentConduct = intent.getStringExtra("StudentConduct");

        account = (Account) intent.getSerializableExtra("account");
        String se = intent.getStringExtra("semester");
        Log.d(TAG, "onCreate: HK "+semester);

        Spinner spinnerSemester = findViewById(R.id.spinnerSemester);
        spinnerSemester.setVisibility(View.GONE);
        TextView txtPageTitle = findViewById(R.id.pageTitle);
        TextView txtNameStudent = findViewById(R.id.txtNameStudent);
        txtSemester = findViewById(R.id.txtSemester);
        txtTrainingPointStudent = findViewById(R.id.txtTrainingPointStudent);
        txtConductStudent = findViewById(R.id.txtConductStudent);
        recyclerView = findViewById(R.id.recyclerViewMistake);

        if(account.getTkChucVu().toLowerCase().trim().equals(MainHome_Edited.bcs)
                || account.getTkChucVu().toLowerCase().trim().equals(MainHome_Edited.hs)){

            spinnerSemester.setVisibility(View.VISIBLE);
            getIdAndConduct("Học Kỳ 1");

            txtPageTitle.setText(account.getTkHoTen());
            txtNameStudent.setText(account.getTkHoTen());

            AdapterCategory adapterSemester = new AdapterCategory(this, R.layout.layout_item_selected, getSemesterCategory());
            spinnerSemester.setAdapter(adapterSemester);
            spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedOption = adapterSemester.getItem(position).getNameCategory().toString();
                    getIdAndConduct(selectedOption);


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }else {
            txtPageTitle.setText(StudentName);
            txtNameStudent.setText(StudentName);
            txtSemester.setText(se);
            txtTrainingPointStudent.setText(StudentTrainningPoint);
            txtConductStudent.setText(StudentConduct);
            getDataMistake(StudentId, se);
        }


        ImageView btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> onBackPressed());
    }
    public void getIdAndConduct(String hk){
       FirebaseFirestore db = FirebaseFirestore.getInstance();
       db.collection("hocSinh").whereEqualTo("TK_id",account.getTkID())
               .get().addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                           String id = queryDocumentSnapshot.getString("HS_id");

                           Query query = db.collection("hanhKiem").whereEqualTo("HS_id", id).whereEqualTo("HK_HocKy",hk);
                           query.get().addOnCompleteListener(task1 -> {
                               if (task1.isSuccessful()) {
                                   for (QueryDocumentSnapshot queryDocumentSnapshot1 : task1.getResult()) {
                                       String idConduct = (String) queryDocumentSnapshot1.getString("HKM_id");
                                       String idStudent = (String) queryDocumentSnapshot1.getString("HS_id");
                                       String trainingPoint = (String) queryDocumentSnapshot1.getString("HKM_DiemRenLuyen");
                                       String conduct = (String) queryDocumentSnapshot1.getString("HKM_HanhKiem");
                                       String term = (String) queryDocumentSnapshot1.getString("HK_HocKy");
                                       txtSemester.setText(hk);
                                       txtTrainingPointStudent.setText(trainingPoint);
                                       txtConductStudent.setText(conduct);

                                       getDataMistake(idStudent,hk);
                                   }

                               }
                           });
                       }
                   }
               });
    }
    public void getDataMistake(String StudentId, String semester){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("luotViPham").whereEqualTo("HS_id",StudentId).whereEqualTo("HK_HocKy",semester)
                .get().addOnCompleteListener(task -> {
                    List<ConductInformation> list = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){

                        String idS = (String) queryDocumentSnapshot.getString("HS_id");
                        String idM = (String) queryDocumentSnapshot.getString("VP_id");
                        String idA = (String) queryDocumentSnapshot.getString("TK_id");
                        String idMT = (String) queryDocumentSnapshot.getString("LVPM_id");
                        String sem = (String) queryDocumentSnapshot.getString("HK_HocKy");
                        String time = (String) queryDocumentSnapshot.getString("LTVP_ThoiGian");
                        ConductInformation data = new ConductInformation(idS, idM, idA, idMT, sem, time);
                        list.add(data);
                    }
                    updateData(list);
                });
    }
    public void updateData(List<ConductInformation> List){
        conductInformationList.clear();
        conductInformationList.addAll(List);
        AdapterConductInformation adapterConductInformation =
                new AdapterConductInformation(ConductInformation.this, conductInformationList, account);
        recyclerView.setAdapter(adapterConductInformation);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private List<Category> getSemesterCategory(){
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Học kỳ 1"));
        categoryList.add(new Category("Học kỳ 2"));
        return categoryList;
    }
    public String getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }

    public String getIdMistake() {
        return idMistake;
    }

    public void setIdMistake(String idMistake) {
        this.idMistake = idMistake;
    }

    public String getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(String idAccount) {
        this.idAccount = idAccount;
    }

    public String getIdMistakeType() {
        return idMistakeType;
    }

    public void setIdMistakeType(String idMistakeType) {
        this.idMistakeType = idMistakeType;
    }

    public String getTimeMistake() {
        return timeMistake;
    }

    public void setTimeMistake(String timeMistake) {
        this.timeMistake = timeMistake;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}