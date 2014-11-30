import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import sun.awt.datatransfer.DataTransferer;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.Id3;
import weka.core.Instance;
import weka.core.Instances;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToString;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.core.converters.ConverterUtils.DataSource;
import com.csvreader.CsvWriter;
import java.io.File;

/**
 *
 * @author Kevin Huang
 */
@ManagedBean(name="ManageBeans", eager = true)
@RequestScoped
public class ManageBeans {
    private Instances datatraininstance;
    private Instances datatestinstance;
    protected static Classifier[] class_type = new Classifier[4];
    protected static String[] savedmodel = {"NaiveBayes","Id3","IBk"};
    public ManageBeans() throws Exception
    {
        datatraininstance = createtraininstance();
        //datatestinstance.add(createtestinstance());
    }
    public Instances createtraininstance() throws Exception{
        InstanceQuery query = new  InstanceQuery();
        query.setUsername("root");
        query.setPassword("");
        query.setQuery("Select Judul,FULL_TEXT,Label from artikel NATURAL JOIN artikel_kategori_verified NATURAL JOIN kategori");
        datatraininstance = query.retrieveInstances();
        System.out.println("Create Instances succesfull");
        return datatraininstance;
    }
    public Instance createtestinstance(){
        String Judulmasukan = "Apakah ada keanehan pada weka?";
        String FullTextmasukan = "Weka mengalami beberapa gangguan aneh sehingga terjadinya kesusahan melakukan pemograman java. Ini bisa disebabkan karena...";
        String Labelmasukan = "Pendidikan";
        Instance temp = new Instance(3);
        temp.setValue(0,Judulmasukan);
        temp.setValue(1,FullTextmasukan);
        temp.setValue(2,Labelmasukan);
        return temp;
    }
    public void filterbyNominaltoString() throws Exception{
        Instances tempdata = null;
        Instance tempprocessdata = null;
        Filter Nominaltostring = new NominalToString();
        Nominaltostring.setInputFormat(datatraininstance);
        for(int i = 0;i<datatraininstance.numInstances();i++)
        {
            Nominaltostring.input(datatraininstance.instance(i));
        }
        Nominaltostring.batchFinished();
        tempdata = Filter.useFilter(datatraininstance,Nominaltostring);
        datatraininstance = tempdata;
        System.out.println("Success NominalToString");
        //return datatraininstance;
    }
    
    public void ClassifyAlgorithm() throws Exception{
        if(datatraininstance.classIndex()== -1)
        {
            datatraininstance.setClassIndex(2);
        }
        datatraininstance.classIndex();
        class_type[0] = new NaiveBayes();
        class_type[1] = new Id3();
        class_type[2] = new IBk();
        for (int i = 0; i < class_type.length; i++) {
            class_type[i].buildClassifier(datatraininstance);
        }
        System.out.println("Classfier set");
    }
    
    public void eval (int type,int algorithm) throws Exception{
        Evaluation eval = new Evaluation(datatraininstance);
        if(type==0)
                eval.crossValidateModel(class_type[algorithm], datatraininstance, 10, new Random(1));
        else
                eval.evaluateModel(class_type[algorithm], datatestinstance);
        System.out.println(eval.toSummaryString("\nResults\n======\n", false));
        System.out.println(eval.toClassDetailsString());
        System.out.println(eval.toMatrixString());

    }
    
    public Instances filterbyStringtoWordVector() throws Exception
    {
        Instances tempdata = null;
        Instance tempprocessdata = null;
        Filter StringtoWordVector = new StringToWordVector();
        StringtoWordVector.setInputFormat(datatraininstance);
        for(int i = 0;i<datatraininstance.numInstances();i++)
        {
            StringtoWordVector.input(datatraininstance.instance(i));
        }
        StringtoWordVector.batchFinished();
        tempdata = Filter.useFilter(datatraininstance,StringtoWordVector);
        datatraininstance = tempdata;
        return datatraininstance;
    }
    
    public void WriteCSV() throws IOException{
        System.out.println("Loading to write file");
        System.out.println("Data ready to write is "+datatraininstance.toString());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter ("traininstance.csv"))) {
            writer.write(datatraininstance.toString());
            writer.newLine();
            writer.flush();
            writer.close();
        }
        System.out.println("Write to ARFF Success");
    }
    
        public void WriteCSV2() throws IOException{
        System.out.println("Loading to write file");
        boolean alreadyExists = new File("traininstance.csv").exists();
        CsvWriter csvOutput = new CsvWriter(new FileWriter("traininstance.csv", true), ',');
			if (!alreadyExists)
			{
				csvOutput.write("id");
				csvOutput.write("name");
				csvOutput.endRecord();
			}
			else
                        {
                            csvOutput.write(datatraininstance.toString());
                            csvOutput.endRecord();
                        }
			csvOutput.close();
        System.out.println("Write to ARFF Success");
    }
    
    private static void load_model(int model) throws Exception {
		class_type[model] = (Classifier) weka.core.SerializationHelper.read(savedmodel[model] + ".model");
		System.out.println("Model loaded.");
    }
    
    private static void save_model(int model) throws Exception {
		weka.core.SerializationHelper.write(savedmodel[model] + ".model", class_type[model]);
		System.out.println("Model saved.");
    }
    
    private void loadCSV(String filepath) throws Exception{
        DataSource source = new DataSource(filepath);
        datatestinstance = source.getDataSet();
        datatraininstance.setClassIndex(2);
        System.out.println("Success Load CSV");
        System.out.println("The data loaded is "+ datatestinstance.toString());
    }
    
    public void testRun() throws Exception{
        filterbyNominaltoString();
        filterbyStringtoWordVector();
        System.out.println("Train num instance : ");
        WriteCSV2();
        //ClassifyAlgorithm();
        //System.out.println(datatestinstance);
    }
     
}
