package Diff;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.HashSet;
import java.util.Set;
import javax.xml.xpath.*;
/**
 *
 * @author pehlivanz
 */
public class Utils {

    public static boolean IsStructureChanged(NodeList BlockListSource, NodeList BlockListVersion)
    {


         Element fstNodeSrc = null;
         Element fstNodeVer = null;

         int size= BlockListSource.getLength();
         if(BlockListSource.getLength() != BlockListVersion.getLength()) return true;
         else
         {
             for (int s = 0; s < size; s++) {

                    fstNodeSrc =( Element ) BlockListSource.item(s);
                    fstNodeVer =( Element ) BlockListVersion.item(s);

                     if  (fstNodeSrc.hasAttribute("ID") && fstNodeVer.hasAttribute("ID"))
                     {
                        if( ! fstNodeSrc.getAttribute("Ref").equals(fstNodeVer.getAttribute("Ref"))) return true;

                     }

             }
         }

        return false;

    }

    public static boolean IsSimilair(Element fstNodeSrc,Element fstNodeVer)
    {
        
        double total = IsSimilair(fstNodeSrc, fstNodeVer, "Links") +  IsSimilair(fstNodeSrc, fstNodeVer, "Imgs") + IsSimilair(fstNodeSrc, fstNodeVer, "Txts");
        if(total /3 > 0.2) return false;
        else return true;

    }

    @SuppressWarnings("empty-statement")
    public static double  IsSimilair(Element fstNodeSrc,Element fstNodeVer, String Tag)
    {

        String IDSource ="";
        String IDVersion = "";

        String[] Source;
        String[] Version;

        Set setSrc = new HashSet();
        Set setVer = new HashSet();
        Set set = new HashSet();
        double diff = 0;

        Element elSource = null;
        Element elVersion = null;

        if(Tag != "Txts")
        {
            if(fstNodeSrc.getElementsByTagName(Tag).getLength() > 0)
            {
                    elSource = (Element)fstNodeSrc.getElementsByTagName(Tag).item(0);
                    IDSource = elSource.getAttribute("IDList");

            }
            if(fstNodeVer.getElementsByTagName(Tag).getLength() > 0)
            {
                    elVersion = (Element)fstNodeVer.getElementsByTagName(Tag).item(0);
                    IDVersion = elVersion.getAttribute("IDList");
            }

            if(!IDSource.equals(IDVersion) )
            {
               Source = IDSource.split(",");
               Version = IDVersion.split(",");
               for(int i=0;i<Source.length;i++) setSrc.add(Source[i]);
               for(int i=0;i<Version.length;i++) setVer.add(Version[i]);

               set.addAll(setSrc);
               set.removeAll(setVer); // search complexity for add all t s nlogn

              // set.addAll(setVer);

               diff =set.size() / setSrc.size(); 

    //           diff = ((setSrc.size()+setVer.size()) - set.size()) / setSrc.size();;

            }
        }
        else
        {
            if(fstNodeSrc.getElementsByTagName(Tag).getLength() > 0)
            {
                    elSource = (Element)fstNodeSrc.getElementsByTagName(Tag).item(0);
                    IDSource = elSource.getAttribute("ID");

            }
            if(fstNodeVer.getElementsByTagName(Tag).getLength() > 0)
            {
                    elVersion = (Element)fstNodeVer.getElementsByTagName(Tag).item(0);
                    IDVersion = elVersion.getAttribute("ID");
            }

            if(!IDSource.equals(IDVersion) )
            {
                diff = 1;
            }
            else diff = 0;



        }


        return diff;


    }

