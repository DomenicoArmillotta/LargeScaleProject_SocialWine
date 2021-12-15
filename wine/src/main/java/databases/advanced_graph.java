package databases;

import org.neo4j.driver.*;

import java.util.HashMap;

public class advanced_graph implements AutoCloseable {
    private final Driver driver;


    public advanced_graph( String uri, String user, String password ) {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    public HashMap<String,Value> top5UserMostFriendship() {
        HashMap<String,Value> friendCount;
        try (Session session = driver.session()) {
            friendCount = session.readTransaction((TransactionWork<HashMap<String, Value>>) tx -> {
                        Result result = tx.run("MATCH path=(p:user)-[f:Follow]-(p:User)\n" +
                                "RETURN  AS User, p.taster_name, COUNT(f) AS numFollow \n" +
                                "ORDER BY numFollow DESC\n" +
                                "LIMIT 5");
                        HashMap<String, Value> friendCountResult = new HashMap<>();
                        while (result.hasNext()) {
                            Record r = result.next();
                            friendCountResult.put(r.get("Taster Name").asString(), r.get("numFollow"));
                        }
                        System.out.println(friendCountResult);
                return friendCountResult;
            });
        }catch (Exception e){
            friendCount = null;
        }
        return friendCount;
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }
}
