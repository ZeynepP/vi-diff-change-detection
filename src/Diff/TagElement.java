package Diff;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author pehlivanz
 */
public class TagElement {

    public String ID;
    public String Name ;
    public String Src ;
    public String BlockRef;




    public TagElement(String id, String name,String src,String bRef )
    {
            ID = id;
            Name = name;
            Src = src;
            BlockRef = bRef;
   
    }

   

    public static  ArrayList<TagElement>  CreateElementList(NodeList Links,String Block,String SrcName)
    {

        Element test = null;
        TagElement tg = null;
        int size= Links.getLength();
        ArrayList<TagElement> List = new ArrayList<TagElement>();
        for(int i=0;i<size;i++)
        {
            try {

                test =((Element)Links.item(i));
               

                tg = new TagElement(
                       
                test.getAttribute("ID"),
                test.getAttribute("Name"),
                test.getAttribute(SrcName),
                Block);

                List.add(tg);


            } catch (Exception ex) {
                Logger.getLogger(TagElement.class.getName()).log(Level.SEVERE, null, ex);

            }
           
      
        }

          return List;
    }

     public static  TagElement  CreateElement(Element Links,String Block,String SrcName)
    {


        TagElement tg = null;


            try {




                tg = new TagElement(

                Links.getAttribute("ID"),
                Links.getAttribute("Name"),
                Links.getAttribute(SrcName),
                Block);


            } catch (Exception ex) {
              //  Logger.getLogger(TagElement.class.getName()).log(Level.SEVERE, null, ex);
               System.out.println( Links.getAttribute("ID") + "   " + ex.toString());

            }

          return tg;
    }

public static int GetBlockDiff(Element el)
{
    int BlockDiff = 0;
    NodeList childs = el.getChildNodes();
    int size = childs.getLength();
    if(size == 0) return BlockDiff;
    else
    {
        for(int i=0; i< size;i++ )
        {
            if(childs.item(i).getNodeName().toLowerCase() == "insert" || childs.item(i).getNodeName().toLowerCase() == "delete" ) BlockDiff++;

        }
    }

    return BlockDiff;

}
  public static void  DetectChanges(String Tag,String Src, ArrayList<TagElement> Source, ArrayList<TagElement> Version,Element Delta,ArrayList<String> BlockRefs,Document newDoc ) {

        Comparator comp = new DefaultNodeNameComparator();
        Collections.sort(Source,comp);
        Collections.sort(Version,comp);

        int sCounter = 0;
        int vCounter = 0;
        int sMax = Source.size();
        int vMax = Version.size();
        boolean EndofSource = false, EndofVersion=false;
        Element rootop = null;
        Element operation = null;
        TagElement source = null;
        TagElement version = null;
        int BlockRefDiff = GetBlockDiff(Delta);
        Element newEl = null;

        try
        {
       // while(sMax>sCounter+1 || vMax > vCounter+1)_
     while(true)
        {
            sMax = Source.size();
            vMax = Version.size();
            
            if(sMax ==0  && vMax==0) break;
            if(sCounter>=sMax) { EndofSource=true;sCounter = 0; } else  EndofSource=false;
            if(vCounter >= vMax ) { EndofVersion=true;vCounter = 0;} else EndofVersion=false;

            if(sMax !=0) source = Source.get(sCounter);
            if(vMax!=0) version = Version.get(vCounter);

            if(source== null)
                source = new TagElement("", "", "", "");
            if(version== null)
                version = new TagElement("", "", "", "");
   
            
            if(source.ID.equals(version.ID)) // ids are equal, looking for move
            {
               if(!source.BlockRef.equals( version.BlockRef)) // MOVED
               {

                    rootop = (Element)Delta.getChildNodes().item(BlockRefs.indexOf(source.BlockRef) +BlockRefDiff);

                   if(rootop.getElementsByTagName("Move").getLength() == 0)
                   {
                       newEl = newDoc.createElement("Move");
                                  //deltaxml
                        
                        newEl.setAttribute("deltaxml:ordered","false");
                   //Deltaxml
                       rootop.appendChild(newEl);
                   }
                   operation = newDoc.createElement(Tag);
                
                   operation.setAttribute(Src,source.Src);
                   operation.setAttribute("Name",source.Name);
                   operation.setAttribute("From",source.BlockRef);
                   operation.setAttribute("To",version.BlockRef);
                   rootop.getElementsByTagName("Move").item(0).appendChild(operation);

                   // System.out.println("MOVED :" + source.ID);
               }
              // else System.out.println("IDEM :" + source.ID);
              if(sMax !=0) Source.remove(sCounter);
              if(vMax !=0) Version.remove(vCounter);
             //  sCounter++;
             //  vCounter++;
            }
            else if(source.Name.equals(version.Name) && source.BlockRef.equals(version.BlockRef))
            {
              
                rootop = (Element)Delta.getChildNodes().item(BlockRefs.indexOf(source.BlockRef) + BlockRefDiff);
                   if(rootop.getElementsByTagName("Update").getLength() == 0)
                      {
                       newEl = newDoc.createElement("Update");
                                  //deltaxml
                        
                        newEl.setAttribute("deltaxml:ordered","false");
                   //Deltaxml
                       rootop.appendChild(newEl);
                   }

                if(!source.Src.equals(version.Src)) // Source update
                {
                    operation = newDoc.createElement(Tag);
                    operation.setAttribute("Name",source.Name);
                    operation.setAttribute("New"+Src,version.Src);
                    operation.setAttribute(Src,source.Src);
                   

                    rootop.getElementsByTagName("Update").item(0).appendChild(operation);

                    // System.out.println("UPDATED SRC :____ " +  source.Name);
                    // sCounter++;
                    // vCounter++;

                    if(sMax !=0) Source.remove(sCounter);
                    if(vMax !=0) Version.remove(vCounter);
                }

            }
            else if(source.Src.equals(version.Src) && source.BlockRef.equals(version.BlockRef))
            {
                  rootop = (Element)Delta.getChildNodes().item(BlockRefs.indexOf(source.BlockRef)  + BlockRefDiff);
                   if(rootop.getElementsByTagName("Update").getLength() == 0)
                       {
                       newEl = newDoc.createElement("Update");
                                  //deltaxml
                        
                        newEl.setAttribute("deltaxml:ordered","false");
                   //Deltaxml
                       rootop.appendChild(newEl);
                   }
                if(!source.Name.equals(version.Name))
                {
                   operation = newDoc.createElement(Tag);
                   operation.setAttribute("Name",source.Name);
                   operation.setAttribute("NewName",version.Name);
                   operation.setAttribute(Src,source.Src);


                   rootop.getElementsByTagName("Update").item(0).appendChild(operation);


                      if(sMax !=0) Source.remove(sCounter);
                    if(vMax !=0) Version.remove(vCounter);
                }

            }
            else 
            {

                if(EndofSource || EndofVersion)
                {
                     if(EndofVersion && sMax !=0)
                     {
                           rootop = (Element)Delta.getChildNodes().item(BlockRefs.indexOf(source.BlockRef)  + BlockRefDiff);
                            if(rootop.getElementsByTagName("Delete").getLength() == 0 ||
                            rootop.getElementsByTagName("Delete").item(0).getParentNode().getNodeName() == "Delta")
                                  {
                       newEl = newDoc.createElement("Delete");
                                  //deltaxml
                        
                        newEl.setAttribute("deltaxml:ordered","false");
                   //Deltaxml
                       rootop.appendChild(newEl);
                   }

                            operation = newDoc.createElement(Tag);
                            operation.setAttribute("Name",source.Name);
                            operation.setAttribute(Src,source.Src);


                            rootop.getElementsByTagName("Delete").item(0).appendChild(operation);
                            //System.out.println("Insert  : ____ " + source.Name);
                           //sCounter++;
                              if(sMax !=0) Source.remove(sCounter);

                             EndofVersion = false;


                     }
                     if(EndofSource && vMax !=0)
                     {
                          rootop = (Element)Delta.getChildNodes().item(BlockRefs.indexOf(version.BlockRef) +BlockRefDiff);
                            if(rootop.getElementsByTagName("Insert").getLength() == 0
                             ||
                            rootop.getElementsByTagName("Insert").item(0).getParentNode().getNodeName() == "Delta")
                                   {
                       newEl = newDoc.createElement("Insert");
                                  //deltaxml

                        newEl.setAttribute("deltaxml:ordered","false");
                   //Deltaxml
                       rootop.appendChild(newEl);
                   }
                            operation = newDoc.createElement(Tag);
                            operation.setAttribute("Name",version.Name);
                            operation.setAttribute(Src,version.Src);


                            rootop.getElementsByTagName("Insert").item(0).appendChild(operation);
                            //vCounter++;
                              if(vMax !=0) Version.remove(vCounter); 
                              EndofSource = false;
                     }
                }
                else if(source.Name.toLowerCase().compareTo(version.Name.toLowerCase()) < 0 
                        && Source.get((sCounter+1)%sMax).Name.toLowerCase().compareTo(Version.get((vCounter+1)%vMax).Name.toLowerCase()) < 0 )
                    sCounter++;
                else vCounter++;

            }

          }
        }catch(Exception ex)
        {
            System.out.println( ex.toString() + "  SCOUNTER :  " +  sCounter);
        }

       

  }


