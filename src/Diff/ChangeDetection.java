/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Diff;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 *
 * @author pehlivanz
 */
public class ChangeDetection {
     
    
    public static void StartDetection(File file1, File file2, String path) {

       
        try
        {

              

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();


                /*SOURCE*/
                Document doc = db.parse(file1);
                doc.getDocumentElement().normalize();
                

                /*VERSION*/
             
                Document docversion = db.parse(file2.toURI().toString());
                docversion.getDocumentElement().normalize();

                DocumentVI docVISource = new DocumentVI(doc,file1.getName());
                DocumentVI docVIVersion = new DocumentVI(docversion,file2.getName());
                
                Document delta =  docVISource.Compare(docVIVersion);
               
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");


                StreamResult deltaresult = new StreamResult(new File(path+"//delta//"+"Delta_" + file2.getName()));
                Source source = new DOMSource(delta);
                transformer.transform(source, deltaresult);
                
             //   return delta;
                

          }

         catch (Exception e)
          {

               System.out.println(e.toString());
             
          }



    }
}
