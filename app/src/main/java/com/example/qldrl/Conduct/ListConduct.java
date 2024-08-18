package com.example.qldrl.Conduct;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.Class.ListClass;
import com.example.qldrl.General.Account;
import com.example.qldrl.General.AdapterCategory;
import com.example.qldrl.General.Category;
import com.example.qldrl.Homes.MainHome_Edited;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListConduct extends AppCompatActivity {
    private String className, quantity, teacherName, conduct;
    private Account account;
    private AdapterListConduct adapterListConduct;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<ListClass> listClasses = new ArrayList<>();
    private String semester = "Học kỳ 1";

    public ListConduct() {
    }

    public ListConduct(String className, String quantity, String teacherName, String conduct) {
        this.className = className;
        this.quantity = quantity;
        this.teacherName = teacherName;
        this.conduct = conduct;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_conduct);

        SearchView searchView = findViewById(R.id.searchView);
        Spinner spinnerGrade = findViewById(R.id.spinnerGrade);
        Spinner spinnerSemester = findViewById(R.id.spinnerSemester);
        Spinner spinnerYear = findViewById(R.id.spinnerYear);
        RecyclerView recyclerView = findViewById(R.id.recyclViewConduct);


        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        if(account.getTkChucVu().equals("Giáo viên")) {
            db.collection("giaoVien").whereEqualTo("TK_id", account.getTkID())
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = (String) document.getString("LH_id");

                                if (account.getTkChucVu().toLowerCase().trim().equals(MainHome_Edited.gv)) {
                                    getDataClassForTeacher(id);
                                }
                            }
                        }
                    });
        }else {
            getDataClass();
        }



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Xử lý khi người dùng nhấn "Search"
                List<ListClass> filteredData = new ArrayList<>();
                for (ListClass item : listClasses) {
                    if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                        filteredData.add(item);
                    }
                }
                adapterListConduct = new AdapterListConduct(filteredData,ListConduct.this, account,semester);
                recyclerView.setAdapter(adapterListConduct);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Xử lý khi người dùng thay đổi từ khóa tìm kiếm
                List<ListClass> filteredData = new ArrayList<>();
                for (ListClass item : listClasses) {
                    if (item.getName().toLowerCase().contains(newText.toLowerCase())) {
                        filteredData.add(item);
                    }
                }
                adapterListConduct = new AdapterListConduct(filteredData,ListConduct.this, account, semester);
                recyclerView.setAdapter(adapterListConduct);
                return true;
            }

        });

        AdapterCategory adapterGrade = new AdapterCategory(this, R.layout.layout_item_selected, getGradeCategory());
        spinnerGrade.setAdapter(adapterGrade);
        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = adapterGrade.getItem(position).getNameCategory().toString();
                String all = "Khối";
                int spaceIndex = selectedOption.indexOf(" ");
                String afterSpace = selectedOption.substring(spaceIndex + 1);
                Log.d(TAG, "onItemSelected: "+afterSpace);
                List<ListClass> filteredData = new ArrayList<>();
                for (ListClass item : listClasses) {
                    if (item.getName().substring(0,2).toLowerCase().contains(afterSpace.toLowerCase())
                            || selectedOption.equals(all)) {
                        filteredData.add(item);
                    }
                }
                adapterListConduct = new AdapterListConduct(filteredData,ListConduct.this, account, semester);
                recyclerView.setAdapter(adapterListConduct);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý trường hợp không có lựa chọn nào được chọn
            }
        });
        AdapterCategory adapterSemester = new AdapterCategory(this, R.layout.layout_item_selected, getSemesterCategory());
        spinnerSemester.setAdapter(adapterSemester);
        spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  String selectedOption = adapterSemester.getItem(position).getNameCategory().toString();
                  List<ListClass> filteredData = new ArrayList<>();
                  semester = selectedOption;
                  for (ListClass item : listClasses) {
                          filteredData.add(item);

                  }
                  adapterListConduct = new AdapterListConduct(filteredData, ListConduct.this, account, semester);
                  recyclerView.setAdapter(adapterListConduct);
              }

              @Override
              public void onNothingSelected(AdapterView<?> parent) {

              }
          });
        final AdapterCategory[] adapterCategory1 = new AdapterCategory[1];
        List<Category> nienKhoaList = new ArrayList<>();
        nienKhoaList.add(new Category("Năm học"));
        CollectionReference nienKhoaRef = db.collection("nienKhoa");
        nienKhoaRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String nienKhoa = document.getString("NK_NienKhoa");
                            nienKhoaList.add(new Category(nienKhoa));
                        }
                        adapterCategory1[0] = new AdapterCategory(ListConduct.this, R.layout.layout_item_selected,nienKhoaList);
                        spinnerYear.setAdapter(adapterCategory1[0]);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase", "Lỗi khi lấy dữ liệu từ collection 'nienKhoa'", e);
                    }
                });
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = adapterCategory1[0].getItem(position).getNameCategory().toString();
                String all = "Năm học";
                List<ListClass> filteredData = new ArrayList<>();
                for (ListClass item : listClasses) {
                    if (item.getYear().toLowerCase().contains(selectedOption.toLowerCase())
                            || selectedOption.equals(all)) {
                        filteredData.add(item);
                    }
                }
                adapterListConduct = new AdapterListConduct(filteredData, ListConduct.this, account, semester);
                recyclerView.setAdapter(adapterListConduct);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý trường hợp không có lựa chọn nào được chọn
            }
        });



        ImageView btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> onBackPressed());
    }
    public void setList(List<ListClass> List){
        listClasses.addAll(List);
        adapterListConduct = new AdapterListConduct(List,ListConduct.this, account, semester);
        RecyclerView recyclerView = findViewById(R.id.recyclViewConduct);
        recyclerView.setAdapter(adapterListConduct);
    }
    public void getDataClass(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("lop").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
//                    classroomList = new ArrayList<>();
                    List<ListClass> List = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = (String) document.getString("LH_id");
                        String classroomName = (String) document.getString("LH_TenLop");
                        String teacherName = (String) document.getString("LH_GVCN");
                        String year = (String) document.getString("NK_NienKhoa");

                        ListClass data = new ListClass(id, classroomName, teacherName, year);
//                        Classlist data = document.toObject(Classlist.class);
                        List.add(data);

                    }
                    Log.d(TAG, "Success: " + List.size());
                    setList(List);
                }
            }
        });
    }
    public void getDataClassForTeacher(String idLH){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("lop").whereEqualTo("LH_id",idLH).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
//                    classroomList = new ArrayList<>();
                List<ListClass> List = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = (String) document.getString("LH_id");
                    String classroomName = (String) document.getString("LH_TenLop");
                    String teacherName = (String) document.getString("LH_GVCN");
                    String year = (String) document.getString("NK_NienKhoa");

                    ListClass data = new ListClass(id, classroomName, teacherName, year);
//                        Classlist data = document.toObject(Classlist.class);
                    List.add(data);

                }
                Log.d(TAG, "Success: " + List.size());
                setList(List);
            }
        });
    }
    private List<Category> getGradeCategory(){
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Khối"));
        categoryList.add(new Category("Khối 10"));
        categoryList.add(new Category("Khối 11"));
        categoryList.add(new Category("Khối 12"));
        return categoryList;
    }
    private List<Category> getSemesterCategory(){
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Học kỳ 1"));
        categoryList.add(new Category("Học kỳ 2"));
        return categoryList;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getConduct() {
        return conduct;
    }

    public void setConduct(String conduct) {
        this.conduct = conduct;
    }


}