    public static  void DetectTextChanges(ArrayList<Element> SourceEl, ArrayList<Element>  VersionEl,Element Delta,Document newDoc,ArrayList<String> BlockRefs)
    {

        int size = SourceEl.size();
        String[] Src = null;
        String[] Ver = null;
        Element Source = null;
        Element Version = null;
        Element rootop = null;
        int difftemp = 0;
        for(int m = 0 ; m<size;m++)
        {


                Source  = SourceEl.get(m);
                Version = VersionEl.get(m);

                  // to test structural changes // or did not work weird but i will chack it later
                 if(Version == null)  {difftemp++;}
                 else if(Source == null) 
                	 {difftemp++;}
                 
                 if(Source == null)
                 {
                   
                    Source  = SourceEl.get(m-difftemp);
                 }

                 if(Version == null)
                 {
                  // difftemp++;
                    Version = VersionEl.get(m-difftemp);
                 }




                Src = Source.getAttribute("Txt").split("\\s");
                Ver =  Version.getAttribute("Txt").split("\\s");


                int sourcesize, versionsize;
                Set setSrc = new HashSet();
                Set setVer = new HashSet();
                Set set = new HashSet();
                sourcesize = Src.length;
                versionsize = Ver.length;

                for(int i=0;i<sourcesize;i++) { if(Src[i].trim() != "") setSrc.add(Src[i]);}
                for(int i=0;i<versionsize;i++) { if(Ver[i].trim() != "")setVer.add(Ver[i]);}

                set.addAll(setSrc); set.addAll(setVer);

                double diff =(double)((setSrc.size()+setVer.size()) - set.size());

                Element operation = null;

                 sourcesize = setSrc.size();

                if( (diff/setSrc.toArray().length)*100 < 50)
                {


                        rootop = (Element)Delta.getChildNodes().item(BlockRefs.indexOf(((Element)Source.getParentNode()).getAttribute("Ref")));
                        if(rootop.getElementsByTagName("Delete").getLength() == 0)
                                 rootop.appendChild(newDoc.createElement("Delete"));
                        if(rootop.getElementsByTagName("Insert").getLength() == 0)
                                 rootop.appendChild(newDoc.createElement("Insert"));

                        operation = newDoc.createElement("Txts");
                        operation.setAttribute("Txt", Version.getAttribute("Txt"));
                        rootop.getElementsByTagName("Insert").item(0).appendChild(operation);

                        operation = newDoc.createElement("Txts");
                        operation.setAttribute("Txt", Source.getAttribute("Txt"));
                        rootop.getElementsByTagName("Delete").item(0).appendChild(operation);


                }
                else
                {
                   rootop = (Element)Delta.getChildNodes().item(BlockRefs.indexOf(((Element)Source.getParentNode()).getAttribute("Ref")));
                        if(rootop.getElementsByTagName("Update").getLength() == 0)
                                 rootop.appendChild(newDoc.createElement("Update"));


                    operation = newDoc.createElement("Txts");
                    operation.setAttribute("Txt", Source.getAttribute("Txt"));
                    operation.setAttribute("NewTxt", Version.getAttribute("Txt"));

                    rootop.getElementsByTagName("Update").item(0).appendChild(operation);
                }
        }

    }



  

}
