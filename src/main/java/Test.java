import com.zdx.tri.TickerIndexBuilder;
import com.zdx.tri.TriListBuilder;

public class Test {
	public static void main(String[] args) {

		String pairPath = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\t1.json";
		String triPath = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\t2.json";
		String tickerIndexPath = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\t3.json";
		//TriListBuilder.buildTriListFromPairFile(pairPath, triPath);
		TickerIndexBuilder tib = new TickerIndexBuilder();
		tib.buildIndexFromFile(triPath);
		tib.saveToFile(tickerIndexPath);
	}
}
