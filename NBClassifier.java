
import java.io.*;
import java.util.*;


public class NBClassifier {
	String[] trainingDocs;				
	int[] trainingClasses;
	String[] testingDocs;				
	int[] testingClasses;
	int numClasses = 2;
	int[] classDocCounts; 				
	String[] classStrings; 				
	int[] classTokenCounts; 		
	ArrayList<Integer> trainlabels=new ArrayList<Integer>();
	ArrayList<Integer> testlabels=new ArrayList<Integer>();
	HashMap<String,Double>[] condProb;
	HashSet<String> vocabulary; 		
	ArrayList<String> trainfilenames=new ArrayList<String>();
	ArrayList<String> testfilenames=new ArrayList<String>();
	ArrayList<String> termList=new ArrayList<String>();
	int count=0;
	int total_positive;
	int total_negative;
	String filename;
	
	/**
	 * Build a Naive Bayes Classify using a training document set
	 * @param trainDataFolder the training document folder
	 */
	@SuppressWarnings("unchecked")
	public NBClassifier(String trainDataFolder)
	{
		preprocess(trainDataFolder);
		
		classDocCounts = new int[numClasses];
		classStrings = new String[numClasses];
		classTokenCounts = new int[numClasses];
		condProb = new HashMap[numClasses];
		vocabulary = new HashSet<String>();
		
		for(int i=0;i<numClasses;i++){
			classStrings[i] = "";
			condProb[i] = new HashMap<String,Double>();
		}
		
		//System.out.println(trainingLabels.length);
		for(int i=0;i<trainingClasses.length;i++){
			classDocCounts[trainingClasses[i]]++;
			classStrings[trainingClasses[i]] += (trainingDocs[i] + " ");
		}
		for(int i=0;i<numClasses;i++){
			String[] tokens = classStrings[i].split("[ .,?!:;$%()\"--'/\\t]+");
	
			classTokenCounts[i] = tokens.length;

			for(String token:tokens){
					vocabulary.add(token);
					if(condProb[i].containsKey(token)){
						double count = condProb[i].get(token);
						condProb[i].put(token, count+1);
					}
					else
						condProb[i].put(token, 1.0);
				}
			}
		
		//computing the class conditional probability
		for(int i=0;i<numClasses;i++){
			Iterator<Map.Entry<String, Double>> iterator = condProb[i].entrySet().iterator();
			int vSize = vocabulary.size();
			while(iterator.hasNext())
			{
				Map.Entry<String, Double> entry = iterator.next();
				String token = entry.getKey();
				Double count = entry.getValue();
				count = (count+1)/(classTokenCounts[i]+vSize);
				condProb[i].put(token, count);
			}
		}

	}

	
	/**
	 * Classify a test doc
	 * @param doc test doc
	 * @return class label
	 */
	public int classify(String doc){
		int label = 0;
		int vSize = vocabulary.size();
		double[] score = new double[numClasses];
		for(int i=0;i<score.length;i++){
			score[i] = Math.log(classDocCounts[i]*1.0/trainingDocs.length);
		}
		String[] tokens = doc.split("[ .,?!:;$%()\"--'/\\t]+");
		for(int i=0;i<numClasses;i++){
			for(int j=0;j<tokens.length;j++){
				String token=(tokens[j]);
					if(condProb[i].containsKey(token))
						score[i] += Math.log(condProb[i].get(token));
					else
						score[i] += Math.log(1.0/(classTokenCounts[i]+vSize));
				}
			}
			double maxScore = score[0];
			for(int i=0;i<score.length;i++){
				if(score[i]>maxScore)
					label = i;
			}

			return label;
		}

	


		public void preprocess(String trainDataFolder)
		{
			File folder=new File(trainDataFolder);
			fileList(folder,"train");
			trainingDocs=new String[trainfilenames.size()];
			trainingClasses=new int[trainlabels.size()];
			for(int i=0;i<trainfilenames.size();i++){
				trainingDocs[i]=parse(trainfilenames.get(i));
				trainingClasses[i]=trainlabels.get(i);
			}
		}
		
		private void fileList(File folder,String folder_type) {
			if(folder_type.equals("train")){
				Check1(folder);
				}
			
			else{
				Check2(folder);
			}
		}
		private void Check1(File folder) {
			for(File file : folder.listFiles()){
				if(file.isDirectory()){
					Check1(file);
				}
				else{
					if(file.isFile()){
							if(folder.getName().equals("neg")){
								trainlabels.add(new Integer(1));
							}
							else{
								trainlabels.add(new Integer(0));
							}
							trainfilenames.add(file.getAbsolutePath());
					}
				}
			}
		}
		private void Check2(File folder) {
			for(File file : folder.listFiles()){
				if(file.isDirectory()){
					Check2(file);
				}
				else{
					if(file.isFile()){
							if(folder.getName().equals("neg")){
								testlabels.add(new Integer(1));
								total_negative++;
							}
							else{
								testlabels.add(new Integer(0));
								total_positive++;
							}
							testfilenames.add(file.getAbsolutePath());
					}
				}
			}

		}

		public String parse(String fileName) {
			String Lines="";
			String Line;

			try {
				BufferedReader reader=new BufferedReader(new FileReader(fileName));
				while((Line=reader.readLine())!=null){
					Lines+=Line;
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Lines;
		}
		/**
		 *  Classify a set of testing documents and report the accuracy
		 * @param testDataFolder fold that contains the testing documents
		 * @return classification accuracy
		 */
		public double classifyAll(String testDataFolder)
		{

			
			File folder=new File(testDataFolder);

			fileList(folder,"test");

			testingDocs=new String[testfilenames.size()];
			testingClasses=new int[testlabels.size()];
			for(int i=0;i<testfilenames.size();i++){
				testingDocs[i]=parse(testfilenames.get(i));
				testingClasses[i]=testlabels.get(i).intValue();
			}
			int correctClassification = 0;
			for(int i=0;i<testingDocs.length;i++){
				int label=classify(testingDocs[i]);
				if(label==testingClasses[i]){
					correctClassification ++;
				}

			}
			
			
			System.out.println(correctClassification+" documents are correctly classified out of "+ testingDocs.length +" documents");
			double acc = (correctClassification*1.0)/testingDocs.length;
			return acc;
		}


		public static void main(String[] args)
		{
			NBClassifier nb=new NBClassifier("/Users/Priyanka/Documents/workspace/Priyanka_Lab4/data/train");
			double acc = nb.classifyAll("/Users/Priyanka/Documents/workspace/Priyanka_Lab4/data/test");
			System.out.println("Accuracy : "+acc);

		}
	}
