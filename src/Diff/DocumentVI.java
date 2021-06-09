package Diff;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.File;
import java.util.ArrayList;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
 import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.*;
import javax.xml.xpath.*;



/**
 *
 * @author pehlivanz
 */
public class DocumentVI {


    public ArrayList<TagElement> Links;
    public ArrayList<TagElement> Images;
    public ArrayList<Element> Txts;

    public Document doc;
    public String fileName;
    public String Message = "";


    public DocumentVI(Document docs,String fn)
    {
        doc = docs;
        Links = new ArrayList<TagElement>();
        Images = new ArrayList<TagElement>();
        Txts = new ArrayList<Element>();

        fileName = fn;

    }

    public Document Compare(DocumentVI Version) throws ParserConfigurationException
    {


        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element Delta = document.createElement("Delta");
        Delta.setAttribute("From", this.fileName);
        Delta.setAttribute("To", Version.fileName);
        // For deltaxml test
        Delta.setAttribute("xmlns:deltaxml","http://www.deltaxml.com/ns/well-formed-delta-v1" );
        Delta.setAttribute("deltaxml:ordered","false");
        document.appendChild(Delta);


        NodeList nodeLstsource = doc.getElementsByTagName("Block");
        NodeList nodeLstversion = Version.doc.getElementsByTagName("Block");

        if(Utils.IsStructureChanged(nodeLstsource, nodeLstversion))
       {
            CompareWithStructuralChanges(Version,nodeLstsource, nodeLstversion,Delta,document);
        }
        else
        {
            CompareWithoutStructuralChanges(Version,nodeLstsource, nodeLstversion,Delta,document,null,null,null,-1);
        }



     return document;

    }
    
    
    
    
    
    
    private void CompareWithStructuralChanges(DocumentVI Version, NodeList nodeLstsource,NodeList nodeLstversion, Element Delta, Document newDoc )
    {

        ArrayList<Element> SourceList = new ArrayList<Element>();
        ArrayList<Element> VersionList = new ArrayList<Element>();


        int sCounter = 0;
        int vCounter = 0;
        int sMax = nodeLstsource.getLength();
        int vMax = nodeLstversion.getLength();
        boolean EndofSource = false, EndofVersion=false;
        boolean IsMaxSource = false;
        Element source = null;
        Element version = null;
        int BlockRefCount =-1;
        int Temp = 0;


        Element operation = null;
        Element tempelement = null;

        for(int i =0;i<sMax;i++)
        {
             source = ((Element)nodeLstsource.item(i));
             if(source.hasAttribute("ID"))
                 SourceList.add(source);
        }

        for(int i =0;i<vMax;i++)
        {
             version = ((Element)nodeLstversion.item(i));
             if(version.hasAttribute("ID"))
                 VersionList.add(version);
        }
        sMax = SourceList.size();
        vMax = VersionList.size();

        try
        {
         while(true)
        {

           // if(sCounter < 0 || vCounter < 0) break;
            if(sMax >= vMax) IsMaxSource = true;
            else IsMaxSource = false;


            if(vMax == 0 && sMax==0) break;
            if(sCounter==sMax)   {EndofSource=true;sCounter=0;}
            if(vCounter==vMax )  {EndofVersion=true;vCounter=0;}



            //{ EndofVersion=true;vCounter = vMax-1;if(IsMaxSource) diff--;}




            if(sMax !=0) source = SourceList.get(sCounter);
            if(vMax!=0) version = VersionList.get(vCounter);

            // same ID same Ref
                if(source.getAttribute("Ref").equals(version.getAttribute("Ref")) && source.getAttribute("ID").equals(version.getAttribute("ID")))
                {
                    // IDEM

                    SourceList.remove(source);
                    VersionList.remove(version);
                    vMax--; sMax--;
                }

                // same ID different Ref
                else if(source.getAttribute("ID").equals(version.getAttribute("ID")) && !source.getAttribute("Ref").equals(version.getAttribute("Ref")) )
                {
                   // System.out.println( source.getAttribute("Ref") +" is now 1 "  + version.getAttribute("Ref"));

                    operation = newDoc.createElement("Block");
                    operation.setAttribute("Ref",source.getAttribute("Ref"));
                    operation.setAttribute("newRef", version.getAttribute("Ref"));

                    Delta.appendChild(operation);
                    BlockRefCount++;

                    SourceList.remove(source);
                    VersionList.remove(version);
                    vMax--; sMax--;

                }
                // different ID different/same Ref

                else
                {
                        if(Utils.IsSimilair(source, version))
                        {

                           // System.out.println( source.getAttribute("Ref") +" is now 2 "  + version.getAttribute("Ref"));

                            operation = newDoc.createElement("Block");
                            operation.setAttribute("Ref",source.getAttribute("Ref"));
                            operation.setAttribute("newRef", version.getAttribute("Ref"));

                            Delta.appendChild(operation);
                             BlockRefCount++;

                            CompareWithoutStructuralChanges(Version,null,null,Delta,newDoc,source,version,version.getAttribute("Ref"),BlockRefCount);

                           
                           

                            SourceList.remove(source);
                            VersionList.remove(version);
                            vMax--; sMax--;

                        }
                         else if(EndofVersion || EndofSource)
                        {

                            if(IsMaxSource) vCounter ++;
                            else sCounter ++;

                            if(vCounter==vMax || vMax == 0)
                            {
                               // System.out.println( source.getAttribute("Ref") + "  is DELETED 1" );

 Temp = Delta.getElementsByTagName("Delete").getLength();
                                if(Temp == 0 ||
                                          Delta.getElementsByTagName("Delete").item(0).getParentNode().getNodeName() != "Delta")
                                {

                                     operation = newDoc.createElement("Block");
                                     operation.setAttribute("Ref",source.getAttribute("Ref"));
                                     tempelement = newDoc.createElement("Delete");
                                     tempelement.appendChild(operation);
                                     Delta.appendChild(tempelement);
                                     BlockRefCount++;

                                }
                                else
                                {

                                    for(int i=0; i < Temp;i++)
                                    {
                                        if(Delta.getElementsByTagName("Delete").item(i).getParentNode().getNodeName() == "Delta")
                                        {
                                            operation = newDoc.createElement("Block");
                                            operation.setAttribute("Ref",source.getAttribute("Ref"));
                                            Delta.getElementsByTagName("Delete").item(i).appendChild(operation);
                                        }
                                    }
// whose parent is not block

                                }

                               SourceList.remove(source);
                               sMax--;

                              EndofVersion = false;
                                if(!IsMaxSource)
                                    sCounter++;

                            }


                            if(sCounter==sMax || sMax ==0)
                            {
                               // System.out.println( version.getAttribute("Ref") + "  is INSERTED 22" );
                                  Temp = Delta.getElementsByTagName("Insert").getLength();
                                if(Temp == 0 ||
                                          Delta.getElementsByTagName("Insert").item(0).getParentNode().getNodeName() != "Delta")
                                {

                                     operation = newDoc.createElement("Block");
                                     operation.setAttribute("Ref",version.getAttribute("Ref"));
                                     tempelement = newDoc.createElement("Insert");
                                     tempelement.appendChild(operation);
                                     Delta.appendChild(tempelement);
                                     BlockRefCount++;

                                }
                                else
                                {

                                for(int i=0; i < Temp;i++)
                                {
                                    if(Delta.getElementsByTagName("Insert").item(i).getParentNode().getNodeName() == "Delta")
                                    {
                                        operation = newDoc.createElement("Block");
                                        operation.setAttribute("Ref",version.getAttribute("Ref"));
                                        Delta.getElementsByTagName("Insert").item(i).appendChild(operation);
                                    }
                                }
// whose parent is not block

                                }
                                VersionList.remove(version);
                                vMax--;
                                EndofSource = false;

                                if(IsMaxSource)
                                    vCounter++;

                            }


                        }
                        else
                        {
                           if(IsMaxSource) vCounter++; else sCounter++;


                        }
                    }


          } // whhile


        }// try

        catch(Exception ex)
        {
            System.out.println( ex.toString() + "  SCOUNTER :  " +  sCounter);
        }



    }

