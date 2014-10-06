package edu.cmu.annotators;

import java.util.Map;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.deiis.types.GeneMentionTag;



/**
 * @author niloyg
 *
 *Redundant annotator. Uses stanford NLP tool to parse entities. 
 */

public class PosTagGeneAnnotator extends JCasAnnotator_ImplBase {
  
  @Override
  public void process( JCas jcas ) throws AnalysisEngineProcessException {  

    String geneText = jcas.getDocumentText();
    if ( geneText == null ) return;
    String lineId = geneText.substring(0, geneText.indexOf(" ")); //Extracting the sentence ID
    geneText = geneText.substring(geneText.indexOf(" ")+1);
  
    
    Map<Integer, Integer> geneSpans = PosTagNamedEntityRecognizer.getInstance().getGeneSpans(geneText);
    FSIterator<Annotation> it = jcas.getAnnotationIndex(GeneMentionTag.type).iterator();

    while (it.hasNext()) {
      GeneMentionTag sentence = (GeneMentionTag) it.next();
      if((sentence.getConfidence()<0.40))
      {
        boolean isGeneTagged = false;
        for(Integer begin:geneSpans.keySet())
        {
          if(geneText.substring(begin, geneSpans.get(begin)).equals(sentence.getGeneTag()))
            isGeneTagged = true;
        }
        if(!isGeneTagged)
          sentence.setConfidence(0.0);
      }
    }
    
  }

}
