package com.example.campusride;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MyPassFragment extends Fragment {

    TextView tvName, tvSap, tvProgramme, tvBranch, tvYear, tvStatus;
    ImageView qrImage;

    DatabaseHelper databaseHelper;

    String sapId;

    public MyPassFragment(String sapId) {
        this.sapId = sapId;
    }

    public MyPassFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mypass, container, false);

        tvName = view.findViewById(R.id.tvName);
        tvSap = view.findViewById(R.id.tvSap);
        tvProgramme = view.findViewById(R.id.tvProgramme);
        tvBranch = view.findViewById(R.id.tvBranch);
        tvYear = view.findViewById(R.id.tvYear);
        tvStatus = view.findViewById(R.id.tvStatus);
        qrImage = view.findViewById(R.id.qrImage);

        databaseHelper = new DatabaseHelper(getContext());

        if(!TextUtils.isEmpty(sapId)){

            StudentModel student = databaseHelper.getPassBySapId(sapId);

            if(student != null){

                tvName.setText(student.getName());
                tvSap.setText("SAP ID: " + student.getSapId());
                tvProgramme.setText("Programme: " + student.getProgramme());
                tvBranch.setText("Branch: " + student.getBranch());
                tvYear.setText("Year: " + student.getYear());
                tvStatus.setText("ACTIVE");

                generateQR(student.getSapId());

            }else{

                tvStatus.setText("NO PASS FOUND");

            }

        }

        return view;
    }

    private void generateQR(String text){

        try{

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

            Bitmap bitmap = barcodeEncoder.encodeBitmap(
                    text,
                    BarcodeFormat.QR_CODE,
                    400,
                    400
            );

            qrImage.setImageBitmap(bitmap);

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}