    private void CompareWithoutStructuralChanges(DocumentVI Version, NodeList nodeLstsource,NodeList nodeLstversion,
            Element rootElement, Document document,Element SrcElement,Element VerElement, String newRef,int BlockRefPosition)
    {
            Performance pr = new Performance();
          


            String Ref="";
            ArrayList<String> BlockRefs = new ArrayList<String>();
            Element Block=null;

            int diff = 0;

            Element fstNodeSrc = null;
            Element fstNodeVer = null;

            int size=0;
            if(nodeLstsource != null)
            {
                size =  nodeLstsource.getLength();
            }
            else size = 1;

  pr.start();
            for (int s = 0; s < size; s++) {

                 if(nodeLstsource != null)
                {
                     fstNodeSrc =( Element ) nodeLstsource.item(s);
                     fstNodeVer =( Element ) nodeLstversion.item(s);
                 }
                 else
                 {
                    fstNodeSrc = SrcElement;
                    fstNodeVer = VerElement;

                 }
                 // to test structural changes
                 if(fstNodeSrc == null)
                 {
                   diff++;
                   fstNodeSrc =( Element ) nodeLstsource.item(s-diff);
                 }

                 if(fstNodeVer == null)
                 {
                   diff++;
                   fstNodeVer =( Element ) nodeLstversion.item(s-diff);
                 }


                 if  (fstNodeSrc.hasAttribute("ID") && fstNodeVer.hasAttribute("ID"))
                 {

                      if(! fstNodeSrc.getAttribute("ID").equals(fstNodeVer.getAttribute("ID")) )
                      {
                                Ref = fstNodeSrc.getAttribute("Ref");
                                if(BlockRefPosition != -1 )
                                {
                                    for(int k = BlockRefs.size(); k< BlockRefPosition;k++)
                                            BlockRefs.add("");
                                }
                                BlockRefs.add(Ref);
                                Block = document.createElement("Block");
                                Block.setAttribute("Ref", Ref);
                           // DELTAXML
                              
                                Block.setAttribute("deltaxml:ordered","false");
                                if(newRef != null)
                                    Block.setAttribute("newfffRef", newRef);
                                rootElement.appendChild(Block);

                                // links et images init

                                InitElements(fstNodeSrc,fstNodeVer,Ref,Version);



                      }
                 }

            }

  pr.stop();
  Message+= " INIT TIME : " + pr.toString() + "\n";

            // Detect changes for links

pr.start();
if(Links.size() != 0 || Version.Links.size()!=0)
            TagElement.DetectChanges("link", "Adr", this.Links, Version.Links, rootElement, BlockRefs, document);
if(Images.size() != 0 || Version.Images.size()!=0)
            TagElement.DetectChanges("img", "Src", this.Images, Version.Images, rootElement, BlockRefs, document);
pr.stop();
Message+= " DETECT LINK IMG TIME : " + pr.toString()  + "\n";
pr.start();
if(Txts.size() != 0 || Version.Txts.size()!=0)
            TagElement.DetectTextChanges(Txts, Version.Txts,rootElement,document,BlockRefs);
pr.stop();
Message+= " DETECT TEXT  TIME : " + pr.toString()  + "\n";

    }

