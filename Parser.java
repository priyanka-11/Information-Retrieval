package parser_practice;

import java.util.*;
import java.io.*;
import java.lang.reflect.Array;

public class Parser {
	String[] myDocs;
	ArrayList<String> termList;
	//String[] stopList = {"a","is","in","so","of","at","the","to","and","it","as","be","are"};
	String line = null;
	ArrayList<ArrayList<Integer>> docLists; //set of postings//columns
	
	public Parser(String folderName)
	{
		String token1 = null;
		termList = new ArrayList<String>();
		docLists = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> docList;		//One particular document list
		File folder = new File(folderName);
		File[] listOfFiles = folder.listFiles();
		System.out.println(Arrays.toString(listOfFiles));
		myDocs = new String[listOfFiles.length];//stores filenames//we dont store actual content
		System.out.println(Arrays.toString(myDocs));
		for(int i=0;i<listOfFiles.length;i++)
		{
			//System.out.println("File " + listOfFiles[i].getName());
			myDocs[i] = listOfFiles[i].getName();
		}
		for(int i=0;i<myDocs.length;i++){
		String[] tokens = parse(folderName + "/" + myDocs[i]);
		//Arrays.sort(stopList);
		for(String token:tokens){
			Stemmer st = new Stemmer();
			st.add(token.toCharArray(),token.length());
			st.stem();
			token = st.toString();
			
			if(searchStopword(token)==-1)
				//System.out.println(token);
				token1 = token; 
			//System.out.println("Stemmed word "+query1a);
			
			if(!termList.contains(token1)){//a new term
				termList.add(token);	//add a new term,create new posting for that term						
				docList = new ArrayList<Integer>(); //and add document id to that postings
				docList.add(new Integer(i));  
				docLists.add(docList);
			}
			else{//an existing term
				int index = termList.indexOf(token1);	//go to that dictionary/term/row and
				docList = docLists.get(index);			//and update posting for that term
				if(!docList.contains(new Integer(i))){
					docList.add(new Integer(i));
					docLists.set(index, docList);
				}
			}
		}
		//stemming
		
	}
	}
	
	public String toString(){
		String matrixString = new String();
		ArrayList<Integer> docList;
		for(int i=0;i<termList.size();i++){
			matrixString += String.format("%-15s", termList.get(i));
			docList = docLists.get(i);
			for(int j=0;j<docList.size();j++)
				matrixString += docList.get(j) + "\t";
			matrixString += "\n";
		}
		return matrixString;
	}
	public ArrayList<Integer> search1(String[] query){
		ArrayList<Integer> result = search(query[0]);
		//System.out.println("Term 1 posting: "+result);
		int termId = 1;
		while(termId<query.length){
			//System.out.println(query[termId]);
			ArrayList<Integer> result1 = search(query[termId]);
			//System.out.println("Term 2 posting:"+result1);
			if(result != null && result1!=null){
			//result = merge(result, result1);
				result.removeAll(result1);
				result.addAll(result1);
			}
			else{
				return null;
			}
			termId++;
		}
		return result;
	}
	
	public ArrayList<Integer> search(String query){
		int index = termList.indexOf(query);
		if(index <0)
			return null;
		return docLists.get(index);
	}
	
	public ArrayList<Integer> search(String[] query){
		ArrayList<Integer> result = search(query[0]);
		//System.out.println("Term 1 posting: "+result);
		int termId = 1;
		while(termId<query.length){
			//System.out.println(query[termId]);
			ArrayList<Integer> result1 = search(query[termId]);
			//System.out.println("Term 2 posting:"+result1);
			if(result != null && result1!=null){
			result = merge(result, result1);
			}
			else{
				return null;
			}
			termId++;
		}
		return result;
	}
	
	// to find multiple keyword
	//for term 1 you have number of lists
	//for term 2 you have number of lists
	//here you will find intersection of two lists will have result of two terms
	//then you merge this list with list of term 3 and so on
	private ArrayList<Integer> merge(ArrayList<Integer> l1, ArrayList<Integer> l2){
		ArrayList<Integer> mergedList = new ArrayList<Integer>();
		int id1=0, id2=0;
		while(id1<l1.size()&&id2<l2.size()){
			if(l1.get(id1).intValue()==l2.get(id2).intValue()){
				mergedList.add(l1.get(id1));
				id1++;
				id2++;
			}
			else if(l1.get(id1)<l2.get(id2))
				id1++;
			else
				id2++;
		}
		return mergedList;
	}
	
	public String[] parse(String fileName)
	{
		String[] tokens = null;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String allLines = new String();
			String line = null;
			while((line=reader.readLine())!=null){
				allLines += line.toLowerCase(); //case folding
			}
			reader.close();
			tokens = allLines.split("[ '.,?!:;$%&*+()\\-\\^]+");
		
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		return tokens;
	}
	
