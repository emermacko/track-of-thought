package base.obj;

import java.util.List;

import base.Log;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

import static base.Utils.getColRowFromXY;

public class Ball extends Circle {
	
	private final static Track NULL_TRACK = null;
	
	private int index = 0;
	private int column;
	private int row;
	private int delay;
	private String color;
	private List<Track> tracks;
	private Track currentTrack;
	private boolean border;
	
//	private double[][] finalTrackPath;
	private int finalCounter = 0;
	private int finalDirection;
	private boolean finalStation = false;
	
	
	public Ball(double[] xy, Color fill, List<Track> tracks, int delay, boolean border) {
		this((int)xy[0], (int)xy[1], 10, fill, tracks, delay, border);
	}
	
	public Ball(int x, int y, int radius, Color color, List<Track> tracks, int delay, boolean border) {
		super(border ? radius-4 : radius);	// r - padding for border
		
		this.column = getColRowFromXY(x);
		this.row = getColRowFromXY(y);
		this.color = color.toString();
		this.tracks = tracks;
		this.delay = delay;
		this.border = border;
		
		setCenterX(x + 15);
		setCenterY(y + 25);
		setFill(color);
		getStyleClass().add("ball");
		
		/* if should have border, add it */
		if(border) {
			setStroke(Color.rgb(255, 255, 255, .8));
			setStrokeType(StrokeType.OUTSIDE);
			setStrokeWidth(4);
		}
		
		/* event listeners to change track when ball above it is clicked */
		setOnMouseEntered(e -> modifyTrackOnHover());
		setOnMouseMoved(e -> modifyTrackOnHover());
		setOnMouseClicked(e -> {
			if(currentTrack != null && currentTrack.isClickable()) {
				currentTrack.changeType();
			}
		});
		
		/* reset stuff when user hovers out */
		setOnMouseExited(e -> {
			setCursor(Cursor.DEFAULT);
			if(currentTrack != null) {
				currentTrack.setId("");
			}
			this.currentTrack = null;
		});
	}
		
	/* all the logic behind the ball's movement */
	public void update(FullTrack nodes) {
		Track track = nodes.findTrack(column, row);		// get the track that ball is on
				
		/* if the station is next track */
		if(finalStation) {
			moveNextDirection(finalDirection);	// calc where ball should move
				finalCounter++;					// move it 25px total into station
			return;
		}
		
		/* start of the game */
		if(track == null) {
			Station startStation = nodes.getStations().get(0);	// get starting station
			if(startStation != null) {
				moveNextDirection(startStation.getExit());		// move towards its exit
			}
			return;
		}
				
		double[][] path = track.getPath();	// get path xy coordinates from current track

		/* if the ball is on last pixel of the path */
		if(index == path[0].length-1) {	
			int nextCol = track.getNextTrackColumn();	// get next column
			int nextRow = track.getNextTrackRow();		// get next row
			
			track = nodes.findTrack(nextCol, nextRow);	// get a corresponding track
			
			/* if there is no next track available */
			if(track == null) {
				finalStation = true;									// set the flag
				finalDirection = calcFinalDirection(nextCol, nextRow);	// calculate direction
				return;
			}
			path = track.getPath();			// overwrite path with new one					
			column = track.getColumn();		// overwrite ball column
			row = track.getRow();			// overwrite ball row
			index = -1;						// reset index
		}
		
		/* get new ball coordinates and move to them */
		double x = path[0][index+1];
		double y = path[1][index+1];
		setCenterX(x);
		setCenterY(y);
		index++;
	}
			
	public String getColor() {
		return color;
	}
	
	public boolean getBorder() {
		return border;
	}
	
	public int getCounter() {
		return this.finalCounter;
	}
	
	public int getColumn() {
		return getColRowFromXY(getCenterX());
	}
	
	public int getRow() {
		return getColRowFromXY(getCenterY());
	}
	
	public int getDelay() {
		return delay;
	}
	
	/* check if ball is currently on clickable track */
	private Track getCurrentTrack() {
		for(Track track : tracks) {
			if(track.getColumn() == getColumn() && track.getRow() == getRow()) {
				return track;
			}
		}
		return NULL_TRACK;
	}
	
	/* returns a direction the ball should move at the beginning */
	private void moveNextDirection(int direction) {
		switch(direction) {
			case 0:
				setCenterY(getCenterY() - 1);
				break;
			case 1:
				setCenterX(getCenterX() + 1);
				break;
			case 2:
				setCenterY(getCenterY() + 1);
				break;
			case 3:
				setCenterX(getCenterX() - 1);
				break;
			default:
				Log.error("Wrong direction");
		}
		column = getColumn();
		row = getRow();
	}
	
	/* returns a final direction the ball should move when 'parking' on the station */
	private int calcFinalDirection(int nextCol, int nextRow) {
		if(nextCol > column) {
			return 1;
		}
		if(nextCol < column) {
			return 3;
		}
		if(nextRow > row) {
			return 2;
		}
		if(nextRow < row) {
			return 0;
		}
		return -1;
	}
	
	/* when ball is hovered while being on a changable track modifies that track */
	private void modifyTrackOnHover() {
		Track currentTrack = getCurrentTrack();
		this.currentTrack = currentTrack;
		
		/* if the ball is over a clickable track allow to change it */
		if(currentTrack != null && currentTrack.isClickable()) {
			setCursor(Cursor.HAND);
			currentTrack.setId("ball-hovered");
		}
	}
}