    public static void SaveDOMTree(Document document,String FileName) throws TransformerConfigurationException, TransformerException
    {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        StreamResult result = new StreamResult(new File("Delta" + FileName));
        Source source = new DOMSource(document);
        transformer.transform(source, result);

    }


    public void InitElements(Element fstNodeSrc,Element fstNodeVer,String BlockRef,DocumentVI Version)
    {
            String IDSource ="";
            String IDVersion = "";

            Element elSource = null;
            Element elVersion = null;
        try
        {
            if(fstNodeSrc.getElementsByTagName("Links").getLength() > 0)
            {
                    elSource = (Element)fstNodeSrc.getElementsByTagName("Links").item(0);
                    IDSource = elSource.getAttribute("ID");

            }
            if(fstNodeVer.getElementsByTagName("Links").getLength() > 0)
            {
                    elVersion = (Element)fstNodeVer.getElementsByTagName("Links").item(0);
                    IDVersion = elVersion.getAttribute("ID");
            }

            if(!IDSource.equals(IDVersion) )
            {
                if(IDSource!="")

                   Links.addAll(TagElement.CreateElementList(elSource.getElementsByTagName("link"),BlockRef,"Adr"));

                if(IDVersion!="")

                   Version.Links.addAll(TagElement.CreateElementList(elVersion.getElementsByTagName("link"),BlockRef,"Adr"));

            }

             IDSource ="";
             IDVersion = "";
             elSource = null;
             elVersion = null;

            if(fstNodeSrc.getElementsByTagName("Imgs").getLength() > 0)
            {
                    elSource = (Element)fstNodeSrc.getElementsByTagName("Imgs").item(0);
                    IDSource = elSource.getAttribute("ID");
            }
            if(fstNodeVer.getElementsByTagName("Imgs").getLength() > 0)
            {
                    elVersion = (Element)fstNodeVer.getElementsByTagName("Imgs").item(0);
                    IDVersion = elVersion.getAttribute("ID");
            }

            if(!IDSource.equals(IDVersion))
            {
                if(IDSource!="")

                       Images.addAll(TagElement.CreateElementList( elSource.getElementsByTagName("img"),BlockRef,"Src"));

                if(IDVersion!="")

                       Version.Images.addAll(TagElement.CreateElementList( elVersion.getElementsByTagName("img"),BlockRef,"Src"));
            }
            // TEXT CHECK

            IDSource ="";
            IDVersion = "";
            

                if(fstNodeSrc.getElementsByTagName("Txts").getLength() > 0)
                        IDSource =((Element) fstNodeSrc.getElementsByTagName("Txts").item(0)).getAttribute("ID");
                if(fstNodeVer.getElementsByTagName("Txts").getLength() > 0)
                        IDVersion =((Element) fstNodeVer.getElementsByTagName("Txts").item(0)).getAttribute("ID");


                if(!IDSource.equals(IDVersion))
                {
                    Txts.add(((Element) fstNodeSrc.getElementsByTagName("Txts").item(0)));
                    Version.Txts.add(((Element) fstNodeVer.getElementsByTagName("Txts").item(0)));


                }

}
catch(Exception ex)
{
    System.out.println("EXCEMTIN IN INIT BLOCKREF = " + BlockRef);
}

    }


