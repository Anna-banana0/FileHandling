
public class ExhibitMonitor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileMonitor fileMonitor = new FileMonitor(MyConstants.INPUTDIR, MyConstants.INPROCESS_DIR);
		fileMonitor.start();
	}

}
