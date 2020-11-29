package base;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;
import org.json.JSONTokener;

import base.obj.GridSquare;
import javafx.scene.paint.Color;

public class Utils {
	

	public static final String OS = System.getProperty("os.name").toLowerCase();	// get current operating system
	public static final boolean WINDOWS = !OS.equals("linux");
	
	public static final String PATH_ROOT = System.getenv(WINDOWS ? "APPDATA" : "HOME") + "/Track of thought/";
	public static final String PATH_LEVELS = PATH_ROOT + "levels/";
	public static final String PATH_LEVELS_CUSTOM = PATH_LEVELS + "custom/";
	public static final String[] PATHS_TO_LOAD = {PATH_ROOT, PATH_LEVELS};

	public final static Color COLOR_LEVEL = Color.web("#282d33");
	public final static Color COLOR_ACCENT = Color.web("#C7B59D");
	public final static Color COLOR_BACKGROUND = Color.web("#2D2E37");
//	public final static Color COLOR_BACKGROUND = Color.web("#363638");

	public final static Color BLACK = Color.web("#101114");
	public final static Color RED = Color.web("#F44241");
	public final static Color GREEN = Color.web("#57E669");
	public final static Color BLUE = Color.web("#4487F3");
	public final static Color CYAN = Color.web("#79FFFA");
	public final static Color PINK = Color.web("#AA46F1");
	public final static Color YELLOW = Color.web("#EBF14A");
	
	public final static List<Color> COLORS_BASE = Arrays.asList(new Color[] {
			RED, GREEN, BLUE, CYAN, PINK, YELLOW
	});
	public final static List<String> COLORS_BASE_STR = Arrays.asList(new String[] {
			"red", "green", "blue", "cyan", "yellow", "pink"
	});
	public final static List<String> COLORS_BORDER_STR = Arrays.asList(new String[] {
			"red + o", "green + o", "blue + o", "cyan + o", "yellow + o", "pink + o"
	});
	public final static List<String> COLORS_STR = Arrays.asList(new String[] {
			"red", "green", "blue", "cyan", "yellow", "pink",
			"red + o", "green + o", "blue + o", "cyan + o", "yellow + o", "pink + o"
	});
			
	public static List<String> getRandomColors(int amount, List<String> toExclude, boolean prioritizeBase) {
		final List<String> newColors = new ArrayList<>();
		final List<String> allColors = new ArrayList<>(COLORS_STR);
			allColors.removeAll(toExclude);
			
		if(prioritizeBase) {
			final List<String> baseColors = new ArrayList<>(allColors);
				baseColors.removeIf(color -> color.contains("+"));
			newColors.addAll(baseColors);
			
			if(newColors.size() < amount) {
				allColors.removeAll(baseColors);
				Random r = new Random();
				
				while(newColors.size() < amount) {
					final int index = r.nextInt(allColors.size());
					newColors.add(allColors.get(index));
					allColors.remove(index);
				}
			}
		}
		return newColors;
	}
	
	/* fallback for different arguments */
	
	public static List<String> getRandomColors(int amount, boolean prioritizeBase) {
		return getRandomColors(amount, new ArrayList<String>(), prioritizeBase);
	}
	
	public static List<String> getRandomColors(int amount, List<String> toExclude) {
		return getRandomColors(amount, toExclude, false);
	}
	
	public static List<String> getRandomColors(int amount) {
		return getRandomColors(amount, new ArrayList<String>(), false);
	}
			
	/* returns integer direction from a string value */
	public static int parseDirectionToInt(String dir) {
		switch(dir) {
			case "top":
				return 0;
			case "right":
				return 1;
			case "bottom":
				return 2;
			case "left":
				return 3;
			default:
				Log.error("Wrong direction");
				return -1;
		}
	}
	
	/* returns array with color name and boolean value of border */
	public static Object[] parseColorWithBorder(String colorName) {
		Color color = parseColorName(colorName);
		return new Object[] {color, colorName.contains("+")};
	}
	
	/* returns color from a string value */
	public static Color parseColorName(String colorName) {
		/* if the given color string has a border information, strip it to colorname-only string */
		if(colorName == null) {
			return null;
		}
		if(colorName.contains("+")) {
			colorName = colorName.split("\\+")[0].trim();
		}
		switch(colorName.toUpperCase()) {
			case "BLACK":
				return BLACK;
			case "RED":
				return RED;
			case "GREEN":
				return GREEN;
			case "BLUE":
				return BLUE;
			case "CYAN":
				return CYAN;
			case "PINK":
				return PINK;
			case "YELLOW":
				return YELLOW;
			default:
				return Color.TRANSPARENT;
		}
	}
	
	/* returns (x from column) or (y from row) */
	public static int getXYFromRowCol(int rowcol) {
		return (rowcol + 1)*50;
	}
	
	/* returns (column from x) or (row from y) */
	public static int getColRowFromXY(int xy) {
		return xy/50-1;
	}
	
	/* same for double argument */
	public static int getColRowFromXY(double xy) {
		return getColRowFromXY((int) xy);
	}
	
	/* returns list of files in resources folder + relative path */
	public static List<String> getAllResourceFiles(String path) {
		List<String> filenames = new ArrayList<>();

		try {
            InputStream in = Scenes.class.getResourceAsStream("/resources/" + path);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
	        String resource;
	        while ((resource = br.readLine()) != null) {
	            filenames.add(resource);
	        }
		} catch (Exception e) {
			Log.error(e.toString());
		}
	    return filenames;
	}
	
	/* returns full grid */
	public static GridSquare[][] getGrid() {
		GridSquare[][] grid = new GridSquare[15][9];
		for(int i=0; i<15; i++) {
			for(int j=0; j<9; j++) {
				grid[i][j] = new GridSquare(i, j);
			}
		}
		return grid;
	}
	
	/* returns all keys for given json object */
	public static List<String> getAllJsonKeys(JSONObject json) {
		List<String> keys = new ArrayList<String>();
		Map<String, Object> mapObject = json.toMap();
				
        for (Map.Entry<String, Object> entry : mapObject.entrySet()) {
            keys.add(entry.getKey());
        }
        
        Collections.sort(keys);
        return keys;
	}
	
	/* returns json structure from file */
	public static JSONObject getJsonFromFile(String path) {
		InputStream stream = Scenes.class.getResourceAsStream(path);
		return new JSONObject(new JSONTokener(stream));
	}
	
	/* returns all values for a given key from a given object */
	public static List<Object> getAllJsonValues(JSONObject json, String key) {
		return json.getJSONArray(key).toList();
	}

	public static void createFolder(String path) {
		new File(path).mkdir();
	}
	
	public static boolean fileExists(String name) {
		return new File(name).exists();
	}
}
