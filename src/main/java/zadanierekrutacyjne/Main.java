package zadanierekrutacyjne;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.System.out;

/**
 * Main - Tester class, Program can be extended to support multi thread programming.
 * If you want to do something with file, get it from outputQueue.
 * @author danielobrebski
 *
 */
public class Main 
{

	/**
	 * Main function
	 * @param args can have a file path in first element
	 */
	public static void main(String[] args) 
	{
		File f;
		final LinkedBlockingQueue<File> inputQueue = new LinkedBlockingQueue<File>(); //File synchronization between threads, that could be implemented
		final LinkedBlockingQueue<File> outputQueue = new LinkedBlockingQueue<File>();
		Adder helper = new Adder(inputQueue, outputQueue);
		
		if(args.length > 0)
		{
			f = new File(args[0]);
			if(!f.exists()) //if file does not exist, program takes data from project folder
			{
				f = new File("src/main/resources/Plik z danymi.txt");
			}
		}
		else
		{
			f = new File("src/main/resources/Plik z danymi.txt");
		}
		
		showInfo();
		
		try 
		{
			inputQueue.put(f); //put file to the input queue
			Thread helperThread = new Thread(helper); //create new thread
			helperThread.start();
			inputQueue.put(new File("")); //terminating the thread, by sending file that not exists
			helperThread.join();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Function that shows info about program
	 */
	public static void showInfo()
	{
		out.println("*******************************");
		out.println("Program that counts sum of amount.");
		out.println("Daniel Obrebski");
		out.println("*******************************");
	}
}

