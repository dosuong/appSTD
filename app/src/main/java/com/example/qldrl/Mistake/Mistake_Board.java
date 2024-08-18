package com.example.qldrl.Mistake;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Mistake_Board extends AppCompatActivity {
    public static final int REQUEST_CODE_UPDATE = 123;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclVClass;
    private Spinner spCategory;
    private Spinner spYear;
    private SearchView searchClass;
    private AdapterCategory adapterCategory;

    public static AdapterClassRom adapterClassRom;
    private List<ClassRom> classRomList = new ArrayList<>();
    private Account account;
    private TextView noData;

    List<Category> listY = new ArrayList<>();
    private boolean isSearchViewFocused = false, isBackPressed = false;
    private ImageView imgBackMistakeBoard;
    @Override
    public void onBackPressed() {
        // Kiểm tra xem SearchView có focus không
        if (isSearchViewFocused) {
            // Nếu có, ẩn bàn phím ảo
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchClass.getWindowToken(), 0);

            // Và bỏ focus khỏi SearchView
            searchClass.clearFocus();
            isSearchViewFocused = false;
        } else {
            // Nếu không, xử lý như bình thường

                // Nếu đã nhấn nút back 2 lần, thì mới gọi super.onBackPressed()
                if (isBackPressed) {
                    super.onBackPressed();
                } else {
                    isBackPressed = true;
                    finish();
                }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mistake_board);

        noData = findViewById(R.id.noData);
        searchClass = findViewById(R.id.searchClass);
        spCategory = findViewById(R.id.spClass);
        spYear = findViewById(R.id.spYear);
        recyclVClass = findViewById(R.id.recyclVClass);
        imgBackMistakeBoard = findViewById(R.id.imgBackMistakeBoard);

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
      //  Toast.makeText(this, account.getTkChucVu(), Toast.LENGTH_LONG).show();

        searchClass();



     //   Log.d("day la so luong",listY.size() + "");


//        sp(getListCategory(), spCategory);
//        sp(getListYear(), spYear);
        if (account.getTkChucVu().toLowerCase().equals(MainHome_Edited.gv) || account.getTkChucVu().toLowerCase().equals(MainHome_Edited.bcs)) {
            List<ClassRom> classRomList = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

           // Toast.makeText(this, account.getTkID(), Toast.LENGTH_LONG).show();
            if(account.getTkChucVu().toLowerCase().equals(MainHome_Edited.gv)) {
                db.collection("giaoVien").whereEqualTo("TK_id", account.getTkID())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                        String idLH = documentSnapshot.getString("LH_id");
                                        //    Toast.makeText(Mistake_Board.this, idLH, Toast.LENGTH_LONG).show();
                                        getAllClassRoomsTeacher(idLH);

                                    }
                                }
                            }
                        });
            }
            if(account.getTkChucVu().toLowerCase().equals(MainHome_Edited.bcs)) {
                db.collection("hocSinh").whereEqualTo("TK_id", account.getTkID())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                        String idLH = documentSnapshot.getString("LH_id");
                                        //    Toast.makeText(Mistake_Board.this, idLH, Toast.LENGTH_LONG).show();
                                        getAllClassRoomsTeacher(idLH);

                                    }
                                }
                            }
                        });
            }





        } else {
            getAllClassRooms();
        }


        recyclVClass.setLayoutManager(new GridLayoutManager(Mistake_Board.this, 1));

        //Set Spinner
        adapterCategory = new AdapterCategory(this, R.layout.layout_item_selected, getListCategory());
        spCategory.setAdapter(adapterCategory);
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = adapterCategory.getItem(position).getNameCategory().toString();
                String all = "Tất cả";
                int spaceIndex = selectedOption.indexOf(" ");
                String afterSpace = selectedOption.substring(spaceIndex + 1);
                Log.d(TAG, "onItemSelected: " + afterSpace);
                List<ClassRom> filteredData = new ArrayList<>();
                for (ClassRom item : classRomList) {
                    if (item.getLhTen().substring(0, 2).toLowerCase().contains(afterSpace.toLowerCase())
                            || selectedOption.equals(all)) {
                        filteredData.add(item);
                    }
                }
                adapterClassRom = new AdapterClassRom(filteredData, Mistake_Board.this, account); //truyen vao tuy tung list
                recyclVClass.setAdapter(adapterClassRom);
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

                         adapterCategory1[0] = new AdapterCategory(Mistake_Board.this, R.layout.layout_item_selected,nienKhoaList);
                         spYear.setAdapter(adapterCategory1[0]);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase", "Lỗi khi lấy dữ liệu từ collection 'nienKhoa'", e);
                    }
                });


        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = adapterCategory1[0].getItem(position).getNameCategory().toString();
                String all = "Năm học";
                List<ClassRom> filteredData = new ArrayList<>();
                for (ClassRom item : classRomList) {
                    if (item.getNkNienKhoa().toLowerCase().contains(selectedOption.toLowerCase())
                            || selectedOption.equals(all)) {
                        filteredData.add(item);
                    }
                }
                adapterClassRom = new AdapterClassRom(filteredData, Mistake_Board.this, account); //truyen vao tuy tung list
                recyclVClass.setAdapter(adapterClassRom);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




        imgBackMistakeBoard.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void updataRecyc(List<ClassRom> classRomLists) {
        classRomList.addAll(classRomLists);
      //  Toast.makeText(this, "size lop"+classRomLists.size(), Toast.LENGTH_SHORT).show();
        adapterClassRom = new AdapterClassRom(classRomList, Mistake_Board.this, account); //truyen vao tuy tung list
        recyclVClass.setAdapter(adapterClassRom);
        recyclVClass.setLayoutManager(new GridLayoutManager(Mistake_Board.this, 1));

    }

    private void searchClass() {
        searchClass.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapterClassRom.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterClassRom.getFilter().filter(newText);
                return false;
            }


        });

        searchClass.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            isSearchViewFocused = hasFocus;
            isBackPressed = false;
        });
    }

    private  void sp(List<Category> listCategory, Spinner spinner) {
        adapterCategory = new AdapterCategory(this, R.layout.layout_item_selected, listCategory);
        spinner.setAdapter(adapterCategory);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(Mistake_Board.this, adapterCategory.getItem(position).getNameCategory(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }
    private List<Category> getListCategory(){
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Tất cả"));
        categoryList.add(new Category("Lop 10"));
        categoryList.add(new Category("Lop 11"));
        categoryList.add(new Category("Lop 12"));
        return categoryList;
    }




    private void getAllClassRooms() {
        List<ClassRom> classRomList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference taiKhoanRef = db.collection("lop");

        taiKhoanRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {

                    String lhid = documentSnapshot.getString("LH_id");
                    String lhTen = documentSnapshot.getString("LH_TenLop");
                    String lhGVCN = documentSnapshot.getString("LH_GVCN");
                    String nkNienKhoan = documentSnapshot.getString("NK_NienKhoa");


                    ClassRom classRom = new ClassRom( lhid , lhTen, lhGVCN, nkNienKhoan);
                    classRomList.add(classRom);
                }
                if(classRomList.size() == 0) {
                    noData.setVisibility(View.VISIBLE);
                }
                updataRecyc(classRomList);
               // Log.d("helllo" ,classRomList.size() + "");
//                adapterClassRom = new AdapterClassRom(classRomList, Mistake_Board.this, account); //truyen vao tuy tung list
//                recyclVClass.setAdapter(adapterClassRom);
//
//                recyclVClass.setLayoutManager(new GridLayoutManager(Mistake_Board.this, 1));
            } else {
                //Toast.makeText(getApplicationContext(), "Error retrieving accounts", Toast.LENGTH_SHORT).show();
            }
        });
      //  Log.d("helllo" ,classRomList.size() + "");


    }
    private void getAllClassRoomsTeacher(String idlh) {
        List<ClassRom> classRomList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
       db.collection("lop").whereEqualTo("LH_id", idlh)
               .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {

                    String lhid = documentSnapshot.getString("LH_id");
                    String lhTen = documentSnapshot.getString("LH_TenLop");
                    String lhGVCN = documentSnapshot.getString("LH_GVCN");
                    String nkNienKhoan = documentSnapshot.getString("NK_NienKhoa");


                    ClassRom classRom = new ClassRom( lhid , lhTen, lhGVCN, nkNienKhoan);
                    classRomList.add(classRom);
                }
                updataRecyc(classRomList);
                // Log.d("helllo" ,classRomList.size() + "");
//                adapterClassRom = new AdapterClassRom(classRomList, Mistake_Board.this, account); //truyen vao tuy tung list
//                recyclVClass.setAdapter(adapterClassRom);
//
//                recyclVClass.setLayoutManager(new GridLayoutManager(Mistake_Board.this, 1));
            } else {
                //Toast.makeText(getApplicationContext(), "Error retrieving accounts", Toast.LENGTH_SHORT).show();
            }
        });
        //  Log.d("helllo" ,classRomList.size() + "");


    }


}