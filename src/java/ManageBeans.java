import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import weka.core.Instance;
import weka.core.Instances;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToString;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author Kevin Huang
 */
@ManagedBean(name="ManageBeans", eager = true)
@RequestScoped
public class ManageBeans {
    private Instances datainstance;
            
    public ManageBeans() throws Exception
    {
        datainstance = createInstance();
    }
    public Instances createInstance() throws Exception{
        InstanceQuery query = new  InstanceQuery();
        query.setUsername("root");
        query.setPassword("");
        query.setQuery("Select Judul,FULL_TEXT from artikel NATURAL JOIN artikel_kategori_verified");
        datainstance = query.retrieveInstances();
        System.out.println("Create Instances succesfull");
        return datainstance;
    }
    public void filterbyNominaltoString() throws Exception{
        Instances tempdata = null;
        Instance tempprocessdata = null;
        Filter Nominaltostring = new NominalToString();
        Nominaltostring.setInputFormat(datainstance);
        for(int i = 0;i<datainstance.numInstances();i++)
        {
            Nominaltostring.input(datainstance.instance(i));
        }
        Nominaltostring.batchFinished();
        tempdata = Filter.useFilter(datainstance,Nominaltostring);
        datainstance = tempdata;
        System.out.println("Success NominalToString");
        //return datainstance;
    }
    
    public void filterbyAlgorithm() throws Exception{
        Instances tempdata = null;
        Instance tempprocessdata = null;
        Filter Nominaltostring = new NominalToString();
        Nominaltostring.setInputFormat(datainstance);
        for(int i = 0;i<datainstance.numInstances();i++)
        {
            Nominaltostring.input(datainstance.instance(i));
        }
        Nominaltostring.batchFinished();
        tempdata = Filter.useFilter(datainstance,Nominaltostring);
        datainstance = tempdata;
        System.out.println("Success NominalToString");
        
    }
    
    public Instances filterbyStringtoWordVector() throws Exception
    {
        Instances tempdata = null;
        Instance tempprocessdata = null;
        System.out.println("Instance pertama " +datainstance.instance(1));
        Filter StringtoWordVector = new StringToWordVector();
        StringtoWordVector.setInputFormat(datainstance);
        for(int i = 0;i<datainstance.numInstances();i++)
        {
            StringtoWordVector.input(datainstance.instance(i));
        }
        StringtoWordVector.batchFinished();
        tempdata = Filter.useFilter(datainstance,StringtoWordVector);
        datainstance = tempdata;
        System.out.println("Success StringtoWordVectorFilter");
        return datainstance;
    }
    public void testRun() throws Exception{
        datainstance = createInstance();
        filterbyNominaltoString();
        filterbyStringtoWordVector();
    }
    
}
