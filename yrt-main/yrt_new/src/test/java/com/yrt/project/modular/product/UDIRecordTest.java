package com.yrt.project.modular.product;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.yrt.common.utils.poi.ExcelUtil;
import com.yrt.project.modular.product.domain.UDIDeviceRecord;
import com.yrt.project.modular.product.mapper.UDIDeviceRecordMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
//@Transactional
public class UDIRecordTest {

	@Autowired UDIDeviceRecordMapper mapper;
	
	@Test
	public void importUDIRecord()throws Exception{
		ExcelUtil<UDIDeviceRecord> util = new ExcelUtil<UDIDeviceRecord>(UDIDeviceRecord.class);
		String path = "D:\\医润通文件\\UDI";
		File currentDir = new File(path);
		File[] fs = currentDir.listFiles((file, name) -> name.endsWith(".xls"));
		for (File file : fs) {
			System.err.println("开始导入" + file.getName()+"文件");
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				List<UDIDeviceRecord> udiList = util.importExcel("标识信息",fileInputStream);
				for (UDIDeviceRecord record : udiList) {
					try {
						mapper.insertUDIDeviceRecord(record);
					}catch(Exception e) {
						/**
						 * 国家网站上下载的excel文件内容确实存在错误内容，所以选择跳过有问题的记录后继续导入
						 */
						System.err.println(String.format("%s文件中有导入错误：%s", file.getName(),e.getMessage()));
					}
				}
			}catch (FileNotFoundException e) {
				System.out.println("文件不存在或者文件不可读或者文件是目录");
			}
			Thread.sleep(3000);
		}
	}
	
}
