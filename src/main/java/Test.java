import com.zdx.producer.TickerProducer;
import com.zdx.tri.TickerIndexBuilder;
import com.zdx.tri.TriListBuilder;

public class Test {
	public static void main(String[] args) {

		runProducer();
	}
	
	public static void run1(){
		String pairPath = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\t1.json";
		String triPath = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\t2.json";
		String tickerIndexPath = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\t3.json";
		//TriListBuilder.buildTriListFromPairFile(pairPath, triPath);
		TickerIndexBuilder tib = new TickerIndexBuilder();
		tib.buildIndexFromFile(triPath);
		tib.saveToFile(tickerIndexPath);
	}
	
	public static void runProducer(){
		String confPath = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\TickerProducer.conf";
		try {
			TickerProducer.execute(confPath);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
