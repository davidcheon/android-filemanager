package com.example.android1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.tools.ContactsUtil;
import com.example.tools.excelutil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends Activity {
	private Button openbutton=null,loadbutton=null,getcontacts=null,selectall=null,deletecontacts=null;
	private TextView tvshow=null;
	private final int ResultCode=1;
	private List<String>results;
	private ListView lv=null;
	private ProgressDialog mpd,cpd,dpd;
	private ContactsUtil cutil;
	private ListView contactlist=null;
	private MyContactListAdapter mycla=null;
	private List<Map<String,String>>localphones=null;
	public static final int SHOW_PROGRESS_DIALOG=2;
	public static final int LOAD_LOCALPHONE_OK=3;
	public static final int DELETE_LOCALPHONE_OK=4;
	private boolean flag=false;
	public Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case LOAD_LOCALPHONE_OK:
				contactlist.setAdapter(mycla=new MyContactListAdapter(MainActivity.this, localphones));
				selectall.setVisibility(View.VISIBLE);
				deletecontacts.setVisibility(View.VISIBLE);
				cpd.dismiss();
				break;
			case DELETE_LOCALPHONE_OK:
				MainActivity.this.mycla.freshdatabase();
				stopdeletedialog();
				break;
			}
			super.handleMessage(msg);
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		results=new ArrayList<String>();
		selectall=(Button)findViewById(R.id.selectall);
		deletecontacts=(Button)findViewById(R.id.delectcontacts);
		lv=(ListView)this.findViewById(R.id.resultlist);
		contactlist=(ListView)this.findViewById(R.id.contactslist);
		openbutton=(Button)findViewById(R.id.button1);
		loadbutton=(Button)findViewById(R.id.button2);
		getcontacts=(Button)this.findViewById(R.id.button3);
		tvshow=(TextView)this.findViewById(R.id.Textview1);
		cutil=new ContactsUtil(getContentResolver());
			if(flag){
				selectall.setVisibility(View.VISIBLE);
				deletecontacts.setVisibility(View.VISIBLE);
			}	
		openbutton.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,MyFileManager.class);
				startActivityForResult(intent,ResultCode);
			}
		});
		loadbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new LoadFile().execute();
			}
		});
		getcontacts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateContactList();
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				// TODO Auto-generated method stub
				TextView tmp=(TextView)view.findViewById(R.id.showresult);
				AlertDialog.Builder builder=new Builder(MainActivity.this);
				String fullpath=tmp.getText().toString();
				String filename=(String) fullpath.subSequence(fullpath.lastIndexOf("/"), fullpath.length());
				builder.setMessage("You want to delete <"+filename+"> ?");
				builder.setTitle("Info");
				builder.setPositiveButton("yes", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						results.remove(position);
						lv.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.resultitem, R.id.showresult, results));
					}
				});
				builder.setNegativeButton("no", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				builder.create().show();
				return false;
			}
		} 
		);
		selectall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			MainActivity.this.mycla.selectallcb();
			}
		});
		deletecontacts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				MainActivity.this.mycla.delete();
				showdeletedialog();
			}
		});
	}
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg0==ResultCode){
			loadbutton.setEnabled(true);
			Bundle bundle=null;
			if(arg2!=null&&(bundle=arg2.getExtras())!=null){
				checkandaddfile(bundle.getString("file"));
				tvshow.setText("You selected:\n");
				lv.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.resultitem, R.id.showresult, results));
			}
		}
	};
	private void checkandaddfile(String filename){
		if(!filename.trim().endsWith(".xls")){
			return;
		}
		for(String path:results){
			if(path.equals(filename)){
				return;
			}
		}
		results.add(filename);
	}
	private void startshowdialog(){
		mpd=new ProgressDialog(this);
		mpd.setMessage("Loading File.....");
		mpd.setIndeterminate(false);
		mpd.setMax(100);
		mpd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mpd.show();
	}
	private void stopshowdialog(){
		mpd.dismiss();
	}
	private void updateContactList(){
		cpd=new ProgressDialog(MainActivity.this);
		cpd.setTitle("Information");
		cpd.setMessage("Loading....");
		cpd.show();
		new Thread(new  Runnable() {
			public void run() {
				flag=true;
				localphones=cutil.getAllPhonesAsListUpdate();
				handler.sendEmptyMessage(LOAD_LOCALPHONE_OK);
			}
		}).start();
	}
	private void showdeletedialog(){
		AlertDialog.Builder builder=new Builder(this);
		builder.setMessage("Are you sure to delete?");
		builder.setTitle("Information");
		builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				dpd=new ProgressDialog(MainActivity.this);
				dpd.setTitle("Information");
				dpd.setMessage("Deleting Contacts.....");
				dpd.show();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						MainActivity.this.mycla.delete(cutil);
						handler.sendEmptyMessage(DELETE_LOCALPHONE_OK);
					}
				}).start();
			}
		});
		builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	private void stopdeletedialog(){
		dpd.dismiss();
	}
	class LoadFile extends AsyncTask<String, String, String>{
//		int finishfile=1;
		double prog=1;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			startshowdialog();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Map<String,String>localphones=cutil.getAllPhones();
			int total=0;
			for (String path:results) {
			total+=excelutil.getExcelcounts(path);
			}
			for (String path:results) {
			List<Map<String,String>>rs=excelutil.readExcel(path);
			for (int i=0;i<rs.size();i++) {
				
				String phonevalue=rs.get(i).get("手机");
				if(localphones.containsKey(phonevalue)){
					rs.remove(i);
					i--;
					total--;
				}
			}
//			boolean writeresult=cutil.writeContacts(rs);
//			if(! writeresult){
//				return "failed";
//			}
//			prog=(int)100/results.size()*finishfile;
//			publishProgress(""+prog);
//			finishfile+=1;
			Log.i("result tag","---total--->"+total);
			for(int i=0;i<rs.size();i++){
				cutil.writeContactsUpdate(rs.get(i));
				
				publishProgress(""+(int)(prog/total*100));
				
				Log.i("result tag","------>"+(int)(prog/total*100));
				prog+=1;
			}
		}
			return "succeed";
		}
		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			mpd.setProgress(Integer.parseInt(values[0]));
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			stopshowdialog();
			if(flag){
				updateContactList();
			}
			Toast.makeText(getApplicationContext(), "load "+result, Toast.LENGTH_SHORT).show();
		}
	}
}
