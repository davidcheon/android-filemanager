package com.example.android1;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
public class MyFileManager extends ListActivity {
	private String selectpath="/sdcard/Download";
	private final int RESULTCODE=1;
	private TextView currentpath=null;
	private String rootpath="/sdcard/Download";
	private List<String> items=null;
	private List<String> paths=null;
	private MyListAdapter madapter=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_myfilemanager);
		Button okbutton=(Button)findViewById(R.id.okbutton);
		currentpath=(TextView)findViewById(R.id.currentpath);
		okbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File file=new File(selectpath);
				if(file.isDirectory()){
					dialog("You should select a xls file.",1);
				}else{
				Intent data=new Intent(MyFileManager.this,MainActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("file", selectpath);
				data.putExtras(bundle);
				setResult(RESULTCODE, data);
				finish();
				}
			}
		});
		Button cancelbutton=(Button)findViewById(R.id.cancelbutton);
		cancelbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		getFileDir(rootpath);
	}
	private void getFileDir(String filepath){
		currentpath.setText(filepath);
		items=new ArrayList<String>();
		paths=new ArrayList<String>();
		File file=new File(filepath);
		items.add("root");
		items.add("forward");
		paths.add(rootpath);
		paths.add(file.getParent().contains(rootpath)?file.getParent():rootpath);
		if(file.isDirectory()){
			File[]fs=file.listFiles();
			if (fs.length!=0){
				for (int i = 0; i <fs.length; i++) {
					items.add(fs[i].getName());
					paths.add(fs[i].getAbsolutePath());
				}	
			}
			
		}else if(file.isFile()){
			items.add(file.getName());
			paths.add(file.getAbsolutePath());
		}
		setListAdapter(madapter=new MyListAdapter(this,items,paths));
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		String path=(paths.get(position).toString().equals("/")?paths.get(0):paths.get(position));
		File file=new File(path);
		selectpath=file.getAbsolutePath();
		for(int i=0;i<l.getCount();i++){
			if(position!=i){
				v.setBackgroundColor(Color.TRANSPARENT);
			}
		}
		if (file.isDirectory()){
			getFileDir(selectpath);
		}else{
			dialog("You want to select it?",2);
		}
	}
	private void dialog(String message,int nums){
		AlertDialog.Builder builder=new Builder(this);
		builder.setMessage(message);
		builder.setTitle("Information");
		if(nums==2){
		builder.setPositiveButton("yes", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent data=new Intent(MyFileManager.this,MainActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("file", selectpath);
				data.putExtras(bundle);
				setResult(RESULTCODE, data);
				finish();
			}
		});
		}
		String tmp=nums!=1?"no":"ok";
		builder.setNegativeButton(tmp, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}
