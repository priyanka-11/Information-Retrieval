
import java.util.*;

//priyanka
class BTNode{
   BTNode left, right;
   String term;
   ArrayList<Integer> docLists;
	
	/**
	 * Create a tree node using a term and a document list
	 * @param term the term in the node
	 * @param docList the ids of the documents that contain the term
	 */
   public BTNode(String term, ArrayList<Integer> docList)
      {
      this.term = term;
      this.docLists = docList;
      }
	
   }

/**
 * Binary search tree structure to store the term dictionary
 */
public class BinaryTree {
	
	int newInt;
   private static BTNode root;
   public static ArrayList<BTNode> searchList =new ArrayList<BTNode>();
	
	/**
	 * insert a node to a subtree 
	 * @param node root node of a subtree
	 * @param iNode the node to be inserted into the subtree
	 */
	
   public void add(BTNode node, BTNode iNode)
      {  
       //check if root is present or not
      if (root == null) { 
         root = node;
         return;
         }
      else if(iNode.term.equals(node.term)){
			if(!node.docLists.contains(iNode.docLists.get(0))){
				node.docLists.add(iNode.docLists.get(0));
			}
			return;
		}
      while (true) {
         	//checking for left child node
         if (node.term.compareTo(iNode.term) > 0) { 
            if (node.left != null) node = node.left;
            else {
               node.left = iNode;
               break;
               }
            }
         else 
            //checking for right child node
            if (node.term.compareTo(iNode.term) < 0) { 
               if (node.right != null) 
                  node = node.right;
               else{ 
                  node.right = iNode; 
                  break; 
                  }
               }
            else 
               break; 
         }
      }
	
	/**
	 * Search a term in a subtree
	 * @param n root node of a subtree
	 * @param key a query term
	 * @return tree nodes with term that match the query term or null if no match
	 */
   public BTNode search(BTNode n, String key)
      {
	 //TO BE COMPLETED
	 try{
	 	
	 		if(n.term.equals(key)==true){
	 				return n;
	 			}
	 			else if(n.term.compareTo(key)<0){
	 				return search(n.right,key);
	 			}
	 			
	 			else if(n.term.compareTo(key)>0){
	 				return search(n.left,key);
	 			}
	 			else{
	 				return null;
	 			}
	 		}
	 		catch(NullPointerException e) {
	 			return null;
	 		}		   
      }
	
	/**
	 * Do a wildcard search in a subtree
	 * @param n the root node of a subtree
	 * @param key a wild card term, e.g., ho (terms like home will be returned)
	 * @return tree nodes that match the wild card
	 */
   public ArrayList<BTNode> wildCardSearch(BTNode n, String key)
      {
	   try{
	   ArrayList<BTNode> resultList = new ArrayList<BTNode>();
		if(n.term.startsWith(key)){
			resultList.add(n);
		}
		else if(n.term.compareTo(key)>0 && n.left!=null){
			resultList.addAll(wildCardSearch(n.left,key));
		}
		else if(n.term.compareTo(key)<0 && n.right!=null){
			resultList.addAll(wildCardSearch(n.right,key));
		}
		
		return resultList;
	   }
	   catch(NullPointerException e) {
			return null;
		}	
      }
	
	/**
	 * Print the inverted index based on the increasing order of the terms in a subtree
	 * @param node the root node of the subtree
	 */
   public void printInOrder(BTNode node)
      {
      if (node != null) {
         printInOrder(node.left);
         System.out.println(node.term + " "+node.docLists);
         printInOrder(node.right);
         }
      }

	
   }

