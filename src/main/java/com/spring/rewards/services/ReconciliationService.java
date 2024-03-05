package com.spring.rewards.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.spring.rewards.Repository.EmployeeRepository;
import com.spring.rewards.Repository.ReconciliationRepository;
import com.spring.rewards.entity.Employee;
import com.spring.rewards.entity.Reconciliation;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ReconciliationService {

	@Autowired
	private EmployeeRepository empRepo;

	@Autowired
	private ReconciliationRepository reconRepo;
	
	
	public List<Reconciliation> getChildrenReconciliationRecords(Long parentEmpId){
		Optional<Employee> parentOptional = empRepo.findById(parentEmpId);
		if(parentOptional.isPresent()) {
			Employee parentEmployee  =parentOptional.get();
			
			List<Employee>children= parentEmployee.getChildern();
			
			List<Reconciliation>reconciliationRecords=new ArrayList<>();
			for(Employee child:children) {
				List<Reconciliation>childReconciliations = reconRepo.findByEmployeeNumber(child.getEmpId());
				reconciliationRecords.addAll(childReconciliations);
			}
			return reconciliationRecords;
			
		}else {
			return Collections.emptyList();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public void reconcile(InputStream otlInputStream, InputStream timexInputStream,String weekNum,String periodNum, String yearNum) {
		Map<Long, String> idMap = fetchEmployeesWithBothIds();
		System.out.println(idMap);

		if (idMap.isEmpty()) {
			System.out.println("No employee IDs found in Employee Table");
			return;
		}

//		Map<String, Float> otlHoursMap = readAndProcessOtlFile(otlInputStream, "Peopleone Number", idMap.keySet(),
//				"Monday Hours", "Tuesday Hours", "Wednesday Hours", "Thursday Hours", "Friday Hours", "Saturday Hours",
//				"Sunday Hours");

		Map<String, Float> otlHoursMap = readAndProcessOtlFile(otlInputStream, "Peopleone Number", idMap.keySet(),
				"Hours","Project Number");
		System.out.println(otlHoursMap);

		LinkedHashSet<String> tescoIdsSet = new LinkedHashSet<>(idMap.values());

		Map<String, Float> timexHoursMap = readAndProcessTimexFile(timexInputStream, "Employee_Number", tescoIdsSet,
				"Booked_Hours","Project_Code");
		System.out.println(idMap.values());
		System.out.println(timexHoursMap);

		for (Map.Entry<Long, String> entry : idMap.entrySet()) {
			Long empId = entry.getKey();
			String timexId = entry.getValue();

			if (otlHoursMap.containsKey(String.valueOf(empId)) && timexHoursMap.containsKey(timexId)) {
				Float otlHours = otlHoursMap.get(String.valueOf(empId));
				Float timexHours = timexHoursMap.get(timexId);
				Float difference = otlHours - timexHours;

				// Update reconciliation table
				insertIntoReconciliationTable(empId, difference,otlHours,timexHours,weekNum,yearNum,periodNum);
			}
		}
	}

	private Map<Long, String> fetchEmployeesWithBothIds() {
		Map<Long, String> idMap = new HashMap<>();

		// Fetch employees with both empId and tescoId
		Iterable<Employee> employees = empRepo.findAll();
		for (Employee employee : employees) {
			if (employee.getEmpId() != null && employee.getTescoId() != null) {
				idMap.put(employee.getEmpId(), employee.getTescoId());
			}
		}
		return idMap;
	}

//	private Map<String, Float> readAndProcessOtlFile(InputStream inputStream, String employeeNumberColumnName,
//			Set<Long> empIds, String... hourColumnNames) {
//		Map<String, Float> hoursMap = new HashMap<>();
//
//		try (Workbook workbook = new XSSFWorkbook(inputStream)) {
//			Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
//
//			int employeeNumberColumnIndex = -1;
//			int[] hourColumnIndices = new int[hourColumnNames.length];
//
//			Row headerRow = sheet.getRow(0);
//			if (headerRow != null) {
//				for (int i = 0; i < headerRow.getLastCellNum(); i++) {
//					Cell cell = headerRow.getCell(i);
//					if (cell != null && cell.getCellType() == CellType.STRING) {
//						String columnName = cell.getStringCellValue().trim();
//						if (columnName.equalsIgnoreCase(employeeNumberColumnName)) {
//							employeeNumberColumnIndex = i;
//						} else {
//							for (int j = 0; j < hourColumnNames.length; j++) {
//								if (columnName.equalsIgnoreCase(hourColumnNames[j])) {
//									hourColumnIndices[j] = i;
//									break;
//								}
//							}
//						}
//					}
//				}
//			}
//
//			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//				Row row = sheet.getRow(i);
//				if (row != null) {
//					Cell empIdCell = row.getCell(employeeNumberColumnIndex);
//					if (empIdCell != null && empIdCell.getCellType() == CellType.NUMERIC) {
//						long empId = (long) empIdCell.getNumericCellValue();
//						if (empIds.contains(empId)) {
//							float totalHours = 0.0f;
//							for (int hourColumnIndex : hourColumnIndices) {
//								Cell cell = row.getCell(hourColumnIndex);
//								if (cell != null && cell.getCellType() == CellType.NUMERIC) {
//									totalHours += (float) cell.getNumericCellValue();
//								}
//							}
//							hoursMap.put(String.valueOf(empId), totalHours);
//						}
//					}
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return hoursMap;
//	}
	
	private Map<String, Float> readAndProcessOtlFile(InputStream inputStream, String employeeNumberColumnName,
			Set<Long> empIds, String hoursColumnName,String projectNumberColumnName) {
		Map<String, Float> hoursMap = new HashMap<>();

		try (Workbook workbook = new XSSFWorkbook(inputStream)) {
			Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

			int employeeNumberColumnIndex = -1;
			int hoursColumnIndex = -1;

			Row headerRow = sheet.getRow(0);
			if (headerRow != null) {
				for (int i = 0; i < headerRow.getLastCellNum(); i++) {
					Cell cell = headerRow.getCell(i);
					if (cell != null && cell.getCellType() == CellType.STRING) {
						String columnName = cell.getStringCellValue().trim();
						if (columnName.equalsIgnoreCase(employeeNumberColumnName)) {
							employeeNumberColumnIndex = i;
						} else if (columnName.equalsIgnoreCase(hoursColumnName)) {
							hoursColumnIndex = i;
						}
					}
				}
			}

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					Cell empIdCell = row.getCell(employeeNumberColumnIndex);
					if (empIdCell != null && empIdCell.getCellType() == CellType.NUMERIC) {
						long empId = (long) empIdCell.getNumericCellValue();
						if (empIds.contains(empId) && !isExcludedProjectNumber(projectNumberColumnName)) {
							Cell hoursCell = row.getCell(hoursColumnIndex);
							if (hoursCell != null && hoursCell.getCellType() == CellType.NUMERIC) {
								float hours = (float) hoursCell.getNumericCellValue();
								if (hoursMap.containsKey(String.valueOf(empId))) {
									hours += hoursMap.get(String.valueOf(empId));
								}
								hoursMap.put(String.valueOf(empId), hours);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return hoursMap;
	}

	private Map<String, Float> readAndProcessTimexFile(InputStream inputStream, String employeeNumberColumnName,
			Set<String> tescoIds, String bookedHoursColumnName,String projectCodeColumnName) {
		Map<String, Float> hoursMap = new HashMap<>();

		try (Workbook workbook = new XSSFWorkbook(inputStream)) {
			Sheet sheet = workbook.getSheetAt(0);
			int employeeNumberColumnIndex = -1;
			int bookedHoursColumnIndex = -1;

			Row headerRow = sheet.getRow(0);
			if (headerRow != null) {
				for (int i = 0; i < headerRow.getLastCellNum(); i++) {
					Cell cell = headerRow.getCell(i);
					if (cell != null && cell.getCellType() == CellType.STRING) {
						String columnName = cell.getStringCellValue().trim();
						if (columnName.equalsIgnoreCase(employeeNumberColumnName)) {
							employeeNumberColumnIndex = i;
						} else if (columnName.equalsIgnoreCase(bookedHoursColumnName)) {
							bookedHoursColumnIndex = i;
						}
					}
				}
			}

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					Cell tescoIdCell = row.getCell(employeeNumberColumnIndex);
					if (tescoIdCell != null && tescoIdCell.getCellType() == CellType.STRING) {
						String tescoId = tescoIdCell.getStringCellValue();
						System.out.println(tescoId);
						if (tescoIds.contains(tescoId) && !isExcludedProjectCode(projectCodeColumnName)) {
							Cell bookedHoursCell = row.getCell(bookedHoursColumnIndex);
							if (bookedHoursCell != null && bookedHoursCell.getCellType() == CellType.NUMERIC) {
								float bookedHours = (float) bookedHoursCell.getNumericCellValue();
								if (hoursMap.containsKey(String.valueOf(tescoId))) {
									bookedHours += hoursMap.get(String.valueOf(tescoId));
								}
								System.out.println(tescoId);
								hoursMap.put(tescoId, bookedHours);
								System.out.println(hoursMap);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return hoursMap;
	}
	
	
	private boolean isExcludedProjectNumber(String projectNumber) {
	    Set<String> excludedProjectNumbers = new HashSet<>(Arrays.asList(
	            "40-B797", "01-B797", "02-B797", "03-B797", "04-B797", "08-B797",
	            "09-B797", "102-B797", "104-B797", "14-B797"
	    ));
	    return excludedProjectNumbers.contains(projectNumber);
	}
	
	
	
	private boolean isExcludedProjectCode(String projectNumber) {
	    Set<String> excludedProjectNumbers = new HashSet<>(Arrays.asList(
	    		"W11000"
	           ));
	    return excludedProjectNumbers.contains(projectNumber);
	}
	

	private void insertIntoReconciliationTable(Long empId, Float difference,Float otlHours,Float timexHours,String weekNum, String periodNum,String yearNum) {
		Reconciliation reconciliation = new Reconciliation();
		reconciliation.setEmployeeNumber(empId);
		reconciliation.setDifferenceInHours(difference);
		reconciliation.setOtlBookedHours(otlHours);
		reconciliation.setTimexBookedHours(timexHours);
		reconciliation.setWeekNumber(weekNum);
		reconciliation.setPeriodName(periodNum);
		reconciliation.setYearNumber(yearNum);
		
		
		if(otlHours==timexHours) {
			reconciliation.setStatus("Matched");
		}else {
			reconciliation.setStatus("Not_Matched");
		}
		
		Optional< Employee> optionalEmployee=empRepo.findById(empId);;
		if(optionalEmployee.isEmpty()) {
			throw new EntityNotFoundException("employee with id"  + empId +  "does not exist.");
			
		}
		Employee employee=optionalEmployee.get();
		String empName= employee.getEmpName();
		reconciliation.setEmpName(empName);
		reconRepo.save(reconciliation);
	}
	
	
	
	
	
	
	
	
	
	
	

}
