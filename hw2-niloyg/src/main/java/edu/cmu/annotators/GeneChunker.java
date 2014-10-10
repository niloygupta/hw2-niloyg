package edu.cmu.annotators;

import java.io.File;
import java.io.IOException;

import com.aliasi.chunk.Chunker;
import com.aliasi.util.AbstractExternalizable;

/**
 * @author niloygupta
 * Singleton class which loads the HMM Trained Model during initialization
 */
public class GeneChunker {

  private String trainedGeneERModel; 
  private static GeneChunker geneChunker;
  private Chunker chunker;
  
  private GeneChunker(){}

  public static GeneChunker getInstance()
  {
    if (geneChunker == null)
      geneChunker = new GeneChunker();

    return geneChunker;
  }
 
  /**
   * @return Returns existing chunker. If chunker is null initializes it.
   */
  public Chunker getChunker()
  {
    if(chunker==null)
      intializeChunker();
    return chunker;
  }
  
  /**
   * @param ERModelFile File path to the HMM Model
   */
  public void setTrainedGeneERModel(String ERModelFile)
  {
    trainedGeneERModel = ERModelFile;
  }
  
  /**
   * Initializes the HMM Model for LingPipe
   */
  public void intializeChunker()
  {
    File modelFile = new File(trainedGeneERModel);
    try {
      //chunker = (Chunker) AbstractExternalizable.readObject(modelFile);
      chunker = (Chunker) AbstractExternalizable.readResourceObject(GeneERCollectionReader.class,trainedGeneERModel);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
