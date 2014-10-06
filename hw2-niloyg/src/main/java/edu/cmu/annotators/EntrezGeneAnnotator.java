package edu.cmu.annotators;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.deiis.types.GeneMentionTag;
import edu.cmu.lti.oaqa.bio.annotate.entrezgene.EntrezGeneWrapper;



/**
 * @author niloyg
 *
 *Makes a call to the Entrez Gene database to check whether the entity 
 *is present in the gene database. Increases the confidence of the entity if it is present in the gene DB.
 */

public class EntrezGeneAnnotator extends JCasAnnotator_ImplBase {
  
  @Override
  public void process( JCas jcas ) throws AnalysisEngineProcessException {  

    String geneText = jcas.getDocumentText();
    if ( geneText == null ) return;
    geneText = geneText.substring(geneText.indexOf(" ")+1);
  
    FSIterator<Annotation> geneTags = jcas.getAnnotationIndex(GeneMentionTag.type).iterator();

    while (geneTags.hasNext()) {
      GeneMentionTag geneTag = (GeneMentionTag) geneTags.next();
      if((geneTag.getConfidence()>0.40 && geneTag.getConfidence()<0.50))
      {
        if(EntrezGeneWrapper.getInstance().checkGene(geneTag.getGeneTag()))
          geneTag.setConfidence(1.5*geneTag.getConfidence());
      }
    }
    
  }

}
