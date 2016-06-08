package zadanierekrutacyjne;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

/**
 * 
 * @author danielobrebski
 * Class that opens file and do the calculation.
 */
public class Adder implements Runnable
{	

	private BigDecimal invoiceSum = new BigDecimal(0.00d); //invoiceSum is a BigDecimal to do proper addition
	private BufferedReader bf;
	private Integer recordCount = 0; //record counter
	private final LinkedBlockingQueue<File> inputQueue;
	private final LinkedBlockingQueue<File> outputQueue;
	
	
	/**
	 * Function that open file and read line by line
	 * @param f input file
	 * @throws IOException throwing IOException
	 */
	private void openFileAndReadData(File f) throws IOException
	{
		String line;
		try 
		{
			bf = new BufferedReader(new FileReader(f));
			while((line = bf.readLine()) != null)
			{
				invoiceSum = invoiceSum.add(getLineAmount(line));
			}	
		} 
		catch (FileNotFoundException e) 
		{
			out.println("File does not exist.");
			e.printStackTrace();
		} 
		catch (Exception e)
		{
			out.println("Another error.");
			e.printStackTrace();
		}
	}
	
	/**
	 * File that gets PLN value from line
	 * @param line Line from input File
	 * @return Value of the line
	 */
	private BigDecimal getLineAmount(String line)
	{
		BigDecimal lineValue = null;
		Matcher valueMatcher = Pattern.compile("amount:" + "(.*?)" + "PLN").matcher(line); //match amount(value)PLN

		if(valueMatcher.find())
		{
			try
			{
				recordCount++;
				lineValue = new BigDecimal(valueMatcher.group(1).replace(",", "."));
				assert(lineValue.doubleValue() < 0); //assertion if value < 0
			} 
			catch (NumberFormatException e)
			{
				out.println("Data format mismatch.");
				lineValue = new BigDecimal(0.00d);
			}
		}
		else
		{
			lineValue = new BigDecimal(0.00d);
		}

		return lineValue;
	}
	
	/**
	 * Function that returns total sum
	 * @return total sum
	 */
	public BigDecimal getTotalSum()
	{
		invoiceSum.setScale(2, BigDecimal.ROUND_HALF_UP);
		return invoiceSum;
	}
	
	/**
	 * Constructor of Helper Class
	 * @param inputQueue Queue with files to input
	 * @param outputQueue Queue with files to output
	 */
	public Adder(LinkedBlockingQueue<File> inputQueue, LinkedBlockingQueue<File> outputQueue)
	{
		this.inputQueue = inputQueue;
		this.outputQueue = outputQueue;
	}
	
	/**
	 * Function implemented to run Thread
	 * Thread is terminated when file in inputQueue does not exists
	 */
	public void run() 
	{
		while(true)
		{
			try 
			{
				File f = inputQueue.take(); //take file from the inputqueue
				if(!f.exists()) //empty file is a poison pill that stop the read, Thread.stop() is deprecated
				{
					break;
				}
				openFileAndReadData(f);
				
				out.printf("Number of records finded: %d\nSum: %.2f PLN\n", recordCount, getTotalSum());
				outputQueue.put(f);	//put to the output queue, another thread can take it	
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
}