    public static void Init(String NodeName, Element fstNodeSrc,Element fstNodeVer,String BlockRef,DocumentVI Source,DocumentVI Version)
    {


            String IDSource ="";
            String IDVersion = "";

            Element elSource = null;
            Element elVersion = null;

            Set setSrc = new HashSet();
            Set setVer = new HashSet();
            Set setTemp = new HashSet();

            String[] Src = null;
            String[] Ver = null;


try{

            if(fstNodeSrc.getElementsByTagName(NodeName).getLength() > 0)
            {
                    elSource = (Element)fstNodeSrc.getElementsByTagName(NodeName).item(0);
                    IDSource = elSource.getAttribute("ID");

            }
            if(fstNodeVer.getElementsByTagName(NodeName).getLength() > 0)
            {
                    elVersion = (Element)fstNodeVer.getElementsByTagName(NodeName).item(0);
                    IDVersion = elVersion.getAttribute("ID");
            }

            if(!IDSource.equals(IDVersion) )
            {
                if(IDSource != "")
                {
                    Src = elSource.getAttribute("IDList").split(",");
                    for(int i=0;i<Src.length;i++) {  setSrc.add(Src[i]);}
                }
                if(IDVersion != "")
                {
                    Ver = elVersion.getAttribute("IDList").split(",");
                    for(int i=0;i<Ver.length;i++) {   setVer.add(Ver[i]);}
                }

                     setTemp = ((Set)((HashSet)setSrc).clone());
                     setSrc.removeAll(setVer);
                     setVer.removeAll(setTemp);


                     XPathFactory factory = XPathFactory.newInstance();
                     XPath xpath = factory.newXPath();
                     XPathExpression expr ;


                     Object[] tempS = null;
                     if(setSrc.size() != 0)
                     {
                         tempS   = setSrc.toArray();
                         if(NodeName == "Links") // not to check node name in loop
                         {
                             for(int i=0; i < tempS.length;i++)
                             {
                               //  expr = xpath.compile("//link[@ID='"+ tempS[i].toString().trim()  +"']");
                               // Source.Links.add(TagElement.CreateElement((Element)((NodeList)expr.evaluate(elSource, XPathConstants.NODESET)).item(0),BlockRef,"Adr"));
                                for(int m =0; m < elSource.getElementsByTagName("link").getLength(); m++)
                                {

                                    if(((Element)elSource.getElementsByTagName("link").item(m)).getAttribute("ID").trim() == tempS[i].toString().trim())
                                    {
                                        Source.Links.add(TagElement.CreateElement(((Element)elSource.getElementsByTagName("link").item(m)),BlockRef,"Adr"));
                                        break;
                                    }
                                    }
                                }

                             }
                         }
                         else
                         {
                             for(int i=0; i < tempS.length;i++)
                             {
                               //  expr = xpath.compile("//img[@ID='"+ tempS[i].toString().trim()  +"']");
                               // Source.Images.add(TagElement.CreateElement((Element)((NodeList)expr.evaluate(elSource, XPathConstants.NODESET)).item(0),BlockRef,"Src"));
                                  for(int m =0; m < elSource.getElementsByTagName("img").getLength(); m++)
                                {

                                    if(((Element)elSource.getElementsByTagName("img").item(m)).getAttribute("ID").trim() == tempS[i].toString().trim())
                                    {
                                        Source.Images.add(TagElement.CreateElement(((Element)elSource.getElementsByTagName("img").item(m)),BlockRef,"Src"));
                                         break;
                                    }
                                }
                             }
                         }
                     
                     if(setVer.size() != 0)
                     {
                         tempS = setVer.toArray();
                         if(NodeName == "Links")
                         {
                             for(int i=0; i < tempS.length;i++)
                             {
                              //   expr = xpath.compile("//link[@ID='"+ tempS[i].toString().trim() +"']");
                               //  Version.Links.add(TagElement.CreateElement((Element)((NodeList)expr.evaluate(elVersion, XPathConstants.NODESET)).item(0),BlockRef,"Adr"));
                                  for(int m =0; m < elSource.getElementsByTagName("link").getLength(); m++)
                                {

                                    if(((Element)elSource.getElementsByTagName("link").item(m)).getAttribute("ID").trim() == tempS[i].toString().trim())
                                    {
                                        Version.Links.add(TagElement.CreateElement(((Element)elSource.getElementsByTagName("link").item(m)),BlockRef,"Adr"));
                                         break;
                                    }
                                }
                             }
                         }
                         else
                         {
                             for(int i=0; i < tempS.length;i++)
                             { for(int m =0; m < elSource.getElementsByTagName("img").getLength(); m++)
                                {
                                // expr = xpath.compile("//img[@ID='"+ tempS[i].toString().trim() +"']");
                                // Version.Images.add(TagElement.CreateElement((Element)((NodeList)expr.evaluate(elVersion, XPathConstants.NODESET)).item(0),BlockRef,"Src"));
                                   if(((Element)elSource.getElementsByTagName("img").item(m)).getAttribute("ID").trim() == tempS[i].toString().trim())
                                   {
                                       Version.Images.add(TagElement.CreateElement(((Element)elSource.getElementsByTagName("img").item(m)),BlockRef,"Src"));
                                        break;
                                    }
                               }
                             }
                         }
                     }


               }
}
catch(Exception ex)
{
    System.out.println("init in Utils: EXCEMTIN IN INIT BLOCKREF = " + BlockRef + " ex:" + ex.toString());
}

    }

