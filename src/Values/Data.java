package Values;

public class Data {

	public int total = 0;
	public double[] x;
	public double[] y;
	
	private static Data instance = null;
	private Data(){}
	
	public static synchronized Data getInstance() {
		if (instance == null) {
			instance = new Data();
		}
		return instance;
	}
}
