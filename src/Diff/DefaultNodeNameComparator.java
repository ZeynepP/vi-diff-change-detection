package Diff;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Comparator;
import org.w3c.dom.Element;

/**
 *
 * @author pehlivanz
 */
  public class DefaultNodeNameComparator implements Comparator {

        public  int compare(Object o1, Object o2) {
                 return ((TagElement)o1).Name.toLowerCase().compareTo(((TagElement)o2).Name.toLowerCase());
           
         }


 }