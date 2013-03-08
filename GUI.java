/*
 * GUI.java
 *
 * Version:
 * $Id: GUI.java,v 1.5 2010/02/18 03:54:19 ajl2612 Exp $
 *
 * Revisions:
 * $Log: GUI.java,v $
 * Revision 1.5  2010/02/18 03:54:19  ajl2612
 * works perfectly except for seralization.
 *
 * Revision 1.4  2010/02/17 04:30:59  ajl2612
 * all feilds still need to be tested with actual data. add method fnctions for adding programs to database.
 * quit function does not work with prompt save before it. will try to debug before deadline but not major
 * priority
 *
 * Revision 1.3  2010/02/15 02:08:51  ajl2612
 * implemented OMGDontSave exception to assist in saving issues if user hits cancel. several buttons still
 * need to be implemented and all need to be tested with data
 *
 * Revision 1.2  2010/02/14 22:35:05  ajl2612
 * still a work in progress. need to implement save and add file to database
 *
 * Revision 1.1  2010/02/14 05:02:42  ajl2612
 * Initial revision
 *
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;


/**
 * GUI_COMMENT
 *
 * @author ajl2612: Andrew Lyne III
 */

public class GUI {

    static SourceCode database = null;
    static String fileCurrentlyDisplayed = null;
    ObjectOutputStream outStream = null;
    ObjectInputStream inStream = null;
    File helpFile = new File( "help.txt" );
    File aboutFile = new File( "about.txt" );

    // make all the data feilds to be used later
    final JTextArea text = new JTextArea( 25, 60 );
    JScrollPane scrollPane = new JScrollPane( text );
    
    JMenuBar menuBar = new JMenuBar();
    File fileToAdd;
    JMenu file = new JMenu( "File" );
    JMenuItem fileItem1 = new JMenuItem( "Open File" );
    JMenuItem fileItem2 = new JMenuItem( "Save Changes" );
    JMenuItem fileItem3 = new JMenuItem( "Close File" );
    JMenuItem fileItem4 = new JMenuItem( "Add File To Database" );
    JMenuItem fileItem5 = new JMenuItem( "Remove File From Database" );
    JMenuItem fileItem6 = new JMenuItem( "Quit" );
    
    JMenu search = new JMenu( "Search" );
    JMenuItem searchItem1 = new JMenuItem( "By File Name" );
    JMenuItem searchItem2 = new JMenuItem( "By Topic" );
    JMenuItem searchItem3 = new JMenuItem( "By Class Name" );
    JMenuItem searchItem4 = new JMenuItem( "By Quarter" );
    JMenuItem searchItem5 = new JMenuItem( "List All Files" );
    
    JMenu favs = new JMenu( "Favorites" );
    JMenuItem favsItem1 = new JMenuItem( "Add Favorite" );
    JMenuItem favsItem2 = new JMenuItem( "Remove Favorite" );
    JMenuItem favsItem3 = new JMenuItem( "Open Favorite");

    JMenu info = new JMenu( "Info" );
    JMenuItem info1 = new JMenuItem( "Help" );
    JMenuItem info2 = new JMenuItem( "About the Author" );


   /**
    * makes a new student file veiwer GUI. the program takes in no parameters
    *
    */

