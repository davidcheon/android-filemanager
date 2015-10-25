package com.example.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;

public class ContactsUtil {
	private ContentResolver contentresolver=null;
	public ContactsUtil(ContentResolver cr){
		this.contentresolver=cr;
	}
	public Map<String,String> getAllPhones(){
		Map<String,String> allphones=new HashMap<String,String>();
		Cursor cursor=this.contentresolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if(cursor.moveToFirst()){
			int displaynamecolumn=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			int idcolumn = cursor.getColumnIndex(ContactsContract.Contacts._ID); 
			do{
				int phonecount = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				if (phonecount>0){
				String contactid = cursor.getString(idcolumn); 
				Cursor phonescursor = this.contentresolver.query(  
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,  
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID  
						+ " = " + contactid, null, null);
				if(phonescursor.moveToFirst()){
					List<String>phones=new ArrayList<String>();
					do{
					String phoneNumber = phonescursor  
							.getString(phonescursor  
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					phones.add(phoneNumber);
					}while(phonescursor.moveToNext());
					if(phones.size()>0){
					String name=cursor.getString(displaynamecolumn)+"=="+contactid;
					allphones.put(phones.get(0),name);
					}
				}
				}
			}while(cursor.moveToNext());
		}
		return allphones;
	}
public boolean writeContacts(List<Map<String,String>> values){
	try{
	for(Map<String,String>map:values){
	String phonenumber=map.get("手机");
	String name=map.get("姓名");
	Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
    ContentValues cv = new ContentValues();  
    long contactid = ContentUris.parseId(this.contentresolver.insert(uri, cv));  
    uri = Uri.parse("content://com.android.contacts/data");  
    cv.put("raw_contact_id", contactid);  
    cv.put("mimetype", "vnd.android.cursor.item/name");  
    cv.put("data2", name);  
    this.contentresolver.insert(uri, cv);  
    cv.clear();  
    cv.put("raw_contact_id", contactid);  
    cv.put("mimetype", "vnd.android.cursor.item/phone_v2");  
    cv.put("data2", "2");  
    cv.put("data1", phonenumber);  
    this.contentresolver.insert(uri, cv);
	}
	}catch(Exception e){
		e.printStackTrace();
		return false;
	}
	return true;
	}
public List<Map<String,String>> getAllPhonesAsList(){
	List<Map<String,String>> tmp=new ArrayList<Map<String,String>>();
	Map<String,String> map=getAllPhones();
	for(Entry<String,String>e:map.entrySet()){
		Map<String,String>m=new HashMap<String,String>();
		m.put(e.getKey(), e.getValue());
		tmp.add(m);
	}
	return tmp;
}
public List<Map<String,String>> getAllPhonesAsListUpdate(){
	List<Map<String,String>> allphones=new ArrayList<Map<String,String>>();
	Cursor cursor=this.contentresolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
	if(cursor.moveToFirst()){
		int displaynamecolumn=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		int idcolumn = cursor.getColumnIndex(ContactsContract.Contacts._ID); 
		do{
			int phonecount = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
			if (phonecount>0){
			String contactid = cursor.getString(idcolumn); 
			Cursor phonescursor = this.contentresolver.query(  
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,  
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID  
					+ " = " + contactid, null, null);
			
			if(phonescursor.moveToFirst()){
				List<String>phones=new ArrayList<String>();
				do{
				String phoneNumber = phonescursor  
						.getString(phonescursor  
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				phones.add(phoneNumber);
				}while(phonescursor.moveToNext());
				if(phones.size()>0){
				String name=cursor.getString(displaynamecolumn)+"=="+contactid;
				Map<String,String>m=new HashMap<String, String>();
				m.put(phones.get(0),name);
				allphones.add(m);
				}
			}
			}
		}while(cursor.moveToNext());
	}
	return allphones;
}
public boolean deleteContact(String name,String id,String phonenumber){
	 ArrayList<ContentProviderOperation> ops =new ArrayList<ContentProviderOperation>();

	 ops.add(ContentProviderOperation
	            .newDelete(RawContacts.CONTENT_URI)
	            .withSelection(
	                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
	                    new String[] {String.valueOf(id) }).build());
	 
	 try {
		contentresolver.applyBatch(ContactsContract.AUTHORITY, ops);
		ops.clear();
		return true;
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	} catch (OperationApplicationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
}
}
