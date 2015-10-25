package com.example.android1;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Bitmap mback1,mback2,mfile,mfolder;
	private List<String> items;
	private List<String> paths;
	public MyListAdapter(Context c,List<String>items,List<String> paths){
		inflater=LayoutInflater.from(c);
		this.items=items;
		this.paths=paths;
		mback1=BitmapFactory.decodeResource(c.getResources(),R.drawable.ic_launcher);
		mback2=BitmapFactory.decodeResource(c.getResources(),R.drawable.ic_launcher);
		mfile=BitmapFactory.decodeResource(c.getResources(),R.drawable.ic_launcher);
		mfolder=BitmapFactory.decodeResource(c.getResources(),R.drawable.ic_launcher);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewKeeper vk;
		if(convertView==null){
			convertView=inflater.inflate(R.layout.showitem, null);
			vk=new ViewKeeper();
			vk.tv=(TextView)convertView.findViewById(R.id.text);
			vk.iv=(ImageView)convertView.findViewById(R.id.icon);
			convertView.setTag(vk);
		}else{
			vk=(ViewKeeper)convertView.getTag();
		}
		File f=new File(paths.get(position).toString());
		if(items.get(position).toString().equals("root")){
			vk.tv.setText("Back root");
			vk.iv.setImageBitmap(mback1);
		}else if(items.get(position).toString().equals("forward")){
			vk.tv.setText("Back Forward");
			vk.iv.setImageBitmap(mback2);
		}else{
			if(f.isDirectory()){
				vk.tv.setText(f.getName());
				vk.iv.setImageBitmap(mfolder);
			}else{
				vk.tv.setText(f.getName());
				vk.iv.setImageBitmap(mfile);
			}
		}
		return convertView;
	}
	class ViewKeeper{
		TextView tv;
		ImageView iv;
	}
}
