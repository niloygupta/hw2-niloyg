package edu.cmu.annotators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.deiis.types.GeneMentionTag;

/**
 * @author niloyg
 *
 * Uses the ABNER Named Entity library to update confidence information on gene tags passed from
 * the previous annotator.
 */

public class AbnerGeneAnnotator extends JCasAnnotator_ImplBase {

  private List<String> abnerGeneEntities = new ArrayList<String>();

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    String geneText = jcas.getDocumentText();
    if (geneText == null)
      return;
    
    geneText = geneText.substring(geneText.indexOf(" ") + 1);

    FSIterator<Annotation> geneTags = jcas.getAnnotationIndex(GeneMentionTag.type).iterator();

    while (geneTags.hasNext()) {
      GeneMentionTag geneTag = (GeneMentionTag) geneTags.next();
      if ((geneTag.getConfidence() > 0.30 && geneTag.getConfidence() < 0.50)) {
        if (checkAbner(geneTag.getGeneTag(), geneText))
          geneTag.setConfidence(1.5*geneTag.getConfidence());
        else
          geneTag.setConfidence(0.9*geneTag.getConfidence());
      }
    }

  }

  /**
   * @param geneTag recognized gene from the previous annotator
   * @param geneText the complete sentence from the input data
   * @return true if the gene is also identified by ABNER
   * 
   * The ABNER tagger tags the gene text only once and the results are reused for other gene tags
   */
  private boolean checkAbner(String geneTag, String geneText) {
    if (abnerGeneEntities == null || abnerGeneEntities.isEmpty()) {
      String[][] result = AbnerTagger.getInstance().getTagger().getEntities(geneText);
      abnerGeneEntities = new ArrayList<String>(Arrays.asList(result[0]));
    }
    for (String geneEntities : abnerGeneEntities) {
      if (geneEntities.contains(geneTag) || geneTag.contains(geneEntities))
        return true;
    }
    return false;
  }

}
