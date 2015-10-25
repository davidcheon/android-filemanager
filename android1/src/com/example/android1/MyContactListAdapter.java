package com.example.android1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.example.tools.ContactsUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class MyContactListAdapter extends BaseAdapter {

	private Context context=null;
	private List<Map<String,String>>localphones=null;
	private Map<Integer,Boolean>checkstatus=null;
	private boolean flag;
	public MyContactListAdapter(Context context,List<Map<String,String>>phones){
		this.context=context;
		this.localphones=phones;
		this.checkstatus=new HashMap<Integer,Boolean>();
		init();
	}
	private void init() {
		// TODO Auto-generated method stub
		for(int i=0;i<this.localphones.size();i++){
			this.checkstatus.put(i,false);
		}
		notifyDataSetInvalidated();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return localphones.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return localphones.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.contactitem, null);
			holder.tv=(TextView)convertView.findViewById(R.id.contactcontent);
			
			holder.cb=(CheckBox)convertView.findViewById(R.id.contactselect);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				CheckBox cb=(CheckBox)buttonView;
				checkstatus.put(position, cb.isChecked());
			}
		});
		holder.cb.setChecked(checkstatus.get(position));
		Map<String,String> map=localphones.get(position);
		String content="";
		for(Entry<String,String>e:map.entrySet()){
			content="Name:"+e.getValue().split("==")[0]+"-------Phone:"+e.getKey();
		}
		holder.tv.setText(content);
		return convertView;
	}
public void selectallcb(){
	for(int i=0;i<this.localphones.size();i++){
		this.checkstatus.put(i, flag);
	}
	flag=! flag;
	notifyDataSetChanged();
}
public void delete(ContactsUtil cutil){
	
	List<Integer>indexs=new ArrayList<Integer>();
	for(Entry<Integer,Boolean> e:this.checkstatus.entrySet()){
		if(e.getValue()){
			indexs.add(e.getKey());
		}
	}
	if (indexs.size()==0){
		return;
	}
	int size=indexs.size();
	int tmp=size;
	for(Integer index:indexs){
		int tm=size-tmp;
		for(Entry<String,String> en:this.localphones.get(index-tm).entrySet()){
			String phonenumber=en.getKey();
			String name=en.getValue();
			String id=name.split("==")[1];
			name=name.split("==")[0];
			/*
			 * delete action
			 * 
			 */
				if(cutil.deleteContact(name, id, phonenumber)){
					this.localphones.remove(index.intValue()-tm);	
					tmp--;
				}
			continue;
		}
	}
}
public void freshdatabase(){
	for(Entry<Integer,Boolean>e:this.checkstatus.entrySet()){
		e.setValue(false);
	}
	notifyDataSetChanged();
}
}
class ViewHolder{
	TextView tv;
	CheckBox cb;
}
