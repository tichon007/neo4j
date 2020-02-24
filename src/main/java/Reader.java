import com.google.common.base.Strings;

import java.io.*;
import java.text.Normalizer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Reader {


    public List<Publication> processInputFile(String inputFilePath) {
        List<Publication> inputList = new ArrayList<>();
        try{
            File inputF = new File(this.getClass().getClassLoader().getResource(inputFilePath).getFile());
            InputStream inputFS = new FileInputStream(inputF);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
            // skip the header of the csv
            inputList = br.lines().skip(1).map(this.mapToItem).collect(Collectors.toList());
            br.close();
        } catch ( IOException e) {
            e.printStackTrace();
        }
        return inputList ;
    }


    public  Function<String, Publication> mapToItem = (line) -> {
        String[] p = line.split(";");// a CSV has comma separated lines
        Publication item = new Publication();
        item.setId(p[0]);
        item.setType(p[1]);
        item.setTitle(p[2]);
        item.setAutorString(stripAccents(p[3]));
        if(!Strings.isNullOrEmpty(item.getAutorString()))
        {
String[] autorsSplited = item.getAutorString().split(",");
item.setAutors(Arrays.asList((autorsSplited)));
        }
        item.setAutorString(p[3]);
        item.setYear(extractYear(p[4]));
        //<-- this is the first column in the csv file
        /*if (p[3] != null && p[3].trim().length() > 0) {
            item.setSomeProeprty(p[3]);
        }*/
        //more initialization goes here
        return item;
    };



    public Set<String> processInputFileAuthorFromLamsade(String inputFilePath) {
        Set<String> inputList = new HashSet<>();
        try{
            File inputF = new File(this.getClass().getClassLoader().getResource(inputFilePath).getFile());
            InputStream inputFS = new FileInputStream(inputF);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
            // skip the header of the csv
            inputList = br.lines().skip(1).map(this.mapToItemFromLamsade).collect(Collectors.toSet());
            br.close();
        } catch ( IOException e) {
            e.printStackTrace();
        }
        return inputList ;
    }


    public  Function<String, String> mapToItemFromLamsade = (line) -> {
        String[] p = line.split(";");// a CSV has comma separated lines


        //more initialization goes here
        return stripAccents(p[0])+" "+stripAccents(p[1]);
    };




    public Set<String> getYears;



    public String extractYear(String date)
    {
        if(getYears==null)
            getYears=getYearFrom1900To3000();
        for(String year : getYears)
        if(date.contains(year))
            return year;


            return null;
    }



    public  Set<String> getYearFrom1900To3000(){

        Set<String> years = new HashSet<>();
        for(int i = 1900 ;i < 3000 ; i++)
        years.add(""+i);

        return years;



    }


    public static String stripAccents(String s)
    {
        if(s==null)
            return null;
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s.toUpperCase();
    }


}
