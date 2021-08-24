package com.javaex.mysite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.javaex.vo.GuestbookVo;

import java.util.List;

public class GuestbookListAdapter extends ArrayAdapter<GuestbookVo> {

    private TextView txtNo;
    private TextView txtName;
    private TextView txtRegDate;
    private TextView txtContent;




    public GuestbookListAdapter(Context context, int resource, List<GuestbookVo> objects) {
        super(context, resource, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if(view == null) { //만들어놓은 이름이없다

            LayoutInflater layoutInflater = (LayoutInflater) LayoutInflater.from(getContext());


            view = layoutInflater.inflate(R.layout.activity_list_item, null);

        }

        //1개의 방명록 데이터가 있다.
        //1개의 처리

        txtNo = view.findViewById(R.id.txtNo);
        txtName = view.findViewById(R.id.txtName);
        txtRegDate = view.findViewById(R.id.txtRegDate);
        txtContent = view.findViewById(R.id.txtContent);

        //데이터 가져오기(1개 데이터)

        GuestbookVo guestbookVo = super.getItem(position);

        txtNo.setText("" + guestbookVo.getNo());
        txtName.setText(guestbookVo.getName());
        txtRegDate.setText(guestbookVo.getRegDate());
        txtContent.setText(guestbookVo.getContent());

        return view;

    }
}
