package com.example.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class excelutil {
	public static List<Map<String,String>> readExcel(String path){
		List<Map<String,String>> rs=new ArrayList<Map<String,String>>();
		Workbook wb=null;
		try {
			wb = Workbook.getWorkbook(new File(path));
			Sheet sheet=wb.getSheet(0);
			
			String name=sheet.getCell(0, 0).getContents();
			String phonenumber=sheet.getCell(1, 0).getContents();
			for(int x=1;x<sheet.getRows();x++){
				Map<String,String>map=new HashMap<String, String>();
						map.put(name, sheet.getCell(0, x).getContents());
						map.put(phonenumber, sheet.getCell(1, x).getContents());
						rs.add(map);
			}
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			wb.close();
		}
		return rs;
	}
}
