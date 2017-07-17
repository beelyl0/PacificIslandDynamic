package com.pacificisland;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Islands {

	private static final char ISLAND_SYM = '*';

	private static final char SEA_SYM = '~';

	private int[][] area;
		
	private String inputFileName;
	
	private int islandsCount;
			
	Islands() {
		inputFileName = "input.txt";
	}
	
	Islands(String inputFileName) {
		this.inputFileName = inputFileName;
	}


	public static void main(String[] args) {
		Islands islands = new Islands();
		try {
			islands.read();
		} catch (IOException e) {
			System.err.println("read input file error");
			System.exit(1);
		} catch (IllegalInputDataException e) {
			System.err.println("format input file error");
			System.exit(1);
		}
		System.out.println(islands.count());
	}
	
	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}
	
	public String getInputFileName() {
		return inputFileName;
	}

	public void read() throws IOException, IllegalInputDataException {
		Path file = Paths.get(inputFileName);
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.US_ASCII)) {
			String line = reader.readLine();
			String[] areaSizes = line.split(" ");
			int n = Integer.valueOf(areaSizes[0]);
			int m = Integer.valueOf(areaSizes[1]);
			area = new int[n + 2][m + 2];
			int i = 1;
			while ((line = reader.readLine()) != null) {
				int j = 1;
				for (char sym : line.toCharArray()) {
					if (sym == ISLAND_SYM) {
						area[i][j] = 1;
					} else if (sym != SEA_SYM) {
						throw new IllegalInputDataException();
					}
					++j;
				}
				++i;
			}
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			throw new IllegalInputDataException();
		}
	}
	
			
	public int count() {
		
		if (area == null) {
			return islandsCount;
		}
		
		islandsCount = 0;
				
		List<Zone> zones = new ArrayList<>();

		zones.add(new Zone(0));
		zones.add(new Zone(1));
		
		int zoneId = 2;
		
		int n = area.length - 1;
		int m = area[0].length - 1;
		
		for (int i = 1; i < n; i++) {
			for (int j = 1; j < m; j++) {
				if (area[i][j] == 1) {
					
					Zone leftZone = zones.get(area[i][j - 1]);
					Zone upZone = zones.get(area[i - 1][j]); 
					int left = leftZone.getId();
					int up = upZone.getId();
					
					if (left == up) {
						
						if (left == 0) {
							Zone newZone = new Zone(zoneId++);
							zones.add(newZone);
							newZone.joinCell(i, j);							
						} else {				
							leftZone.joinCell(i, j);
						}
						
					} else {
						
						if (left == 0) {							
							upZone.joinCell(i, j);							
						} else if (up == 0) {							
							leftZone.joinCell(i, j);							
						} else {
							
							if (left > up) {
								upZone.merge(leftZone).joinCell(i, j);
								zones.set(left, upZone);
							} else {
								leftZone.merge(upZone).joinCell(i, j);
								zones.set(up, leftZone);
							}
														
						}
					}
				}
			}
		}
				
		Map<Integer, Integer> id2count = new HashMap<>(); 
				
		for (Zone zone : zones) {
			id2count.put(zone.getId(), zone.getCellCount());
		}
		
		for (Integer count : id2count.values()) {
			if (count > 1) {
				++islandsCount;
			}
		}
		
		area = null;
		
		return islandsCount;		
	}
		
	private class Zone {
		private int id;
		private int cellCount;
		
		Zone(int id) {
			this.id = id;
		}
				
		int getId() {
			return id; 
		}
				
		int getCellCount() {
			return cellCount;
		}
				
		void joinCell(int i, int j) {
			++cellCount;
			area[i][j] = id;
		}
		
		Zone merge(Zone mergedZone) {
			cellCount += mergedZone.getCellCount();
			return this;			
		}
						

	}
		
}