  public void InitElementsLIST(Element fstNodeSrc,Element fstNodeVer,String BlockRef,DocumentVI Version)
    {
     
            // LINK CHECK
            Utils.Init("Links", fstNodeSrc, fstNodeVer, BlockRef, this, Version);
 

            // Img CHECK
            Utils.Init("Imgs", fstNodeSrc, fstNodeVer, BlockRef, this, Version);
 
        try{

            // TEXT CHECK
            String IDSource ="";
            String IDVersion = "";
            if(fstNodeSrc.getElementsByTagName("Txts").getLength() > 0)
                    IDSource =((Element) fstNodeSrc.getElementsByTagName("Txts").item(0)).getAttribute("ID");
            if(fstNodeVer.getElementsByTagName("Txts").getLength() > 0)
                    IDVersion =((Element) fstNodeVer.getElementsByTagName("Txts").item(0)).getAttribute("ID");


            if(!IDSource.equals(IDVersion))
            {
                Txts.add(((Element) fstNodeSrc.getElementsByTagName("Txts").item(0)));
                Version.Txts.add(((Element) fstNodeVer.getElementsByTagName("Txts").item(0)));


            }

        }
        catch(Exception ex)
        {
            System.out.println(ex + "TEXT CHECHK  IN INIT BLOCKREF = " + BlockRef);
        }

    }







}

/*

    }*/

