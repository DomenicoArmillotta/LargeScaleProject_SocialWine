# SocialWine

Application:
- [x] Scraper
- [ ] implement scraper on app
- [ ] implement auto update of the review scraped on mongo db in the two collection (user , winery)
- [ ] implement auto adding of the user scraped on graph db

Mongo Db:
- [x] Crud in mongo Db
- [x] 1 advanced mongo db
- [x] 2 advanced mongo db
- [x] 3 advanced mongo db

Graph Db:
- [x] Crud in Graph
- [x] 1 advanced og graph
- [x] 2 advanced on graph
- [ ] Create query ADD_POST where you specify the winery, the title, the description, the creator. In order to create the "created" and "Belong" relationships
- [ ] Testing the graph advanced query
- [ ] populating the graph db with user -----> <name>
- [ ] populating the graph db with Post (review) ----> <title,description>
- [ ] create the random follow between user
- [ ] create the random like between user and post



Distruibuited : 
- [ ] Create the replica on the linux servers

  
  
# Population function:
-we use random function to create "follow" relationships between users [to be used only once]
- we use the random function to create "like" relationships between users and posts [to be used only once]
- the add review function will take as input = (title, description, author, linked winery) and create the "created" relationship between the post and the user, and the "belong" relationship between the post and the PageWinery [to be used when a user wants to write a review]
  
  

Domande :
1. possiamo eliminare la seconda collezione e lasciare solo review perche tutte le advanced query su mongo le facciamo su review? non ci sono costrains
2. Non abbiamo nessun nested document , dovremmo implementarlo nel document delle winery? Si lo implementiamo  ma non ci facciamo nessuna query perche non è nelle costrains

Link per Neo4j:
https://github.com/andrealagna/FUTDataMining/tree/08253590b90f68caf3d70c00e288dedeb94394ac/FUTPersistence/src/main/java/neo4j



