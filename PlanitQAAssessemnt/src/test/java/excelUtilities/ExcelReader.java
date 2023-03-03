package excelUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
	
	public Object[][] readData() throws IOException{
		Object[][] data = null;
		Workbook book= null;
		Row row;
		
		String filename = System.getProperty("user.dir")+"//Resources//TestData.xlsx";
		File file = new File(filename);
		InputStream stream = new FileInputStream(file);
		
		String extension = filename.substring(filename.indexOf("."));
		if(extension.equalsIgnoreCase(".xlsx")){
			book = new XSSFWorkbook(stream);
		} else if(extension.equalsIgnoreCase(".xls")){
			book = new HSSFWorkbook(stream);
		}
	
		Sheet sheet = book.getSheet("Data");
		
		int noRows = sheet.getPhysicalNumberOfRows();
		System.out.println("noRows = "+noRows);
		data = new Object[noRows-1][3]; // Bypass header of excel
		for(int i=1;i<noRows;i++){
			row = sheet.getRow(i);
			for(int j=0;j<row.getLastCellNum();j++){
				if (row.getCell(j).getCellType() == 1){
				String cellValue = row.getCell(j).getStringCellValue();
				data[i-1][j]=cellValue;
				System.out.println("String -"+cellValue);
				} else {
					int cellValue = (int) row.getCell(j).getNumericCellValue();
					data[i-1][j]=String.valueOf(cellValue) ;
					System.out.println("Integer -"+cellValue);
				}
				
				
				
				System.out.println("data["+(i-1)+"]["+j+"]="+data[i-1][j]);
				
			}
		}
			
		return data;
	}
	

}
