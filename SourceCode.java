/*
 * SourceCode.java
 *
 * Version:
 * $Id: SourceCode.java,v 1.5 2010/02/18 03:54:47 ajl2612 Exp $
 *
 * Revisions:
 * $Log: SourceCode.java,v $
 * Revision 1.5  2010/02/18 03:54:47  ajl2612
 * works perfectly except for object seralization
 *
 * Revision 1.4  2010/02/17 04:29:35  ajl2612
 * almost done!
 *
 * Revision 1.3  2010/02/14 22:35:47  ajl2612
 * should be done, making changes as nessicary. will be back in 4 hours to
 * continte work
 *
 * Revision 1.2  2010/02/14 05:02:50  ajl2612
 * cblah
 *
 * Revision 1.1  2010/01/31 04:12:57  ajl2612
 * Initial revision
 *
 */

import java.util.*;
import java.io.*;

/**
 * Makes a database of StudentFiles. Users will be able to add and remove files
 *  to the database. Users will also be able to search through the files 
 * stored in the database by file name or by concepts the file covers.
 *
 * This class also contains the subclass StudentFile. A StudentFile object 
 * contains a file, a string array of concepts the file covers and the name of
 * the file.
 *
 * @author ajl2612: Andrew Lyne III
 */

public class SourceCode implements Serializable{

    public static ArrayList<String> favorites = new ArrayList<String>();
    public static int numFiles = 0;
    public static TreeSet<String> tree = new TreeSet<String>();
    public static HashMap< String, StudentFile> hashMap = new HashMap<String, StudentFile>();



    /**
     * constructor -- DELETE METHOD IF NOT NEEDED
     * 
     */

    public SourceCode() {
       
    }

    /**
     * checks to see if a file by the same name already exists in the 
     * database
     *
     * @param   name    string name of file to search for
     * @return boolean  true if file exists in database. false if not
     */
    public boolean isFileInDatabase( String fileName ){
        
        Iterator itr = tree.iterator();
        while( itr.hasNext() ){
            if( itr.next().equals( fileName ) )
                return true;
        }
        return false;
    }
 

    /**
     * accessor method for favorites list of sourcecode
     *
     * @return  ArrayList<String>  this sourceCode's treeSet
     */
    public ArrayList<String> getFavorites(){
        
        return favorites;
    }

    /**
     * accessor method for treeSet of sourcecode
     *
     * @return tree  this sourceCode's treeSet
     */
    public TreeSet<String> getTree(){
        
        return tree;
    }
    
    /**
     * accessor method for hashMap of sourcecode
     *
     * @return hashMap  this sourceCode's hashmap
     */
    public HashMap<String, StudentFile> getHashMap(){
        
        return hashMap;
    }


    /**
     * returns a file object given the file name as a string 
     * 
     *
     * @param   name    string name of file to search for
     * @return  file    the file you were looking for
     */
    public File getFile( String fileName ){
        File f = null;
        Iterator itr = tree.iterator();
        String name = null;
        while( itr.hasNext() ){
            name = (String)itr.next();
            if( name.equals( fileName ) )
                return hashMap.get( fileName ).getFile();
        }
        return null;
    }

    /**
     * adds a file to the database. adds the name of the file to the treeset 
     * and the file ojject to a hashmap with the filename as the key.
     *
     * @param   fileName    string name of new file
     * @param   newFile     file to be added to the database
     * @param   topics      java ideas that the file covers/implements
     */
    public void addFile( String fileName, File newFile, String topics, 
                         String className, String quarter ){
        StudentFile tempFile = new StudentFile( fileName, newFile, topics, 
                                               className, quarter );
        tree.add( fileName );
        hashMap.put( fileName, tempFile );
    }


    /**
     * removes a file to the database. 
     *
     * @param   fileName    string name of file
     * @return  boolean     true if file successfully removed from database
     *                      fale if file did not exist in database
     */
    public boolean removeFile( String fileName ){
        if( isFileInDatabase( fileName ) ){
            tree.remove( fileName );
            hashMap.remove( fileName );
            return true;
        }
        return false;
    }



        
        /////////////////////////////////////////////////////////////
        //                    SEARCH METHOD                        //
        //                                                         //
        //               IMPLEMENTED IN GUI CLASS                  //  
        /////////////////////////////////////////////////////////////

      
    /**
     * add a file to a list of favorites
     *
     * @param    name    string name of file
     */
    public void addFavorite( String fileName ){

        favorites.add( fileName );

    }

    /**
     * remove a file from a list of favorites
     *
     * @param    name    string name of file
     */
    public void removeFavorite( String fileName ){

        for( int i = 0; i< favorites.size(); i++ ){
            if( favorites.get( i ).equals( fileName ) ){
                favorites.remove( i );
                break;
            }
        }
    }


    /** 
     * A student file object contains the file to be added to the array list,
     * the name of the file, an arrayList of topics covered by the file and
     * several accessor and mutator methods. 
     *
     */

    public class StudentFile implements Serializable{
        
        private String name;
        private String keywords;
        private File data;
        private String className;
        private String quarter;
        
        /**
         * Creates a student file object that contains the file to be added
         * to the database, the name of the file and a string arraylist of
         * topics the file includes. 
         *
         * @param    fileName     string name of the file
         * @param    fileData     the file to be added to the database
         * @param    contents     string of the topics covered in the file
         * @param    className    name of the class this assignment was completed in
         * @param    quarter      year and quarter assignment was completed in. EG: a file 
         *                        completed in the spring quarter of 2010 would have the 
         *                        name 20093.
         *                        
         */
        public StudentFile( String fileName, File fileData, String contents, 
                            String className, String quarter ){

            name = fileName;
            data = fileData;
            keywords = contents;
            this.className = className;
            this.quarter = quarter;

        } 

        /**
         * accessor method for name of file
         *
         * @return   name     name of file
         */
        public String getName(){

            return name;

        }

        /**
         * accessor method for list of topics covered in the file.
         *
         * @return   contents     string concatonation of topics in file with
         *                        a single space separating each topic
         */
        public String getKeywords(){
            
            return keywords;

        }

        /**
         * accessor method for the file. returnsa copy of the file 
         *
         * @return   file     copy of the file, not the real copy
         */
        public File getFile(){

            return data;

        }

        /**
         * accessor method for className.  
         *
         * @return   className     name of the class this assignment was created in
         */
        public String getClassName(){

            return className;

        }        

        /**
         * accessor method for the year.  
         *
         * @return   quarter     returns the quarter this assignment was created in 
         */
        public String getQuarter(){

            return quarter;

        }


        /**
         * replace the file in this object with the file given as a parameter
         *
         * @param   file     file to replace current file object      
         */
        public void replaceFile( File newFile ){

            data = newFile;

        }

    }// StudentFile

} // SourceCode
