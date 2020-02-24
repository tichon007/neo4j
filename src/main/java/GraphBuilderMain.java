import com.google.common.collect.Lists;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

import java.util.*;

import static org.neo4j.driver.Values.parameters;

public class GraphBuilderMain implements AutoCloseable
{
    private final Driver driver;

    public GraphBuilderMain(String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public void printGreeting( final String message )
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run( "CREATE (a:Greeting) " +
                                                     "SET a.message = $message " +
                                                     "RETURN a.message + ', from node ' + id(a)",
                            parameters( "message", message ) );
                    return result.single().get( 0 ).asString();
                }
            } );
            System.out.println( greeting );
        }
    }



    public void createAutorNode( final List<Publication> publications,final Set<String> lamsadAuthor)
    {

        Set<String> autors = new HashSet<>();
        for (Publication publication : publications) {
            if(publication.getAutors()!=null)
                autors.addAll(publication.getAutors());
        }



        try ( Session session = driver.session() )
        {

            for (String autor : autors) {

final Boolean isFromLamsad = lamsadAuthor.contains(autor)?true:false;



            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {

                    System.out.println("CREATE (a:Author) " +
                            "SET a.name = '"+autor+"', a.isFromLamsad = '"+isFromLamsad+"'" +
                            "RETURN a.name + ', from node ' + id(a)");


                    Result result = tx.run( "CREATE (a:Author) " +
                                    "SET a.name = $authorName , a.isFromLamsad = $isFromLamsad " +
                                    "RETURN a.name + ', from node ' + id(a)",
                            parameters( "authorName", autor,"isFromLamsad",isFromLamsad ) );
                    return result.single().get( 0 ).asString();
                }
            } );
            //System.out.println( greeting );
        }
        }
    }




    public void createRelation( final List<Publication> publications)
    {




        try ( Session session = driver.session() )
        {

            for (Publication publication : publications) {

                if(publication.getAutors()!=null)
                for (String autor : publication.getAutors()) {


                    String greeting = session.writeTransaction(new TransactionWork<String>() {
                        @Override
                        public String execute(Transaction tx) {

                            System.out.println("MATCH (a:Publication),(b:Author) " +
                                    "WHERE a.id = '"+publication.getId()+"' AND b.name = '"+autor+"' " +
                                    "CREATE (a)-[r:RELTYPE { name: a.id + '<->' + b.name }]->(b)");

                            Result result = tx.run("MATCH (a:Publication),(b:Author) " +
                                            "WHERE a.id = $publicationId AND b.name = $authorName " +
                                            "CREATE (a)-[r:RELTYPE { name: a.id + '<->' + b.name }]->(b)" +
                                            "",
                                    parameters("authorName", autor, "publicationId", publication.getId()));
                            return "";
                        }
                    });
                    //System.out.println(greeting);
                }
            }
        }
    }










    public void createPublication( final List<Publication> publications)
    {




        try ( Session session = driver.session() )
        {

            for (Publication publication : publications) {




                String greeting = session.writeTransaction( new TransactionWork<String>()
                {
                    @Override
                    public String execute( Transaction tx )
                    {

                        System.out.println("CREATE (a:Publication) " +
                                "SET a.publicationName = '"+publication.getTitle()+"' , a.id =  '"+publication.getId()+"' " +
                                "RETURN a.name + ', from node ' + id(a)");

                        Result result = tx.run( "CREATE (a:Publication) " +
                                        "SET a.publicationName = $publicationName , a.id =  $publicationId " +
                                        "RETURN a.name + ', from node ' + id(a)",
                                parameters( "publicationName", publication.getTitle(),"publicationId",publication.getId() ) );
                        return result.single().get( 0 ).asString();
                    }
                } );
                //System.out.println( greeting );
            }
        }
    }





    public void createCoAuthorRelation( final Set<CoAuthor> coAuthors)
    {




        try ( Session session = driver.session() )
        {

            for (CoAuthor coAuthor : coAuthors) {



                String greeting = session.writeTransaction(new TransactionWork<String>() {
                    @Override
                    public String execute(Transaction tx) {

                        System.out.println("MATCH (a:Author),(b:Author) " +
                                "WHERE a.name = '"+coAuthor.firstAuthorName+"' AND b.name = '"+coAuthor.secondeAuthorName+"' " +
                                "CREATE (a)-[r:CO_AUTHOR { nbrPublication: '"+coAuthor.coAuthorCount+"',yearFirstPublication : '"+coAuthor.coAuthorFirstEditionYear+"' }]->(b)");

                        Result result = tx.run("MATCH (a:Author),(b:Author) " +
                                        "WHERE a.name = $firstAuthorName AND b.name = $secondeAuthorName " +
                                        "CREATE (a)-[r:CO_AUTHOR { nbrPublication: $coAuthorCount,yearFirstPublication : $yearFirstPublication }]->(b)",
                                parameters("firstAuthorName", coAuthor.firstAuthorName, "secondeAuthorName",
                                        coAuthor.secondeAuthorName,"coAuthorCount",coAuthor.coAuthorCount,"yearFirstPublication",coAuthor.coAuthorFirstEditionYear));
                        return "";
                    }
                });
                //System.out.println(greeting);
            }
        }
    }




    public static void main( String... args ) throws Exception


    {
       // try ( HelloWorldExample greeter = new HelloWorldExample( "bolt://localhost:7687", "neo4j", "ngXXXXXX" ) )
        try ( GraphBuilderMain greeter = new GraphBuilderMain( "bolt://XXXXX:7687", "neo4j", "XXXXX" ) )
        {
         //   greeter.printGreeting( "hello, world" );

            Map<String,CoAuthor> mapCoAuthor = new HashMap<>();



            List<Publication> publications =  new Reader().processInputFile("neo4jCleaned_2.csv");
            Set<String> emsadMember =  new Reader().processInputFileAuthorFromLamsade("Membres.csv");

            for(Publication publication : publications)
            {

                System.out.println(publication.getAutorString());
                if(publication.getAutors().size()>1) {
                    Collections.sort(publication.getAutors());


                    for (int i = 0; i < publication.getAutors().size(); i++) {

                        for (int j = 0; j < publication.getAutors().size(); j++) {
                            if (j != i) {
                                System.out.println(publication.getAutors().get(i) + "_" + publication.getAutors().get(j));

                                String key = getKey(publication.getAutors().get(i) , publication.getAutors().get(j));

                                if(mapCoAuthor.get(key)==null) {
                                 CoAuthor coAuthor = new CoAuthor();
                                 coAuthor.firstAuthorName=publication.getAutors().get(i);
                                 coAuthor.secondeAuthorName=publication.getAutors().get(j);
                                 coAuthor.publicationSet = new HashSet<>();
                                    mapCoAuthor.put(key, coAuthor);
                                }

                                mapCoAuthor.get(key).publicationSet.add(publication);
                            }


                        }
                    }
                }
                //System.out.println(publication.getAutorString());
                String enc = new String(publication.getAutorString().getBytes("UTF8"), "UTF8");
                //System.out.println(enc);
            }


            for (CoAuthor coAuthor : mapCoAuthor.values()) {
                if(coAuthor.publicationSet!=null) {
                 coAuthor.coAuthorCount=coAuthor.publicationSet.size();
                 Integer minYear = null;
                    for (Publication publication : coAuthor.publicationSet) {
if(minYear==null) {
    minYear = Integer.parseInt(publication.getYear());
continue;
}

if(Integer.parseInt(publication.getYear())<minYear)
    minYear = Integer.parseInt(publication.getYear());


                    }
                    coAuthor.coAuthorFirstEditionYear = minYear;

                }
            }
            greeter.createAutorNode(publications,emsadMember);
            greeter.createPublication(publications);
            greeter.createRelation(publications);
            greeter.createCoAuthorRelation(new HashSet<CoAuthor>(mapCoAuthor.values()));
            System.out.println();
        }




    }


    private static String getKey(String fist,String second)
    {

        if(fist.equals(second))
            return null;
        List<String> ketList = Lists.newArrayList(fist,second);
        Collections.sort(ketList);

        return ketList.get(0)+"_"+ketList.get(1);



    }



}
