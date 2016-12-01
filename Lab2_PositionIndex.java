

import java.util.ArrayList;
import java.util.Arrays;


public class Lab2_PositionIndex {
	String[] myDocs;
	ArrayList<String> termList;
	ArrayList<ArrayList<DocId>> docLists;
	

	public Lab2_PositionIndex(String[] docs)
	{
		myDocs = docs;
		termList = new ArrayList<String>();
		docLists = new ArrayList<ArrayList<DocId>>();
		ArrayList<DocId> docList;
		for(int i=0;i<myDocs.length;i++){
			String[] tokens = myDocs[i].split(" ");
			String token;
			for(int j=0;j<tokens.length;j++){
				token = tokens[j];
				if(!termList.contains(token)){
					termList.add(token);
					docList = new ArrayList<DocId>();
					DocId doid = new DocId(i,j);
					docList.add(doid);
					docLists.add(docList);
				}
				else{ //existing term
					int index = termList.indexOf(token);
					docList = docLists.get(index);
					int k=0;
					boolean match = false;
					//search the postings for a document id, if match, insert a new position
					//number to the document id
					for(DocId doid:docList)
					{
						if(doid.docId==i)
						{
							doid.insertPosition(j);
							docList.set(k, doid);
							match = true;
							break;
						}
						k++;
					}
					//if no match, add a new document id along with the position number
					if(!match)
					{
						DocId doid = new DocId(i,j);
						docList.add(doid);
					}
				}
			}
		}
	}
	

	public String toString()
	{
		String matrixString = new String();
		ArrayList<DocId> docList;
		for(int i=0;i<termList.size();i++){
				matrixString += String.format("%-15s", termList.get(i));
				docList = docLists.get(i);
				for(int j=0;j<docList.size();j++)
				{
					matrixString += docList.get(j)+ "\t";
				}
				matrixString += "\n";
			}
		return matrixString;
	}
	
	
	public ArrayList<DocId> intersect(ArrayList<DocId> l1, ArrayList<DocId> l2)
	{
		ArrayList<DocId> mergedList = new ArrayList<DocId>();
		int id1=0, id2=0;
		while(id1<l1.size()&&id2<l2.size()){
			//if both terms appear in the same document
			if(l1.get(id1).docId==l2.get(id2).docId){
				//get the position information for both terms
				ArrayList<Integer> pp1 = l1.get(id1).positionList;
				//System.out.println(pp1);
				ArrayList<Integer> pp2 = l2.get(id2).positionList;
				//System.out.println(pp2);
				int pid1 =0, pid2=0;
				while(pid1<pp1.size()){
					boolean match = false;
					while(pid2<pp2.size()){
						//if the two terms appear together, we find a match
						if((Math.abs(pp1.get(pid1)-pp2.get(pid2))<=1) && pp1.get(pid1)<pp2.get(pid2)){
							match = true;
							mergedList.add(l2.get(id2));
							break;
						}
						else if(pp2.get(pid2)>pp1.get(pid1))
							break;
						pid2++;
					}
					if(match) //if a match if found, the search for the current document can be stopped
						break;
					pid1++;
				}
				id1++;
				id2++;
			}
			else if(l1.get(id1).docId<l2.get(id2).docId)
				id1++;
			else
				id2++;
		}		
		return mergedList;
	}
	
	
	public ArrayList<DocId> phraseQuery1(String query){
		int index = termList.indexOf(query);
		if(index <0)
			return null;
		return docLists.get(index);
	}

	public ArrayList<DocId> phraseQuery(String[] query)
	{
		
		ArrayList<DocId> result2 = null;
		ArrayList<DocId> result = phraseQuery1(query[0]); 
		ArrayList<DocId> newresult = result;
		int termId = 1;
		while(termId<query.length){
			ArrayList<DocId> result1 = phraseQuery1(query[termId]);
			//System.out.println("Term 2 posting:"+result1);
			if(newresult != null && result1!=null){
				result2 = intersect(newresult, result1);

				newresult = result2;
				//System.out.println("Result2"+result2);
			}
			else{
				return null;
			}
			termId++;
		}
		return result2;
	}

	
	public static void main(String[] args)
	{
		String[] docs = {"new home sales top forecasts",
						 "home sales rise in july",
						 "increase in home sales in july",
						 "july new home sales rise"
						};
		Lab2_PositionIndex pi = new Lab2_PositionIndex(docs);
		System.out.print(pi);
		
			System.out.println("\nTest 1");
			String[] query1a = {"new","home"};  
			ArrayList<DocId> result = pi.phraseQuery(query1a);
			if(result!=null && !result.isEmpty())	{
				System.out.println("The term "+ Arrays.toString(query1a) +" occurs in following documents :");
				for(int j=0;j<result.size();j++)
					System.out.println(result.get(j).docId +": "+docs[result.get(j).docId]);
			}
			else{ 
				System.out.println("The term "+ Arrays.toString(query1a) +" does not occure in any of the documents");
			}
			
			System.out.println("\nTest 2");
			String[] query2 = {"home","new"};  
			ArrayList<DocId> result2 = pi.phraseQuery(query2);
			if(result2!=null && !result2.isEmpty())	{
				System.out.println("The term "+ Arrays.toString(query2) +" occurs in following documents :");
				for(int j=0;j<result2.size();j++)
					System.out.println(result2.get(j).docId +": "+docs[result2.get(j).docId]);
			}
			else{ 
				System.out.println("The term "+ Arrays.toString(query2) +" does not occure in any of the documents");
			}
			
			System.out.println("\nTest 3");
			String[] query3 = {"new","home","sales","top"};  
			ArrayList<DocId> result3 = pi.phraseQuery(query3);
			if(result3!=null && !result3.isEmpty())	{
				System.out.println("The term "+ Arrays.toString(query3) +" occurs in following documents :");
				for(int j=0;j<result3.size();j++)
					System.out.println(result3.get(j).docId +": "+docs[result3.get(j).docId]);
			}
			else{ 
				System.out.println("The term "+ Arrays.toString(query3) +" does not occure in any of the documents");
			}
			
			System.out.println("\nTest 4");
			String[] query4 = {"new","knowledge"};  
			ArrayList<DocId> result4 = pi.phraseQuery(query4);
			if(result4!=null && !result4.isEmpty())	{
				System.out.println("The term "+ Arrays.toString(query4) +" occurs in following documents :");
				for(int j=0;j<result4.size();j++)
					System.out.println(result4.get(j).docId +": "+docs[result4.get(j).docId]);
			}
			else{ 
				System.out.println("The term "+ Arrays.toString(query4) +" does not occure in any of the documents");
			}
			
		}
	}


/**
 * 
 * Document id class that contains the document id and the position list
 */
class DocId{
	int docId;
	ArrayList<Integer> positionList;
	public DocId(int did)
	{
		docId = did;
		positionList = new ArrayList<Integer>();
	}
	public DocId(int did, int position)
	{
		docId = did;
		positionList = new ArrayList<Integer>();
		positionList.add(new Integer(position));
	}
	
	public void insertPosition(int position)
	{
		positionList.add(new Integer(position));
	}
	
	
	public String toString()
	{
		String docIdString = ""+docId + ":<";
		for(Integer pos:positionList)
			docIdString += pos + ",";
		docIdString = docIdString.substring(0,docIdString.length()-1) + ">";
		return docIdString;		
	}
}

