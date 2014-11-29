/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kevin Huang
 */
/**
	klien: mengirimkan bilangan n ke server, menerima n + 1 dari server
	disarankan untuk membungkus address dan socket ke kelas tersendiri
*/
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.Id3;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.classifiers.Classifier;


public class Weka {

	protected final static String dataset = "../src/java/weather.nominal.arff";
	protected final static String trainset = "../src/java/weather.nominal.arff";
	protected final static String unlabeled_dataset = "../src/java/weather.nominal.unlabeled.arff";
	protected final static String labeled_dataset = "../src/java/weather.nominal.labeled.arff";
	protected final static String[] saveclf = {"bayes","id3","ibk","percept"};
	protected static DataSource source;
	protected static DataSource trainsource;
	protected static Instances train;
	protected static Instances data;
	protected static Classifier[] class_type = new Classifier[4];
	private static Scanner cin;
        
	public static void initial() throws Exception{
                System.out.println("Tahap 0");
                cin = new Scanner(System.in);
		System.out.println("Tahap 1");
                source = new DataSource(dataset);
                System.out.println("Tahap 2");
		data = source.getDataSet();
		if (data.classIndex() == -1)
			   data.setClassIndex(data.numAttributes() - 1);
		trainsource = new DataSource(trainset);
		System.out.println("Tahap 3");
                train = trainsource.getDataSet();
		if (train.classIndex() == -1)
			   train.setClassIndex(train.numAttributes() - 1);
                System.out.println("@@@@@@");
		class_type[0] = new NaiveBayes();
		class_type[1] = new Id3();
		class_type[2] = new IBk();
		class_type[3] = new MultilayerPerceptron();
		for (int i = 0; i < class_type.length; i++) {
			class_type[i].buildClassifier(data);
		}
		System.out.println("File loaded");
	}
	
	public static void eval(int type, int clf) throws Exception {
		Evaluation eval = new Evaluation(data);
		if(type==0)
			eval.crossValidateModel(class_type[clf], data, 10, new Random(1));
		else
			eval.evaluateModel(class_type[clf], train);
		System.out.println(eval.toSummaryString("\nResults\n======\n", false));
		System.out.println(eval.toClassDetailsString());
		System.out.println(eval.toMatrixString());
	}
	
	public static void classify_model(int clf) throws Exception {
		Instances unlabeled = (new DataSource(unlabeled_dataset)).getDataSet();
		if (unlabeled.classIndex() == -1)
			unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
		Instances labeled = new Instances(unlabeled);
		for (int i = 0; i < unlabeled.numInstances(); i++) {
			double clsLabel = class_type[clf].classifyInstance(unlabeled.instance(i));
			labeled.instance(i).setClassValue(clsLabel);
		}
		BufferedWriter writer = new BufferedWriter(
                new FileWriter(labeled_dataset));
		writer.write(labeled.toString());
		writer.newLine();
		writer.flush();
		writer.close();
		System.out.println("Dataset labeled");
		
	}

	public static void load_model(int clf) throws Exception {
		class_type[clf] = (Classifier) weka.core.SerializationHelper.read(saveclf[clf] + ".model");
		System.out.println("Model loaded.");
	}
	
	public static void save_model(int clf) throws Exception {
		weka.core.SerializationHelper.write(saveclf[clf] + ".model", class_type[clf]);
		System.out.println("Model saved.");
	}

	/*public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int choice;
		System.out.println("Loading weather.nominal.arff ...");
		ini_tial();
		do
		{
			System.out.println("===============================================================");
			System.out.println("===========================   MENU   ==========================");
			System.out.println("===============================================================");
			System.out.println("1. Use 10 fold cross validation with Naive Bayes");
			System.out.println("2. Use 10 fold cross validation with ID3");
			System.out.println("3. Use 10 fold cross validation with IBk");
			System.out.println("4. Use 10 fold cross validation with Multilayer Perceptron");
			System.out.println("5. Use full training with Naive Bayes");
			System.out.println("6. Use full training with ID3");
			System.out.println("7. Use full training with IBk");
			System.out.println("8. Use full training with Multilayer Perceptron");
			System.out.println("9. Save Naive Bayes model");
			System.out.println("10. Save ID3 model");
			System.out.println("11. Save IBk model");
			System.out.println("12. Save Multilayer Perceptron model");
			System.out.println("13. Load Naive Bayes model");
			System.out.println("14. Load ID3 model");
			System.out.println("15. Load IBk model");
			System.out.println("16. Load Multilayer Perceptron model");
			System.out.println("17. Classify with Naive Bayes");
			System.out.println("18. Classify with ID3");
			System.out.println("19. Classify with IBk");
			System.out.println("20. Classify with Multilayer Perceptron");
			System.out.println("===============================================================");
			System.out.print("Select your choice: ");
			choice = cin.nextInt();
			switch (choice)
			{
				case 0: System.out.println("Exiting ..."); break;
				case 1:	eval(0,0); break;
				case 2: eval(0,1); break;
				case 3: eval(0,2); break;
				case 4: eval(0,3); break;
				case 5: eval(1,0); break;
				case 6: eval(1,1); break;
				case 7: eval(1,2); break;
				case 8: eval(1,3); break;
				case 9: save_model(0); break;
				case 10: save_model(1); break;
				case 11: save_model(2); break;
				case 12: save_model(3); break;
				case 13: load_model(0); break;
				case 14: load_model(1); break;
				case 15: load_model(2); break;
				case 16: load_model(3); break;
				case 17: classify_model(0); break;
				case 18: classify_model(1); break;
				case 19: classify_model(2); break;
				case 20: classify_model(3); break;
			}
		}
		while(choice!=0);
	}*/

}