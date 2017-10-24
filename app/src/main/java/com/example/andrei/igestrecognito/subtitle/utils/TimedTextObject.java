package com.example.andrei.igestrecognito.subtitle.utils;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;

public class TimedTextObject {

  public String title = "";
    public String description = "";
    public String copyrigth = "";
    public String author = "";
    public String fileName = "";
    public String language = "";

    //list of styles (id, reference)
    public Hashtable<String, Style> styling;

    //list of layouts (id, reference)
    public Hashtable<String, Region> layout;

    //list of captions (begin time, reference)
    //represented by a tree map to maintain order
    public TreeMap<Integer, Caption> captions;

    //to store non fatal errors produced during parsing
    public String warnings;

    //**** OPTIONS *****
    //to know whether file should be saved as .ASS or .SSA
    public boolean useASSInsteadOfSSA = true;
    //to delay or advance the subtitles, parsed into +/- milliseconds
    public int offset = 0;

    //to know if a parsing method has been applied
    public boolean built = false;


    /**
     * Protected constructor so it can't be created from outside
     */
    protected TimedTextObject(){

        styling = new Hashtable<String, Style>();
        layout = new Hashtable<String, Region>();
        captions = new TreeMap<Integer, Caption>();

        warnings = "List of non fatal errors produced during parsing:\n\n";

    }


	/*
	 * Writing Methods
	 *
	 */
    /**
     * Method to generate the .SRT file
     *
     * @return an array of strings where each String represents a line
     */
    public String[] toSRT(){
        return new FormatSRT().toFile(this);
    }


    /**
     * Method to generate the .ASS file
     *
     * @return an array of strings where each String represents a line
     */
    public String[] toASS(){
        return new FormatASS().toFile(this);
    }

    /**
     * Method to generate the .STL file
     */
    public byte[] toSTL(){
        return new FormatSTL().toFile(this);
    }

    /**
     * Method to generate the .SCC file
     * @return
     */
    public String[] toSCC(){
        return new FormatSCC().toFile(this);
    }

    /**
     * Method to generate the .XML file
     * @return
     */
    public String[] toTTML(){
        return new FormatTTML().toFile(this);
    }

	/*
	 * PROTECTED METHODS
	 *
	 */

    /**
     * This method simply checks the style list and eliminate any style not referenced by any caption
     * This might come useful when default styles get created and cover too much.
     * It require a unique iteration through all captions.
     *
     */
    protected void cleanUnusedStyles(){
        //here all used styles will be stored
        Hashtable<String, Style> usedStyles = new Hashtable<String, Style>();
        //we iterate over the captions
        Iterator<Caption> itrC = captions.values().iterator();
        while(itrC.hasNext()){
            //new caption
            Caption current = itrC.next();
            //if it has a style
            if(current.style != null){
                String iD = current.style.iD;
                //if we haven't saved it yet
                if(!usedStyles.containsKey(iD))
                    usedStyles.put(iD, current.style);
            }
        }
        //we saved the used styles
        this.styling = usedStyles;
    }

}