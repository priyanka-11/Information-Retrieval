
import java.util.*;

//priyanka


public class BTreeIndex {
    String[] myDocs;
    BTNode root;
    BinaryTree termList;
   ArrayList<String> termLists;
    ArrayList<Integer> docList;
    ArrayList<ArrayList<Integer>> docLists;
    ArrayList<BTNode> searchList;
	
	/**
	 * Construct binary search tree to store the term dictionary 
	 * @param docs List of input strings
	 * 
	 */
   public BTreeIndex(String[] docs)
      {
   	//TO BE COMPLETED
   	
      BTNode node=null;
      myDocs = docs;
      termList = new BinaryTree();
      termLists = new ArrayList<String>();
      docLists = new ArrayList<ArrayList<Integer>>();
      docList = new ArrayList<Integer>();
   	
   	
      for(int i=0;i<myDocs.length;i++){
         String[] tokens=myDocs[i].split(" ");
         for(String token: tokens) {
            if(!termLists.contains(token)) {
               termLists.add(token);
               docList = new ArrayList<Integer>();
               docList.add(new Integer(i));
               docLists.add(docList);
               }
            else {
               int index = termLists.indexOf(token);
               docList = docLists.get(index);
               if(!docList.contains(new Integer(i)))
                  docList.add(new Integer(i));
               }
            }
         }

      for(int i=0;i<termLists.size();i++){
         if(i==0)
            root=new BTNode(termLists.get(i),docLists.get(i));
         else
            node=new BTNode(termLists.get(i),docLists.get(i));
         termList.add(root, node);
         }
      }
	
	/**
	 * Single keyword search
	 * @param query the query string
	 * @return doclists that contain the term
	 */
   public ArrayList<Integer> search(String query)
      {
      BTNode node = termList.search(root, query);
      if(node==null)
         return null;
         // System.out.println(node.docLists);
      return node.docLists;
      }
	
	/**
	 * conjunctive query search
	 * @param query the set of query terms
	 * @return doclists that contain all the query terms
	 */
   public ArrayList<Integer> search(String[] query)
      {
      ArrayList<Integer> result = search(query[0]);
      int termId = 1;
      while(termId<query.length)
		{
			ArrayList<Integer> result1 = search(query[termId]);
			if (result != null && result1!=null){
				result = merge(result,result1);
			}
			else {
				return null;
			}
			termId++;
		}		
      return result;
      }
	
	/**
	 * 
	 * @param wildcard the wildcard query, e.g., ho (so that home can be located)
	 * @return a list of ids of documents that contain terms matching the wild card
	 */
   public ArrayList<Integer> wildCardSearch(String wildcard)
   {
	 //TO BE COMPLETED
	   docList = new ArrayList<Integer>();
		searchList = termList.wildCardSearch(root, wildcard);
		for(BTNode bt:searchList){
			docList = search(docList,bt.docLists);
		}
		System.out.println(docList);
		return docList;
      }
	
	
   private ArrayList<Integer> merge(ArrayList<Integer> l1, ArrayList<Integer> l2)
      {
      ArrayList<Integer> mergedList = new ArrayList<Integer>();
      int id1 = 0, id2=0;
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
   
   public ArrayList<Integer> search(ArrayList<Integer> list1, ArrayList<Integer> list2){
		if(list1==null) {
			return list2;
		}
		ArrayList<Integer> list =new ArrayList<Integer>();
		int l1=0, l2=0;
		while(l1<list1.size()&&l2<list2.size())
		{
			if(list1.get(l1)<list2.get(l2))
				list.add(list1.get(l1++));
			else if(list1.get(l1)>list2.get(l2))
				list.add(list2.get(l2++));
			else {
				list.add(list2.get(l2++));
				l1++;
			}
		}
		while(l1<list1.size()){
			list.add(list1.get(l1++));
		}
		while(l2<list2.size()){
			list.add(list2.get(l2++));
		}
		return list;
	}
	
   @Override
   public String toString() {
      String outString=new String();
      for(int i=0;i<termLists.size();i++){
         outString+=String.format("%-15s",termLists.get(i));
         ArrayList<Integer> docList=docLists.get(i);
         for (int j=0;j<docList.size();j++){
            outString+=docList.get(j)+"\t";
            }
         outString+="\n";
         }
      return outString;
      }

   public static void main(String[] args)
      {
      String[] docs = {"new home sales top forecasts",
				 		"home sales rise in july",
				 		"increase in home sales in july",
				 		"july new home sales rise"
						};
      BTreeIndex b1=new BTreeIndex(docs);	
      
      b1.termList.printInOrder(b1.root);
      	//Query for single term
		String query1[]={"top"};
		ArrayList<Integer> result = b1.search(query1);
		//System.out.println(result);
		if(result!=null){
		System.out.println("The term '"+Arrays.toString(query1)+"' is found in documents:"+result);
		}
		else 
			System.out.println("No match!");
		
		//Query for multiple terms
		String query2[]={"home","sales"};
		ArrayList<Integer> result1 = b1.search(query2);
		if(result1!=null){
		System.out.println("The term '"+Arrays.toString(query2)+"' is found in documents:"+result1);
		}
		else 
			System.out.println("No match!");
		
      //Query for Wildcard search
		String query3 = "ju";
		ArrayList<Integer> result3 = b1.wildCardSearch(query3);
		//System.out.println(result);
		if(result3!=null){
		System.out.println("The term '"+query3+"' is found in documents:"+result3);
		}
		else 
			System.out.println("No match!");
   	
   		
      }
   }