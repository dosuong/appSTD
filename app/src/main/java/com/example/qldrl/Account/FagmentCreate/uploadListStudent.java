package com.example.qldrl.Account.FagmentCreate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.qldrl.Account.CreateManyAccountCallback;
import com.example.qldrl.Account.listAcc;
import com.example.qldrl.General.Account;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link uploadListStudent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class uploadListStudent extends Fragment {

    private CreateManyAccountCallback callback;

    private static final int REQUEST_CODE = 123;
    private TextView txtNameStudentFile;
    private Button btnExitStudentAcc,btnUploadStudent, btnChoiceStudetFile;
    private View myUploadListStudent;
    private ActivityResultLauncher<Intent> fileChooserLauncher;
    Uri uri;
    private Dialog currentDialog;

    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public uploadListStudent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment uploadListStudent.
     */
    // TODO: Rename and change types and number of parameters
    public static uploadListStudent newInstance(String param1, String param2) {
        uploadListStudent fragment = new uploadListStudent();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof
                CreateManyAccountCallback) {
            callback = (CreateManyAccountCallback) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement CreateManyAccountCallback");
        }
    }
    private void onCreateAccount(List<Account> newAccounts) {
        // Xử lý tại Fragment khi tài khoản mới được tạo
        // Ví dụ: cập nhật dữ liệu, hiển thị thông báo, v.v.

        // Sau đó gọi callback để thông báo cho Activity
        callback.onManyAccountCreated(newAccounts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        myUploadListStudent =  inflater.inflate(R.layout.fragment_upload_list_student, container, false);

        txtNameStudentFile = myUploadListStudent.findViewById(R.id.txtNameStudentFile);
        btnUploadStudent = myUploadListStudent.findViewById(R.id.btnUploadStudent);
        btnChoiceStudetFile = myUploadListStudent.findViewById(R.id.btnChoiceStudetFile);
        btnExitStudentAcc = myUploadListStudent.findViewById(R.id.btnExitStudentAcc);



        btnChoiceStudetFile.setOnClickListener(v -> {
            openFileChooser();
        });
        initFileChooserLauncher();

        btnUploadStudent.setOnClickListener(v -> {
            // Hiển thị dialog trước
            openDinalogLoading(Gravity.CENTER);

            // Thực hiện tải file lên Firestore
            new Thread(() -> {
                try {
                    uploadExcelDataToFirestore1(uri);
                    // Đóng dialog và hiển thị thông báo thành công từ luồng chính
                    requireActivity().runOnUiThread(() -> {
                        currentDialog.dismiss();

                        showToastMessage("Tải file lên thành công!");
                        listAcc.currentDialog.dismiss();
                    });
                } catch (IOException e) {
                    // Đóng dialog và hiển thị thông báo lỗi từ luồng chính
                    requireActivity().runOnUiThread(() -> {
                        currentDialog.dismiss();

                        showToastMessage("Có lỗi xảy ra, vui lòng thử lại!");
                    });
                    throw new RuntimeException(e);
                }
            }).start();



        });

        btnExitStudentAcc.setOnClickListener(v -> {
            if (listAcc.currentDialog != null) {
                listAcc.currentDialog.dismiss();
            }
        });

       return  myUploadListStudent;
    }


    private void showToastMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }



    private void openDinalogLoading(int gravity) {
        currentDialog = new Dialog(requireContext());
        currentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        currentDialog.setContentView(R.layout.layout_dialog_loading);

        Window window = currentDialog.getWindow();
        if(window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);

        TextView txtMessage = currentDialog.findViewById(R.id.txtMessage);


        currentDialog.setCancelable(true);


        currentDialog.show();
    }


    private void initFileChooserLauncher() {
        fileChooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();

                        String fileName = getFileName(fileUri);

                        txtNameStudentFile.setText( fileName);

                   //     Toast.makeText(getContext(), getFilePath(getContext(), fileUri), Toast.LENGTH_LONG).show();

                        uri = fileUri;
                    }
                }
        );
    }


    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        fileChooserLauncher.launch(intent);
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    private void uploadExcelDataToFirestore(Uri fileUri) throws IOException {

       // Log.d("hhhhhhh", "jhhhhhhhhhhh");

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri)) {
            if (inputStream != null) {
                Workbook workbook = new XSSFWorkbook(inputStream);
                ((XSSFWorkbook) workbook).setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Sheet sheet = workbook.getSheetAt(0);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
               // sheet.getLastRowNum()
                for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Bắt đầu từ hàng thứ hai (giả sử hàng đầu tiên là tiêu đề)
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        String hsId = getCellValueAsString(row.getCell(0));
                        String hsHoTen = getCellValueAsString(row.getCell(1));
                        String hsNgaySinh = getCellValueAsString(row.getCell(2));
                        String hsGioiTinh = getCellValueAsString(row.getCell(3));
                        String hsTenLop = getCellValueAsString(row.getCell(4));
                        String hsChucVu = getCellValueAsString(row.getCell(5));
                        String hsNienKhoa = getCellValueAsString(row.getCell(6));
                        String hsMatKhau = getCellValueAsString(row.getCell(7));

                        // Lưu dữ liệu học sinh vào Firestore
                        Map<String, Object> hsData = new HashMap<>();
                        hsData.put("HS_id", hsId);
                        hsData.put("HS_HoTen", hsHoTen);
                        hsData.put("HS_NgaySinh", hsNgaySinh);
                        hsData.put("HS_GioiTinh", hsGioiTinh);
                        hsData.put("HS_ChucVu", hsChucVu);
                        hsData.put("LH_id", hsTenLop+hsNienKhoa);
                        hsData.put("TK_id", hsId);

                     //    Lưu dữ liệu tài khoản vào Firestore
                        Map<String, Object> tkData = new HashMap<>();
                        tkData.put("TK_id", hsId);
                        tkData.put("TK_HoTen", hsHoTen);
                        tkData.put("TK_TenTaiKhoan", hsId);
                        tkData.put("TK_NgaySinh", hsNgaySinh);
                        tkData.put("TK_ChucVu", hsChucVu);
                        tkData.put("TK_MatKhau", hsMatKhau);



                        Map<String, Object> HK1data = new HashMap<>();
                        HK1data.put("HKM_DiemRenLuyen", "100");
                        HK1data.put("HKM_id", "HKI"+hsId);
                        HK1data.put("HKM_HanhKiem", "Tốt");
                        HK1data.put("HK_HocKy", "Học kỳ 1");
                        HK1data.put("HS_id", hsId);

                        Map<String, Object> HK2data = new HashMap<>();
                        HK2data.put("HKM_DiemRenLuyen", "100");
                        HK2data.put("HKM_id", "HKII"+hsId);
                        HK2data.put("HKM_HanhKiem", "Tốt");
                        HK2data.put("HK_HocKy", "Học kỳ 2");
                        HK2data.put("HS_id", hsId);

                        // Lưu dữ liệu học sinh vào collection "hocSinh"
                        DocumentReference hsRef = db.collection("hocSinh").document(hsId);
                        Task<Void> hsWriteResult = hsRef.set(hsData);
                        hsWriteResult.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            //    Toast.makeText(getContext(),"ess",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý lỗi khi lưu dữ liệu học sinh
                               // Toast.makeText(getContext(),"effffss",Toast.LENGTH_LONG).show();
                            }
                        });




                        DocumentReference tkRef = db.collection("taiKhoan").document(hsId);
                        Task<Void> tkWriteResult = tkRef.set(tkData);
                        tkWriteResult.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Account account = new Account(hsId,hsId,hsNgaySinh, hsMatKhau, hsHoTen, hsChucVu);
                                List<Account> accountList = new ArrayList<>();
                                accountList.add(account);
                                onCreateAccount(accountList);
                                // Lưu dữ liệu tài khoản thành công
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý lỗi khi lưu dữ liệu tài khoản
                            }
                        });



                        DocumentReference hkiem1Ref = db.collection("hanhKiem").document("HKI"+hsId);
                        Task<Void> hKiemWriteResult = hkiem1Ref.set(HK1data);
                        hKiemWriteResult.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                          //      Toast.makeText(getContext(),"ess",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý lỗi khi lưu dữ liệu học sinh
                              //  Toast.makeText(getContext(),"effffss",Toast.LENGTH_LONG).show();
                            }
                        });

                        DocumentReference hkiem2Ref = db.collection("hanhKiem").document("HKII"+hsId);
                        Task<Void> hKiem2WriteResult = hkiem2Ref.set(HK2data);
                        hKiem2WriteResult.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                             ///   Toast.makeText(getContext(),"ess",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý lỗi khi lưu dữ liệu học sinh
                               // Toast.makeText(getContext(),"effffss",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }
    }


    private void uploadExcelDataToFirestore1(Uri fileUri) throws IOException {

        //    Log.d("hhhhhhh", "jhhhhhhhhhhh");

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri)) {
            if (inputStream != null) {
                Workbook workbook = new XSSFWorkbook(inputStream);
                ((XSSFWorkbook) workbook).setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Sheet sheet = workbook.getSheetAt(0);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // sheet.getLastRowNum()
                for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Bắt đầu từ hàng thứ hai (giả sử hàng đầu tiên là tiêu đề)
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        String hsId = getCellValueAsString(row.getCell(0));
                        String hsHoTen = getCellValueAsString(row.getCell(1));
                        String hsNgaySinh = getCellValueAsString(row.getCell(2));
                        String hsGioiTinh = getCellValueAsString(row.getCell(3));
                        String hsTenLop = getCellValueAsString(row.getCell(4));
                        String hsChucVu = getCellValueAsString(row.getCell(5));
                        String hsNienKhoa = getCellValueAsString(row.getCell(6));
                        String hsMatKhau = getCellValueAsString(row.getCell(7));

                        // Lưu dữ liệu học sinh vào Firestore
                        Map<String, Object> hsData = new HashMap<>();
                        hsData.put("HS_id", hsId);
                        hsData.put("HS_HoTen", hsHoTen);
                        hsData.put("HS_NgaySinh", hsNgaySinh);
                        hsData.put("HS_GioiTinh", hsGioiTinh);
                        hsData.put("HS_ChucVu", hsChucVu);
                        hsData.put("LH_id", hsTenLop+hsNienKhoa);
                        hsData.put("TK_id", hsId);

                        //    Lưu dữ liệu tài khoản vào Firestore
                        Map<String, Object> tkData = new HashMap<>();
                        tkData.put("TK_id", hsId);
                        tkData.put("TK_HoTen", hsHoTen);
                        tkData.put("TK_TenTaiKhoan", hsId);
                        tkData.put("TK_NgaySinh", hsNgaySinh);
                        tkData.put("TK_ChucVu", hsChucVu);
                        tkData.put("TK_MatKhau", hsMatKhau);

//                        Account account = new Account(hsId,hsId,hsNgaySinh, hsMatKhau, hsHoTen, hsChucVu);
//                        List<Account> accountList = new ArrayList<>();
//                        accountList.add(account);
//                        onCreateAccount(accountList);

                        Map<String, Object> HK1data = new HashMap<>();
                        HK1data.put("HKM_DiemRenLuyen", "100");
                        HK1data.put("HKM_id", "HKI"+hsId);
                        HK1data.put("HKM_HanhKiem", "Tốt");
                        HK1data.put("HK_HocKy", "Học kỳ 1");
                        HK1data.put("HS_id", hsId);

                        Map<String, Object> HK2data = new HashMap<>();
                        HK2data.put("HKM_DiemRenLuyen", "100");
                        HK2data.put("HKM_id", "HKII"+hsId);
                        HK2data.put("HKM_HanhKiem", "Tốt");
                        HK2data.put("HK_HocKy", "Học kỳ 2");
                        HK2data.put("HS_id", hsId);


                        // Lưu dữ liệu học sinh vào collection "hocSinh"
                        DocumentReference hsRef = db.collection("hocSinh").document(hsId);
                        Task<Void> hsWriteResult = hsRef.set(hsData);
                        hsWriteResult.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //  Toast.makeText(getContext(),"ess",Toast.LENGTH_LONG).show();
                                requireActivity().runOnUiThread(() -> {
                                    // Hiển thị thông báo thành công hoặc cập nhật trạng thái của widget ở đây
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý lỗi khi lưu dữ liệu học sinh
                                // Toast.makeText(getContext(),"effffss",Toast.LENGTH_LONG).show();
                                requireActivity().runOnUiThread(() -> {
                                    // Hiển thị thông báo thành công hoặc cập nhật trạng thái của widget ở đây
                                });
                            }
                        });

                        DocumentReference tkRef = db.collection("taiKhoan").document(hsId);
                        Task<Void> tkWriteResult = tkRef.set(tkData);
                        tkWriteResult.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Lưu dữ liệu tài khoản thành công

                                List<Account> accountList = new ArrayList<>();
                                Account account = new Account(hsId,hsId,hsNgaySinh, hsMatKhau, hsHoTen, hsChucVu);

                                accountList.add(account);
                                onCreateAccount(accountList);

                                requireActivity().runOnUiThread(() -> {
                                    // Hiển thị thông báo thành công hoặc cập nhật trạng thái của widget ở đây
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý lỗi khi lưu dữ liệu tài khoản
                                requireActivity().runOnUiThread(() -> {
                                    // Hiển thị thông báo thành công hoặc cập nhật trạng thái của widget ở đây
                                });
                            }
                        });


                        DocumentReference hkiem1Ref = db.collection("hanhKiem").document("HKI"+hsId);
                        Task<Void> hKiemWriteResult = hkiem1Ref.set(HK1data);
                        hKiemWriteResult.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //      Toast.makeText(getContext(),"ess",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý lỗi khi lưu dữ liệu học sinh
                                //  Toast.makeText(getContext(),"effffss",Toast.LENGTH_LONG).show();
                            }
                        });

                        DocumentReference hkiem2Ref = db.collection("hanhKiem").document("HKII"+hsId);
                        Task<Void> hKiem2WriteResult = hkiem2Ref.set(HK1data);
                        hKiem2WriteResult.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ///   Toast.makeText(getContext(),"ess",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý lỗi khi lưu dữ liệu học sinh
                                // Toast.makeText(getContext(),"effffss",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        } else if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                // Nếu ô có kiểu dữ liệu là Date
                Date cellDate = cell.getDateCellValue();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                return sdf.format(cellDate);
            } else {
                // Nếu ô có kiểu dữ liệu là số
                return String.valueOf(cell.getNumericCellValue());
            }
        } else if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else {
            return "";
        }
    }




}