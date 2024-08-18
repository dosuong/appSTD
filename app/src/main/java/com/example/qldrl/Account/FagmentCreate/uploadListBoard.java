package com.example.qldrl.Account.FagmentCreate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
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
 * Use the {@link uploadListBoard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class uploadListBoard extends Fragment {
    private CreateManyAccountCallback callback;
    private static final int REQUEST_CODE = 123;
    private TextView txtNameFile;
    private Button btnExitAcc,btnUpload, btnChoiceFile;
    private View myListBoard;
    private ProgressDialog progressDialog;
    private Dialog currentDialog;
    private ActivityResultLauncher<Intent> fileChooserLauncher;
    Uri uri;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public uploadListBoard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment uploadList.
     */
    // TODO: Rename and change types and number of parameters
    public static uploadListBoard newInstance(String param1, String param2) {
        uploadListBoard fragment = new uploadListBoard();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        myListBoard =  inflater.inflate(R.layout.fragment_upload_list_board, container, false);

        txtNameFile = myListBoard.findViewById(R.id.txtNameFileBoard);
        btnUpload = myListBoard.findViewById(R.id.btnUploadBoard);
        btnChoiceFile = myListBoard.findViewById(R.id.btnChoiceFileBoard);
        btnExitAcc = myListBoard.findViewById(R.id.btnExitBoard);
       // progressBar2 = myListBoard.findViewById(R.id.progressBar2);


        btnChoiceFile.setOnClickListener(v -> {
            openFileChooser();
        });
        initFileChooserLauncher();



        btnUpload.setOnClickListener(v -> {
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





        btnExitAcc.setOnClickListener(v -> {
            if (listAcc.currentDialog != null) {
                listAcc.currentDialog.dismiss();
            }
        });

        return  myListBoard;
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

                        txtNameFile.setText( fileName);

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





    private void uploadExcelDataToFirestore1(Uri fileUri) throws IOException {
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri)) {
            if (inputStream != null) {
                Workbook workbook = new XSSFWorkbook(inputStream);
                Sheet sheet = workbook.getSheetAt(0);
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Bắt đầu từ hàng thứ hai
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        String hsId = getCellValueAsString(row.getCell(0));
                        String hsHoTen = getCellValueAsString(row.getCell(1));
                        String hsNgaySinh = getCellValueAsString(row.getCell(2));
                        String hsChucVu = getCellValueAsString(row.getCell(3));
                        String hsMatKhau = getCellValueAsString(row.getCell(4));

                        Map<String, Object> tkData = new HashMap<>();
                        tkData.put("TK_id", hsId);
                        tkData.put("TK_HoTen", hsHoTen);
                        tkData.put("TK_TenTaiKhoan", hsId);
                        tkData.put("TK_NgaySinh", hsNgaySinh);
                        tkData.put("TK_ChucVu", hsChucVu);
                        tkData.put("TK_MatKhau", hsMatKhau);

                        DocumentReference tkRef = db.collection("taiKhoan").document(hsId);
                        tkRef.set(tkData)
                                .addOnSuccessListener(aVoid -> {
                                    // Cập nhật UI trên luồng chính
                                    List<Account>  accountList = new ArrayList<>();
                                    Account account = new Account(hsId,hsId,hsNgaySinh, hsMatKhau, hsHoTen, hsChucVu);
                                    accountList.add(account);
                                    onCreateAccount(accountList);
                                    requireActivity().runOnUiThread(() -> {
                                        // Hiển thị thông báo thành công hoặc cập nhật trạng thái của widget ở đây
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    // Cập nhật UI trên luồng chính
                                    requireActivity().runOnUiThread(() -> {
                                        // Thông báo lỗi hoặc cập nhật giao diện
                                    });
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
    private void onCreateAccount(List<Account> newAccounts) {
        // Xử lý tại Fragment khi tài khoản mới được tạo
        // Ví dụ: cập nhật dữ liệu, hiển thị thông báo, v.v.

        // Sau đó gọi callback để thông báo cho Activity
        callback.onManyAccountCreated(newAccounts);
    }




}