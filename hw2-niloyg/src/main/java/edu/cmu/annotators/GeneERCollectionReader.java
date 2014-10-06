package edu.cmu.annotators;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;


/**
 * @author niloygupta
 * Primary collection reader for the pipeline
 */
public class GeneERCollectionReader extends CollectionReader_ImplBase {

  /**
   * Name of configuration parameter that must be set to the path of the input file.
   */
  public static final String PARAM_INPUT_FILE = "InputFile";

  /**
   * File path to the HMM trained model
   */
  
  public static final String PARAM_MODEL_FILE = "ModelFile";
  
  
  private File inputFile;
  private String hmmModel;

  private Scanner dataLine;
  
  public void initialize() throws ResourceInitializationException {
    inputFile = new File(((String) getConfigParameterValue(PARAM_INPUT_FILE)).trim());
    hmmModel  = (String) getConfigParameterValue(PARAM_MODEL_FILE);

    GeneChunker.getInstance().setTrainedGeneERModel(hmmModel);
    NBestGeneChunker.getInstance().setTrainedGeneERModel(hmmModel);

    // if input file does not exist, throw exception
    if (!inputFile.exists()) {
      throw new ResourceInitializationException("File Not Found",
              new Object[] { PARAM_INPUT_FILE, this.getMetaData().getName(), inputFile.getPath() });
    }
    try {
      dataLine = new Scanner(inputFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  
  @Override
  public void getNext(CAS aCAS) throws IOException, CollectionException {

    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new CollectionException(e);
    }
   
    // put line from data file in CAS
    jcas.setDocumentText(dataLine.nextLine());
  }

  @Override
  public void close() throws IOException {
   

  }

  @Override
  public Progress[] getProgress() {
    return null;
  }

  @Override
  public boolean hasNext() throws IOException, CollectionException {
    return dataLine.hasNextLine();
  }

}
