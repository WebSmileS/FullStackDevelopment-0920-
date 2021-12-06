package com.yrt.common.utils.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TestExcelUtil {

	public static List<List<Map<String, String>>> readExcel(File filepath) throws Exception {
		if (!filepath.exists()) {
			System.out.println("文件不存在！");
		}
		String filename = filepath.toString();// 转化为string类型
		String fileType = filename.substring(filename.lastIndexOf(".") + 1, filename.length());// 提取文件名后缀
		InputStream is = null;
		Workbook wb = null;
		try {
			is = new FileInputStream(filepath);
			if (fileType.equals("xls")) {
				wb = new HSSFWorkbook(is);
			} else if (fileType.equals("xlsx")) {
				wb = new XSSFWorkbook(is);
			} else {
				System.out.println("文件名错误!");
			}
			// 新建集合，考虑到要用value值去查询数据库，所以value设置为string类型
			List<List<Map<String, String>>> result = new ArrayList<List<Map<String, String>>>();
			int sheetSize = wb.getNumberOfSheets();// 获取表格的个数
			System.out.println(String.format("表格的个数: %d", sheetSize));
			for(int i = 0; i < sheetSize; i++) {
				System.err.println(wb.getSheetName(i));
			}
//			for (int i = 0; i < sheetSize; i++) {// 遍历所有表格
//				Sheet sheet = wb.getSheetAt(i);
//				List<Map<String, String>> sheetList = new ArrayList<Map<String, String>>();
//				List<String> titles = new ArrayList<String>();// 放置所有的标题
//				int rowSize = sheet.getLastRowNum() + 1;// 此处getLastRowNum()方法获取的行数从0开始，故要+1
//				for (int j = 0; j < rowSize; j++) {// 遍历所有行
//					Row row = sheet.getRow(j);
//					if (row == null) {// 略过空行
//						continue;
//					}
//					int cellSize = row.getLastCellNum();// 获取列数
//					if (j == 0) {// 第一行是标题行
//						for (int k = 0; k < cellSize; k++) {// 添加到标题集合中
//							Cell cell = row.getCell(k);
//							titles.add(cell.toString());
//						}
//					} else {// 其他行是数据行，为数字
//						Map<String, String> rowMap = new HashMap<String, String>();// 保存一行的数据
//						for (int k = 0; k < titles.size(); k++) {// 遍历保存此行数据
//							Cell cell = row.getCell(k);
//							String key = titles.get(k);
//							String value = null;
//							if (cell != null) {
//								/*
//								 * 这里因为读取excel数据默认值是double类型的，但我的数据都是整数，为了方便先进行一次转换 先判断数据类型，然后先转换然后在复制给value
//								 * 数值类型是0，字符串类型是1，公式型是2，空值是3，布尔值4，错误5
//								 */
//								if (row.getCell(k).getCellType() == CellType.NUMERIC) {
//									value = (int) row.getCell(k).getNumericCellValue() + "";
//								} else {
//									value = cell.toString();// 转换成string赋值给value
//								}
//							}
//							rowMap.put(key, value);// 把数据存入map集合
//						}
//						sheetList.add(rowMap);// 把存好行的数据存入表格的集合中
//					}
//				}
//				result.add(sheetList);// 把表格的数据存到excel的集合中
//			}
			return result;
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		File f = new File("D:\\医润通文件\\UDI截至20210606全量数据\\UDID_FULL_20210606PART_1.xls");
		TestExcelUtil.readExcel(f);
	}

}