   public GUI() {
       // make the database and various objects for accessing and modifying components
       // Make the frame that contains all attrubutes of the GUI and sets the 
       // layout.                               
       readDatabase();
       final JFrame frame  = new JFrame( "Student File Viewer" );
       frame.setLayout( new BorderLayout() );
       frame.setLocationRelativeTo( null );




       // make the open action listener
       ActionListener openListener = new ActionListener(){

               public void actionPerformed( ActionEvent e ){
                   try{
                       if( fileCurrentlyDisplayed != null )
                           promptSave();
                       
                       final JFrame openFrame = new JFrame( "Open" );
                       JButton b1 = new JButton( "Open" );
                       final JTextField tf1 = new JTextField( "Enter File Name", 20 );
                       
                       ActionListener b1Listener = new ActionListener(){
                               
                               public void actionPerformed( ActionEvent e ){
                                   try{
                                       String tempString = tf1.getText();
                                       File tempFile;
                                       if(  database.isFileInDatabase( tempString ) ){
                                           fileCurrentlyDisplayed = tempString;
                                           tempFile = database.getFile( tempString );
                                           Scanner input = new Scanner( tempFile );
                                           text.setText( "" );
                                           while( input.hasNext() ){
                                               tempString = input.nextLine();
                                               text.append( tempString );
                                               text.append( "\n" );
                                           }
                                           text.setCaretPosition( 0 );
                                           openFrame.dispose();
                                       }
                                       else{
                                           text.setText( " 404: File Not Found " );
                                           openFrame.dispose();
                                       }
                                   }
                                   catch( FileNotFoundException r ){
                                       text.setText( " 404: File Not Found " );
                                       openFrame.dispose();
                                   }
                               }
                           };
                       b1.addActionListener( b1Listener );
                       openFrame.setLocationRelativeTo( null );
                       openFrame.setLayout( new GridLayout( 0, 1 ) );
                       openFrame.add( tf1 );
                       openFrame.add( b1 );
                       openFrame.pack();
                       openFrame.setVisible( true );
                   }
                   catch( OMGDontSaveException r ){
                   }
               }
           };


       // make the save file listener
       ActionListener saveFileListener = new ActionListener(){

               public void actionPerformed( ActionEvent e ){
                   
                   
                   try{   
                       if( fileCurrentlyDisplayed != null ){
                           promptSave();
                           writeDatabase();
                       }
                       else{
                           JFrame popUp = new JFrame( "" );
                           JOptionPane.showMessageDialog( popUp, "Nothing to Save" );

                       }
                   }
                   catch( OMGDontSaveException r ){

                   }
               }
           }; 


       // make the close file listener
       ActionListener closeFileListener = new ActionListener(){

               public void actionPerformed( ActionEvent e ){
                   try{   
                       if( fileCurrentlyDisplayed != null )
                           promptSave();
                       text.setText( "" );
                       fileCurrentlyDisplayed = null;
                       writeDatabase();
                   }
                   catch( OMGDontSaveException r ){
                       // do nothing
                   }
               }
           }; 

              

       // make the add file to database listener
       ActionListener addFileToDatabaseListener = new ActionListener(){

               public void actionPerformed( ActionEvent e ){

                   String errorMessage = " ";
                   final JFrame popUp = new JFrame( "Add File to Database" );

                   try{
                       
                       final JFrame fileFrame = new JFrame( "Add File to Database" );
                       final JFrame dataFrame = new JFrame( "Add File To Database" );
                       final JButton b1 = new JButton( "Add" );
                       final JTextField tf1 = new JTextField( "Enter Topics", 20 );
                       final JTextField tf2 = new JTextField( "Enter Class Name", 20 );
                       final JTextField tf3 = new JTextField( "Enter Quarter Number", 20 );
                       final JFileChooser fileChoice = new JFileChooser();
                       
                       ActionListener b1Listener = new ActionListener(){
                               
                               public void actionPerformed( ActionEvent e ){
                                   // get the values from the text fields
                                   String keywords;
                                   String className = tf2.getText();
                                   String quarter = tf3.getText();
                                   String message;
                                   
                                   // test to see of there is at least one word in feilds.
                                   // reassigns values to " " if not. 
                                   if( tf1.getText().length() < 1 || tf1.getText().
                                       equals( "Enter Topics" ) )
                                       keywords = " ";
                                   else
                                       keywords = tf1.getText();

                                   if( tf2.getText().length() < 1 || tf2.getText().
                                       equals( "Enter Class Name" ))
                                       className = " ";
                                   else
                                       className = tf2.getText();

                                   if( tf3.getText().length() < 4 || tf3.getText().
                                       equals( "Enter Quarter Number" ))
                                       quarter = " ";
                                   else
                                       quarter = tf3.getText();
                                   
                                   database.addFile( fileToAdd.getName(), fileToAdd, keywords, 
                                                     className, quarter );
                                   message = fileToAdd.getName() + " Added to Database";
                                   JOptionPane.showMessageDialog( popUp, message );

                                   dataFrame.dispose();
                                   writeDatabase();
                               }    
                           };
                       
                       
                       b1.addActionListener( b1Listener );
                       
                       dataFrame.setLayout( new GridLayout( 0,1 ) );
                       dataFrame.add( tf1 );
                       dataFrame.add( tf2 );
                       dataFrame.add( tf3 );
                       dataFrame.add( b1 ); 
                       
                       dataFrame.pack();
                       dataFrame.setLocationRelativeTo( null );
                       
                       int returnValue = fileChoice.showOpenDialog( fileFrame );
                       
                       if( returnValue == JFileChooser.APPROVE_OPTION ){
                           fileToAdd = fileChoice.getSelectedFile();
                           if( !(fileToAdd.isFile() ) ){
                               errorMessage = " Invalid File ";
                               throw new OMGDontSaveException( " hi " );
                           }
                           if( database.isFileInDatabase( fileToAdd.getName() ) ){
                               errorMessage = " File Already In Database ";
                               throw new OMGDontSaveException( "hiio " );
                           }
                           dataFrame.setVisible( true );
                       }
                       else {
                           if( returnValue == JFileChooser.CANCEL_OPTION ){  
                               errorMessage = "Please select a file";
                               throw new OMGDontSaveException( "hi" ); 
                           }
                           else{
                               
                               errorMessage = "PLEASE select a file";
                               throw new OMGDontSaveException( "hi" );
                           }                  
                           
                       }
                       
                   }
                   catch( OMGDontSaveException r ){
                       
                       JOptionPane.showMessageDialog( popUp, errorMessage );

                   }
               }
           };


       // make the remove file frome database listener
       ActionListener removeFileFromDatabaseListener = new ActionListener(){

               public void actionPerformed( ActionEvent e ){
                   
                   final JFrame openFrame = new JFrame( "Remove File" );
                   JButton b1 = new JButton( "Delete" );
                   final JTextField tf1 = new JTextField( "Enter File Name", 20 );
                   
                   ActionListener b1Listener = new ActionListener(){
                           
                           public void actionPerformed( ActionEvent e ){
                               
                               String tempString = tf1.getText();
                               
                               if(  database.isFileInDatabase( tempString ) ){
                                   database.removeFile( tempString );
                                   openFrame.dispose();
                               }
                               else{
                                   JFrame popUp = new JFrame( "" );
                                   String message = " 404: File Not Found In Database ";
                                   JOptionPane.showMessageDialog( popUp, message );
                                   openFrame.dispose();
                               }
                               writeDatabase();
                           }
                       };
                   
                   b1.addActionListener( b1Listener );

                   openFrame.setLocationRelativeTo( null );
                   openFrame.setLayout( new GridLayout( 0, 1 ) );
                   openFrame.add( tf1 );
                   openFrame.add( b1 );
                   
                   openFrame.pack();
                   openFrame.setVisible( true );
               }
           };

       // make the quit program listener
       ActionListener quitProgram = new ActionListener(){

               public void actionPerformed( ActionEvent e ){
                   try{
                       if( fileCurrentlyDisplayed != null )
                	   promptSave();
                       writeDatabase();
                       frame.dispose();                       
                   }
                   catch( OMGDontSaveException r ){
                	   //do nothing
                   }          
               }
           };


       // make the search by file name listener
       ActionListener searchByFileListener = new ActionListener(){

               public void actionPerformed( ActionEvent e ){

                   final JFrame openFrame = new JFrame( "Search By File Name" );
                   JButton b1 = new JButton( "Search" );
                   final JTextField tf1 = new JTextField( "Enter File Name", 20 );
                           
                   ActionListener b1Listener = new ActionListener(){

                           public void actionPerformed( ActionEvent e ){

                               String tempString = tf1.getText();
                               text.setText( "" );
                               String tempString2 = null;
                               boolean foundMatch = false;

                               Iterator<String> it1 = database.getTree().iterator();
                               while( it1.hasNext() ){
                                   tempString2 = (String)it1.next();
                                   if( tempString2.contains( tempString ) ){
                                       text.append( tempString2 + ": " + "\t" );
                                       text.append( database.getHashMap().get(tempString2).
                                    		   getKeywords() );
                                       foundMatch = true;
                                   }
                               }
                               if( !foundMatch ){
                                   JFrame popUp = new JFrame( "" );
                                   String message = " No Matching Files Found ";
                                   JOptionPane.showMessageDialog( popUp, message );
                               }

                               openFrame.dispose();
                           }    
                       };
                   b1.addActionListener( b1Listener );
                   openFrame.setLocationRelativeTo( null );
                   openFrame.setLayout( new GridLayout( 0, 1 ) );
                   openFrame.add( tf1 );
                   openFrame.add( b1 );
                   openFrame.pack();
                   openFrame.setVisible( true );
               }
           };

       // make the search by keywords listener
       ActionListener searchByKeywordListener = new ActionListener(){

               public void actionPerformed( ActionEvent e ){

                   final JFrame openFrame = new JFrame( "Search By Topics" );
                   JButton b1 = new JButton( "Search" );
                   final JTextField tf1 = new JTextField( "Enter Topic", 20 );
                           
                   ActionListener b1Listener = new ActionListener(){

                           public void actionPerformed( ActionEvent e ){

                               String tempString = tf1.getText();
                               text.setText( "" );
                               String tempString2 = null;
                               String fileName = "";
                               boolean foundMatch = false;

                               Iterator<String> it1 = database.getTree().iterator();
                               while( it1.hasNext() ){
                            	   fileName = it1.next();
                                   tempString2 = database.getHashMap().
                                   	get(fileName).getKeywords();
                                   if( tempString2.contains( tempString ) ){

                                       text.append( fileName + ": " + "\t" );
                                       text.append( tempString2 );
                                       text.append( "\n" );
                                       foundMatch = true;
                                   }
                               }
                               if( !foundMatch ){
                                   JFrame popUp = new JFrame( "" );
                                   String message = " No Matching Files Found ";
                                   JOptionPane.showMessageDialog( popUp, message );
                               }
                               openFrame.dispose();
                           }    
                       };
                   
                   b1.addActionListener( b1Listener );
                   openFrame.setLocationRelativeTo( null );
                   openFrame.setLayout( new GridLayout( 0, 1 ) );
                   openFrame.add( tf1 );
                   openFrame.add( b1 );                  
                   openFrame.pack();
                   openFrame.setVisible( true );               
               }
           };

       // make the search by class name listener
       ActionListener searchByClassListener = new ActionListener(){

               public void actionPerformed( ActionEvent e ){

                   final JFrame openFrame = new JFrame( "Search by Class" );
                   JButton b1 = new JButton( "Search" );
                   final JTextField tf1 = new JTextField( "Enter Class Name", 20 );
                           
                   ActionListener b1Listener = new ActionListener(){

                           public void actionPerformed( ActionEvent e ){

                               String tempString = tf1.getText();
                               text.setText( "" );
                               String tempString2 = null;
                               String fileName = "";
                               boolean foundMatch = false;

                               Iterator<String> it1 = database.getTree().iterator();
                               while( it1.hasNext() ){
                            	   fileName = it1.next();
                                   tempString2 = database.getHashMap().get(fileName).
                                   		getClassName();
                                   if( tempString2.contains( tempString ) ){

                                       text.append( fileName + ": " + "\t" );
                                       text.append( tempString2 + "\n" );
                                       foundMatch = true;
                                   }
                               }
                               if( !foundMatch ){
                                   JFrame popUp = new JFrame( "" );
                                   String message = " No Matching Files Found ";
                                   JOptionPane.showMessageDialog( popUp, message );
                               }
                               openFrame.dispose();
                           }    
                       };                 
                   b1.addActionListener( b1Listener );
                   openFrame.setLocationRelativeTo( null );
                   openFrame.setLayout( new GridLayout( 0, 1 ) );
                   openFrame.add( tf1 );
                   openFrame.add( b1 );                  
                   openFrame.pack();
                   openFrame.setVisible( true );                 
               }
           };


       // make the search by quarter listener
       ActionListener searchByQuarterListener = new ActionListener(){

               public void actionPerformed( ActionEvent e ){

                   final JFrame openFrame = new JFrame( "Search By Quarter" );
                   JButton b1 = new JButton( "Search" );
                   final JTextField tf1 = new JTextField( "Enter Quarter", 20 );
                           
                   ActionListener b1Listener = new ActionListener(){

                           public void actionPerformed( ActionEvent e ){

                               String tempString = tf1.getText();
                               text.setText( "" );
                               String tempString2 = null;
                               String fileName;
                               boolean foundMatch = false;

                               Iterator<String> it1 = database.getTree().iterator();
                               while( it1.hasNext() ){
                            	   fileName = it1.next();
                                   tempString2 = database.getHashMap().get(fileName).
                                   		getQuarter();
                                   if( tempString2.contains( tempString ) ){

                                       text.append( fileName + ": " + "\t" );
                                       text.append( database.getHashMap().get(fileName).getKeywords()
                                    		   + "\n" );
                                       foundMatch = true;
                                   }
                               }
                               if( !foundMatch ){
                                   JFrame popUp = new JFrame( "" );
                                   String message = " No Matching Files Found ";
                                   JOptionPane.showMessageDialog( popUp, message );
                               }
                               openFrame.dispose();
                           }    
                       };
                   b1.addActionListener( b1Listener );
                   openFrame.setLocationRelativeTo( null );
                   openFrame.setLayout( new GridLayout( 0, 1 ) );
                   openFrame.add( tf1 );
                   openFrame.add( b1 );
                   openFrame.pack();
                   openFrame.setVisible( true );
               }
           };


       // make the list all files listener
       ActionListener listAllFilesListener = new ActionListener(){

               public void actionPerformed( ActionEvent e ){

                   String tempString = null;
                   String tempString2 = null;
                   boolean isEmpty = true;
                   
                   Iterator<String> it1 = database.getTree().iterator();
                   while( it1.hasNext() ){
                       tempString = (String)it1.next();
                       tempString2 = database.getHashMap().get( 
                                       tempString ).getKeywords();
                           text.append( tempString + ": " + "\t" );
                           text.append( tempString2 + "\n" );
                           isEmpty = false;
                   }
                   if( isEmpty ){
                       JFrame popUp = new JFrame( "" );
                       String message = " No Files In Database ";
                       JOptionPane.showMessageDialog( popUp, message );
                   }
                   

               }
           };


       // make the add file to favorites listener
       ActionListener addFavListener = new ActionListener(){

               public void actionPerformed( ActionEvent e ){

                   
                   final JFrame openFrame = new JFrame( "Add File to Favorites" );
                   JButton b1 = new JButton( "Add" );
                   final JTextField tf1 = new JTextField( "Enter File Name", 20 );
                           
                   ActionListener b1Listener = new ActionListener(){

                           public void actionPerformed( ActionEvent e ){
                               
                               JFrame messageFrame = new JFrame( "Message" );
                               String tempString = tf1.getText();
                               String message;
                               openFrame.dispose();
                               if(  database.isFileInDatabase( tempString )){
                                   database.addFavorite( tempString );
                                   message = tempString + "  added to favorites";
                                   JOptionPane.showMessageDialog( messageFrame, message );
                               }    
                               else{
                                   message = "File Not Found In Database";
                                   JOptionPane.showMessageDialog( messageFrame, message );
                               }
                               writeDatabase();
                           }
                       };
                   
                   b1.addActionListener( b1Listener );
                   
                   openFrame.setLocationRelativeTo( null );
                   openFrame.setLayout( new GridLayout( 0, 1 ) );
                   openFrame.add( tf1 );
                   openFrame.add( b1 );
                   
                   openFrame.pack();
                   openFrame.setVisible( true );


               }
           };


       // make the remove file from favorites listener
       ActionListener removeFavListener = new ActionListener(){

               public void actionPerformed( ActionEvent e ){

                   
                   final JFrame openFrame = new JFrame( "Remove File from Database" );
                   JButton b1 = new JButton( "Delete" );
                   final JTextField tf1 = new JTextField( "Enter File Name", 20 );
                           
                   ActionListener b1Listener = new ActionListener(){

                           public void actionPerformed( ActionEvent e ){
                               
                               JFrame messageFrame = new JFrame( "Message" );
                               String tempString = tf1.getText();
                               String message = "File Not Foudnd In Database";
                               
                               openFrame.dispose();
                               for( int i = 0; i < database.getFavorites().size(); i++ ){

                                   if( database.getFavorites().get( i ).equals( tempString ) ){
                                       database.removeFavorite( tempString );
                                       message = tempString + "  removed from favorites";
                                       i =  database.getFavorites().size();
                                   }
                               }
                               JOptionPane.showMessageDialog( messageFrame, message );
                               writeDatabase();
                           } 
                       };
                   
                   b1.addActionListener( b1Listener );
                   
                   openFrame.setLocationRelativeTo( null );
                   openFrame.setLayout( new GridLayout( 0, 1 ) );
                   openFrame.add( tf1 );
                   openFrame.add( b1 );
                   
                   openFrame.pack();
                   openFrame.setVisible( true );


               }
           };


       // make the open a favorite file listener

       ActionListener openFavListener = new ActionListener(){

               public void actionPerformed( ActionEvent e ){
                   
            	   try{
                       if( fileCurrentlyDisplayed != null )
            		   promptSave();
                       final JFrame openFrame = new JFrame( "Open Favorite" );
                       JButton b1 = new JButton( "Open" );
                       final JComboBox cb1 = new JComboBox( database.getFavorites().toArray() );
                       ActionListener b1Listener = new ActionListener(){
                               public void actionPerformed( ActionEvent e ){
                                   String tempString = (String)cb1.getSelectedItem();
                                   File tempFile;
                                   try{
                                       if(  database.isFileInDatabase( tempString ) ){
                                           tempFile = database.getFile( tempString );
                                           Scanner input = new Scanner( tempFile );
                                           text.setText( "" );
                                           while( input.hasNext() ){
                                               tempString = input.nextLine();
                                               text.append( tempString );
                                               text.append( "\n" );
                                           }
                                           text.setCaretPosition( 0 );
                                       }
                                       else{
                                           JFrame popUp = new JFrame( "" );
                                           String message = " 404: File Not Found ";
                                           JOptionPane.showMessageDialog( popUp, message );
                                       }
                                       openFrame.dispose();
                                   }
                                   catch( FileNotFoundException r ){
                                       JFrame popUp = new JFrame( "" );
                                       String message = " 404: File Not Found ";
                                       JOptionPane.showMessageDialog( popUp, message ); 
                                       openFrame.dispose();
                                   }
                               }
                           };
                       b1.addActionListener( b1Listener );
                       openFrame.setLocationRelativeTo( null );
                       openFrame.setLayout( new GridLayout( 0, 1 ) );
                       openFrame.add( cb1 );
                       openFrame.add( b1 );
                       
                       openFrame.setSize( 300, 100 );
                       openFrame.setVisible( true );
            	   }
            	   catch( OMGDontSaveException s){
            	   }
               }
           };

       // make the help listener

       ActionListener helpListener = new ActionListener(){

               public void actionPerformed( ActionEvent e ){
                   try{                       
                       JFrame openFrame = new JFrame( "Help" );
                       JTextArea ta1 = new JTextArea( 30, 45 );
                       JScrollPane scrollPane = new JScrollPane( ta1 );
                       Scanner input = new Scanner( helpFile );
                       String tempString = null;
                       ta1.setText( "" );
                       while( input.hasNext() ){
                           tempString = input.nextLine();
                           ta1.append( tempString );
                           ta1.append( "\n" );
                       }
                       ta1.setCaretPosition( 0 );
                       input.close();

                       openFrame.setLocationRelativeTo( null );
                       openFrame.add( scrollPane );
                       openFrame.pack();
                       openFrame.setVisible( true );
                   }
                   catch( FileNotFoundException s ){
                       System.err.println( s.getMessage() ); 
                   }
               }
           };

       // make the about  listener

       ActionListener aboutListener = new ActionListener(){

               public void actionPerformed( ActionEvent e ){
                   try{                       
                       JFrame openFrame = new JFrame( "About Me" );
                       JTextArea ta1 = new JTextArea( 15, 45 );
                       JScrollPane scrollPane = new JScrollPane( ta1 );
                       Scanner input = new Scanner( aboutFile );
                       String tempString = null;
                       ta1.setText( "" );
                       while( input.hasNext() ){
                           tempString = input.nextLine();
                           ta1.append( tempString );
                           ta1.append( "\n" );
                       }
                       ta1.setCaretPosition( 0 );
                       input.close();

                       openFrame.setLocationRelativeTo( null );
                       openFrame.add( scrollPane );
                       openFrame.pack();
                       openFrame.setVisible( true );
                   }
                   catch( FileNotFoundException s ){
                       System.err.println( s.getMessage() ); 
                   }
               }
           };


                   



       // make the text area and add it to the center of the frame
       // frame.add( text, BorderLayout.CENTER );
       frame.add( scrollPane, BorderLayout.CENTER );
       
       // Make the file tab
       menuBar.add( file );
       
       file.add( fileItem1 );
       fileItem1.addActionListener( openListener );
       file.add( fileItem2 );
       fileItem2.addActionListener( saveFileListener );
       file.add( fileItem3 );
       fileItem3.addActionListener( closeFileListener );
       file.add( fileItem4 );
       fileItem4.addActionListener( addFileToDatabaseListener );
       file.add( fileItem5 );
       fileItem5.addActionListener( removeFileFromDatabaseListener );
       file.add( fileItem6 );
       fileItem6.addActionListener( quitProgram );

       // Make the search tab
       menuBar.add( search );
   
       search.add( searchItem1 );
       searchItem1.addActionListener( searchByFileListener );
       search.add( searchItem2 );
       searchItem2.addActionListener( searchByKeywordListener );
       search.add( searchItem3 );
       searchItem3.addActionListener( searchByClassListener );
       search.add( searchItem4 );
       searchItem4.addActionListener( searchByQuarterListener );
       search.add( searchItem5 );
       searchItem5.addActionListener( listAllFilesListener );

       // Make the Favorites tab
       menuBar.add( favs );

       favs.add( favsItem1 );
       favsItem1.addActionListener( addFavListener );
       favs.add( favsItem2 );
       favsItem2.addActionListener( removeFavListener );
       favs.add( favsItem3 );
       favsItem3.addActionListener( openFavListener );

       // make the info tab

       menuBar.add( info );
       info.add( info1 );
       info1.addActionListener( helpListener );
       info.add( info2 );
       info2.addActionListener( aboutListener );

       //////////////////////////////////////////////////////////////
       //                  TO BE IMPLEMENTED                       //
       //////////////////////////////////////////////////////////////
       // For each element in the favorites array in the database  // 
       // class, add a tab here.                                   //
       //////////////////////////////////////////////////////////////

       
       frame.add( menuBar, BorderLayout.NORTH );


       // set the outer frame to visible  and program system exit command
       frame.setSize(900, 500);
       frame.setVisible( true );
       frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );


   }
    /**
     * prompts the user if they want to save changes to text in JTextArea. Changes will be saved over the
     * old file.
     * 
     * @exception  numberFormatException    only throws
     *            
     */
    private void promptSave() throws OMGDontSaveException {
        JFrame saveFrame = new JFrame( "SAVE PROMPT" );
        int option = JOptionPane.showConfirmDialog( saveFrame, "Save Changes To File?" );
        switch ( option ){
            
        case JOptionPane.YES_OPTION:
            if( fileCurrentlyDisplayed == null )
                break;
            try{
                String pathName = database.getFile( fileCurrentlyDisplayed ).getAbsolutePath();
                System.out.println( pathName );
                String [] lineArray = null;
                lineArray = text.getText().split("\n");
                PrintWriter write = new PrintWriter( new FileWriter( pathName ) );
                for( int i = 0; i < lineArray.length; i++ ){
                   write.println( lineArray[i] );
                }
                write.close();
                writeDatabase();
            }
            catch( IOException r ){
                JFrame popUp = new JFrame( "" );
                String message = " Error Saving File ";
                JOptionPane.showMessageDialog( popUp, message ); 
            }
            saveFrame.dispose();
            break;
            
        case JOptionPane.NO_OPTION:
            saveFrame.dispose();
            break;
            
        case JOptionPane.CANCEL_OPTION:
        	saveFrame.dispose();
            throw new OMGDontSaveException(" Oh hi " );
            
        default:
        	saveFrame.dispose();
            text.setText( "Well this is awkward....I think you broke me =(" );
            break;
        }   
    }

    /**
     * Saves the database object to a text file. This command is only executed immidately before the
     * program closes.
     *
     */
    private void writeDatabase(){
        
        try{
            FileOutputStream fileOut = new FileOutputStream( "sourceCode.txt" );
            outStream = new ObjectOutputStream( fileOut );
            outStream.writeObject( database );
            outStream.close();
            fileOut.close();

        }
        catch( IOException r ){
        
        }
    }

    /**
     * Saves the database object to a text file. This command is only executed immidately before the
     * program closes.
     *
     */
    private void readDatabase(){
        
        try{

            inStream = new ObjectInputStream( new FileInputStream( "sourceCode.txt" ) );
            database = (SourceCode)inStream.readObject();
            inStream.close();
            if( database == null ){
                System.out.println("hi" );
                database = new SourceCode();
            }
        }
        catch( ClassNotFoundException s ){
        
        }
        catch( IOException r ){
        
        }
    }

    
    /**
     * This class defines the object to be thrown by a TankFarm object when it 
     * detects a constructor error.
     *
     * @author ajl2612: Andrew Lyne III
     */
    
    public class OMGDontSaveException extends java.lang.Exception {
        
        /**
         * Constructor for the TankFarmException class. 
         *
         * @param    message      String description of message
         */
        public OMGDontSaveException( String message ) {
            super( message );    
        }   
    }
   


   /**
    * main method -- DELETE METHOD IF NOT NEEDED
    *
    * @param    args      command line arguments
    */

   public static void main( String args[] ) {
       database = new SourceCode();
       GUI test = new GUI();

   }

} // GUI