	public int searchStopword(String key)	//strings are ordered in alphabetical order
	{
		try
		{
		BufferedReader stopwordFile = new BufferedReader(new FileReader("stopwords.txt"));
		List<String> data = new ArrayList<String>();
		
		String line = null;
		while((line=stopwordFile.readLine())!=null) {
		    data.add(line);
		    //System.out.println( line); 
		}
		stopwordFile.close();
		
		// If you want to convert to a String[]
		String[] stopList = data.toArray(new String[]{});
		//Arrays.sort(stopList);
		
		int lo = 0;
		int hi = stopList.length-1;
		while(lo<=hi)
		{
			//Key is in a[lo..hi] or not present
			int mid = lo + (hi-lo)/2;
			int result = key.compareTo(stopList[mid]);
			if(result <0) hi = mid - 1;
			else if(result >0) lo = mid+1;
			else return mid;
		}
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		return -1;
	}
	
	public static void main(String[] args)
	{
		Parser p = new Parser(args[0]);
		System.out.println(p);
		//System.out.println("Stopwords: " + p.searchStopword("in"));
		
		
		 String query1a = "stake";
		ArrayList<Integer> result1a = p.search(query1a);
		System.out.println(result1a);
		if(result1a!=null){
			System.out.println("\n Q1.A The term '"+ query1a +"' occurs in following documents :\n ");
			for(Integer i:result1a)
				System.out.println(p.myDocs[i.intValue()]);
		}
		else
			System.out.println("No match!");
	
		String query1b = "priyanka";
		ArrayList<Integer> result1b = p.search(query1b);
		if(result1b!=null){
			System.out.println("\n Q1.B The term '"+ query1b +"' occurs in following documents :\n ");
			for(Integer i:result1b)
				System.out.println(p.myDocs[i.intValue()]);
		}
		else
			System.out.println("\n Q1.B The term '"+ query1b +"' occurs in following documents :\n ");
			System.out.println("No match!");
	
		
		String[] query2a = {"teen","plot"};		// {"teen","plot","priyanka"};
		//System.out.println(query2aa);
		ArrayList<Integer> result2a = p.search(query2a);
		if(result2a!=null){
			System.out.println("\n Q2.A The term '"+ Arrays.toString(query2a) +"' occurs in following documents :\n ");
			for(Integer i:result2a)
				System.out.println(p.myDocs[i.intValue()]);
		}
		else
			System.out.println("No match!");
		
		String[] query2b = {"teen","priyanka"};		// {"teen","plot","priyanka"};
		//System.out.println(query2aa);
		ArrayList<Integer> result2b = p.search(query2b);
		if(result2b!=null){
			System.out.println("\n Q2.B The term '"+ Arrays.toString(query2b) +"' occurs in following documents :\n ");
			for(Integer i:result2b)
				System.out.println(p.myDocs[i.intValue()]);
		}
		else
			System.out.println("\n Q2.B The term '"+ Arrays.toString(query2b) +"' occurs in following documents :\n ");
			System.out.println("No match!");
		
		String[] query3a = {"teen","plot"};		// {"teen","plot","priyanka"};
		//System.out.println(query2aa);
		ArrayList<Integer> result3a = p.search1(query3a);
		if(result3a!=null){
			System.out.println("\n Q3.A The term '"+ Arrays.toString(query3a) +"' occurs in following documents :(OR)\n ");
			for(Integer i:result3a)
				System.out.println(p.myDocs[i.intValue()]);
		}
		else
			System.out.println("No match!");
		
		String[] query3b = {"teen","priyanka"};		// {"teen","plot","priyanka"};
		//System.out.println(query2aa);
		ArrayList<Integer> result3b = p.search1(query3b);
		if(result3b!=null){
			System.out.println("\n Q3.A The term '"+ Arrays.toString(query3b) +"' occurs in following documents :(OR)\n ");
			for(Integer i:result3b)
				System.out.println(p.myDocs[i.intValue()]);
		}
		else{
			System.out.println("\n Q3.A The term '"+ Arrays.toString(query3b) +"' occurs in following documents :(OR)\n ");
			System.out.println("No match!");
			}
		
		
		String[] query4a = {"teen","plot", "squad"};		// {"teen","plot","priyanka"};
		ArrayList<Integer> result4a = p.search(query4a);
		if(result4a!=null){
			System.out.println("\n Q4.A The term '"+ Arrays.toString(query4a) +"' occurs in following documents :\n ");
			for(Integer i:result4a)
				System.out.println(p.myDocs[i.intValue()]);
		}
		else
			System.out.println("No match!");
		
		String[] query4b = {"teen","plot", "priyanka"};		// {"teen","plot","priyanka"};
		ArrayList<Integer> result4b = p.search(query4b);
		if(result4b!=null){
			System.out.println("\n Q4.A The term '"+ Arrays.toString(query4b) +"' occurs in following documents :\n ");
			for(Integer i:result4b)
				System.out.println(p.myDocs[i.intValue()]);
		}
		else
			System.out.println("\n Q4.A The term '"+ Arrays.toString(query4b) +"' occurs in following documents :\n ");
			System.out.println("No match!");
		
		
		/*String word = "cementcement";
		Stemmer st = new Stemmer();
		st.add(word.toCharArray(),word.length());
		st.stem();
		word = st.toString();
		System.out.println("stemmed: " + word); */
	}

}