    /**
 * Returns the child node with specified tag
 */


    
    /*
   private void CompareWithStructuralChanges(DocumentVI Version, NodeList nodeLstsource,NodeList nodeLstversion, Element Delta, Document newDoc )
    {

        ArrayList<Element> SourceList = new ArrayList<Element>();
        ArrayList<Element> VersionList = new ArrayList<Element>();


        int diff = 0;
        int sCounter = 0;
        int vCounter = 0;
        int sMax = nodeLstsource.getLength();
        int vMax = nodeLstversion.getLength();
        boolean EndofSource = false, EndofVersion=false;
        boolean IsMaxSource = false;
        Element source = null;
        Element version = null;
        int BlockRefCount =-1;


        Element operation = null;

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

        if(sMax >= vMax) IsMaxSource = true;
        else IsMaxSource = false;


        try
        {
         while(true)
        {

           // if(sCounter < 0 || vCounter < 0) break;

            if(vCounter >= vMax && sCounter>=sMax) break;
            else if(sCounter==sMax) { EndofSource=true;sCounter = sMax-1;if(!IsMaxSource) diff--; }
            else if(vCounter == vMax ) { EndofVersion=true;vCounter = vMax-1;if(IsMaxSource) diff--;}





            source = SourceList.get(sCounter);
            version = VersionList.get(vCounter);

            // same ID same Ref
                if(source.getAttribute("Ref").equals(version.getAttribute("Ref")) && source.getAttribute("ID").equals(version.getAttribute("ID")))
                {
                    // IDEM
                     sCounter ++;
                     vCounter++;
                }

                // same ID different Ref
                else if(source.getAttribute("ID").equals(version.getAttribute("ID")) && !source.getAttribute("Ref").equals(version.getAttribute("Ref")) )
                {
                    System.out.println( source.getAttribute("Ref") +" is now 1 "  + version.getAttribute("Ref"));

                    operation = newDoc.createElement("Block");
                    operation.setAttribute("Ref",source.getAttribute("Ref"));
                    operation.setAttribute("newRef", version.getAttribute("Ref"));

                    Delta.appendChild(operation);
                    BlockRefCount++;

                    sCounter ++;
                    vCounter++;
                    if(diff > 0) diff++;
                }
                // different ID different Ref

                else
                {
                        if(Utils.IsSimilair(source, version))
                        {

                            System.out.println( source.getAttribute("Ref") +" is now 2 "  + version.getAttribute("Ref"));



                            CompareWithoutStructuralChanges(Version,null,null,Delta,newDoc,source,version,version.getAttribute("Ref"),BlockRefCount);
                            BlockRefCount++;
                            Delta.appendChild(operation);

                            sCounter ++;
                            vCounter++;
                            if(diff > 0) diff++;

                        }
                         else if(EndofVersion || EndofSource)
                        {

                            if(IsMaxSource)
                            {
                                sCounter ++;
                                vCounter = vCounter - diff;
                            }
                            else
                            {
                                vCounter ++;
                                sCounter = sCounter - diff;

                            }
                            diff =0;

                            if(EndofVersion )
                            {
                                System.out.println( source.getAttribute("Ref") + "  is DELETED 1" );



                               if(Delta.getElementsByTagName("Delete").getLength() == 0)
                               {
                                   Delta.appendChild(newDoc.createElement("Delete"));
                                   BlockRefCount++;
                               }
                               operation = newDoc.createElement( source.getAttribute("Ref"));
                               Delta.getElementsByTagName("Delete").item(0).appendChild(operation);

                                EndofVersion = false;
                                if(!IsMaxSource)
                                    sCounter++;

                            }
                            if(EndofSource)
                            {
                                System.out.println( version.getAttribute("Ref") + "  is INSERTED 22" );

                                if(Delta.getElementsByTagName("Insert").getLength() == 0)
                                {
                                   Delta.appendChild(newDoc.createElement("Insert"));
                                   BlockRefCount++;
                                }
                                operation = newDoc.createElement("Block");
                                operation.setAttribute("Ref",version.getAttribute("Ref"));
                                Delta.getElementsByTagName("Insert").item(0).appendChild(operation);


                                EndofSource=false;
                                if(IsMaxSource)
                                    vCounter++;

                            }


                        }
                        else
                        {
                           if(IsMaxSource) vCounter++; else sCounter++;

                           diff++;

                        }
                    }


               if(EndofSource) {sCounter = sMax;}
               if(EndofVersion) {vCounter = vMax;}

          } // whhile


        }// try

        catch(Exception ex)
        {
            System.out.println( ex.toString() + "  SCOUNTER :  " +  sCounter);
        }



    }

    }*/

}
