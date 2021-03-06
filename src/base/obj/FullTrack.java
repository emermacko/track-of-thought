package base.obj;

import java.util.ArrayList;
import java.util.List;

public class FullTrack {
	
	private List<Station> stations;
	private List<Track> tracks;
	private List<Ball> balls;
	private final List<String> usedColors = new ArrayList<>();
	private List<Ball> activeBalls = new ArrayList<>();
	private List<String> activeBallColors = new ArrayList<>();
	
	public FullTrack(List<Station> stations, List<Track> tracks, List<Ball> balls) {
		this.stations = stations;
		this.tracks = tracks;
		this.balls = balls;
		
		/* Sets usedColors to colors from all stations // run loop only once instead of creating a getter */
		for(Station s : stations) {
			if(!s.isStart()) {
				String color = s.getColorStr();
				if(s.hasBorder()) {
					color += " + o"; 
				}
				usedColors.add(color);
			}
		}
	}
	
	public List<Ball> getActiveBalls() {
		return activeBalls;
	}
	
	public List<String> getActiveBallColors() {
		return activeBallColors;
	}
	
	public void addActiveBall(Ball b) {
		activeBalls.add(b);
		activeBallColors.add(b.getColorStr());
	}
	
	public void removeActiveBall(Ball b) {
		activeBalls.remove(b);
		activeBallColors.remove(b.getColorStr());
	}
	
	/* returns x mostRecent balls or up to 0 index */
	public List<Ball> getMostRecentBalls(int amount) {
		List<Ball> recentBalls = new ArrayList<>();
		
		Ball mostRecent = null;
		for(Ball b : balls) {
			if(b.getColor() == null) {
				break;
			}
			mostRecent = b;
		}
					
		final int mostRecentIndex = balls.indexOf(mostRecent);
		final int leastRecentIndex = mostRecentIndex - amount + 1;
				
		int i = mostRecentIndex;
		while(i>=0 && i>=leastRecentIndex) {
			recentBalls.add(balls.get(i--));
		}
		
		return recentBalls;
	}
	
	public Station getStartStation() {
		for(Station s : stations) {
			if(s.isStart()) {
				return s;
			}
		}
		return null;
	}
	
	public Track findTrack(int col, int row) {
		for(Track track : tracks) {
			if(track.getColumn() == col && track.getRow() == row) {
				return track;
			}
		}
		return null;
	}
	
	public Station findStation(int col, int row) {
		for(Station station : stations) {
			if(station.getColumn() == col && station.getRow() == row) {
				return station;
			}
		}
		return null;
	}
	
	public List<String> getUsedColors(){
		return usedColors;
	}
	
	public Track getNextTrack(Track current) {
		int nextCol = current.getNextTrackColumn();
		int nextRow = current.getNextTrackRow();
		
		return findTrack(nextCol, nextRow);
	}
	
	public Station getCurrentEndStation() {
		int[] firstTrackColRow = getStartStation().getFirstTrackColRow();
		int col = firstTrackColRow[0], row= firstTrackColRow[1];
		
		Track currentTrack = findTrack(col, row);
		Track nextTrack = getNextTrack(currentTrack);
		

		while(true) {
			nextTrack = getNextTrack(currentTrack);
			if(nextTrack == null) {
				col = currentTrack.getNextTrackColumn();
				row = currentTrack.getNextTrackRow();
				break;
			}
			currentTrack = nextTrack;
		}
		
		return findStation(col, row);
	}
	
	public List<Ball> getBalls() {
		return this.balls;
	}
	
	public List<Station> getStations() {
		return this.stations;
	}
	
	public List<Track> getTracks() {
		return this.tracks;
	}
}
